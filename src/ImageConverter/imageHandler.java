package ImageConverter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class imageHandler {
    public static void convert(String param, String output, String path, String type) throws IOException {
        File file = new File(path);
        BufferedImage image = ImageIO.read(file);
        ImageIO.write(image, type, new File(output));
    }
}
