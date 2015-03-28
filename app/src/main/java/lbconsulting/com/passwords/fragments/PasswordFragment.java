package lbconsulting.com.passwords.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import de.greenrobot.event.EventBus;
import lbconsulting.com.passwords.R;
import lbconsulting.com.passwords.activities.MainActivity;
import lbconsulting.com.passwords.classes.MyLog;
import lbconsulting.com.passwords.classes.MySettings;
import lbconsulting.com.passwords.classes.clsEvents;

/**
 * A fragment that allows the editing of a Credit Card
 */
public class PasswordFragment extends Fragment implements View.OnClickListener {

    // fragment state variables
    private static final String ARG_IS_FIRST_TIME = "isFirstTime";
    private boolean mIsFirstTime = false;
    private boolean mShowPasswordText = false;

    private ProgressBar progressBar;
    private EditText txtAppPassword;
    private Button btnDisplay;
    private Button btnOK;
    private Button btnSelectDropboxFolder;
    private View line1;
    private View line2;

    public static PasswordFragment newInstance(boolean isFirstTime) {
        PasswordFragment fragment = new PasswordFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_IS_FIRST_TIME, isFirstTime);
        fragment.setArguments(args);
        return fragment;
    }

    public PasswordFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("PasswordFragment", "onCreate()");

        if (getArguments() != null) {
            mIsFirstTime = getArguments().getBoolean(ARG_IS_FIRST_TIME);
        }

        EventBus.getDefault().register(this);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MyLog.i("PasswordFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_password, container, false);

        progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);

        txtAppPassword = (EditText) rootView.findViewById(R.id.txtAppPassword);
        btnDisplay = (Button) rootView.findViewById(R.id.btnDisplay);
        btnOK = (Button) rootView.findViewById(R.id.btnOK);
        btnSelectDropboxFolder = (Button) rootView.findViewById(R.id.btnSelectDropboxFolder);

        btnDisplay.setOnClickListener(this);
        btnOK.setOnClickListener(this);
        btnSelectDropboxFolder.setOnClickListener(this);

        line1 = (View) rootView.findViewById(R.id.line1);
        line2 = (View) rootView.findViewById(R.id.line2);

        return rootView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("PasswordFragment", "onActivityCreated()");
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        MyLog.i("PasswordFragment", "onSaveInstanceState()");
    }

    @Override
    public void onResume() {
        super.onResume();
        MyLog.i("PasswordFragment", "onResume()");
        MySettings.setActiveFragmentID(MySettings.FRAG_APP_PASSWORD);
        updateUI();
    }

    private void updateUI() {
        // TODO: 3/24/2015 remove Test Password
        txtAppPassword.setText("Test Password");
        btnSelectDropboxFolder.setText("Select Dropbox Folder\n\nCurrent folder: " + MySettings.getDropboxFilename());
    }

    public void onEvent(clsEvents.readLabPasswordDataComplete event) {
        EventBus.getDefault().post(new clsEvents.replaceFragment(-1, MySettings.FRAG_ITEMS_LIST, false));
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("PasswordFragment", "onPause()");
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        MyLog.i("PasswordFragment", "onDestroy()");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btnDisplay:

                if (mShowPasswordText) {
                    txtAppPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);

                    btnDisplay.setText("Display");
                } else {
                    txtAppPassword.setInputType(InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btnDisplay.setText("Hide");
                }
                txtAppPassword.setSelection(txtAppPassword.getText().length());
                mShowPasswordText = !mShowPasswordText;

                //Toast.makeText(getActivity(), "TO COME: btnDisplay", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnOK:
                MySettings.setAppPassword(txtAppPassword.getText().toString().trim());
                // TODO: 3/24/2015 start longevity service
                showProgressBar();

                MainActivity.openAndReadLabPasswordDataAsync();
                //EventBus.getDefault().post(new clsEvents.replaceFragment(-1,MySettings.FRAG_ITEMS_LIST,false));
                // Toast.makeText(getActivity(), "TO COME: btnOK", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnSelectDropboxFolder:
                EventBus.getDefault().post(new clsEvents.replaceFragment(-1, MySettings.FRAG_DROPBOX_LIST, false));
                //Toast.makeText(getActivity(), "TO COME: btnSelectDropboxFolder", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        txtAppPassword.setVisibility(View.GONE);
        btnDisplay.setVisibility(View.GONE);
        btnOK.setVisibility(View.GONE);
        btnSelectDropboxFolder.setVisibility(View.GONE);
        line1.setVisibility(View.GONE);
        line2.setVisibility(View.GONE);

    }

}
