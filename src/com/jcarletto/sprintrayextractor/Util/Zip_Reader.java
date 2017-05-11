package com.jcarletto.sprintrayextractor.Util;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by jcarlett on 5/10/2017.
 */
public class Zip_Reader {
    private static final int BUFFER = 2048;
    private List<byte[]> bytes;
    private File zfile = null;

    private ZipFile zipFile = null;

    public Zip_Reader(File file) {
        try {
            zipFile = new ZipFile(file);
            this.zfile = file;
            bytes = readZipBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public byte[] getImage(InputStream in) {
        try {
            BufferedImage bufferedImage = ImageIO.read(in);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "png", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


    public List<byte[]> readZipBytes() {
        Enumeration zipFileEntries = zipFile.entries();
        List<byte[]> outBytes = null;
        InputStream is = null;

        outBytes = new ArrayList<byte[]>();
        for (int x = 0; x < zipFile.size() + 1; x++) {
            outBytes.add(new byte[]{});
        }

        while (zipFileEntries.hasMoreElements()) {




            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();


            if (FilenameUtils.getExtension(currentEntry).equals("png")) {
                int entryNumber = Integer.parseInt(currentEntry.replace(".png", "")) - 1;
                System.out.println("Entry Number : " + entryNumber);
                try {
                    is = zipFile.getInputStream(entry);
                    outBytes.set(entryNumber, getImage(is));


                } catch (IOException e) {
                    e.printStackTrace();

                } finally {

                    IOUtils.closeQuietly(is);
                }

            }
        }


        return outBytes;
    }


    public List<byte[]> getPngBytes() {
        return bytes;
    }
}
