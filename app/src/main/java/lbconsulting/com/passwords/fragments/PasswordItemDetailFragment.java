package lbconsulting.com.passwords.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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

    clsPasswordItem mPasswordItem;

    Button btnCallAlternate;
    Button btnCallPrimary;
    Button btnCopyAccountNumber;
    Button btnCopyPassword;
    Button btnGoToWebsite;
    EditText txtComments;
    ImageButton btnEditItem;
    ImageButton btnEditWebsite;
    TextView tvItemDetail;
    TextView tvPasswordItemName;
    TextView tvWebsiteDetail;


    public static PasswordItemDetailFragment newInstance(int itemID) {
        PasswordItemDetailFragment fragment = new PasswordItemDetailFragment();
        Bundle args = new Bundle();
        args.putInt(MySettings.ARG_ITEM_ID, itemID);
        fragment.setArguments(args);
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
        if (getArguments().containsKey(MySettings.ARG_ITEM_ID)) {
            int itemID = getArguments().getInt(MySettings.ARG_ITEM_ID);
            mPasswordItem = MainActivity.getPasswordItem(itemID);
        }
        setHasOptionsMenu(true);
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

        updateUI();
        return rootView;
    }

    private void updateUI() {
        tvPasswordItemName.setText(mPasswordItem.getName());
        tvItemDetail.setText(mPasswordItem.getItemDetail());
        tvWebsiteDetail.setText(mPasswordItem.getWebsiteDetail());
        txtComments.setText(mPasswordItem.getComments());

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
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("PasswordItemDetailFragment", "onActivityCreated()");
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("PasswordItemDetailFragment", "onResume()");
        MainActivity.setActiveFragmentID(MySettings.FRAG_ITEM_DETAIL);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("PasswordItemDetailFragment", "onSaveInstanceState()");
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("PasswordItemDetailFragment", "onPause()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("PasswordItemDetailFragment", "onDestroy()");
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
                Toast.makeText(getActivity(), "TO COME: action_discard", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_new:
                Toast.makeText(getActivity(), "TO COME: action_new", Toast.LENGTH_SHORT).show();
                return true;
            default:
                // Not implemented here
                return false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCallAlternate:
                Toast.makeText(getActivity(), "TO COME: btnCallAlternate", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnCallPrimary:
                Toast.makeText(getActivity(), "TO COME: btnCallPrimary", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnCopyAccountNumber:
                Toast.makeText(getActivity(), "TO COME: btnCopyAccountNumber", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnCopyPassword:
                Toast.makeText(getActivity(), "TO COME: btnCopyPassword", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnGoToWebsite:
                Toast.makeText(getActivity(), "TO COME: btnGoToWebsite", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnEditItem:
                switch (mPasswordItem.getItemType_ID()){
                    case  MySettings.
                        EventBus.getDefault().post(new clsEvents.replaceFragment(mPasswordItem.getID(), MySettings.FRAG_EDIT_CREDIT_CARD));
                        break;

                    case MySettings.FRAG_EDIT_GENERAL_ACCOUNT:

                        break;
                }
                //Toast.makeText(getActivity(), "TO COME: btnEditItem", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnEditWebsite:
                Toast.makeText(getActivity(), "TO COME: btnEditWebsite", Toast.LENGTH_SHORT).show();
                break;

        }
    }
}
