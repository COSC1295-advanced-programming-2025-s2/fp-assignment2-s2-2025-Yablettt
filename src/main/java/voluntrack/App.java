package main.java.voluntrack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.java.voluntrack.store.Database;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        // create db
        Database.init();

        //load screen
        Navigator.setStage(stage);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/LoginView.fxml"));
        stage.setTitle("VolunTrack");
        stage.setScene(new Scene(loader.load()));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
