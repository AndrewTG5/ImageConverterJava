package ImageConverter;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

public class imageHandler {
    public static BufferedImage image;

    public static void read(String path) throws IOException {
        File file = new File(path);

        try (ImageInputStream input = ImageIO.createImageInputStream(file)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);

            if (!readers.hasNext()) {
                throw new IllegalArgumentException("No reader for: " + file);
            }

            ImageReader reader = readers.next();

            try {
                reader.setInput(input);

                ImageReadParam param = reader.getDefaultReadParam();

                // Optionally, control read settings like sub sampling, source region or destination etc.
                //param.setSourceSubsampling(...);
                //param.setSourceRegion(...);
                //param.setDestination(...);

                // Finally read the image, using settings from param
                image = reader.read(0, param);
                //System.out.println(image); has lots of useful image information

                // Optionally, read thumbnails, meta data, etc...
                //int numThumbs = reader.getNumThumbnails(0);
                // ...
            } finally {
                reader.dispose();
            }
        }
    }


    public static void convert(String outputPath, String type) throws IOException, AWTException {

        // Get the writer
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(type);

        if (!writers.hasNext()) {
            throw new IllegalArgumentException("No writer for: " + type);
        }

        ImageWriter writer = writers.next();

        try {
            try (ImageOutputStream output = ImageIO.createImageOutputStream(new File(outputPath))) {
                writer.setOutput(output);

                ImageWriteParam param = writer.getDefaultWriteParam();

                // Optionally, control format specific settings of param (requires casting), or
                // control generic write settings like sub sampling, source region, output type etc.



                /*  streamMetadata - an IIOMetadata object representing stream metadata, or null to use default values.
                    image - an IIOImage object containing an image, thumbnails, and metadata to be written.
                    param - an ImageWriteParam, or null to use a default ImageWriteParam.
                 */
                writer.write(null, new IIOImage(image, null, null), param);
                mainController.trayMessage("Image converted Successfully");
            }
        }
        finally { writer.dispose(); }
    }

    public static String readHeight(String path) throws IOException {
        File file = new File(path);
        try(ImageInputStream in = ImageIO.createImageInputStream(file)){
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            ImageReader reader = readers.next();
            reader.setInput(in);
            return String.valueOf(reader.getHeight(0));
        }
    }

    public static String readWidth(String path) throws IOException {
        File file = new File(path);
        try(ImageInputStream in = ImageIO.createImageInputStream(file)){
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            ImageReader reader = readers.next();
            reader.setInput(in);
            return String.valueOf(reader.getWidth(0));
        }
    }
}