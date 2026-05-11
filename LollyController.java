package Assignment2;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class LollyController {

    private LollyModel model;
    private LollyView view;

    public LollyController(LollyModel model, LollyView view) {
        this.model = model;
        this.view = view;
    }

    public EventHandler<ActionEvent> getSortNameHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String result = model.sortByName();
                view.refreshInventory();
                view.setStatus(result);
            }
        };
    }

    public EventHandler<ActionEvent> getSortSizeHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String result = model.sortBySize();
                view.refreshInventory();
                view.setStatus(result);
            }
        };
    }

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

    public EventHandler<ActionEvent> getOpenAddHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.showAddWindow();
            }
        };
    }

    public EventHandler<ActionEvent> getAddLollyHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String name   = view.getAddName();
                String colour = view.getAddColour();
                String price  = view.getAddPrice();
                String size   = view.getAddSize();

                String result = model.addLolly(name, colour, price, size);
                view.setAddStatus(result);

                if (result.startsWith("Added")) {
                    view.refreshInventory();
                    view.clearAddFields();
                    view.setStatus(result);
                }
            }
        };
    }

    public EventHandler<ActionEvent> getCloseAddHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.closeAddWindow();
            }
        };
    }

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

    public EventHandler<ActionEvent> getOpenSaleHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.showSaleWindow();
            }
        };
    }

    public EventHandler<ActionEvent> getMakeSaleHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String name    = view.getSaleName();
                String payment = view.getSalePayment();
                String card    = view.getSaleCard();
                String cash    = view.getSaleCash();

                String result = model.makeSale(name, payment, card, cash);
                view.setSaleStatus(result);

                if (result.startsWith("Sale complete")) {
                    view.refreshInventory();
                    view.clearSaleFields();
                    view.setStatus(result);
                }
            }
        };
    }

    public EventHandler<ActionEvent> getCloseSaleHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.closeSaleWindow();
            }
        };
    }

    public EventHandler<ActionEvent> getOpenRecHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.showRecWindow();
            }
        };
    }

    public EventHandler<ActionEvent> getRecommendHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String size = view.getRecSize();
                String result = model.recommendBySize(size);
                view.setRecResult(result);
            }
        };
    }

    public EventHandler<ActionEvent> getCloseRecHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.closeRecWindow();
            }
        };
    }

    public EventHandler<ActionEvent> getOpenSalesHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.showSalesWindow();
            }
        };
    }

    public EventHandler<ActionEvent> getCloseSalesHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.closeSalesWindow();
            }
        };
    }

    public EventHandler<ActionEvent> getOpenFilterHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.showFilterWindow();
            }
        };
    }

    public EventHandler<ActionEvent> getFilterHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                String colour = view.getFilterColour();
                String[] results = model.filterByColour(colour);
                view.refreshFilter(results);
            }
        };
    }

    public EventHandler<ActionEvent> getCloseFilterHandler() {
        return new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                view.closeFilterWindow();
            }
        };
    }
}