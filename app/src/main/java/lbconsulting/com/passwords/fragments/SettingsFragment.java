package lbconsulting.com.passwords.fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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
public class SettingsFragment extends Fragment implements View.OnClickListener {

    // fragment state variables

    private ArrayList<clsUsers> mUsers;
    private clsUsers mActiveUser;
    private static final String ARG_IS_FIRST_TIME = "isFirstTime";
    private boolean mIsFirstTime = false;

/*    private TextView tvFirstTimeMessage;
    private View line;*/
    private Button btnSelectUser;
    private Button btnCreateNewUser;
    private Button btnEditUserName;
    private Button btnSelectPasswordLongevity;
    private Button btnSelectDropboxFolder;


    public static SettingsFragment newInstance(boolean isFirstTime) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_FIRST_TIME, isFirstTime);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("SettingsFragment", "onCreate()");

        if (getArguments() != null) {
            mIsFirstTime = getArguments().getBoolean(ARG_IS_FIRST_TIME);
        }

        setHasOptionsMenu(true);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("SettingsFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_settings, container, false);

/*        line = (View) rootView.findViewById(R.id.line1);
        tvFirstTimeMessage = (TextView) rootView.findViewById(R.id.tvFirstTimeMessage);*/
/*        if (!mIsFirstTime) {
            tvFirstTimeMessage.setVisibility(View.GONE);
            line.setVisibility(View.GONE);
        } else {
            tvFirstTimeMessage.setText("Step 1: Select Dropbox folder.\nStep 2: Create new user.");
            tvFirstTimeMessage.setTypeface(null, Typeface.BOLD);
        }*/
        btnSelectUser = (Button) rootView.findViewById(R.id.btnSelectUser);
        btnCreateNewUser = (Button) rootView.findViewById(R.id.btnCreateNewUser);
        btnEditUserName = (Button) rootView.findViewById(R.id.btnEditUserName);
        btnSelectPasswordLongevity = (Button) rootView.findViewById(R.id.btnSelectPasswordLongevity);
        btnSelectDropboxFolder = (Button) rootView.findViewById(R.id.btnSelectDropboxFolder);

        btnSelectUser.setOnClickListener(this);
        btnCreateNewUser.setOnClickListener(this);
        btnEditUserName.setOnClickListener(this);
        btnSelectPasswordLongevity.setOnClickListener(this);
        btnSelectDropboxFolder.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("SettingsFragment", "onActivityCreated()");
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("SettingsFragment", "onSaveInstanceState()");
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
                    btnSelectUser.setText("Select User\n\nCurrent user: " + mActiveUser.getUserName());
                } else {
                    btnSelectUser.setText("Select User\n\nCurrent user: NONE");
                }
            }
            String longevityDescription = getLongevityDescription(MySettings.getPasswordLongevity());
            btnSelectPasswordLongevity.setText("Select Password Longevity\n\nCurrent Longevity: " + longevityDescription);
        }
    }

    private String getLongevityDescription(int longevity) {
        String description = "5 min";
        switch (longevity) {
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
                description = "5 min";
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
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("SettingsFragment", "onDestroy()");
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
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
                            userNames.add(user.getUserName());
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
                            mActiveUser = users.get(item);
                            selectActiveUser();
                            dialog.dismiss();
                        }
                    });
                    usersDialog = builder.create();
                    usersDialog.show();
                }
                break;

            case R.id.btnCreateNewUser:
                AlertDialog.Builder newUserDialog = new AlertDialog.Builder(getActivity());

                newUserDialog.setTitle("Enter New User Name");
                newUserDialog.setMessage("");

                // Set an EditText view to get user input
                final EditText input = new EditText(getActivity());
                input.setHint("New User Name");
                input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                newUserDialog.setView(input);

                newUserDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newUserName = input.getText().toString().trim();
                        if (IsUnique(newUserName)) {
                            int newUserID = MainActivity.getNextUserID();
                            mActiveUser = new clsUsers();
                            mActiveUser.setUserID(newUserID);
                            mActiveUser.setUserName(newUserName);
                            MySettings.setActiveUserID(newUserID);
                            MySettings.setActiveUserName(newUserName);
                            selectActiveUser();
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                            MyLog.e("SettingsFragment", "onClick OK: new user is not unique");
                            MainActivity.showOkDialog(getActivity(), "Failed to create new user",
                                    "The provide use name \"" + newUserName + "\" already exists!");
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

            case R.id.btnEditUserName:
                AlertDialog.Builder editUserDialog = new AlertDialog.Builder(getActivity());

                editUserDialog.setTitle("Edit User Name");
                editUserDialog.setMessage("");

                // Set an EditText view to get user input
                final EditText userNameInput = new EditText(getActivity());
                userNameInput.setHint("Edit User Name");
                userNameInput.setText(mActiveUser.getUserName());

                userNameInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                editUserDialog.setView(userNameInput);

                editUserDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newUserName = userNameInput.getText().toString().trim();
                        if (IsUnique(newUserName)) {
                            mActiveUser.setUserName(newUserName);
                            selectActiveUser();
                            MySettings.setActiveUserName(newUserName);
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                            MyLog.e("SettingsFragment", "onClick OK: new user name is not unique");
                            MainActivity.showOkDialog(getActivity(), "Failed to edit user name",
                                    "The provide use name \"" + newUserName + "\" already exists!");
                        }
                    }
                });

                editUserDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // Canceled.
                        dialog.dismiss();
                    }
                });

                editUserDialog.show();
                break;

            case R.id.btnSelectPasswordLongevity:
                // Strings to Show In Dialog with Radio Buttons
                final CharSequence[] items = {"5 min", "15 min", "30 min", "1 hr", "4 hrs", "8 hrs"};

                int longevity = MySettings.getPasswordLongevity();
                int selectedLongevityPosition = 0;
                switch (longevity) {
                    case 15:
                        selectedLongevityPosition = 1;
                        break;
                    case 30:
                        selectedLongevityPosition = 2;
                        break;
                    case 60:
                        selectedLongevityPosition = 3;
                        break;
                    case 240:
                        selectedLongevityPosition = 4;
                        break;
                    case 480:
                        selectedLongevityPosition = 5;
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
                                newLongevity = 5;
                                break;
                            case 1:
                                newLongevity = 15;
                                break;
                            case 2:
                                newLongevity = 30;
                                break;
                            case 3:
                                newLongevity = 60;
                                break;
                            case 4:
                                newLongevity = 240;
                                break;
                            case 5:
                                newLongevity = 480;
                                break;
                        }
                        String newLongevityDescription = getLongevityDescription(newLongevity);
                        MySettings.setPasswordLongevity(newLongevity);
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
        }

    }

    private boolean IsUnique(String newUserName) {
        boolean result = true;
        for (clsUsers user : mUsers) {
            if (user.getUserName().equalsIgnoreCase(newUserName)) {
                result = false;
                break;
            }
        }
        return result;
    }

    private void selectActiveUser() {
        MySettings.setActiveUserID(mActiveUser.getUserID());
        // TODO: 3/16/2015 use plurals
        String actionBarTitle = mActiveUser.getUserName() + "'s Passwords";
        MainActivity.setActionBarTitle(actionBarTitle);
        updateUI();
        EventBus.getDefault().post(new clsEvents.replaceFragment(-1, MySettings.FRAG_ITEMS_LIST, false));
    }
}