package main.java.voluntrack.controller;

import javafx.beans.property.ReadOnlyDoubleWrapper;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.voluntrack.model.Project;
import main.java.voluntrack.store.CartStore;
import main.java.voluntrack.store.ProjectStore;
import main.java.voluntrack.Navigator;

public class UserDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label selectedProjectLabel;

    @FXML private TableView<Project> projectTable;
    @FXML private TableColumn<Project, String> titleCol;
    @FXML private TableColumn<Project, String> locationCol;
    @FXML private TableColumn<Project, String> dayCol;
    @FXML private TableColumn<Project, Number> hourlyCol;
    @FXML private TableColumn<Project, Number> availableCol;
    @FXML private Spinner<Integer> slotsSpinner;
    @FXML private Spinner<Integer> hoursSpinner;
    @FXML private Button addBtn;


    private String username;
    private Project selectedProject;

    public void setUsername(String username) {
        this.username = username;
        welcomeLabel.setText("Welcome " + this.username);
    }

    @FXML
    private void onOpenCart() {
        Navigator.go("CartView.fxml", this.username);
    }

    @FXML
    private void onOpenHistory() {
        Navigator.go("HistoryView.fxml", this.username);
    }

    @FXML
    private void onLogout() {
        Navigator.go("LoginView.fxml");
    }


    @FXML
    private void initialize() {
        titleCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTitle()));
        locationCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getLocation()));
        dayCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getDay()));
        hourlyCol.setCellValueFactory(c -> new ReadOnlyDoubleWrapper(c.getValue().getHourlyValue()));
        availableCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().getAvailableSlots()));
        slotsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 3, 1));
        hoursSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 3, 1));

        projectTable.getItems().setAll(ProjectStore.loadFromCsv("/data/projects.csv"));
        projectTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        selectedProjectLabel.setText("No project selected");
        setCartControlsEnabled(false);

        projectTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            selectedProject = newSel;
            if (newSel != null) {
                selectedProjectLabel.setText(newSel.getTitle());
                if (slotsSpinner.getValueFactory() != null) slotsSpinner.getValueFactory().setValue(1);
                if (hoursSpinner.getValueFactory() != null) hoursSpinner.getValueFactory().setValue(1);
                setCartControlsEnabled(true);
            } else {
                selectedProjectLabel.setText("No project selected");
                setCartControlsEnabled(false);
            }
        });

    }

    private void setCartControlsEnabled(boolean enabled) {
        slotsSpinner.setDisable(!enabled);
        hoursSpinner.setDisable(!enabled);
        addBtn.setDisable(!enabled);
    }

    @FXML
    private void onAddToCart() {
        Project p = projectTable.getSelectionModel().getSelectedItem();
        if (p == null) {
            new Alert(Alert.AlertType.WARNING, "Select a project first.").showAndWait();
            return;
        }
        int slots = slotsSpinner.getValue();
        int hours = hoursSpinner.getValue();
        if (hours < 1 || hours > 3 || slots < 1 || slots > 3) {
            new Alert(Alert.AlertType.ERROR, "Hours and slots must be 1..3.").showAndWait();
            return;
        }
        if (slots > p.getAvailableSlots()) {
            new Alert(Alert.AlertType.ERROR, "Not enough available slots.").showAndWait();
            return;
        }

        CartStore.add(username, p.getTitle(), p.getHourlyValue(), slots, hours);
        new Alert(Alert.AlertType.INFORMATION, "Added to cart: " + p.getTitle()).showAndWait();
    }



}

