package main.java.voluntrack.controller;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import main.java.voluntrack.Navigator;
import main.java.voluntrack.model.Project;
import main.java.voluntrack.model.Registration;
import main.java.voluntrack.store.ProjectStore;
import main.java.voluntrack.store.RegistrationStore;

public class HistoryController {

    @FXML private TableView<Registration> historyTable;

    @FXML private TableColumn<Registration, Number> regIdCol;
    @FXML private TableColumn<Registration, String> dateTimeCol;
    @FXML private TableColumn<Registration, String> titleCol;
    @FXML private TableColumn<Registration, String> locationCol;
    @FXML private TableColumn<Registration, String> dayCol;
    @FXML private TableColumn<Registration, Number> slotsCol;
    @FXML private TableColumn<Registration, Number> hoursCol;
    @FXML private TableColumn<Registration, Number> totalCol;

    private String username;

    @FXML
    private void initialize() {
        regIdCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().regId()));
        dateTimeCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().dateTime()));

        titleCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(getProjectTitle(c.getValue().projectId())));
        locationCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(getProjectLocation(c.getValue().projectId())));
        dayCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(getProjectDay(c.getValue().projectId())));

        slotsCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().slots()));
        hoursCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().hours()));
        totalCol.setCellValueFactory(c -> new ReadOnlyDoubleWrapper(c.getValue().value()));
    }

    public void setUsername(String username) {
        this.username = username;
        loadData();
    }

    private void loadData() {
        if (username == null) return;
        historyTable.setItems(FXCollections.observableArrayList(
                RegistrationStore.loadFor(username)
        ));
    }

    private String getProjectTitle(int projectId) {
        Project p = ProjectStore.findById(projectId);
        return p == null ? "" : p.getTitle();
    }

    private String getProjectLocation(int projectId) {
        Project p = ProjectStore.findById(projectId);
        return p == null ? "" : p.getLocation();
    }

    private String getProjectDay(int projectId) {
        Project p = ProjectStore.findById(projectId);
        return p == null ? "" : p.getDay();
    }

    @FXML
    private void onBackToDashboard() {
        Navigator.go("UserDashboard.fxml", username);
    }

    @FXML
    private void onExport() {
        var rows = historyTable.getItems();
        if (rows == null || rows.isEmpty()) {
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION,
                    "No history to export.").showAndWait();
            return;
        }

        String fileName = "history_" + (username == null ? "user" : username) + ".txt";

        var chooser = new javafx.stage.FileChooser();
        chooser.setTitle("Export Participation History");
        chooser.setInitialFileName(fileName);
        java.io.File file = chooser.showSaveDialog(null);
        if (file == null) return;

        StringBuilder sb = new StringBuilder();
        sb.append("Participation History for ").append(username == null ? "" : username).append("\n");
        sb.append("====================================================\n");

        for (var r : rows) {
            sb.append("Registration ID : ").append(String.format("%04d", r.regId())).append("\n");
            sb.append("Confirmed At    : ").append(r.dateTime()).append("\n");
            sb.append("Project ID      : ").append(r.projectId()).append("\n");
            sb.append("Slots           : ").append(r.slots()).append("\n");
            sb.append("Hours           : ").append(r.hours()).append("\n");
            sb.append("Total Value     : ").append(String.format("%.2f", r.value())).append("\n");
            sb.append("----------------------------------------------------\n");
        }

        try {
            java.nio.file.Files.writeString(
                    file.toPath(),
                    sb.toString(),
                    java.nio.charset.StandardCharsets.UTF_8
            );
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION,
                    "Exported to:\n" + file.getAbsolutePath()).showAndWait();
        } catch (Exception ex) {
            new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR,
                    "Export failed: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onOpenCart() {
        Navigator.go("CartView.fxml", username);
    }
}
