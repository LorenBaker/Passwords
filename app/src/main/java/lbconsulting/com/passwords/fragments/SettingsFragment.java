package lbconsulting.com.passwords.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import lbconsulting.com.passwords.R;
import lbconsulting.com.passwords.activities.MainActivity;
import lbconsulting.com.passwords.classes.MyLog;
import lbconsulting.com.passwords.classes.MySettings;
import lbconsulting.com.passwords.classes.clsEvents;
import lbconsulting.com.passwords.classes.clsUsers;

public class SettingsFragment extends Fragment implements View.OnClickListener {

    // fragment state variables

    private ArrayList<clsUsers> mUsers;
    private clsUsers mActiveUser;

    private Button btnSelectUser;
    private Button btnUserSettings;
    private Button btnSelectPasswordLongevity;
    private Button btnChangeAppPassword;
    private Button btnSelectDropboxFolder;

    private EditText txtAppPassword;
    private EditText txtConfirmAppPassword;
    private Button btnSave;
    private ImageView ivCheckMark;

    private Button btnPasswordDisplay;
    private Button btnConfirmPasswordDisplay;
    private boolean mShowPasswordText = false;
    private boolean mShowConfirmPasswordText = false;


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("SettingsFragment", "onCreate()");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("SettingsFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_settings, container, false);

        btnSelectUser = (Button) rootView.findViewById(R.id.btnSelectUser);
        btnUserSettings = (Button) rootView.findViewById(R.id.btnUserSettings);
        btnSelectPasswordLongevity = (Button) rootView.findViewById(R.id.btnSelectPasswordLongevity);
        btnChangeAppPassword = (Button) rootView.findViewById(R.id.btnChangeAppPassword);
        btnSelectDropboxFolder = (Button) rootView.findViewById(R.id.btnSelectDropboxFolder);

        btnSelectUser.setOnClickListener(this);
        btnUserSettings.setOnClickListener(this);
        btnSelectPasswordLongevity.setOnClickListener(this);
        btnChangeAppPassword.setOnClickListener(this);
        btnSelectDropboxFolder.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("SettingsFragment", "onActivityCreated()");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        MySettings.setOnSaveInstanceState(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("SettingsFragment", "onSaveInstanceState");
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("SettingsFragment", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_SETTINGS);
        updateUI();
    }

    private void updateUI() {
        if (MainActivity.getPasswordsData() != null) {
            mUsers = MainActivity.getPasswordsData().getUsers();
            if (mUsers != null) {
                mActiveUser = MySettings.getActiveUser();
                if (mActiveUser != null) {
                    btnSelectUser.setText(getString(R.string.btnSelectUser_text) + mActiveUser.getUserName());
                } else {
                    btnSelectUser.setText(getString(R.string.btnSelectUser_text) + getString(R.string.none_text));
                }
            }
            int passwordLongevity = (int) MySettings.getPasswordLongevity() / 60000;
            String longevityDescription = getLongevityDescription(passwordLongevity);
            btnSelectPasswordLongevity.setText("Select Password Longevity\n\nCurrent Longevity: " + longevityDescription);

            btnSelectDropboxFolder.setText(getString(R.string.btnSelectDropboxFolder_setText)
                    + MySettings.getDropboxFolderName());
        }
    }

    private String getLongevityDescription(int longevity) {
        String description;
        switch (longevity) {
            case 5:
                description = "5 min";
                break;
            case 15:
                description = "15 min";
                break;
            case 30:
                description = "30 min";
                break;
            case 60:
                description = "1 hr";
                break;
            case 240:
                description = "4 hrs";
                break;
            case 480:
                description = "8 hrs";
                break;
            default:
                description = "None";
                break;
        }
        return description;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Do Fragment menu item stuff here

            case android.R.id.home:
                EventBus.getDefault().post(new clsEvents.PopBackStack());
                return true;

            default:
                // Not implemented here
                return false;
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("SettingsFragment", "onPause()");
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("SettingsFragment", "onDestroy()");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSelectUser:
                // Toast.makeText(getActivity(), "TO COME: btnSelectUser", Toast.LENGTH_SHORT).show();
                if (MainActivity.getPasswordsData() != null) {
                    // Strings to Show In Dialog with Radio Buttons
                    final ArrayList<clsUsers> users = MainActivity.getPasswordsData().getUsers();
                    ArrayList<String> userNames = new ArrayList<>();
                    if (users != null) {
                        for (clsUsers user : users) {
                            if (user.getUserID() > 0) {
                                userNames.add(user.getUserName());
                            }
                        }
                    }
                    CharSequence[] names = userNames.toArray(new CharSequence[userNames.size()]);
                    int selectedUserPosition = -1;

                    if (mActiveUser != null) {
                        for (int i = 0; i < names.length; i++) {
                            if (names[i].toString().equals(mActiveUser.getUserName())) {
                                selectedUserPosition = i;
                                break;
                            }
                        }
                    }
                    // Creating and Building the Dialog
                    Dialog usersDialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Select User");
                    builder.setSingleChoiceItems(names, selectedUserPosition, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            if (users != null) {
                                mActiveUser = users.get(item);
                            }
                            selectActiveUser();
                            dialog.dismiss();
                        }
                    });
                    usersDialog = builder.create();
                    usersDialog.show();
                }
                break;

            case R.id.btnUserSettings:
                EventBus.getDefault().post(new clsEvents.replaceFragment(-1, MySettings.FRAG_USER_SETTINGS, false));
                break;

            case R.id.btnSelectPasswordLongevity:
                // Strings to Show In Dialog with Radio Buttons
                final CharSequence[] items = {"None", "5 min", "15 min", "30 min", "1 hr", "4 hrs", "8 hrs"};

                long passwordLongevity = MySettings.getPasswordLongevity();
                int longevity = (int) passwordLongevity / 60000;
                int selectedLongevityPosition;
                switch (longevity) {
                    case 5:
                        selectedLongevityPosition = 1;
                        break;
                    case 15:
                        selectedLongevityPosition = 2;
                        break;
                    case 30:
                        selectedLongevityPosition = 3;
                        break;
                    case 60:
                        selectedLongevityPosition = 4;
                        break;
                    case 240:
                        selectedLongevityPosition = 5;
                        break;
                    case 480:
                        selectedLongevityPosition = 6;
                        break;
                    default:
                        selectedLongevityPosition = 0;
                        break;
                }


                // Creating and Building the Dialog
                Dialog usersDialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("App Password Longevity");
                builder.setSingleChoiceItems(items, selectedLongevityPosition, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int position) {
                        int newLongevity = 5;

                        switch (position) {
                            case 0:
                                newLongevity = -1;
                                break;

                            case 1:
                                newLongevity = 5;
                                break;
                            case 2:
                                newLongevity = 15;
                                break;
                            case 3:
                                newLongevity = 30;
                                break;
                            case 4:
                                newLongevity = 60;
                                break;
                            case 5:
                                newLongevity = 240;
                                break;
                            case 6:
                                newLongevity = 480;
                                break;
                        }

                        String newLongevityDescription = getLongevityDescription(newLongevity);
                        long longevity = newLongevity * 60000;
                        MySettings.setPasswordLongevity(longevity);
                        btnSelectPasswordLongevity.setText("Select Password Longevity\n\nCurrent Longevity: " + newLongevityDescription);
                        dialog.dismiss();
                    }
                });
                usersDialog = builder.create();
                usersDialog.show();
                break;

            case R.id.btnSelectDropboxFolder:
                EventBus.getDefault().post(new clsEvents.replaceFragment(-1, MySettings.FRAG_DROPBOX_LIST, false));
                break;

            case R.id.btnChangeAppPassword:
                // custom dialog
                final Dialog changePasswordDialog = new Dialog(getActivity());
                changePasswordDialog.setContentView(R.layout.dialog_app_change_password);
                changePasswordDialog.setTitle("Change Password");

                // set the custom dialog components - text, image and button
                txtAppPassword = (EditText) changePasswordDialog.findViewById(R.id.txtAppPassword);
                txtConfirmAppPassword = (EditText) changePasswordDialog.findViewById(R.id.txtConfirmAppPassword);
                ivCheckMark = (ImageView) changePasswordDialog.findViewById(R.id.ivCheckMark);
                ivCheckMark.setImageResource(R.drawable.btn_check_buttonless_off);

                txtAppPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        validatePasswordsAreTheSame();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                txtConfirmAppPassword.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        validatePasswordsAreTheSame();
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });

                btnPasswordDisplay = (Button) changePasswordDialog.findViewById(R.id.btnPasswordDisplay);
                btnConfirmPasswordDisplay = (Button) changePasswordDialog.findViewById(R.id.btnConfirmPasswordDisplay);

                btnPasswordDisplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mShowPasswordText) {
                            txtAppPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);

                            btnPasswordDisplay.setText(getString(R.string.btnDisplay_setText_Display));
                        } else {
                            txtAppPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            btnPasswordDisplay.setText(getString(R.string.btnDisplay_setText_Hide));
                        }
                        txtAppPassword.setSelection(txtAppPassword.getText().length());
                        mShowPasswordText = !mShowPasswordText;
                    }
                });

                btnConfirmPasswordDisplay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mShowConfirmPasswordText) {
                            txtConfirmAppPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);

                            btnConfirmPasswordDisplay.setText(getString(R.string.btnDisplay_setText_Display));
                        } else {
                            txtConfirmAppPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                            btnConfirmPasswordDisplay.setText(getString(R.string.btnDisplay_setText_Hide));
                        }
                        txtConfirmAppPassword.setSelection(txtConfirmAppPassword.getText().length());
                        mShowConfirmPasswordText = !mShowConfirmPasswordText;
                    }
                });


                Button btnCancel = (Button) changePasswordDialog.findViewById(R.id.btnCancel);
                // do nothing, close the dialog
                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        changePasswordDialog.dismiss();
                    }
                });

                btnSave = (Button) changePasswordDialog.findViewById(R.id.btnSave);
                btnSave.setEnabled(false);
                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String appPassword = txtAppPassword.getText().toString().trim();
                        if (appPasswordIsValid(appPassword)) {
                            MySettings.setAppPassword(appPassword);
                        }
                        Toast.makeText(getActivity(), "Password \"" + appPassword + "\" saved.", Toast.LENGTH_SHORT).show();
                        EventBus.getDefault().post(new clsEvents.saveChangesToDropbox());
                        changePasswordDialog.dismiss();
                    }
                });

                changePasswordDialog.show();
        }

    }

    private boolean appPasswordIsValid(String password) {
        boolean result = false;
        if (!password.isEmpty()) {
            result = true;
        } else {
            MainActivity.showOkDialog(getActivity(), "Invalid Password", "No password provided!");
        }
        return result;
    }

    private void validatePasswordsAreTheSame() {
        String password = txtAppPassword.getText().toString().trim();
        String confirmAppPassword = txtConfirmAppPassword.getText().toString().trim();

        if (password.equals(confirmAppPassword) && !password.isEmpty()) {
            btnSave.setEnabled(true);
            ivCheckMark.setImageResource(R.drawable.btn_check_buttonless_on);
        } else {
            btnSave.setEnabled(false);
            ivCheckMark.setImageResource(R.drawable.btn_check_buttonless_off);
        }
    }


    private void selectActiveUser() {
        MySettings.setActiveUserID(mActiveUser.getUserID());
        updateUI();
/*        if (saveToDropbox) {
            EventBus.getDefault().post(new clsEvents.saveChangesToDropbox());
        }*/
        EventBus.getDefault().post(new clsEvents.replaceFragment(-1, MySettings.FRAG_ITEMS_LIST, false));
    }
}
