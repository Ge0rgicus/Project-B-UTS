package Assignment2;

import java.util.ArrayList;
import java.util.HashMap;

// MODEL - handles all data and business logic.
// It never touches the GUI. It just does the work and returns a result string.
public class LollyModel {

    private LollyInventory inventory;       // all lollies currently in stock
    private LollySales sales;               // completed sales history
    private LolliesRecommended recommended; // used for size-based recommendations

    // Set up with some starting data so the app isn't empty on launch
    public LollyModel() {
        inventory   = new LollyInventory();
        sales       = new LollySales();
        recommended = new LolliesRecommended();

        // Starting stock
        inventory.addLolly(new Lollies("Rainbow Pop",  LollySize.LARGE,  "Rainbow", 4.50));
        inventory.addLolly(new Lollies("Rainbow Pop",  LollySize.LARGE,  "Rainbow", 4.50));
        inventory.addLolly(new Lollies("Choco Swirl",  LollySize.MEDIUM, "Brown",   3.20));
        inventory.addLolly(new Lollies("Mint Twist",   LollySize.SMALL,  "Green",   2.00));
        inventory.addLolly(new Lollies("Berry Blast",  LollySize.MEDIUM, "Red",     3.00));

        // Starting sales so recommendations work right away
        recommended.recordSale(new Lollies("Mint Twist",  LollySize.SMALL,  "Green",   2.00));
        recommended.recordSale(new Lollies("Berry Blast", LollySize.MEDIUM, "Red",     3.00));
        recommended.recordSale(new Lollies("Rainbow Pop", LollySize.LARGE,  "Rainbow", 4.50));
    }


    // ADD LOLLY
    // Called by the Controller with values the user typed in the add form.
    // Validates everything before adding to inventory.
    // MVC flow: View (user fills form) -> Controller (reads fields)
    //        -> Model (validates + adds) -> Controller -> View (shows result)
    public String addLolly(String name, String colour, String priceText, String sizeText) {
        // check nothing was left blank
        if (name.isEmpty() || colour.isEmpty() || priceText.isEmpty()) {
            return "Please fill in all fields.";
        }

        // try to parse price - catch any non-number input
        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            return "Price must be a number.";
        }

        if (price <= 0) {
            return "Price must be greater than zero.";
        }

        // convert the size string from the ComboBox to an enum value
        LollySize size;
        if (sizeText.equals("LARGE")) {
            size = LollySize.LARGE;
        } else if (sizeText.equals("MEDIUM")) {
            size = LollySize.MEDIUM;
        } else {
            size = LollySize.SMALL;
        }

        inventory.addLolly(new Lollies(name, size, colour, price));
        return "Added: " + name;
    }


    // REMOVE LOLLY
    // Name comes from whatever is selected in the ListView.
    // MVC flow: View (user selects + clicks Remove) -> Controller
    //        -> Model (finds + removes) -> Controller -> View (refresh list)
    public String removeLolly(String name) {
        if (name.isEmpty()) {
            return "Please select a lolly from the list first.";
        }

        Lollies found = inventory.getLolly(name);
        if (found == null) {
            return "Not found: " + name;
        }

        inventory.removeLolly(name);
        return "Removed: " + name;
    }


    // MAKE A SALE
    // Handles card and cash payment, applies discount, records the sale
    // and removes the lolly from stock.
    // MVC flow: View (user fills sale form) -> Controller (reads fields)
    //        -> Model (validates + processes) -> Controller -> View (shows result)
    public String makeSale(String lollyName, String paymentType,
                           String cardNumber, String cashText) {
        if (lollyName.isEmpty()) {
            return "Please enter a lolly name.";
        }

        Lollies lolly = inventory.getLolly(lollyName);
        if (lolly == null) {
            return "Lolly not found in inventory.";
        }

        // apply 10% discount to get final price
        double finalPrice = sales.applyDiscount(lolly.getPrice());

        if (paymentType.equals("Card")) {
            if (cardNumber.isEmpty()) {
                return "Please enter a card number.";
            }

            CardPayment cardPayment = new CardPayment(finalPrice, cardNumber);
            cardPayment.processPayment();

            // record sale in both lists so it shows up in recommendations
            sales.recordSale(lolly);
            recommended.recordSale(lolly);
            inventory.removeLolly(lollyName);
            return "Sale complete! Price: $" + String.format("%.2f", finalPrice);

        } else {
            // cash payment
            if (cashText.isEmpty()) {
                return "Please enter cash amount.";
            }

            double cashGiven;
            try {
                cashGiven = Double.parseDouble(cashText);
            } catch (NumberFormatException e) {
                return "Cash amount must be a number.";
            }

            CashPayment cashPayment = new CashPayment(finalPrice, cashGiven);
            boolean success = cashPayment.processPayment();
            if (!success) {
                return "Not enough cash. Price is $" + String.format("%.2f", finalPrice);
            }

            double change = cashPayment.getChange();

            sales.recordSale(lolly);
            recommended.recordSale(lolly);
            inventory.removeLolly(lollyName);
            return "Sale complete! Price: $" + String.format("%.2f", finalPrice)
                    + " | Change: $" + String.format("%.2f", change);
        }
    }


    // Returns inventory as a String array so the View can load it into the ListView
    public String[] getInventoryList() {
        ArrayList<Lollies> stock = inventory.getStock();
        if (stock.size() == 0) {
            return new String[]{"Inventory is empty."};
        }
        String[] result = new String[stock.size()];
        for (int i = 0; i < stock.size(); i++) {
            result[i] = stock.get(i).toString();
        }
        return result;
    }


    // Returns sales history as a String array for the sales window
    public String[] getSalesList() {
        ArrayList<Lollies> soldList = sales.getSold();
        if (soldList.size() == 0) {
            return new String[]{"No sales recorded yet."};
        }
        String[] result = new String[soldList.size()];
        for (int i = 0; i < soldList.size(); i++) {
            result[i] = soldList.get(i).toString();
        }
        return result;
    }


    // SORT BY NAME
    // MVC flow: View (button click) -> Controller -> Model (sorts)
    //        -> Controller -> View (calls refreshInventory)
    public String sortByName() {
        inventory.sortByName();
        return "Sorted by name.";
    }


    // SORT BY SIZE - same flow as sort by name
    public String sortBySize() {
        inventory.sortBySize();
        return "Sorted by size.";
    }


    // FILTER BY COLOUR
    // Builds a sub-list of lollies that match the given colour.
    // MVC flow: View (user types colour) -> Controller
    //        -> Model (searches stock) -> Controller -> View (shows results)
    public String[] filterByColour(String colour) {
        if (colour.isEmpty()) {
            return new String[]{"Please enter a colour."};
        }

        ArrayList<Lollies> stock = inventory.getStock();
        ArrayList<String> results = new ArrayList<String>();

        for (int i = 0; i < stock.size(); i++) {
            if (stock.get(i).getColour().equalsIgnoreCase(colour)) {
                results.add(stock.get(i).toString());
            }
        }

        if (results.size() == 0) {
            return new String[]{"No lollies found with colour: " + colour};
        }

        // convert ArrayList to array to return to the View
        String[] arr = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            arr[i] = results.get(i);
        }
        return arr;
    }


    // CHECK LOW STOCK
    // Uses a HashMap to count how many of each lolly are left,
    // then warns about any at or below the threshold.
    // MVC flow: View (button click) -> Controller -> Model (checks counts)
    //        -> Controller -> View (shows warnings in status bar)
    public String[] checkLowStock() {
        HashMap<String, Integer> count = inventory.getStockCount();
        int threshold = inventory.getLowStockThreshold();
        ArrayList<String> warnings = new ArrayList<String>();

        for (String name : count.keySet()) {
            if (count.get(name) <= threshold) {
                warnings.add("LOW STOCK: " + name + " (" + count.get(name) + " left)");
            }
        }

        if (warnings.size() == 0) {
            return new String[]{"All stock levels are fine."};
        }

        String[] arr = new String[warnings.size()];
        for (int i = 0; i < warnings.size(); i++) {
            arr[i] = warnings.get(i);
        }
        return arr;
    }


    // RECOMMEND BY SIZE
    // Searches sold history for a lolly matching the chosen size.
    // Gets better over time as more sales are made.
    // MVC flow: View (user picks size) -> Controller
    //        -> Model (searches recommended list) -> Controller -> View
    public String recommendBySize(String sizeText) {
        LollySize size;
        if (sizeText.equals("LARGE")) {
            size = LollySize.LARGE;
        } else if (sizeText.equals("MEDIUM")) {
            size = LollySize.MEDIUM;
        } else {
            size = LollySize.SMALL;
        }

        Lollies result = recommended.recommendBySize(size);
        if (result == null) {
            return "No recommendation available for size: " + sizeText;
        }
        return "Recommended: " + result.toString();
    }
}