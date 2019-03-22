package plotter;

import javafx.collections.MapChangeListener;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;
import org.jxmapviewer.viewer.WaypointPainter;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    public SwingNode node;
    @FXML
    public Menu menuRemove;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        node.setContent(Setup.mapController.getMapViewer());
        Setup.mapController.getPointsMap().addListener((MapChangeListener<String, WaypointPainter<GeoPos>>) change -> {
            if (change.wasAdded()) {
                MenuItem item = new MenuItem(change.getKey());
                item.setOnAction(this::removePoints);
                menuRemove.getItems().add(item);
            } else if (change.wasRemoved()) {
                Optional<MenuItem> item = menuRemove.getItems().stream().filter(i -> i.getText().equals(change.getKey())).findFirst();
                item.ifPresent(menuItem -> menuRemove.getItems().remove(menuItem));
            }
        });
    }

    @FXML
    public void addPoints() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PointsAdd.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.show();
        } catch (IOException e) {
        }
    }

    @FXML
    public void removePoints(ActionEvent event) {
        if (event.getSource() instanceof MenuItem) {
            MenuItem source = (MenuItem) event.getSource();
            Setup.mapController.removePoints(source.getText());
        }
    }

    @FXML
    public void resetPoints() {
        Setup.mapController.resetPoints();
    }
}
