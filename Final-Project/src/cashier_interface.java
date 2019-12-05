import java.sql.*;
import java.util.Scanner;
import java.lang.Integer;
import java.util.*;

public class cashier_interface extends Homepage {
    Connection con = null;
    private String cashier_name;
    private String startWorkDate;
    private String cashier_gender;

    public String getCashier_gender() {
        return this.cashier_gender;
    }

    public String getCashier_name() {
        return this.cashier_name;
    }

    public String getStartWorkDate() {
        return this.startWorkDate;
    }

    private String setCashier_name(String name) {
        this.cashier_name = name;
        return cashier_name;
    }

    private String setCashier_gender(String cashier_gender) {
        this.cashier_gender = cashier_gender;
        return cashier_gender;
    }

    private String setCashier_startWorkDate(String startWorkDate) {
        this.startWorkDate = startWorkDate;
        return this.startWorkDate;
    }

    public void getInfo() {
        System.out.printf("%1$-30s %2$-10s %3$-10s %4$-20s \n", "Employee Name", "Role", "Gender", "Start Working Date");
        System.out.printf("%1$-30s %2$-10s %3$-10s %4$-20s \n", "Hoa Bui", "Cashier", "Female", "Spring 2018");
    }

    public int getConnect() {
        Scanner myScanner = new Scanner(System.in);
        int tf = 0;
        String userid = "";
        String answer = "";
        String pw = "";
        do {
            try {
                tf = 0;
                System.out.println("Enter your cashier username: ");
                userid = myScanner.nextLine();
                System.out.println("Enter your password: ");
                pw = myScanner.nextLine();

                con = DriverManager.getConnection("jdbc:oracle:thin:@edgar0.cse.lehigh.edu:1521:cse241", userid, pw);
                break;
            } catch (Exception e) {
                tf = 1;
                System.out.println("You have entered wrong ID/password. Please try again.");
                System.out.println("Press 'X' to exit. Any other to re-try.");
                answer = myScanner.nextLine();
                if (answer.toLowerCase().equals("x")) {
                    System.out.println("Logged out.");
                    return 1;
                }
            }
        } while (tf == 1);
        System.out.println("You have successfully connected!");
        return 0; //
    }

    public int withCustomer() {

        String q = "";
        Scanner myScanner = new Scanner(System.in);
        int tf = 0;
        String answer = "";
        String userid = "";
        String pw = "";
        int endProg = 0;
        try (
                Statement s = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
        ) {
            while (endProg == 0) {
                do {
                    tf = 0;
                    System.out.println();
                    System.out.println("1: Get your profile.");
                    System.out.println("2: Site for customers.");
                    System.out.println("3: Exit.");
                    answer = myScanner.nextLine();
                    List<String> list = Arrays.asList("1", "2", "3", "4");

                    if (super.checkAnswer(list, answer) == 1) {
                        tf = 1;
                        continue;
                    }
                } while (tf == 1);
                if (answer.equals("1")) {
                    getInfo();
                    endProg = 0;
                } else if (answer.equals("2")) {
                    int retval = orderBuy(con);
                    if (retval == 111) {
                        continue;
                    }
                    else if (retval == 112) {
                        return 112;
                    }
                    else {
                        endProg = 0;
                        continue;
                    }
                } else if (answer.equals("3")) {
                    System.out.println("Bye. Have a good day! We are happy you are here to take care of our customers!");
                    s.close();
                    con.close();
                    return 112;
                }
            }
            s.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    // Frequent Mem
    public String frequentWanted() {
        String frequent_mem = "";
        int tf = 0;
        Scanner myScanner = new Scanner(System.in);

        do {
            tf = 0;

            System.out.println("Is frequent membership wanted? (Y/N)");
            frequent_mem = myScanner.nextLine();
            frequent_mem.toLowerCase();

            List<String> list = Arrays.asList("yes", "no", "y", "n");
            if (super.checkAnswer(list, frequent_mem) == 1) {
                System.out.println("Only Yes/Y/No/N are accepted. Try again.");
                tf = 1;
                continue;
            }
        } while (tf == 1);

        if (frequent_mem.toLowerCase().equals("yes") || frequent_mem.toLowerCase().equals("y")) {
            return "y";
        }
        return "n";
    }

    public int orderBuy(Connection con) {
        int goBack = 0;
        String q = "";
        try (
                Statement s = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
        ) {
            int more = 0;
            String inputcus_id = "";
            int tf = 0;
            Scanner myScanner = new Scanner(System.in);
            String answer = "";
            String frequent_mem = "";
            int newCus = 1;

            int checkrepeatedid = 0;
            do {
                tf = 0;
                System.out.println();
                System.out.println("Enter cus_id: ");
                inputcus_id = myScanner.nextLine();

                q = "SELECT * FROM customer WHERE cus_id = '" + inputcus_id + "'";

                if (super.checkExist(s, q, "cusomter id") == 0) {
                    do {
                        tf = 0;
                        System.out.println("This is a new customer. Create a new profile.");
                        System.out.println("Create a new profile? (Y/N)?");
                        answer = myScanner.nextLine();

                        List<String> list = Arrays.asList("yes", "y", "no", "n");
                        if (super.checkAnswer(list, answer) == 1) {
                            tf = 1;
                        }

                        if (answer.toLowerCase().equals("y") || answer.toLowerCase().equals("yes")) {
                            System.out.println("This is a new customer. Create a new profile.");
                            inputcus_id = surveyNewCus(s);
                        }
                    }while(tf == 1);

                } else {
                    newCus = 0;
                    System.out.println("This Customer ID is In DataBase.");
                }
                //Deciding which customer to deal with
                int retval = direction(inputcus_id);
                if (retval == 111) {
                    tf = 1;
                    continue;
                } else if (retval == 112) {
                } else {
                    return 113;
                }
            } while (tf == 1);

            do {
                tf = 0;
                System.out.println();
                System.out.println("1: Check information.");
                System.out.println("2: Record an order.");
                System.out.println("Press 'B' to be back to Main Customer Site for another customer");
                System.out.println("Press 'X' to exit customer site.");
                answer = myScanner.nextLine();

                goBack = goBack(answer);
                if (goBack != 0) {
                    return goBack;
                }

                List<String> list = Arrays.asList("1", "2");
                if (super.checkAnswer(list, answer) == 1) {
                    tf = 1;
                }

                if (answer.toLowerCase().equals("1")) {
                    getCusInfo(s, inputcus_id);
                } else if (answer.toLowerCase().equals("2")) {
                    int retval = recordOrder(s, inputcus_id);
                    if(retval == -1){
                        return -1;
                    }
                }
            } while (tf == 0);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public int recordOrder(Statement s, String inputcus_id) {
        String answer = "";
        int tf = 0;
        Scanner myScanner = new Scanner(System.in);
        String q = "";
        int checkrepeatedid = 0;
        int endOrder = 0;
        String input_storeid = "";
        String input_prodid = "";
        String cordered_amount = "";
        int ordered_amount = 0;
        double price = 0;
        double total = 0;
        String delivery_method = "Pick-up";
        String ship_status = "Pending";
        int back = 0;
        int anotherProd = 0;
        double totalAmount = 0;

        String new_orderid = super.generateUniqueID(s, "cart", "order_id");

        do {
            tf = 0;
            System.out.println();
            printGoBack();
            System.out.println("Enter your store id: ");

            input_storeid = myScanner.nextLine().toLowerCase();

            back = goBack(input_storeid);
            if (back != 0) {
                return back;
            }

            q = "SELECT * FROM store WHERE store_id = '" + input_storeid + "'";

            if (super.checkExist(s, q, "store ID.") == 0 || input_prodid.equals("x2lg4nn8lklr")) {
                System.out.println("You have entered a wrong physical store ID.");
                System.out.println("Online store ID is x2lg4nn8lklr. Keep in mind.");
                System.out.println("Otherwise, this store ID does not exist.");
                tf = 1;
            }
        } while (tf == 1);

        do {
            anotherProd = 0;
            int cur_amount = 0;
            int more = 0;
            do {
                more = 1;
                do {
                    tf = 0;
                    System.out.println();
                    printGoBack();
                    System.out.println("Press 'E' to end the order to check out and print receipt.");
                    System.out.println("Enter product id: ");

                    input_prodid = myScanner.nextLine().toLowerCase();

                    if (input_prodid.toLowerCase().equals("e")) {
                        continue;
                    }

                    back = goBack(input_prodid);
                    if (back != 0) {
                        return back;
                    }

                    q = "SELECT * FROM PRODUCT WHERE PROD_ID = '" + input_prodid + "' AND store_id = '"
                            + input_storeid + "'";

                    if (super.checkExist(s, q, ": Product ID is not in this Store") == 0) {
                        tf = 1;
                        continue;
                    }

                    try {
                        ResultSet result = s.executeQuery(q);

                        while (result.next()) {
                            cur_amount = result.getInt("amount");
                        }
                    } catch (SQLException e) {
                        System.out.println(e.getMessage());
                        System.out.println("Please contact technical department at htb320@lehigh.edu");
                    }
                } while (tf == 1);

                if (input_prodid.toLowerCase().equals("e")) {
                    more = 0;
                    continue;
                }

                do {
                    tf = 0;
                    System.out.println();
                    printGoBack();
                    System.out.println("Amount ordered (max: 20 items): ");
                    cordered_amount = myScanner.nextLine();

                    super.checkCardNumber(cordered_amount, cordered_amount.length());
                    back = goBack(cordered_amount);
                    if (back != 0) {
                        return back;
                    }

                    ordered_amount = Integer.parseInt(cordered_amount);


                    if (ordered_amount > 20) {
                        do {
                            tf = 0;

                            System.out.println("Buying with large amount, contact the manager.");
                            System.out.println("Proceed with another product? (y/n)");
                            answer = myScanner.nextLine();

                            List<String> list = Arrays.asList("n", "y", "yes", "no");

                            if (super.checkAnswer(list, answer.toLowerCase()) == 1) {
                                tf = 1;
                                continue;
                            }
                        } while (tf == 1);
                    } else if (ordered_amount > cur_amount) {
                        System.out.println("Exceeded current stock. Change the amount: ");
                        tf = 1;
                    }
                } while (tf == 1);

                if (answer.toLowerCase().equals("n") || answer.toLowerCase().equals("no")) {
                    return 1110; // Too big of order. Stop.
                }
                if (answer.toLowerCase().equals("y") || answer.toLowerCase().equals("yes")) {
                    more = 1;
                    continue; // Too big of order. Another prod.
                }


                q = "SELECT price FROM product WHERE prod_id = '" + input_prodid + "' AND " +
                        "store_id = '" + input_storeid + "'";

                try {
                    ResultSet result = s.executeQuery(q);

                    while (result.next()) {
                        price = result.getDouble("price");
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    System.out.println("Please contact the technical department at htb320@lehigh.edu");
                }

                if (price == 0) {
                    System.out.println("Sorry. Price for this product has not decided. Proceed to the next product.");
                    continue;
                }

                System.out.println("The price of this product is: " + price);

                total = (int) (Math.round(price * ordered_amount * 100));
                total = total / 100;
                System.out.println("Total cost for this product is:" + total);

                System.out.println();
                System.out.println("Recorded in the system:");
                System.out.println(new_orderid + "        " + inputcus_id
                        + "        " + input_storeid + "        " + input_prodid + "        " +
                        ordered_amount + "        " + price + total + "        " + delivery_method);

                q = "insert into cart values ('" + new_orderid
                        + "', '" + inputcus_id
                        + "', '" + input_storeid
                        + "', SYSTIMESTAMP, '" + input_prodid
                        + "', " + ordered_amount
                        + " , " + price
                        + " , " + total
                        + " , '" + delivery_method
                        + "', 'Pending', null)";

            } while (more == 1);

            int paymentTry = paymentProcess(s, inputcus_id, new_orderid, totalAmount);
            if (paymentTry == 1112 || paymentTry == 1113) {
                return 1112; // Bye, cannot make payment.
            } else if (paymentTry == 112) {
                return 112;
            } else if (paymentTry == 113) {
                return 113;
            }
            try {
                s.executeUpdate(q);
                System.out.println("Order successfully recorded with Order ID: " + new_orderid);

                q = "select * from cart where order_id = '" + new_orderid + "'";


                //print out the order infor here.
                System.out.printf("%1$-15s %2$-15s %3$-15s %4$-25s %5$-15s %6$-5s " +
                                "%7$-9s %8$-13s %9$-13s %10$-13s %11$-12s \n", "order_id", "cus_id",
                        "store_id", "order_date", "prod_id", "amount", "price", "total", "deliver_method",
                        "ship_status", "ship_date");
                ResultSet printC = s.executeQuery(q);
                while (printC.next()) {
                    System.out.printf("%1$-15s %2$-15s %3$-15s %4$-25s %5$-15s %6$-5s " +
                                    "%7$-9s %8$-13s %9$-13s %10$-13s %11$-12s \n", printC.getString("order_id"),
                            printC.getString("cus_id"), printC.getString("store_id"),
                            printC.getString("order_date"), printC.getString("prod_id"),
                            printC.getString("amount"), printC.getString("price"),
                            printC.getString("total"), printC.getString("delivery_method")
                            , printC.getString("ship_status"), printC.getString("ship_date"));
                }

            } catch (SQLException e) {
                System.out.println(e.getMessage());
                System.out.println("Cannot input this order.");
            }
        } while (anotherProd == 1);

        q = "select sum(total) from cart where order_id = '" + new_orderid + "'";
        try {
            ResultSet totalMoney = s.executeQuery(q);

            while (totalMoney.next()) {
                totalAmount = totalMoney.getDouble("Sum(total)");
                totalAmount = (int) (Math.round(totalAmount * 100));
                totalAmount = totalAmount / 100;
                System.out.println("Total of order " + new_orderid + " is: " + totalAmount);
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Please contact technical department at ht320@lehigh.edu");
        }
        return 0;
    }

    public int paymentProcess(Statement s, String inputcus_id, String new_orderid, double totalAmount) {
        //Show all of the order
        //Payment
        String new_transid = super.randomString(12);
        Scanner myScanner = new Scanner(System.in);
        String answer = "";
        int tf = 0;
        String q = "";
        int payType = 0;
        String payment_status = "Pending";
        String payment_method = "";
        String create_paymentid = "";
        String cur_paymentid = "";

        int haveCard = 0;

        q = "SELECT * FROM payment WHERE cus_id = '" + inputcus_id + "'";
        try {
            ResultSet alreadyCard = s.executeQuery(q);
            alreadyCard.last();
            int rowNumber = alreadyCard.getRow();
            if (rowNumber > 0) {
                System.out.println("This customer: " + inputcus_id + " has saved some payment cards.");
                cur_paymentid = alreadyCard.getString("payment_id");
                haveCard = 1;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        do {
            tf = 0;
            printGoBack();
            System.out.println();
            System.out.println("Payment method:");
            System.out.println("1: Cash");
            System.out.println("2: Debit Card");
            System.out.println("3: Credit Card");
            answer = myScanner.nextLine();

            int back = goBack(answer);
            if (back != 0) {
                return back;
            }

            List<String> list = Arrays.asList("1", "2", "3", "4");
            if (super.checkAnswer(list, answer) == 1) {
                tf = 1;
            }
        } while (tf == 1);

        payType = Integer.parseInt(answer);

        if (payType == 2) {
            payment_method = "Debit Card";
        } else if (payType == 3) {
            payment_method = "Credit Card";
        }

        double cashReceived = 0;
        if (payType == 1) {
            do {
                try {
                    tf = 0;
                    System.out.println("Cashed received: ");
                    cashReceived = myScanner.nextDouble();

                    if (cashReceived < totalAmount) {
                        System.out.println("Cash received must be greater than total.");
                        tf = 1;
                        continue;
                    }
                } catch (NumberFormatException e) {
                    System.out.println(e.getMessage());
                    System.out.println("Cash received must be valid. Try again");
                    tf = 1;
                    continue;
                }
            } while (tf == 1);


            payment_status = "Paid";
            cashReceived = (int) (Math.round(cashReceived * 100));
            cashReceived = cashReceived / 100;

            System.out.println("Received: " + cashReceived);
            System.out.println("The change is: " + ((int) (Math.round((cashReceived - totalAmount) * 100)) / 100));

            q = "insert into transaction values ('" + new_transid + "', 'Cash', '" + new_orderid
                    + "', null, 'Paid', systimestamp, " + totalAmount + ")";
            try {
                s.executeUpdate(q);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                System.out.println("Please contact technical department at htb320@lehigh.edu");
                return 1113; //trans cannot made by cash
            }
        } else {
            int paid = 0;
            cashReceived = totalAmount;
            boolean yo = false;

            if (haveCard == 1) {
                q = "SELECT * FROM payment WHERE cus_id = '" + inputcus_id + "'";
                try {
                    ResultSet existing = s.executeQuery(q);

                    while (existing.next()) {
                        String lastIndex = existing.getString("card_number");
                        lastIndex = lastIndex.substring(12);

                        System.out.println("Cards saved are: ");
                        System.out.printf("%1$-25s %2$-53s %3$-7s \n", "Last 4 card numbers", "Name on Card", "Exp Date");
                        System.out.printf("%1$-25s %2$-53s %3$-7s \n", lastIndex,
                                existing.getString("card_name"), existing.getString("exp_date"));
                    }
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                }

                //print each card option out
                do {
                    tf = 0;
                    System.out.println("1: Pay with existing card.");
                    System.out.println("2: Add a new card.");
                    answer = myScanner.nextLine();

                    List<String> list = Arrays.asList("1", "2");
                    if (super.checkAnswer(list, answer) == 1) {
                        tf = 1;
                    }
                } while (tf == 1);

                if (answer.equals("2")) {
                    create_paymentid = super.randomString(12);
                    yo = updateCart(s, create_paymentid, inputcus_id);
                }
                if (answer.equals("1")) {
                    create_paymentid = cur_paymentid;
                }
            }

            if(haveCard == 0) {
                System.out.println("Card information needed for this purchase.");
                create_paymentid = super.randomString(12);
                yo = updateCart(s, create_paymentid, inputcus_id);
            }

            if(yo) {
            System.out.println("Payment approved!");

            q = "insert into transaction values ('" + new_transid + "', '" + payment_method
                    + "' , '" + new_orderid
                    + "', '" + create_paymentid + "', 'Pending', systimestamp, " + totalAmount + ")";

            try {
                s.executeUpdate(q);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                System.out.println("Please contact technical department at htb320@lehigh.edu");
            }
            }
            else {
                System.out.println("Failed card payment.");
                return -1;
            }
        }
        return 0;
    }


    public String surveyNewCus(Statement s) {
        int tf = 0;
        Scanner myScanner = new Scanner(System.in);
        int checkrepeatedid = 0;
        String q = "";
        String first_name = "";
        String last_name = "";
        String street = "";
        String city = "";
        String state = "";
        String zip_code = "";
        String phone_number = "";
        String email = "";
        String dob = "";
        String frequent_id = "";

        do {
            tf = 0;

            System.out.println("First name: ");
            first_name = myScanner.nextLine();
            System.out.println("Last name: ");
            last_name = myScanner.nextLine();
            if (first_name.length() == 0 || last_name.length() == 0) {
                System.out.println("First/Last name must be filled out. Try again.");
                tf = 1;
            }
        } while (tf == 1);

        do {
            tf = 0;
            System.out.println("Street: ");
            street = myScanner.nextLine();
            if (street.length() == 0) {
                street = null;
            } else if (street.length() > 50) {
                System.out.println("Street cannot be more than 50 characters. Try again.");
                tf = 1;
                continue;
            }
        } while (tf == 1);

        do {
            tf = 0;
            System.out.println("City: ");
            city = myScanner.nextLine();
            if (city.length() == 0) {
                city = null;
            } else if (city.length() > 50) {
                System.out.println("City cannot be more than 50 characters. Try again.");
                tf = 1;
                continue;
            }
        } while (tf == 1);

        do {
            tf = 0;
            System.out.println("State: ");
            state = myScanner.nextLine();
            if (state.length() == 0) {
                state = null;
            } else if (state.length() > 5) {
                System.out.println("State cannot be more than 30 characters. Try again.");
                tf = 1;
                continue;
            }
        } while (tf == 1);

        do {
            tf = 0;
            System.out.println("Zip code: ");
            zip_code = myScanner.nextLine();
            if (zip_code.length() == 0) {
                zip_code = null;
            } else if (zip_code.length() > 5) {
                System.out.println("Zip code cannot be more than 5 digits. Try again.");
                tf = 1;
                continue;
            } else {
                try {
                    Integer.parseInt(zip_code);
                } catch (NumberFormatException e) {
                    tf = 1;
                    System.out.println("You have entered an invalid zip_code. Try again.");
                    continue;
                }
            }
        } while (tf == 1);

        do {
            tf = 0;
            System.out.println("Phone number: ");
            phone_number = myScanner.nextLine();
            if (phone_number.length() == 0) {
                phone_number = null;
            } else if (phone_number.length() > 11) {
                System.out.println("Phone number cannot be more than  11 numbers. Try again.");
                tf = 1;
                continue;
            } else {
                if (!super.checkCardNumber(phone_number, phone_number.length())) {
                    tf = 1;
                    System.out.println("You have entered an invalid phone number. Try again.");
                    continue;
                }
            }
        } while (tf == 1);

        do {
            tf = 0;
            System.out.println("Email: ");
            email = myScanner.nextLine();
            if (email.length() == 0) {
                email = null;
            } else if (!super.isValid(email)) {
                System.out.println("Invalid Email. Try again.");
                tf = 1;
                continue;
            }
        } while (tf == 1);

        do {
            tf = 0;
            System.out.println("Date of birth(DD-MM-YY): ");
            dob = myScanner.nextLine();
            if (dob.length() == 0) {
                dob = null;
            } else if (!super.isDate(dob)) {
                System.out.println("Invalid Date. Try again.");
                tf = 1;
                continue;
            }
        } while (tf == 1);

        String check = frequentWanted();
        if (check.equals("y")) {
            int checkrepeatedfid = 0;
            try {
                do {
                    frequent_id = super.randomString(12);

                    q = "SELECT * FROM customer WHERE frequent_id = '" + frequent_id + "'";
                    ResultSet checknewID = s.executeQuery(q);

                    if (checknewID.next()) {
                        checkrepeatedfid = 1;
                    } else {
                        checkrepeatedfid = 0;
                    }
                } while (checkrepeatedfid == 1);
                System.out.println("Frequent ID generated: " + frequent_id);
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                System.out.println("Contact the technical department at htb320@lehigh.edu");
            }
        } else {
            frequent_id = null;
        }

        String new_cusid = "";

        try {
            do {
                new_cusid = super.randomString(12);

                q = "SELECT * FROM customer WHERE cus_id = '" + new_cusid + "'";

                ResultSet checknewID = s.executeQuery(q);

                if (checknewID.next()) {
                    checkrepeatedid = 1;
                } else {
                    checkrepeatedid = 0;
                }
            } while (checkrepeatedid == 1);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Contact the technicle department for help. Email: htb320@lehihg.edu");
        }


        System.out.println("Customer ID Generated: " + new_cusid);

        //SimpleDateFormat format = new SimpleDateFormat("ddMMyy");
        //Date parsed = format.parse(dob);
        //java.sql.Date dob_input = new java.sql.Date(parsed.getTime());

        q = "INSERT INTO customer VALUES ('" + new_cusid +
                "', '" + first_name +
                "', '" + last_name +
                "', '" + street +
                "', '" + city +
                "', '" + state +
                "', '" + zip_code +
                "', '" + phone_number +
                "', '" + email +
                "', to_date('" + dob + "', 'DD-MM-YY')" +
                ", '" + frequent_id + "')";

        insertNewCus(s, new_cusid, q);
        return new_cusid;
    }


    public void insertNewCus(Statement s, String new_cusid, String q) {
        try {
            s.executeUpdate(q);
            System.out.println("Inserted the new cus_id " + new_cusid);
            q = "SELECT * FROM CUSTOMER WHERE CUS_ID = '" + new_cusid + "'";


            System.out.printf("%1$-15s %2$-17s %3$-17s %4$-52s %5$-52s %6$-32s " +
                            "%7$-7s %8$-13s %9$-52s %10$-11s %11$-15s \n", "cud_id", "first_name",
                    "last_name", "street", "city", "state", "zip_code", "phone_number", "email", "dob", "frequent_id");
            ResultSet printC = s.executeQuery(q);
            while (printC.next()) {
                System.out.printf("%1$-15s %2$-17s %3$-17s %4$-52s %5$-52s %6$-32s " +
                                "%7$-7s %8$-13s %9$-52s %10$-11s %11$-15s \n", printC.getString("cus_id"),
                        printC.getString("first_name"), printC.getString("last_name"),
                        printC.getString("street"), printC.getString("city"),
                        printC.getString("state"), printC.getString("zip_code"),
                        printC.getString("phone_number"), printC.getString("email")
                        , printC.getString("dob"), printC.getString("frequent_id"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public boolean updateCart(Statement s, String new_paymentid, String cus_id) {
        int tf = 0;
        Scanner myScanner = new Scanner(System.in);
        String name = "";
        String card_num = "";
        String exp_date = "";
        String street = "";
        String city = "";
        String state = "";
        String zip_code = "";
        System.out.println("Name on card:");
        name = myScanner.nextLine();

        do {
            tf = 0;
            System.out.println("Card Number: ");
            card_num = myScanner.nextLine();

            if (card_num.length() != 16) {
                tf = 1;
                System.out.println("Card number must contain 16 digits. Try again.");
                continue;
            }

            if (!super.checkCardNumber(card_num, 16)) {
                tf = 1;
                System.out.println("Card number must contain numbers only. Try again.");
            }
        } while (tf == 1);

        do {
            tf = 0;
            try {
                System.out.println("Expiration Date:");
                exp_date = myScanner.nextLine();

                if(exp_date.length() != 4) {
                    System.out.println("Invalid. Need 4 digits.");
                    tf = 1;
                    continue;
                }
                if(!checkCardNumber(exp_date, exp_date.length())) {
                    System.out.println("Expiration Date are 4 digits.");
                    tf = 1;
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage() + "Try again.");
                tf = 1;
            }
        } while (tf == 1);

        do {
            tf = 0;
            System.out.println("Street: ");
            street = myScanner.nextLine();
            if (street.length() == 0) {
                street = null;
            } else if (street.length() > 50) {
                System.out.println("Street cannot be more than 50 characters. Try again.");
                tf = 1;
            }
        } while (tf == 1);

        do {
            tf = 0;
            System.out.println("City: ");
            city = myScanner.nextLine();
            if (city.length() == 0) {
                city = null;
            } else if (city.length() > 50) {
                System.out.println("City cannot be more than 50 characters. Try again.");
                tf = 1;
            }
        } while (tf == 1);

        do {
            tf = 0;
            System.out.println("State: ");
            state = myScanner.nextLine();
            if (state.length() == 0) {
                state = null;
            } else if (state.length() > 30) {
                System.out.println("State cannot be more than 30 characters. Try again.");
                tf = 1;
            }
        } while (tf == 1);

        do {
            tf = 0;
            System.out.println("Zip code: ");
            zip_code = myScanner.nextLine();
            if (zip_code.length() == 0) {
                zip_code = null;
            } else if (zip_code.length() > 5) {
                System.out.println("Zip code cannot be more than 5 digits. Try again.");
                tf = 1;
                continue;
            } else {
                try {
                    Integer.parseInt(zip_code);
                } catch (NumberFormatException e) {
                    tf = 1;
                    System.out.println("You have entered an invalid zip_code. Try again.");
                }
            }
        } while (tf == 1);

        String q = "insert into PAYMENT values ('" + new_paymentid + "', '" + cus_id + "', '" + name
                + "', " + card_num + ", " + Integer.parseInt(exp_date) + ", '" + street + "' , '" + city
                + "', '" + state + "', " + Integer.parseInt(zip_code) + ")";
        try {
            System.out.println("New Card Infor recorded.");
            System.out.printf("%1$-15s %2$-17s %3$-63s %4$-18s %5$-6s %6$-52s %7$-52s %8$-33s %9$-7s \n",
                    "payment_id", "cus_id", "card_name", "card_number", "exp_date", "street",
                    "city", "state", "zip_code");

            s.executeUpdate(q);

            q = "select * from payment where payment_id = '" + new_paymentid + "'";
            ResultSet insertPayment = s.executeQuery(q);

            while (insertPayment.next()) {
                System.out.printf("%1$-15s %2$-17s %3$-63s %4$-18s %5$-6s %6$-52s " +
                                "%7$-52s %8$-33s %9$-7s \n", insertPayment.getString("payment_id"),
                        insertPayment.getString("cus_id"), insertPayment.getString("card_name"),
                        insertPayment.getString("card_number"), insertPayment.getString("exp_date"),
                        insertPayment.getString("street"),insertPayment.getString("city"),
                        insertPayment.getString("state"), insertPayment.getString("zip_code"));
                System.out.println("Successfully created this card information with payment_id: " + new_paymentid + " under customer: "
                        + cus_id);
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage() + ". Could not create this card information.");
            return false;
        }
        return false;
    }

    public int direction(String inputcus_id) {
        int tf = 0;
        Scanner myScanner = new Scanner(System.in);
        String answer = "";
        do {
            tf = 0;
            System.out.println();
            System.out.println("1: Finish and proceed to another customer.");
            System.out.println("2: Proceed with this customer: " + inputcus_id);
            System.out.println("3: Exit customer site.");
            answer = myScanner.nextLine();

            List<String> list = Arrays.asList("1", "2", "3");
            if (super.checkAnswer(list, answer) == 1) {
                tf = 1;
            }
        } while (tf == 1);

        if (answer.equals("1")) {
            return 111; //Another cus
        } else if (answer.equals("2")) {
            return 112; // Continue with this one
        } else {
            return 0; // Simply exit the site
        }
    }

    public void getCusInfo(Statement s, String inputcus_id) {
        String q = "";
        try {
            q = "SELECT * FROM CUSTOMER WHERE CUS_ID = '" + inputcus_id + "'";


            System.out.printf("%1$-15s %2$-17s %3$-17s %4$-52s %5$-52s %6$-32s " +
                            "%7$-7s %8$-13s %9$-52s %10$-11s %11$-15s \n", "cud_id", "first_name",
                    "last_name", "street", "city", "state", "zip_code", "phone_number", "email", "dob", "frequent_id");
            ResultSet printC = s.executeQuery(q);
            while (printC.next()) {

                System.out.printf("%1$-15s %2$-17s %3$-17s %4$-52s %5$-52s %6$-32s " +
                                "%7$-7s %8$-13s %9$-52s %10$-11s %11$-15s \n", printC.getString("cus_id"),
                        printC.getString("first_name"), printC.getString("last_name"),
                        printC.getString("street"), printC.getString("city"),
                        printC.getString("state"), printC.getString("zip_code"),
                        printC.getString("phone_number"), printC.getString("email")
                        , printC.getString("dob"), printC.getString("frequent_id"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Contact htb320@lehigh.edu");
        }
    }

    public int goBack(String answer) {
        if (answer.toLowerCase().equals("b")) {
            return 111; //back to MM site;
        } else if (answer.toLowerCase().equals("x")) {
            return 112; // exit
        }
        return 0;
    }

    public void printGoBack() {
        System.out.println("Press 'B' to go back to Main Management Site.");
        System.out.println("Press 'X' to exit");
    }
}