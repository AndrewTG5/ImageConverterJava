package ImageConverter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

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
        // Gets supported ImageIO output filetypes and adds them to the output type dropdown. Cleans duplicates.
        String[] extList = ImageIO.getWriterFormatNames();
        for (int i = 0; i < extList.length; i++) {
            extList[i] = extList[i].toUpperCase();
        }
        LinkedHashSet<String> filteredList = new LinkedHashSet<>(Arrays.asList(extList));
        String[] newArray = filteredList.toArray(new String[0]);
        Arrays.sort(newArray);
        ObservableList<String> filetypeList = FXCollections.observableArrayList(newArray);
        filetypeSelect.setItems(filetypeList);
        // Checks clipboard once for images so images can be copied before opening the app.
        pasteButton.setDisable(!Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable(imageFlavor));
        // Adds clipboard listener
        Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(e -> {
            // Simplified if else statement to disable or enable pasteButton depending if the data in the clipboard is an image
            try {
                Thread.sleep(100);
            } catch (InterruptedException interruptedException) {
                interruptedException.printStackTrace();
            }
            // Sleep otherwise java.lang.IllegalStateException: cannot open system clipboard
            pasteButton.setDisable(!Toolkit.getDefaultToolkit().getSystemClipboard().isDataFlavorAvailable(imageFlavor));
        });
    }

    /**
     * Called when pasteButton is clicked. Reads an image from clipboard, and saves it as a png in the OS temp folder. Does misc other tasks to display the image in the UI and read parameters.
     */
    public void handlePaste() throws IOException, UnsupportedFlavorException {
        BufferedImage pImg = (BufferedImage) Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null).getTransferData(DataFlavor.imageFlavor);
        File file = File.createTempFile("pastedImg", "png");
        ImageIO.write(pImg, "png", file);
        urls = file.getAbsolutePath();
        Image img = new Image(new FileInputStream(urls));
        imageView.setImage(img);
        dropLabel.setVisible(false);
        readParam();
        loadedFile.setText("Pasted from clipboard");
        outputPath.clear();
    }

    /**
     * Called when files are dragged over, accepts dragged files.
     * @param dragEvent The JavaFX drag event
     */
    public void handleDragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.ANY);
        }
    }

    /**
     * Called when files are dropped. Loads the file, reads parameters and other misc tasks
     * @param dragEvent The JavaFX drag event
     */
    public void handleDrop(DragEvent dragEvent) throws IOException {
        List<File> files = dragEvent.getDragboard().getFiles();
        urls = dragEvent.getDragboard().getUrl().substring(6);
        Image img = new Image(new FileInputStream(files.get(0)));
        imageView.setImage(img);
        dropLabel.setVisible(false);
        readParam();
        loadedFile.setText(urls);
        outputPath.clear();
    }

    /**
     * Called when the drop area is clicked. Brings up a JavaFX file chooser that filters image files supported by ImageIO. Loads the selected file and does misc related tasks like other import methods.
     */
    public void handleDropClick() throws IOException {
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
    }

    /**
     * Called when the convert button is clicked. Collects output parameters from UI and sends them to imageHandler.
     */
    public void convertClick() throws IOException, AWTException {
        String output = outputPath.getText();
        String type = filetypeSelect.getValue();
        imageHandler.convert(output, type);
    }

    /**
     *  Called when the save to button is clicked. Makes a JavaFX file chooser that defaults to the source image file path. Lets the user pick a path and file name for the output file.
     */
    public void outputPicker() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(FilenameUtils.getFullPath(urls)));
        String filter = "*."+FilenameUtils.getExtension(urls);
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image file", filter);
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showSaveDialog(Main.getStage());
        pickedOutput = file.getPath();
        outputPath.setText(pickedOutput);
    }

    /**
     * Called whenever a system tray notification is needed. Takes a message and the app icon and makes a tray notification.
     * @param message The message to display
     */
    public static void trayMessage(String message) throws AWTException {
        SystemTray tray = SystemTray.getSystemTray();
        java.awt.Image image = Toolkit.getDefaultToolkit().createImage(mainController.class.getResource("icon.png"));
        TrayIcon trayIcon = new TrayIcon(image, "ImageConverter");
        trayIcon.setImageAutoSize(true);
        trayIcon.setToolTip("ImageConverter");
        tray.add(trayIcon);
        trayIcon.displayMessage(message, "ImageConverter", TrayIcon.MessageType.INFO);
    }

    /**
     * Called when an image is imported. Gets parameters from the image and sets them as defaults in the UI.
     */
    public void readParam() throws IOException {
        imageHandler.read(urls);
        height.setText(imageHandler.readHeight(urls));
        width.setText(imageHandler.readWidth(urls));
    }
}
