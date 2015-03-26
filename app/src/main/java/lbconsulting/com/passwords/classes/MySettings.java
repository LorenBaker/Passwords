package lbconsulting.com.passwords.classes;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import de.greenrobot.event.EventBus;
import lbconsulting.com.passwords.activities.MainActivity;

/**
 * Created by Loren on 3/5/2015.
 */
public class MySettings {

    public static final String NOT_AVAILABLE = "N/A...N/A";

    private static final String PASSWORDS_SAVED_STATES = "PasswordsSavedState";
    private static final String SETTING_ACTIVE_LIST_VIEW_ID = "arg_active_list_view";
    private static final String SETTING_ACTIVE_USER_ID = "arg_active_user_id";
    private static final String SETTING_APP_PASSWORD = "appPasswordKey";
    private static final String SETTING_ACTIVE_FRAGMENT_ID = "activeFragmentID";
    private static final String SETTING_ACTIVE_ITEM_ID = "activeItemID";
    private static final String SETTING_SEARCH_TEXT = "searchText";
    private static final String SETTING_PASSWORD_LONGEVITY = "passwordLongevity";


    private static final String SETTING_DROPBOX_FOLDER_NAME = "dropboxFolderName";
    // TODO: 3/25/2015 set default dropbox path
    private static final String DEFAULT_DROPBOX_PATH = "/BakerShare/LABPasswords";
    private static final String DROPBOX_FILENAME = "/JsonTest.txt";
    //private static final String DROPBOX_FILENAME = DEFAULT_DROPBOX_PATH + "/JsonTest.txt";



    public static final String ARG_ACTIVE_FRAGMENT = "arg_active_fragment";
    public static final int FRAG_ITEMS_LIST = 10;
    public static final int FRAG_ITEM_DETAIL = 20;
    public static final int FRAG_EDIT_CREDIT_CARD = 31;
    public static final int FRAG_EDIT_GENERAL_ACCOUNT = 32;
    public static final int FRAG_EDIT_SOFTWARE = 33;
    public static final int FRAG_EDIT_WEBSITE = 34;
    public static final int FRAG_SETTINGS = 40;
    public static final int FRAG_APP_PASSWORD = 41;


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
        // TODO: 3/25/2015 set the default user ID to -1 ??
        return passwordsSavedState.getInt(SETTING_ACTIVE_USER_ID, 1);
    }

    public static void setActiveUserID(int userID) {
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

    public static void setActiveUserName(String newName){
        clsUsers result = null;
        int activeUserID = getActiveUserID();
        if (activeUserID > 0) {
            for (clsUsers user : MainActivity.getPasswordsData().getUsers()) {
                if (user.getUserID() == activeUserID) {
                    result = user;
                }
            }
        }
        if(result!=null){
            result.setUserName(newName);
            EventBus.getDefault().post(new clsEvents.isDirty());
        }
    }

    public static String getDropboxFolderName() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getString(SETTING_DROPBOX_FOLDER_NAME, DEFAULT_DROPBOX_PATH);
    }

    public static void setDropboxFolderName(String dropboxFolderName) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putString(SETTING_DROPBOX_FOLDER_NAME, dropboxFolderName);
        editor.commit();
    }

    public static String getDropboxFilename() {
        return getDropboxFolderName() + DROPBOX_FILENAME;
    }

    public static int getActiveFragmentID() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getInt(SETTING_ACTIVE_FRAGMENT_ID, FRAG_ITEMS_LIST);
    }

    public static void setActiveFragmentID(int activeFragmentID) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_ACTIVE_FRAGMENT_ID, activeFragmentID);
        editor.commit();
    }

    public static int getPasswordLongevity() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getInt(SETTING_PASSWORD_LONGEVITY, 15);
    }

    public static void setPasswordLongevity(int passwordLongevity) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_PASSWORD_LONGEVITY, passwordLongevity);
        editor.commit();
    }

    public static String getAppPassword() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getString(SETTING_APP_PASSWORD, NOT_AVAILABLE);
    }

    public static void setAppPassword(String appPassword) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putString(SETTING_APP_PASSWORD, appPassword);
        editor.commit();
    }

    public static void resetAppPassword() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putString(SETTING_APP_PASSWORD, NOT_AVAILABLE);
        editor.commit();
    }

    public static int getActivePasswordItemID() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getInt(SETTING_ACTIVE_ITEM_ID, -1);

    }

    public static void setActivePasswordItemID(int activePasswordItemID) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_ACTIVE_ITEM_ID, activePasswordItemID);
        editor.commit();
    }

    public static int getActiveListViewID() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getInt(SETTING_ACTIVE_LIST_VIEW_ID, clsItemTypes.CREDIT_CARDS);
    }

    public static void setActiveListViewID(int activeListViewID) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_ACTIVE_LIST_VIEW_ID, activeListViewID);
        editor.commit();
    }

    public static String getSearchText() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getString(SETTING_SEARCH_TEXT, "");
    }

    public static void setSearchText(String searchText) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putString(SETTING_SEARCH_TEXT, searchText);
        editor.commit();
    }

    public static class Credentials {
        private final static String mIV = "74172ca8e67761d2";
        // TODO: 3/11/2015 remove Test Password
        //private static String mPassword = "Test Password";
        private static String mPassword = getAppPassword();

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
