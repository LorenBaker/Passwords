package lbconsulting.com.passwords.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lbconsulting.com.passwords.R;
import lbconsulting.com.passwords.activities.MainActivity;
import lbconsulting.com.passwords.adapters.DropboxFoldersListViewAdapter;
import lbconsulting.com.passwords.adapters.PasswordItemsListViewAdapter;
import lbconsulting.com.passwords.classes.MyLog;
import lbconsulting.com.passwords.classes.clsDbxFolder;

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
/*        EventBus.getDefault().register(this);
        setHasOptionsMenu(true);*/
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyLog.i("DropboxListFragment", "onActivityCreated()");
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyLog.i("DropboxListFragment", "onDestroy()");
/*        EventBus.getDefault().unregister(this);*/
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnCancel:
                Toast.makeText(getActivity(), "btnCancel Clicked", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btnSelect:
                Toast.makeText(getActivity(), "btnSelect Clicked", Toast.LENGTH_SHORT).show();

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
}
