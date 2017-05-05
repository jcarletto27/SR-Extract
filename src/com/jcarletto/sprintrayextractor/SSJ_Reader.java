package com.jcarletto.sprintrayextractor;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * Created by jcarlett on 5/5/2017.
 */

public class SSJ_Reader {

    private final byte[] delimiter = {(byte) 0x89, (byte) 0x50, (byte) 0x4e, (byte) 0x47, (byte) 0x0d, (byte) 0x0a, (byte) 0x1a, (byte) 0x0a};
    private final byte[] IEND = "IEND".getBytes();

    private File ssjFile;
    private String contents;
    private byte[] byteArray;
    private List<byte[]> pngBytes;

    public SSJ_Reader(File file) {
        System.out.println("Delim : " + new String(delimiter));
        // System.out.println("IEND : " + new String(IEND));

        setSsjFile(file);
        openFile();
        readFileContents();
        pngBytes.remove(0);


    }

    public List<byte[]> getPngBytes() {
        return pngBytes;
    }

    public void setPngBytes(List<byte[]> pngBytes) {
        this.pngBytes = pngBytes;
    }


    private void openFile() {

        try {
            byteArray = IOUtils.toByteArray(new FileInputStream(ssjFile));

            setPngBytes(splitFile(delimiter));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFileContents() {
        for (int x = 1; x < pngBytes.size(); x++) {
            System.out.println(new String(pngBytes.get(x)));
        }

    }

    private List<byte[]> splitFile(byte[] delimiter) {
        List<byte[]> arrays = new LinkedList<>();
        if (delimiter.length == 0) {
            return arrays;
        }
        int begin = 0;

        outer:
        for (int i = 0; i < byteArray.length - delimiter.length + 1; i++) {
            for (int j = 0; j < delimiter.length; j++) {
                if (byteArray[i + j] != delimiter[j]) {
                    continue outer;
                }
            }
            arrays.add(ArrayUtils.addAll(delimiter, Arrays.copyOfRange(byteArray, begin, i)));
            begin = i + delimiter.length;
        }
        arrays.add(Arrays.copyOfRange(byteArray, begin, byteArray.length));
        return arrays;
    }

    private void setSsjFile(File ssjFile) {
        this.ssjFile = ssjFile;
    }


}
