package plotter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Main.fxml"));
            BorderPane mainPane = loader.load();

            MainController controller = loader.getController();
            Setup.mapController.setTitle(primaryStage.titleProperty());
            mainPane.setCenter(controller.node);

            primaryStage.setScene(new Scene(mainPane));
        } catch (IOException e) {
            e.printStackTrace();
        }
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> Setup.store());
    }

    public static void main(String[] args) {
        launch(args);
    }
}
