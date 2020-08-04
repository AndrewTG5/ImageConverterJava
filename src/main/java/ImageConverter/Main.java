package ImageConverter;

import com.sun.jna.platform.win32.Advapi32Util;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import static com.sun.jna.platform.win32.WinReg.HKEY_CURRENT_USER;

public class Main extends Application {

    private static Stage stage;
    public static Stage getStage() { return stage; }

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        Scene scene = new Scene(root);
        stage.getIcons().add(new Image(getClass().getResource("icon.png").toExternalForm()));
        // system dark theme check
        if (Advapi32Util.registryGetIntValue(HKEY_CURRENT_USER, "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Themes\\Personalize", "AppsUseLightTheme") == 0) {
            scene.getStylesheets().add(getClass().getResource("dark.css").toExternalForm());
        } else {
            scene.getStylesheets().clear();
        }
        primaryStage.setTitle("ImageConverter");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        // Kills process on exit, needed because trayIcon persists
        primaryStage.setOnCloseRequest(event -> System.exit(0));
    }

    public static void main(String[] args) {
        launch(args);
    }
}

