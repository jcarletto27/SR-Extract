package com.jcarletto.sprintrayextractor.Util;


import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
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

    private byte[] byteArray;
    private List<byte[]> pngBytes;
    private String ssjFileInfo = "";

    public SSJ_Reader(File file) {


        setSsjFile(file);
        openFile();



        ssjFileInfo = parseInfo(pngBytes.get(0)).replace(" ", "").split("microns")[0].split("at")[1];


        pngBytes.remove(0);


    }

    public List<byte[]> getPngBytes() {
        return pngBytes;
    }

    public void setPngBytes(List<byte[]> pngBytes) {
        this.pngBytes = pngBytes;
    }

    public ByteArrayInputStream getStreamFromIndex(byte[] index) {

        return new ByteArrayInputStream(index);
    }

    public File getSsjFile() {
        return ssjFile;
    }

    private void setSsjFile(File ssjFile) {
        this.ssjFile = ssjFile;
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
        for (int x = 0; x < pngBytes.size(); x++) {
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

    private String parseInfo(byte[] bytes) {
        List<Byte> newArray = new ArrayList<>();
        byte space = (byte) 0x00;
        for (byte b : bytes) {
            if (b != space) {
                newArray.add(b);

            }
        }
        Byte[] fromList = newArray.toArray(new Byte[newArray.size()]);
        String fromArray = new String(ArrayUtils.toPrimitive(fromList));

        return fromArray;
    }

    public String getSsjFileInfo() {
        return ssjFileInfo;
    }


}
