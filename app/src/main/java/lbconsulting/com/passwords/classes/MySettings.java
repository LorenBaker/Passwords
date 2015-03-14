package lbconsulting.com.passwords.classes;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Loren on 3/5/2015.
 */
public class MySettings {

    public static final String PASSWORDS_SAVED_STATE = "PasswordsSavedState";
    public static final String ARG_ACTIVE_LIST_VIEW = "arg_active_list_view";
    public static final String ARG_ACTIVE_USER_ID = "arg_active_user_id";
    public static final String ARG_ITEM_ID = "arg_item_id";

    //                                         "/BakerShare/LABPasswords"
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


    public static final String[] CreditCardNames = {"American Express", "Diners Club", "Discover", "JCB", "MasterCard", "VISA"};
    public static final String UNKNOWN = "UNKNOWN";
    public static final int UNKNOWN_CARD = -1;
    public static final int AMERICAN_EXPRESS = 0;
    public static final int DINERS_CLUB = 1;
    public static final int DISCOVER = 2;
    public static final int JCB = 3;
    public static final int MASTERCARD = 4;
    public static final int VISA = 5;


    public static int getActiveUserID() {
        // TODO: 3/9/2015 active UserID set from setting activity
        return 1;
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
