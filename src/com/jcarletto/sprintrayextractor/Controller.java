package com.jcarletto.sprintrayextractor;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private MenuItem menu_item_Export_Zip;
    @FXML
    private Pagination pagination;
    @FXML
    private MenuItem menu_item_Export_to_Folder;
    @FXML
    private MenuItem menu_item_Quit;
    @FXML
    private MenuItem menu_item_Open;

    private SSJ_Reader ssj_reader;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        menu_item_Quit.setOnAction(event -> Platform.exit());

        menu_item_Open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SSJ File", "*.ssj"));
                fileChooser.setTitle("Open ssj file");
                fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
                File file = fileChooser.showOpenDialog(null);
                if (file != null) {
                    openFile(file);
                    setPaginationFactory(ssj_reader.getPngBytes());
                }
            }
        });


    }

    private void setPaginationFactory(List<byte[]> pngBytes) {
        PageModel model = new PageModel(pngBytes);

        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer param) {
                ImageView view = new ImageView(model.getImage(param));
                view.setPreserveRatio(true);
                view.setFitWidth(pagination.getWidth());
                view.setFitHeight(pagination.getHeight());
                return view;
            }
        });
        pagination.setPageCount(model.numPages());
    }

    private void openFile(File file) {
        ssj_reader = new SSJ_Reader(file);
    }
}
