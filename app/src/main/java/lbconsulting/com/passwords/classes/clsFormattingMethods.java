package lbconsulting.com.passwords.classes;

import android.widget.Spinner;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import lbconsulting.com.passwords.activities.MainActivity;

public class clsFormattingMethods {

    public static String unformatKeyCode(String keyCode) {
        String unformattedKeycode = keyCode.replace("-", "");
        unformattedKeycode = unformattedKeycode.replace(" ", "");
        return unformattedKeycode;
    }

    public static class creditCard {
        private String cardType = MySettings.UNKNOWN;
        private int cardPosition = Spinner.INVALID_POSITION;
        private String formattedCardNumber = "";

        public String getCardType() {
            return cardType;
        }

        public void setCardType(String cardType) {
            this.cardType = cardType;
        }

        public String getFormattedCardNumber() {
            return formattedCardNumber;
        }

        public void setFormattedCardNumber(String formattedCardNumber) {
            this.formattedCardNumber = formattedCardNumber;
        }

        public int getCardPosition() {
            return cardPosition;
        }

        public void setCardPosition(int cardPosition) {
            this.cardPosition = cardPosition;
        }

        public creditCard() {

        }
    }

    public static creditCard getCreditCardType(String creditCardNumber) {
        creditCard card = new creditCard();
        if (creditCardNumber != null && !creditCardNumber.isEmpty()) {
            if (isVISACard(creditCardNumber)) {
                card.setCardPosition(MySettings.VISA);
                card.setCardType(MySettings.CreditCardNames[MySettings.VISA]);
                card.setFormattedCardNumber(formatTypicalAccountNumber(creditCardNumber, 4));
            } else if (isMasterCard(creditCardNumber)) {
                card.setCardPosition(MySettings.MASTERCARD);
                card.setCardType(MySettings.CreditCardNames[MySettings.MASTERCARD]);
                card.setFormattedCardNumber(formatTypicalAccountNumber(creditCardNumber, 4));
            } else if (isAmericanExpress(creditCardNumber)) {
                card.setCardPosition(MySettings.AMERICAN_EXPRESS);
                card.setCardType(MySettings.CreditCardNames[MySettings.AMERICAN_EXPRESS]);
                card.setFormattedCardNumber(formatAmericanExpress(creditCardNumber));
            } else if (isDiscoverCard(creditCardNumber)) {
                card.setCardPosition(MySettings.DISCOVER);
                card.setCardType(MySettings.CreditCardNames[MySettings.DISCOVER]);
                card.setFormattedCardNumber(formatTypicalAccountNumber(creditCardNumber, 4));
            } else if (isDinersClubCard(creditCardNumber)) {
                card.setCardPosition(MySettings.DINERS_CLUB);
                card.setCardType(MySettings.CreditCardNames[MySettings.DINERS_CLUB]);
                card.setFormattedCardNumber(formatDinersClub(creditCardNumber));
            } else if (isJCBCard(creditCardNumber)) {
                card.setCardPosition(MySettings.JCB);
                card.setCardType(MySettings.CreditCardNames[MySettings.JCB]);
                card.setFormattedCardNumber(formatTypicalAccountNumber(creditCardNumber, 4));
            } else {
                card.setCardPosition(Spinner.INVALID_POSITION);
                card.setCardType(MySettings.UNKNOWN);
                card.setFormattedCardNumber(creditCardNumber);
            }
        }
        return card;
    }


    private static final String VISACreditCardPattern = "^4[0-9]{12}(?:[0-9]{3})?$";

    private static boolean isVISACard(String creditCardNumber) {
        Pattern pattern = Pattern.compile(VISACreditCardPattern);
        Matcher matcher = pattern.matcher(creditCardNumber);
        return matcher.find();
    }

    private static final String MasterCardCreditCardPattern = "^5[1-5][0-9]{14}$";

    private static boolean isMasterCard(String creditCardNumber) {

        Pattern pattern = Pattern.compile(MasterCardCreditCardPattern);
        Matcher matcher = pattern.matcher(creditCardNumber);
        return matcher.find();
    }

    private static final String AmericanExpressCreditCardPattern = "^3[47][0-9]{13}$";

    private static boolean isAmericanExpress(String creditCardNumber) {
        Pattern pattern = Pattern.compile(AmericanExpressCreditCardPattern);
        Matcher matcher = pattern.matcher(creditCardNumber);
        return matcher.find();
    }

    private static final String DiscoverCreditCardPattern = "^6(?:011|5[0-9]{2})[0-9]{12}$";

    private static boolean isDiscoverCard(String creditCardNumber) {
        Pattern pattern = Pattern.compile(DiscoverCreditCardPattern);
        Matcher matcher = pattern.matcher(creditCardNumber);
        return matcher.find();
    }

    private static final String DinersClubCreditCardPattern = "^3(?:0[0-5]|[68][0-9])[0-9]{11}$";

    private static boolean isDinersClubCard(String creditCardNumber) {
        Pattern pattern = Pattern.compile(DinersClubCreditCardPattern);
        Matcher matcher = pattern.matcher(creditCardNumber);
        return matcher.find();
    }

    private static final String JCBCreditCardPattern = "^(?:2131|1800|35\\d{3})\\d{11}$";

    private static boolean isJCBCard(String creditCardNumber) {
        Pattern pattern = Pattern.compile(JCBCreditCardPattern);
        Matcher matcher = pattern.matcher(creditCardNumber);
        return matcher.find();
    }

    public static boolean luhnTest(String number) {

        if (number == null || number.isEmpty()) {
            return false;
        }
        // source: http://rosettacode.org/wiki/Luhn_test_of_credit_card_numbers
        int s1 = 0, s2 = 0;
        String reverse = new StringBuffer(number).reverse().toString();
        for (int i = 0; i < reverse.length(); i++) {
            int digit = Character.digit(reverse.charAt(i), 10);
            if (i % 2 == 0) {//this is for odd digits, they are 1-indexed in the algorithm
                s1 += digit;
            } else {//add 2 * digit for 0-4, add 2 * digit - 9 for 5-9
                s2 += 2 * digit;
                if (digit >= 5) {
                    s2 -= 9;
                }
            }
        }
        return (s1 + s2) % 10 == 0;
    }

    public static String formatPhoneNumber(String unformattedPhoneNumber) {
        unformattedPhoneNumber = unFormatPhoneNumber((unformattedPhoneNumber));

        String formattedPhoneNumber = unformattedPhoneNumber;
        String areaCode;
        String exchange;
        String subscriber;
        switch (unformattedPhoneNumber.length()) {

            case 7:
                exchange = unformattedPhoneNumber.substring(0, 3);
                subscriber = unformattedPhoneNumber.substring(3, 7);
                formattedPhoneNumber = exchange + "-" + subscriber;
                break;

            case 10:
                areaCode = unformattedPhoneNumber.substring(0, 3);
                exchange = unformattedPhoneNumber.substring(3, 6);
                subscriber = unformattedPhoneNumber.substring(6, 10);
                formattedPhoneNumber = "(" + areaCode + ") " + exchange + "-" + subscriber;
                break;

            case 11:
                if (unformattedPhoneNumber.startsWith("1")) {
                    areaCode = unformattedPhoneNumber.substring(1, 4);
                    exchange = unformattedPhoneNumber.substring(4, 7);
                    subscriber = unformattedPhoneNumber.substring(7, 11);
                    formattedPhoneNumber = "(" + areaCode + ") " + exchange + "-" + subscriber;
                }
                break;

        }

        return formattedPhoneNumber;
    }

    public static String unFormatPhoneNumber(String formattedPhoneNumber) {
        String unformattedPhoneNumber = formattedPhoneNumber.trim();
        if (unformattedPhoneNumber.isEmpty()) {
            return "";
        }
        unformattedPhoneNumber = unformattedPhoneNumber.replace(" ", "");
        unformattedPhoneNumber = unformattedPhoneNumber.replace("-", "");
        unformattedPhoneNumber = unformattedPhoneNumber.replace("(", "");
        unformattedPhoneNumber = unformattedPhoneNumber.replace(")", "");
        unformattedPhoneNumber = unformattedPhoneNumber.replace(".", "");

        switch (unformattedPhoneNumber.length()) {
            case 7:
            case 10:
                return unformattedPhoneNumber;

            case 11:
                if (unformattedPhoneNumber.startsWith("1")) {
                    unformattedPhoneNumber = unformattedPhoneNumber.substring(1, 10);
                    return unformattedPhoneNumber;
                } else {
                    unformattedPhoneNumber = unformattedPhoneNumber.substring(0, 9);
                    return unformattedPhoneNumber;
                }

            default:
                int numberLength = unformattedPhoneNumber.length();
                if (numberLength > 9) {
                    numberLength = 9;
                }
                unformattedPhoneNumber = unformattedPhoneNumber.substring(0, 9);
                return unformattedPhoneNumber;
        }

    }

    public static String formatTypicalAccountNumber(String accountNumber, int subGroupLength) {
        String formattedNumber = accountNumber;
        if (subGroupLength < 1) {
            subGroupLength = 1;
        }

        // clean up the provided accountNumber
        accountNumber = accountNumber.trim();
        accountNumber = accountNumber.replace("-", "");
        accountNumber = accountNumber.replace(" ", "");
        String dash = "\u2013";
        accountNumber = accountNumber.replace(dash, "");

        if (accountNumber.isEmpty()) {
            formattedNumber = "";
        } else {
            int end = subGroupLength;
            if (end > accountNumber.length()) {
                end = accountNumber.length();
            }
            formattedNumber = accountNumber.substring(0, end);
            for (int i = subGroupLength; i < accountNumber.length(); i = i + subGroupLength) {
                end = i + subGroupLength;
                if (end > accountNumber.length()) {
                    end = accountNumber.length();
                }
                formattedNumber = formattedNumber + dash + accountNumber.substring(i, end);
            }
        }

        return formattedNumber;
    }

    private static String formatAmericanExpress(String accountNumber) {
        String formattedNumber = accountNumber;
        accountNumber = accountNumber.trim();
        if (accountNumber.isEmpty()) {
            formattedNumber = "";
        } else {
            if (accountNumber.length() == 15) {
                formattedNumber = accountNumber.substring(0, 4);
                formattedNumber = formattedNumber + "-" + accountNumber.substring(4, 10);
                formattedNumber = formattedNumber + "-" + accountNumber.substring(10, 15);
            }
        }

        return formattedNumber;
    }

    private static String formatDinersClub(String accountNumber) {
        String formattedNumber = accountNumber;
        accountNumber = accountNumber.trim();
        if (accountNumber.isEmpty()) {
            formattedNumber = "";
        } else {
            if (accountNumber.length() == 14) {
                formattedNumber = accountNumber.substring(0, 4);
                formattedNumber = formattedNumber + "-" + accountNumber.substring(4, 10);
                formattedNumber = formattedNumber + "-" + accountNumber.substring(10, 14);
            }
        }

        return formattedNumber;
    }

    // This method compares the provided decrypted Passwords file clsLabPasswords content
    // with the backup file clsLabPasswords content.
    // Returns true if the decrypted contents of the files are the same.
    // Returns false if the files are not the same.
    public static boolean passwordsFileCompare(DbxFileSystem dbxFs, String key, clsLabPasswords passwordsObject, String backupFilename) {

        String backupFileEncryptedContents;
        try {
            // verify that the backup file exists ... if not, return false
            DbxPath backupFilenamePath = new DbxPath(backupFilename);
            if (!dbxFs.isFile(backupFilenamePath)) {
                MyLog.i("clsFormattingMethods", "passwordsFileCompare: " + backupFilename + " file not found... returning false");
                return false;
            }

            // Open the backup file
            DbxFile backupFile;
            backupFile = dbxFs.open(backupFilenamePath);

            // Read the backup file
            if (backupFile == null) {
                // Unable to open the backup file ... return false
                MyLog.e("clsFormattingMethods", "passwordsFileCompare: Unable to open the backup file... returning false");
                return false;
            }
            backupFileEncryptedContents = backupFile.readString();
            backupFile.close();

        } catch (IOException e) {
            MyLog.e("clsFormattingMethods", "passwordsFileCompare: IOException: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        // Decrypt the backup file
        if (backupFileEncryptedContents.isEmpty()) {
            // there is nothing to decrypt .... return false
            MyLog.e("clsFormattingMethods", "passwordsFileCompare: backup file encrypted content is empty... returning false");
            return false;
        }
        String decryptedBackupFileContents;
        try {
            CryptLib mCrypt = new CryptLib();
            String iv = backupFileEncryptedContents.substring(0, 16);
            String encryptedBackupFileContentsWithoutIv = backupFileEncryptedContents.substring(16);
            decryptedBackupFileContents = mCrypt.decrypt(encryptedBackupFileContentsWithoutIv, key, iv);
            decryptedBackupFileContents = decryptedBackupFileContents.trim();

        } catch (NoSuchAlgorithmException e) {
            MyLog.e("clsFormattingMethods", "passwordsFileCompare: NoSuchAlgorithmException: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (NoSuchPaddingException e) {
            MyLog.e("clsFormattingMethods", "passwordsFileCompare: NoSuchPaddingException: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (InvalidKeyException e) {
            MyLog.e("clsFormattingMethods", "passwordsFileCompare: InvalidKeyException: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (InvalidAlgorithmParameterException e) {
            MyLog.e("clsFormattingMethods", "passwordsFileCompare: InvalidAlgorithmParameterException: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (IllegalBlockSizeException e) {
            MyLog.e("clsFormattingMethods", "passwordsFileCompare: IllegalBlockSizeException: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (BadPaddingException e) {
            MyLog.e("clsFormattingMethods", "passwordsFileCompare: BadPaddingException: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (UnsupportedEncodingException e) {
            MyLog.e("clsFormattingMethods", "passwordsFileCompare: UnsupportedEncodingException: " + e.getMessage());
            e.printStackTrace();
            return false;
        }

        // Create clsLabPasswords object from the backup file
        Gson gson = new Gson();
        clsLabPasswords backupFilePasswordsObject = null;
        try {
            backupFilePasswordsObject = gson.fromJson(decryptedBackupFileContents, clsLabPasswords.class);
        } catch (JsonSyntaxException e) {
            MyLog.e("clsFormattingMethods", "passwordsFileCompare: JsonSyntaxException: " + e.getMessage());
            e.printStackTrace();
        }

        boolean result = false;
        if (passwordsObject != null && backupFilePasswordsObject != null) {
            // Sort the backup file object
            if (backupFilePasswordsObject.getPasswordItems().size() > 0) {
                Collections.sort(backupFilePasswordsObject.getPasswordItems(), new MainActivity.sortPasswordItems());
            }

            if (backupFilePasswordsObject.getUsers().size() > 0) {
                Collections.sort(backupFilePasswordsObject.getUsers(), new MainActivity.sortUsers());
            }

            // Compare the password items size
            if (passwordsObject.getPasswordItems().size() == backupFilePasswordsObject.getPasswordItems().size()) {
                // Compare the users items size
                if (passwordsObject.getUsers().size() == passwordsObject.getUsers().size()) {
                    // Compare the user lists
                    if (compareUsers(passwordsObject.getUsers(), passwordsObject.getUsers())) {
                        // Compare the password item lists
                        result = comparePasswordItems(passwordsObject.getPasswordItems(), backupFilePasswordsObject.getPasswordItems());
                    }
                }
            }
        }

        MyLog.i("clsFormattingMethods", "passwordsFileCompare result = " + result);
        return result;
    }


    private static boolean comparePasswordItems(ArrayList<clsPasswordItem> passwordsItemsList1,
                                                ArrayList<clsPasswordItem> passwordsItemsList2) {
        boolean result = false;
        int index = 0;

        for (clsPasswordItem item1 : passwordsItemsList1) {
            clsPasswordItem item2 = passwordsItemsList2.get(index);
            if (item1.getID() != item2.getID()) {
                MyLog.e("clsFormattingMethods", "comparePasswordItems: password item IDs are NOT the same! index = "
                        + index + "; item1 ID = " + item1.getID() + "; item2 ID = " +item2.getID()
                        + "; item1 Name = " + item1.getName() + "; item 2 Name = " +item2.getName());
                break;
            }

            if (!item1.getAlternatePhoneNumber().equals(item2.getAlternatePhoneNumber())) break;
            if (!item1.getComments().equals(item2.getComments())) break;
            if (!item1.getCreditCardAccountNumber().equals(item2.getCreditCardAccountNumber()))
                break;
            if (!item1.getCreditCardExpirationMonth().equals(item2.getCreditCardExpirationMonth()))
                break;
            if (!item1.getCreditCardExpirationYear().equals(item2.getCreditCardExpirationYear()))
                break;
            if (!item1.getCardCreditSecurityCode().equals(item2.getCardCreditSecurityCode())) break;
            if (!item1.getGeneralAccountNumber().equals(item2.getGeneralAccountNumber())) break;
            if (item1.getItemType_ID() != item2.getItemType_ID()) break;
            if (!item1.getName().equals(item2.getName())) break;
            if (!item1.getPrimaryPhoneNumber().equals(item2.getPrimaryPhoneNumber())) break;
            if (!item1.getSoftwareKeyCode().equals(item2.getSoftwareKeyCode())) break;
            if (item1.getSoftwareSubgroupLength() != item2.getSoftwareSubgroupLength()) break;
            if (item1.getUser_ID() != item2.getUser_ID()) break;
            if (!item1.getWebsitePassword().equals(item2.getWebsitePassword())) break;
            if (!item1.getWebsiteURL().equals(item2.getWebsiteURL())) break;
            if (!item1.getWebsiteUserID().equals(item2.getWebsiteUserID())) break;

            index++;
        }

        if (index == passwordsItemsList1.size()) {
            // All password items examined. The two password item lists are the same.
            result = true;
        }
        return result;
    }


    private static boolean compareUsers(ArrayList<clsUsers> userList1,
                                        ArrayList<clsUsers> userList2) {
        boolean result = false;
        int index = 0;

        for (clsUsers user1 : userList1) {
            clsUsers user2 = userList2.get(index);
            if (user1.getUserID() != user2.getUserID()) {
                MyLog.e("clsFormattingMethods", "compareUsers: user IDs are NOT the same!");
                break;
            }

            if (!user1.getUserName().equals(user2.getUserName())) {
                break;
            }
            index++;
        }

        if (index == userList1.size()) {
            // All users examined. The two user lists are the same.
            result = true;
        }
        return result;

    }

    public static boolean FileCopy(DbxFileSystem dbxFs, String sourceFilename, String destinationFilename) {
        // Determine if the same file was referenced two times.
        if (sourceFilename.toUpperCase().equals(destinationFilename.toUpperCase())) {
            // Return false ... did not copy the file
            return false;
        }

        try {
            DbxPath sourceFilePath = new DbxPath(sourceFilename);

            if (!dbxFs.isFile(sourceFilePath)) {
                MyLog.i("clsFormattingMethods", "FileCopy: Source file = " + sourceFilename + " not found.");
                return false;
            }

            // Open source file
            DbxFile sourceFile = dbxFs.open(sourceFilePath);
            return FileCopy(dbxFs, sourceFile, destinationFilename, true);

        } catch (DbxException e) {
            MyLog.e("clsFormattingMethods", "FileCopy(string, string): DbxException" + e.getMessage());
            e.printStackTrace();
            return false;
        }

    }

    public static boolean FileCopy(DbxFileSystem dbxFs, DbxFile sourceFile, String destinationFilename, boolean closeSourceFile) {

        DbxFile destinationFile = null;

        try {
            // check for the dropbox file
            DbxPath destinationFilePath = new DbxPath(destinationFilename);

            // check if the destination file exists ... if so, delete it.
            if (dbxFs.isFile(destinationFilePath)) {
                MyLog.i("clsFormattingMethods", "FileCopy: DELETING existing destination file: " + destinationFilename);
                dbxFs.delete(destinationFilePath);
            }

            // Create then Open the destination file
            destinationFile = dbxFs.create(destinationFilePath);
            //destinationFile = dbxFs.open(destinationFilePath);

            // get input and output streams
            FileInputStream sourceFileInputStream = sourceFile.getReadStream();
            FileOutputStream destinationFileOutputStream = destinationFile.getWriteStream();

            // copy the file
            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = sourceFileInputStream.read(buf)) > 0) {
                destinationFileOutputStream.write(buf, 0, bytesRead);
            }

            // close the input and output streams
            sourceFileInputStream.close();
            destinationFileOutputStream.close();

            DbxFileInfo destinationFileInfo = destinationFile.getInfo();
            String msg = "backup file: " + destinationFilename + " written: size = " + destinationFileInfo.size;
            MyLog.i("clsFormattingMethods", "FileCopy: " + msg);

            return true;

        } catch (DbxException e) {
            MyLog.e("clsFormattingMethods", "FileCopy: DbxException: " + e.toString());
            e.printStackTrace();
            return false;

        } catch (IOException e) {
            MyLog.e("clsFormattingMethods", "FileCopy: IOException: " + e.toString());
            e.printStackTrace();
            return false;

        } finally {
            // Close the files
            if (sourceFile != null && closeSourceFile) {
                sourceFile.close();
            }
            if (destinationFile != null) {
                destinationFile.close();
            }
        }
    }
}
