package ImageConverter;

import javax.imageio.*;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import static java.awt.image.BufferedImage.TYPE_INT_ARGB;

public class imageHandler {
    public static BufferedImage image;

    /**
     * Reads the image specified by path and saves it to a buffered images.
     * @param path The image to read
     */
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

    /**
     * Converts and image to the specified file type and saves it to the specified location
     * @param outputPath Where to save the image
     * @param type The format to convert the image to
     * @param height The height to resize the image to
     * @param width The width to resize the image to
     */
    public static void convert(String outputPath, String type, int height, int width) throws IOException, AWTException {
        // Get the writer
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(type);
        if (!writers.hasNext()) {
            throw new IllegalArgumentException("No writer for: " + type);
        }

        BufferedImage sizedImage = resizeImage(image, width, height);

        ImageWriter writer = writers.next();
        try {
            try (ImageOutputStream output = ImageIO.createImageOutputStream(new File(outputPath))) {
                writer.setOutput(output);
                ImageWriteParam param = writer.getDefaultWriteParam();

                // Optionally, control format specific settings of param (requires casting), or
                // control generic write settings like sub sampling, source region, output type etc.


                //  streamMetadata - an IIOMetadata object representing stream metadata, or null to use default values.
                //  image - an IIOImage object containing an image, thumbnails, and metadata to be written.
                // param - an ImageWriteParam, or null to use a default ImageWriteParam.

                writer.write(null, new IIOImage(sizedImage, null, null), param);
                mainController.trayMessage("Image converted Successfully");
            }
        }
        finally { writer.dispose(); }
    }

    /**
     * Resizes the image for the convert method.
     * @param originalImage The image to resize as a BufferedImage.
     * @param img_width The width to resize to.
     * @param img_height The height to resize to.
     * @return The resized BufferedImage
     */
    private static BufferedImage resizeImage(BufferedImage originalImage, Integer img_width, Integer img_height)
    {
        BufferedImage resizedImage = new BufferedImage(img_width, img_height, TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, img_width, img_height, null);
        g.dispose();

        return resizedImage;
    }

    /**
     * Gets the height of an image for use in the UI
     * @param path The image to read
     * @return The height of the image, in pixels. Needs to be a String for JavaFX
     */
    public static String readHeight(String path) throws IOException {
        File file = new File(path);
        try(ImageInputStream in = ImageIO.createImageInputStream(file)){
            final Iterator<ImageReader> readers = ImageIO.getImageReaders(in);
            ImageReader reader = readers.next();
            reader.setInput(in);
            return String.valueOf(reader.getHeight(0));
        }
    }

    /**
     * Gets the width of an image for use in the UI
     * @param path The image to read
     * @return The width of the image, in pixels. Needs to be a String for JavaFX
     */
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