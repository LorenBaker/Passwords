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

        public replaceFragment(int itemID, int fragmentID) {
            mItemID = itemID;
            mFragmentID = fragmentID;
        }

        public int getItemID() {
            return mItemID;
        }

        public int getFragmentID() {
            return mFragmentID;
        }
    }
}


