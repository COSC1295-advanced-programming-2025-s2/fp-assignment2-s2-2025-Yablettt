package main.java.voluntrack;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.voluntrack.controller.UserDashboardController;

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

    // helps pass username to dashboard
    public static void go(String fxmlFile, String username) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource("/view/" + fxmlFile));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof UserDashboardController udc) {
                udc.setUsername(username);
            }
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + fxmlFile, e);
        }
    }
}
