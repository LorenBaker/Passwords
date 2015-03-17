package lbconsulting.com.passwords.classes;

import java.util.ArrayList;

/**
 * Created by Loren on 3/8/2015.
 */
public class clsLabPasswords {
    public ArrayList<clsUsers> getUsers() {
        return Users;
    }

    public void setUsers(ArrayList<clsUsers> mUsers) {
        this.Users = mUsers;
    }

    public ArrayList<clsItemTypes> getItemTypes() {
        return ItemTypes;
    }

    public void setItemTypes(ArrayList<clsItemTypes> mItemTypes) {
        this.ItemTypes = mItemTypes;
    }

    public ArrayList<clsPasswordItem> getPasswordItems() {
        return PasswordItems;
    }

    public void setPasswordItems(ArrayList<clsPasswordItem> mPasswordItems) {
        this.PasswordItems = mPasswordItems;
    }

    private ArrayList<clsUsers> Users = new ArrayList<>();
    private ArrayList<clsItemTypes> ItemTypes = new ArrayList<>();
    private ArrayList<clsPasswordItem> PasswordItems = new ArrayList<>();

    public clsUsers getUser(int userID) {
        clsUsers result = null;
        for (clsUsers user : Users) {
            if (user.getUserID() == userID) {
                result = user;
                break;
            }
        }
        return result;
    }
}
