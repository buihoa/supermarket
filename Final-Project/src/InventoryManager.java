import java.util.*;
import java.io.*;
import java.sql.*;
import java.sql.SQLException;

//Check how much inventory there are in each store, including online store
// List all products in one store
// List all stores
// List all vendors
// See transaction + Update transaction on stockinput
// Enter a new vendor
// See cart order
// See transaction to see if paid or not
public class InventoryManager extends Homepage {
    Connection con = null;

    public int getConnect() {
        Scanner myScanner = new Scanner(System.in);
        int tf = 0;
        String userid = "";
        String answer = "";
        String pw = "";
        do {
            try {
                tf = 0;
                System.out.println();
                System.out.println("Enter your manager username: ");
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

    public int checkInventory() {
        Scanner myScanner = new Scanner(System.in);
        String answer = "";
        int tf = 0;
        int endProg = 0;

        try (
                Statement s = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
        ) {
            do {
                endProg = 0;
                do {
                    endProg = 0;
                    tf = 0;
                    System.out.println();
                    System.out.println("Welcome to Management Site!");
                    System.out.println("1: Get your information");
                    System.out.println("2: Look for product availability");
                    System.out.println("3: Look for store's current stock");
                    System.out.println("4: About vendor and past restock");
                    System.out.println("5: To update a new inventory input");
                    System.out.println("6: The top largest restock in the past 100 days");
                    System.out.println("7: Exit");
                    answer = myScanner.nextLine();

                    List<String> list = Arrays.asList("1", "2", "3", "4", "5", "6", "7");
                    if (checkAnswer(list, answer) == 1) {
                        tf = 1;
                        System.out.println("Invalid. Please enter correct instructed number.");
                    }

                } while (tf == 1);

                if (answer.toLowerCase().equals("1")) {
                    getInfo();
                } else if (answer.toLowerCase().equals("2")) {
                    int retval = currentInventory(s);
                    if (retval == 111) {
                        continue;
                    }
                    if (retval == 112) {
                        return retval;
                    }
                } else if (answer.toLowerCase().equals("3")) {
                    int retval = storeManage(s);
                    if (retval == 111) {
                        continue;
                    }
                    if (retval == 112) {
                        return retval;
                    }
                } else if (answer.toLowerCase().equals("4")) {
                    int retval = vendorManage(s);
                    if (retval == 111) {
                        continue;
                    }
                    if (retval == 112) {
                        return retval;
                    }
                } else if (answer.toLowerCase().equals("5")) {
                    updateNewInventory(s);
                } else if (answer.toLowerCase().equals("6")) {
                    topBestSeller(s);
                }
                else if (answer.toLowerCase().equals("7")) {
                    s.close();
                    con.close();
                    return 112;
                }
            } while (endProg == 0);
            s.close();
            con.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;
    }

    public void getInfo() {
        System.out.printf("%1$-30s %2$-30s %3$-10s %4$-20s \n", "Employee Name", "Role", "Gender", "Start Working Date");
        System.out.printf("%1$-30s %2$-30s %3$-10s %4$-20s \n", "Hoa Bui", "Inventory Manager", "Female", "Spring 2018");
    }


    public void topBestSeller(Statement s) {
        String q = "select prod_id, totalRestock, rank () over (order by totalRestock desc) as topseller\n" +
                "from(\n" +
                "select prod_id, sum(amount) as totalRestock\n" +
                "from stockinput\n" +
                "where extract(day from (systimestamp - CAST(date_input as timestamp))) <= 100\n" +
                "group by prod_id\n" +
                ")";

        try {
            ResultSet result = s.executeQuery(q);
            System.out.println();
            System.out.printf("%1$-15s %2$-15s %3$-3s  \n", "Prod ID", "Total", "Rank");

            while(result.next())
            {
                System.out.printf("%1$-15s %2$-15s %3$-3s  \n", result.getString(1), result.getString(2),
                        result.getString(3));
            }

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }

    }


    /////////////////// Update means MONEY OUT!! :( BUT STUFF IN!!!
    public int updateNewInventory(Statement s) {
        int tf = 0;
        Scanner myScanner = new Scanner(System.in);
        String answer = "";
        String new_inputid = "";
        String q = "";

        System.out.println("Enter product id restock");
        answer = myScanner.nextLine();

        q = "SELECT * FROM PRODUCT WHERE PROD_ID = '" + answer + "'";

        int retval = super.checkExist(s, q, "prod id");

        if (retval == 0) {
            updateRestock(s, 1);
        }
        updateRestock(s, 0);
        return 0;
    }

    public void updateRestock(Statement s, int flag) {
        String new_inputid = super.randomString(12);
        String vendor_id = "";
        String new_prodid = super.randomString(12);
        String store_id = "";
        String name = "";
        String brand = "";
        int amount = 0;
        int price_unit = 0;
        Scanner myScanner = new Scanner(System.in);

        System.out.println("Enter the vendor id: ");
        vendor_id = myScanner.nextLine();

        System.out.println("Enter the product name: ");
        name = myScanner.nextLine();

        System.out.println("Enter the brand: ");
        brand = myScanner.nextLine();

        System.out.println("Enter the store id: ");
        store_id = myScanner.nextLine();

        System.out.println("Enter the amount: ");
        amount = myScanner.nextInt();

        System.out.println("Enter the price per unit: ");
        price_unit = myScanner.nextInt();

        if(flag == 1) { // new id
            System.out.println();
            System.out.println("This is a new product.");
            enterStockInput(s, new_inputid, vendor_id, new_prodid, store_id, amount, price_unit);
            return;
        }
        enterNewProduct(s, new_prodid, name, store_id, price_unit,amount,brand);
    }

    public void enterStockInput(Statement s,String new_inputid,
                                String vendor_id, String new_proid,
                                String storeid, int amount,
                                int price) {
        String q = "insert into stockinput values ('" + new_inputid + "',"
                + vendor_id + "', " + new_proid
                + "', '" + storeid + "', SYSTIMESTAMP, " + amount
                + ", " + price + ")";
        try {
            s.executeUpdate(q);
            System.out.println(q);
        }catch(SQLException e){
            System.out.println(e.getMessage());
            System.out.println("Please contact technical department at htb320@lehigh.edu");
        }
    }

    public void enterNewProduct(Statement s, String new_proid,
                                String prodname, String storeid,
                                int price, int amount, String brand) {
        String q = "insert into product values ('" + new_proid
                + "', '" + prodname + "', '" + storeid + "', " + price
                + ", " + amount + ", SYSTIMESTAMP)";

        try {
            s.executeUpdate(q);
            System.out.println(q);
        }catch(SQLException e){
            System.out.println(e.getMessage());
            System.out.println("Please contact technical department at htb320@lehigh.edu");
        }
    }


    ////////////////// VENDOR MANAGE
    public int vendorManage(Statement s) {
        int tf = 0;
        String q = "";
        Scanner myScanner = new Scanner(System.in);
        String answer = "";
        int back = 0;
        String vendor_id = "";

        do {
            tf = 0;
            System.out.println();
            printGoBack();
            System.out.println("1: Get information of a vendor");
            System.out.println("2: Check past restocks");

            answer = myScanner.nextLine();
            back = goBack(answer);

            if (back != 0) {
                return back;
            }

            List<String> list = Arrays.asList("1", "2");
            if (checkAnswer(list, answer) == 1) {
                continue;
            }

            if (answer.toLowerCase().equals("1")) {
                System.out.println("Enter the vendor id: ");
                answer = myScanner.nextLine();
                getVendorInfo(s, answer);

            } else if (answer.toLowerCase().equals("2")) {
                int retval = checkPastRestock(s, vendor_id);

                if (retval == 111) {
                    continue;
                }
                if (retval == 112) {
                    return retval;
                }
            }
        } while (tf == 0);

        return 0;
    }

    public void getVendorInfo(Statement s, String vendor_id) {
        String q = "select * from VENDOR where vendor_id = '" + vendor_id + "'";

        int retval = super.checkExist(s, q, "vendor id");

        if (retval == 0) {
            return;
        }

        try {
            ResultSet printC = s.executeQuery(q);

            // Fix this format
            System.out.printf("%1$-15s %2$-74s %3$-30s %4$-27s %5$-10s %6$-7s " +
                            "%7$-15s %8$-33s \n", "vendor_id", "vendor_name", "street", "city",
                    "state", "zip_code" , "phone_number", "email");

            while (printC.next()) {
                System.out.printf("%1$-15s %2$-74s %3$-30s %4$-27s %5$-10s %6$-7s " +
                                "%7$-15s %8$-33s\n",
                        printC.getString("vendor_id"),
                        printC.getString("vendor_name"),
                        printC.getString("street"), printC.getString("city"),
                        printC.getString("state"), printC.getString("zip_code"),
                        printC.getString("phone_number"), printC.getString("email"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public int checkPastRestock(Statement s, String vendor_id) {
        int tf = 0;
        Scanner myScanner = new Scanner(System.in);
        String prod_id = "";
        int back = 0;
        String q = "";
        String answer = "";

        do {
            tf = 0;
            System.out.println();
            printGoBack();
            System.out.println("1: Of a product id");
            System.out.println("2: All of restock with a vendor");
            answer = myScanner.nextLine();

            back = goBack(answer);
            if (back != 0) {
                return back;
            }

            List<String> list = Arrays.asList("1", "2");
            if (checkAnswer(list, answer) == 1) {
                tf = 1;
                System.out.println("Invalid. Please enter correct instructed number.");
            }

            if (answer.toLowerCase().equals("1")) {
                int retval = vendorProductRestock(s, vendor_id);
                if (retval == 111) {
                    continue;
                }
                if (retval == 112) {
                    return retval;
                }
            } else if (answer.toLowerCase().equals("2")) {
                int retval =vendorAllRestock(s, vendor_id);
                if (retval == 111) {
                    continue;
                }
                if (retval == 112) {
                    return retval;
                }
            }

        }
        while (tf == 1);
        return 0;
    }

    public int vendorProductRestock(Statement s, String vendor_id) {
        int tf = 0;
        String q = "";
        String answer = "";
        Scanner myScanner = new Scanner(System.in);
        int back = 0;

        do {
            System.out.println("Enter the product id: ");
            answer = myScanner.nextLine();

            back = goBack(answer);
            if (back != 0) {
                return back;
            }
            q = "SELECT * FROM PRODUCT WHERE PROD_ID = '" + answer + "'";

            int retval = super.checkExist(s, q, "prod id");

            if (retval == 0) {
                tf = 1;
            }
        } while (tf == 1);

        q = "SELECT * " +
                "FROM stockinput " +
                "WHERE prod_id = '" + answer + "'";

        try {
            ResultSet restock = s.executeQuery(q);
            System.out.printf("%1$-17s %2$-17s %3$-15s %4$-17s %5$-25s " +
                            "%6$-15s %7$-17s  \n",
                    "input_id", "vendor_id", "prod_id", "store_id", "date_input", "amount", "price_unit");
            while (restock.next()) {
                System.out.printf("%1$-17s %2$-17s %3$-15s %4$-17s %5$-25s " +
                                "%6$-15s %7$-17s  \n",
                        restock.getString("input_id"), restock.getString("vendor_id"),
                        restock.getString("prod_id"), restock.getString("store_id"),
                        restock.getString("date_input"),restock.getString("amount"),
                        restock.getString("price_unit"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Please contact technical department at htb320@lehigh.edu");
        }
        return 0;
    }

    public int vendorAllRestock(Statement s, String vendor_id) {
        int tf = 0;
        String q = "";
        String answer = "";
        Scanner myScanner = new Scanner(System.in);
        int back = 0;

        do {
            System.out.println("Enter the vendor id: ");
            answer = myScanner.nextLine();

            back = goBack(answer);
            if (back != 0) {
                return back;
            }
            q = "SELECT * FROM vendor WHERE VENDOR_ID = '" + answer + "'";

            int retval = super.checkExist(s, q, "vendor id");

            if (retval == 0) {
                tf = 1;
            }
        } while (tf == 1);

        q = "select * " +
                "from stockinput " +
                "where vendor_id = '" + answer + "'";

        try {
            ResultSet restock = s.executeQuery(q);
            System.out.printf("%1$-17s %2$-17s %3$-15s %4$-17s %5$-25s " +
                            "%6$-15s %7$-17s  \n",
                    "input_id", "vendor_id", "prod_id", "store_id", "date_input", "amount", "price_unit");

            while (restock.next()) {
                System.out.printf("%1$-17s %2$-17s %3$-15s %4$-17s %5$-25s " +
                                "%6$-15s %7$-17s  \n",
                        restock.getString("input_id"), restock.getString("vendor_id"),
                        restock.getString("prod_id"), restock.getString("store_id"),
                        restock.getString("date_input"),restock.getString("amount"),
                        restock.getString("price_unit"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Please contact technical department at htb320@lehigh.edu");
        }
        return 0;
    }


    ////////////////// GO FROM STORE
    public int storeManage(Statement s) {
        int tf = 0;
        String q = "";
        Scanner myScanner = new Scanner(System.in);
        String answer = "";
        int back = 0;
        String store_id = "";

        do {
            tf = 0;
            System.out.println();
            printGoBack();
            System.out.println("Enter the store ID: ");
            store_id = myScanner.nextLine();

            back = goBack(store_id);
            if (back != 0) {
                return back;
            }

            q = "SELECT * FROM store WHERE store_id = '" + store_id + "'";

            int retval = super.checkExist(s, q, "store id");

            if (retval == 0) {
                tf = 1;
            }
        } while (tf == 1);

        do {
            tf = 0;
            System.out.println();
            System.out.println("1: Get information of this store.");
            System.out.println("2: List all products and stock in  this store.");
            printGoBack();
            answer = myScanner.nextLine();

            back = goBack(answer);
            if (back != 0) {
                return back;
            }

            List<String> list = Arrays.asList("1", "2");
            if (checkAnswer(list, answer) == 1) {
                continue;
            }

            if (answer.toLowerCase().equals("1")) {
                getStoreInfo(s, store_id);
            } else if (answer.toLowerCase().equals("2")) {
                listAllStockStore(s, store_id);
            }
        } while (tf == 0);
        return 0;
    }

    public void listAllStockStore(Statement s, String store_id) {
        int retval = 0;

        String q = "select product.prod_id, store.store_id, product.amount " +
                "from product, store " +
                "where product.store_id = store.store_id and " +
                "store.store_id = '" + store_id + "'";
        try {
            ResultSet prodStoreView = s.executeQuery(q);
            System.out.printf("%1$-17s %2$-17s %3$-15s \n",
                    "prod_id", "store_id", "amount");
            while (prodStoreView.next()) {
                System.out.printf("%1$-17s %2$-17s %3$-15s \n", prodStoreView.getString("prod_id"),
                        prodStoreView.getString("store_id"), prodStoreView.getString("amount"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Please contact technical department at htb320@lehigh.edu");
        }
    }

    public void getStoreInfo(Statement s, String store_id) {
        String q = "select * from store where store_id = '" + store_id + "'";

        try {
            ResultSet result = s.executeQuery(q);

            System.out.printf("%1$-15s %2$-40s %3$-15s %4$-10s %5$-8s %6$-15s \n", "store_id",
                    "street", "city", "state", "zip_code", "phone_number");

            while (result.next()) {
                System.out.printf("%1$-15s %2$-40s %3$-15s %4$-10s %5$-8s %6$-15s \n", result.getString("store_id"),
                        result.getString("street"), result.getString("city"),
                        result.getString("state"), result.getString("zip_code"),
                        result.getString("phone_number"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    ////////////////// GO FROM INVENTORY
    public int currentInventory(Statement s) {
        int tf = 0;
        String q = "";
        Scanner myScanner = new Scanner(System.in);
        String answer = "";
        int back = 0;
        String prod_id = "";

        do {
            tf = 0;
            System.out.println();
            printGoBack();
            System.out.println("Enter the product ID: ");
            prod_id = myScanner.nextLine();

            back = goBack(prod_id);
            if (back != 0) {
                return back;
            }

            q = "SELECT * FROM PRODUCT WHERE PROD_ID = '" + prod_id + "'";

            int retval = super.checkExist(s, q, "prod id");

            if (retval == 0) {
                tf = 1;
            }
        } while (tf == 1);

        do {
            tf = 0;
            System.out.println();
            System.out.println("1: List total amount across stores.");
            System.out.println("2: List available stock in each store.");
            System.out.println("3: List available stock in 1 store");
            //System.out.println("4: Current price."); Come back to this later!
            printGoBack();
            answer = myScanner.nextLine();

            back = goBack(answer);
            if (back != 0) {
                return back;
            }

            List<String> list = Arrays.asList("1", "2", "3");
            if (checkAnswer(list, answer) == 1) {
                continue;
            }
            if (answer.toLowerCase().equals("1")) {
                checkTotalProdStore(s, prod_id);
            } else if (answer.toLowerCase().equals("2")) {
                checkProdALLStore(s, prod_id);
            } else if (answer.toLowerCase().equals("3")) {
                checkProdEachStore(s, prod_id);
            } //else if (answer.toLowerCase().equals("4")) {
            //}
        } while (tf == 0);
        return 0;
    }


    //public ResultSet search(Connection c, ResultSet table, int column, int howMany) {
    //}


    public void printGoBack() {
        System.out.println("Press 'B' to go back to Main Management Site.");
        System.out.println("Press 'X' to exit");
    }

    public int goBack(String answer) {
        if (answer.toLowerCase().equals("b")) {
            return 111; //back to MM site;
        } else if (answer.toLowerCase().equals("x")) {
            return 112; // exit
        }
        return 0;
    }

    public void checkProdALLStore(Statement s, String prod_id) {
        String q = "select product.prod_id, store_id, amount " +
                "from product " +
                "where product.prod_id = '" + prod_id + "'";
        try {
            ResultSet prodStoreView = s.executeQuery(q);

            System.out.printf("%1$-17s %2$-17s %3$-15s \n",
                    "prod_id", "store_id", "amount");
            while (prodStoreView.next()) {
                System.out.printf("%1$-17s %2$-17s %3$-15s \n", prodStoreView.getString("prod_id"),
                        prodStoreView.getString("store_id"), prodStoreView.getString("amount"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Please contact technical department at htb320@lehigh.edu");
        }
    }

    public void checkProdEachStore(Statement s, String prod_id) {
        int retval = 0;
        String q = "";

        Scanner myScanner = new Scanner(System.in);
        String answer = "";

        System.out.println("Enter store id: ");
        answer = myScanner.nextLine();

        q = "select * " +
                "from product where store_id = '" + answer + "' and prod_id = '" + prod_id + "'";

        retval = super.checkExist(s, q, "");

        if (retval == 0) {
            System.out.print("This store does not have this product.");
            System.out.println();
            return;
        } else {
            try {
                q = "select product.prod_id, store.store_id, product.amount " +
                        "from product, store " +
                        "where product.store_id = store.store_id and product.prod_id = '" + prod_id + "' and " +
                        "store.store_id = '" + answer + "'";
                ResultSet prodStoreView = s.executeQuery(q);

                System.out.println();
                System.out.printf("%1$-17s %2$-17s %3$-15s \n",
                        "Prod_id", "Store_id", "Current Amount");
                while (prodStoreView.next()) {
                    System.out.printf("%1$-17s %2$-17s %3$-15s \n", prodStoreView.getString("prod_id"),
                            prodStoreView.getString("store_id"), prodStoreView.getString("amount"));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                System.out.println("Please contact technical department at htb320@lehigh.edu");
            }
        }
    }


    public void checkTotalProdStore(Statement s, String prod_id) {
        int retval = 0;
        String q = "select sum(amount) " +
                "from product where prod_id = '" + prod_id + "'";

        retval = super.checkExist(s, q, "search for total stock of this product.");

        if (retval == 0) {
            return;
        } else {
            try {
                ResultSet prodStoreView = s.executeQuery(q);

                System.out.println("Total stock of " + prod_id + " cross stores is:");
                System.out.println();
                System.out.printf("%1$-17s \n", "Total");
                while (prodStoreView.next()) {
                    System.out.printf("%1$-17s \n", prodStoreView.getString("sum(amount)"));
                }
            } catch (SQLException e) {
                System.out.println(e.getMessage());
                System.out.println("Please contact technical department at htb320@lehigh.edu");
            }
        }
    }
}
