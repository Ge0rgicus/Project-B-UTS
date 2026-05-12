package Assignment2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// CONTROLLER - sits between the View and Model.
// When a button is clicked, the Controller reads the input from the View,
// sends it to the Model, gets a result back, and tells the View to update.
// The View and Model never talk to each other directly.
public class LollyController {

    private LollyModel model;
    private LollyView view;

    // Store both so we can pass messages between them
    public LollyController(LollyModel model, LollyView view) {
        this.model = model;
        this.view  = view;
    }


    // SORT BY NAME button
    // Controller tells Model to sort, then tells View to refresh
    public EventHandler<ActionEvent> getSortNameHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String result = model.sortByName();  // Model does the sort
                view.refreshInventory();             // View updates the list
                view.setStatus(result);              // View shows the message
            }
        };
    }


    // SORT BY SIZE button - same pattern as sort by name
    public EventHandler<ActionEvent> getSortSizeHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String result = model.sortBySize();
                view.refreshInventory();
                view.setStatus(result);
            }
        };
    }


    // CHECK LOW STOCK button
    // Model returns warnings, Controller joins them into one string for the View
    public EventHandler<ActionEvent> getLowStockHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String[] warnings = model.checkLowStock();
                String message = "";
                for (int i = 0; i < warnings.length; i++) {
                    message = message + warnings[i] + "   ";
                }
                view.setStatus(message);
            }
        };
    }


    // OPEN ADD WINDOW button - just tells the View to show the window
    public EventHandler<ActionEvent> getOpenAddHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.showAddWindow();
            }
        };
    }


    // ADD LOLLY button (inside add window)
    // MVC flow:
    //   1. Read what the user typed using View's getters
    //   2. Send it to the Model to validate and add
    //   3. Show the result in the add window
    //   4. If it worked, refresh the list and close the form
    public EventHandler<ActionEvent> getAddLollyHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                // step 1: read input from View
                String name   = view.getAddName();
                String colour = view.getAddColour();
                String price  = view.getAddPrice();
                String size   = view.getAddSize();

                // step 2: send to Model
                String result = model.addLolly(name, colour, price, size);

                // step 3: show result
                view.setAddStatus(result);

                // step 4: if it worked, update the View
                if (result.startsWith("Added")) {
                    view.refreshInventory();
                    view.closeAddWindow();
                    view.clearAddFields();
                    view.setStatus(result);
                }
            }
        };
    }


    // CANCEL button in the add window
    public EventHandler<ActionEvent> getCloseAddHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.closeAddWindow();
            }
        };
    }


    // REMOVE SELECTED button
    // Reads the selected lolly name from the View, passes to Model, refreshes list
    public EventHandler<ActionEvent> getRemoveHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String name = view.getSelectedLollyName();
                if (name.isEmpty()) {
                    view.setStatus("Please click a lolly in the list first, then click Remove.");
                    return;
                }

                String result = model.removeLolly(name);
                view.refreshInventory();
                view.setStatus(result);
            }
        };
    }


    // OPEN SALE WINDOW button
    // Pre-fills the lolly name if something is already selected in the list
    public EventHandler<ActionEvent> getOpenSaleHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String selectedName = view.getSelectedLollyName();
                if (!selectedName.isEmpty()) {
                    view.prefillSaleName(selectedName);
                }
                view.showSaleWindow();
            }
        };
    }


    // PROCESS PAYMENT button (inside sale window)
    // MVC flow:
    //   1. Read lolly name, payment type, card/cash from View
    //   2. Send to Model to validate and process
    //   3. Show result in sale window
    //   4. If it worked, refresh inventory, close window, clear fields
    public EventHandler<ActionEvent> getMakeSaleHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                // step 1: read all sale inputs from View
                String name    = view.getSaleName();
                String payment = view.getSalePayment();
                String card    = view.getSaleCard();
                String cash    = view.getSaleCash();

                // step 2: send to Model
                String result = model.makeSale(name, payment, card, cash);

                // step 3: show result
                view.setSaleStatus(result);

                // step 4: if sale worked, update View
                if (result.startsWith("Sale complete")) {
                    view.refreshInventory();
                    view.closeSaleWindow();
                    view.clearSaleFields();
                    view.setStatus(result);
                }
            }
        };
    }


    // CANCEL button in the sale window
    public EventHandler<ActionEvent> getCloseSaleHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.closeSaleWindow();
            }
        };
    }


    // OPEN RECOMMEND WINDOW button
    public EventHandler<ActionEvent> getOpenRecHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.showRecWindow();
            }
        };
    }


    // GET RECOMMENDATION button
    // Reads the chosen size from View, asks Model for a match, shows result in View
    public EventHandler<ActionEvent> getRecommendHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String size = view.getRecSize();
                String result = model.recommendBySize(size);
                view.setRecResult(result);
            }
        };
    }


    // CLOSE button in the recommend window
    public EventHandler<ActionEvent> getCloseRecHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.closeRecWindow();
            }
        };
    }


    // OPEN SALES WINDOW button
    public EventHandler<ActionEvent> getOpenSalesHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.showSalesWindow();
            }
        };
    }


    // CLOSE button in the sales window
    public EventHandler<ActionEvent> getCloseSalesHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.closeSalesWindow();
            }
        };
    }


    // OPEN FILTER WINDOW button
    public EventHandler<ActionEvent> getOpenFilterHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.showFilterWindow();
            }
        };
    }


    // FILTER button (inside filter window)
    // Reads the colour from View, asks Model to search, shows results in View
    public EventHandler<ActionEvent> getFilterHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String colour = view.getFilterColour();
                String[] results = model.filterByColour(colour);
                view.refreshFilter(results);
            }
        };
    }


    // CLOSE button in the filter window
    public EventHandler<ActionEvent> getCloseFilterHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.closeFilterWindow();
            }
        };
    }
}