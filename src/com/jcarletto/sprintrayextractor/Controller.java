package com.jcarletto.sprintrayextractor;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private MenuItem menu_item_Open;

    @FXML
    private MenuItem menu_item_Export_to_Folder;

    @FXML
    private MenuItem menu_item_Export_Zip;

    @FXML
    private MenuItem menu_item_Quit;

    @FXML
    private MenuItem menu_item_SaveSettings;

    @FXML
    private Pagination pagination;

    @FXML
    private TextField text_view_printer_name;

    @FXML
    private TextField text_view_x_pixels;

    @FXML
    private TextField text_view_y_pixels;

    @FXML
    private TextField text_view_resolution;

    private PageModel model;
    private SSJ_Reader ssj_reader;
    private Printer printer;
    private SettingsHelper settingsHelper = new SettingsHelper();
    private ImageHelper imageHelper = new ImageHelper();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        printer = new Printer();


        initPrinterSettings();


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

        menu_item_Export_to_Folder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ssj_reader.getSsjFile() != null) {
                    System.out.println("Total number of records : " + ssj_reader.getPngBytes().size());
                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
                    directoryChooser.setTitle("Please choose a directory for export");
                    File folder = directoryChooser.showDialog(null);
                    Image tempImage = model.getImage(pagination.getCurrentPageIndex());
                    if (folder != null) {
                        processImages(printer, tempImage, folder);
                    }

                }
            }
        });
        menu_item_SaveSettings.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                printer.setPrinterName(text_view_printer_name.getText());
                printer.setxPixels(Double.parseDouble(text_view_x_pixels.getText()));
                printer.setyPixels(Double.parseDouble(text_view_y_pixels.getText()));
                printer.setPrinterXYRes(Float.parseFloat(text_view_resolution.getText()));
                settingsHelper.writeProps(printer.getPrinterSettings());
            }
        });


    }

    private void processImages(Printer printer, Image image, File folder) {
        for (int x = 0; x < ssj_reader.getPngBytes().size()-1; x++) {
            byte[] b = ssj_reader.getPngBytes().get(x);
            String fileName = String.format("%08d", x + 1) + ".png";
            int padding = imageHelper.getPaddingValue(image.getHeight(), printer.getyPixels());
            //System.out.println("Padding Value : " + padding);
            Path path = Paths.get(folder.getPath(), fileName);
            File pngFile = path.toFile();
            Double xPixels = printer.getxPixels();
            Double yPixels = printer.getyPixels();

            try {
                if (!pngFile.exists()) {
                    imageHelper.process(ssj_reader.getStreamFromIndex(b), xPixels.intValue(), yPixels.intValue(), padding, pngFile);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Slice # " + x + " could not be exported. Something went wrong!");
                e.printStackTrace();
            }

        }
    }

    private void setPaginationFactory(List<byte[]> pngBytes) {
        model = new PageModel(pngBytes);


        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override
            public Node call(Integer param) {
                ImageView view = new ImageView(model.getImage(param));
                view.setPreserveRatio(true);
                view.setFitWidth(pagination.getWidth());


     /*           float currentRes = imageHelper.getXYRes((float) view.getImage().getWidth(), (float) 96);
                double currentDimension = view.getImage().getWidth();
                double newDim = printer.newImageDimension(currentRes, currentDimension);
               */
                view.setFitHeight(pagination.getHeight());


                return view;
            }
        });
        pagination.setPageCount(model.numPages());
    }


    public void initPrinterSettings() {
        printer.setPrinterSettings(settingsHelper.readProps());
        if (printer.getPrinterName() != "" || printer.getPrinterName() != null) {


            text_view_printer_name.setText(printer.getPrinterName().toString());
            text_view_resolution.setText(String.valueOf(printer.getPrinterXYRes()));
            text_view_x_pixels.setText(String.valueOf(printer.getxPixels()));
            text_view_y_pixels.setText(String.valueOf(printer.getyPixels()));
        } else {
            //defaults
            printer.setxPixels(1920d);
            printer.setyPixels(1080d);
            printer.setPrinterName("DIY");
            printer.setPrinterXYRes(100.0f);
        }

    }

    private void openFile(File file) {
        ssj_reader = new SSJ_Reader(file);
    }
}
