package ImageConverter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class mainController {

    @FXML
    public ImageView imageView;
    public TextField outputPath;
    public Button convertButton;
    public ChoiceBox<String> filetypeSelect;

    public String urls;
    public String pickedOutput;


    public void initialize() {
        String[] filetypes = { "PNG", "JPEG", "BMP" };
        ObservableList<String> filetypeList = FXCollections.observableArrayList(filetypes);
        filetypeSelect.setItems(filetypeList);
    }

    public void handleDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void handleDrop(DragEvent dragEvent) throws FileNotFoundException {
        List<File> files = dragEvent.getDragboard().getFiles();
        urls = dragEvent.getDragboard().getUrl().substring(6);
        Image img = new Image(new FileInputStream(files.get(0)));
        imageView.setImage(img);
    }

    public void convertClick(ActionEvent actionEvent) throws IOException {
        String param = "param";
        String output = outputPath.getText();
        String path = urls;
        String type = filetypeSelect.getValue();
        imageHandler.convert(param, output, path, type);
    }

    public void outputPicker(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        String filter = "*."+filetypeSelect.getValue();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image file", filter);
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(Main.getStage());
        pickedOutput = file.getPath();
        outputPath.setText(pickedOutput);
    }
}
