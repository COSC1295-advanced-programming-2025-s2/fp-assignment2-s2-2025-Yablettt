package main.java.voluntrack.controller;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.voluntrack.Navigator;
import main.java.voluntrack.model.Project;
import main.java.voluntrack.store.ProjectStore;

public class AdminDashboardController {

    @FXML private TableView<Project> table;
    @FXML private TableColumn<Project, String> titleCol;
    @FXML private TableColumn<Project, String> locationCol;
    @FXML private TableColumn<Project, String> dayCol;
    @FXML private TableColumn<Project, Number> hourlyCol;
    @FXML private TableColumn<Project, Number> registeredCol;
    @FXML private TableColumn<Project, Number> totalCol;
    @FXML private TableColumn<Project, Number> availableCol;

    @FXML private TextField titleField;
    @FXML private TextField locationField;
    @FXML private TextField dayField;
    @FXML private TextField hourlyField;
    @FXML private TextField totalSlotsField;

    @FXML
    private void initialize() {
        titleCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(getTitle(c.getValue())));
        locationCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(getLocation(c.getValue())));
        dayCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(getDay(c.getValue())));
        hourlyCol.setCellValueFactory(c -> new ReadOnlyDoubleWrapper(getHourly(c.getValue())));
        registeredCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(getRegistered(c.getValue())));
        totalCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(getTotal(c.getValue())));
        availableCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(getAvailable(c.getValue())));

        refreshTable();

        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, sel) -> {
            if (sel != null) {
                titleField.setText(getTitle(sel));
                locationField.setText(getLocation(sel));
                dayField.setText(getDay(sel));
                hourlyField.setText(Double.toString(getHourly(sel)));
                totalSlotsField.setText(Integer.toString(getTotal(sel)));
            }
        });
    }

    @FXML
    private void onRefresh() {
        refreshTable();
    }

    @FXML
    private void onAdd() {
        try {
            String title = titleField.getText().trim();
            String location = locationField.getText().trim();
            String day = dayField.getText().trim();
            double hourly = Double.parseDouble(hourlyField.getText().trim());
            int total = Integer.parseInt(totalSlotsField.getText().trim());

            boolean ok = ProjectStore.insert(title, location, day, hourly, 0, total);
            if (!ok) {
                showError("Could not insert project.");
                return;
            }
            refreshTable();
            clearForm();
        } catch (NumberFormatException nfe) {
            showError("Hourly/Total slots must be numbers.");
        }
    }

    @FXML
    private void onModify() {
        Project selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a project first.");
            return;
        }
        try {
            String oldTitle = getTitle(selected);
            String newTitle = titleField.getText().trim();
            String newLocation = locationField.getText().trim();
            String newDay = dayField.getText().trim();
            double newHourly = Double.parseDouble(hourlyField.getText().trim());
            int newTotal = Integer.parseInt(totalSlotsField.getText().trim());

            boolean ok = ProjectStore.updateProject(oldTitle, newTitle, newLocation, newDay, newHourly, newTotal);
            if (!ok) {
                showError("Could not update project.");
                return;
            }
            refreshTable();
        } catch (NumberFormatException nfe) {
            showError("Hourly/Total slots must be numbers.");
        }
    }

    @FXML
    private void onEnable() {
        Project selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a project first.");
            return;
        }

        boolean newStatus = !selected.isEnabled();
        ProjectStore.setEnabled(selected.getTitle(), newStatus);

        String msg = newStatus ? "Project enabled successfully." : "Project disabled successfully.";
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();

        refreshTable();
    }


    private void refreshTable() {
        table.getItems().setAll(ProjectStore.loadAll());
        table.refresh();
    }

    @FXML
    private void onLogout() {
        Navigator.go("LoginView.fxml");
    }

    private void clearForm() {
        titleField.clear();
        locationField.clear();
        dayField.clear();
        hourlyField.clear();
        totalSlotsField.clear();
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private String getTitle(Project p) { return p.getTitle(); }
    private String getLocation(Project p) { return p.getLocation(); }
    private String getDay(Project p) { return p.getDay(); }
    private double getHourly(Project p) { return p.getHourlyValue(); }
    private int getRegistered(Project p) { return p.getRegisteredSlots(); }
    private int getTotal(Project p) { return p.getTotalSlots(); }
    private int getAvailable(Project p) { return Math.max(0, getTotal(p) - getRegistered(p)); }
}
