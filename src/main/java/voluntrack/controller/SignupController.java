package main.java.voluntrack.controller;

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


