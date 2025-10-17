/*package main.java.voluntrack.controller;

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

    @FXML
    private void initialize() {
        titleCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().title()));
        locationCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().location()));
        dayCol.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().day()));
        hourlyCol.setCellValueFactory(c -> new javafx.beans.property.ReadOnlyDoubleWrapper(c.getValue().hourlyValue()));
        availableCol.setCellValueFactory(c -> new javafx.beans.property.ReadOnlyIntegerWrapper(c.getValue().availableSlots()));

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

 */
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

    private String passedUsername;
    private String username;
    private Project selectedProject;

    public void setUsername(String username) {
        if (welcomeLabel != null && username != null) {
            welcomeLabel.setText("Welcome " + username);
        }
    }

    @FXML
    private void onOpenCart() {
        Navigator.go("CartView.fxml", passedUsername);
    }

    @FXML
    private void onOpenHistory() {
        Navigator.go("HistoryView.fxml", passedUsername);
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

        // when somethings selected, now reacts / changes
        projectTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, p) -> {
            if (p == null) {
                selectedProjectLabel.setText("No project selected");
                setCartControlsEnabled(false);
                return;
            }

            selectedProjectLabel.setText(p.getTitle());

            int maxSlots = Math.max(1, Math.min(3, p.getAvailableSlots()));
            slotsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxSlots, Math.min(1, maxSlots)));
            slotsSpinner.setDisable(maxSlots == 0);


            setCartControlsEnabled(maxSlots > 0);
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
            new Alert(Alert.AlertType.ERROR, "Hours and slots must be between 1 and 3.").showAndWait();
            return;
        }
        if (slots > p.getAvailableSlots()) {
            new Alert(Alert.AlertType.ERROR, "Not enough available slots for this project.").showAndWait();
            return;
        }


        //CartStore.add(username, p, slots, hours);
        Navigator.go("CartView.fxml", username);

        new Alert(Alert.AlertType.INFORMATION, "Added to cart: " + p.getTitle()).showAndWait();
    }


}

