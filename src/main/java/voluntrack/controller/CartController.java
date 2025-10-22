package main.java.voluntrack.controller;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.voluntrack.Navigator;
import main.java.voluntrack.store.CartStore;
import main.java.voluntrack.store.RegistrationStore;

public class CartController {

    @FXML private TableView<CartStore.Item> cartTable;
    @FXML private TableColumn<CartStore.Item, String> titleCol;
    @FXML private TableColumn<CartStore.Item, Number> slotsCol;
    @FXML private TableColumn<CartStore.Item, Number> hoursCol;
    @FXML private TableColumn<CartStore.Item, Number> totalCol;

    @FXML private Label totalLabel;

    private String username;

    public void setUsername(String username) {
        this.username = username;
        syncFromStore();
        cartTable.setItems(CartStore.getCart(username));
        cartTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    // Reference: Couldn't get cart to sync to history so asked chatgpt for help and gave me this method
    private void syncFromStore() {
        if (username == null) return;
        items.setAll(CartStore.getCart(username));
        refreshTotals();
    }

    public void addFromDashboard(String title, double hourlyValue, int slots, int hours) {
        main.java.voluntrack.store.CartStore.add(username, title, hourlyValue, slots, hours);

        if (cartTable != null) {
            cartTable.refresh();
        }
        refreshTotals();
    }

    // holds all items user adds
    private final ObservableList<CartStore.Item> items = FXCollections.observableArrayList();


    @FXML
    private void initialize() {
        titleCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTitle()));
        slotsCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().getSlots()));
        hoursCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().getHours()));
        totalCol.setCellValueFactory(c -> new ReadOnlyDoubleWrapper(c.getValue().getTotal()));
        cartTable.setItems(items);
        refreshTotals();
    }

    @FXML
    private void onRemove() {
        int id = cartTable.getSelectionModel().getSelectedIndex();
        if (id < 0) {
            new Alert(Alert.AlertType.INFORMATION, "Select an item to remove.").showAndWait();
            return;
        }
        CartStore.removeAt(username, id);
        refreshTotals();
    }


    @FXML
    private void onConfirm() {
        // 6 digit code confirmation
        TextInputDialog d = new TextInputDialog();
        d.setTitle("Confirmation Code");
        d.setHeaderText("Enter the 6-digit confirmation code");
        d.setContentText("Code:");
        d.getEditor().setPromptText("e.g. 123456");

        String code = d.showAndWait().orElse("");
        if (!code.matches("\\d{6}")) {
            new Alert(Alert.AlertType.ERROR, "Enter a valid 6-digit code.").showAndWait();
            return;
        }

        // this will help this.items mirror current state of CartStore
        syncFromStore();

        if (items.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Cart is empty.").showAndWait();
            return;
        }

        boolean fail = false;
        double totalContribution = 0.0;

        for (var it : items) {
            boolean ok = RegistrationStore.addRegistration(
                    username,
                    it.getTitle(),
                    it.getSlots(),
                    it.getHours(),
                    it.getTotal()
            );
            if (!ok) {
                fail = true;
            } else {
                totalContribution += it.getTotal();
            }
        }

        if (fail) {
            new Alert(Alert.AlertType.ERROR,
                    "This project has already happened").showAndWait();
            return;
        }

        CartStore.clear(username);
        items.clear();
        refreshTotals();

        new Alert(Alert.AlertType.INFORMATION,
                String.format("Registration successful!\nTotal contribution: $%.2f", totalContribution)
        ).showAndWait();
        Navigator.go("HistoryView.fxml", username);
    }


    @FXML
    private void onBack() {
        Navigator.go("UserDashboard.fxml", username);
    }

    private void refreshTotals() {
        double sum = 0;
        for (CartStore.Item it : CartStore.getCart(username)) {
            sum += it.getTotal();
        }
        if (totalLabel != null) {
            totalLabel.setText(String.format("Total: $%.2f", sum));
        }
    }

}

