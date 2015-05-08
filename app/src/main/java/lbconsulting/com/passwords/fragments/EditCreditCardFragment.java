package lbconsulting.com.passwords.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputFilter;
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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

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
public class EditCreditCardFragment extends Fragment implements TextWatcher {

    private static final String ARG_IS_DIRTY = "isDirty";
    private static final String ARG_ACTIVE_CARD_TYPE = "activeCardType";
    private static final String ARG_CREDIT_CARD_NUMBER = "creditCardNumber";
    private static final String ARG_PASSWORD_ITEM_ID = "passwordItemID";
    private static final String ARG_IS_NEW_PASSWORD_ITEM = "isNewPasswordItem";

    // fragment state variables
    private boolean mIsDirty = false;
    private boolean mNameValidated = false;
    private String mOriginalItemName = "";
    private boolean mIsItemNameDirty = false;
    private int mActiveCardType = MySettings.VISA;
    private String mCreditCardNumber = "";
    private boolean mIsNewPasswordItem = false;
    private int mSelectedCreditCardTypePosition = Spinner.INVALID_POSITION;

    private clsPasswordItem mPasswordItem;
    private int maxLengthCardPart1 = 4;
    private int maxLengthCardPart2 = 4;
    private int maxLengthCardPart3 = 4;
    private int maxLengthCardPart4 = 4;

    private EditText txtItemName;
    private Spinner spnCreditCardType;
    private EditText txtCreditCardPart1;
    private EditText txtCreditCardPart2;
    private EditText txtCreditCardPart3;
    private EditText txtCreditCardPart4;
    private TextView tvSpacer3;
    private ImageView ivCardVerification;
    private EditText txtExpirationMonth;
    private EditText txtExpirationYear;
    private EditText txtSecurityCode;
    private EditText txtPrimaryPhoneNumber;
    private EditText txtAlternatePhoneNumber;

    private boolean mTextChangedListenersEnabled = false;


    private class CreditCardParts {
        private String part1 = "";
        private String part2 = "";
        private String part3 = "";
        private String part4 = "";

        public String getPart1() {
            return part1;
        }

        public String getPart2() {
            return part2;
        }

        public String getPart3() {
            return part3;
        }

        public String getPart4() {
            return part4;
        }

        public CreditCardParts(String creditCardNumber, int creditCardType) {
            if (creditCardNumber != null && !creditCardNumber.isEmpty()) {
                switch (creditCardType) {
                    case MySettings.AMERICAN_EXPRESS:
                        if (creditCardNumber.length() >= 4) {
                            part1 = creditCardNumber.substring(0, 4);
                        }

                        if (creditCardNumber.length() >= 10) {
                            part2 = creditCardNumber.substring(4, 10);
                        }

                        if (creditCardNumber.length() >= 15) {
                            part3 = creditCardNumber.substring(10, 15);
                        }
                        part4 = "";
                        break;

                    case MySettings.DINERS_CLUB:
                        if (creditCardNumber.length() >= 4) {
                            part1 = creditCardNumber.substring(0, 4);
                        }

                        if (creditCardNumber.length() >= 10) {
                            part2 = creditCardNumber.substring(4, 10);
                        }

                        if (creditCardNumber.length() >= 14) {
                            part3 = creditCardNumber.substring(10, 14);
                        }
                        part4 = "";
                        break;

                    default:
                        if (creditCardNumber.length() >= 4) {
                            part1 = creditCardNumber.substring(0, 4);
                        }

                        if (creditCardNumber.length() >= 8) {
                            part2 = creditCardNumber.substring(4, 8);
                        }

                        if (creditCardNumber.length() >= 12) {
                            part3 = creditCardNumber.substring(8, 12);
                            part4 = creditCardNumber.substring(12, creditCardNumber.length());
                        }

                        break;
                }
            }
        }
    }


    public static EditCreditCardFragment newInstance(boolean isNewPasswordItem) {
        EditCreditCardFragment fragment = new EditCreditCardFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_NEW_PASSWORD_ITEM, isNewPasswordItem);
        fragment.setArguments(args);
        return fragment;
    }

    public EditCreditCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("EditCreditCardFragment", "onCreate()");
        if (getArguments() != null) {
            mIsNewPasswordItem = getArguments().getBoolean(ARG_IS_NEW_PASSWORD_ITEM);
            if (mIsNewPasswordItem) {
                mIsDirty = true;
            }
            mPasswordItem = MainActivity.getActivePasswordItem();
            if (mPasswordItem != null) {
                mSelectedCreditCardTypePosition = findSpinnerPosition(mPasswordItem.getCreditCardAccountNumber());
            }
        }
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);


    }

    private int findSpinnerPosition(String creditCardAccountNumber) {
        clsFormattingMethods.creditCard card = clsFormattingMethods.getCreditCardType(creditCardAccountNumber);
        int position = Spinner.INVALID_POSITION;
        if (card.getCardType().equals(MySettings.CreditCardNames[0])) {
            position = 0; // American Express
        } else if (card.getCardType().equals(MySettings.CreditCardNames[1])) {
            position = 1; // Diners Club
        } else if (card.getCardType().equals(MySettings.CreditCardNames[2])) {
            position = 2; // Discover
        } else if (card.getCardType().equals(MySettings.CreditCardNames[3])) {
            position = 3; // JCB
        } else if (card.getCardType().equals(MySettings.CreditCardNames[4])) {
            position = 4; // MasterCard
        } else if (card.getCardType().equals(MySettings.CreditCardNames[5])) {
            position = 5; // VISA
        }
        return position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("EditCreditCardFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_edit_credit_card, container, false);

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

        spnCreditCardType = (Spinner) rootView.findViewById(R.id.spnCreditCardType);
        final ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, MySettings.CreditCardNames);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCreditCardType.setAdapter(dataAdapter);
        spnCreditCardType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mSelectedCreditCardTypePosition = position;
                InputFilter length4Filter = new InputFilter.LengthFilter(4);
                InputFilter length5Filter = new InputFilter.LengthFilter(5);
                InputFilter length6Filter = new InputFilter.LengthFilter(6);
                switch (position) {
                    case MySettings.AMERICAN_EXPRESS:
                        mActiveCardType = MySettings.AMERICAN_EXPRESS;
                        maxLengthCardPart1 = 4;
                        maxLengthCardPart2 = 6;
                        maxLengthCardPart3 = 5;
                        txtCreditCardPart1.setFilters(new InputFilter[]{length4Filter});
                        txtCreditCardPart2.setFilters(new InputFilter[]{length6Filter});
                        txtCreditCardPart3.setFilters(new InputFilter[]{length5Filter});
                        txtCreditCardPart3.setNextFocusDownId(R.id.txtExpirationMonth);
                        txtCreditCardPart4.setVisibility(View.GONE);
                        tvSpacer3.setVisibility(View.GONE);
                        break;
                    case MySettings.DINERS_CLUB:
                        mActiveCardType = MySettings.DINERS_CLUB;
                        maxLengthCardPart1 = 4;
                        maxLengthCardPart2 = 6;
                        maxLengthCardPart3 = 4;
                        txtCreditCardPart1.setFilters(new InputFilter[]{length4Filter});
                        txtCreditCardPart2.setFilters(new InputFilter[]{length6Filter});
                        txtCreditCardPart3.setFilters(new InputFilter[]{length4Filter});
                        txtCreditCardPart3.setNextFocusDownId(R.id.txtExpirationMonth);
                        txtCreditCardPart4.setVisibility(View.GONE);
                        tvSpacer3.setVisibility(View.GONE);
                        break;
                    default:
                        maxLengthCardPart1 = 4;
                        maxLengthCardPart2 = 4;
                        maxLengthCardPart3 = 4;
                        mActiveCardType = MySettings.VISA;
                        txtCreditCardPart1.setFilters(new InputFilter[]{length4Filter});
                        txtCreditCardPart2.setFilters(new InputFilter[]{length4Filter});
                        txtCreditCardPart3.setFilters(new InputFilter[]{length4Filter});
                        txtCreditCardPart3.setNextFocusDownId(R.id.txtCreditCardPart4);
                        txtCreditCardPart4.setFilters(new InputFilter[]{length4Filter});
                        txtCreditCardPart4.setVisibility(View.VISIBLE);
                        tvSpacer3.setVisibility(View.VISIBLE);
                        break;
                }
                mTextChangedListenersEnabled = false;
                updateCreditCardUI();
                mTextChangedListenersEnabled = true;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        txtCreditCardPart1 = (EditText) rootView.findViewById(R.id.txtCreditCardPart1);
        txtCreditCardPart1.addTextChangedListener(this);

        txtCreditCardPart2 = (EditText) rootView.findViewById(R.id.txtCreditCardPart2);
        txtCreditCardPart2.addTextChangedListener(this);

        txtCreditCardPart3 = (EditText) rootView.findViewById(R.id.txtCreditCardPart3);
        txtCreditCardPart3.addTextChangedListener(this);

        txtCreditCardPart4 = (EditText) rootView.findViewById(R.id.txtCreditCardPart4);
        txtCreditCardPart4.addTextChangedListener(this);

        tvSpacer3 = (TextView) rootView.findViewById(R.id.tvSpacer3);
        ivCardVerification = (ImageView) rootView.findViewById(R.id.ivCardVerification);
        txtExpirationMonth = (EditText) rootView.findViewById(R.id.txtExpirationMonth);
        txtExpirationMonth.addTextChangedListener(this);

        txtExpirationYear = (EditText) rootView.findViewById(R.id.txtExpirationYear);
        txtExpirationYear.addTextChangedListener(this);

        txtSecurityCode = (EditText) rootView.findViewById(R.id.txtSecurityCode);
        txtSecurityCode.addTextChangedListener(this);

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
        txtPrimaryPhoneNumber.addTextChangedListener(this);

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
        txtAlternatePhoneNumber.addTextChangedListener(this);

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

    private void validateCreditCard() {
        mCreditCardNumber = makeCreditCardNumber();
        clsFormattingMethods.creditCard card = clsFormattingMethods.getCreditCardType(mCreditCardNumber);
        boolean creditCardTypeResult = !card.getCardType().equals(MySettings.UNKNOWN);
        if (creditCardTypeResult) {
            boolean luhnTestResult = clsFormattingMethods.luhnTest(mCreditCardNumber);
            if (luhnTestResult) {
                if (mSelectedCreditCardTypePosition == card.getCardPosition()) {
                    ivCardVerification.setImageResource(R.drawable.btn_check_buttonless_on);
                    return;
                }
            }
        }
        ivCardVerification.setImageResource(R.drawable.btn_check_buttonless_off);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("EditCreditCardFragment", "onActivityCreated()");
        mTextChangedListenersEnabled = false;
        // Restore saved state
        if (savedInstanceState != null) {
            MyLog.i("EditCreditCardFragment", "onActivityCreated(): savedInstanceState");
            mIsDirty = savedInstanceState.getBoolean(ARG_IS_DIRTY);
            mActiveCardType = savedInstanceState.getInt(ARG_ACTIVE_CARD_TYPE);
            mCreditCardNumber = savedInstanceState.getString(ARG_CREDIT_CARD_NUMBER);
            mPasswordItem = MainActivity.getActivePasswordItem();
            mSelectedCreditCardTypePosition = findSpinnerPosition(mPasswordItem.getCreditCardAccountNumber());
        }
        spnCreditCardType.setSelection(mSelectedCreditCardTypePosition);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        MySettings.setOnSaveInstanceState(false);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("EditCreditCardFragment", "onSaveInstanceState()");

        outState.putBoolean(ARG_IS_DIRTY, mIsDirty);
        outState.putInt(ARG_ACTIVE_CARD_TYPE, mActiveCardType);
        outState.putString(ARG_CREDIT_CARD_NUMBER, mCreditCardNumber);
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("EditCreditCardFragment", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_EDIT_CREDIT_CARD);
        updateUI();
    }

    public void onEvent(clsEvents.updateUI event) {
        MyLog.i("EditCreditCardFragment", "onEvent.updateUI()");
        mOriginalItemName = "";
        updateUI();
    }

    private void updateUI() {
        if (!mIsDirty) {
            mTextChangedListenersEnabled = false;
            mPasswordItem = MainActivity.getActivePasswordItem();
            if (mPasswordItem != null) {
                txtItemName.setText(mPasswordItem.getName());
                if (mOriginalItemName.isEmpty()) {
                    mOriginalItemName = mPasswordItem.getName();
                }

                updateCreditCardUI();

                txtExpirationMonth.setText(mPasswordItem.getCreditCardExpirationMonth());
                txtExpirationYear.setText(mPasswordItem.getCreditCardExpirationYear());
                txtSecurityCode.setText(mPasswordItem.getCardCreditSecurityCode());

                String formattedPrimaryPhoneNumber = clsFormattingMethods.formatPhoneNumber(mPasswordItem.getPrimaryPhoneNumber());
                String formattedAlternatePhoneNumber = clsFormattingMethods.formatPhoneNumber(mPasswordItem.getAlternatePhoneNumber());
                txtPrimaryPhoneNumber.setText(formattedPrimaryPhoneNumber);
                txtAlternatePhoneNumber.setText(formattedAlternatePhoneNumber);
            }
            mTextChangedListenersEnabled = true;
        }
    }

    private void updateCreditCardUI() {
        if (mPasswordItem != null) {

            CreditCardParts creditCardParts = new CreditCardParts(mPasswordItem.getCreditCardAccountNumber(), mSelectedCreditCardTypePosition);
            txtCreditCardPart1.setText(creditCardParts.getPart1());
            txtCreditCardPart2.setText(creditCardParts.getPart2());
            txtCreditCardPart3.setText(creditCardParts.getPart3());
            txtCreditCardPart4.setText(creditCardParts.getPart4());

            validateCreditCard();
        }
    }

    private void updatePasswordItem() {

        mPasswordItem.setName(txtItemName.getText().toString().trim());
        mCreditCardNumber = makeCreditCardNumber();
        mPasswordItem.setCreditCardAccountNumber(mCreditCardNumber);
        mPasswordItem.setCreditCardExpirationMonth(txtExpirationMonth.getText().toString());
        mPasswordItem.setCreditCardExpirationYear(txtExpirationYear.getText().toString());
        mPasswordItem.setCreditCardSecurityCode(txtSecurityCode.getText().toString());
        String unformattedPrimaryPhoneNumber = clsFormattingMethods.unFormatPhoneNumber(txtPrimaryPhoneNumber.getText().toString());
        String unformattedAlternatePhoneNumber = clsFormattingMethods.unFormatPhoneNumber(txtAlternatePhoneNumber.getText().toString());
        mPasswordItem.setPrimaryPhoneNumber(unformattedPrimaryPhoneNumber);
        mPasswordItem.setAlternatePhoneNumber(unformattedAlternatePhoneNumber);

        // save the changes
        EventBus.getDefault().post(new clsEvents.saveChangesToDropbox());
        mIsDirty = false;
    }

    private String makeCreditCardNumber() {
        StringBuilder sb = new StringBuilder();
        sb.append(txtCreditCardPart1.getText().toString())
                .append(txtCreditCardPart2.getText().toString())
                .append(txtCreditCardPart3.getText().toString());
        switch (mActiveCardType) {
            case MySettings.AMERICAN_EXPRESS:
            case MySettings.DINERS_CLUB:
                break;

            default:
                sb.append(txtCreditCardPart4.getText().toString());
                break;
        }
        return sb.toString();
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
                txtCreditCardPart1.setText("");
                txtCreditCardPart2.setText("");
                txtCreditCardPart3.setText("");
                txtCreditCardPart4.setText("");

                txtExpirationMonth.setText("");
                txtExpirationYear.setText("");
                txtSecurityCode.setText("");

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
        mTextChangedListenersEnabled = false;
        super.onPause();
        MyLog.i("EditCreditCardFragment", "onPause()");
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
        MyLog.i("EditCreditCardFragment", "onDestroy()");
        EventBus.getDefault().unregister(this);


    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //MyLog.d("EditCreditCardFragment", "beforeTextChanged");
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (mTextChangedListenersEnabled) {
            String editTextName = "";
            if (txtItemName.getText().hashCode() == s.hashCode()) {
                editTextName = "txtItemName";
            } else if (txtCreditCardPart1.getText().hashCode() == s.hashCode()) {
                editTextName = "txtCreditCardPart1";
            } else if (txtCreditCardPart2.getText().hashCode() == s.hashCode()) {
                editTextName = "txtCreditCardPart2";
            } else if (txtCreditCardPart3.getText().hashCode() == s.hashCode()) {
                editTextName = "txtCreditCardPart3";
            } else if (txtCreditCardPart4.getText().hashCode() == s.hashCode()) {
                editTextName = "txtCreditCardPart4";
            } else if (txtExpirationMonth.getText().hashCode() == s.hashCode()) {
                editTextName = "txtExpirationMonth";
            } else if (txtExpirationYear.getText().hashCode() == s.hashCode()) {
                editTextName = "txtExpirationYear";
            } else if (txtSecurityCode.getText().hashCode() == s.hashCode()) {
                editTextName = "txtSecurityCode";
            } else if (txtPrimaryPhoneNumber.getText().hashCode() == s.hashCode()) {
                editTextName = "txtPrimaryPhoneNumber";
            } else if (txtAlternatePhoneNumber.getText().hashCode() == s.hashCode()) {
                editTextName = "txtAlternatePhoneNumber";
            }

            MyLog.d("EditCreditCardFragment", "onTextChanged: EditText = " + editTextName);
            mIsDirty = true;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        //MyLog.d("EditCreditCardFragment", "afterTextChanged");
    }
}
