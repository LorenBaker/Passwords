package lbconsulting.com.passwords.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;
import lbconsulting.com.passwords.R;
import lbconsulting.com.passwords.activities.MainActivity;
import lbconsulting.com.passwords.adapters.PasswordItemsListViewAdapter;
import lbconsulting.com.passwords.classes.MyLog;
import lbconsulting.com.passwords.classes.MySettings;
import lbconsulting.com.passwords.classes.clsEvents;
import lbconsulting.com.passwords.classes.clsItemTypes;
import lbconsulting.com.passwords.classes.clsPasswordItem;
import lbconsulting.com.passwords.classes.clsUsers;

/**
 * Created by Loren on 3/5/2015.
 * This fragment shows lists of Password Items
 */
public class PasswordItemsListFragment extends Fragment
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    //<editor-fold desc="Fragment Views">

    private EditText txtSearch;
    private Button btnCreditCards;
    private Button btnGeneralAccounts;
    private Button btnWebsites;
    private Button btnSoftware;
    private ListView lvAllUserItems;
    private ListView lvCreditCards;
    private ListView lvGeneralAccounts;
    private ListView lvWebsites;
    private ListView lvSoftware;

    //</editor-fold>


    //<editor-fold desc="Module Variables">

    private int mActiveListView = clsItemTypes.CREDIT_CARDS;
    private String mSearchText;


    //private int mActiveUserID;
    private ArrayList<clsPasswordItem> mAllItems;
    private ArrayList<clsPasswordItem> mAllUserItems;
    private ArrayList<clsPasswordItem> mUserCreditCardItems;
    private ArrayList<clsPasswordItem> mUserGeneralAccountItems;
    private ArrayList<clsPasswordItem> mUserWebsiteItems;
    private ArrayList<clsPasswordItem> mUserSoftwareItems;
    private PasswordItemsListViewAdapter mAllUserItemsAdapter;

    //</editor-fold>


    public PasswordItemsListFragment() {

    }

    public static PasswordItemsListFragment newInstance() {
        PasswordItemsListFragment fragment = new PasswordItemsListFragment();
/*        Bundle args = new Bundle();
        args.putInt(MySettings.SETTING_ACTIVE_USER_ID, userID);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("PasswordItemsListFragment", "onCreate()");
        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("PasswordItemsListFragment", "onActivityCreated()");
        mSearchText = MySettings.getSearchText();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MyLog.i("PasswordItemsListFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_password_items_list, container, false);

        txtSearch = (EditText) rootView.findViewById(R.id.txtSearch);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (lvAllUserItems != null && mAllUserItems != null) {
                    mSearchText = s.toString();
                    if (s.length() == 0) {
                        mAllUserItemsAdapter = new PasswordItemsListViewAdapter(getActivity(), mAllUserItems);
                        lvAllUserItems.setAdapter(mAllUserItemsAdapter);
                    } else {
                        ArrayList<clsPasswordItem> filteredUserItems = new ArrayList<clsPasswordItem>();
                        for (clsPasswordItem item : mAllUserItems) {
                            if (item.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                                filteredUserItems.add(item);
                            }
                        }
                        mAllUserItemsAdapter = new PasswordItemsListViewAdapter(getActivity(), filteredUserItems);
                        lvAllUserItems.setAdapter(mAllUserItemsAdapter);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        btnCreditCards = (Button) rootView.findViewById(R.id.btnCreditCards);
        btnGeneralAccounts = (Button) rootView.findViewById(R.id.btnGeneralAccounts);
        btnWebsites = (Button) rootView.findViewById(R.id.btnWebsites);
        btnSoftware = (Button) rootView.findViewById(R.id.btnSoftware);

        btnCreditCards.setOnClickListener(this);
        btnGeneralAccounts.setOnClickListener(this);
        btnWebsites.setOnClickListener(this);
        btnSoftware.setOnClickListener(this);

        lvAllUserItems = (ListView) rootView.findViewById(R.id.lvAllUserItems);
        lvCreditCards = (ListView) rootView.findViewById(R.id.lvCreditCards);
        lvGeneralAccounts = (ListView) rootView.findViewById(R.id.lvGeneralAccounts);
        lvWebsites = (ListView) rootView.findViewById(R.id.lvWebsites);
        lvSoftware = (ListView) rootView.findViewById(R.id.lvSoftware);

        lvAllUserItems.setOnItemClickListener(this);
        lvCreditCards.setOnItemClickListener(this);
        lvGeneralAccounts.setOnItemClickListener(this);
        lvWebsites.setOnItemClickListener(this);
        lvSoftware.setOnItemClickListener(this);

        return rootView;
    }


    public void onEvent(clsEvents.updateUI event) {
        MyLog.i("PasswordItemsListFragment", "onEvent.updateUI()");
        updateUI();
    }

    private void updateUI() {
        MainActivity.setUserNameInActionBar();
        if (MainActivity.getPasswordsData() != null) {
            mAllItems = MainActivity.getPasswordsData().getPasswordItems();
            if (mAllItems != null) {
                MyLog.i("PasswordItemsListFragment", "updateUI()");
                fillUserArrayLists();
                setArrayAdapters();
            }
        }
    }

    private void fillUserArrayLists() {
        mAllUserItems = new ArrayList<>();
        mUserCreditCardItems = new ArrayList<>();
        mUserGeneralAccountItems = new ArrayList<>();
        mUserSoftwareItems = new ArrayList<>();
        mUserWebsiteItems = new ArrayList<>();

        int lastPasswordItemID = -1;
        for (clsPasswordItem item : mAllItems) {
            if (item.getID() > lastPasswordItemID) {
                lastPasswordItemID = item.getID();
            }
            if (item.getUser_ID() == MySettings.getActiveUserID()) {
                mAllUserItems.add(item);
                switch (item.getItemType_ID()) {
                    case clsItemTypes.CREDIT_CARDS:
                        mUserCreditCardItems.add(item);
                        break;

                    case clsItemTypes.GENERAL_ACCOUNTS:
                        mUserGeneralAccountItems.add(item);
                        break;

                    case clsItemTypes.SOFTWARE:
                        mUserSoftwareItems.add(item);
                        break;

                    case clsItemTypes.WEBSITES:
                        mUserWebsiteItems.add(item);
                        break;
                }
            }
        }
        MainActivity.setLastPasswordItemID(lastPasswordItemID);
    }

    private void setArrayAdapters() {
        mAllUserItemsAdapter = new PasswordItemsListViewAdapter(getActivity(), mAllUserItems);
        PasswordItemsListViewAdapter userCreditCardItemsAdapter =
                new PasswordItemsListViewAdapter(getActivity(), mUserCreditCardItems);
        PasswordItemsListViewAdapter userGeneralAccountItemsAdapter =
                new PasswordItemsListViewAdapter(getActivity(), mUserGeneralAccountItems);
        PasswordItemsListViewAdapter userSoftwareItemsAdapter =
                new PasswordItemsListViewAdapter(getActivity(), mUserSoftwareItems);
        PasswordItemsListViewAdapter userWebsiteItemsAdapter =
                new PasswordItemsListViewAdapter(getActivity(), mUserWebsiteItems);

        // placing text into txtSearch triggers onTextChanged event that sets lvAllUserItems adapter
        txtSearch.setText(mSearchText);

        lvCreditCards.setAdapter(userCreditCardItemsAdapter);
        lvGeneralAccounts.setAdapter(userGeneralAccountItemsAdapter);
        lvSoftware.setAdapter(userSoftwareItemsAdapter);
        lvWebsites.setAdapter(userWebsiteItemsAdapter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_password_items_list, menu);
        super.onCreateOptionsMenu(menu, inflater);

        if (mActiveListView == clsItemTypes.ALL_ITEMS) {
            menu.findItem(R.id.action_show_categories).setVisible(true);
            menu.findItem(R.id.action_show_search).setVisible(false);
        } else {
            menu.findItem(R.id.action_show_categories).setVisible(false);
            menu.findItem(R.id.action_show_search).setVisible(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Do Fragment menu item stuff here
            case R.id.action_new:
                final clsPasswordItem newPasswordItem = MainActivity.createNewPasswordItem();

                if (lvCreditCards.getVisibility() == View.VISIBLE) {
                    newPasswordItem.setItemType_ID(clsItemTypes.CREDIT_CARDS);
                    EventBus.getDefault().post(new clsEvents.replaceFragment(newPasswordItem.getID(),
                            MySettings.FRAG_EDIT_CREDIT_CARD, true));

                } else if (lvGeneralAccounts.getVisibility() == View.VISIBLE) {
                    newPasswordItem.setItemType_ID(clsItemTypes.GENERAL_ACCOUNTS);
                    EventBus.getDefault().post(new clsEvents.replaceFragment(newPasswordItem.getID(),
                            MySettings.FRAG_EDIT_GENERAL_ACCOUNT, true));

                } else if (lvSoftware.getVisibility() == View.VISIBLE) {
                    newPasswordItem.setItemType_ID(clsItemTypes.SOFTWARE);
                    EventBus.getDefault().post(new clsEvents.replaceFragment(newPasswordItem.getID(),
                            MySettings.FRAG_EDIT_SOFTWARE, true));

                } else if (lvWebsites.getVisibility() == View.VISIBLE) {
                    newPasswordItem.setItemType_ID(clsItemTypes.WEBSITES);
                    EventBus.getDefault().post(new clsEvents.replaceFragment(newPasswordItem.getID(),
                            MySettings.FRAG_EDIT_WEBSITE, true));

                } else if (lvAllUserItems.getVisibility() == View.VISIBLE) {
                    // Strings to Show In Dialog with Radio Buttons
                    int selectedItemType = -1;

                    // Creating and Building the Dialog
                    Dialog itemTypesDialog;
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Select Item Type");
                    builder.setSingleChoiceItems(clsItemTypes.ITEM_TYPES, selectedItemType, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {
                            int itemType = item+1;
                            switch (itemType){
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
                            dialog.dismiss();
                        }
                    });
                    itemTypesDialog = builder.create();
                    itemTypesDialog.show();
                }

                //Toast.makeText(getActivity(), "TO COME: action_new", Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_show_search:
                //Toast.makeText(getActivity(), "TO COME: action_show_search", Toast.LENGTH_SHORT).show();
                setupDisplay(clsItemTypes.ALL_ITEMS);
                getActivity().invalidateOptionsMenu();
                return true;

            case R.id.action_show_categories:
                //Toast.makeText(getActivity(), "TO COME: action_show_categories", Toast.LENGTH_SHORT).show();
                setupDisplay(clsItemTypes.CREDIT_CARDS);
                getActivity().invalidateOptionsMenu();
                return true;

            default:
                // Not implemented here
                return false;
        }
    }


    @Override
    public void onResume() {
        MyLog.i("PasswordItemsListFragment", "onResume()");
        // Restore preferences

        mActiveListView = MySettings.getActiveListViewID();
        MySettings.setActiveFragmentID(MySettings.FRAG_ITEMS_LIST);
        MySettings.setActivePasswordItemID(-1);
        setupDisplay(mActiveListView);
        MainActivity.sortPasswordsData();
        if (mActiveListView == clsItemTypes.ALL_ITEMS) {
            mSearchText = MySettings.getSearchText();
            txtSearch.setText(mSearchText);
        }
        updateUI();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("PasswordItemsListFragment", "onPause()");
        MySettings.setActiveListViewID(mActiveListView);
        MySettings.setSearchText(mSearchText);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("PasswordItemsListFragment", "onDestroy()");
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnCreditCards:
                setupDisplay(clsItemTypes.CREDIT_CARDS);
                break;

            case R.id.btnGeneralAccounts:
                setupDisplay(clsItemTypes.GENERAL_ACCOUNTS);
                break;

            case R.id.btnSoftware:
                setupDisplay(clsItemTypes.SOFTWARE);
                break;

            case R.id.btnWebsites:
                setupDisplay(clsItemTypes.WEBSITES);
                break;

        }
    }


    private void setupDisplay(int displayType) {
        switch (displayType) {
            case clsItemTypes.CREDIT_CARDS:
                txtSearch.setVisibility(View.GONE);
                lvAllUserItems.setVisibility(View.GONE);
                btnCreditCards.setVisibility(View.VISIBLE);
                btnGeneralAccounts.setVisibility(View.VISIBLE);
                btnWebsites.setVisibility(View.VISIBLE);
                btnSoftware.setVisibility(View.VISIBLE);
                lvCreditCards.setVisibility(View.VISIBLE);
                lvGeneralAccounts.setVisibility(View.GONE);
                lvWebsites.setVisibility(View.GONE);
                lvSoftware.setVisibility(View.GONE);
                mActiveListView = clsItemTypes.CREDIT_CARDS;
                break;

            case clsItemTypes.GENERAL_ACCOUNTS:
                txtSearch.setVisibility(View.GONE);
                lvAllUserItems.setVisibility(View.GONE);
                btnCreditCards.setVisibility(View.VISIBLE);
                btnGeneralAccounts.setVisibility(View.VISIBLE);
                btnWebsites.setVisibility(View.VISIBLE);
                btnSoftware.setVisibility(View.VISIBLE);
                lvCreditCards.setVisibility(View.GONE);
                lvGeneralAccounts.setVisibility(View.VISIBLE);
                lvWebsites.setVisibility(View.GONE);
                lvSoftware.setVisibility(View.GONE);
                mActiveListView = clsItemTypes.GENERAL_ACCOUNTS;
                break;

            case clsItemTypes.SOFTWARE:
                txtSearch.setVisibility(View.GONE);
                lvAllUserItems.setVisibility(View.GONE);
                btnCreditCards.setVisibility(View.VISIBLE);
                btnGeneralAccounts.setVisibility(View.VISIBLE);
                btnWebsites.setVisibility(View.VISIBLE);
                btnSoftware.setVisibility(View.VISIBLE);
                lvCreditCards.setVisibility(View.GONE);
                lvGeneralAccounts.setVisibility(View.GONE);
                lvWebsites.setVisibility(View.GONE);
                lvSoftware.setVisibility(View.VISIBLE);
                mActiveListView = clsItemTypes.SOFTWARE;
                break;

            case clsItemTypes.WEBSITES:
                txtSearch.setVisibility(View.GONE);
                lvAllUserItems.setVisibility(View.GONE);
                btnCreditCards.setVisibility(View.VISIBLE);
                btnGeneralAccounts.setVisibility(View.VISIBLE);
                btnWebsites.setVisibility(View.VISIBLE);
                btnSoftware.setVisibility(View.VISIBLE);
                lvCreditCards.setVisibility(View.GONE);
                lvGeneralAccounts.setVisibility(View.GONE);
                lvWebsites.setVisibility(View.VISIBLE);
                lvSoftware.setVisibility(View.GONE);
                mActiveListView = clsItemTypes.WEBSITES;
                break;

            case clsItemTypes.ALL_ITEMS:
                txtSearch.setVisibility(View.VISIBLE);
                lvAllUserItems.setVisibility(View.VISIBLE);
                btnCreditCards.setVisibility(View.GONE);
                btnGeneralAccounts.setVisibility(View.GONE);
                btnWebsites.setVisibility(View.GONE);
                btnSoftware.setVisibility(View.GONE);
                lvCreditCards.setVisibility(View.GONE);
                lvGeneralAccounts.setVisibility(View.GONE);
                lvWebsites.setVisibility(View.GONE);
                lvSoftware.setVisibility(View.GONE);
                mActiveListView = clsItemTypes.ALL_ITEMS;
                txtSearch.setText("");
                break;
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tvItemName = (TextView) view.findViewById(R.id.tvItemName);
        if (tvItemName != null) {
            clsPasswordItem item = (clsPasswordItem) tvItemName.getTag();
            if (item != null) {
                int itemID = item.getID();
                MySettings.setActivePasswordItemID(itemID);
                //MainActivity.setActivePosition(position);
                EventBus.getDefault().post(new clsEvents.replaceFragment(itemID, MySettings.FRAG_ITEM_DETAIL, false));
            }
        }
    }
}
