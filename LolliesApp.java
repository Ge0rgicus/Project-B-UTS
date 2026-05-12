package Assignment2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

// This file holds all the domain classes for the lolly shop.
// These are the "building blocks" that the Model uses to store
// and manage data. None of these classes know about the GUI.

// Enum for lolly size - using an enum means we can't accidentally
// pass in a random string like "medium" or "med", it has to be
// one of these three values
enum LollySize {
    LARGE, MEDIUM, SMALL
}

// Payable interface - both CardPayment and CashPayment implement this
// so we can treat them the same way when processing a payment
interface Payable {
    boolean processPayment();
}

// Main lolly class - stores all the info about a single lolly
class Lollies {
    private String name;
    private LollySize size;
    private String colour;
    private double price;

    // Full constructor - used when we know all the details
    public Lollies(String name, LollySize size, String colour, double price) {
        this.name = name;
        this.size = size;
        this.colour = colour;
        this.price = price;
    }

    // Overloaded constructor - if size and colour aren't given we use defaults
    // This is method overloading: same method name, different parameters
    public Lollies(String name, double price) {
        this(name, LollySize.MEDIUM, "Unknown", price);
    }

    public String getName()    { return name; }
    public LollySize getSize() { return size; }
    public String getColour()  { return colour; }
    public double getPrice()   { return price; }

    // toString formats the lolly nicely for display in the ListView
    @Override
    public String toString() {
        return name + " | " + size + " | " + colour + " | $" + String.format("%.2f", price);
    }
}

// LollyDetails extends Lollies to add dietary info
// This shows inheritance - LollyDetails IS-A Lollies but with extra fields
class LollyDetails extends Lollies {
    private boolean sugarFree;
    private boolean glutenFree;

    public LollyDetails(String name, LollySize size, String colour,
                        double price, boolean sugarFree, boolean glutenFree) {
        super(name, size, colour, price);
        this.sugarFree = sugarFree;
        this.glutenFree = glutenFree;
    }

    // Override toString to also show the dietary info
    @Override
    public String toString() {
        return super.toString() + " | SugarFree=" + sugarFree + " | GlutenFree=" + glutenFree;
    }
}

// Manages the list of lollies currently in stock
// Uses an ArrayList to store them and a HashMap to count stock levels
class LollyInventory {
    private ArrayList<Lollies> stock = new ArrayList<Lollies>();
    private int lowStockThreshold = 2; // warn if a lolly has 2 or fewer left

    public void addLolly(Lollies lolly) {
        stock.add(lolly);
    }

    // Overloaded version - add by name and price only
    public void addLolly(String name, double price) {
        addLolly(new Lollies(name, price));
    }

    // Remove the first lolly found with this name
    // We only remove one at a time because one sale = one lolly
    public void removeLolly(String name) {
        for (int i = 0; i < stock.size(); i++) {
            if (stock.get(i).getName().equalsIgnoreCase(name)) {
                stock.remove(i);
                return;
            }
        }
    }

    // Find and return a lolly by name, or null if it doesn't exist
    public Lollies getLolly(String name) {
        for (Lollies lolly : stock) {
            if (lolly.getName().equalsIgnoreCase(name)) {
                return lolly;
            }
        }
        return null;
    }

    public ArrayList<Lollies> getStock() {
        return stock;
    }

    // Sort the stock list alphabetically by name
    public void sortByName() {
        Collections.sort(stock, new Comparator<Lollies>() {
            public int compare(Lollies a, Lollies b) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
    }

    // Sort by size (LARGE first, then MEDIUM, then SMALL)
    public void sortBySize() {
        Collections.sort(stock, new Comparator<Lollies>() {
            public int compare(Lollies a, Lollies b) {
                return a.getSize().compareTo(b.getSize());
            }
        });
    }

    // Build a HashMap of lolly name -> how many are in stock
    // We use a HashMap here so we can quickly look up counts by name
    public HashMap<String, Integer> getStockCount() {
        HashMap<String, Integer> count = new HashMap<String, Integer>();
        for (Lollies lolly : stock) {
            String name = lolly.getName();
            if (!count.containsKey(name)) {
                count.put(name, 0);
            }
            count.put(name, count.get(name) + 1);
        }
        return count;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }
}

// Tracks completed sales and applies a 10% discount
// LolliesRecommended extends this class
class LollySales {
    protected ArrayList<Lollies> sold = new ArrayList<Lollies>();
    protected double discountRate = 0.10;

    // Work out the discounted price
    public double applyDiscount(double price) {
        return price - (price * discountRate);
    }

    public void recordSale(Lollies lolly) {
        sold.add(lolly);
    }

    public ArrayList<Lollies> getSold() {
        return sold;
    }
}

// Extends LollySales to add recommendation logic
// It overrides applyDiscount to give 20% off for recommended lollies
// Shares the sold list with LollySales so recommendations reflect real sales
class LolliesRecommended extends LollySales {

    // Look through the sold list for a lolly matching the requested size
    public Lollies recommendBySize(LollySize size) {
        for (Lollies lolly : sold) {
            if (lolly.getSize() == size) {
                return lolly;
            }
        }
        return null;
    }

    // Override the discount - recommended lollies get 20% off instead of 10%
    // This is method overriding: same method name, different behaviour in subclass
    @Override
    public double applyDiscount(double price) {
        return price - (price * 0.20);
    }
}

// Simple customer class to store contact details
class Customer {
    private String name;
    private String email;

    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @Override
    public String toString() {
        return name + " | " + email;
    }
}

// Base payment class - implements the Payable interface
// CardPayment and CashPayment both extend this
class Payment implements Payable {
    protected double amount;

    public Payment(double amount) {
        this.amount = amount;
    }

    // Default - subclasses override this with their own logic
    public boolean processPayment() {
        return true;
    }
}

// Card payment - always succeeds as long as a card number is entered
class CardPayment extends Payment {
    private String cardNumber;

    public CardPayment(double amount, String cardNumber) {
        super(amount);
        this.cardNumber = cardNumber;
    }

    @Override
    public boolean processPayment() {
        return true;
    }
}

// Cash payment - only succeeds if enough cash was given
class CashPayment extends Payment {
    private double cashGiven;

    public CashPayment(double amount, double cashGiven) {
        super(amount);
        this.cashGiven = cashGiven;
    }

    // Check if the customer gave enough cash
    @Override
    public boolean processPayment() {
        return cashGiven >= amount;
    }

    // Calculate how much change to give back
    public double getChange() {
        return cashGiven - amount;
    }
}