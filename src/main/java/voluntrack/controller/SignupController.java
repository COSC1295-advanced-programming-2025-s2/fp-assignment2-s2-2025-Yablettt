/*package main.java.voluntrack.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.java.voluntrack.Navigator;
import main.java.voluntrack.model.User;
import main.java.voluntrack.store.UserStore;

public class SignupController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleCreateAccount() {
        User newUser = new User(
                usernameField.getText(),
                passwordField.getText()
        );
        UserStore.addUser(newUser);
        new Alert(Alert.AlertType.INFORMATION, "Account created. Please log in.").showAndWait();
        Navigator.go("LoginView.fxml");
    }

    @FXML
    private void handleBack() {
        Navigator.go("LoginView.fxml");
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

public class SignupController {

    @FXML private TextField fullNameField;
    @FXML private TextField usernameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleCreateAccount() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String pass     = passwordField.getText().trim();

        if (fullName.isEmpty() || username.isEmpty() || pass.isEmpty()) {
            new Alert(Alert.AlertType.ERROR, "Full name, username and password are required.").showAndWait();
            return;
        }

        try {
            UserStore.add(new User(fullName, username, email, pass));
            new Alert(Alert.AlertType.INFORMATION, "Account created. Please log in.").showAndWait();
            Navigator.go("LoginView.fxml");
        } catch (RuntimeException ex) {
            new Alert(Alert.AlertType.ERROR, "Signup failed: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void handleBack() {
        Navigator.go("LoginView.fxml");
    }
}


