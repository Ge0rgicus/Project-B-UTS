package Assignment2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LollyView extends Application {

    private LollyModel model;
    private LollyController controller;

    private ListView<String> inventoryListView;
    private Label statusLabel;

    private TextField addNameField;
    private TextField addColourField;
    private TextField addPriceField;
    private ComboBox<String> addSizeCombo;
    private Label addStatusLabel;
    private Stage addStage;

    private TextField saleNameField;
    private ComboBox<String> salePaymentCombo;
    private TextField saleCardField;
    private TextField saleCashField;
    private Label saleStatusLabel;
    private Stage saleStage;

    private ComboBox<String> recSizeCombo;
    private Label recResultLabel;
    private Stage recStage;

    private ListView<String> salesListView;
    private Stage salesStage;

    private TextField filterColourField;
    private ListView<String> filterListView;
    private Stage filterStage;

    @Override
    public void start(Stage primaryStage) {
        model = new LollyModel();
        controller = new LollyController(model, this);

        buildMainWindow(primaryStage);
        buildAddWindow();
        buildSaleWindow();
        buildRecommendWindow();
        buildSalesWindow();
        buildFilterWindow();

        refreshInventory();

        primaryStage.show();
    }

    private void buildMainWindow(Stage stage) {
        Label titleLabel = new Label("Lolly Shop");
        titleLabel.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

        Label inventoryLabel = new Label("Inventory:");

        inventoryListView = new ListView<String>();
        inventoryListView.setPrefHeight(180);

        Button sortNameBtn = new Button("Sort by Name");
        Button sortSizeBtn = new Button("Sort by Size");
        Button lowStockBtn = new Button("Check Low Stock");
        HBox sortRow = new HBox(8, sortNameBtn, sortSizeBtn, lowStockBtn);

        Button addBtn = new Button("Add Lolly");
        Button removeBtn = new Button("Remove Selected");
        Button saleBtn = new Button("Make a Sale");
        Button filterBtn = new Button("Filter by Colour");
        Button recBtn = new Button("Recommend Lolly");
        Button salesBtn = new Button("View Sales");
        HBox actionRow = new HBox(8, addBtn, removeBtn, saleBtn, filterBtn, recBtn, salesBtn);

        statusLabel = new Label("Welcome to the Lolly Shop!");

        VBox layout = new VBox(10,
                titleLabel,
                inventoryLabel,
                inventoryListView,
                sortRow,
                actionRow,
                statusLabel);
        layout.setStyle("-fx-padding: 15;");

        sortNameBtn.setOnAction(controller.getSortNameHandler());
        sortSizeBtn.setOnAction(controller.getSortSizeHandler());
        lowStockBtn.setOnAction(controller.getLowStockHandler());
        addBtn.setOnAction(controller.getOpenAddHandler());
        removeBtn.setOnAction(controller.getRemoveHandler());
        saleBtn.setOnAction(controller.getOpenSaleHandler());
        filterBtn.setOnAction(controller.getOpenFilterHandler());
        recBtn.setOnAction(controller.getOpenRecHandler());
        salesBtn.setOnAction(controller.getOpenSalesHandler());

        Scene scene = new Scene(layout, 700, 400);
        stage.setTitle("Lolly Shop");
        stage.setScene(scene);
    }

    private void buildAddWindow() {
        Label title = new Label("Add a New Lolly");
        title.setStyle("-fx-font-weight: bold;");

        addNameField = new TextField();
        addNameField.setPromptText("e.g. Strawberry Swirl");

        addColourField = new TextField();
        addColourField.setPromptText("e.g. Pink");

        addPriceField = new TextField();
        addPriceField.setPromptText("e.g. 2.50");

        addSizeCombo = new ComboBox<String>();
        addSizeCombo.getItems().addAll("LARGE", "MEDIUM", "SMALL");
        addSizeCombo.setValue("MEDIUM");

        Button addBtn = new Button("Add");
        Button cancelBtn = new Button("Cancel");
        HBox buttons = new HBox(10, addBtn, cancelBtn);

        addStatusLabel = new Label("");

        VBox layout = new VBox(10,
                title,
                new Label("Name:"), addNameField,
                new Label("Colour:"), addColourField,
                new Label("Price ($):"), addPriceField,
                new Label("Size:"), addSizeCombo,
                buttons,
                addStatusLabel);
        layout.setStyle("-fx-padding: 15;");

        addBtn.setOnAction(controller.getAddLollyHandler());
        cancelBtn.setOnAction(controller.getCloseAddHandler());

        addStage = new Stage();
        addStage.setTitle("Add Lolly");
        addStage.setScene(new Scene(layout, 300, 370));
    }

    private void buildSaleWindow() {
        Label title = new Label("Make a Sale");
        title.setStyle("-fx-font-weight: bold;");

        saleNameField = new TextField();
        saleNameField.setPromptText("e.g. Mint Twist");

        salePaymentCombo = new ComboBox<String>();
        salePaymentCombo.getItems().addAll("Card", "Cash");
        salePaymentCombo.setValue("Card");

        saleCardField = new TextField();
        saleCardField.setPromptText("e.g. 1234-5678-9012-3456");

        saleCashField = new TextField();
        saleCashField.setPromptText("e.g. 5.00");

        Button processBtn = new Button("Process Payment");
        Button cancelBtn = new Button("Cancel");
        HBox buttons = new HBox(10, processBtn, cancelBtn);

        saleStatusLabel = new Label("");

        VBox layout = new VBox(10,
                title,
                new Label("Lolly name:"), saleNameField,
                new Label("Payment type:"), salePaymentCombo,
                new Label("Card number (if paying by card):"), saleCardField,
                new Label("Cash given (if paying by cash):"), saleCashField,
                buttons,
                saleStatusLabel);
        layout.setStyle("-fx-padding: 15;");

        processBtn.setOnAction(controller.getMakeSaleHandler());
        cancelBtn.setOnAction(controller.getCloseSaleHandler());

        saleStage = new Stage();
        saleStage.setTitle("Make a Sale");
        saleStage.setScene(new Scene(layout, 320, 360));
    }

    private void buildRecommendWindow() {
        Label title = new Label("Recommend a Lolly");
        title.setStyle("-fx-font-weight: bold;");

        recSizeCombo = new ComboBox<String>();
        recSizeCombo.getItems().addAll("LARGE", "MEDIUM", "SMALL");
        recSizeCombo.setValue("MEDIUM");

        Button recBtn = new Button("Get Recommendation");
        Button closeBtn = new Button("Close");
        HBox buttons = new HBox(10, recBtn, closeBtn);

        recResultLabel = new Label("Result will appear here.");

        VBox layout = new VBox(10,
                title,
                new Label("Choose a size:"), recSizeCombo,
                buttons,
                recResultLabel);
        layout.setStyle("-fx-padding: 15;");

        recBtn.setOnAction(controller.getRecommendHandler());
        closeBtn.setOnAction(controller.getCloseRecHandler());

        recStage = new Stage();
        recStage.setTitle("Recommend Lolly");
        recStage.setScene(new Scene(layout, 320, 200));
    }

    private void buildSalesWindow() {
        Label title = new Label("Sales Record");
        title.setStyle("-fx-font-weight: bold;");

        salesListView = new ListView<String>();
        salesListView.setPrefHeight(200);

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(controller.getCloseSalesHandler());

        VBox layout = new VBox(10, title, salesListView, closeBtn);
        layout.setStyle("-fx-padding: 15;");

        salesStage = new Stage();
        salesStage.setTitle("Sales Record");
        salesStage.setScene(new Scene(layout, 450, 280));
    }

    private void buildFilterWindow() {
        Label title = new Label("Filter by Colour");
        title.setStyle("-fx-font-weight: bold;");

        filterColourField = new TextField();
        filterColourField.setPromptText("e.g. Red");

        Button filterBtn = new Button("Filter");
        Button closeBtn = new Button("Close");
        HBox buttons = new HBox(10, filterBtn, closeBtn);

        filterListView = new ListView<String>();
        filterListView.setPrefHeight(150);

        VBox layout = new VBox(10,
                title,
                new Label("Colour:"), filterColourField,
                buttons,
                filterListView);
        layout.setStyle("-fx-padding: 15;");

        filterBtn.setOnAction(controller.getFilterHandler());
        closeBtn.setOnAction(controller.getCloseFilterHandler());

        filterStage = new Stage();
        filterStage.setTitle("Filter by Colour");
        filterStage.setScene(new Scene(layout, 450, 290));
    }

    public void refreshInventory() {
        inventoryListView.getItems().clear();
        String[] items = model.getInventoryList();
        for (int i = 0; i < items.length; i++) {
            inventoryListView.getItems().add(items[i]);
        }
    }

    public void refreshSales() {
        salesListView.getItems().clear();
        String[] items = model.getSalesList();
        for (int i = 0; i < items.length; i++) {
            salesListView.getItems().add(items[i]);
        }
    }

    public void refreshFilter(String[] results) {
        filterListView.getItems().clear();
        for (int i = 0; i < results.length; i++) {
            filterListView.getItems().add(results[i]);
        }
    }

    public void showAddWindow()    { addStage.show(); }
    public void closeAddWindow()   { addStage.hide(); }
    public void showSaleWindow()   { saleStage.show(); }
    public void closeSaleWindow()  { saleStage.hide(); }
    public void showRecWindow()    { recStage.show(); }
    public void closeRecWindow()   { recStage.hide(); }
    public void showSalesWindow()  { refreshSales(); salesStage.show(); }
    public void closeSalesWindow() { salesStage.hide(); }
    public void showFilterWindow() { filterStage.show(); }
    public void closeFilterWindow(){ filterStage.hide(); }

    public void setStatus(String message)     { statusLabel.setText(message); }
    public void setAddStatus(String message)  { addStatusLabel.setText(message); }
    public void setSaleStatus(String message) { saleStatusLabel.setText(message); }
    public void setRecResult(String message)  { recResultLabel.setText(message); }

    public String getAddName()    { return addNameField.getText().trim(); }
    public String getAddColour()  { return addColourField.getText().trim(); }
    public String getAddPrice()   { return addPriceField.getText().trim(); }
    public String getAddSize()    { return addSizeCombo.getValue(); }

    public String getSaleName()    { return saleNameField.getText().trim(); }
    public String getSalePayment() { return salePaymentCombo.getValue(); }
    public String getSaleCard()    { return saleCardField.getText().trim(); }
    public String getSaleCash()    { return saleCashField.getText().trim(); }

    public String getFilterColour() { return filterColourField.getText().trim(); }
    public String getRecSize()      { return recSizeCombo.getValue(); }

    public String getSelectedLollyName() {
        String selected = inventoryListView.getSelectionModel().getSelectedItem();
        if (selected == null || selected.equals("Inventory is empty.")) {
            return "";
        }
        return selected.split("\\|")[0].trim();
    }

    public void clearAddFields() {
        addNameField.clear();
        addColourField.clear();
        addPriceField.clear();
        addSizeCombo.setValue("MEDIUM");
        addStatusLabel.setText("");
    }

    public void clearSaleFields() {
        saleNameField.clear();
        saleCardField.clear();
        saleCashField.clear();
        salePaymentCombo.setValue("Card");
        saleStatusLabel.setText("");
    }

    public static void main(String[] args) {
        launch(args);
    }
}