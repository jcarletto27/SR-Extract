package com.jcarletto.sprintrayextractor;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by user on 5/6/2017.
 */
public class Printer {
    final String PRINTER_NAME = "Printer_Name";
    final String XY_RES = "XYRes";
    final String X_PIXELS = "xPixels";
    final String Y_PIXELS = "yPixels";
    final String SSJ_XY_RES = "SSJ_XY_RES";
    final String ANTI_ALIAS_PASSES = "ANTI_ALIAS_PASSES";


    private int antiAliasPasses = 0;

    private String printerName = "diy";
    private float printerXYRes = 100f;
    private double xPixels = 1920d;
    private double yPixels = 1080d;

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


    public List<String[]> getPrinterSettings() {
        List<String[]> list = new ArrayList<>();
        list.add(new String[]{PRINTER_NAME, getPrinterName()});
        list.add(new String[]{XY_RES, String.valueOf(getPrinterXYRes())});
        list.add(new String[]{X_PIXELS, String.valueOf(getxPixels())});
        list.add(new String[]{Y_PIXELS, String.valueOf(getyPixels())});
        list.add(new String[]{SSJ_XY_RES, String.valueOf(getSsjRes())});
        list.add(new String[]{ANTI_ALIAS_PASSES, String.valueOf(getAntiAliasPasses())});
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
                case ANTI_ALIAS_PASSES:
                    setAntiAliasPasses(Integer.parseInt(s[1]));
                    break;
            }
        }
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

    public int newImageDimension(int currentDimension, float currentRes, float newRes) {
        float resScale = currentRes / newRes;
        //System.out.println("Converting to " + newRes + " micron from" + currentRes);
        int newDim = Math.round((float) currentDimension * resScale);
        return newDim;
    }

    public int newImageDimension(float currentRes, double currentDimension) {
        float resScale = currentRes / printerXYRes;
        int ret = Math.round((float) currentDimension * resScale);
        return ret;
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


    public float resScale() {

        return (ssjRes / printerXYRes);
    }
}

