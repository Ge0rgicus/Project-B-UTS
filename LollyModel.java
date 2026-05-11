package Assignment2;

import java.util.ArrayList;
import java.util.HashMap;

public class LollyModel {

    private LollyInventory inventory;
    private LollySales sales;
    private LolliesRecommended recommended;

    public LollyModel() {
        inventory = new LollyInventory();
        sales = new LollySales();
        recommended = new LolliesRecommended();

        inventory.addLolly(new Lollies("Rainbow Pop", LollySize.LARGE, "Rainbow", 4.50));
        inventory.addLolly(new Lollies("Rainbow Pop", LollySize.LARGE, "Rainbow", 4.50));
        inventory.addLolly(new Lollies("Choco Swirl", LollySize.MEDIUM, "Brown", 3.20));
        inventory.addLolly(new Lollies("Mint Twist", LollySize.SMALL, "Green", 2.00));
        inventory.addLolly(new Lollies("Berry Blast", LollySize.MEDIUM, "Red", 3.00));

        recommended.recordSale(new Lollies("Mint Twist", LollySize.SMALL, "Green", 2.00));
        recommended.recordSale(new Lollies("Berry Blast", LollySize.MEDIUM, "Red", 3.00));
        recommended.recordSale(new Lollies("Rainbow Pop", LollySize.LARGE, "Rainbow", 4.50));
    }

    public String addLolly(String name, String colour, String priceText, String sizeText) {
        if (name.isEmpty() || colour.isEmpty() || priceText.isEmpty()) {
            return "Please fill in all fields.";
        }
        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            return "Price must be a number.";
        }
        if (price <= 0) {
            return "Price must be greater than zero.";
        }
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

    public String makeSale(String lollyName, String paymentType, String cardNumber, String cashText) {
        if (lollyName.isEmpty()) {
            return "Please enter a lolly name.";
        }
        Lollies lolly = inventory.getLolly(lollyName);
        if (lolly == null) {
            return "Lolly not found in inventory.";
        }
        double finalPrice = sales.applyDiscount(lolly.getPrice());

        if (paymentType.equals("Card")) {
            if (cardNumber.isEmpty()) {
                return "Please enter a card number.";
            }
            CardPayment payment = new CardPayment(finalPrice, cardNumber);
            payment.processPayment();
            sales.recordSale(lolly);
            inventory.removeLolly(lollyName);
            return "Sale complete! Price: $" + String.format("%.2f", finalPrice);

        } else {
            if (cashText.isEmpty()) {
                return "Please enter cash amount.";
            }
            double cashGiven;
            try {
                cashGiven = Double.parseDouble(cashText);
            } catch (NumberFormatException e) {
                return "Cash amount must be a number.";
            }
            CashPayment payment = new CashPayment(finalPrice, cashGiven);
            boolean success = payment.processPayment();
            if (!success) {
                return "Not enough cash. Price is $" + String.format("%.2f", finalPrice);
            }
            double change = payment.getChange();
            sales.recordSale(lolly);
            inventory.removeLolly(lollyName);
            return "Sale complete! Price: $" + String.format("%.2f", finalPrice)
                    + " | Change: $" + String.format("%.2f", change);
        }
    }

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

    public String sortByName() {
        inventory.sortByName();
        return "Sorted by name.";
    }

    public String sortBySize() {
        inventory.sortBySize();
        return "Sorted by size.";
    }

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
        String[] arr = new String[results.size()];
        for (int i = 0; i < results.size(); i++) {
            arr[i] = results.get(i);
        }
        return arr;
    }

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