package com.jcarletto.sprintrayextractor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    protected static Stage mainStage;
    private static Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    public static Stage getStage() {
        return mainStage;
    }

    public void setStage(Stage stage) {
        mainStage = stage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String resourcePath = "/extractor-main.fxml";
        URL location = getClass().getResource(resourcePath);
        FXMLLoader loader = new FXMLLoader(location);
        Scene scene = new Scene(loader.load(), 1000, 800);
        List<Image> icons = new ArrayList<>();
        icons.add(new Image(getClass().getResourceAsStream("/Extract-object-icon.png")));
        primaryStage.setTitle("SprintRay Extractor");
        primaryStage.getIcons().setAll(icons);
        primaryStage.setScene(scene);
        setStage(primaryStage);
        primaryStage.show();
    }
}
