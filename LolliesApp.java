package Assignment2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

// This file has all the domain classes - the "things" the app works with.
// None of these classes touch the GUI, they just store and manage data.

// Enum for lolly size - limits choices to LARGE, MEDIUM or SMALL only
enum LollySize {
    LARGE, MEDIUM, SMALL
}

// Interface - any class that implements this must have a processPayment method
interface Payable {
    boolean processPayment();
}

// A single lolly with name, size, colour and price
class Lollies {
    private String name;
    private LollySize size;
    private String colour;
    private double price;

    // Full constructor - all details provided
    public Lollies(String name, LollySize size, String colour, double price) {
        this.name = name;
        this.size = size;
        this.colour = colour;
        this.price = price;
    }

    // Overloaded constructor - no size or colour given, so we use defaults
    // Same method name, different parameters = method overloading
    public Lollies(String name, double price) {
        this(name, LollySize.MEDIUM, "Unknown", price);
    }

    public String getName()    { return name; }
    public LollySize getSize() { return size; }
    public String getColour()  { return colour; }
    public double getPrice()   { return price; }

    // Used to display each lolly as a line in the ListView
    @Override
    public String toString() {
        return name + " | " + size + " | " + colour + " | $" + String.format("%.2f", price);
    }
}

// LollyDetails IS-A Lollies but adds dietary info (inheritance)
class LollyDetails extends Lollies {
    private boolean sugarFree;
    private boolean glutenFree;

    public LollyDetails(String name, LollySize size, String colour,
                        double price, boolean sugarFree, boolean glutenFree) {
        super(name, size, colour, price);
        this.sugarFree = sugarFree;
        this.glutenFree = glutenFree;
    }

    // Add dietary info to the normal display string
    @Override
    public String toString() {
        return super.toString() + " | SugarFree=" + sugarFree + " | GlutenFree=" + glutenFree;
    }
}

// Holds all lollies currently in stock
// ArrayList stores them, HashMap is used to count stock by name
class LollyInventory {
    private ArrayList<Lollies> stock = new ArrayList<Lollies>();
    private int lowStockThreshold = 2; // warn if 2 or fewer left

    public void addLolly(Lollies lolly) {
        stock.add(lolly);
    }

    // Overloaded - add by name and price only, no other details needed
    public void addLolly(String name, double price) {
        addLolly(new Lollies(name, price));
    }

    // Remove one lolly with this name (one sale = one lolly removed)
    public void removeLolly(String name) {
        for (int i = 0; i < stock.size(); i++) {
            if (stock.get(i).getName().equalsIgnoreCase(name)) {
                stock.remove(i);
                return;
            }
        }
    }

    // Find a lolly by name, returns null if not found
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

    // Sort stock alphabetically by name
    public void sortByName() {
        Collections.sort(stock, new Comparator<Lollies>() {
            public int compare(Lollies a, Lollies b) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
    }

    // Sort by size (LARGE first, SMALL last)
    public void sortBySize() {
        Collections.sort(stock, new Comparator<Lollies>() {
            public int compare(Lollies a, Lollies b) {
                return a.getSize().compareTo(b.getSize());
            }
        });
    }

    // Build a HashMap of name -> how many are in stock
    // HashMap lets us look up counts by name quickly
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
class LollySales {
    protected ArrayList<Lollies> sold = new ArrayList<Lollies>();
    protected double discountRate = 0.10;

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

// Extends LollySales to add recommendations based on past sales
// Overrides applyDiscount to give 20% off instead of 10%
// Same method name, different behaviour in subclass = method overriding
class LolliesRecommended extends LollySales {

    // Look through sold history for a lolly matching the requested size
    public Lollies recommendBySize(LollySize size) {
        for (Lollies lolly : sold) {
            if (lolly.getSize() == size) {
                return lolly;
            }
        }
        return null;
    }

    // Recommended lollies get 20% off instead of the usual 10%
    @Override
    public double applyDiscount(double price) {
        return price - (price * 0.20);
    }
}

// Stores basic customer info
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

// Base payment class - implements Payable
// CardPayment and CashPayment both extend this
class Payment implements Payable {
    protected double amount;

    public Payment(double amount) {
        this.amount = amount;
    }

    // Subclasses override this with their own logic
    public boolean processPayment() {
        return true;
    }
}

// Card payment - succeeds as long as a card number was entered
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

    // Returns false if the customer didn't give enough
    @Override
    public boolean processPayment() {
        return cashGiven >= amount;
    }

    public double getChange() {
        return cashGiven - amount;
    }
}