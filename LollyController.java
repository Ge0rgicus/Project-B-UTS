package Assignment2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// CONTROLLER - this sits between the View and the Model.
// When the user clicks a button in the View, the Controller
// reads the input, passes it to the Model, gets back a result,
// and then tells the View what to update.
//
// The View never talks to the Model directly and the Model
// never talks to the View directly - everything goes through here.
public class LollyController {

    private LollyModel model;
    private LollyView view;

    // Store references to both the Model and View so we can
    // pass messages between them
    public LollyController(LollyModel model, LollyView view) {
        this.model = model;
        this.view  = view;
    }


    // SORT BY NAME button
    // View fires the event -> Controller tells Model to sort
    // -> Model sorts the list -> Controller tells View to refresh
    public EventHandler<ActionEvent> getSortNameHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String result = model.sortByName();  // Model does the work
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
    // Model returns an array of warning strings, we join them
    // into one message and show it in the status bar
    public EventHandler<ActionEvent> getLowStockHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String[] warnings = model.checkLowStock(); // ask the Model
                String message = "";
                for (int i = 0; i < warnings.length; i++) {
                    message = message + warnings[i] + "   ";
                }
                view.setStatus(message); // show it in the View
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


    // ADD LOLLY button (inside the add window)
    // MVC flow:
    //   1. Controller reads what the user typed using View's getters
    //   2. Controller passes it all to the Model to validate and add
    //   3. Model returns a result string
    //   4. Controller shows the result in the add window
    //   5. If it worked, refresh the inventory list and clear the form
    public EventHandler<ActionEvent> getAddLollyHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                // step 1: read input from the View
                String name   = view.getAddName();
                String colour = view.getAddColour();
                String price  = view.getAddPrice();
                String size   = view.getAddSize();

                // step 2: send to Model
                String result = model.addLolly(name, colour, price, size);

                // step 3: show the result
                view.setAddStatus(result);

                // step 4: if successful, update the View
                if (result.startsWith("Added")) {
                    view.refreshInventory();  // update the main list
                    view.closeAddWindow();    // close the form
                    view.clearAddFields();    // reset fields for next time
                    view.setStatus(result);   // show success in main window
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
    // Gets the selected lolly name from the View, passes to Model,
    // then refreshes the list
    public EventHandler<ActionEvent> getRemoveHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                // read which lolly is selected in the ListView
                String name = view.getSelectedLollyName();
                if (name.isEmpty()) {
                    view.setStatus("Please click a lolly in the list first, then click Remove.");
                    return;
                }

                String result = model.removeLolly(name); // tell Model to remove it
                view.refreshInventory();                 // update the list in the View
                view.setStatus(result);
            }
        };
    }


    // OPEN SALE WINDOW button
    public EventHandler<ActionEvent> getOpenSaleHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                // pre-fill the lolly name if something is selected in the list
                String selectedName = view.getSelectedLollyName();
                if (!selectedName.isEmpty()) {
                    view.prefillSaleName(selectedName);
                }
                view.showSaleWindow();
            }
        };
    }


    // PROCESS PAYMENT button (inside the sale window)
    // MVC flow:
    //   1. Controller reads lolly name, payment type, card/cash from View
    //   2. Controller passes to Model which validates and processes it
    //   3. Model returns a result string
    //   4. Controller shows result in the sale window
    //   5. If successful, refresh inventory, close window, clear fields
    public EventHandler<ActionEvent> getMakeSaleHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                // step 1: read all the sale inputs from the View
                String name    = view.getSaleName();
                String payment = view.getSalePayment();
                String card    = view.getSaleCard();
                String cash    = view.getSaleCash();

                // step 2: pass to Model to do the work
                String result = model.makeSale(name, payment, card, cash);

                // step 3: show result in the sale window
                view.setSaleStatus(result);

                // step 4: if sale worked, update the View
                if (result.startsWith("Sale complete")) {
                    view.refreshInventory();  // sold lolly disappears from list
                    view.closeSaleWindow();   // close the sale form
                    view.clearSaleFields();   // reset for next sale
                    view.setStatus(result);   // show success in main window
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
    // MVC flow: View (user picks size) -> Controller (reads size)
    //        -> Model (searches sold history) -> Controller -> View (shows result)
    public EventHandler<ActionEvent> getRecommendHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String size = view.getRecSize();          // read from View
                String result = model.recommendBySize(size); // ask the Model
                view.setRecResult(result);                // show in View
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
                view.showSalesWindow(); // View refreshes the list when it opens
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


    // FILTER button (inside the filter window)
    // MVC flow: View (user types colour) -> Controller (reads it)
    //        -> Model (searches inventory) -> Controller -> View (shows matches)
    public EventHandler<ActionEvent> getFilterHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String colour = view.getFilterColour();          // read from View
                String[] results = model.filterByColour(colour); // ask the Model
                view.refreshFilter(results);                     // show in View
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