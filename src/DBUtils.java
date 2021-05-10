
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;

public class DBUtils {

    private static int[] getAllContracts() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select contract_no from contracts");
            ArrayList<Integer> contractNoArrayList = new ArrayList<>();
            while (rs.next()) {
                contractNoArrayList.add(rs.getInt(1));
            }
            int[] nums = new int[contractNoArrayList.size()];
            for (int i = 0; i < contractNoArrayList.size(); i++) {
                nums[i] = contractNoArrayList.get(i);
            }
            return nums;
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return new int[]{};
    }

    public static int[] getItemsInContract(int CONTRACT_NO) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            if (isValidContract(CONTRACT_NO)) {
                ResultSet rs = statement.executeQuery("select item_no_fk from to_supply where contract_no_fk = " + CONTRACT_NO);
                ArrayList<Integer> itemsArrayList = new ArrayList<>();
                if (!rs.isBeforeFirst()) {
                    return new int[]{};
                } else {
                    while (rs.next()) {
                        itemsArrayList.add(rs.getInt(1));
                    }
                }
                int[] items = new int[itemsArrayList.size()];
                for (int i = 0; i < itemsArrayList.size(); i++) {
                    items[i] = itemsArrayList.get(i);
                }
                return items;
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return new int[]{0};
    }

    public static void contractSummary() {
        System.out.printf("%15s %15s %20s %25s", "Contract Number", "Item Number", "Quantity Bought", "Max Quantity Available");
        System.out.println();
        for (int j : getAllContracts()) {
            try {
                int CONTRACT_NO = j;
                Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
                Statement statement = con.createStatement();
                if (isValidContract(CONTRACT_NO)) {
                    statement.executeUpdate("create temporary table contract_summary (contract_no int, item_no int, quantity_bought int , quantity_total_available int)");
                    for (int i : getItemsInContract(CONTRACT_NO)) {
                        statement.executeUpdate("insert into contract_summary values ( " + CONTRACT_NO + ", " + i + ", " + findBoughtQuantity(i, CONTRACT_NO) + ", " + findTotalQuantity(i, CONTRACT_NO) + ")");
                    }
                    ResultSet rs = statement.executeQuery("select * from contract_summary order by CONTRACT_NO asc, ITEM_NO asc");
                    rs.next();
                    //Cycle through rows
                    for (int i = 0; i < getItemsInContract(CONTRACT_NO).length; i++) {
                        System.out.printf("%15d %15d %20d %25d", rs.getBigDecimal("contract_no").intValue(), rs.getBigDecimal("item_no").intValue(), rs.getBigDecimal("quantity_bought").intValue(), rs.getBigDecimal("quantity_total_available").intValue());
                        System.out.println();
                        rs.next();
                    }
                    con.close();
                }
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }
        }
        System.out.println();
    }

    public static void CreateDB() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            statement.executeUpdate("create database if not exists turner_construction");
            statement.executeUpdate("use turner_construction");
            statement.executeUpdate("create table if not exists suppliers(supplier_no int unique key, supplier_address char(30), supplier_name char(20))");
            statement.executeUpdate("create table if not exists contracts(contract_no int unique key, supplier_no_fk int, foreign key (supplier_no_fk) references suppliers(supplier_no), date_of_contract date)");
            statement.executeUpdate("create table if not exists projects(project_no int unique key, project_data char(20))");
            statement.executeUpdate("create table if not exists orders(order_no int unique key, contract_no_fk int, foreign key (contract_no_fk) references contracts(contract_no), project_no_fk int, foreign key (project_no_fk) references projects(project_no),date_required date, date_completed date)");
            statement.executeUpdate("create table if not exists items(item_no int unique key, item_description char(20))");
            statement.executeUpdate("create table if not exists to_supply(item_no_fk int, foreign key (item_no_fk) references items(item_no), contract_no_fk int, foreign key (contract_no_fk) references contracts(contract_no), contract_price decimal(8,2), contract_amount int, primary key(item_no_fk, contract_no_fk))");
            statement.executeUpdate("create table if not exists made_of(item_no_fk int, foreign key (item_no_fk) references items(item_no), order_no_fk int, foreign key (order_no_fk) references orders(order_no), order_qty int)");
            con.close();
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }


    public static boolean isValidOrder(int ORDER_NO) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select * from orders where order_no =  " + ORDER_NO);
            if (!rs.isBeforeFirst()) {
                con.close();
                return false;
            } else {
                con.close();
                return true;
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return false;
    }

    public static boolean isValidContract(int CONTRACT_NO) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select * from contracts where contract_no =  " + CONTRACT_NO);
            if (!rs.isBeforeFirst()) {
                con.close();
                return false;
            } else {
                con.close();
                return true;
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return false;
    }

    public static int findTotalQuantity(int ITEM_NO, int CONTRACT_NO) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            ResultSet rs =
                    rs = statement.executeQuery("select contract_amount from to_supply where item_no_fk = " + ITEM_NO + " and contract_no_fk = " + CONTRACT_NO);
            if (!rs.isBeforeFirst()) {
                return 0;
            }
            rs.next();
            return rs.getInt(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    public static int findBoughtQuantity(int ITEM_NO, int CONTRACT_NO) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select sum(order_qty) from made_of where item_no_fk =" + ITEM_NO + " and (select contract_no_fk from orders where order_no = order_no_fk) = " + CONTRACT_NO);
            rs.next();
            return rs.getInt(1);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        return 0;
    }


    public static boolean findQuantityAvailable(int ITEM_NO, int CONTRACT_NO) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select contract_amount from to_supply where item_no_fk = " + ITEM_NO + " and contract_no_fk = " + CONTRACT_NO);
            if (!rs.isBeforeFirst()) {
                System.out.println("Item is not included in the contract");
                return false;
            }
            rs.next();
            int total = rs.getInt(1);
            rs = statement.executeQuery("select sum(order_qty) from made_of where item_no_fk =" + ITEM_NO + " and (select contract_no_fk from orders where order_no = order_no_fk) = " + CONTRACT_NO);
            rs.next();
            int available = total - rs.getInt(1);
            System.out.println("The amount available is: " + available);
            con.close();
            return true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return false;
    }

    public static void findContractWithSupplier(int SUPPLIER_NO) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select contract_no from contracts where supplier_no_fk = " + SUPPLIER_NO);
            if (!rs.isBeforeFirst()) {
                System.out.println("No contracts found.");
                return;
            }
            HashSet<Integer> set = new HashSet<>();
            StringBuilder sb = new StringBuilder();
            sb.append("Supplier found in contracts: ");
            while (rs.next()) {
                set.add(rs.getInt(1));
            }
            for (int i : set) {
                sb.append(i).append(", ");
            }
            sb.deleteCharAt(sb.length() - 2);
            System.out.println(sb.toString());
            con.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Failed");
        }
        System.out.println("\n");
    }

    public static void findPriceInContract(int ITEM_NO, int CONTRACT_NO) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select contract_price from to_supply where contract_no_fk = " + CONTRACT_NO + " and item_no_fk = " + ITEM_NO);
            if (!rs.isBeforeFirst()) {
                System.out.println("Item not found in contract");
            } else {
                rs.next();
                System.out.println("Item Price in Contract " + CONTRACT_NO + ": " + rs.getBigDecimal(1).toString());
            }
            con.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
        System.out.println("\n");

    }

    public static void findOrdersContaining(int ITEM_NO) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select order_no_fk from made_of where item_no_fk = " + ITEM_NO);
            if (!rs.isBeforeFirst()) {
                System.out.println("Item was not found in any orders");
                con.close();
                return;
            }
            HashSet<Integer> set = new HashSet<>();
            StringBuilder sb = new StringBuilder();
            sb.append("Item found in orders: ");
            while (rs.next()) {
                set.add(rs.getInt(1));
            }
            for (int i : set) {
                sb.append(i).append(", ");
            }
            sb.deleteCharAt(sb.length() - 2);
            System.out.println(sb.toString());
            con.close();
        } catch (SQLException exception) {
            exception.printStackTrace();
            System.out.println("Failed");
        }
        System.out.println("\n");
    }

    public static void findPriceInOrder(int ITEM_NO, int ORDER_NO) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select contract_no_fk from orders where order_no = " + ORDER_NO);
            rs.next();
            int contractNo = rs.getInt(1);
            rs = statement.executeQuery("select contract_price from to_supply where item_no_fk = " + ITEM_NO + " and contract_no_fk = " + contractNo);
            rs.next();
            System.out.println("Item Price: " + rs.getBigDecimal(1));
            con.close();
        } catch (SQLException exception) {
            System.out.println("Failed to find item in contract.");
        }
        System.out.println();
    }

    public static void getOrderInfo(int ORDER_NO) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select * from orders where order_no = " + ORDER_NO);
            rs.next();
            String date = "n/a";
            if (rs.getDate(5) != null) {
                date = rs.getDate(5).toString();
            }
            System.out.println("Order Number: " + ORDER_NO + "\tContract Number: " + rs.getInt(2) + "\tProject Number: " + rs.getInt(3) + "\tDate Required: " + rs.getDate(4).toString() + "\tDate Completed: " + date);
            rs = statement.executeQuery("select item_no_fk from made_of where order_no_fk = " + ORDER_NO);
            ArrayList<Integer> itemsList = new ArrayList<>();
            while (rs.next()) {
                itemsList.add(rs.getInt(1));
            }
            ArrayList<String> itemNamesList = new ArrayList<>();
            ArrayList<Integer> quantity = new ArrayList<>();
            for (int i : itemsList) {
                rs = statement.executeQuery("select item_description from items where item_no = " + i);
                while (rs.next()) {
                    itemNamesList.add(rs.getString(1));
                }
            }
            rs = statement.executeQuery("select order_qty from made_of where order_no_fk = " + ORDER_NO);
            while (rs.next()) {
                quantity.add(rs.getInt(1));
            }

            System.out.printf("%10s %20s %10s", "Item Number", "Item Name", "Quantity");
            for (int i = 0; i < itemNamesList.size(); i++) {
                System.out.printf("%n%10d %20s %10d", itemsList.get(i), itemNamesList.get(i), quantity.get(i));
            }
            System.out.println("\n");
            con.close();
        } catch (SQLException exception) {
            exception.printStackTrace();

        }
    }

    public static boolean madeOf(int ITEM_NO, int ORDER_NO, int ORDER_QTY) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            //Check how many are available
            //Checks the amount the contract with the item number and contract number(from orders) has.
            ResultSet rs = statement.executeQuery("select contract_no_fk from orders where order_no = " + ORDER_NO);
            rs.next();
            int contractNo = rs.getInt(1);
            rs = statement.executeQuery("select contract_amount from to_supply where item_no_fk = " + ITEM_NO + " and contract_no_fk = " + contractNo);
            rs.next();
            int total = rs.getInt(1);
            rs = statement.executeQuery("select sum(order_qty) from made_of where item_no_fk = " + ITEM_NO + " and (select contract_no_fk from orders where order_no = " + ORDER_NO + ") = " + contractNo);
            rs.next();
            int available = total - rs.getInt(1);
            if (available - ORDER_QTY >= 0) {
                rs = statement.executeQuery("select sum(order_qty) from made_of where order_no_fk = " + ORDER_NO);
                rs.next();
                if (!(rs.getInt(1) + ORDER_QTY > 100)) {
                    statement.executeUpdate("insert into made_of values (" + ITEM_NO + ", " + ORDER_NO + ", " + ORDER_QTY + ")");
                } else {
                    System.out.println("Max number of items per order exceeded with " + ORDER_QTY + ". Amount of items available in order: " + rs.getInt(1));
                }
            } else {
                System.out.println("Contract quantity exceeded, order item addition failed.");
                con.close();
                return false;
            }

            con.close();
        } catch (SQLIntegrityConstraintViolationException exception) {
            System.out.println("Item addition failed");
            return false;
        } catch (SQLException exception) {
            System.out.println("Item addition failed");
            return false;
        }
        return false;
    }

    public static boolean addOrder(int ORDER_NO, int CONTRACT_NO, int PROJECT_NO, String DATE_REQUIRED) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select count(*) from orders where contract_no_fk = " + CONTRACT_NO);
            if (rs.isBeforeFirst()) {
                if (rs.getInt(1) == 6000) {
                    System.out.println("Maximum number of orders for this contract reached: 6000");
                    con.close();
                    return false;
                }
            }

            int result = statement.executeUpdate("insert into orders values (" + ORDER_NO + "," + CONTRACT_NO + "," + PROJECT_NO + ",\"" + DATE_REQUIRED + "\", null)");
            System.out.println("Order successfully added\n");
            con.close();
            return true;
        } catch (SQLIntegrityConstraintViolationException exception) {
            System.out.println("Order creation failed");
            return false;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return false;
        }
    }

    public static void addSupplier(int SUPPLIER_NO, String SUPPLIER_ADDRESS, String SUPPLIER_NAME) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            int result = statement.executeUpdate("insert into suppliers values (" + SUPPLIER_NO + ", \"" + SUPPLIER_ADDRESS + "\",\"" + SUPPLIER_NAME + "\")");
            System.out.println("Supplier successfully added\n");
            con.close();
        } catch (SQLIntegrityConstraintViolationException exception) {
            System.out.println("Supplier with number: \"" + SUPPLIER_NO + "\" already exists\n");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void addItem(int ITEM_NO, String ITEM_DESCRIPTION) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            int result = statement.executeUpdate("insert into items values (" + ITEM_NO + ", \"" + ITEM_DESCRIPTION + "\")");
            System.out.println("Item successfully added\n");
            con.close();
        } catch (SQLIntegrityConstraintViolationException exception) {
            System.out.println("Item with number: \"" + ITEM_NO + "\" already exists\n");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static void addProject(int PROJECT_NO, String PROJECT_DATA) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            int result = statement.executeUpdate("insert into projects values (" + PROJECT_NO + ", \"" + PROJECT_DATA + "\")");
            System.out.println("Project successfully added\n");
            con.close();
        } catch (SQLIntegrityConstraintViolationException exception) {
            System.out.println("Project with number: \"" + PROJECT_NO + "\" already exists\n");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public static boolean addContract(int CONTRACT_NO, int SUPPLIER_NO, String DATE_OF_CONTRACT) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            ResultSet rs = statement.executeQuery("select count(*) from contracts");
            rs.next();
            if (rs.getInt(1) == 50) {
                System.out.println("Maximum of 50 Contracts Reached");
                con.close();
                return false;
            }
            rs = statement.executeQuery("select count(*) from contracts where supplier_no_fk = " + SUPPLIER_NO);
            rs.next();
            if (rs.getInt(1) == 10) {
                System.out.println("Max contracts reached for this supplier");
                con.close();
                return false;
            }
            int result = statement.executeUpdate("insert into contracts values (" + CONTRACT_NO + ", " + SUPPLIER_NO + ",\"" + DATE_OF_CONTRACT + "\")");
            System.out.println("Contract successfully added\n");
            con.close();
            return true;
        } catch (SQLIntegrityConstraintViolationException exception) {
            System.out.println("Contract creation with number: \"" + CONTRACT_NO + "\" failed. Double check your contract number / supplier number\n");
            return false;
        } catch (SQLException exception) {
            exception.printStackTrace();
            return true;
        }
    }

    public static void addToSupply(int CONTRACT_NO, int ITEM_NO, double CONTRACT_PRICE, int CONTRACT_AMOUNT) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/turner_construction", "root", "Password1!");
            Statement statement = con.createStatement();
            //Check how many items the contract already contains
            ResultSet rs = statement.executeQuery("select count(contract_no_fk) from to_supply where contract_no_fk = 1");
            rs.next();
            //Compare count against limit
            if (rs.getInt(1) > 499) {
                throw new LimitExceedException();
            }
            int result = statement.executeUpdate("insert into to_supply values (" + ITEM_NO + ", " + CONTRACT_NO + "," + CONTRACT_PRICE + ", " + CONTRACT_AMOUNT + ")");
            System.out.println("To supply contract successfully added\n");
            con.close();
        } catch (LimitExceedException exception) {
            System.out.println(exception.getMessage());
        } catch (SQLIntegrityConstraintViolationException exception) {
            System.out.println("To supply contract creation failed. Double check your contract number / item number\n");
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static class LimitExceedException extends Exception {
        public LimitExceedException() {
            super("Limit has been exceeded on this type.");
        }
    }
}
