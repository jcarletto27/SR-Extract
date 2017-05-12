package com.jcarletto.sprintrayextractor;

import com.jcarletto.sprintrayextractor.Util.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


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
    private CheckMenuItem check_menu_item_realistic_scaling;
    @FXML
    private TextField text_view_x_pixels;
    @FXML
    private TextField text_view_y_pixels;
    @FXML
    private MenuItem menu_item_run_slide_show;
    @FXML
    private Slider slider_slice_picker;
    @FXML
    private Label label_progress;
    @FXML
    private ProgressBar progress_bar;
    @FXML
    private Button button_cancel;
    @FXML
    private TextField text_view_resolution;
    @FXML
    private TextField text_view_file_x_pixels;
    @FXML
    private TextField text_view_file_y_pixels;
    @FXML
    private TextField text_view_file_resolution;
    @FXML
    private CheckMenuItem check_menu_item_play_slideshow_automatically;
    @FXML
    private VBox vbox_root;
    @FXML
    private CheckMenuItem check_menu_item_include_json;


    private boolean autoPlaySlideShow = true;

    private Task slideShow;
    private Task zipExport;
    private Task folderExport;

    private PageModel model;
    private SSJ_Reader ssj_reader;
    private Zip_Reader zip_reader;
    private Printer printer;

    private SettingsHelper settingsHelper = new SettingsHelper();
    private ImageHelper imageHelper = new ImageHelper();
    private ZipOutputStream zipOutputStream;

    private Info_Writer info_writer = new Info_Writer();
    private boolean isFileLoaded = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        vbox_root.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {

                    event.acceptTransferModes(TransferMode.ANY);

                } else {
                    event.consume();
                }
            }
        });
        vbox_root.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasFiles()) {
                    success = true;
                    String filePath = db.getFiles().get(0).getAbsolutePath().toString();
                    openFile(new File(filePath));
                }
                event.setDropCompleted(success);
                event.consume();
            }
        });


        menu_item_Export_to_Folder.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCodeCombination.CONTROL_DOWN, KeyCodeCombination.SHIFT_DOWN));
        menu_item_Export_Zip.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN));
        check_menu_item_realistic_scaling.setAccelerator(new KeyCodeCombination(KeyCode.F6));
        menu_item_Open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCodeCombination.CONTROL_DOWN));
        menu_item_Quit.setAccelerator(new KeyCodeCombination(KeyCode.Q, KeyCodeCombination.CONTROL_DOWN));
        menu_item_run_slide_show.setAccelerator(new KeyCodeCombination(KeyCode.F5));
        menu_item_SaveSettings.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN));

        check_menu_item_include_json.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                printer.setIncludeJson(newValue);
                settingsHelper.writeProps(printer.getPrinterSettings());
            }
        });


        slider_slice_picker.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                pagination.setCurrentPageIndex(newValue.intValue());
            }
        });

        check_menu_item_realistic_scaling.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                printer.setRealisticScaling(newValue);

                settingsHelper.writeProps(printer.getPrinterSettings());
                if (isFileLoaded) {
                    if (ssj_reader.getSsjFile() != null) {
                        setPaginationFactory(ssj_reader.getPngBytes());
                    } else {
                        setPaginationFactory(zip_reader.getPngBytes());
                    }
                }
            }
        });

        check_menu_item_play_slideshow_automatically.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                printer.setAutoPlay(newValue);

                settingsHelper.writeProps(printer.getPrinterSettings());
                if (isFileLoaded && newValue) {
                    slideShow = createSlideShowWorker(pagination.getPageCount() - 1);
                    slideShow.messageProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                            pagination.setCurrentPageIndex(Integer.parseInt(newValue));
                        }
                    });
                    new Thread(slideShow).start();
                }
            }
        });

        pagination.currentPageIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                slider_slice_picker.valueProperty().setValue(newValue);
            }
        });

        text_view_resolution.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                printer.setPrinterXYRes(Float.parseFloat(newValue));

            }
        });
        text_view_file_resolution.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                printer.setSsjRes(Float.parseFloat(newValue));
            }
        });

        printer = new Printer();

        check_menu_item_play_slideshow_automatically.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                autoPlaySlideShow = newValue;
            }
        });

        fileNotLoaded();

        button_cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    zipExport.cancel();

                } catch (Exception e) {

                }
                try {
                    folderExport.cancel();
                } catch (Exception e) {

                }
                pagination.setCurrentPageIndex(0);
            }
        });


        menu_item_Export_Zip.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                FileChooser saveFileDialog = new FileChooser();
                saveFileDialog.setInitialDirectory(new File(printer.getLastSaveLocation()));
                saveFileDialog.setTitle("Save this Export to .zip");
                saveFileDialog.getExtensionFilters().add(new FileChooser.ExtensionFilter("Zip Files", "*.zip"));
                File file = saveFileDialog.showSaveDialog(null);
                if (file != null && ssj_reader.getSsjFile() != null) {
                    if (file.isDirectory()) {
                        printer.setLastSaveLocation(file.getAbsolutePath());

                    } else {
                        printer.setLastSaveLocation(file.getParent());
                    }
                    try {

                        zipOutputStream = new ZipOutputStream(new FileOutputStream(file));

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    zipExport = createExportZipWorker();

                    progress_bar.progressProperty().unbind();
                    progress_bar.progressProperty().bind(zipExport.progressProperty());
                    label_progress.textProperty().bind(zipExport.messageProperty());


                    zipExport.setOnFailed(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            button_cancel.setVisible(false);
                            button_cancel.setDisable(true);
                            progress_bar.progressProperty().unbind();
                            progress_bar.progressProperty().setValue(0);
                            label_progress.textProperty().unbind();
                            label_progress.setText("!");

                        }
                    });
                    zipExport.setOnCancelled(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            button_cancel.setVisible(false);
                            button_cancel.setDisable(true);
                            progress_bar.progressProperty().unbind();
                            progress_bar.progressProperty().setValue(0);
                            label_progress.textProperty().unbind();
                            label_progress.setText("!");

                        }
                    });


                    zipExport.setOnRunning(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            button_cancel.setDisable(false);
                            button_cancel.setVisible(true);
                        }
                    });
                    zipExport.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                        @Override
                        public void handle(WorkerStateEvent event) {
                            button_cancel.setVisible(false);
                            button_cancel.setDisable(true);
                            progress_bar.progressProperty().unbind();
                            progress_bar.progressProperty().setValue(0);
                            label_progress.textProperty().unbind();
                            label_progress.setText("");


                        }
                    });


                    zipExport.messageProperty().addListener(new ChangeListener<String>() {
                        @Override
                        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                            pagination.setCurrentPageIndex(Integer.parseInt(newValue.split("/")[0]));

                        }
                    });
                    Thread worker = new Thread(zipExport);
                    worker.start();

                }
            }
        });

        menu_item_run_slide_show.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {


                slideShow = createSlideShowWorker(pagination.getPageCount() - 1);
                slideShow.messageProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                        pagination.setCurrentPageIndex(Integer.parseInt(newValue));
                    }
                });
                new Thread(slideShow).start();

            }
        });

        menu_item_Export_Zip.setDisable(true);


        initPrinterSettings();


        menu_item_Quit.setOnAction(event -> Platform.exit());

        menu_item_Open.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                FileChooser fileChooser = new FileChooser();

                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SSJ File, Zip File", "*.ssj", "*.zip"));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("SSJ File", "*.ssj"));
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("ZIP file", "*.zip"));
                fileChooser.setTitle("Open ssj file");
                fileChooser.setInitialDirectory(new File(printer.getLastOpenLocation()));
                File file = fileChooser.showOpenDialog(null);

                openFile(file);
            }
        });

        menu_item_Export_to_Folder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (ssj_reader.getSsjFile() != null) {


                    DirectoryChooser directoryChooser = new DirectoryChooser();
                    directoryChooser.setInitialDirectory(new File(printer.getLastSaveLocation()));
                    directoryChooser.setTitle("Please choose a directory for export");
                    File folder = directoryChooser.showDialog(null);
                    Image tempImage = model.getImage(pagination.getCurrentPageIndex());
                    if (folder != null) {

                        if (folder.isDirectory()) {
                            printer.setLastSaveLocation(folder.getAbsolutePath());
                        } else {
                            printer.setLastSaveLocation(folder.getParent());
                        }
                        folderExport = createExportFolderWorker(printer, tempImage, folder, ssj_reader.getPngBytes().size());
                        progress_bar.progressProperty().unbind();
                        progress_bar.progressProperty().bind(folderExport.progressProperty());
                        label_progress.textProperty().unbind();
                        label_progress.textProperty().bind(folderExport.messageProperty());


                        folderExport.setOnFailed(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent event) {
                                button_cancel.setVisible(false);
                                button_cancel.setDisable(true);
                                progress_bar.progressProperty().unbind();
                                progress_bar.progressProperty().setValue(0);
                                label_progress.textProperty().unbind();
                                label_progress.setText("");

                            }
                        });
                        folderExport.setOnCancelled(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent event) {
                                button_cancel.setVisible(false);
                                button_cancel.setDisable(true);
                                progress_bar.progressProperty().unbind();
                                progress_bar.progressProperty().setValue(0);
                                label_progress.textProperty().unbind();
                                label_progress.setText("");

                            }
                        });


                        folderExport.setOnRunning(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent event) {
                                button_cancel.setDisable(false);
                                button_cancel.setVisible(true);
                            }
                        });
                        folderExport.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent event) {
                                button_cancel.setVisible(false);
                                button_cancel.setDisable(true);
                                progress_bar.progressProperty().unbind();
                                progress_bar.progressProperty().setValue(0);
                                label_progress.textProperty().unbind();
                                label_progress.setText("");


                            }
                        });


                        folderExport.messageProperty().addListener(new ChangeListener<String>() {
                            @Override
                            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                pagination.setCurrentPageIndex(Integer.parseInt(newValue.split("/")[0]));

                            }
                        });
                        settingsHelper.writeProps(printer.getPrinterSettings());
                        Thread worker = new Thread(folderExport);
                        worker.start();

                    }

                }
            }
        });
        menu_item_SaveSettings.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                printer.setxPixels(Double.parseDouble(text_view_x_pixels.getText()));
                printer.setyPixels(Double.parseDouble(text_view_y_pixels.getText()));
                printer.setPrinterXYRes(Float.parseFloat(text_view_resolution.getText()));
                printer.setSSJProps(Double.parseDouble(text_view_file_x_pixels.getText()), Double.parseDouble(text_view_file_y_pixels.getText()), Float.parseFloat(text_view_file_resolution.getText()));
                printer.setAutoPlay(autoPlaySlideShow);
                printer.setIncludeJson(check_menu_item_include_json.isSelected());
                settingsHelper.writeProps(printer.getPrinterSettings());

                Task updateTask = createStatus(3);
                new Thread(updateTask).start();
                updateTask.progressProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                        label_progress.textProperty().unbind();
                        label_progress.setText(" Saving settings ");
                    }
                });
                updateTask.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        label_progress.textProperty().unbind();
                        label_progress.textProperty().setValue("  ");
                    }
                });

            }
        });


    }

    private void openZip(File file) {
        zip_reader = new Zip_Reader(file);
    }

    private void processImages(Printer printer, Image image, int index, File folder) {

        byte[] b = ssj_reader.getPngBytes().get(index);
        String fileName = String.format("%05d", index + 1) + ".png";
        int padding = imageHelper.getPaddingValue(image.getHeight(), printer.getyPixels());

        Path path = Paths.get(folder.getPath(), fileName);
        File pngFile = path.toFile();
        Double xPixels = printer.getxPixels();
        Double yPixels = printer.getyPixels();

        try {
            if (!pngFile.exists()) {


                imageHelper.processForFolder(ssj_reader.getStreamFromIndex(b), xPixels.intValue(), yPixels.intValue(), printer.getResScale(), padding, printer.getAntiAliasPasses(), pngFile);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Slice # " + index + " could not be exported. Something went wrong!");
            //e.printStackTrace();
        }


    }

    private void setPaginationFactory(List<byte[]> pngBytes) {
        model = new PageModel(pngBytes);


        pagination.setPageFactory(new Callback<Integer, Node>() {
            @Override

            public Node call(Integer param) {
                Double scaledWidth = null;
                Image img = model.getImage(param);
                ImageView view = new ImageView(img);
                view.setPreserveRatio(true);

                if (printer.getRealisticScaling()) {
                    Double scale = imageHelper.scaleImageForScreen(printer.getSsjRes());
                    scaledWidth = img.getWidth() * scale;
                } else {
                    scaledWidth = pagination.getWidth();
                }


                view.setFitWidth(scaledWidth);


                view.setFitHeight(pagination.getHeight());


                return view;
            }
        });
        pagination.setPageCount(model.numPages());
    }


    public void initPrinterSettings() {
        printer.setPrinterSettings(settingsHelper.readProps());
        if (printer.getPrinterName() != "" || printer.getPrinterName() != null) {

            text_view_file_resolution.setText(String.valueOf(printer.getSsjRes()));
            text_view_file_x_pixels.setText(String.valueOf(printer.getSsjXPixels()));
            text_view_file_y_pixels.setText(String.valueOf(printer.getSsjYPixels()));

            check_menu_item_realistic_scaling.setSelected(printer.getRealisticScaling());
            check_menu_item_include_json.setSelected(printer.getIncludeJson());
            check_menu_item_play_slideshow_automatically.setSelected(printer.getAutoPlaySlideShow());
            autoPlaySlideShow = check_menu_item_play_slideshow_automatically.isSelected();

            text_view_resolution.setText(String.valueOf(printer.getPrinterXYRes()));
            text_view_x_pixels.setText(String.valueOf(printer.getxPixels()));
            text_view_y_pixels.setText(String.valueOf(printer.getyPixels()));
        } else {
            //defaults
            printer.setDefaults();
            initPrinterSettings();
        }

    }

    private void fileLoaded() {
        menu_item_Export_to_Folder.setDisable(false);
        menu_item_Export_Zip.setDisable(false);
        menu_item_run_slide_show.setDisable(false);
        slider_slice_picker.setDisable(false);
        settingsHelper.writeProps(printer.getPrinterSettings());
        //check_menu_item_play_slideshow_automatically.setDisable(false);
        isFileLoaded = true;
    }

    private void fileNotLoaded() {
        // check_menu_item_play_slideshow_automatically.setDisable(true);
        isFileLoaded = false;
        slider_slice_picker.setDisable(true);
        button_cancel.setDisable(true);
        button_cancel.setVisible(false);
        menu_item_Export_to_Folder.setDisable(true);
        menu_item_Export_Zip.setDisable(true);
        menu_item_run_slide_show.setDisable(true);
    }

    public Task createSlideShowWorker(final int upperValue) {

        return new Task() {

            @Override
            protected Object call() throws Exception {

                for (int x = 0; x < upperValue; x++) {
                    Thread.sleep(2);
                    updateMessage(String.valueOf(x));
                    updateProgress(x + 1, upperValue);

                }
                Thread.sleep(200);
                updateMessage(String.valueOf(0));
                return true;
            }
        };
    }

    public Task createStatus(final int length) {

        return new Task() {

            @Override
            protected Object call() throws Exception {

                for (int x = 0; x < length; x++) {
                    Thread.sleep(1000);
                    updateProgress(x, length);

                }
                return true;
            }
        };
    }

    public Task createExportFolderWorker(Printer printer, Image image, File file, int pngBytesSize) {

        return new Task() {

            @Override
            protected Object call() throws Exception {

                for (int x = 0; x < pngBytesSize - 1; x++) {
                    if (isCancelled()) {
                        break;
                    }
                    updateProgress(x + 1, pngBytesSize);
                    processImages(printer, image, x, file);
                    updateMessage(String.valueOf(x) + "/" + pngBytesSize);


                }
                return true;
            }
        };
    }

    public Task createExportZipWorker() {

        return new Task() {

            @Override
            protected Object call() throws Exception {

                info_writer = new Info_Writer();

                for (int x = 0; x < ssj_reader.getPngBytes().size() - 1; x++) {
                    if (isCancelled()) {
                        zipOutputStream.close();
                        break;
                    }

                    try {
                        String fileName = String.format("%05d", x + 1) + ".png";
                        zipOutputStream.putNextEntry(new ZipEntry(fileName));
                        ImageHelper helper = new ImageHelper();

                        Double xPixels = printer.getxPixels();
                        Double yPixels = printer.getyPixels();
                        ByteArrayInputStream bis = new ByteArrayInputStream(ssj_reader.getPngBytes().get(x));
                        Image curImage = new Image(bis);
                        int padding = helper.getPaddingValue(curImage.getHeight(), yPixels);

                        if (printer.getIncludeJson()) {
                            int pixels = imageHelper.countPixelsOfAColor(curImage, Color.WHITE);
                            double area = imageHelper.areaOfPixelsInMMSq(pixels, printer.getSsjRes());
                            info_writer.writeInfo(area, pixels);
                        }

                        zipOutputStream.write(helper.processForZip(ssj_reader.getPngBytes().get(x), xPixels.intValue(), yPixels.intValue(), printer.getResScale(), padding, printer.getAntiAliasPasses()));

                        zipOutputStream.closeEntry();


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    updateMessage(String.valueOf(x + 1) + "/" + String.valueOf(ssj_reader.getPngBytes().size() - 1));
                    updateProgress(x + 1, ssj_reader.getPngBytes().size());

                }

                if (printer.getIncludeJson()) {
                    String filename = "info.json";
                    zipOutputStream.putNextEntry(new ZipEntry(filename));
                    zipOutputStream.write(info_writer.getJsonInfo().getBytes());
                    zipOutputStream.closeEntry();
                }

                zipOutputStream.close();

                return true;
            }
        };


    }

    private void openSSJFile(File file) {
        ssj_reader = new SSJ_Reader(file);
    }

    private void openFile(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                printer.setLastOpenLocation(file.getAbsolutePath());
            } else {
                printer.setLastOpenLocation(file.getParent());
            }
            if (FilenameUtils.getExtension(file.getName()).contentEquals("ssj")) {


                Stage primaryStage = Main.getStage();
                primaryStage.setTitle("SprintRay Extractor - " + file.getName());


                openSSJFile(file);
                printer.setSsjRes(Float.parseFloat(ssj_reader.getSsjFileInfo()));
                text_view_file_resolution.textProperty().setValue(String.valueOf(Double.valueOf(ssj_reader.getSsjFileInfo())));
                setPaginationFactory(ssj_reader.getPngBytes());
                fileLoaded();
                slideShow = createSlideShowWorker(pagination.getPageCount() - 1);
                slideShow.messageProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        pagination.setCurrentPageIndex(Integer.parseInt(newValue));
                    }
                });
                pagination.setCurrentPageIndex(0);
                int pages = pagination.getPageCount();
                slider_slice_picker.setMax((double) pages);
                if (pages / 10 > 1) {
                    slider_slice_picker.setShowTickMarks(true);
                    slider_slice_picker.setMajorTickUnit((double) (pages / 10));
                } else {
                    slider_slice_picker.setShowTickMarks(false);
                }


                if (autoPlaySlideShow) {
                    new Thread(slideShow).start();
                }
            } else if (FilenameUtils.getExtension(file.getName()).contentEquals("zip")) {

                Stage primaryStage = Main.getStage();
                primaryStage.setTitle("SprintRay Extractor - " + file.getName());


                openZip(file);

                setPaginationFactory(zip_reader.getPngBytes());

                fileLoaded();

                slideShow = createSlideShowWorker(pagination.getPageCount() - 1);

                slideShow.messageProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        pagination.setCurrentPageIndex(Integer.parseInt(newValue));
                    }
                });

                pagination.setCurrentPageIndex(0);
                int pages = pagination.getPageCount();
                slider_slice_picker.setMax((double) pages);
                if (pages / 10 > 1) {
                    slider_slice_picker.setShowTickMarks(true);
                    slider_slice_picker.setMajorTickUnit((double) (pages / 10));
                } else {
                    slider_slice_picker.setShowTickMarks(false);
                }
                if (autoPlaySlideShow) {
                    new Thread(slideShow).start();
                }
            }
        }
        settingsHelper.writeProps(printer.getPrinterSettings());


    }


}
