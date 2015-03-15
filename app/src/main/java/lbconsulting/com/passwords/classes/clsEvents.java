package lbconsulting.com.passwords.classes;


public class clsEvents {


    public static class updateUI {
        public updateUI() {
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

        public boolean getIsNewPasswordItem(){
            return mIsNewPasswordItem;
        }
    }
}


