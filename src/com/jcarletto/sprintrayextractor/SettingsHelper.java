package com.jcarletto.sprintrayextractor;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created by user on 5/6/2017.
 */
public class SettingsHelper {

    Properties properties = new Properties();
    OutputStream outputStream = null;


    private List<String[]> defaultSettings() {
        Printer printer = new Printer();


        return printer.getPrinterSettings();
    }


    public void writeProps(List<String[]> propList) {
        try {
            outputStream = new FileOutputStream("config.properties");

            for (String[] strings : propList) {
                properties.setProperty(strings[0], strings[1]);
            }

            properties.store(outputStream, null);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public List<String[]> readProps() {
        Properties properties = new Properties();
        InputStream inputStream = null;
        List<String[]> out = new ArrayList<>();
        try {
            inputStream = new FileInputStream("config.properties");
            properties.load(inputStream);
            Enumeration enumeration = properties.propertyNames();
            while (enumeration.hasMoreElements()) {
                String key = (String) enumeration.nextElement();
                out.add(new String[]{key, properties.getProperty(key)});
            }

        } catch (FileNotFoundException e) {
            Printer printer = new Printer();
            return printer.getPrinterSettings();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return out;
    }


}
