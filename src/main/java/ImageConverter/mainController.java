package ImageConverter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import static java.awt.datatransfer.DataFlavor.imageFlavor;

public class mainController {

    @FXML
    public ImageView imageView;
    public TextField outputPath;
    public Button convertButton;
    public ComboBox<String> filetypeSelect;
    public Label dropLabel;
    public Label loadedFile;
    public TextField width;
    public TextField height;

    public String urls;
    public String pickedOutput;
    public Button pasteButton;

    public void initialize() {
        String[] extList = ImageIO.getWriterFormatNames();
        for (int i = 0; i < extList.length; i++) {
            extList[i] = extList[i].toUpperCase();
        }
        LinkedHashSet<String> filteredList = new LinkedHashSet<>(Arrays.asList(extList));
        String[] newArray = filteredList.toArray(new String[0]);
        Arrays.sort(newArray);
        ObservableList<String> filetypeList = FXCollections.observableArrayList(newArray);
        filetypeSelect.setItems(filetypeList);
        Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(new FlavorListener() {
            //adds clipboard listener
            @Override
            public void flavorsChanged(FlavorEvent e) {
                //simplified if else statement to disable or enable pasteButton depending if the data in the clipboard is an image
                try {
                    Thread.sleep(100);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
                //sleep otherwise java.lang.IllegalStateException: cannot open system clipboard
                pasteButton.setDisable(!Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable(imageFlavor));
            }
        });
    }

    public void handlePaste(ActionEvent actionEvent) throws IOException, UnsupportedFlavorException {
        BufferedImage pImg = (BufferedImage) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.imageFlavor);
        File file = File.createTempFile("pastedImg", "png");
        ImageIO.write(pImg, "png", file);
        Image img = new Image(new FileInputStream(file.getAbsolutePath()));
        imageView.setImage(img);
        dropLabel.setVisible(false);
        readParam();
        loadedFile.setText(urls);
        outputPath.clear();
        pasteButton.setDisable(true);
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
        readParam();
        loadedFile.setText(urls);
        outputPath.clear();
        pasteButton.setDisable(true);
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
        URL url = file.toURI().toURL();
        urls = url.toString().substring(6);
        Image img = new Image(new FileInputStream(file));
        imageView.setImage(img);
        dropLabel.setVisible(false);
        readParam();
        loadedFile.setText(urls);
        outputPath.clear();
        pasteButton.setDisable(true);
    }

    public void convertClick(ActionEvent actionEvent) throws IOException, AWTException {
        String output = outputPath.getText();
        String type = filetypeSelect.getValue();
        imageHandler.convert(output, type);
    }

    public void outputPicker(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(FilenameUtils.getFullPath(urls)));
        String filter = "*."+FilenameUtils.getExtension(urls);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image file", filter);
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(Main.getStage());
        pickedOutput = file.getPath();
        outputPath.setText(pickedOutput);
    }

    public static void trayMessage(String message) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        java.awt.Image image = Toolkit.getDefaultToolkit().createImage(mainController.class.getResource("icon.png"));
        TrayIcon trayIcon = new TrayIcon(image, "ImageConverter");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("ImageConverter");
        tray.add(trayIcon);
        trayIcon.displayMessage(message, "ImageConverter", TrayIcon.MessageType.INFO);
    }

    public void readParam() throws IOException {
        imageHandler.read(urls);
        height.setText(imageHandler.readHeight(urls));
        width.setText(imageHandler.readWidth(urls));
    }
}
