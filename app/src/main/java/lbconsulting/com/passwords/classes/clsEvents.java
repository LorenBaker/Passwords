package lbconsulting.com.passwords.classes;


public class clsEvents {


    public static class updateUI {
        public updateUI() {
        }
    }

    public static class readLabPasswordDataComplete {
        public readLabPasswordDataComplete() {
        }
    }

    public static class downLoadResults {
        private int mResults;

        public downLoadResults(int results) {
            mResults = results;
        }

        public int getDownLoadResults() {
            return mResults;
        }
    }

    public static class openAndReadLabPasswordDataAsync{
        public openAndReadLabPasswordDataAsync(){
        }
    }

    public static class readLabPasswordDataAsync{
        public readLabPasswordDataAsync(){
        }
    }

    public static class openAndSaveLabPasswordDataAsync{
        public openAndSaveLabPasswordDataAsync(){
        }
    }

    public static class isDirty {
        public isDirty() {
        }
    }

    public static class PopBackStack {
        public PopBackStack() {
        }
    }

    public static class showOkDialog {

        String mTitle;
        String mMessage;

        public showOkDialog(String title, String message) {
            mTitle = title;
            mMessage = message;
        }

        public String getTitle() {
            return mTitle;
        }

        public String getMessage() {
            return mMessage;
        }
    }

    public static class replaceFragment {
        int mItemID;
        int mFragmentID;
        boolean mIsNewPasswordItem;

        public replaceFragment(int itemID, int fragmentID, boolean isNewPasswordItem) {
            mItemID = itemID;
            mFragmentID = fragmentID;
            mIsNewPasswordItem = isNewPasswordItem;
        }

        public int getItemID() {
            return mItemID;
        }

        public int getFragmentID() {
            return mFragmentID;
        }

        public boolean getIsNewPasswordItem() {
            return mIsNewPasswordItem;
        }
    }
}


