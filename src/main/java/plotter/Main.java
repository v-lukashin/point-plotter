package plotter;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import plotter.controller.MainController;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("view/Main.fxml"));
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
