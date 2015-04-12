package lbconsulting.com.passwords.classes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import de.greenrobot.event.EventBus;
import lbconsulting.com.passwords.activities.MainActivity;
import lbconsulting.com.passwords.fragments.AppPasswordFragment;

/**
 * Created by Loren on 3/5/2015.
 */
public class MySettings {

    public static final String NOT_AVAILABLE = "N/A...N/A";

    private static final String PASSWORDS_SAVED_STATES = "PasswordsSavedState";
    private static final String SETTING_ACTIVE_LIST_VIEW_ID = "arg_active_list_view";
    private static final String SETTING_ACTIVE_USER_ID = "arg_active_user_id";
    private static final String SETTING_APP_PASSWORD = "appPasswordKey";
    private static final String SETTING_APP_PASSWORD_SAVED_TIME = "appPasswordSavedTime";
    private static final String SETTING_ON_SAVE_INSTANCE_STATE = "onSaveInstanceState";

    private static final String SETTING_ACTIVE_FRAGMENT_ID = "activeFragmentID";
    private static final String SETTING_ACTIVE_ITEM_ID = "activeItemID";
    private static final String SETTING_SEARCH_TEXT = "searchText";
    private static final String SETTING_PASSWORD_LONGEVITY = "passwordLongevity";
    private static final String STATE_APP_PASSWORD_FRAGMENT = "appPasswordFragmentState";


    private static final String SETTING_DROPBOX_FOLDER_NAME = "dropboxFolderName";
    private static final String DEFAULT_DROPBOX_PATH = "No Folder Selected";
    private static final String DROPBOX_FILENAME = "/PasswordsDatafile.txt";
    //private static final String DROPBOX_FILENAME = DEFAULT_DROPBOX_PATH + "/JsonTest.txt";

    public static final long DEFAULT_LONGEVITY_MILLISECONDS = 15 * 60000;

    public static final String PASSWORDS_FILENAME = "passwordsFile";

    public static final String ARG_ACTIVE_FRAGMENT = "arg_active_fragment";
    public static final int FRAG_ITEMS_LIST = 10;
    public static final int FRAG_ITEM_DETAIL = 20;
    public static final int FRAG_EDIT_CREDIT_CARD = 31;
    public static final int FRAG_EDIT_GENERAL_ACCOUNT = 32;
    public static final int FRAG_EDIT_SOFTWARE = 33;
    public static final int FRAG_EDIT_WEBSITE = 34;
    public static final int FRAG_SETTINGS = 40;
    public static final int FRAG_APP_PASSWORD = 41;
    public static final int FRAG_DROPBOX_LIST = 42;

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

    public static void setActiveUserID(int userID) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(SETTING_ACTIVE_USER_ID, userID);
        editor.apply();
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

    public static void setActiveUserName(String newName) {
        clsUsers result = null;
        int activeUserID = getActiveUserID();
        if (activeUserID > 0) {
            for (clsUsers user : MainActivity.getPasswordsData().getUsers()) {
                if (user.getUserID() == activeUserID) {
                    result = user;
                }
            }
        }
        if (result != null) {
            result.setUserName(newName);
            EventBus.getDefault().post(new clsEvents.saveChangesToDropbox());
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
        editor.apply();
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
        editor.apply();
    }

    public static long getPasswordLongevity() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getLong(SETTING_PASSWORD_LONGEVITY, DEFAULT_LONGEVITY_MILLISECONDS);
    }

    public static void setPasswordLongevity(long passwordLongevity) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putLong(SETTING_PASSWORD_LONGEVITY, passwordLongevity);
        editor.apply();
    }

    public static String getAppPassword() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);

        String appPassword = NOT_AVAILABLE;
        long passwordSavedTime = getPasswordSavedTime();
        long elapsedTimeMs = System.currentTimeMillis() - passwordSavedTime;
        long passwordLongevity = getPasswordLongevity();
        if (elapsedTimeMs < passwordLongevity) {
            // TODO: decrypt appPassword
            appPassword = passwordsSavedState.getString(SETTING_APP_PASSWORD, NOT_AVAILABLE);
        }

        return appPassword;
    }

    public static String getSavedAppPassword() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getString(SETTING_APP_PASSWORD, NOT_AVAILABLE);
    }

    public static void setAppPassword(String appPassword) {
        MyLog.i("MySettings", "setAppPassword to: " + appPassword);
        // TODO: encrypt appPassword
        setPasswordSavedTime();
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putString(SETTING_APP_PASSWORD, appPassword);
        editor.apply();
    }

    public static long getPasswordSavedTime() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getLong(SETTING_APP_PASSWORD_SAVED_TIME, -1);
    }

    private static void setPasswordSavedTime() {
        long currentTime = System.currentTimeMillis();
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putLong(SETTING_APP_PASSWORD_SAVED_TIME, currentTime);
        editor.apply();
    }

    public static boolean getOnSaveInstanceState(){
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getBoolean(SETTING_ON_SAVE_INSTANCE_STATE, false);
    }

    public static void setOnSaveInstanceState(boolean onSaveInstanceState){
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putBoolean(SETTING_ON_SAVE_INSTANCE_STATE, onSaveInstanceState);
        editor.apply();
    }

    public static void resetAppPassword() {
        setAppPassword(NOT_AVAILABLE);
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
        editor.apply();
    }


    public static int getAppPasswordState() {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        return passwordsSavedState.getInt(STATE_APP_PASSWORD_FRAGMENT, AppPasswordFragment.STATE_STEP_0);
    }

    public static void setAppPasswordState(int appPasswordState) {
        SharedPreferences passwordsSavedState =
                mContext.getSharedPreferences(PASSWORDS_SAVED_STATES, 0);
        SharedPreferences.Editor editor = passwordsSavedState.edit();
        editor.putInt(STATE_APP_PASSWORD_FRAGMENT, appPasswordState);
        editor.apply();
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
        editor.apply();
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
        editor.apply();
    }

    public static class Credentials {
        private final static String mIV = "74172ca8e67761d2";

        public static String getIV() {
            return mIV;
        }

        public static String getPassword() {
            return getSavedAppPassword();
        }

        public static String getKey() {
            String key = "";
            String savedPassword = getSavedAppPassword();
            try {
                if (!savedPassword.isEmpty()) {
                    key = CryptLib.SHA256(savedPassword, 32);
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
