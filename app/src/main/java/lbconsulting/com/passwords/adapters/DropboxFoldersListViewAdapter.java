package lbconsulting.com.passwords.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import lbconsulting.com.passwords.R;
import lbconsulting.com.passwords.classes.clsDbxFolder;

/**
 * List view adapter for clsDbxFolder
 */
public class DropboxFoldersListViewAdapter extends ArrayAdapter<clsDbxFolder> {

    private Context mContext;
    private ArrayList<clsDbxFolder> mItems;

    // View lookup cache
    private static class ViewHolder {
        TextView tvFolderName;
    }

    public DropboxFoldersListViewAdapter(Context context, ArrayList<clsDbxFolder> items) {
        super(context, R.layout.row_lv_dropbox_folder, items);
        this.mContext = context;
        this.mItems = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        clsDbxFolder record = mItems.get(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.row_lv_dropbox_folder, parent, false);
            viewHolder.tvFolderName = (TextView) convertView.findViewById(R.id.tvFolderName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data into the template view using the data object
        viewHolder.tvFolderName.setText(record.toString() + ": " + record.getIconName());
        viewHolder.tvFolderName.setTag(record);

        // Return the completed view to render on screen
        return convertView;
    }
}
