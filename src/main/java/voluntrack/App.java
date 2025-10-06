package main.java.voluntrack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Navigator.setStage(stage);
        stage.setScene(new Scene(FXMLLoader.load(App.class.getResource("/view/LoginView.fxml"))));
        stage.setTitle("VolunTrack");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

