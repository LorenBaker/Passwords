package lbconsulting.com.passwords.classes;

import android.widget.Spinner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Loren on 3/10/2015.
 */
public class clsFormattingMethods {

/*    public static String formatKeyCode(String keyCode, int spacing) {
        String unformatKeyCode = unformatKeyCode(keyCode);
        String formattedKeyCode = "";
        int start = 0;
        int end = spacing;
        if (unformatKeyCode.length() < spacing) {
            end = unformatKeyCode.length();
            formattedKeyCode = unformatKeyCode;
        } else {
            formattedKeyCode = unformatKeyCode.substring(start, end);
            for (int i = spacing; i < unformatKeyCode.length(); i += spacing) {
                start = i;
                end = start + spacing;
                if (end > unformatKeyCode.length()) {
                    end = unformatKeyCode.length();
                }
                formattedKeyCode = formattedKeyCode +" - "+ unformatKeyCode.substring(start, end);
            }
        }
        return formattedKeyCode;
    }*/

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
            formattedNumber = accountNumber.substring(0, subGroupLength);
            int end;
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
}
