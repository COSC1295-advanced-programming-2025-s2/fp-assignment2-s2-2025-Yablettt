package main.java.voluntrack.controller;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.java.voluntrack.Navigator;
import main.java.voluntrack.model.Project;
import main.java.voluntrack.store.ProjectStore;

import java.util.Optional;

public class AdminDashboardController {

    @FXML private TableView<Project> projectTable;
    @FXML private TableColumn<Project, String> titleCol;
    @FXML private TableColumn<Project, String> locationCol;
    @FXML private TableColumn<Project, String> dayCol;
    @FXML private TableColumn<Project, Number> hourlyCol;
    @FXML private TableColumn<Project, Number> availableCol;
    @FXML private TableColumn<Project, Boolean> enabledCol;


    @FXML private Button btnAddProject;
    @FXML private Button btnLogout;

    private String username;

    public void setUsername(String username) { this.username = username; }

    @FXML
    private void initialize() {
        /*titleCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTitle()));
        locationCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getLocation()));
        dayCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getDay()));
        hourlyCol.setCellValueFactory(c -> new ReadOnlyDoubleWrapper(c.getValue().getHourlyValue()));
        availableCol.setCellValueFactory(c ->
                new ReadOnlyIntegerWrapper(c.getValue().getAvailableSlots()));

         */
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        dayCol.setCellValueFactory(new PropertyValueFactory<>("day"));
        hourlyCol.setCellValueFactory(new PropertyValueFactory<>("hourlyValue"));
        availableCol.setCellValueFactory(new PropertyValueFactory<>("availableSlots"));
        enabledCol.setCellValueFactory(new PropertyValueFactory<>("enabled"));


        projectTable.getItems().setAll(ProjectStore.loadAll());
    }

    @FXML
    private void onAddProject() {
        Navigator.go("ProjectForm.fxml", username);
    }

    @FXML
    private void onLogout() {
        Navigator.go("LoginView.fxml");
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void showInfo(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @FXML
    private void modifyProject() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a project to modify.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.title());
        dialog.setHeaderText("Modify Project Title");
        dialog.setContentText("Enter new title:");
        Optional<String> result = dialog.showAndWait();
        if (result.isEmpty()) return;

        String newTitle = result.get().trim();
        if (newTitle.isEmpty() || newTitle.length() > 30) {
            showError("Title must be non-empty and at most 30 characters.");
            return;
        }


        Project selected = table.getSelectionModel().getSelectedItem();
        if (selected != null) {
            boolean newStatus = !selected.isEnabled();
            ProjectStore.setEnabled(selected.getTitle(), newStatus);
            table.getItems().setAll(ProjectStore.loadAll());
        }

    }

    @FXML
    private void toggleProjectStatus() {
        Project selected = projectTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Please select a project first.");
            return;
        }

        boolean newStatus = !ProjectStore.isDisabled(selected);
        ProjectStore.setDisabled(selected, newStatus);

        String message = newStatus ? "Project disabled successfully." : "Project re-enabled successfully.";
        showInfo(message);

        projectTable.getItems().setAll(ProjectStore.loadAll());
    }

}
