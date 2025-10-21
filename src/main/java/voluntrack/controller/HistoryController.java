package main.java.voluntrack.controller;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.voluntrack.Navigator;
import main.java.voluntrack.store.RegistrationStore;

public class HistoryController {

    @FXML private TableView<RegistrationStore.HistoryRow> historyTable;

    @FXML private TableColumn<RegistrationStore.HistoryRow, String> regIdCol;
    @FXML private TableColumn<RegistrationStore.HistoryRow, String> dateTimeCol;
    @FXML private TableColumn<RegistrationStore.HistoryRow, String> titleCol;
    @FXML private TableColumn<RegistrationStore.HistoryRow, String> locationCol;
    @FXML private TableColumn<RegistrationStore.HistoryRow, String> dayCol;
    @FXML private TableColumn<RegistrationStore.HistoryRow, Number> slotsCol;
    @FXML private TableColumn<RegistrationStore.HistoryRow, Number> hoursCol;
    @FXML private TableColumn<RegistrationStore.HistoryRow, Number> totalCol;

    private String username;

    @FXML
    private void initialize() {
        regIdCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(String.format("%04d", c.getValue().getRegId())));
        dateTimeCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getDateTime()));
        titleCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTitle()));
        locationCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getLocation()));
        dayCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getDay()));
        slotsCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().getSlots()));
        hoursCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().getHours()));
        totalCol.setCellValueFactory(c -> new ReadOnlyDoubleWrapper(c.getValue().getTotal()));
    }

    public void setUsername(String username) {
        this.username = username;
        loadData();
    }

    private void loadData() {
        if (username == null) {
            return;
        }
        var rows = main.java.voluntrack.store.RegistrationStore.loadHistoryRowsFor(username);
        for (var r : rows) {
            System.out.println("  row: " + r.getRegId() + " " + r.getTitle() + " $" + r.getTotal());
        }
        historyTable.setItems(javafx.collections.FXCollections.observableArrayList(rows));
    }

    @FXML private void onBackToDashboard() { Navigator.go("UserDashboard.fxml", username); }
    @FXML private void onOpenCart()        { Navigator.go("CartView.fxml", username); }

    @FXML
    private void onExport() {
        var rows = historyTable.getItems();
        if (rows == null || rows.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No history to export.").showAndWait();
            return;
        }
        String fileName = "history_" + username + ".txt";
        try (java.io.PrintWriter out = new java.io.PrintWriter(fileName)) {
            out.println("Registration History for " + username);
            out.println("RegID | DateTime | Title | Location | Day | Slots | Hours | Total");
            for (var r : rows) {
                out.printf("%04d | %s | %s | %s | %s | %d | %d | %.2f%n",
                        r.getRegId(), r.getDateTime(), r.getTitle(), r.getLocation(),
                        r.getDay(), r.getSlots(), r.getHours(), r.getTotal());
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage()).showAndWait();
            return;
        }
        new Alert(Alert.AlertType.INFORMATION, "Exported to " + fileName).showAndWait();
    }
}
