package ImageConverter;

import javax.imageio.*;
import javax.imageio.event.IIOReadProgressListener;
import javax.imageio.event.IIOWriteProgressListener;
import javax.imageio.metadata.IIOMetadata;
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
        ImageInputStream input = ImageIO.createImageInputStream(file);

        try {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(input);

            if (!readers.hasNext()) {
                throw new IllegalArgumentException("No reader for: " + file);
            }

            ImageReader reader = readers.next();

            try {
                reader.setInput(input);

                // Optionally, listen for read warnings, progress, etc.
                //reader.addIIOReadWarningListener(...);
                IIOReadProgressListener Rlistener = new ProgressListener();
                //reader.addIIOReadProgressListener(Rlistener);

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
            }
            finally { reader.dispose(); }
        }
        finally { input.close(); }
    }


    public static void convert(String outputPath, String type) throws IOException, AWTException {

        // Get the writer
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(type);

        if (!writers.hasNext()) {
            throw new IllegalArgumentException("No writer for: " + type);
        }

        ImageWriter writer = writers.next();

        try {
            // Create output stream
            ImageOutputStream output = ImageIO.createImageOutputStream(new File(outputPath));

            try {
                writer.setOutput(output);

                IIOWriteProgressListener Wlistener = new ProgressListener();
                //writer.addIIOWriteProgressListener(Wlistener);

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
            finally { output.close(); }
        }
        finally { writer.dispose(); }
    }
}

class ProgressListener implements IIOReadProgressListener, IIOWriteProgressListener {

    public ProgressListener() {}


    @Override
    public void sequenceStarted(ImageReader source, int minIndex) { }

    @Override
    public void sequenceComplete(ImageReader source) { }

    @Override
    public void imageStarted(ImageReader source, int imageIndex) { mainController.progressBar(0.1f); }

    @Override
    public void imageProgress(ImageReader source, float percentageDone) { mainController.progressBar(percentageDone); }

    @Override
    public void imageComplete(ImageReader source) { mainController.progressBar(0.1f); }

    @Override
    public void thumbnailStarted(ImageReader source, int imageIndex, int thumbnailIndex) { }

    @Override
    public void thumbnailProgress(ImageReader source, float percentageDone) { }

    @Override
    public void thumbnailComplete(ImageReader source) { }

    @Override
    public void readAborted(ImageReader source) { }

    @Override
    public void imageStarted(ImageWriter source, int imageIndex) { }

    @Override
    public void imageProgress(ImageWriter source, float percentageDone) { }

    @Override
    public void imageComplete(ImageWriter source) { }

    @Override
    public void thumbnailStarted(ImageWriter source, int imageIndex, int thumbnailIndex) { }

    @Override
    public void thumbnailProgress(ImageWriter source, float percentageDone) { }

    @Override
    public void thumbnailComplete(ImageWriter source) { }

    @Override
    public void writeAborted(ImageWriter source) { }
}
