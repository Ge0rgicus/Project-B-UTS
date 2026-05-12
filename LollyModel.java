package Assignment2;

import java.util.ArrayList;
import java.util.HashMap;

// MODEL - this is the data and logic layer of MVC
// It handles all the business rules and data changes.
// The Model never touches the GUI - it just does the work
// and returns a result string back to the Controller.
public class LollyModel {

    private LollyInventory inventory;       // holds all lollies currently in stock
    private LollySales sales;               // keeps track of completed sales
    private LolliesRecommended recommended; // used to recommend lollies by size

    // Set up the model with some starting data so the app isn't empty on launch
    public LollyModel() {
        inventory   = new LollyInventory();
        sales       = new LollySales();
        recommended = new LolliesRecommended();

        // Add some sample lollies to inventory
        inventory.addLolly(new Lollies("Rainbow Pop",  LollySize.LARGE,  "Rainbow", 4.50));
        inventory.addLolly(new Lollies("Rainbow Pop",  LollySize.LARGE,  "Rainbow", 4.50));
        inventory.addLolly(new Lollies("Choco Swirl",  LollySize.MEDIUM, "Brown",   3.20));
        inventory.addLolly(new Lollies("Mint Twist",   LollySize.SMALL,  "Green",   2.00));
        inventory.addLolly(new Lollies("Berry Blast",  LollySize.MEDIUM, "Red",     3.00));

        // Add some starting sales history so recommendations work straight away
        recommended.recordSale(new Lollies("Mint Twist",  LollySize.SMALL,  "Green",   2.00));
        recommended.recordSale(new Lollies("Berry Blast", LollySize.MEDIUM, "Red",     3.00));
        recommended.recordSale(new Lollies("Rainbow Pop", LollySize.LARGE,  "Rainbow", 4.50));
    }


    // ADD LOLLY
    // The Controller calls this with values typed in by the user.
    // We validate everything here before touching the inventory.
    // MVC flow: View (user fills form) -> Controller (reads fields)
    //        -> Model (validates + adds) -> Controller (gets result)
    //        -> View (shows result message, refreshes list)
    public String addLolly(String name, String colour, String priceText, String sizeText) {
        // make sure nothing was left blank
        if (name.isEmpty() || colour.isEmpty() || priceText.isEmpty()) {
            return "Please fill in all fields.";
        }

        // try to parse the price - catch it if they typed letters
        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            return "Price must be a number.";
        }

        if (price <= 0) {
            return "Price must be greater than zero.";
        }

        // convert the size string from the ComboBox into an enum value
        LollySize size;
        if (sizeText.equals("LARGE")) {
            size = LollySize.LARGE;
        } else if (sizeText.equals("MEDIUM")) {
            size = LollySize.MEDIUM;
        } else {
            size = LollySize.SMALL;
        }

        // everything is valid so add it to the inventory
        inventory.addLolly(new Lollies(name, size, colour, price));
        return "Added: " + name;
    }


    // REMOVE LOLLY
    // Name comes from whatever the user selected in the ListView.
    // MVC flow: View (user clicks item + Remove) -> Controller
    //        -> Model (finds + removes) -> Controller -> View (refresh)
    public String removeLolly(String name) {
        if (name.isEmpty()) {
            return "Please select a lolly from the list first.";
        }

        // check it actually exists before trying to remove it
        Lollies found = inventory.getLolly(name);
        if (found == null) {
            return "Not found: " + name;
        }

        inventory.removeLolly(name);
        return "Removed: " + name;
    }


    // MAKE A SALE
    // This is the most complex method - it handles both card and cash,
    // applies a discount, processes the payment, records the sale,
    // and removes the lolly from inventory all in one go.
    // MVC flow: View (user fills sale form) -> Controller (reads fields)
    //        -> Model (validates, processes payment, updates data)
    //        -> Controller (gets result) -> View (shows result, refreshes)
    public String makeSale(String lollyName, String paymentType,
                           String cardNumber, String cashText) {
        if (lollyName.isEmpty()) {
            return "Please enter a lolly name.";
        }

        // find the lolly in the inventory
        Lollies lolly = inventory.getLolly(lollyName);
        if (lolly == null) {
            return "Lolly not found in inventory.";
        }

        // apply the 10% discount to get the final price
        double finalPrice = sales.applyDiscount(lolly.getPrice());

        if (paymentType.equals("Card")) {
            if (cardNumber.isEmpty()) {
                return "Please enter a card number.";
            }

            // process the card payment (always succeeds if a number was given)
            CardPayment cardPayment = new CardPayment(finalPrice, cardNumber);
            cardPayment.processPayment();

            // record the sale and remove from stock
            sales.recordSale(lolly);
            recommended.recordSale(lolly); // also add to recommended so it shows up in suggestions
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

            // check if they gave enough cash
            CashPayment cashPayment = new CashPayment(finalPrice, cashGiven);
            boolean success = cashPayment.processPayment();
            if (!success) {
                return "Not enough cash. Price is $" + String.format("%.2f", finalPrice);
            }

            double change = cashPayment.getChange();

            // record the sale and remove from stock
            sales.recordSale(lolly);
            recommended.recordSale(lolly); // also add to recommended so it shows up in suggestions
            inventory.removeLolly(lollyName);
            return "Sale complete! Price: $" + String.format("%.2f", finalPrice)
                    + " | Change: $" + String.format("%.2f", change);
        }
    }


    // Returns the inventory as a String array so the View can
    // put it straight into the ListView
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


    // Returns completed sales as a String array for the sales window
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
    // Model does the sort, Controller tells View to refresh
    // MVC flow: View (button click) -> Controller -> Model (sorts)
    //        -> Controller -> View (refreshInventory called)
    public String sortByName() {
        inventory.sortByName();
        return "Sorted by name.";
    }


    // SORT BY SIZE
    // Same flow as sort by name
    public String sortBySize() {
        inventory.sortBySize();
        return "Sorted by size.";
    }


    // FILTER BY COLOUR
    // Builds a sub-list from the inventory that matches the colour.
    // MVC flow: View (user types colour + clicks Filter) -> Controller
    //        -> Model (searches inventory) -> Controller -> View (shows results)
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

        // convert the ArrayList to a regular array to return to the View
        String[] arr = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            arr[i] = results.get(i);
        }
        return arr;
    }


    // CHECK LOW STOCK
    // Uses a HashMap to count how many of each lolly name are left,
    // then flags any that are at or below the threshold.
    // MVC flow: View (button click) -> Controller -> Model (checks map)
    //        -> Controller -> View (shows warning messages in status bar)
    public String[] checkLowStock() {
        HashMap<String, Integer> count = inventory.getStockCount();
        int threshold = inventory.getLowStockThreshold();
        ArrayList<String> warnings = new ArrayList<String>();

        // go through each entry in the map and check the count
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
    // Looks through the sold history to find a lolly of the chosen size.
    // Because we add to recommended every time a sale is made, this gets
    // better over time as more lollies are sold.
    // MVC flow: View (user picks size + clicks button) -> Controller
    //        -> Model (searches recommended list) -> Controller -> View
    public String recommendBySize(String sizeText) {
        // convert the size string to an enum value
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