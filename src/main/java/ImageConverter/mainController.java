package ImageConverter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;

public class mainController {

    @FXML
    public ImageView imageView;
    public TextField outputPath;
    public Button convertButton;
    public ChoiceBox<String> filetypeSelect;
    public Label dropLabel;
    //public ProgressBar progressBar;

    public String urls;
    public String pickedOutput;

    //static float inProgress;


    public void initialize() {
        ObservableList<String> filetypeList = FXCollections.observableArrayList(ImageIO.getWriterFormatNames());
        filetypeSelect.setItems(filetypeList);
    }

    public static void progressBar(float progress){
        //int progressInt = Math.round(progress);
        ProgressBar progressBar = new ProgressBar();
        progressBar.setProgress(progress);
    }

    public void handleDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void handleDrop(DragEvent dragEvent) throws IOException {
        List<File> files = dragEvent.getDragboard().getFiles();
        urls = dragEvent.getDragboard().getUrl().substring(6);
        Image img = new Image(new FileInputStream(files.get(0)));
        imageView.setImage(img);
        dropLabel.setVisible(false);
        imageHandler.read(urls);
    }

    public void handleDropClick(MouseEvent mouseEvent) throws IOException {
        String[] filterList = ImageIO.getReaderFileSuffixes();
        for(int i = 0; i < filterList.length; i++) {
            filterList[i] = "*." + filterList[i];
        }

        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Supported Images", filterList);
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(Main.getStage());
        URI uri = file.toURI(); //  maybe don't need?
        URL url = uri.toURL();
        urls = url.toString().substring(6);
        Image img = new Image(new FileInputStream(file));
        imageView.setImage(img);
        dropLabel.setVisible(false);
        imageHandler.read(urls);
    }

    public void convertClick(ActionEvent actionEvent) throws IOException, AWTException {
        String output = outputPath.getText();
        String type = filetypeSelect.getValue();
        imageHandler.convert(output, type);
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

    public static void trayMessage(String message) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        java.awt.Image image = Toolkit.getDefaultToolkit().createImage("icon.png");
        TrayIcon trayIcon = new TrayIcon(image, "ImageConverter");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("ImageConverter");
        tray.add(trayIcon);
        trayIcon.displayMessage(message, "ImageConverter", TrayIcon.MessageType.INFO);
    }
}
