package lbconsulting.com.passwords.classes;

/**
 * Created by Loren on 3/8/2015.
 */
public class clsUsers {

    private int UserID;
    private String UserName;
    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    @Override
    public String toString() {
        return UserName;
    }
}
