package lbconsulting.com.passwords.fragments;


import android.app.AlertDialog;
import android.content.DialogInterface;
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

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import lbconsulting.com.passwords.R;
import lbconsulting.com.passwords.activities.MainActivity;
import lbconsulting.com.passwords.classes.MyLog;
import lbconsulting.com.passwords.classes.MySettings;
import lbconsulting.com.passwords.classes.clsEvents;
import lbconsulting.com.passwords.classes.clsPasswordItem;
import lbconsulting.com.passwords.classes.clsUsers;

public class UserSettingsFragment extends Fragment implements View.OnClickListener {

    // fragment state variables

    private ArrayList<clsUsers> mUsers;
    private clsUsers mActiveUser;

    private Button btnCreateNewUser;
    private Button btnEditUserName;
    private Button btnDeleteUser;


    public static UserSettingsFragment newInstance() {
        return new UserSettingsFragment();
    }

    public UserSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("UserSettingsFragment", "onCreate()");
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("UserSettingsFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_user_settings, container, false);

        btnCreateNewUser = (Button) rootView.findViewById(R.id.btnCreateNewUser);
        btnEditUserName = (Button) rootView.findViewById(R.id.btnEditUserName);
        btnDeleteUser = (Button) rootView.findViewById(R.id.btnDeleteUser);

        btnCreateNewUser.setOnClickListener(this);
        btnEditUserName.setOnClickListener(this);
        btnDeleteUser.setOnClickListener(this);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("UserSettingsFragment", "onActivityCreated()");
        if(getActivity().getActionBar()!=null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            MySettings.setOnSaveInstanceState(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("UserSettingsFragment", "onSaveInstanceState");
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("UserSettingsFragment", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_SETTINGS);
        updateUI();
    }

    private void updateUI() {
        if (MainActivity.getPasswordsData() != null) {
            mUsers = MainActivity.getPasswordsData().getUsers();
            if (mUsers != null) {
                mActiveUser = MySettings.getActiveUser();
                if (mActiveUser != null) {
                    btnEditUserName.setText(getString(R.string.btnEditUserName_text) + mActiveUser.getUserName());
                    btnDeleteUser.setText(getString(R.string.btnDeleteUser_text) + mActiveUser.getUserName());
                } else {
                    btnEditUserName.setText(getString(R.string.btnEditUserName_text) + getString(R.string.none_text));
                    btnDeleteUser.setText(getString(R.string.btnDeleteUser_text) + getString(R.string.none_text));
                }
            }
        }
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
        MyLog.i("UserSettingsFragment", "onPause()");
        if(getActivity().getActionBar()!=null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("UserSettingsFragment", "onDestroy()");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnCreateNewUser:
                AlertDialog.Builder newUserDialog = new AlertDialog.Builder(getActivity());

                newUserDialog.setTitle(getActivity().getString(R.string.enterNewUserName_dialog_title));
                newUserDialog.setMessage("");

                // Set an EditText view to get user input
                final EditText input = new EditText(getActivity());
                input.setHint(getActivity().getString(R.string.newUserName_editText_hint));
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
                            //MySettings.setActiveUserName(newUserName);
                            MainActivity.addNewUser(mActiveUser);
                            selectActiveUser();
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                            MyLog.e("UserSettingsFragment", "onClick OK: new user is not unique");
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

            case R.id.btnEditUserName:
                //btnEditUserName
                AlertDialog.Builder editUserDialog = new AlertDialog.Builder(getActivity());

                editUserDialog.setTitle(getActivity().getString(R.string.editUserName_dialog_title));
                editUserDialog.setMessage("");

                // Set an EditText view to get user input
                final EditText editUserNameInput = new EditText(getActivity());
                editUserNameInput.setHint(getActivity().getString(R.string.editUserName_editText_hint));
                editUserNameInput.setText(mActiveUser.getUserName());

                editUserNameInput.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                editUserDialog.setView(editUserNameInput);

                editUserDialog.setPositiveButton(
                        getActivity().getString(R.string.btnOK_text), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String newUserName = editUserNameInput.getText().toString().trim();
                                if (isUnique(newUserName)) {
                                    mActiveUser.setUserName(newUserName);
                                    selectActiveUser();
                                    dialog.dismiss();
                                } else {
                                    dialog.dismiss();
                                    MyLog.e("UserSettingsFragment", "onClick OK: new user name is not unique");
                                    MainActivity.showOkDialog(getActivity(), "Failed to edit user name",
                                            "The provide use name \"" + newUserName + "\" already exists!");
                                }
                            }
                        });

                editUserDialog.setNegativeButton(getActivity().getString(R.string.btnCancel_text),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                                dialog.dismiss();
                            }
                        }
                );

                editUserDialog.show();
                break;

            case R.id.btnDeleteUser:
                AlertDialog.Builder deleteUserDialog = new AlertDialog.Builder(getActivity());

                deleteUserDialog.setTitle(getActivity().getString(R.string.deleteUser_dialog_title));
                StringBuilder msg = new StringBuilder();
                msg.append(getActivity().getString(R.string.deleteUser_dialog_message))
                        .append(mActiveUser.getUserName())
                        .append("\"?");

                deleteUserDialog.setMessage(msg.toString());

                deleteUserDialog.setPositiveButton(getActivity().getString(R.string.btnYes_text),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteUserAndAllRelatedPasswordItems(mActiveUser.getUserID());
                                // set the Active user to the first user
                                if (MainActivity.getPasswordsData() != null
                                        && MainActivity.getPasswordsData().getUsers() != null) {
                                    ArrayList<clsUsers> users = MainActivity.getPasswordsData().getUsers();
                                    if (users.size() > 0) {
                                        MySettings.setActiveUserID(users.get(0).getUserID());
                                        mActiveUser = users.get(0);
                                    } else {
                                        String title = getActivity().getString(R.string.noUsersExist_dialog_title);
                                        String message = getActivity().getString(R.string.noUsersExist_dialog_message);
                                        MainActivity.showOkDialog(getActivity(), title, message);
                                    }
                                }
                            }
                        }
                );

                deleteUserDialog.setNegativeButton(getActivity().getString(R.string.btnNo_text),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                // Canceled.
                                dialog.dismiss();
                            }
                        }
                );

                deleteUserDialog.show();
                break;
        }

    }

    private void deleteUserAndAllRelatedPasswordItems(int userID) {
        // Delete all Passwords items associated with the active user
        ArrayList<Integer> itemIDsForDeletion = new ArrayList<>();
        // Find all items associated with the active user
        for (clsPasswordItem item : MainActivity.getPasswordsData().getPasswordItems()) {
            if (item.getUser_ID() == userID) {
                itemIDsForDeletion.add(item.getID());
            }
        }

        for (Integer ID : itemIDsForDeletion) {
            deletePasswordItem(ID);
        }

        // Delete the user
        deleteUser(userID);
    }

    private void deletePasswordItem(Integer itemID) {
        ArrayList<clsPasswordItem> passwordItems = MainActivity.getPasswordsData().getPasswordItems();
        if (passwordItems != null) {
            for (int i = passwordItems.size() - 1; i >= 0; i--) {
                if (passwordItems.get(i).getID() == itemID) {
                    passwordItems.remove(i);
                    break;
                }
            }
        }
    }

    private void deleteUser(int userID) {
        ArrayList<clsUsers> users = MainActivity.getPasswordsData().getUsers();
        if (users != null) {
            for (int i = users.size() - 1; i >= 0; i--) {
                if (users.get(i).getUserID() == userID) {
                    users.remove(i);
                    break;
                }
            }
        }
    }

    private boolean isUnique(String newUserName) {
        // TODO: Move to Main Activity ??
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
        updateUI();
            EventBus.getDefault().post(new clsEvents.saveChangesToDropbox());
        EventBus.getDefault().post(new clsEvents.replaceFragment(-1, MySettings.FRAG_ITEMS_LIST, false));
    }
}
