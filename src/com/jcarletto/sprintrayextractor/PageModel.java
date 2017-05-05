package com.jcarletto.sprintrayextractor;

import javafx.scene.image.Image;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created by jcarlett on 5/5/2017.
 */
public class PageModel {

    List<byte[]> pngList;

    public PageModel(List<byte[]> pngs) {
        setPngList(pngs);


    }

    public void setPngList(List<byte[]> pngList) {
        this.pngList = pngList;
    }

    public Image getImage(int pageNumeber) {
        //pngList.get(pageNumeber);
        ByteArrayInputStream stream = new ByteArrayInputStream(pngList.get(pageNumeber));
        Image image = new Image(stream);

        return image;
    }

    public int numPages() {
        return pngList.size()-1;
    }
}
