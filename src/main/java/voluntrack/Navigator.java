package main.java.voluntrack;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.voluntrack.controller.CartController;
import main.java.voluntrack.controller.HistoryController;
import main.java.voluntrack.controller.UserDashboardController;

import java.lang.reflect.Method;

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
            if (controller != null) {
                try {
                    Method m = controller.getClass().getMethod("setUsername", String.class);
                    m.invoke(controller, username);
                } catch (NoSuchMethodException ignore) {
                }
            }

            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load " + fxmlFile, e);
        }
    }

    public static void goToCart(String username, String title, double hourlyValue, int slots, int hours) {
        try {
            FXMLLoader loader = new FXMLLoader(Navigator.class.getResource("/view/CartView.fxml"));
            Parent root = loader.load();
            Object c = loader.getController();
            if (c instanceof main.java.voluntrack.controller.CartController cart) {
                cart.setUsername(username);
                cart.addFromDashboard(title, hourlyValue, slots, hours);
            }
            primaryStage.setScene(new Scene(root));
            primaryStage.show();
        } catch (Exception e) {
            throw new RuntimeException("Failed to load CartView.fxml", e);
        }
    }


}
