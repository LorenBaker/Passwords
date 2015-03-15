package lbconsulting.com.passwords.activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Comparator;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import de.greenrobot.event.EventBus;
import lbconsulting.com.passwords.classes.CryptLib;
import lbconsulting.com.passwords.classes.MyLog;
import lbconsulting.com.passwords.classes.MySettings;
import lbconsulting.com.passwords.classes.clsEvents;
import lbconsulting.com.passwords.classes.clsItemTypes;
import lbconsulting.com.passwords.classes.clsLabPasswords;
import lbconsulting.com.passwords.classes.clsPasswordItem;
import lbconsulting.com.passwords.classes.clsUsers;
import lbconsulting.com.passwords.fragments.EditCreditCardFragment;
import lbconsulting.com.passwords.fragments.EditGeneralAccountFragment;
import lbconsulting.com.passwords.fragments.PasswordItemDetailFragment;
import lbconsulting.com.passwords.fragments.PasswordItemsListFragment;
import lbconsulting.com.passwords.R;


public class MainActivity extends FragmentActivity {

    private static final int REQUEST_LINK_TO_DBX = 999;  // This value is up to you
    private static final String APP_KEY = "kz0qsqlw52f41cy";
    private static final String APP_SECRET = "owdln6x88inn9vo";
    private DbxAccountManager mDbxAcctMgr;
    private static DbxFileSystem dbxFs;

    private static clsLabPasswords mPasswordsData;
    private static int mActivePasswordItemID;
    private static int mPreviousPasswordItemID;

    public static int getActivePasswordItemID() {
        return mActivePasswordItemID;
    }

    public static void setActivePasswordItemID(int activePasswordItemID) {
        mActivePasswordItemID = activePasswordItemID;
    }

    private boolean mIsNewPasswordItem;

    private static int mLastPasswordItemID;

    public static int getLastPasswordItemID() {
        return mLastPasswordItemID;
    }

    public static void setLastPasswordItemID(int lastPasswordItemID) {
        mLastPasswordItemID = lastPasswordItemID;
    }

    private static int mActivePosition;

    public static void setActivePosition(int position) {
        mActivePosition = position;
    }

    private static int mActiveFragmentID;

    public static void setActiveFragmentID(int activeFragmentID) {
        mActiveFragmentID = activeFragmentID;
    }

    private boolean mIsDirty = false;
    private boolean mTwoPane;

    private android.app.ActionBar mActionBar;
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
        for (clsPasswordItem item : mPasswordsData.getPasswordItems()) {
            if (item.getID() == mActivePasswordItemID) {
                result = item;
                break;
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
                mActivePasswordItemID = mPreviousPasswordItemID;
                result = true;
                break;
            }
            index++;
        }
        return result;
    }

    public static clsPasswordItem createNewPasswordItem() {
        mPreviousPasswordItemID = mActivePasswordItemID;
        clsPasswordItem newItem = new clsPasswordItem(getNextPasswordItemID(), MySettings.getActiveUserID());
        mPasswordsData.getPasswordItems().add(newItem);
        mActivePasswordItemID = newItem.getID();
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
        setContentView(R.layout.activity_password_items_list);
        EventBus.getDefault().register(this);

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

        // get the active fragment ID
        SharedPreferences passwordsSavedState =
                getSharedPreferences(MySettings.PASSWORDS_SAVED_STATE, 0);
        mActiveFragmentID = passwordsSavedState
                .getInt(MySettings.ARG_ACTIVE_FRAGMENT, MySettings.FRAG_ITEMS_LIST);
        showFragments();

    }

    private void onClickLinkToDropbox() {
        MyLog.i("MainActivity", "onClickLinkToDropbox");
        mDbxAcctMgr.startLink((Activity) this, REQUEST_LINK_TO_DBX);
    }

    public void onClickLinkToDropbox(View view) {
        mDbxAcctMgr.startLink((Activity) this, REQUEST_LINK_TO_DBX);
    }

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

            switch (mActiveFragmentID) {

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
            switch (mActiveFragmentID) {
                case MySettings.FRAG_ITEMS_LIST:
                    fm.beginTransaction()
                            .replace(R.id.fragment_container,
                                    PasswordItemsListFragment.newInstance())
                            .commit();
                    break;

                case MySettings.FRAG_ITEM_DETAIL:
                    mIsNewPasswordItem = false;
                    fm.beginTransaction()
                            .replace(R.id.fragment_container,
                                    PasswordItemDetailFragment.newInstance())
                            .addToBackStack("FRAG_ITEM_DETAIL")
                            .commit();
                    break;

                case MySettings.FRAG_EDIT_CREDIT_CARD:
                    fm.beginTransaction()
                            .replace(R.id.fragment_container,
                                    EditCreditCardFragment.newInstance(mIsNewPasswordItem))
                            .addToBackStack("FRAG_EDIT_CREDIT_CARD")
                            .commit();
                    break;

                case MySettings.FRAG_EDIT_GENERAL_ACCOUNT:
                    fm.beginTransaction()
                            .replace(R.id.fragment_container,
                                    EditGeneralAccountFragment.newInstance(mIsNewPasswordItem))
                            .addToBackStack("FRAG_EDIT_GENERAL_ACCOUNT")
                            .commit();
                    break;

                case MySettings.FRAG_EDIT_SOFTWARE:
                    //TODO: SinglePain: Show FRAG_EDIT_SOFTWARE

                    break;

                case MySettings.FRAG_EDIT_WEBSITE:
                    //TODO: SinglePain: Show FRAG_EDIT_WEBSITE

                    break;

            }
        }
    }

    public void onEvent(clsEvents.PopBackStack event) {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }

    public void onEvent(clsEvents.replaceFragment event) {
        mActiveFragmentID = event.getFragmentID();
        mIsNewPasswordItem = event.getIsNewPasswordItem();
        showFragments();
    }

    public void onEvent(clsEvents.isDirty event) {
        mIsDirty = true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //TODO: create help menu in fragments.
        MyLog.i("MainActivity", "onCreateOptionsMenu()");
        getMenuInflater().inflate(R.menu.menu_activity_password_items_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_upload) {
            //Toast.makeText(this, "TO COME: action_upload", Toast.LENGTH_SHORT).show();
            new readLabPasswordData().execute();
            return true;
        } else if (id == R.id.action_settings) {
            Toast.makeText(this, "TO COME: action_settings", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_help) {
            Toast.makeText(this, "TO COME: action_help", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_about) {
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
            encryptAndSaveData();
        }
        super.onPause();
    }

    private void encryptAndSaveData() {
        // TODO: 3/12/2015 Encrypt and save the app's data
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
            try {
                dbxFs = DbxFileSystem.forAccount(mDbxAcctMgr.getLinkedAccount());
                new readLabPasswordData().execute();
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

    private void readData() {
        try {

            DbxPath path = new DbxPath(MySettings.getDropboxPath());
            DbxPath filePath = null;
            String encryptedContents = "";

            dbxFs.awaitFirstSync();

            int sleepMils = 2000;
            boolean pathFound = false;
            for (int sleepCount = 1; sleepCount <= 20; sleepCount++) {
                if (dbxFs.isFolder(path)) {
                    pathFound = true;
                    break;
                } else {
                    // have not yet found the dropbox path
                    try {
                        MyLog.i("MainActivity", "readData,  *** " + sleepCount + " Thread sleeping");
                        Thread.sleep(sleepMils);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!pathFound) {
                MyLog.e("MainActivity", "readData FAILED; path: "
                        + MySettings.getDropboxPath() + " does not exist!");
                return;
            }

            filePath = new DbxPath(MySettings.getDropboxFilename());
            if (dbxFs.isFile(filePath)) {
                DbxFile jsonDataFile = null;
                try {
                    jsonDataFile = dbxFs.open(filePath);
                    MyLog.i("MainActivity", "readData: jsonDataFile.getSyncStatus = " + jsonDataFile.getSyncStatus());
                    encryptedContents = jsonDataFile.readString();

                    // TODO: 3/9/2015 Check that this is the correct place to add this file listener.
                    jsonDataFile.addListener(new DbxFile.Listener() {
                        @Override
                        public void onFileChange(DbxFile file) {
                            MyLog.d("*** MainActivity", "onFileChange");
                        }
                    });

                } catch (IOException e) {
                    MyLog.e("MainActivity", "readData; IOException");
                    e.printStackTrace();
                } finally {
                    if (jsonDataFile != null) {
                        jsonDataFile.close();
                    }
                }
            } else {
                MyLog.e("MainActivity", "readData; file: " + filePath.getName() + " does not exist!");
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

                    } else {
                        MyLog.d("MainActivity", "readData PASSWORDS DATA NULL!");
                    }
                } else {
                    MyLog.e("MainActivity", "readData decryptedContents.isEmpty");
                }
            } else {
                MyLog.e("MainActivity", "readData: file encryptedContents.isEmpty");
            }
        } catch (DbxException e) {
            MyLog.e("MainActivity", "readData; DbxException");
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


    public class readLabPasswordData extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyLog.i("readLabPasswordData", "onPreExecute");
        }

        @Override
        protected Void doInBackground(Void... params) {
            MyLog.i("readLabPasswordData", "doInBackground");
            readData();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mPasswordsData != null) {
                MyLog.i("readLabPasswordData", "onPostExecute: mPasswordsData not null.");
                EventBus.getDefault().post(new clsEvents.updateUI());
                clsUsers activeUser = mPasswordsData.getUsers().get(MySettings.getActiveUserID());
                if (activeUser != null) {
                    // TODO: 3/9/2015 Implement plurals
                    mActionBar.setTitle(activeUser.getUserName() + "'s Passwords");
                }
            } else {
                MyLog.i("readLabPasswordData", "onPostExecute: mPasswordsData is NULL.");
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
