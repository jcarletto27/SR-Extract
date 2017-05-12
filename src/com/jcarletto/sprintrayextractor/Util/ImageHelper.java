package com.jcarletto.sprintrayextractor.Util;


import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by user on 5/6/2017.
 */
public class ImageHelper {


    public int getPaddingValue(double imageHeight, double printerHeight) {
        Double pad = printerHeight - imageHeight;


        if (pad <= 0) {
            return 1;
        } else

            return pad.intValue();
    }

    public BufferedImage getBufferedImageFromStream(ByteArrayInputStream stream) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public BufferedImage resizeBaseImage(BufferedImage image, int targetWidth, int targetHeight) {


        BufferedImage newImage = Scalr.resize(image, Scalr.Method.AUTOMATIC, Scalr.Mode.FIT_TO_WIDTH, targetWidth);

        return newImage;
    }

    public BufferedImage addPadding(BufferedImage image, int padding) {
        BufferedImage bufferedImage = Scalr.pad(image, padding, java.awt.Color.BLACK);

        return bufferedImage;
    }

    public void writePNGFile(BufferedImage image, File file) {
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BufferedImage cropImage(BufferedImage image, int targetWidth, int targetHeight) {
        BufferedImage cropped = Scalr.crop(image, (image.getWidth() - targetWidth) / 2, (image.getHeight() - targetHeight) / 2, targetWidth, targetHeight);
        return cropped;

    }

    public float getXYRes(float xPixels, float xWidthMM) {
        return (xWidthMM * 1000f) / xPixels;
    }

    public BufferedImage antiAlias(BufferedImage image) {
        BufferedImage aliased = Scalr.apply(image, Scalr.OP_ANTIALIAS);

        return aliased;

    }


    public double getScreenWidthInMM() {
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int pixelPerInch = java.awt.Toolkit.getDefaultToolkit().getScreenResolution();

        double width = screen.getWidth() / pixelPerInch;


        return width * 25.4;
    }

    public double getScreenWidthInPixels() {
        Dimension screen = java.awt.Toolkit.getDefaultToolkit().getScreenSize();

        return screen.getWidth();
    }

    public double scaleImageForScreen(float imageRes) {

        double scaleRes = (getScreenWidthInMM() * 1000) / getScreenWidthInPixels();

        return imageRes / scaleRes;
    }


    public void processForFolder(ByteArrayInputStream imageData, int targetWidth, int targetHeight, float scale, int padding, int antiAliasPasses, File file) {
        BufferedImage slice = getBufferedImageFromStream(imageData);

        Float scaledWidth = (float) slice.getWidth() * scale;
        Float scaledHeight = (float) slice.getHeight() * scale;

        slice = resizeBaseImage(slice, scaledWidth.intValue(), scaledHeight.intValue());
        slice = addPadding(slice, padding);
        slice = cropImage(slice, targetWidth, targetHeight);

        for (int x = 0; x < antiAliasPasses; x++) {
            slice = antiAlias(slice);
        }
        writePNGFile(slice, file);

    }

    public byte[] processForZip(byte[] imageBytes, int targetWidth, int targetHeight, float scale, int padding, int antiAliasPasses) {
        BufferedImage slice;


        byte[] outBytes = {};
        try {


            slice = ImageIO.read(new ByteArrayInputStream(imageBytes));

            Float scaledWidth = (float) slice.getWidth() * scale;
            Float scaledHeight = (float) slice.getHeight() * scale;

            slice = resizeBaseImage(slice, scaledWidth.intValue(), scaledHeight.intValue());
            slice = addPadding(slice, padding);
            slice = cropImage(slice, targetWidth, targetHeight);

            for (int x = 0; x < antiAliasPasses; x++) {
                slice = antiAlias(slice);
            }
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            ImageIO.write(slice, "png", outputStream);

            outBytes = outputStream.toByteArray();
            outputStream.flush();
            outputStream.close();


        } catch (IOException e) {
            e.printStackTrace();
        }

        return outBytes;
    }

    public int countPixelsOfAColor(Image image, Color color) {
        int count = 0;

        for (int w = 0; w < image.getWidth(); w++) {
            for (int h = 0; h < image.getHeight(); h++) {
                Color c = image.getPixelReader().getColor(w, h);
                if (c.equals(color)) {
                    count++;
                }

            }
        }


        return count;
    }

    public double areaOfPixelsInMMSq(int pixelCount, float res) {

        double out = (res * Math.sqrt(pixelCount)) / 1000;
        return Math.pow(out, 2);
    }

}
//.01