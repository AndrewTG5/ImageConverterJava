module ImageConverterJava {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens ImageConverter to javafx.fxml;
    exports ImageConverter;
}