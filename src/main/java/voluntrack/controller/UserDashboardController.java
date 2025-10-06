package main.java.voluntrack.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.voluntrack.model.Project;
import main.java.voluntrack.store.ProjectStore;
import main.java.voluntrack.model.User;


public class UserDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private TableView<Project> projectTable;
    @FXML private TableColumn<Project, String> titleCol;
    @FXML private TableColumn<Project, String> locationCol;
    @FXML private TableColumn<Project, String> dayCol;
    @FXML private TableColumn<Project, Number> hourlyCol;
    @FXML private TableColumn<Project, Number> availableCol;

    private String username;

    // Called  when FXML is loaded
    @FXML
    private void initialize() {
        titleCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().title()));
        locationCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().location()));
        dayCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().day()));
        hourlyCol.setCellValueFactory(c -> new javafx.beans.property.ReadOnlyDoubleWrapper(c.getValue().hourlyValue()));
        availableCol.setCellValueFactory(c -> new javafx.beans.property.ReadOnlyIntegerWrapper(c.getValue().availableSlots()));

        // Loads the data
        projectTable.getItems().setAll(ProjectStore.loadAll());
    }

    public void setUser(User user) {
        if (user != null) {
            setUsername(user.getUsername());
        }
    }

    public void setUsername(String username) {
        this.username = username;
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome " + username);
        }
    }
}

