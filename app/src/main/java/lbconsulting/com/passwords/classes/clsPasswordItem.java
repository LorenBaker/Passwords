package lbconsulting.com.passwords.classes;

/**
 * Created by Loren on 3/8/2015.
 */
public class clsPasswordItem {

    private int ID;
    private String Name;
    private int ItemType_ID;
    private int User_ID;
    private String SoftwareKeyCode;
    private int SoftwareSubgroupLength;
    private String Comments;
    private String CreditCardAccountNumber;
    private String CreditCardSecurityCode;
    private String CreditCardExpirationMonth;
    private String CreditCardExpirationYear;
    private String GeneralAccountNumber;
    private String PrimaryPhoneNumber;
    private String AlternatePhoneNumber;
    private String WebsiteURL;
    private String WebsiteUserID;
    private String WebsitePassword;

    public clsPasswordItem() {

    }

    public clsPasswordItem(int passwordItemID, int userID) {
        ID = passwordItemID;
        User_ID = userID;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        String result = "";
        if (Name != null) {
            result = Name;
        }
        return result;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getItemType_ID() {
        return ItemType_ID;
    }

    public void setItemType_ID(int itemType_ID) {
        ItemType_ID = itemType_ID;
    }

    public int getUser_ID() {
        return User_ID;
    }

    public void setUser_ID(int user_ID) {
        User_ID = user_ID;
    }

    public String getSoftwareKeyCode() {
        String result = "";
        if (SoftwareKeyCode != null) {
            result = SoftwareKeyCode;
        }
        return result;
    }

    public void setSoftwareKeyCode(String softwareKeyCode) {
        SoftwareKeyCode = softwareKeyCode;
    }

    public int getSoftwareSubgroupLength() {
        return SoftwareSubgroupLength;
    }

    public void setSoftwareSubgroupLength(int softwareSubgroupLength) {
        SoftwareSubgroupLength = softwareSubgroupLength;
    }

    public String getComments() {
        String result = "";
        if (Comments != null) {
            result = Comments;
        }
        return result;
    }

    public void setComments(String comments) {
        Comments = comments;
    }

    public String getCreditCardAccountNumber() {
        String result = "";
        if (CreditCardAccountNumber != null) {
            result = CreditCardAccountNumber;
        }
        return result;
    }

    public void setCreditCardAccountNumber(String creditCardAccountNumber) {
        CreditCardAccountNumber = creditCardAccountNumber;
    }

    public String getCardCreditSecurityCode() {
        String result = "";
        if (CreditCardSecurityCode != null) {
            result = CreditCardSecurityCode;
        }
        return result;
    }

    public void setCreditCardSecurityCode(String cardCreditSecurityCode) {
        CreditCardSecurityCode = cardCreditSecurityCode;
    }

    public String getCreditCardExpirationMonth() {
        String result = "";
        if (CreditCardExpirationMonth != null) {
            result = CreditCardExpirationMonth;
        }
        return result;
    }

    public void setCreditCardExpirationMonth(String creditCardExpirationMonth) {
        CreditCardExpirationMonth = creditCardExpirationMonth;
    }

    public String getCreditCardExpirationYear() {

        String result = "";
        if (CreditCardExpirationYear != null) {
            result = CreditCardExpirationYear;
        }
        return result;
    }

    public void setCreditCardExpirationYear(String creditCardExpirationYear) {
        CreditCardExpirationYear = creditCardExpirationYear;
    }

    public String getGeneralAccountNumber() {
        String result = "";
        if (GeneralAccountNumber != null) {
            result = GeneralAccountNumber;
        }
        return result;
    }

    public void setGeneralAccountNumber(String generalAccountNumber) {
        GeneralAccountNumber = generalAccountNumber;
    }

    public String getPrimaryPhoneNumber() {
        String result = "";
        if (PrimaryPhoneNumber != null) {
            result = PrimaryPhoneNumber;
        }
        return result;
    }

    public void setPrimaryPhoneNumber(String primaryPhoneNumber) {
        PrimaryPhoneNumber = primaryPhoneNumber;
    }

    public String getAlternatePhoneNumber() {
        String result = "";
        if (AlternatePhoneNumber != null) {
            result = AlternatePhoneNumber;
        }
        return result;
    }

    public void setAlternatePhoneNumber(String alternatePhoneNumber) {
        AlternatePhoneNumber = alternatePhoneNumber;
    }

    public String getWebsiteURL() {
        String result = "";
        if (WebsiteURL != null) {
            result = WebsiteURL;
        }
        return result;
    }

    public void setWebsiteURL(String websiteURL) {
        WebsiteURL = websiteURL;
    }

    public String getWebsiteUserID() {
        String result = "";
        if (WebsiteUserID != null) {
            result = WebsiteUserID;
        }
        return result;
    }

    public void setWebsiteUserID(String websiteUserID) {
        WebsiteUserID = websiteUserID;
    }

    public String getWebsitePassword() {
        String result = "";
        if (WebsitePassword != null) {
            result = WebsitePassword;
        }
        return result;
    }

    public void setWebsitePassword(String websitePassword) {
        WebsitePassword = websitePassword;
    }

    @Override
    public String toString() {
        return Name;
    }

    public String getItemDetail() {
        StringBuilder sb = new StringBuilder();
        String formattedCreditCardNumber = "";
        clsFormattingMethods.creditCard card = null;
        switch (ItemType_ID) {
            case clsItemTypes.CREDIT_CARDS:
                if (CreditCardAccountNumber != null) {
                    card = clsFormattingMethods.getCreditCardType(CreditCardAccountNumber);
                }
                String cardType = "UNKNOWN";
                String formattedCardNumber = "";
                if (card != null) {
                    cardType = card.getCardType();
                    formattedCardNumber = card.getFormattedCardNumber();
                }

                sb.append(cardType).append(":\n").append(formattedCardNumber);
                if (CreditCardExpirationMonth != null && CreditCardExpirationYear != null) {
                    sb.append(System.getProperty("line.separator")).append("Expires: ").append(CreditCardExpirationMonth)
                            .append("/").append(CreditCardExpirationYear);
                }
                if (CreditCardSecurityCode != null) {
                    sb.append(System.getProperty("line.separator")).append("Security Code: ").append(CreditCardSecurityCode);
                }
                if (PrimaryPhoneNumber != null) {
                    String formattedPrimaryPhoneNumber = clsFormattingMethods.formatPhoneNumber(PrimaryPhoneNumber);
                    sb.append(System.getProperty("line.separator")).append("Primary: ").append(formattedPrimaryPhoneNumber);
                }
                if (AlternatePhoneNumber != null) {
                    String formattedAltPhoneNumber = clsFormattingMethods.formatPhoneNumber(AlternatePhoneNumber);
                    sb.append(System.getProperty("line.separator")).append("Alternate: ").append(formattedAltPhoneNumber);
                }
                break;

            case clsItemTypes.GENERAL_ACCOUNTS:
                sb.append("Account Number: ").append(GeneralAccountNumber);
                if (PrimaryPhoneNumber != null) {
                    String formattedPrimaryPhoneNumber = clsFormattingMethods.formatPhoneNumber(PrimaryPhoneNumber);
                    sb.append(System.getProperty("line.separator")).append("Primary: ").append(formattedPrimaryPhoneNumber);
                }
                if (AlternatePhoneNumber != null) {
                    String formattedAltPhoneNumber = clsFormattingMethods.formatPhoneNumber(AlternatePhoneNumber);
                    sb.append(System.getProperty("line.separator")).append("Alternate: ").append(formattedAltPhoneNumber);
                }
                break;

            case clsItemTypes.SOFTWARE:
                String formattedKeyCode = clsFormattingMethods.formatTypicalAccountNumber(SoftwareKeyCode, SoftwareSubgroupLength);
                sb.append("Software Key Code:\n").append(formattedKeyCode);
                break;
/*            case clsItemTypes.WEBSITES:
                break;*/

        }
        return sb.toString();
    }

    public String getWebsiteDetail() {
        StringBuilder sb = new StringBuilder();
        sb
                .append("URL:\n  ").append(WebsiteURL).append(System.getProperty("line.separator"))
                .append(System.getProperty("line.separator"))
                .append("User ID: ").append(WebsiteUserID).append(System.getProperty("line.separator"))
                .append("Password:\n  ").append(WebsitePassword);
        return sb.toString();
    }

}
