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

        setCartControlsEnabled(false);

        projectTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel == null) {
                selectedProjectLabel.setText("No project selected");
                setCartControlsEnabled(true);
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
            new Alert(Alert.AlertType.ERROR, "Please select a project first.").showAndWait();
            return;
        }
        int slots = slotsSpinner.getValue();
        int hours = hoursSpinner.getValue();
        Navigator.goToCart(username, p.getTitle(), p.getHourlyValue(), slots, hours);
    }

}

