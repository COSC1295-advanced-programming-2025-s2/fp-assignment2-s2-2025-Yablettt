/*package main.java.voluntrack.controller;

import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (!username.isEmpty() && !password.isEmpty()) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/UserDashboard.fxml"));
                Parent root = loader.load();

                UserDashboardController dashboardController = loader.getController();
                dashboardController.setUsername(username);

                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to load UserDashboard.fxml", e);
            }
        } else {
            System.out.println("Login failed: username or password empty");
        }
    }

    @FXML
    private void handleSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/SignupView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load SignupView.fxml", e);
        }
    }
}
 */

package main.java.voluntrack.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import main.java.voluntrack.Navigator;
import main.java.voluntrack.model.User;
import main.java.voluntrack.store.UserStore;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        User u = UserStore.findUser(username, password);
        if (u != null) {
            // pass the username to dashboard
            Navigator.go("UserDashboard.fxml", u.getUsername());
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR, "Invalid username or password.");
            a.setHeaderText(null);
            a.setTitle("Login Failed");
            a.showAndWait();
        }
    }

    @FXML
    private void handleSignup() {
        Navigator.go("SignupView.fxml");
    }
}


