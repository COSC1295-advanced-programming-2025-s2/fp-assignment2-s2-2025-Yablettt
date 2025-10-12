package main.java.voluntrack.controller;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.voluntrack.Navigator;
import main.java.voluntrack.store.RegistrationStore;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CartController {


    public static class CartItem {
        private final String title;
        private final int slots;
        private final int hours;
        private final double hourlyValue;

        public CartItem(String title, int slots, int hours, double hourlyValue) {
            this.title = title; this.slots = slots; this.hours = hours; this.hourlyValue = hourlyValue;
        }
        public String getTitle() { return title; }
        public int getSlots() { return slots; }
        public int getHours() { return hours; }
        public double getHourlyValue() { return hourlyValue; }
        public double getTotal() { return hourlyValue * hours * slots; }
    }

    private String username;

    public void setUsername(String username) { this.username = username; }
    public void addSelectedProject(String title, double hourly) {
        cartItems.add(new CartItem(title, 1, 1, hourly));
        refreshTotals();
    }

    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, String> titleCol;
    @FXML private TableColumn<CartItem, Number> slotsCol;
    @FXML private TableColumn<CartItem, Number> hoursCol;
    @FXML private TableColumn<CartItem, Number> totalCol;

    @FXML private TextField titleField;
    @FXML private Spinner<Integer> slotsSpinner;
    @FXML private Spinner<Integer> hoursSpinner;
    @FXML private TextField codeField;

    @FXML private Label totalLabel;
    @FXML private Button btnAdd;
    @FXML private Button btnRemove;
    @FXML private Button btnConfirm;
    @FXML private Button btnBack;

    private final ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        titleCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTitle()));
        slotsCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().getSlots()));
        hoursCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().getHours()));
        totalCol.setCellValueFactory(c -> new ReadOnlyDoubleWrapper(c.getValue().getTotal()));
        cartTable.setItems(cartItems);

        if (slotsSpinner != null) {
            slotsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 3, 1));
        }
        if (hoursSpinner != null) {
            hoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 3, 1));
        }
        refreshTotals();
    }

    @FXML
    private void onAdd() {
        String t = titleField.getText().trim();
        if (t.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Enter project title to add").showAndWait();
            return;
        }
        cartItems.add(new CartItem(t, slotsSpinner.getValue(), hoursSpinner.getValue(), /*hourly*/ 25.0));
        refreshTotals();
    }

    @FXML
    private void onRemove() {
        CartItem sel = cartTable.getSelectionModel().getSelectedItem();
        if (sel != null) {
            cartItems.remove(sel);
            refreshTotals();
        }
    }

    @FXML
    private void onConfirm() {
        // validate code
        String code = codeField.getText().trim();
        if (!code.matches("\\d{6}")) {
            new Alert(Alert.AlertType.ERROR, "Enter a valid 6-digit confirmation code").showAndWait();
            return;
        }
        if (cartItems.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Cart is empty").showAndWait();
            return;
        }


        boolean allOk = true;
        for (CartItem it : cartItems) {
            boolean ok = RegistrationStore.addRegistration(username, it.getTitle(),
                    it.getSlots(), it.getHours(), it.getTotal());
            if (!ok) allOk = false;
        }

        if (allOk) {
            String ts = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
            new Alert(Alert.AlertType.INFORMATION, "Registration successful on " + ts).showAndWait();
            cartItems.clear();
            refreshTotals();
        } else {
            new Alert(Alert.AlertType.ERROR, "Some registrations failed validation.").showAndWait();
        }
    }

    @FXML
    private void onBack() {
        Navigator.go("UserDashboard.fxml", username);
    }

    private void refreshTotals() {
        double sum = cartItems.stream().mapToDouble(CartItem::getTotal).sum();
        if (totalLabel != null) totalLabel.setText(String.format("Total: $%.2f", sum));
    }
}
