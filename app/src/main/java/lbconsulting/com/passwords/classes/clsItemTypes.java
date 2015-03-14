package lbconsulting.com.passwords.classes;

/**
 * Created by Loren on 3/8/2015.
 */
public class clsItemTypes {

    public static final int CREDIT_CARDS = 1;
    public static final int GENERAL_ACCOUNTS = 2;
    public static final int SOFTWARE = 3;
    public static final int WEBSITES = 4;
    public static final int ALL_ITEMS = 5;

    public int getItemTypeID() {
        return ItemTypeID;
    }

    public void setItemTypeID(int itemTypeID) {
        ItemTypeID = itemTypeID;
    }

    public String getItemType() {
        return ItemType;
    }

    public void setItemType(String itemType) {
        ItemType = itemType;
    }

    private int ItemTypeID;
    private String ItemType;
}
