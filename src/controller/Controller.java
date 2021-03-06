package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.CarDetection;

import java.io.File;

public class Controller {

    @FXML
    private ImageView imageVIew;
    @FXML
    private Label label;

    @FXML
    public void handleOpenMenu(ActionEvent event) {
        //declare extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Video files (*.avi)", "*.avi");
        FileChooser fileChooser = new FileChooser();
        //add extension filter
        fileChooser.getExtensionFilters().add(extFilter);
        //show choosing file dialog
        File file = fileChooser.showOpenDialog(new Stage());
        CarDetection carDetection = new CarDetection();
        carDetection.detect(file.getAbsolutePath(), imageVIew, label);
    }

}
