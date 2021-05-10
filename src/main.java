import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class main {
    public static void main(String[] args) {
        mainMenu();
    }


    public static void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        String choice = "";
        while (!choice.equalsIgnoreCase("exit")) {
            System.out.println("Enter \"exit\" to exit the system");
            System.out.println("1: Add a new Supplier");
            System.out.println("2: Add a new Item");
            System.out.println("3: Add a new Project");
            System.out.println("4: Enter a new Contract");
            System.out.println("5: Enter a new Order");
            System.out.println("6: Inspect Orders");
            System.out.println("7: Find price of item in an order");
            System.out.println("8: Find orders containing item");
            System.out.println("9: Find price of item in contract");
            System.out.println("10: Find contract with supplier");
            System.out.println("11: Find quantity available in contract");
            System.out.println("12: Add items to contract");
            System.out.println("13: Add items to order");
            choice = scanner.nextLine();
            switch (choice) {
                case "1": {
                    addSupplier(scanner);
                }
                case "2": {
                    addItem(scanner);
                }
                case "3": {
                    addProject(scanner);
                }
                case "4": {
                    addContract(scanner);
                }
                case "5": {
                    addOrder(scanner);
                }
                case "6": {
                    inspectOrders(scanner);
                }
                case "7": {
                    findPriceInOrder(scanner);
                }
                case "8": {
                    findOrdersContaining(scanner);
                }
                case "9": {
                    findPriceInContract(scanner);
                }
                case "10": {
                    findContractWithSupplier(scanner);
                }
                case "11": {
                    findQuantityAvailable(scanner);
                }
                case "12": {
                    addItemsToContract(scanner);
                }
                case "13": {
                    addItemsToOrder(scanner);
                }
            }
        }
    }

    private static void addItemsToOrder(Scanner scanner) {
        System.out.println("Enter order number");
        int orderNo = scanner.nextInt();
        scanner.nextLine();
        String choice = "";
        if (!DBUtils.isValidOrder(orderNo)) {
            System.out.println("Invalid order number");
            return;
        }
        while (!choice.equalsIgnoreCase("stop")) {
            System.out.println("Enter item number to order");
            int itemNo = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Enter the quantity");
            int orderQuantity = scanner.nextInt();
            scanner.nextLine();
            DBUtils.madeOf(itemNo, orderNo, orderQuantity);
            System.out.println("Enter \"exit\" to exit, continue to continue adding items");
            choice = scanner.nextLine();
        }
    }

    private static void addItemsToContract(Scanner scanner) {
        System.out.println("Enter the contract number");
        int contractNumber = scanner.nextInt();
        scanner.nextLine();
        String choice = "";
        if (!DBUtils.isValidContract(contractNumber)) {
            System.out.println("Invalid contract number");
            return;
        }
        while (!choice.equalsIgnoreCase("stop")) {
            System.out.println("Enter the number of the item that is to be supplied");
            int itemNo = scanner.nextInt();
            scanner.nextLine();
            System.out.println("Enter the contract price for the item");
            double contractPrice = scanner.nextBigDecimal().doubleValue();
            scanner.nextLine();
            System.out.println("Enter the amount for the item");
            int contractAmount = scanner.nextInt();
            scanner.nextLine();
            DBUtils.addToSupply(contractNumber, itemNo, contractPrice, contractAmount);
            System.out.println("Enter \"stop\" to stop adding contract items, else enter continue");
            choice = scanner.nextLine();
        }
    }

    private static void findQuantityAvailable(Scanner scanner) {
        System.out.println("Enter the item number");
        int itemNo = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the contract number");
        int contractNo = scanner.nextInt();
        scanner.nextLine();
        DBUtils.findQuantityAvailable(itemNo, contractNo);
    }

    private static void findContractWithSupplier(Scanner scanner) {
        System.out.println("Enter the supplier number");
        int supplierNo = scanner.nextInt();
        scanner.nextLine();
        DBUtils.findContractWithSupplier(supplierNo);
    }

    private static void findPriceInContract(Scanner scanner) {
        System.out.println("Enter item number");
        int itemNo = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter contract number");
        int contractNo = scanner.nextInt();
        scanner.nextLine();
        DBUtils.findPriceInContract(itemNo, contractNo);
    }

    private static void findOrdersContaining(Scanner scanner) {
        System.out.println("Enter item number");
        int itemNo = scanner.nextInt();
        scanner.nextLine();
        DBUtils.findOrdersContaining(itemNo);
    }

    private static void findPriceInOrder(Scanner scanner) {
        System.out.println("Enter the item number");
        int itemNo = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter order number");
        int orderNo = scanner.nextInt();
        scanner.nextLine();
        DBUtils.findPriceInOrder(itemNo, orderNo);
    }

    private static void inspectOrders(Scanner scanner) {
        System.out.println("Enter the order number you would like to inspect");
        int orderNo = scanner.nextInt();
        scanner.nextLine();
        DBUtils.getOrderInfo(orderNo);
    }

    private static void addOrder(Scanner scanner) {
        System.out.println("Enter the order number");
        int orderNo = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the contract number");
        int contractNo = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the project number");
        int projectNo = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the date-required, use format YYYY-MM-DD");
        String dateRequired = scanner.nextLine();
        if (DBUtils.addOrder(orderNo, contractNo, projectNo, dateRequired)) {
            String choice = "";
            while (!choice.equalsIgnoreCase("stop")) {
                System.out.println("Enter item number to order");
                int itemNo = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter the quantity");
                int orderQuantity = scanner.nextInt();
                scanner.nextLine();
                DBUtils.madeOf(itemNo, orderNo, orderQuantity);
                System.out.println("Enter \"exit\" to exit, continue to continue adding items");
                choice = scanner.nextLine();
            }
        }
    }

    private static void addContract(Scanner scanner) {
        System.out.println("Enter the contract number");
        int contractNumber = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the supplier number");
        int supplierNo = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Use today's date? y/n");
        String date = "";
        String temp = scanner.nextLine();
        if (temp.equalsIgnoreCase("yes") || temp.equalsIgnoreCase("y")) {
            date = LocalDate.now().toString();
        } else {
            System.out.println("Please use formal YYYY-MM-DD");
            date = scanner.nextLine();
        }
        if (DBUtils.addContract(contractNumber, supplierNo, date)) {
            String choice = "";
            while (!choice.equalsIgnoreCase("stop")) {
                System.out.println("Enter the number of the item that is to be supplied");
                int itemNo = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Enter the contract price for the item");
                double contractPrice = scanner.nextBigDecimal().doubleValue();
                scanner.nextLine();
                System.out.println("Enter the amount for the item");
                int contractAmount = scanner.nextInt();
                scanner.nextLine();
                DBUtils.addToSupply(contractNumber, itemNo, contractPrice, contractAmount);
                System.out.println("Enter \"stop\" to stop adding contract items, else enter continue");
                choice = scanner.nextLine();
            }
        }
    }

    private static void addSupplier(Scanner scanner) {
        System.out.println("Enter supplier's number");
        int supplierNo = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter the supplier's address");
        String supplierAddress = scanner.nextLine();
        System.out.println("Enter the supplier's name");
        String supplierName = scanner.nextLine();
        DBUtils.addSupplier(supplierNo, supplierAddress, supplierName);
    }

    private static void addItem(Scanner scanner) {
        System.out.println("Enter the item number");
        int itemNo = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter item description");
        String itemDescription = scanner.nextLine();
        DBUtils.addItem(itemNo, itemDescription);
    }

    private static void addProject(Scanner scanner) {
        System.out.println("Enter Project Number");
        int projNo = scanner.nextInt();
        scanner.nextLine();
        System.out.println("Enter Project Data");
        String projData = scanner.next();
        DBUtils.addProject(projNo, projData);
    }

}
