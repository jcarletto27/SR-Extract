package com.jcarletto.sprintrayextractor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 5/6/2017.
 */
public class Printer {
    final String ANTI_ALIAS_PASSES = "ANTI_ALIAS_PASSES";

    final String PRINTER_NAME = "Printer_Name";
    final String XY_RES = "XYRES";
    final String X_PIXELS = "XPIXELS";
    final String Y_PIXELS = "YPIXELS";
    final String AUTOPLAY_SLIDESHOW = "AUTOPLAY_SLIDESHOW";
    final String REALISTIC_SCALING = "REALISTIC_SCALING";


    final String SSJ_Y_PIXELS = "SSJ_Y_PIXELS";
    final String SSJ_X_PIXELS = "SSJ_X_PIXELS";
    final String SSJ_XY_RES = "SSJ_XY_RES";

    final String LAST_OPEN_LOCATION = "LAST_OPEN_LOCATION";
    final String LAST_SAVE_LOCATION = "LAST_SAVE_LOCATION";

    private String lastSaveLocation = new File(System.getProperty("user.home") + "/Desktop").getAbsolutePath();
    private String lastOpenLocation = new File(System.getProperty("user.home") + "/Desktop").getAbsolutePath();


    private int antiAliasPasses = 0;
    private String printerName = "diy";
    private float printerXYRes = 100f;
    private double xPixels = 1920d;
    private double yPixels = 1080d;

    private boolean autoPlay = true;
    private boolean realisticScaling = true;

    private double ssjXPixels = 1280;
    private double ssjYPixels = 800;
    private float ssjRes = 100f;


    public Printer() {

    }

    public Printer(String printerName, double xPixels, double yPixels, float printerXYRes) {
        setPrinterXYRes(printerXYRes);
        setPrinterName(printerName);
        setxPixels(xPixels);
        setyPixels(yPixels);
    }

    public void setPrinterProps(int xPixels, int yPixels, float res) {
        setxPixels((double) xPixels);
        setyPixels((double) yPixels);
        setPrinterXYRes(res);
    }

    public void setSSJProps(double xPixels, double yPixels, float ssjRes) {
        setSsjRes(ssjRes);
        setSsjXPixels(xPixels);
        setSsjYPixels(yPixels);

    }


    public List<String[]> getPrinterSettings() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{PRINTER_NAME, getPrinterName()});
        list.add(new String[]{XY_RES, String.valueOf(getPrinterXYRes())});
        list.add(new String[]{X_PIXELS, String.valueOf(getxPixels())});
        list.add(new String[]{Y_PIXELS, String.valueOf(getyPixels())});

        list.add(new String[]{ANTI_ALIAS_PASSES, String.valueOf(getAntiAliasPasses())});

        list.add(new String[]{SSJ_Y_PIXELS, String.valueOf(getSsjYPixels())});
        list.add(new String[]{SSJ_X_PIXELS, String.valueOf(getSsjXPixels())});
        list.add(new String[]{SSJ_XY_RES, String.valueOf(getSsjRes())});

        list.add(new String[]{AUTOPLAY_SLIDESHOW, String.valueOf(getAutoPlaySlideShow())});
        list.add(new String[]{REALISTIC_SCALING, String.valueOf(getRealisticScaling())});

        list.add(new String[]{LAST_OPEN_LOCATION, String.valueOf(getLastOpenLocation())});
        list.add(new String[]{LAST_SAVE_LOCATION, String.valueOf(getLastSaveLocation())});
        return list;
    }

    public void setPrinterSettings(List<String[]> settings) {
        for (String[] s : settings) {
            switch (s[0]) {
                case PRINTER_NAME:
                    setPrinterName(s[1]);
                    break;
                case XY_RES:
                    setPrinterXYRes(Float.parseFloat(s[1]));
                    break;
                case X_PIXELS:
                    setxPixels(Double.parseDouble(s[1]));
                    break;
                case Y_PIXELS:
                    setyPixels(Double.parseDouble(s[1]));
                    break;
                case SSJ_XY_RES:
                    setSsjRes(Float.parseFloat(s[1]));
                    break;
                case SSJ_X_PIXELS:
                    setSsjXPixels(Double.parseDouble(s[1]));
                    break;
                case SSJ_Y_PIXELS:
                    setSsjYPixels(Double.parseDouble(s[1]));
                    break;
                case ANTI_ALIAS_PASSES:
                    setAntiAliasPasses(Integer.parseInt(s[1]));
                    break;
                case AUTOPLAY_SLIDESHOW:
                    setAutoPlay(Boolean.parseBoolean(s[1]));
                    break;
                case REALISTIC_SCALING:
                    setRealisticScaling(Boolean.parseBoolean(s[1]));
                    break;
            }
        }
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
    }

    public double getxPixels() {
        return xPixels;
    }

    public void setxPixels(double xPixels) {
        this.xPixels = xPixels;
    }

    public double getyPixels() {
        return yPixels;
    }

    public void setyPixels(double yPixels) {
        this.yPixels = yPixels;
    }

    public float getPrinterXYRes() {
        return printerXYRes;
    }

    public void setPrinterXYRes(float printerXYRes) {
        this.printerXYRes = printerXYRes;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public double getSsjXPixels() {
        return ssjXPixels;
    }

    public void setSsjXPixels(double ssjXPixels) {
        this.ssjXPixels = ssjXPixels;
    }

    public double getSsjYPixels() {
        return ssjYPixels;
    }

    public void setSsjYPixels(double ssjYPixels) {
        this.ssjYPixels = ssjYPixels;
    }

    public float getSsjRes() {
        return ssjRes;
    }

    public void setSsjRes(float ssjRes) {
        this.ssjRes = ssjRes;
    }

    public void setSSJFileSettings(double xPixels, double yPixels, float ssjRes) {
        setSsjXPixels(xPixels);
        setSsjYPixels(yPixels);
        setSsjRes(ssjRes);
    }

    public int getAntiAliasPasses() {
        return antiAliasPasses;
    }

    public void setAntiAliasPasses(int antiAliasPasses) {

        this.antiAliasPasses = antiAliasPasses;
    }

    public float getResScale() {

        return ((float) ssjRes / (float) printerXYRes);
    }

    public void setDefaults() {
        setPrinterName("DIY");
        setPrinterProps(1920, 1080, 100f);
        setSSJProps(1280, 800, 100f);

    }

    public boolean getAutoPlaySlideShow() {
        return autoPlay;
    }

    public String getLastOpenLocation() {
        return lastOpenLocation;
    }

    public void setLastOpenLocation(String lastOpenLocation) {
        this.lastOpenLocation = lastOpenLocation;
    }

    public String getLastSaveLocation() {
        return lastSaveLocation;
    }

    public void setLastSaveLocation(String lastSaveLocation) {
        this.lastSaveLocation = lastSaveLocation;
    }

    public double getSSJWidthInMM() {
        Double width = ((double) getSsjRes() * getSsjXPixels()) / 1000;
        //System.out.println("Width in mm " + width);

        return width;

    }

    public boolean getRealisticScaling() {
        return realisticScaling;
    }

    public void setRealisticScaling(boolean realisticScaling) {
        this.realisticScaling = realisticScaling;

    }


}

