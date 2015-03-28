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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

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
public class EditSoftwareFragment extends Fragment {

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
    private boolean mFlagInhibitTextChange = true;

    private EditText txtItemName;
    private EditText txtKeyCode;
    private Spinner spnSpacing;
    private final int mFirstSubgroupLength = 2;
    private int mSubgroupLength = mFirstSubgroupLength;


    public static EditSoftwareFragment newInstance(boolean isNewPasswordItem) {
        EditSoftwareFragment fragment = new EditSoftwareFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_NEW_PASSWORD_ITEM, isNewPasswordItem);
        fragment.setArguments(args);
        return fragment;
    }

    public EditSoftwareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("EditSoftwareFragment", "onCreate()");
        if (getArguments() != null) {
            mIsNewPasswordItem = getArguments().getBoolean(ARG_IS_NEW_PASSWORD_ITEM);
            mPasswordItem = MainActivity.getActivePasswordItem();
        }
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("EditSoftwareFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_edit_software, container, false);

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

        txtKeyCode = (EditText) rootView.findViewById(R.id.txtKeyCode);
        txtKeyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (mFlagInhibitTextChange) {
                    return;
                }

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mIsDirty = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!mFlagInhibitTextChange) {
                    String formattedKeyCode = clsFormattingMethods
                            .formatTypicalAccountNumber(txtKeyCode.getText().toString(), mSubgroupLength);

                    if (!txtKeyCode.getText().toString().equals(formattedKeyCode)) {
                        txtKeyCode.setText(formattedKeyCode);
                    }

                }
            }
        });

        spnSpacing = (Spinner) rootView.findViewById(R.id.spnSpacing);
        String[] spacing = {"2", "3", "4", "5", "6", "7", "8",};
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, spacing);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSpacing.setAdapter(dataAdapter);
        spnSpacing.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSubgroupLength = position + mFirstSubgroupLength;
                mFlagInhibitTextChange = true;
                formatKeyCode();
                mFlagInhibitTextChange = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return rootView;
    }

    private void formatKeyCode() {
        String formattedKeyCode = clsFormattingMethods.formatTypicalAccountNumber(txtKeyCode.getText().toString().trim(), mSubgroupLength);
        txtKeyCode.setText(formattedKeyCode);
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
        MyLog.i("EditSoftwareFragment", "onActivityCreated()");
        // Restore saved state
        if (savedInstanceState != null) {
            MyLog.i("EditSoftwareFragment", "onActivityCreated(): savedInstanceState");
            mIsDirty = savedInstanceState.getBoolean(ARG_IS_DIRTY);
            mAccountNumber = savedInstanceState.getString(ARG_ACCOUNT_NUMBER);
            mPasswordItem = MainActivity.getActivePasswordItem();
        }
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("EditSoftwareFragment", "onSaveInstanceState()");

        outState.putBoolean(ARG_IS_DIRTY, mIsDirty);
        outState.putString(ARG_ACCOUNT_NUMBER, mAccountNumber);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("EditSoftwareFragment", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_EDIT_GENERAL_ACCOUNT);
        updateUI();
    }

    public void onEvent(clsEvents.updateUI event) {
        MyLog.i("EditSoftwareFragment", "onEvent.updateUI()");
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

            mSubgroupLength = mPasswordItem.getSoftwareSubgroupLength();
            int position = mSubgroupLength - mFirstSubgroupLength;
            if (position < 0) {
                position = 0;
            }

            String formattedKeyCode = clsFormattingMethods.formatTypicalAccountNumber(mPasswordItem.getSoftwareKeyCode(), mSubgroupLength);
            txtKeyCode.setText(formattedKeyCode);
            spnSpacing.setSelection(position);
            mIsDirty = false;
        }
    }

    private void updatePasswordItem() {

        mPasswordItem.setName(txtItemName.getText().toString().trim());
        String unformattedKeyCode = clsFormattingMethods.unformatKeyCode(txtKeyCode.getText().toString().trim());
        mPasswordItem.setSoftwareKeyCode(unformattedKeyCode);
        mPasswordItem.setSoftwareSubgroupLength(mSubgroupLength);
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
                txtKeyCode.setText("");
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
        MyLog.i("EditSoftwareFragment", "onPause()");
        if (mIsDirty) {
            EventBus.getDefault().post(new clsEvents.isDirty());
            updatePasswordItem();
        }
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("EditSoftwareFragment", "onDestroy()");
        EventBus.getDefault().unregister(this);
    }


}
