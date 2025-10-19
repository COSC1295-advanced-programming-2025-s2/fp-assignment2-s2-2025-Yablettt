package main.java.voluntrack.controller;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.voluntrack.Navigator;
import main.java.voluntrack.store.CartStore;

public class CartController {

    @FXML private TableView<CartStore.Item> cartTable;
    @FXML private TableColumn<CartStore.Item, String> titleCol;
    @FXML private TableColumn<CartStore.Item, Number> slotsCol;
    @FXML private TableColumn<CartStore.Item, Number> hoursCol;
    @FXML private TableColumn<CartStore.Item, Number> totalCol;

    @FXML private Label totalLabel;
    @FXML private TextField codeField;

    private String username;

    public void setUsername(String username) {
        this.username = username;

        cartTable.setItems(CartStore.getCart(username));

        CartStore.getCart(username).addListener((ListChangeListener<CartStore.Item>) c -> refreshTotals());
        refreshTotals();
    }

    public void addFromDashboard(String title, double hourlyValue, int slots, int hours) {
        main.java.voluntrack.store.CartStore.add(username, title, hourlyValue, slots, hours);

        if (cartTable != null) {
            cartTable.refresh();
        }
        refreshTotals();
    }


    @FXML
    private void initialize() {
        titleCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTitle()));
        slotsCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().getSlots()));
        hoursCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().getHours()));
        totalCol.setCellValueFactory(c -> new ReadOnlyDoubleWrapper(c.getValue().getTotal()));
    }

    @FXML
    private void onRemove() {
        CartStore.Item sel = cartTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            CartStore.remove(username, sel);
            refreshTotals();
        }
    }

    @FXML
    private void onConfirm() {
        // get 6 digit code
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Confirmation Code");
        dialog.setHeaderText("Enter the 6-digit confirmation code");
        dialog.setContentText("Code:");
        dialog.getEditor().setPromptText("e.g. 123456");

        String code = dialog.showAndWait().orElse("").trim();
        if (!code.matches("\\d{6}")) {
            new Alert(Alert.AlertType.ERROR, "Enter a valid 6-digit code.").showAndWait();
            return;
        }

        if (CartStore.getCart(username).isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Cart is empty.").showAndWait();
            return;
        }

        // compute total contribution
        double total = CartStore.getCart(username).stream()
                .mapToDouble(CartStore.Item::getTotal)
                .sum();

        CartStore.clear(username);
        refreshTotals();

        new Alert(Alert.AlertType.INFORMATION,
                String.format("Registration successful!\nTotal contribution: $%.2f", total)
        ).showAndWait();
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
