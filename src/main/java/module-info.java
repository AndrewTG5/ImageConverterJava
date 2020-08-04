module ImageConverterJava {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.commons.io;
    requires com.sun.jna.platform;

    opens ImageConverter to javafx.fxml;
    exports ImageConverter;
}