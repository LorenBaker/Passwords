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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;
import lbconsulting.com.passwords.R;
import lbconsulting.com.passwords.activities.MainActivity;
import lbconsulting.com.passwords.adapters.DropboxFoldersListViewAdapter;
import lbconsulting.com.passwords.classes.MyLog;
import lbconsulting.com.passwords.classes.MySettings;
import lbconsulting.com.passwords.classes.clsDbxFolder;
import lbconsulting.com.passwords.classes.clsEvents;

/**
 * Created by Loren on 3/5/2015.
 * This fragment shows lists of Password Items
 */
public class DropboxListFragment extends Fragment
        implements View.OnClickListener, AdapterView.OnItemClickListener {

    //<editor-fold desc="Fragment Views">
    private TextView tvFolderName;
    //private TextView tvFolderPath;
    private ListView lvFolders;
    private Button btnCancel;
    private Button btnSelect;
    //</editor-fold>


    //<editor-fold desc="Module Variables">
    private static DbxFileSystem dbxFs;
    private ArrayList<clsDbxFolder> mDropboxFolders;
    private DropboxFoldersListViewAdapter mDropboxFoldersAdapter;

    private String mFolderPath = "";
    private DbxPath mActivePath;

    //</editor-fold>


    public DropboxListFragment() {

    }

    public static DropboxListFragment newInstance() {
        DropboxListFragment fragment = new DropboxListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLog.i("DropboxListFragment", "onCreate()");
        setHasOptionsMenu(true);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("DropboxListFragment", "onActivityCreated()");
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        dbxFs = MainActivity.getDbxFs();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MyLog.i("DropboxListFragment", "onCreateView()");
        View rootView = inflater.inflate(R.layout.frag_dropbox_list, container, false);

        tvFolderName = (TextView) rootView.findViewById(R.id.tvFolderName);
        // tvFolderPath = (TextView) rootView.findViewById(R.id.tvFolderPath);

        lvFolders = (ListView) rootView.findViewById(R.id.lvFolders);
        lvFolders.setOnItemClickListener(this);

        btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
        btnSelect = (Button) rootView.findViewById(R.id.btnSelect);
        btnCancel.setOnClickListener(this);
        btnSelect.setOnClickListener(this);

        return rootView;
    }


    private void updateUI(DbxPath path) {
        if (dbxFs != null) {
            mActivePath = path;
            try {
                List<DbxFileInfo> listFolders = dbxFs.listFolder(path);
                // TODO: 3/26/2015 sort listFolders
                if (listFolders != null) {
                    mDropboxFolders = new ArrayList<>();
                    for (DbxFileInfo folder : listFolders) {
                        if (folder.isFolder) {
                            clsDbxFolder dbxFolder = new clsDbxFolder(folder, null);
                            mDropboxFolders.add(dbxFolder);
                        }
                    }

                    DbxPath parentPath = null;
                    if (path.getName().isEmpty()) {
                        tvFolderName.setText(clsDbxFolder.DROPBOX_TEXT);
                        mFolderPath = "/";
                    } else {
                        try {
                            parentPath = path.getParent();
                        } catch (Exception e) {
                            // do nothing
                        }
                        if (parentPath != null) {
                            clsDbxFolder position0 = new clsDbxFolder(null, parentPath);
                            mDropboxFolders.add(0, position0);
                        }
                        tvFolderName.setText(path.getName());
                        mFolderPath = getFullFolderPath(path);
                        //tvFolderPath.setText(getFullFolderPath(path));
                    }

                    setArrayAdapter();
                }
            } catch (DbxException e) {
                MyLog.e("DropboxListFragment", "updateUI: DbxException");
                e.printStackTrace();
            }

        }
    }

    private String getFullFolderPath(DbxPath path) {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> pathList = new ArrayList<>();
        pathList.add(path.getName());

        DbxPath dbxPath = path.getParent();
        while (dbxPath != null) {
            pathList.add(dbxPath.getName());
            try {
                dbxPath = dbxPath.getParent();
            } catch (Exception e) {
                dbxPath = null;
            }
        }
        Collections.reverse(pathList);
        for (String pathElement : pathList) {
            sb.append("/").append(pathElement);
        }
        String result = sb.toString();
        result = result.replace("//", "/");
        return result;

    }


    private void setArrayAdapter() {
        mDropboxFoldersAdapter = new DropboxFoldersListViewAdapter(getActivity(), mDropboxFolders);
        lvFolders.setAdapter(mDropboxFoldersAdapter);
    }


    @Override
    public void onResume() {
        MyLog.i("DropboxListFragment", "onResume()");
        // Restore preferences
        DbxPath startingPath = DbxPath.ROOT;
        updateUI(startingPath);
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MyLog.i("DropboxListFragment", "onPause()");
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("DropboxListFragment", "onDestroy()");
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnCancel:
                //Toast.makeText(getActivity(), "btnCancel Clicked", Toast.LENGTH_SHORT).show();
                EventBus.getDefault().post(new clsEvents.PopBackStack());
                break;

            case R.id.btnSelect:
                //Toast.makeText(getActivity(), "btnSelect Clicked", Toast.LENGTH_SHORT).show();
                selectFolder(mActivePath);
                break;

        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        TextView tvFolderName = (TextView) view.findViewById(R.id.tvFolderName);
        if (tvFolderName != null) {
            clsDbxFolder dbxFolder = (clsDbxFolder) tvFolderName.getTag();
            if (dbxFolder != null) {
                if (dbxFolder.isUpToFolder()) {
                    updateUI(dbxFolder.getPreviousFolderPath());
                } else {
                    updateUI(dbxFolder.dbxPath());
                }
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_frag_dropbox_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            // Do Fragment menu item stuff here
            case R.id.action_new:
                // Toast.makeText(getActivity(), "action_new Clicked", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder newUserDialog = new AlertDialog.Builder(getActivity());

                newUserDialog.setTitle("Enter Dropbox Folder Name");
                newUserDialog.setMessage("");

                // Set an EditText view to get user input
                final EditText input = new EditText(getActivity());
                input.setHint("New Dropbox Folder Name");
                input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                newUserDialog.setView(input);

                newUserDialog.setPositiveButton("Create Folder", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String newFolderName = input.getText().toString().trim();
                        if (isUnique(newFolderName)) {
                            if (dbxFs != null) {
                                try {
                                    DbxPath newFolderPath = new DbxPath(mActivePath, newFolderName);
                                    dbxFs.createFolder(newFolderPath);
                                    selectFolder(newFolderPath);
                                } catch (DbxException e) {
                                    MyLog.e("DropboxListFragment", "onClick: DbxException");
                                    e.printStackTrace();
                                }
                            }
                            dialog.dismiss();
                        } else {
                            dialog.dismiss();
                            MyLog.e("DropboxListFragment", "onClick Create Folder: new folder name is not unique");
                            MainActivity.showOkDialog(getActivity(), "Failed to create new Dropbox folder.",
                                    "The provide folder name \"" + newFolderName + "\" already exists!");
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
                return true;

            case android.R.id.home:
                EventBus.getDefault().post(new clsEvents.PopBackStack());
                return true;

            default:
                // Not implemented here
                return false;
        }
    }

    private void selectFolder(DbxPath newFolderPath) {
        String newFolderPathString = getFullFolderPath(newFolderPath);
        MySettings.setDropboxFolderName(newFolderPathString);
        EventBus.getDefault().post(new clsEvents.PopBackStack());
    }

    private boolean isUnique(String newFolderName) {
        boolean result = true;
        for (clsDbxFolder folder : mDropboxFolders) {
            if (folder.getFolderName().equalsIgnoreCase(newFolderName)) {
                result = false;
                break;
            }
        }
        return result;
    }
}
