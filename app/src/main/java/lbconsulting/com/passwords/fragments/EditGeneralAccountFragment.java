package lbconsulting.com.passwords.fragments;


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
import lbconsulting.com.passwords.classes.clsFormattingMethods;
import lbconsulting.com.passwords.classes.clsPasswordItem;

/**
 * A fragment that allows the editing of a Credit Card
 */
public class EditGeneralAccountFragment extends Fragment {

    private static final String ARG_IS_DIRTY = "isDirty";
    private static final String ARG_ACCOUNT_NUMBER = "accountNumber";
    private static final String ARG_IS_NEW_PASSWORD_ITEM = "isNewPasswordItem";

    // fragment state variables
    private boolean mIsDirty = false;
    private boolean mNameValidated = false;
    private String mOriginalItemName = "";
    private boolean mIsItemNameDirty = false;
    private String mAccountNumber = "";
    private boolean mIsNewPasswordItem = false;

    private clsPasswordItem mPasswordItem;

    private EditText txtItemName;
    private EditText txtAccountNumber;
    private EditText txtPrimaryPhoneNumber;
    private EditText txtAlternatePhoneNumber;


    public static EditGeneralAccountFragment newInstance(boolean isNewPasswordItem) {
        EditGeneralAccountFragment fragment = new EditGeneralAccountFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_NEW_PASSWORD_ITEM, isNewPasswordItem);
        fragment.setArguments(args);
        return fragment;
    }

    public EditGeneralAccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("EditGeneralAccountFragment", "onCreate()");
        if (getArguments() != null) {
            mIsNewPasswordItem = getArguments().getBoolean(ARG_IS_NEW_PASSWORD_ITEM);
            mPasswordItem = MainActivity.getActivePasswordItem();
        }
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("EditGeneralAccountFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_edit_general_account, container, false);

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

        txtItemName.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsItemNameDirty = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtAccountNumber = (EditText) rootView.findViewById(R.id.txtKeyCode);
        txtAccountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsDirty = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        txtPrimaryPhoneNumber = (EditText) rootView.findViewById(R.id.txtPrimaryPhoneNumber);
        txtPrimaryPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String formattedPrimaryPhoneNumber = clsFormattingMethods
                            .formatPhoneNumber(txtPrimaryPhoneNumber.getText().toString().trim());
                    txtPrimaryPhoneNumber.setText(formattedPrimaryPhoneNumber);
                }
            }
        });
        txtPrimaryPhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsDirty = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        txtAlternatePhoneNumber = (EditText) rootView.findViewById(R.id.txtAlternatePhoneNumber);
        txtAlternatePhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String formattedAlternatePhoneNumber = clsFormattingMethods
                            .formatPhoneNumber(txtAlternatePhoneNumber.getText().toString().trim());
                    txtPrimaryPhoneNumber.setText(formattedAlternatePhoneNumber);
                }
            }
        });
        txtAlternatePhoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsDirty = true;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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
                    //MainActivity.sortPasswordsData();
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("EditGeneralAccountFragment", "onActivityCreated()");
        // Restore saved state
        if (savedInstanceState != null) {
            MyLog.i("EditGeneralAccountFragment", "onActivityCreated(): savedInstanceState");
            mIsDirty = savedInstanceState.getBoolean(ARG_IS_DIRTY);
            mAccountNumber = savedInstanceState.getString(ARG_ACCOUNT_NUMBER);
            mPasswordItem = MainActivity.getActivePasswordItem();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("EditGeneralAccountFragment", "onSaveInstanceState()");

        outState.putBoolean(ARG_IS_DIRTY, mIsDirty);
        outState.putString(ARG_ACCOUNT_NUMBER, mAccountNumber);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("EditGeneralAccountFragment", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_EDIT_GENERAL_ACCOUNT);
        updateUI();
    }

    public void onEvent(clsEvents.updateUI event) {
        MyLog.i("EditGeneralAccountFragment", "onEvent.updateUI()");
        mOriginalItemName = "";
        updateUI();
    }

    private void updateUI() {
        mPasswordItem = MainActivity.getActivePasswordItem();
        if (mPasswordItem != null) {
            txtItemName.setText(mPasswordItem.getName());
            if (mOriginalItemName.isEmpty()) {
                mOriginalItemName = mPasswordItem.getName();
            }

            txtAccountNumber.setText((mPasswordItem.getGeneralAccountNumber()));

            String formattedPrimaryPhoneNumber = clsFormattingMethods.formatPhoneNumber(mPasswordItem.getPrimaryPhoneNumber());
            String formattedAlternatePhoneNumber = clsFormattingMethods.formatPhoneNumber(mPasswordItem.getAlternatePhoneNumber());
            txtPrimaryPhoneNumber.setText(formattedPrimaryPhoneNumber);
            txtAlternatePhoneNumber.setText(formattedAlternatePhoneNumber);
            mIsDirty=false;
        }
    }

    private void updatePasswordItem() {

        mPasswordItem.setName(txtItemName.getText().toString().trim());
        mPasswordItem.setGeneralAccountNumber(txtAccountNumber.getText().toString().trim());

        String unformattedPrimaryPhoneNumber = clsFormattingMethods
                .unFormatPhoneNumber(txtPrimaryPhoneNumber.getText().toString());
        String unformattedAlternatePhoneNumber = clsFormattingMethods
                .unFormatPhoneNumber(txtAlternatePhoneNumber.getText().toString());
        mPasswordItem.setPrimaryPhoneNumber(unformattedPrimaryPhoneNumber);
        mPasswordItem.setAlternatePhoneNumber(unformattedAlternatePhoneNumber);
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
                        .getSystemService(getActivity().INPUT_METHOD_SERVICE);
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
                txtAccountNumber.setText("");
                txtPrimaryPhoneNumber.setText("");
                txtAlternatePhoneNumber.setText("");
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
        super.onPause();
        MyLog.i("EditGeneralAccountFragment", "onPause()");
        if (mIsDirty) {
            EventBus.getDefault().post(new clsEvents.isDirty());
            updatePasswordItem();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("EditGeneralAccountFragment", "onDestroy()");
        EventBus.getDefault().unregister(this);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);

    }


}
