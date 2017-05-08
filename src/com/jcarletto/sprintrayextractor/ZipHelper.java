package com.jcarletto.sprintrayextractor;

/**
 * Created by jcarlett on 5/5/2017.
 */

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipHelper {

    public void zip(File file, List<byte[]> images) {
        ZipOutputStream zipOutputStream;
        try {

            zipOutputStream = new ZipOutputStream(new FileOutputStream(file));

            for (int x = 0; x < images.size()-1; x++) {
                byte[] b = images.get(x);
                String fileName = String.format("%05d", x + 1) + ".png";
                zipOutputStream.putNextEntry(new ZipEntry(fileName));
                zipOutputStream.write(b);
                zipOutputStream.closeEntry();
            }

            zipOutputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
