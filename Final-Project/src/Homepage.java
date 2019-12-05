import java.security.SecureRandom;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Pattern;


public class Homepage {
    public static void main(String[] arg) {

        String q = "";
        Scanner myScanner = new Scanner(System.in);
        String answer = "";
        int tf = 0;
        int endProg = 0;
        int exit = 0;
        char[] characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        do {
            tf = 0;
            System.out.println("Are you a ?");
            System.out.println("1: online customer?");
            System.out.println("2: inventory manager?");
            System.out.println("3: cashier?");
            System.out.println("4: Press 'X' to exit.");
            answer = myScanner.nextLine();
            List<String> list = Arrays.asList("1", "2", "3", "X", "online customer", "manager", "cashier");

            if (checkAnswer(list, answer) == 1) {
                tf = 1;
            }
        } while (tf == 1);

        if (answer.equals("3") || answer.equals("cashier")) {
            cashier_interface cashier = new cashier_interface();
                int login = cashier.getConnect();
                int withCus = cashier.withCustomer();
        } else if (answer.equals("1") || answer.equals("online customer")) {
            System.out.println("Our computer scientist is working on this one. Coming soon. Stay tuned!");
        } else if (answer.equals("2") || answer.toLowerCase().equals("inventory manager")) {
            InventoryManager iManager = new InventoryManager();
            iManager.getConnect();
            iManager.checkInventory();
        } else if (answer.toLowerCase().equals("x")) {
        }
        System.out.println("We hope you had a positive interaction.");
        System.out.println("For feedback, please email htb320@lehigh.edu");
        System.exit(0);
    }

    public static int checkAnswer(List<String> expect, String check) {
        for (int i = 0; i < expect.size(); i++) {
            if (check.toLowerCase().equals(expect.get(i).toLowerCase())) {
                return 0;
            }
        }
        System.out.println("Please enter the instructed choice. Try again.");
        return 1;
    }


    public static boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    public static boolean isDate(String date) {

        // some regular expression
        String time = "(\\s(([01]?\\d)|(2[0123]))[:](([012345]\\d)|(60))"
                + "[:](([012345]\\d)|(60)))?"; // with a space before, zero or one time

        // no check for leap years (Schaltjahr)
        // and 31.02.2006 will also be correct
        String day = "(([12]\\d)|(3[01])|(0?[1-9]))"; // 01 up to 31
        String month = "((1[012])|(0\\d))"; // 01 up to 12
        String year = "(\\d{2})";

        // define here all date format
        ArrayList<Pattern> patterns = new ArrayList<Pattern>();
        patterns.add(Pattern.compile(day + "[-.]" + month + "[-.]" + year + time));
        patterns.add(Pattern.compile(year + "-" + month + "-" + day + time));
        // here you can add more date formats if you want

        // check dates
        for (Pattern p : patterns)
            if (p.matcher(date).matches())
                return true;

        return false;

    }

    public static String randomString(int length) {
        char[] characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }

    public static boolean checkCardNumber(String card_num, int limit) {
        for (int i = 0; i < limit; i++) {
            if (!Character.isDigit(card_num.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public int checkExist(Statement s, String q, String name) {
        int tf = 0;
        try {
            ResultSet checkExist = s.executeQuery(q);

            checkExist.last();
            int rowNumber = checkExist.getRow();
            if (rowNumber < 1) {
                System.out.println("Invalid " + name + ".");
                tf = 1;
                return 0;
            } else {
                System.out.println("Valid " + name + ".");
                return 1;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public String generateUniqueID(Statement s, String from, String where) {
        String new_orderid = "";
        int checkrepeatedid = 0;
        String q = "";
        do {
            try {
                new_orderid = randomString(12);
                q = "select * from " + from + " where " + where + " = '" + new_orderid + "'";
                ResultSet checknewID = s.executeQuery(q);

                if (checknewID.next()) {
                    checkrepeatedid = 1;
                } else {
                    checkrepeatedid = 0;
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                System.out.println("Please contact technical department at htb320@lehigh.edu");
            }
        } while (checkrepeatedid == 1);
        return new_orderid;
    }
}
