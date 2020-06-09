package plotter.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import plotter.Setup;
import plotter.painter.PainterType;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class PointsAddController implements Initializable {
    @FXML
    private VBox box;
    @FXML
    private ChoiceBox<PainterType> type;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Button buttonSetFile;
    @FXML
    private Button buttonOk;

    private AtomicReference<File> fileRef = new AtomicReference<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }

    @FXML
    private void changeType(ActionEvent event) {
        PainterType item = type.getValue();
        switch (item) {
            case HEATMAP:
                colorPicker.setDisable(true);
                break;
            default:
                colorPicker.setDisable(false);
        }
    }

    @FXML
    private void buttonSetFileAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(Setup.lastDir.get());
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Points in CSV", "*.csv", "*.txt");
        fileChooser.getExtensionFilters().add(filter);
        File file = fileChooser.showOpenDialog(box.getScene().getWindow());
        if (file != null) {
            buttonSetFile.setText(file.getName());
            Setup.lastDir.set(file.getParentFile());
            buttonOk.setDisable(false);
        } else {
            buttonSetFile.setText("Set file");
            buttonOk.setDisable(true);
        }
        fileRef.set(file);
    }

    @FXML
    private void buttonOkAction(ActionEvent event) {
        javafx.scene.paint.Color color = colorPicker.getValue();
        File file = fileRef.get();
        if (file != null) {
            Setup.mapController.addPoints(file,
                    new Color((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getOpacity()),
                    type.getValue());
            ((Stage) box.getScene().getWindow()).close();
        }
    }
}
