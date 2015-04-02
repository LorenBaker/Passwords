package lbconsulting.com.passwords.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import lbconsulting.com.passwords.R;
import lbconsulting.com.passwords.activities.MainActivity;
import lbconsulting.com.passwords.classes.MyLog;
import lbconsulting.com.passwords.classes.MySettings;
import lbconsulting.com.passwords.classes.clsEvents;
import lbconsulting.com.passwords.classes.clsUsers;

/**
 * A fragment that allows the editing of a Credit Card
 */
public class AppPasswordFragment extends Fragment implements View.OnClickListener {

    // fragment state variables
    private static final String ARG_IS_CHANGING_PASSWORD = "isChangingPassword";
    private boolean mIsChangingPassword = false;
    private boolean mShowPasswordText = false;
    private clsUsers mActiveUser;

    private ProgressBar progressBar;
    private TextView tvFirstTimeMessage;
    private Button btnCreateNewUser;
    private Button btnSelectUser;
    private Button btnSelectDropboxFolder;
    private EditText txtAppPassword;
    private Button btnDisplay;
    private Button btnOK;
    private Button btnOkReadPasswordFile;

    public static final int STATE_STEP_0 = 0;
    private static final int STATE_STEP_1 = 10;
    private static final int STATE_STEP_2 = 20;
    private static final int STATE_STEP_2a = 21;
    private static final int STATE_STEP_3a = 31;
    private static final int STATE_STEP_2b = 22;
    private static final int STATE_STEP_3b = 32;
    private static final int STATE_PASSWORD_ONLY = 40;
    private int mState = STATE_STEP_0;
    private int mDownloadResults = MainActivity.DOWNLOAD_RESULT_FAIL_READING_ENCRYPTED_FILE;


    public static AppPasswordFragment newInstance(boolean isChangingPassword) {
        AppPasswordFragment fragment = new AppPasswordFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_CHANGING_PASSWORD, isChangingPassword);
        fragment.setArguments(args);
        return fragment;
    }

    public AppPasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("AppPasswordFragment", "onCreate()");

        if (getArguments() != null) {
            mIsChangingPassword = getArguments().getBoolean(ARG_IS_CHANGING_PASSWORD);
        }

        EventBus.getDefault().register(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("AppPasswordFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_app_password, container, false);
        // TODO: Add caption TextView for progressBar
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        tvFirstTimeMessage = (TextView) rootView.findViewById(R.id.tvFirstTimeMessage);

        txtAppPassword = (EditText) rootView.findViewById(R.id.txtAppPassword);
        btnDisplay = (Button) rootView.findViewById(R.id.btnDisplay);
        btnOK = (Button) rootView.findViewById(R.id.btnOK);
        btnOkReadPasswordFile = (Button) rootView.findViewById(R.id.btnOkReadPasswordFile);
        btnCreateNewUser = (Button) rootView.findViewById(R.id.btnCreateNewUser);
        btnSelectUser = (Button) rootView.findViewById(R.id.btnSelectUser);
        btnSelectDropboxFolder = (Button) rootView.findViewById(R.id.btnSelectDropboxFolder);

        btnDisplay.setOnClickListener(this);
        btnOK.setOnClickListener(this);
        btnOkReadPasswordFile.setOnClickListener(this);
        btnCreateNewUser.setOnClickListener(this);
        btnSelectUser.setOnClickListener(this);
        btnSelectDropboxFolder.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("AppPasswordFragment", "onActivityCreated()");
    }


    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("AppPasswordFragment", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_APP_PASSWORD);
        mState = MySettings.getAppPasswordState();
        updateUI();
    }

    private void updateUI() {
        // TODO: Remove "Test Password"
        txtAppPassword.setText("Test Password");

        switch (mState) {
            case STATE_PASSWORD_ONLY:
                MyLog.i("AppPasswordFragment", "updateUI(): State = STATE_PASSWORD_ONLY");
                showPasswordOnly();
                break;

            case STATE_STEP_0:
                MyLog.i("AppPasswordFragment", "updateUI(): State = STATE_STEP_0");

                showProgressBar();
                MySettings.setAppPasswordState(STATE_STEP_1);
                mState = STATE_STEP_1;
                new waitForFirstSync().execute();
                break;

            case STATE_STEP_1:
                MyLog.i("AppPasswordFragment", "updateUI(): State = STATE_STEP_1");
                // Select dropbox folder
                showStep1();
                MySettings.setAppPasswordState(STATE_STEP_2);
                mState = STATE_STEP_2;
                break;

            case STATE_STEP_2:
                MyLog.i("AppPasswordFragment", "updateUI(): State = STATE_STEP_2");
                // Select user
                if (passwordsDataFileExists()) {
                    // Heading down "Path a"
                    // Get password from user
                    showStep2a();
                    MySettings.setAppPasswordState(STATE_STEP_3a);
                    mState = STATE_STEP_3a;
                } else {
                    // Heading down "Path b"
                    // Create new user.
                    showStep2b();
                    MySettings.setAppPasswordState(STATE_STEP_3b);
                    mState = STATE_STEP_3b;
                }
                break;

            case STATE_STEP_2a:
                MyLog.i("AppPasswordFragment", "updateUI(): State=STATE_STEP_2a");
                // Select user
                showStep2a();
                MySettings.setAppPasswordState(STATE_STEP_3a);
                mState = STATE_STEP_3a;
                break;

            case STATE_STEP_3a:
                MyLog.i("AppPasswordFragment", "updateUI(): State=STATE_STEP_3a: Select User");
                // Select user
                showStep3a();
                break;

            case STATE_STEP_3b:
                MyLog.i("AppPasswordFragment", "updateUI(): State=STATE_STEP_3b: Select Password");
                // Get password from user
                showStep3b();
                break;
        }
    }

    public void onEvent(clsEvents.updateUI event) {
        updateUI();
    }

    public void onEvent(clsEvents.downLoadResults event) {
        mDownloadResults = event.getDownLoadResults();
        if (mDownloadResults == MainActivity.DOWNLOAD_RESULT_SUCCESS) {
            switch (mState) {

                case STATE_PASSWORD_ONLY:
                case STATE_STEP_3b:
                    // we're all done
                    MySettings.setAppPasswordState(STATE_PASSWORD_ONLY);
                    mState = STATE_PASSWORD_ONLY;
                    EventBus.getDefault().post(new clsEvents.replaceFragment(-1, MySettings.FRAG_ITEMS_LIST, false));
                    break;

                case STATE_STEP_3a:
                    updateUI();
                    break;
            }

        } else if (mDownloadResults == MainActivity.OPEN_AND_SAVE_FILE_RESULT_SUCCESS) {
            // we're all done
            MySettings.setAppPasswordState(STATE_PASSWORD_ONLY);
            mState = STATE_PASSWORD_ONLY;
            EventBus.getDefault().post(new clsEvents.replaceFragment(-1, MySettings.FRAG_ITEMS_LIST, false));

        } else {
            // reading password data failed
            String title = "Reading Datafile Failed";
            String message = "";
            switch (mDownloadResults) {

                case MainActivity.OPEN_FILE_RESULT_FAIL:
                    message = "Unable to open datafile.";
                    break;

                case MainActivity.DOWNLOAD_RESULT_FAIL_READING_ENCRYPTED_FILE:
                    message = "Unable to read encrypted datafile from Dropbox folder.";
                    break;

                case MainActivity.DOWNLOAD_RESULT_FAIL_DECRYPTING_FILE:
                    message = "Invalid password.\nPlease try again.";
                    break;

                case MainActivity.DOWNLOAD_RESULT_FAIL_PARSING_JSON:
                    message = "Unable to parse decrypted datafile.";
                    break;

                case MainActivity.OPEN_AND_SAVE_FILE_RESULT_FAIL:
                    title = "Saving Datafile Failed";
                    message = "Datafile size is zero!";
                    break;
            }
            MainActivity.showOkDialog(getActivity(), title, message);
            switch (mState) {

                case STATE_PASSWORD_ONLY:
                    showPasswordOnly();
                    break;

                case STATE_STEP_3b:
                    showStep3b();
                    break;

                case STATE_STEP_3a:
                    // go back to state 2a -- Get Password from user
                    MySettings.setAppPasswordState(STATE_STEP_2a);
                    mState = STATE_STEP_2a;
                    updateUI();
                    break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("AppPasswordFragment", "onPause()");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MyLog.i("AppPasswordFragment", "onDestroy()");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnDisplay:

                if (mShowPasswordText) {
                    txtAppPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    btnDisplay.setText(getString(R.string.btnDisplay_setText_Display));
                } else {
                    txtAppPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnDisplay.setText(getString(R.string.btnDisplay_setText_Hide));
                }
                txtAppPassword.setSelection(txtAppPassword.getText().length());
                mShowPasswordText = !mShowPasswordText;
                break;

            case R.id.btnOK:
                switch (mState) {

                    case STATE_PASSWORD_ONLY:
                        if (dropboxFolderExists() && userSelected()) {
                            String appPassword = txtAppPassword.getText().toString().trim();
                            if (appPasswordIsValid(appPassword)) {
                                MySettings.setAppPassword(appPassword);
                                showProgressBar();
                                EventBus.getDefault().post(new clsEvents.openAndReadLabPasswordDataAsync());
                            }
                        } else {
                            MyLog.e("AppPasswordFragment", "onClick: from STATE_PASSWORD_ONLY (Get password from user). " +
                                    "Either dropbox folder does not exits OR user not selected.");
                        }
                        break;

                    case STATE_STEP_3a:
                        if (dropboxFolderExists() && userSelected()) {
                            // open passwords list items fragment
                            MySettings.setAppPasswordState(STATE_PASSWORD_ONLY);
                            mState = STATE_PASSWORD_ONLY;
                            EventBus.getDefault().post(new clsEvents.replaceFragment(-1, MySettings.FRAG_ITEMS_LIST, false));
                        } else {
                            MyLog.e("AppPasswordFragment", "onClick: from STATE_STEP_3a (Select User). " +
                                    "Either dropbox folder does not exits OR user not selected.");
                        }
                        break;

                    case STATE_STEP_3b:
                        if (dropboxFolderExists() && userSelected()) {
                            String appPassword = txtAppPassword.getText().toString().trim();
                            if (appPasswordIsValid(appPassword)) {
                                MySettings.setAppPassword(appPassword);
                                EventBus.getDefault().post(new clsEvents.openAndSaveLabPasswordDataAsync());
                            }

                        } else {
                            MyLog.e("AppPasswordFragment", "onClick: from STATE_STEP_3b (Get Password from User). " +
                                    "Either dropbox folder does not exits OR user not selected.");
                        }
                        break;
                }
                break;

            case R.id.btnOkReadPasswordFile:
                String appPassword = txtAppPassword.getText().toString().trim();
                if (appPasswordIsValid(appPassword)) {
                    MySettings.setAppPassword(appPassword);
                    showProgressBar();
                    EventBus.getDefault().post(new clsEvents.openAndReadLabPasswordDataAsync());
                }
                break;

            case R.id.btnCreateNewUser:
                AlertDialog.Builder newUserDialog = new AlertDialog.Builder(getActivity());

                newUserDialog.setTitle("Enter New User Name");
                newUserDialog.setMessage("");

                // TODO: Make EditText work like the App's other EditText boxes
                // Set an EditText view to get user input
                final EditText input = new EditText(getActivity());
                input.setHint("New User Name");
                input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                newUserDialog.setView(input);

                newUserDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newUserName = input.getText().toString().trim();
                        if (isUnique(newUserName)) {
                            int newUserID = MainActivity.getNextUserID();
                            mActiveUser = new clsUsers();
                            mActiveUser.setUserID(newUserID);
                            mActiveUser.setUserName(newUserName);
                            MySettings.setActiveUserID(newUserID);
                            MySettings.setActiveUserName(newUserName);
                            MainActivity.addNewUser(mActiveUser);
                            updateUI();
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                            MyLog.e("AppPasswordFragment", "onClick OK: new user is not unique");
                            MainActivity.showOkDialog(getActivity(), "Failed to create new user",
                                    "The provide user name \"" + newUserName + "\" already exists!");
                        }
                    }
                });

                newUserDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.dismiss();
                    }
                });

                newUserDialog.show();
                break;

            case R.id.btnSelectUser:
                // from Step 3a - Select user
                if (MainActivity.getPasswordsData() != null) {
                    // Strings to Show In Dialog with Radio Buttons
                    final ArrayList<clsUsers> users = MainActivity.getPasswordsData().getUsers();
                    ArrayList<String> userNames = new ArrayList<>();
                    if (users != null) {
                        for (clsUsers user : users) {
                            userNames.add(user.getUserName());
                        }
                    }
                    CharSequence[] names = userNames.toArray(new CharSequence[userNames.size()]);
                    int selectedUserPosition = -1;
                    clsUsers activeUser = MySettings.getActiveUser();

                    // find selectedUserPosition
                    if (activeUser != null) {
                        for (int i = 0; i < names.length; i++) {
                            if (names[i].toString().equals(activeUser.getUserName())) {
                                selectedUserPosition = i;
                                break;
                            }
                        }
                    }
                    // Creating and Building the Dialog
                    Dialog usersDialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.btnSelectUser_dialog_setTitle));
                    builder.setSingleChoiceItems(names, selectedUserPosition, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            clsUsers selectedUser = users.get(item);
                            MySettings.setActiveUserID(selectedUser.getUserID());
                            MySettings.setActiveUserName(selectedUser.getUserName());
                            // MySettings.setAppPasswordState(STATE_PASSWORD_ONLY);
                            updateUI();
                            dialog.dismiss();
                        }
                    });
                    usersDialog = builder.create();
                    usersDialog.show();
                }
                break;

            case R.id.btnSelectDropboxFolder:
                EventBus.getDefault().post(new clsEvents.replaceFragment(-1, MySettings.FRAG_DROPBOX_LIST, false));
                //Toast.makeText(getActivity(), "TO COME: btnSelectDropboxFolder", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private boolean userSelected() {
        boolean result = false;
        int activeUserID = MySettings.getActiveUserID();
        if (activeUserID > 0) {
            result = true;
        } else {
            String title = "Please Create A New User";
            String message = "Unable to continue; no users exist!";
            MainActivity.showOkDialog(getActivity(), title, message);
        }
        return result;
    }

    private boolean dropboxFolderExists() {
        boolean result = false;
        // check that there is a valid dropbox folder
        DbxFileSystem dbxFs = MainActivity.getDbxFs();
        DbxPath folderPath = new DbxPath(MySettings.getDropboxFolderName());
        if (dbxFs != null) {
            try {
                if (dbxFs.isFolder(folderPath)) {
                    result = true;
                } else {
                    String title = "Please Select Dropbox Folder";
                    String message = "Unable to continue; the Dropbox folder does not exist!";
                    MainActivity.showOkDialog(getActivity(), title, message);
                }

            } catch (DbxException e) {
                MyLog.e("AppPasswordFragment", "dropboxFolderExists: DbxException");
                e.printStackTrace();
            }
        }
        return result;

    }

    private boolean passwordsDataFileExists() {
        boolean result = false;
        // check dropbox data file
        DbxFileSystem dbxFs = MainActivity.getDbxFs();
        if (dbxFs != null) {
            try {
                // check for data file
                DbxPath filePath = new DbxPath(MySettings.getDropboxFilename());
                if (dbxFs.isFile(filePath)) {
                    result = true;
                }
            } catch (DbxException e) {
                MyLog.e("AppPasswordFragment", "passwordsDataFileExists: DbxException");
                e.printStackTrace();
            }
        }
        return result;
    }

    private boolean appPasswordIsValid(String password) {
        boolean result = false;
        if (!password.isEmpty()) {
            result = true;
        } else {
            MainActivity.showOkDialog(getActivity(), "Invalid Password", "No password provided!");
        }
        // TODO: add more app password validation rules ??
        return result;
    }

    private boolean isUnique(String newUserName) {
        // TODO: Move to Main Activity ??
        boolean result = true;
        if (MainActivity.getPasswordsData() != null) {
            for (clsUsers user : MainActivity.getPasswordsData().getUsers()) {
                if (user.getUserName().equalsIgnoreCase(newUserName)) {
                    result = false;
                    break;
                }
            }
        }
        return result;
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        tvFirstTimeMessage.setVisibility(View.GONE);

        btnCreateNewUser.setVisibility(View.GONE);
        btnSelectUser.setVisibility(View.GONE);
        btnSelectDropboxFolder.setVisibility(View.GONE);

        txtAppPassword.setVisibility(View.GONE);
        btnDisplay.setVisibility(View.GONE);
        btnOK.setVisibility(View.GONE);
        btnOkReadPasswordFile.setVisibility(View.GONE);
    }

    private void showStep1() {
        // Select dropbox folder
        showStep1_StartUpViews();
        showButtonText();
    }

    private void showStep2a() {
        // Select user
        showStep2a_StartUpViews();
        showButtonText();
    }

    private void showStep3a() {
        // Select password
        showStep3a_StartUpViews();
        showButtonText();
    }

    private void showStep2b() {
        // Select user
        showStep2b_StartUpViews();
        showButtonText();
    }

    private void showStep3b() {
        // Select password
        showStep3b_StartUpViews();
        showButtonText();
    }


    private void showPasswordOnly() {
        progressBar.setVisibility(View.GONE);

        tvFirstTimeMessage.setVisibility(View.GONE);

        txtAppPassword.setVisibility(View.VISIBLE);
        btnDisplay.setVisibility(View.VISIBLE);
        btnOK.setVisibility(View.VISIBLE);
        btnOkReadPasswordFile.setVisibility(View.GONE);

        btnCreateNewUser.setVisibility(View.GONE);
        btnSelectUser.setVisibility(View.GONE);

        btnSelectDropboxFolder.setVisibility(View.GONE);

    }

    private void showStep1_StartUpViews() {
        // Select Dropbox folder
        progressBar.setVisibility(View.GONE);

        tvFirstTimeMessage.setVisibility(View.VISIBLE);
        tvFirstTimeMessage.setText(getResources().getString(R.string.tvFirstTimeMessage_text_Step1));

        txtAppPassword.setVisibility(View.GONE);
        btnDisplay.setVisibility(View.GONE);
        btnOK.setVisibility(View.GONE);
        btnOkReadPasswordFile.setVisibility(View.GONE);

        btnCreateNewUser.setVisibility(View.GONE);
        btnSelectUser.setVisibility(View.GONE);

        btnSelectDropboxFolder.setVisibility(View.VISIBLE);
    }

    private void showStep2a_StartUpViews() {
        // Get password from user
        progressBar.setVisibility(View.GONE);

        tvFirstTimeMessage.setVisibility(View.VISIBLE);
        tvFirstTimeMessage.setText(getResources().getString(R.string.tvFirstTimeMessage_text_Step2a));

        txtAppPassword.setVisibility(View.VISIBLE);
        btnDisplay.setVisibility(View.VISIBLE);
        btnOK.setVisibility(View.GONE);
        btnOkReadPasswordFile.setVisibility(View.VISIBLE);

        btnCreateNewUser.setVisibility(View.GONE);
        btnSelectUser.setVisibility(View.GONE);

        btnSelectDropboxFolder.setVisibility(View.GONE);
    }

    private void showStep3a_StartUpViews() {
        // Select User
        progressBar.setVisibility(View.GONE);

        tvFirstTimeMessage.setVisibility(View.VISIBLE);
        tvFirstTimeMessage.setText(getResources().getString(R.string.tvFirstTimeMessage_text_Step3a));

        txtAppPassword.setVisibility(View.GONE);
        btnDisplay.setVisibility(View.GONE);
        btnOK.setVisibility(View.VISIBLE);
        btnOkReadPasswordFile.setVisibility(View.GONE);

        btnCreateNewUser.setVisibility(View.GONE);
        btnSelectUser.setVisibility(View.VISIBLE);

        btnSelectDropboxFolder.setVisibility(View.GONE);
    }

    private void showStep2b_StartUpViews() {
        // Create new user
        progressBar.setVisibility(View.GONE);

        tvFirstTimeMessage.setVisibility(View.VISIBLE);
        tvFirstTimeMessage.setText(getResources().getString(R.string.tvFirstTimeMessage_text_Step2b));

        txtAppPassword.setVisibility(View.GONE);
        btnDisplay.setVisibility(View.GONE);
        btnOK.setVisibility(View.VISIBLE);
        btnOkReadPasswordFile.setVisibility(View.GONE);

        btnCreateNewUser.setVisibility(View.VISIBLE);
        btnSelectUser.setVisibility(View.GONE);

        btnSelectDropboxFolder.setVisibility(View.GONE);
    }

    private void showStep3b_StartUpViews() {
        // Get password from user
        progressBar.setVisibility(View.GONE);

        tvFirstTimeMessage.setVisibility(View.VISIBLE);
        tvFirstTimeMessage.setText(getResources().getString(R.string.tvFirstTimeMessage_text_Step3b));

        txtAppPassword.setVisibility(View.VISIBLE);
        btnDisplay.setVisibility(View.VISIBLE);
        btnOK.setVisibility(View.VISIBLE);
        btnOkReadPasswordFile.setVisibility(View.GONE);

        btnCreateNewUser.setVisibility(View.GONE);
        btnSelectUser.setVisibility(View.GONE);

        btnSelectDropboxFolder.setVisibility(View.GONE);
    }

    private void showButtonText() {

        if (mActiveUser != null) {
            btnSelectUser.setText(getString(R.string.btnSelectUser_text) + mActiveUser.getUserName());
        }
        btnSelectDropboxFolder.setText(getString(R.string.btnSelectDropboxFolder_setText)
                + MySettings.getDropboxFolderName());
    }

    public static class waitForFirstSync extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MyLog.i("waitForFirstSync", "onPreExecute()");
        }

        @Override
        protected Void doInBackground(Void... params) {
            MyLog.i("waitForFirstSync", "doInBackground()");
            DbxFileSystem dbxFs = MainActivity.getDbxFs();
            try {
                if (dbxFs != null) {
                    dbxFs.awaitFirstSync();
                }
            } catch (DbxException e) {
                MyLog.e("waitForFirstSync", "doInBackground: DbxException");
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            MyLog.i("waitForFirstSync", "onPostExecute()");
            EventBus.getDefault().post(new clsEvents.updateUI());
        }
    }
}
