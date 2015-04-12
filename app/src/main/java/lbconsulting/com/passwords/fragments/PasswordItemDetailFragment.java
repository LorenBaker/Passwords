package lbconsulting.com.passwords.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import de.greenrobot.event.EventBus;
import lbconsulting.com.passwords.R;
import lbconsulting.com.passwords.activities.MainActivity;
import lbconsulting.com.passwords.classes.MyLog;
import lbconsulting.com.passwords.classes.MySettings;
import lbconsulting.com.passwords.classes.clsEvents;
import lbconsulting.com.passwords.classes.clsItemTypes;
import lbconsulting.com.passwords.classes.clsPasswordItem;

/**
 * A fragment that shows a single PasswordItem detail screen.
 */
public class PasswordItemDetailFragment extends Fragment implements View.OnClickListener {

    private clsPasswordItem mPasswordItem;
    private boolean mIsDirty = false;

    private Button btnCallAlternate;
    private Button btnCallPrimary;
    private Button btnCopyAccountNumber;
    private Button btnCopyPassword;
    private Button btnGoToWebsite;
    private EditText txtComments;
    private ImageButton btnEditItem;
    private ImageButton btnEditWebsite;
    private TextView tvItemDetail;
    private TextView tvPasswordItemName;
    private TextView tvWebsiteDetail;

    private TextWatcher mCommentsTextWatcher;


    public static PasswordItemDetailFragment newInstance() {
        PasswordItemDetailFragment fragment = new PasswordItemDetailFragment();
/*        Bundle args = new Bundle();
        args.putInt(MySettings.ARG_ITEM_ID, itemID);
        fragment.setArguments(args);*/
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PasswordItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("PasswordItemDetailFragment", "onCreate()");
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            MyLog.i("PasswordItemDetailFragment", "onCreateView()");
            View rootView = inflater.inflate(R.layout.frag_password_item_detail, container, false);

            btnCallAlternate = (Button) rootView.findViewById(R.id.btnCallAlternate);
            btnCallPrimary = (Button) rootView.findViewById(R.id.btnCallPrimary);
            btnCopyAccountNumber = (Button) rootView.findViewById(R.id.btnCopyAccountNumber);
            btnCopyPassword = (Button) rootView.findViewById(R.id.btnCopyPassword);
            btnGoToWebsite = (Button) rootView.findViewById(R.id.btnGoToWebsite);
            btnEditItem = (ImageButton) rootView.findViewById(R.id.btnEditItem);
            btnEditWebsite = (ImageButton) rootView.findViewById(R.id.btnEditWebsite);

            btnCallAlternate.setOnClickListener(this);
            btnCallPrimary.setOnClickListener(this);
            btnCopyAccountNumber.setOnClickListener(this);
            btnCopyPassword.setOnClickListener(this);
            btnGoToWebsite.setOnClickListener(this);
            btnEditItem.setOnClickListener(this);
            btnEditWebsite.setOnClickListener(this);

            tvPasswordItemName = (TextView) rootView.findViewById(R.id.tvPasswordItemName);
            tvItemDetail = (TextView) rootView.findViewById(R.id.tvItemDetail);
            tvWebsiteDetail = (TextView) rootView.findViewById(R.id.tvWebsiteDetail);
            txtComments = (EditText) rootView.findViewById(R.id.txtComments);
            mCommentsTextWatcher = new TextWatcher() {
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
            };

            updateUI();
            return rootView;

    }

    private void updateUI() {
        mPasswordItem = MainActivity.getActivePasswordItem();

        if (mPasswordItem != null) {

            // inhibit text change event when loading updating the UI.
            txtComments.removeTextChangedListener(mCommentsTextWatcher);

            // fill the UI views
            tvPasswordItemName.setText(mPasswordItem.getName());
            tvItemDetail.setText(mPasswordItem.getItemDetail());
            tvWebsiteDetail.setText(mPasswordItem.getWebsiteDetail());
            // don't change comments if the user has made edits
            if (!mIsDirty) {
                txtComments.setText(mPasswordItem.getComments());
            }

            btnGoToWebsite.setEnabled(true);
            if (mPasswordItem.getWebsiteURL() == null) {
                btnGoToWebsite.setEnabled(false);
            }

            btnCopyPassword.setEnabled(true);
            if (mPasswordItem.getWebsitePassword() == null) {
                btnCopyPassword.setEnabled(false);
            }

            btnCallAlternate.setEnabled(true);
            if (mPasswordItem.getAlternatePhoneNumber() == null) {
                btnCallAlternate.setEnabled(false);
            }

            btnCallPrimary.setEnabled(true);
            if (mPasswordItem.getPrimaryPhoneNumber() == null) {
                btnCallPrimary.setEnabled(false);
            }

            btnCopyAccountNumber.setEnabled(true);
            switch (mPasswordItem.getItemType_ID()) {
                case clsItemTypes.CREDIT_CARDS:
                    if (mPasswordItem.getCreditCardAccountNumber() == null) {
                        btnCopyAccountNumber.setEnabled(false);
                    }
                    break;

                case clsItemTypes.GENERAL_ACCOUNTS:
                    if (mPasswordItem.getGeneralAccountNumber() == null) {
                        btnCopyAccountNumber.setEnabled(false);
                    }
                    break;

                case clsItemTypes.SOFTWARE:
                    if (mPasswordItem.getSoftwareKeyCode() == null) {
                        btnCopyAccountNumber.setEnabled(false);
                    }
                    break;
            }

            if (mPasswordItem.getItemType_ID() == clsItemTypes.WEBSITES) {
                tvItemDetail.setVisibility(View.GONE);
                btnEditItem.setVisibility(View.GONE);
                btnCopyAccountNumber.setVisibility(View.GONE);
                btnCallAlternate.setVisibility(View.GONE);
                btnCallPrimary.setVisibility(View.GONE);
            } else {
                tvItemDetail.setVisibility(View.VISIBLE);
                btnEditItem.setVisibility(View.VISIBLE);
                btnCopyAccountNumber.setVisibility(View.VISIBLE);
                btnCallAlternate.setVisibility(View.VISIBLE);
                btnCallPrimary.setVisibility(View.VISIBLE);
            }

            //  mIsDirty = false;

            txtComments.addTextChangedListener(mCommentsTextWatcher);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("PasswordItemDetailFragment", "onActivityCreated()");
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        MySettings.setOnSaveInstanceState(false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("PasswordItemDetailFragment", "onSaveInstanceState()");
        MySettings.setOnSaveInstanceState(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("PasswordItemDetailFragment", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_ITEM_DETAIL);
    }

    public void onEvent(clsEvents.updateUI event) {
        MyLog.i("PasswordItemDetailFragment", "onEvent.updateUI()");
        updateUI();
    }


    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("PasswordItemDetailFragment", "onPause()");
        if (mIsDirty && mPasswordItem != null && txtComments != null) {
            mPasswordItem.setComments(txtComments.getText().toString().trim());
            // save comment changes
            EventBus.getDefault().post(new clsEvents.saveChangesToDropbox());
        }
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("PasswordItemDetailFragment", "onDestroy()");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_password_item_detail_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Do Fragment menu item stuff here
            case R.id.action_discard:
                //Toast.makeText(getActivity(), "TO COME: action_discard", Toast.LENGTH_SHORT).show();
                MainActivity.deletePasswordItem(MySettings.getActivePasswordItemID());
                EventBus.getDefault().post(new clsEvents.PopBackStack());
                return true;

            case R.id.action_new:
                //Toast.makeText(getActivity(), "TO COME: action_new", Toast.LENGTH_SHORT).show();
                clsPasswordItem newPasswordItem = MainActivity.createNewPasswordItem();
                switch (mPasswordItem.getItemType_ID()) {
                    case clsItemTypes.CREDIT_CARDS:
                        newPasswordItem.setItemType_ID(clsItemTypes.CREDIT_CARDS);
                        EventBus.getDefault().post(new clsEvents.replaceFragment(newPasswordItem.getID(),
                                MySettings.FRAG_EDIT_CREDIT_CARD, true));
                        break;

                    case clsItemTypes.GENERAL_ACCOUNTS:
                        newPasswordItem.setItemType_ID(clsItemTypes.GENERAL_ACCOUNTS);
                        EventBus.getDefault().post(new clsEvents.replaceFragment(newPasswordItem.getID(),
                                MySettings.FRAG_EDIT_GENERAL_ACCOUNT, true));
                        break;

                    case clsItemTypes.SOFTWARE:
                        newPasswordItem.setItemType_ID(clsItemTypes.SOFTWARE);
                        EventBus.getDefault().post(new clsEvents.replaceFragment(newPasswordItem.getID(),
                                MySettings.FRAG_EDIT_SOFTWARE, true));
                        break;

                    case clsItemTypes.WEBSITES:
                        newPasswordItem.setItemType_ID(clsItemTypes.WEBSITES);
                        EventBus.getDefault().post(new clsEvents.replaceFragment(newPasswordItem.getID(),
                                MySettings.FRAG_EDIT_WEBSITE, true));
                        break;
                }
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
    public void onClick(View v) {
        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        String label = "";
        String textForClip = "";
        switch (v.getId()) {
            case R.id.btnCallAlternate:
                String alternatePhoneNumber = mPasswordItem.getAlternatePhoneNumber();
                if(!alternatePhoneNumber.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + alternatePhoneNumber));
                    getActivity().startActivity(intent);
                }
                //Toast.makeText(getActivity(), "TO COME: btnCallAlternate", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnCallPrimary:
                String primaryPhoneNumber = mPasswordItem.getPrimaryPhoneNumber();
                if(!primaryPhoneNumber.isEmpty()) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + primaryPhoneNumber));
                    getActivity().startActivity(intent);
                }
               // Toast.makeText(getActivity(), "TO COME: btnCallPrimary", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnCopyAccountNumber:
                switch (mPasswordItem.getItemType_ID()) {
                    case clsItemTypes.CREDIT_CARDS:
                        label = "Credit Card Number";
                        textForClip = mPasswordItem.getCreditCardAccountNumber();
                        break;

                    case clsItemTypes.GENERAL_ACCOUNTS:
                        label = "Account Number";
                        textForClip = mPasswordItem.getGeneralAccountNumber();
                        break;

                    case clsItemTypes.SOFTWARE:
                        label = "Software Key Code";
                        textForClip = mPasswordItem.getSoftwareKeyCode();
                        break;
                }

                ClipData clip = ClipData.newPlainText(label, textForClip);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), label + ": " + textForClip + " copied.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnCopyPassword:
                label = "Website Password";
                textForClip = mPasswordItem.getWebsitePassword();
                clip = ClipData.newPlainText(label, textForClip);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), label + ": " + textForClip + " copied.", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnGoToWebsite:
                // copy the website password to the clipboard
                label = "Website Password";
                textForClip = mPasswordItem.getWebsitePassword();
                clip = ClipData.newPlainText(label, textForClip);
                clipboard.setPrimaryClip(clip);

                // open the website
                String websiteURL = mPasswordItem.getWebsiteURL();
                if (!websiteURL.startsWith("http://") && !websiteURL.startsWith("https://")) {
                    websiteURL = "http://" + websiteURL;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(websiteURL));
                startActivity(browserIntent);

                //Toast.makeText(getActivity(), "TO COME: btnGoToWebsite", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnEditItem:
                switch (mPasswordItem.getItemType_ID()) {
                    case clsItemTypes.CREDIT_CARDS:
                        EventBus.getDefault().post(new clsEvents.replaceFragment(mPasswordItem.getID(),
                                MySettings.FRAG_EDIT_CREDIT_CARD, false));
                        break;

                    case clsItemTypes.GENERAL_ACCOUNTS:
                        EventBus.getDefault().post(new clsEvents.replaceFragment(mPasswordItem.getID(),
                                MySettings.FRAG_EDIT_GENERAL_ACCOUNT, false));
                        break;

                    case clsItemTypes.SOFTWARE:
                        EventBus.getDefault().post(new clsEvents.replaceFragment(mPasswordItem.getID(),
                                MySettings.FRAG_EDIT_SOFTWARE, false));
                        break;
                }
                //Toast.makeText(getActivity(), "TO COME: btnEditItem", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnEditWebsite:
                EventBus.getDefault().post(new clsEvents.replaceFragment(mPasswordItem.getID(),
                        MySettings.FRAG_EDIT_WEBSITE, false));
                //Toast.makeText(getActivity(), "TO COME: btnEditWebsite", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
