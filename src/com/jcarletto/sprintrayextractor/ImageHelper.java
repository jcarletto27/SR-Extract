package com.jcarletto.sprintrayextractor;


import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by user on 5/6/2017.
 */
public class ImageHelper {


    public int getPaddingValue(double imageHeight, double printerHeight) {
        Double pad = printerHeight - imageHeight;
        pad = pad / 2;

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
        BufferedImage newImage = Scalr.resize(image, Scalr.Method.ULTRA_QUALITY, Scalr.Mode.FIT_TO_WIDTH, targetWidth);

        return newImage;
    }

    public BufferedImage addPadding(BufferedImage image, int padding) {
        BufferedImage bufferedImage = Scalr.pad(image, padding, Color.BLACK);

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


    public void process(ByteArrayInputStream imageData, int targetWidth, int targetHeight, int padding, File file) {
        BufferedImage slice = getBufferedImageFromStream(imageData);
        slice = antiAlias(slice);
        slice = resizeBaseImage(slice, targetWidth, targetHeight);
        slice = addPadding(slice, padding);
        slice = cropImage(slice, targetWidth, targetHeight);

        writePNGFile(slice, file);

    }

}