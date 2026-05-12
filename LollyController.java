package Assignment2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

// CONTROLLER - sits between the View and Model.
// The View and Model dont interact directly so this is used.
public class LollyController {

    private LollyModel model;
    private LollyView view;

    public LollyController(LollyModel model, LollyView view) {
        this.model = model;
        this.view  = view;
    }

    // Controller tells Model to sort, then tells View to refresh
    public EventHandler<ActionEvent> getSortNameHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String result = model.sortByName(); // Model does the sort
                view.refreshInventory(); // View updates the list
                view.setStatus(result); // View shows the message
            }
        };
    }


    // Same as Name but for the Size
    public EventHandler<ActionEvent> getSortSizeHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String result = model.sortBySize();
                view.refreshInventory();
                view.setStatus(result);
            }
        };
    }


    // check low stock button
    // Model returns warnings
    // Controller joins them into one string for the View
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


    // Just tells the View to show the window
    public EventHandler<ActionEvent> getOpenAddHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.showAddWindow();
            }
        };
    }

    // MVC flow:
    //   1. Read what the user typed using View's getters
    //   2. Send it to the Model to validate and add
    //   3. Show the result in the add window
    //   4. If it worked, refresh the list and close the form
    public EventHandler<ActionEvent> getAddLollyHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                // Read input from View
                String name   = view.getAddName();
                String colour = view.getAddColour();
                String price  = view.getAddPrice();
                String size   = view.getAddSize();

                // Send to Model
                String result = model.addLolly(name, colour, price, size);

                // Show result
                view.setAddStatus(result);

                // If it worked, update the View
                if (result.startsWith("Added")) {
                    view.refreshInventory();
                    view.closeAddWindow();
                    view.clearAddFields();
                    view.setStatus(result);
                }
            }
        };
    }


    // Cancel button in the add window
    public EventHandler<ActionEvent> getCloseAddHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.closeAddWindow();
            }
        };
    }


    // The remove button reads the selected lolly name from the View, passes to Model, refreshes list
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


    // Prefills the lolly name if something is already selected in the list
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


    // Process Payment button the same MVC flow as getAddLollyHandler()
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


    // Cancel button in the sale window
    public EventHandler<ActionEvent> getCloseSaleHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.closeSaleWindow();
            }
        };
    }


    // Open recommend window button
    public EventHandler<ActionEvent> getOpenRecHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.showRecWindow();
            }
        };
    }


    // Get recommendation button
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


    // Close button in the recommend window
    public EventHandler<ActionEvent> getCloseRecHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.closeRecWindow();
            }
        };
    }


    // Open sales window button
    public EventHandler<ActionEvent> getOpenSalesHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.showSalesWindow();
            }
        };
    }


    // Cloise button in the sales window
    public EventHandler<ActionEvent> getCloseSalesHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.closeSalesWindow();
            }
        };
    }


    // Open filter windowbutton
    public EventHandler<ActionEvent> getOpenFilterHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.showFilterWindow();
            }
        };
    }


    // Filter button (inside filter window)
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


    // Close button in the filter window
    public EventHandler<ActionEvent> getCloseFilterHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.closeFilterWindow();
            }
        };
    }
}