package lbconsulting.com.passwords.classes;

import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxPath;

/**
 * Created by Loren on 3/26/2015.
 */
public class clsDbxFolder {

    private DbxFileInfo mFileInfo;
    private DbxPath mPreviousFolderPath;

    public static final String DROPBOX_TEXT = "Dropbox";

    public clsDbxFolder(DbxFileInfo fileInfo, DbxPath previousFolderPath) {
        mFileInfo = fileInfo;
        mPreviousFolderPath = previousFolderPath;
    }

    public boolean isUpToFolder() {
        return mFileInfo == null;
    }

    public String getFolderName() {
        String result = "";
        if (mFileInfo != null && mFileInfo.path != null) {
            result = mFileInfo.path.getName();
        }
        return result;
    }

/*    public String getFolderPath() {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> pathList = new ArrayList<>();
        pathList.add(getFolderName());

        DbxPath dbxPath = mFileInfo.path.getParent();
        while (dbxPath != null) {
            pathList.add(dbxPath.getName());
            dbxPath = mFileInfo.path.getParent();
        }
        Collections.reverse(pathList);
        for (String pathElement : pathList) {
            sb.append("/").append(pathElement);
        }
        return sb.toString();
}*/

    public DbxPath getPreviousFolderPath() {
        return mPreviousFolderPath;
    }

    public DbxPath dbxPath() {
        return mFileInfo.path;
    }

    public String getIconName() {
        String result = "N/A";
        if (mFileInfo != null) {
            result = mFileInfo.iconName;
        }
        return result;
    }

    @Override
    public String toString() {
        String result = "";
        if (isUpToFolder()) {
            String previousFolderName = mPreviousFolderPath.getName();
            if (previousFolderName.isEmpty()) {
                previousFolderName = DROPBOX_TEXT;
            }
            result = "UP TO: " + previousFolderName;
        } else {
            result = getFolderName();
        }
        return result;
    }
}
