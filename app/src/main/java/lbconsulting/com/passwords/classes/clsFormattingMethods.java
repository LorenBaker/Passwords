package lbconsulting.com.passwords.classes;

import android.widget.Spinner;

import com.dropbox.sync.android.DbxException;
import com.dropbox.sync.android.DbxFile;
import com.dropbox.sync.android.DbxFileInfo;
import com.dropbox.sync.android.DbxFileSystem;
import com.dropbox.sync.android.DbxPath;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Loren on 3/10/2015.
 */
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

    // This method accepts two strings the represent two files to
    // compare. A returns true if the contents of the files
    // are the same. A returns false if the files are not the same.
    public static boolean FileCompare(DbxFileSystem dbxFs, DbxFile file1, String filename2) {

        try {
            // check for the dropbox file
            DbxPath file2Path = new DbxPath(filename2);

            if (!dbxFs.isFile(file2Path)) {
                MyLog.e("clsFormattingMethods", "FileCompare: Source file: " + filename2 + " not found.");
                return false;
            }

            // Open file2
            DbxFile file2 = dbxFs.open(file2Path);

            if (file1 != null && file2 != null) {
                DbxFileInfo file1Info = file1.getInfo();
                DbxFileInfo file2Info = file2.getInfo();
                MyLog.d("clsFormattingMethods", "FileCompare: file1 size = "+file1Info.size +" ;file " + filename2 + " size = " +file2Info.size);

                // Check the file sizes. If they are not the same, the files
                // are not the same.
                if (file1Info.size != file2Info.size) {
                    // Close file2
                    if (file2 != null) {
                        file2.close();
                    }

                    // Return false to indicate files are different
                    return false;
                }

                // Read and compare a byte from each file until either a
                // non-matching set of bytes is found or until the end of
                // file1 is reached.
                FileInputStream file1Stream = file1.getReadStream();
                FileInputStream file2Stream = file2.getReadStream();
                int read = -1;
                byte[] file1Buffer = new byte[1];
                byte[] file2Buffer = new byte[1];
                boolean result = true;
                int position =0;
                while ((read = file1Stream.read(file1Buffer)) != -1) {
                    file2Stream.read(file2Buffer);
                    if (file1Buffer[0] != file2Buffer[0]) {
                        MyLog.d("clsFormattingMethods", "FileCompare: Bytes not equal at position = " + position);
                        result = false;
                        break;
                    }
                    position++;
                }

                // close the file streams
                file1Stream.close();
                file2Stream.close();

                // close file2 ... file1 stays open, it is the Passwords data file

                if (file2 != null) {
                    file2.close();
                }

                if (result) {
                    result = ((file1Buffer[0] == file2Buffer[0]));
                }
                MyLog.d("clsFormattingMethods", "FileCompare: files the same: " + result);
                return result;

            } else {
                MyLog.e("clsFormattingMethods", "FileCompare: unable to open one or both files!");
                return false;
            }
        } catch (DbxException e) {
            MyLog.e("clsFormattingMethods", "FileCompare: DbxException: " + e.toString());
            e.printStackTrace();
            return false;

        } catch (IOException e) {
            MyLog.e("clsFormattingMethods", "FileCompare: IOException: " + e.toString());
            e.printStackTrace();
            return false;
        }
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
