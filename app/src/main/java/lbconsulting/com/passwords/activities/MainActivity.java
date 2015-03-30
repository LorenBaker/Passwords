package lbconsulting.com.passwords.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileStatus;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.greenrobot.event.EventBus;
import lbconsulting.com.passwords.R;
import lbconsulting.com.passwords.classes.CryptLib;
import lbconsulting.com.passwords.classes.MyLog;
import lbconsulting.com.passwords.classes.MySettings;
import lbconsulting.com.passwords.classes.clsEvents;
import lbconsulting.com.passwords.classes.clsItemTypes;
import lbconsulting.com.passwords.classes.clsLabPasswords;
import lbconsulting.com.passwords.classes.clsPasswordItem;
import lbconsulting.com.passwords.classes.clsUsers;
import lbconsulting.com.passwords.fragments.DropboxListFragment;
import lbconsulting.com.passwords.fragments.EditCreditCardFragment;
import lbconsulting.com.passwords.fragments.EditGeneralAccountFragment;
import lbconsulting.com.passwords.fragments.EditSoftwareFragment;
import lbconsulting.com.passwords.fragments.EditWebsiteFragment;
import lbconsulting.com.passwords.fragments.PasswordFragment;
import lbconsulting.com.passwords.fragments.PasswordItemDetailFragment;
import lbconsulting.com.passwords.fragments.PasswordItemsListFragment;
import lbconsulting.com.passwords.fragments.SettingsFragment;


public class MainActivity extends FragmentActivity {
    // TODO: 3/28/2015 Look at menu item order

    ///private static final int REQUEST_SETTINGS = 555;

    private static final int REQUEST_LINK_TO_DBX = 999;  // This value is up to you
    private static final String APP_KEY = "kz0qsqlw52f41cy";
    private static final String APP_SECRET = "owdln6x88inn9vo";
    private DbxAccountManager mDbxAcctMgr;
    private static DbxFileSystem dbxFs;

    private static clsLabPasswords mPasswordsData;
    private static int mPreviousPasswordItemID;
    private boolean mArgBoolean;

    private static int mLastPasswordItemID;

    private static DbxFile.Listener mJsonDataFileListener;
    private static DbxFile mJsonDataFile = null;

    public static int getLastPasswordItemID() {
        return mLastPasswordItemID;
    }

    public static void setLastPasswordItemID(int lastPasswordItemID) {
        mLastPasswordItemID = lastPasswordItemID;
    }

    private static int mLastUserID;

    public static int getLastUserID() {
        return mLastUserID;
    }

    public static void setLastUserID(int lastUserID) {
        mLastUserID = lastUserID;
    }

    public static int getNextUserID() {
        mLastUserID++;
        return mLastUserID;
    }

    public static void addNewUser(clsUsers newUser) {
        if (mPasswordsData != null && mPasswordsData.getUsers() != null) {
            mPasswordsData.getUsers().add(newUser);
        }
    }

    private static android.app.ActionBar mActionBar;

    public static void setActionBarTitle(String title) {
        mActionBar.setTitle(title);
    }

    private static boolean mIsDirty = false;

    private boolean mTwoPane;
    private FrameLayout mFragment_container;
    private FrameLayout mDetail_container;
    private Button btnLinkToDropbox;

    public static clsLabPasswords getPasswordsData() {
        return mPasswordsData;
    }

    public static DbxFileSystem getDbxFs() {
        return dbxFs;
    }


    public static clsPasswordItem getActivePasswordItem() {
        clsPasswordItem result = null;
        if (mPasswordsData != null && mPasswordsData.getPasswordItems() != null) {
            int activePasswordItemID = MySettings.getActivePasswordItemID();
            if (activePasswordItemID > -1) {
                for (clsPasswordItem item : mPasswordsData.getPasswordItems()) {
                    if (item.getID() == activePasswordItemID) {
                        result = item;
                        break;
                    }
                }
            } else {
                MyLog.e("MainActivity", "getActivePasswordItem: activePasswordItemID less than or equal 0!");
            }
        }
        return result;
    }

    public static boolean deletePasswordItem(int itemID) {
        boolean result = false;
        int index = 0;
        for (clsPasswordItem item : mPasswordsData.getPasswordItems()) {
            if (item.getID() == itemID) {
                mPasswordsData.getPasswordItems().remove(index);
                //mActivePasswordItemID = mPreviousPasswordItemID;
                MySettings.setActivePasswordItemID(mPreviousPasswordItemID);
                result = true;
                break;
            }
            index++;
        }
        return result;
    }

    public static clsPasswordItem createNewPasswordItem() {
        mPreviousPasswordItemID = MySettings.getActivePasswordItemID();
        clsPasswordItem newItem = new clsPasswordItem(getNextPasswordItemID(), MySettings.getActiveUserID());
        mPasswordsData.getPasswordItems().add(newItem);
        MySettings.setActivePasswordItemID(newItem.getID());
        return newItem;
    }

    private static int getNextPasswordItemID() {
        mLastPasswordItemID++;
        return mLastPasswordItemID;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("MainActivity", "onCreate()");
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        MySettings.setContext(this);

        mActionBar = getActionBar();
        mFragment_container =
                (FrameLayout) findViewById(R.id.fragment_container);
        mDetail_container =
                (FrameLayout) findViewById(R.id.detail_container);

        btnLinkToDropbox = (Button) findViewById(R.id.btnLinkToDropbox);
        btnLinkToDropbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLinkToDropbox();
            }
        });

        mTwoPane = false;
        if (mDetail_container != null) {
            mTwoPane = true;
        }

        // Set up the account manager
        mDbxAcctMgr = DbxAccountManager.getInstance(getApplicationContext(), APP_KEY, APP_SECRET);
    }

    private void onClickLinkToDropbox() {
        MyLog.i("MainActivity", "onClickLinkToDropbox");
        mDbxAcctMgr.startLink((Activity) this, REQUEST_LINK_TO_DBX);
    }

/*    public void onClickLinkToDropbox(View view) {
        mDbxAcctMgr.startLink((Activity) this, REQUEST_LINK_TO_DBX);
    }*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LINK_TO_DBX) {
            if (resultCode == Activity.RESULT_OK) {
                // ... Start using Dropbox files.
                MyLog.i("MainActivity", "onActivityResult: RESULT_OK");

            } else {
                MyLog.e("MainActivity", "onActivityResult: Link failed or was cancelled by the user");
                // ... Link failed or was cancelled by the user.
            }
/*        } else if (requestCode == REQUEST_SETTINGS) {
            Toast.makeText(this, "Settings Changed", Toast.LENGTH_SHORT).show();*/

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showFragments() {
        FragmentManager fm = getSupportFragmentManager();

        if (mTwoPane) {
            fm.beginTransaction()
                    .replace(R.id.fragment_container,
                            PasswordItemsListFragment.newInstance())
                    .commit();

            switch (MySettings.getActiveFragmentID()) {

                case MySettings.FRAG_ITEM_DETAIL:
                    //TODO: TwoPain: add to backstack FRAG_ITEM_DETAIL
                    fm.beginTransaction()
                            .replace(R.id.detail_container,
                                    PasswordItemDetailFragment.newInstance())
                            .commit();
/*                    fm.beginTransaction()
                            .replace(R.id.password_item_detail_container,
                                    PasswordItemsListFragment.newInstance(activeList))
                            .commit();*/
                    break;

                case MySettings.FRAG_EDIT_CREDIT_CARD:
                    //TODO: TwoPain: Show FRAG_EDIT_CREDIT_CARD
                    break;

                case MySettings.FRAG_EDIT_GENERAL_ACCOUNT:
                    //TODO: TwoPain: Show FRAG_EDIT_GENERAL_ACCOUNT
                    break;

                case MySettings.FRAG_EDIT_SOFTWARE:
                    //TODO: TwoPain: Show FRAG_EDIT_SOFTWARE
                    break;

                case MySettings.FRAG_EDIT_WEBSITE:
                    //TODO: TwoPain: Show FRAG_EDIT_WEBSITE
                    break;

            }

        } else {
            // Single pane display
            switch (MySettings.getActiveFragmentID()) {
                case MySettings.FRAG_ITEMS_LIST:
                    clearBackStack();
                    fm.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container,
                                    PasswordItemsListFragment.newInstance(), "FRAG_ITEMS_LIST")
                            .commit();
                    break;

                case MySettings.FRAG_ITEM_DETAIL:
                    mArgBoolean = false;
                    fm.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container,
                                    PasswordItemDetailFragment.newInstance(), "FRAG_ITEM_DETAIL")
                            .addToBackStack("FRAG_ITEM_DETAIL")
                            .commit();
                    break;

                case MySettings.FRAG_EDIT_CREDIT_CARD:
                    fm.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container,
                                    EditCreditCardFragment.newInstance(mArgBoolean), "FRAG_EDIT_CREDIT_CARD")
                            .addToBackStack("FRAG_EDIT_CREDIT_CARD")
                            .commit();
                    break;

                case MySettings.FRAG_EDIT_GENERAL_ACCOUNT:
                    fm.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container,
                                    EditGeneralAccountFragment.newInstance(mArgBoolean), "FRAG_EDIT_GENERAL_ACCOUNT")
                            .addToBackStack("FRAG_EDIT_GENERAL_ACCOUNT")
                            .commit();
                    break;

                case MySettings.FRAG_EDIT_SOFTWARE:
                    fm.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container,
                                    EditSoftwareFragment.newInstance(mArgBoolean), "FRAG_EDIT_SOFTWARE")
                            .addToBackStack("FRAG_EDIT_SOFTWARE")
                            .commit();
                    break;

                case MySettings.FRAG_EDIT_WEBSITE:
                    fm.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container,
                                    EditWebsiteFragment.newInstance(mArgBoolean), "FRAG_EDIT_WEBSITE")
                            .addToBackStack("FRAG_EDIT_WEBSITE")
                            .commit();
                    break;

                case MySettings.FRAG_SETTINGS:
                    fm.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container,
                                    SettingsFragment.newInstance(mArgBoolean), "FRAG_SETTINGS")
                            .addToBackStack("FRAG_SETTINGS")
                            .commit();
                    break;

                case MySettings.FRAG_APP_PASSWORD:
                    fm.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container,
                                    PasswordFragment.newInstance(mArgBoolean), "FRAG_SETTINGS")
                            .addToBackStack("FRAG_SETTINGS")
                            .commit();
                    break;

                case MySettings.FRAG_DROPBOX_LIST:
                    fm.beginTransaction()
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .replace(R.id.fragment_container,
                                    DropboxListFragment.newInstance(), "FRAG_DROPBOX_LIST")
                            .addToBackStack("FRAG_DROPBOX_LIST")
                            .commit();
                    break;

            }
        }
    }

    private void clearBackStack() {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.getBackStackEntryCount() != 0) {
            fragmentManager.popBackStackImmediate();
        }
    }

    public void onEvent(clsEvents.PopBackStack event) {
        FragmentManager fm = getSupportFragmentManager();
        MyLog.i("MainActivity", "onEvent: BackStackEntryCount=" + fm.getBackStackEntryCount());
        if (fm.getBackStackEntryCount() < 2 && MySettings.getActiveFragmentID() != MySettings.FRAG_ITEMS_LIST) {
            MySettings.setActiveFragmentID(MySettings.FRAG_ITEMS_LIST);
            showFragments();
        } else {
            fm.popBackStack();
        }

    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() < 2 && MySettings.getActiveFragmentID() != MySettings.FRAG_ITEMS_LIST) {
            MySettings.setActiveFragmentID(MySettings.FRAG_ITEMS_LIST);
            showFragments();
        } else {
            super.onBackPressed();
        }
    }

    public void onEvent(clsEvents.replaceFragment event) {
        //mActiveFragmentID = event.getFragmentID();
        MySettings.setActiveFragmentID(event.getFragmentID());
        mArgBoolean = event.getIsNewPasswordItem();
        showFragments();
    }

    public void onEvent(clsEvents.showOkDialog event) {
        showOkDialog(this, event.getTitle(), event.getMessage());
    }

    public void onEvent(clsEvents.isDirty event) {
        mIsDirty = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MyLog.i("MainActivity", "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);

        if (MySettings.getActiveFragmentID() == MySettings.FRAG_SETTINGS) {
            menu.findItem(R.id.action_settings).setVisible(false);
            menu.findItem(R.id.action_save_to_dropbox).setVisible(false);

        } else if (MySettings.getActiveFragmentID() == MySettings.FRAG_DROPBOX_LIST) {
            menu.findItem(R.id.action_settings).setVisible(false);
            menu.findItem(R.id.action_save_to_dropbox).setVisible(false);
        } else {
            menu.findItem(R.id.action_settings).setVisible(true);
            menu.findItem(R.id.action_save_to_dropbox).setVisible(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save_to_dropbox) {
            saveEncryptedData(true);
            return true;

        } else if (id == R.id.action_settings) {
            MySettings.setActiveFragmentID(MySettings.FRAG_SETTINGS);
            showFragments();
            return true;

        } else if (id == R.id.action_help) {
            // TODO: make help fragment
            Toast.makeText(this, "TO COME: action_help", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.action_about) {
            // TODO: make about fragment
            Toast.makeText(this, "TO COME: action_about", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        MyLog.i("MainActivity", "onDestroy()");
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        MyLog.i("MainActivity", "onPause()");

        if (mIsDirty) {
            saveEncryptedData(false);
        }

        if (mJsonDataFile != null) {
            // stop listening for json data file changes
            mJsonDataFile.removeListener(mJsonDataFileListener);
            // close the json data file
            mJsonDataFile.close();
        }

//        dbxFs.shutDown();

        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyLog.i("MainActivity", "onResume()");
        if (mDbxAcctMgr.hasLinkedAccount()) {
            // show FrameLayout containers
            if (mFragment_container != null) {
                mFragment_container.setVisibility(View.VISIBLE);
            }
            if (mDetail_container != null) {
                mDetail_container.setVisibility(View.VISIBLE);
            }
            if (btnLinkToDropbox != null) {
                btnLinkToDropbox.setVisibility(View.GONE);
            }

            //mActivePasswordItemID = MySettings.getActivePasswordItemID();
            try {
                dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
                mJsonDataFileListener = new DbxFile.Listener() {
                    @Override
                    public void onFileChange(DbxFile dbxFile) {
                        MyLog.d("MainActivity", "onFileChange");
                        try {
                            DbxFileStatus status = mJsonDataFile.getNewerStatus();
                            MyLog.i("MainActivity", "onFileChange: Newer status: " + status);

                            if (status != null && status.isCached) {
                                MyLog.i("MainActivity", "onFileChange: Newer status.isCached");
                                mJsonDataFile.update();
                                MyLog.i("MainActivity", "onFileChange: mJsonDataFile.update()");
                                new readLabPasswordData().execute();
                            }
                        } catch (DbxException e) {
                            MyLog.e("MainActivity", "onFileChange: DbxException");
                            e.printStackTrace();
                        }
                    }
                };
                String appPassword = MySettings.getAppPassword();
                if (appPassword.equals(MySettings.NOT_AVAILABLE)) {
                    MySettings.setActiveFragmentID(MySettings.FRAG_APP_PASSWORD);
                    mArgBoolean = true;
                } else {
                    openAndReadLabPasswordDataAsync();
                }
                showFragments();
            } catch (DbxException.Unauthorized unauthorized) {
                MyLog.e("MainActivity", "onResume: DbxException.Unauthorized");
                unauthorized.printStackTrace();
            }
        } else {
            // hide FrameLayout containers
            if (mFragment_container != null) {
                mFragment_container.setVisibility(View.GONE);
            }
            if (mDetail_container != null) {
                mDetail_container.setVisibility(View.GONE);
            }
            if (btnLinkToDropbox != null) {
                btnLinkToDropbox.setVisibility(View.VISIBLE);
            }
        }
    }

    public static void openAndReadLabPasswordDataAsync() {

        new openAndReadLabPasswordData().execute();
    }

    private static void openJsonDataFile() {
        if (dbxFs == null) {
            MyLog.e("MainActivity", "openJsonDataFile FAILED; dbxFs == null");
            return;
        }

        try {
            dbxFs.awaitFirstSync();
            //dbxFs.syncNowAndWait();

            // check that there is a valid dropbox folder
            DbxPath folderPath = new DbxPath(MySettings.getDropboxFolderName());
            if (!dbxFs.isFolder(folderPath)) {
                MyLog.e("MainActivity", "openJsonDataFile FAILED; path: "
                        + MySettings.getDropboxFolderName() + " does not exist!");
                String title = "Open Data File";
                String message = "Unable to open data file; the data file does not exist!";
                EventBus.getDefault().post(new clsEvents.showOkDialog(title, message));
                return;
            }

            // check
            DbxPath filePath = new DbxPath(MySettings.getDropboxFilename());
            mJsonDataFile = null;
            if (dbxFs.isFile(filePath)) {
                mJsonDataFile = dbxFs.open(filePath);
                if (mJsonDataFile == null) {
                    MyLog.e("MainActivity", "openJsonDataFile FAILED. mJsonDataFile == null");
                    // TODO: Unable to open JSON data file. Show ok dialog.
                    return;
                }
            } else {
                MyLog.i("MainActivity", "openJsonDataFile: JSON file does not exist... creating file.");
                mJsonDataFile = dbxFs.create(filePath);
            }
            // start listening for changes
            mJsonDataFile.addListener(mJsonDataFileListener);


        } catch (DbxException e) {
            MyLog.e("MainActivity", "openJsonDataFile: DbxException");
            e.printStackTrace();
        }
    }

    private static void updateUI() {
        EventBus.getDefault().post(new clsEvents.updateUI());
        clsUsers activeUser = mPasswordsData.getUser(MySettings.getActiveUserID());
        if (activeUser != null) {
            // TODO: Implement plurals
            setActionBarTitle(activeUser.getUserName() + "'s Passwords");
        }
    }

    private static void readData() {
        String encryptedContents = "";
        try {
            if (mJsonDataFile == null) {
                MyLog.e("MainActivity", "readData FAILED. mJsonDataFile == null");
                return;
            }

            MyLog.d("MainActivity", "readData: mJsonDataFile.getSyncStatus = " + mJsonDataFile.getSyncStatus());
            encryptedContents = mJsonDataFile.readString();

        } catch (IOException e) {
            MyLog.e("MainActivity", "readData; IOException");
            e.printStackTrace();
        }

        if (!encryptedContents.isEmpty()) {
            String decryptedContents = "";
            try {
                CryptLib mCrypt = new CryptLib();
                decryptedContents = mCrypt.decrypt(encryptedContents,
                        MySettings.Credentials.getKey(), MySettings.Credentials.getIV());

            } catch (InvalidKeyException e) {
                MyLog.e("MainActivity", "readData: InvalidKeyException");
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                MyLog.e("MainActivity", "readData: NoSuchPaddingException");
                e.printStackTrace();
            } catch (BadPaddingException e) {
                MyLog.e("MainActivity", "readData: BadPaddingException");
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                MyLog.e("MainActivity", "readData: NoSuchAlgorithmException");
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                MyLog.e("MainActivity", "readData: IllegalBlockSizeException");
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                MyLog.e("MainActivity", "readData: UnsupportedEncodingException");
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                MyLog.e("MainActivity", "readData: InvalidAlgorithmParameterException");
                e.printStackTrace();
            }
            if (!decryptedContents.isEmpty()) {
                Gson gson = new Gson();
                mPasswordsData = gson.fromJson(decryptedContents, clsLabPasswords.class);
                if (mPasswordsData != null) {
                    sortPasswordsData();
                    int numberOfItems = mPasswordsData.getPasswordItems().size();
                    int numberOfUsers = mPasswordsData.getUsers().size();
                    int numberOfItemTypes = mPasswordsData.getItemTypes().size();
                    MyLog.d("MainActivity", "readData COMPLETE. "
                            + numberOfItems + " PasswordItems; "
                            + numberOfUsers + " Users; and "
                            + numberOfItemTypes + " ItemTypes");

                    int lastUserID = -1;
                    for (clsUsers user : mPasswordsData.getUsers()) {
                        if (user.getUserID() > lastUserID) {
                            lastUserID = user.getUserID();
                        }
                    }
                    MainActivity.setLastUserID(lastUserID);

                } else {
                    MyLog.d("MainActivity", "readData PASSWORDS DATA NULL!");
                }
            } else {
                MyLog.e("MainActivity", "readData decryptedContents.isEmpty");
            }
        } else {
            MyLog.e("MainActivity", "readData: file encryptedContents.isEmpty");
        }
    }

    private void saveEncryptedData(boolean showDialog) {
        // Create JSON file string
        Gson gson = new Gson();
        String jsonFileString = gson.toJson(mPasswordsData, clsLabPasswords.class);
        // MyLog.d("MainActivity", "saveEncryptedData: plain text string length = " + jsonFileString.length());
        String encryptedJsonFileString = "";

        // Encrypt JSON file string
        CryptLib mCrypt = null;
        try {
            mCrypt = new CryptLib();
            encryptedJsonFileString = mCrypt.encrypt(jsonFileString,
                    MySettings.Credentials.getKey(), MySettings.Credentials.getIV());
            // MyLog.d("MainActivity", "saveEncryptedData: encrypted text string length = " + encryptedJsonFileString.length());

        } catch (NoSuchAlgorithmException e) {
            MyLog.e("MainActivity", "saveEncryptedData: NoSuchAlgorithmException");
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            MyLog.e("MainActivity", "saveEncryptedData: NoSuchPaddingException");
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            MyLog.e("MainActivity", "saveEncryptedData: IllegalBlockSizeException");
            e.printStackTrace();
        } catch (BadPaddingException e) {
            MyLog.e("MainActivity", "saveEncryptedData: BadPaddingException");
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            MyLog.e("MainActivity", "saveEncryptedData: InvalidAlgorithmParameterException");
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            MyLog.e("MainActivity", "saveEncryptedData: InvalidKeyException");
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            MyLog.e("MainActivity", "saveEncryptedData: UnsupportedEncodingException");
            e.printStackTrace();
        }

        // Save encrypted JSON file string to Dropbox
        try {
            if (!encryptedJsonFileString.isEmpty()) {
                /*String filePathString = MySettings.getDropboxFolderName();
                DbxPath filePath = new DbxPath(filePathString);
                filePath = new DbxPath(filePath, "encryptedTest.txt");*/

                String filePathString = MySettings.getDropboxFilename();
                DbxPath filePath = new DbxPath(filePathString);

                if (mJsonDataFile != null) {
                    // you're going to change the json data file ...
                    // so temporarily stop listening for changes.
                    mJsonDataFile.removeListener(mJsonDataFileListener);

                    // write the file
                    mJsonDataFile.writeString(encryptedJsonFileString);
                    mIsDirty = false;
                    DbxFileInfo fileInfo = dbxFs.getFileInfo(filePath);
                    long fileSize = fileInfo.size;
                    if (showDialog) {
                        showOkDialog(this, "Success.", "Encrypted file saved.\nFile size = "
                                + NumberFormat.getInstance().format(fileSize) + " bytes.");
                    }
                    MyLog.i("MainActivity", "saveEncryptedData: encrypted file SAVED. File size = " + fileSize);

                    // resume listening for changes
                    mJsonDataFile.addListener(mJsonDataFileListener);
                }
            }
        } catch (DbxException e) {
            MyLog.e("MainActivity", "saveEncryptedData: DbxException");
            e.printStackTrace();
        } catch (IOException e) {
            MyLog.e("MainActivity", "saveEncryptedData: IOException");
            e.printStackTrace();
        }
    }

    public static void sortPasswordsData() {
        if (mPasswordsData != null) {
            if (mPasswordsData.getItemTypes() != null) {
                Collections.sort(mPasswordsData.getItemTypes(), new sortItemTypes());
            }

            if (mPasswordsData.getPasswordItems() != null) {
                Collections.sort(mPasswordsData.getPasswordItems(), new sortPasswordItems());
            }

            if (mPasswordsData.getUsers() != null) {
                Collections.sort(mPasswordsData.getUsers(), new sortUsers());
            }
        }
    }

    public static boolean itemNameExist(String itemName, int userID) {
        boolean result = false;
        itemName = itemName.trim();
        if (itemName.isEmpty()) {
            result = true;
        } else {
            for (clsPasswordItem item : mPasswordsData.getPasswordItems()) {
                if (item.getUser_ID() == userID) {
                    if (item.getName().equalsIgnoreCase(itemName)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static void showOkDialog(Context context, String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        // set dialog title and message
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static class readLabPasswordData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyLog.i("readLabPasswordData", "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            MyLog.i("readLabPasswordDataData", "doInBackground");
            readData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // if PasswordFragment is visible ...
            // then the following post will open the PasswordItemsListFragment
            EventBus.getDefault().post(new clsEvents.readLabPasswordDataComplete());
            if (mPasswordsData != null) {
                MyLog.i("readLabPasswordData", "onPostExecute: mPasswordsData not null.");
                updateUI();
            } else {
                MyLog.i("readLabPasswordData", "onPostExecute: mPasswordsData is NULL.");
            }
            mIsDirty = false;
        }
    }

    public static class openAndReadLabPasswordData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyLog.i("openAndReadLabPasswordData", "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            MyLog.i("openAndReadLabPasswordData", "doInBackground");
            openJsonDataFile();
            readData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // if PasswordFragment is visible ...
            // then the following post will open the PasswordItemsListFragment
            EventBus.getDefault().post(new clsEvents.readLabPasswordDataComplete());
            if (mPasswordsData != null) {
                MyLog.i("openAndReadLabPasswordData", "onPostExecute: mPasswordsData not null.");
                updateUI();
            } else {
                MyLog.i("openAndReadLabPasswordData", "onPostExecute: mPasswordsData is NULL.");
            }
            mIsDirty = false;
        }
    }

    private static class sortItemTypes implements Comparator {
        @Override
        public int compare(Object obj1, Object obj2) {
            clsItemTypes itemType1 = (clsItemTypes) obj1;
            clsItemTypes itemType2 = (clsItemTypes) obj2;
            return itemType1.getItemType().compareToIgnoreCase(itemType2.getItemType());
        }
    }

    private static class sortPasswordItems implements Comparator {
        @Override
        public int compare(Object obj1, Object obj2) {
            clsPasswordItem item1 = (clsPasswordItem) obj1;
            clsPasswordItem item2 = (clsPasswordItem) obj2;
            return item1.getName().compareToIgnoreCase(item2.getName());
        }
    }

    private static class sortUsers implements Comparator {
        @Override
        public int compare(Object obj1, Object obj2) {
            clsUsers user1 = (clsUsers) obj1;
            clsUsers user2 = (clsUsers) obj2;
            return user1.getUserName().compareToIgnoreCase(user2.getUserName());
        }
    }

}
