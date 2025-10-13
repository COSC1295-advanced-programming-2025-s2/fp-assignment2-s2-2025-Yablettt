package main.java.voluntrack.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import main.java.voluntrack.Navigator;
import main.java.voluntrack.store.ProjectStore;

public class ProjectFormController {

    @FXML private TextField titleField;
    @FXML private TextField locationField;
    @FXML private TextField dayField;
    @FXML private TextField hourlyField;
    @FXML private Spinner<Integer> totalSlotsSpinner;

    @FXML private Button btnSave;
    @FXML private Button btnCancel;

    private String username;
    public void setUsername(String username) { this.username = username; }

    @FXML
    private void initialize() {
        if (totalSlotsSpinner != null) {
            totalSlotsSpinner.setValueFactory(
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 999, 10)
            );
        }
    }

    @FXML
    private void onSave() {
        String title = titleField.getText().trim();
        String loc   = locationField.getText().trim();
        String day   = dayField.getText().trim();
        double hourly;
        int totalSlots = totalSlotsSpinner.getValue();

        try {
            hourly = Double.parseDouble(hourlyField.getText().trim());
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Hourly value must be a number").showAndWait();
            return;
        }

        if (title.isEmpty() || loc.isEmpty() || day.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Please fill all fields").showAndWait();
            return;
        }

        boolean ok = ProjectStore.insert(title, loc, day, hourly, 0, totalSlots);
        if (ok) {
            new Alert(Alert.AlertType.INFORMATION, "Project saved").showAndWait();
            Navigator.go("AdminDashboard.fxml", username);
        } else {
            new Alert(Alert.AlertType.ERROR, "Failed to save project").showAndWait();
        }
    }

    @FXML
    private void onCancel() {
        Navigator.go("AdminDashboard.fxml", username);
    }
}
