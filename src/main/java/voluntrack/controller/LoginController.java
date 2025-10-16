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


