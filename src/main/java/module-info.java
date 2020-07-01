module ImageConverterJava {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires org.apache.commons.io;

    opens ImageConverter to javafx.fxml;
    exports ImageConverter;
}