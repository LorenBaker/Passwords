package lbconsulting.com.passwords.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import de.greenrobot.event.EventBus;
import lbconsulting.com.passwords.R;
import lbconsulting.com.passwords.activities.MainActivity;
import lbconsulting.com.passwords.classes.MyLog;
import lbconsulting.com.passwords.classes.MySettings;
import lbconsulting.com.passwords.classes.clsEvents;
import lbconsulting.com.passwords.classes.clsPasswordItem;

/**
 * A fragment that allows the editing of a Credit Card
 */
public class EditWebsiteFragment extends Fragment implements TextWatcher {

    private static final String ARG_IS_NEW_PASSWORD_ITEM = "isNewPasswordItem";

    // fragment state variables
    private boolean mIsDirty = false;
    private boolean mTextChangedListenersEnabled = false;
    private boolean mIsItemNameDirty = false;

    private boolean mNameValidated = false;
    private String mOriginalItemName = "";
    private boolean mIsNewPasswordItem = false;

    private clsPasswordItem mPasswordItem;

    private EditText txtItemName;
    private EditText txtWebsiteURL;
    private EditText txtUserID;
    private EditText txtPassword;


    public static EditWebsiteFragment newInstance(boolean isNewPasswordItem) {
        EditWebsiteFragment fragment = new EditWebsiteFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_NEW_PASSWORD_ITEM, isNewPasswordItem);
        fragment.setArguments(args);
        return fragment;
    }

    public EditWebsiteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("EditWebsiteFragment", "onCreate()");
        if (getArguments() != null) {
            mIsNewPasswordItem = getArguments().getBoolean(ARG_IS_NEW_PASSWORD_ITEM);
            if (mIsNewPasswordItem) {
                mIsDirty = true;
            }
            mPasswordItem = MainActivity.getActivePasswordItem();
        }
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("EditWebsiteFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_edit_website, container, false);

        txtItemName = (EditText) rootView.findViewById(R.id.txtItemName);
        txtItemName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (mIsItemNameDirty && !mNameValidated) {
                        validateItemName();
                    }
                }
            }
        });
        txtItemName.addTextChangedListener(this);

        txtWebsiteURL = (EditText) rootView.findViewById(R.id.txtWebsiteURL);
        txtWebsiteURL.addTextChangedListener(this);

        txtUserID = (EditText) rootView.findViewById(R.id.txtUserID);
        txtUserID.addTextChangedListener(this);

        txtPassword = (EditText) rootView.findViewById(R.id.txtAppPassword);
        txtPassword.addTextChangedListener(this);
        return rootView;
    }

    private void validateItemName() {
        String itemName = txtItemName.getText().toString().trim();
        if (!itemName.equalsIgnoreCase(mOriginalItemName)) {
            if (itemName.isEmpty()) {
                MainActivity.showOkDialog(getActivity(),
                        "Invalid Item Name", "The item’s name cannot be empty!\n\nReverting back to the unedited name.");
                txtItemName.setText(mOriginalItemName);
            } else {
                // check if the name exists
                if (MainActivity.itemNameExist(itemName, mPasswordItem.getUser_ID())) {
                    MainActivity.showOkDialog(getActivity(),
                            "Invalid Item Name", "\"" + itemName + "\" already exists!\n\nReverting back to the unedited name.");
                    txtItemName.setText(mOriginalItemName);
                } else {
                    // the item name does not exist
                    mIsDirty = true;
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("EditWebsiteFragment", "onActivityCreated()");
        mTextChangedListenersEnabled = false;

        // Restore saved state
        if (savedInstanceState != null) {
            MyLog.i("EditWebsiteFragment", "onActivityCreated(): savedInstanceState");
            mIsDirty = savedInstanceState.getBoolean(MySettings.ARG_IS_DIRTY);
            mPasswordItem = MainActivity.getActivePasswordItem();
        }
        if (getActivity().getActionBar() != null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        }
        MySettings.setOnSaveInstanceState(false);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("EditWebsiteFragment", "onSaveInstanceState()");
        outState.putBoolean(MySettings.ARG_IS_DIRTY, mIsDirty);
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("EditWebsiteFragment", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_EDIT_GENERAL_ACCOUNT);
        updateUI();
    }

    public void onEvent(clsEvents.updateUI event) {
        MyLog.i("EditWebsiteFragment", "onEvent.updateUI()");
        mOriginalItemName = "";
        updateUI();
    }

    private void updateUI() {
        // inhibit text change event when loading updating the UI.
        mTextChangedListenersEnabled = false;

        // don't update if the user has made edits
        if (!mIsDirty) {
            mPasswordItem = MainActivity.getActivePasswordItem();
            if (mPasswordItem != null) {
                txtItemName.setText(mPasswordItem.getName());
                if (mOriginalItemName.isEmpty()) {
                    mOriginalItemName = mPasswordItem.getName();
                }
                txtWebsiteURL.setText((mPasswordItem.getWebsiteURL()));
                txtUserID.setText((mPasswordItem.getWebsiteUserID()));
                txtPassword.setText((mPasswordItem.getWebsitePassword()));
            }
        }
        mTextChangedListenersEnabled = true;
    }

    private void updatePasswordItem() {

        mPasswordItem.setName(txtItemName.getText().toString().trim());

        String websiteURL = txtWebsiteURL.getText().toString().trim();
        if (!websiteURL.startsWith("http://") && !websiteURL.startsWith("https://")) {
            websiteURL = "http://" + websiteURL;
        }
        mPasswordItem.setWebsiteURL(websiteURL);

        mPasswordItem.setWebsiteUserID(txtUserID.getText().toString().trim());
        mPasswordItem.setWebsitePassword(txtPassword.getText().toString().trim());

        // save changes
        EventBus.getDefault().post(new clsEvents.saveChangesToDropbox());

        mIsDirty = false;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_edit, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Do Fragment menu item stuff here
            case R.id.action_save:
                if (txtItemName.hasFocus()) {
                    validateItemName();
                    mNameValidated = true;
                }
                InputMethodManager imm = (InputMethodManager) getActivity()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtItemName.getWindowToken(), 0);

                EventBus.getDefault().post(new clsEvents.PopBackStack());
                return true;

            case R.id.action_cancel:
                //Toast.makeText(getActivity(), "TO COME: action_cancel", Toast.LENGTH_SHORT).show();
                mIsDirty = false;
                if (mIsNewPasswordItem) {
                    // delete the newly created password item
                    MainActivity.deletePasswordItem(mPasswordItem.getID());
                }
                EventBus.getDefault().post(new clsEvents.PopBackStack());
                return true;

            case R.id.action_clear:
                //Toast.makeText(getActivity(), "TO COME: action_clear", Toast.LENGTH_SHORT).show();
                txtWebsiteURL.setText("");
                txtUserID.setText("");
                txtPassword.setText("");
                mIsDirty = true;
                return true;


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
        mTextChangedListenersEnabled = false;
        super.onPause();
        MyLog.i("EditWebsiteFragment", "onPause()");
        if (mIsDirty) {
            updatePasswordItem();
        }
        if(getActivity().getActionBar()!=null) {
            getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("EditWebsiteFragment", "onDestroy()");
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mTextChangedListenersEnabled) {
            String editTextName = "";
            if (txtItemName.getText().hashCode() == s.hashCode()) {
                editTextName = "txtItemName";
                mIsItemNameDirty = true;
            } else if (txtWebsiteURL.getText().hashCode() == s.hashCode()) {
                editTextName = "txtWebsiteURL";
            } else if (txtUserID.getText().hashCode() == s.hashCode()) {
                editTextName = "txtUserID";
            } else if (txtPassword.getText().hashCode() == s.hashCode()) {
                editTextName = "txtPassword";
            }

            MyLog.d("EditWebsiteFragment", "onTextChanged: EditText = " + editTextName);
            mIsDirty = true;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
