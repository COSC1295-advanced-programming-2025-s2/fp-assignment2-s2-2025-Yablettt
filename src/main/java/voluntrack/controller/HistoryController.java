package main.java.voluntrack.controller;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.voluntrack.Navigator;
import main.java.voluntrack.store.RegistrationStore;
import main.java.voluntrack.store.RegistrationStore.RegistrationRow;

import java.io.FileWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistoryController {

    @FXML private TableView<RegistrationRow> historyTable;
    @FXML private TableColumn<RegistrationRow, String> regIdCol;
    @FXML private TableColumn<RegistrationRow, String> dateTimeCol;
    @FXML private TableColumn<RegistrationRow, String> titleCol;
    @FXML private TableColumn<RegistrationRow, String> locationCol;
    @FXML private TableColumn<RegistrationRow, String> dayCol;
    @FXML private TableColumn<RegistrationRow, Number> slotsCol;
    @FXML private TableColumn<RegistrationRow, Number> hoursCol;
    @FXML private TableColumn<RegistrationRow, Number> totalCol;

    @FXML private Button btnExport;
    @FXML private Button btnBack;

    private String username;
    public void setUsername(String username) { this.username = username; }

    @FXML
    private void initialize() {
        regIdCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().regId()));
        dateTimeCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().dateTime()));
        titleCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().title()));
        locationCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().location()));
        dayCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().day()));
        slotsCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().slots()));
        hoursCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().hours()));
        totalCol.setCellValueFactory(c -> new ReadOnlyDoubleWrapper(c.getValue().value()));
    }

    @FXML
    private void onShown() {
        historyTable.getItems().setAll(RegistrationStore.listByUser(username));
    }

    @FXML
    private void onExport() {
        try {
            var rows = RegistrationStore.listByUser(username);
            Path p = Path.of("history_" + username + ".txt");
            try (FileWriter fw = new FileWriter(p.toFile())) {
                fw.write("VolunTrack Participation History for " + username + "\n");
                fw.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "\n\n");
                for (var r : rows) {
                    fw.write(String.format(
                            "ID: %s | %s | %s (%s, %s) | Slots: %d | Hours: %d | Total: $%.2f%n",
                            r.regId(), r.dateTime(), r.title(), r.location(), r.day(),
                            r.slots(), r.hours(), r.value()
                    ));
                }
            }
            new Alert(Alert.AlertType.INFORMATION, "Exported to " + p.toAbsolutePath()).showAndWait();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Export failed: " + e.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onBackToDashboard() {
        Navigator.go("UserDashboard.fxml", username);
    }

    @FXML
    private void onOpenCart() {
        Navigator.go("CartView.fxml");
    }
}
