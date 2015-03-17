package lbconsulting.com.passwords.classes;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import lbconsulting.com.passwords.activities.MainActivity;

/**
 * Created by Loren on 3/5/2015.
 */
public class MySettings {

    public static final String PASSWORDS_SAVED_STATES = "PasswordsSavedState";
    public static final String ARG_ACTIVE_LIST_VIEW = "arg_active_list_view";
    public static final String SETTING_ACTIVE_USER_ID = "arg_active_user_id";
    public static final String ARG_ITEM_ID = "arg_item_id";
    public static final String SETTING_DROPBOX_FOLDER_NAME = "dropboxFolderName";

    private static final String DROPBOX_PATH = "/BakerShare/LABPasswords";

    public static String getDropboxPath() {
        return DROPBOX_PATH;
    }

    private static final String DROPBOX_FILENAME = DROPBOX_PATH + "/JsonTest.txt";

    public static String getDropboxFilename() {
        return DROPBOX_FILENAME;
    }

    public static final String ARG_ACTIVE_FRAGMENT = "arg_active_fragment";
    public static final int FRAG_ITEMS_LIST = 10;
    public static final int FRAG_ITEM_DETAIL = 20;
    public static final int FRAG_EDIT_CREDIT_CARD = 31;
    public static final int FRAG_EDIT_GENERAL_ACCOUNT = 32;
    public static final int FRAG_EDIT_SOFTWARE = 33;
    public static final int FRAG_EDIT_WEBSITE = 34;
    public static final int FRAG_SETTINGS = 40;



    public static final String[] CreditCardNames = {"American Express", "Diners Club", "Discover", "JCB", "MasterCard", "VISA"};
    public static final String UNKNOWN = "UNKNOWN";
    public static final int UNKNOWN_CARD = -1;
    public static final int AMERICAN_EXPRESS = 0;
    public static final int DINERS_CLUB = 1;
    public static final int DISCOVER = 2;
    public static final int JCB = 3;
    public static final int MASTERCARD = 4;
    public static final int VISA = 5;

    private static Context mContext;

    public static void setContext(Context context) {
        mContext = context;
    }

    //public static final String SETTING_USER_LIST = "setting_user_list";

    public static int getActiveUserID() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);

        return passwordsSavedState.getInt(SETTING_ACTIVE_USER_ID, -1);
    }

    public static void setActiveUserID(int userID){
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_ACTIVE_USER_ID, userID);
        editor.commit();
    }

    public static clsUsers getActiveUser() {
        clsUsers result = null;
        int activeUserID = getActiveUserID();
        if (activeUserID > 0) {
            for (clsUsers user : MainActivity.getPasswordsData().getUsers()) {
                if (user.getUserID() == activeUserID) {
                    result = user;
                }
            }
        }
        return result;
    }

    public static String getDropboxFolderName() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getString(SETTING_DROPBOX_FOLDER_NAME, "");
    }

    public static void setDropboxFolderName(String dropboxFolderName) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putString(SETTING_DROPBOX_FOLDER_NAME, dropboxFolderName);
        editor.commit();
    }

    public static class Credentials {
        private final static String mIV = "74172ca8e67761d2";
        // TODO: 3/11/2015 remove Test Password
        private static String mPassword = "Test Password";

        public static String getIV() {
            return mIV;
        }

        public static String getPassword() {
            return mPassword;
        }

        public static void setPassword(String password) {
            mPassword = password;
        }

        public static String getKey() {
            String key = "";
            try {
                if (!mPassword.isEmpty()) {
                    key = CryptLib.SHA256(mPassword, 32);
                }
            } catch (NoSuchAlgorithmException e) {
                MyLog.e("Credentials", "getKey: NoSuchAlgorithmException");
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                MyLog.e("Credentials", "getKey: UnsupportedEncodingException");
                e.printStackTrace();
            }
            return key;
        }
    }

}
