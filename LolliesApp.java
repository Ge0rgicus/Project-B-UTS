package Assignment2;

import java.util.*;

enum LollySize {
    LARGE, MEDIUM, SMALL
}

interface Payable {
    boolean processPayment();
}

//YO Guys I did something and its not running.
//Didnt work - 17
//Help

class Lollies {
    private String name;
    private LollySize size;
    private String colour;
    private double price;

    public Lollies(String name, LollySize size, String colour, double price) {
        this.name = name;
        this.size = size;
        this.colour = colour;
        this.price = price;
    }

    public Lollies(String name, double price) {
        this(name, LollySize.MEDIUM, "Unknown", price);
    }

    public String getName() { return name; }
    public LollySize getSize() { return size; }
    public String getColour() { return colour; }
    public double getPrice() { return price; }

    public String toString() {
        return name + " | " + size + " | " + colour + " | $" + String.format("%.2f", price);
    }
}

class LollyDetails extends Lollies {
    private boolean sugarFree;
    private boolean glutenFree;

    public LollyDetails(String name, LollySize size, String colour, double price, boolean sugarFree, boolean glutenFree) {
        super(name, size, colour, price);
        this.sugarFree = sugarFree;
        this.glutenFree = glutenFree;
    }

    public String toString() {
        return super.toString() + " | SugarFree=" + sugarFree + " | GlutenFree=" + glutenFree;
    }
}

class LollyInventory {
    private ArrayList<Lollies> stock = new ArrayList<Lollies>();
    private int lowStockThreshold = 2;

    public void addLolly(Lollies l) {
        stock.add(l);
    }

    public void addLolly(String name, double price) {
        addLolly(new Lollies(name, price));
    }

    public void removeLolly(String name) {
        for (int i = 0; i < stock.size(); i++) {
            if (stock.get(i).getName().equalsIgnoreCase(name)) {
                stock.remove(i);
                return;
            }
        }
    }

    public Lollies getLolly(String name) {
        for (Lollies l : stock) {
            if (l.getName().equalsIgnoreCase(name)) return l;
        }
        return null;
    }

    public ArrayList<Lollies> getStock() {
        return stock;
    }

    public void sortByName() {
        Collections.sort(stock, new Comparator<Lollies>() {
            public int compare(Lollies a, Lollies b) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
        });
    }

    public void sortBySize() {
        Collections.sort(stock, new Comparator<Lollies>() {
            public int compare(Lollies a, Lollies b) {
                return a.getSize().compareTo(b.getSize());
            }
        });
    }

    public HashMap<String, Integer> getStockCount() {
        HashMap<String, Integer> count = new HashMap<String, Integer>();
        for (Lollies l : stock) {
            String name = l.getName();
            if (!count.containsKey(name)) count.put(name, 0);
            count.put(name, count.get(name) + 1);
        }
        return count;
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }
}

class LollySales {
    protected ArrayList<Lollies> sold = new ArrayList<Lollies>();
    protected double discountRate = 0.10;

    public double applyDiscount(double price) {
        return price - (price * discountRate);
    }

    public void recordSale(Lollies l) {
        sold.add(l);
    }

    public ArrayList<Lollies> getSold() {
        return sold;
    }
}

class LolliesRecommended extends LollySales {

    public Lollies recommendBySize(LollySize size) {
        for (Lollies l : sold) {
            if (l.getSize() == size) return l;
        }
        return null;
    }

    public double applyDiscount(double price) {
        return price - (price * 0.20);
    }
}

class Customer {
    private String name;
    private String email;

    public Customer(String n, String e) {
        name = n;
        email = e;
    }

    public String toString() {
        return name + " | " + email;
    }
}

class Payment implements Payable {
    protected double amount;

    public Payment(double amount) {
        this.amount = amount;
    }

    public boolean processPayment() {
        return true;
    }
}

class CardPayment extends Payment {
    private String cardNumber;

    public CardPayment(double amount, String cardNumber) {
        super(amount);
        this.cardNumber = cardNumber;
    }

    public boolean processPayment() {
        return true;
    }
}

class CashPayment extends Payment {
    private double cashGiven;

    public CashPayment(double amount, double cashGiven) {
        super(amount);
        this.cashGiven = cashGiven;
    }

    public boolean processPayment() {
        if (cashGiven >= amount) {
            return true;
        }
        return false;
    }

    public double getChange() {
        return cashGiven - amount;
    }
}