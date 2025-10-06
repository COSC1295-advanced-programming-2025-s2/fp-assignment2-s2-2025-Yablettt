package main.java.voluntrack;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.voluntrack.controller.UserDashboardController;
import main.java.voluntrack.model.User;

public class Navigator {
    static Stage primaryStage;
    public static void setStage(Stage stage) { primaryStage = stage; }

    public static void go(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource("/view/" + fxmlFile));
            Parent root = loader.load();
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + fxmlFile, e);
        }
    }

    public static void go(String fxmlFile, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource("/view/" + fxmlFile));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof UserDashboardController c) {
                c.setUser(user);
            }

            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + fxmlFile, e);
        }
    }
}
