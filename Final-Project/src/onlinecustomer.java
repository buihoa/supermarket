import java.io.*;
import java.security.SecureRandom;
import java.sql.*;
import java.util.*;
import java.lang.Integer;

public class onlinecustomer extends Homepage{
    public static void main(String[] arg) {
        Connection con = null;
        String q = "";
        Scanner myScanner = new Scanner(System.in);
        String account_id = "";
        char[] characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
        int tf = 0;
        int more = 0;

        do {
            try {
                System.out.println("Enter the userid: ");
                String userid = myScanner.nextLine();
                System.out.println("Enter the password: ");
                String pw = myScanner.nextLine();
                tf = 0;

                con = DriverManager.getConnection("jdbc:oracle:thin:@edgar0.cse.lehigh.edu:1521:cse241", userid, pw);
                break;
            } catch (Exception e) {
                tf = 1;
                System.out.println("You have entered wrong ID/password. Please try again.");
            }
        } while (tf == 1);


        try (
                Statement s = con.createStatement();
        ) {
            more = 0;
            System.out.println("Enter your account_id: ");
            account_id = myScanner.nextLine();

            q = "SELECT * FROM onlinestore WHERE account_id = '" + account_id + "'";

            String answer = "";
            ResultSet resultC = s.executeQuery(q);
            if (!resultC.next()) {
                do {
                    System.out.println("Wrong account id.");
                    System.out.println("1. Register.");
                    System.out.println("2. Forgot username.");
                    System.out.println("3. Exist.");
                    answer = myScanner.nextLine();

                    List<String> list = Arrays.asList("1", "2", "3");
                    if (checkAnswer(list, answer) == 1) {
                        tf = 1;
                        continue;
                    }
                } while (tf == 1);
                if(answer.equals("1")){

                }
            }

            s.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int checkAnswer(List<String> expect, String check) {
        for (int i = 0; i < expect.size(); i++) {
            if (check.equals(expect.get(i))) {
                return 0;
            }
        }
        System.out.println("Please enter the instructed choice. Try again.");
        return 1;
    }
    public static String randomString(char[] characterSet, int length) {
        Random random = new SecureRandom();
        char[] result = new char[length];
        for (int i = 0; i < result.length; i++) {
            // picks a random index out of character set > random character
            int randomCharIndex = random.nextInt(characterSet.length);
            result[i] = characterSet[randomCharIndex];
        }
        return new String(result);
    }

}
