package com.javarush.cryptoanalyzer.korovnichenko.ui;

import com.javarush.cryptoanalyzer.korovnichenko.app.Application;
import com.javarush.cryptoanalyzer.korovnichenko.controller.MainController;
import com.javarush.cryptoanalyzer.korovnichenko.exception.ApplicationException;
import com.javarush.cryptoanalyzer.korovnichenko.model.Result;
import com.javarush.cryptoanalyzer.korovnichenko.repository.ResultCode;
import com.javarush.cryptoanalyzer.korovnichenko.services.io.FileService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class GUI extends javafx.application.Application implements UI {
    public static final int LINES_FOR_VIEW = 100;
    public static final String FILE_FOR_STATISTICS = "static-analysis.txt";
    private final TextArea inputPreview = new TextArea();
    private final TextArea outputPreview = new TextArea();
    private final ComboBox<String> cipherBox = new ComboBox<>();
    private final TextField keyField = new TextField();
    private final TextArea resultField = new TextArea("Here you will see the result of operation");
    private final Label selectedFileLabel = new Label("No file selected");
    private final Label progressLabel = new Label("Encrypting");


    private final RadioButton autoDetect = new RadioButton("Auto-detect");
    private final RadioButton latin = new RadioButton("Latin");
    private final RadioButton ukrainian = new RadioButton("Ukrainian");
    private final CheckBox checkBoxPunct = new CheckBox("Alphabet with punctuation");
    private final ToggleGroup alphabetGroup = new ToggleGroup();
    private final ProgressBar progressBar = new ProgressBar(0);

    public FileService fileService = new FileService();

    private File inputFile;
    private String selectedMode = "ENCRYPT"; // по умолчанию
    public static Result result;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Cryptoanalyzer");

        // Sidebar (left menu)
        VBox menu = new VBox(10);
        menu.setPadding(new Insets(10));
        menu.setStyle("-fx-background-color: #f4f4f4;");

        ToggleGroup menuGroup = new ToggleGroup();
        RadioButton encryptBtn = new RadioButton("Encrypt");
        RadioButton decryptBtn = new RadioButton("Decrypt");
        RadioButton bruteForceBtn = new RadioButton("Brute Force");

        encryptBtn.setToggleGroup(menuGroup);
        decryptBtn.setToggleGroup(menuGroup);
        bruteForceBtn.setToggleGroup(menuGroup);
        encryptBtn.setSelected(true);

        encryptBtn.setOnAction(e -> {
            selectedMode = "ENCRYPT";
            progressLabel.setText("Encrypting");
        });
        decryptBtn.setOnAction(e -> {
            selectedMode = "DECRYPT";
            progressLabel.setText("Decrypting");
        });
        bruteForceBtn.setOnAction(e -> {
            selectedMode = "BRUTE_FORCE";
            keyField.clear();
            progressLabel.setText("Brute force analyzing");
        });

        menu.getChildren().addAll(encryptBtn, decryptBtn, bruteForceBtn);

        // Main form area
        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setHgap(6);
        form.setVgap(8);
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col2.setHgrow(Priority.ALWAYS);
        form.getColumnConstraints().addAll(col1, col2);

        selectedFileLabel.setStyle("-fx-font-weight: 500;");

        // File browse button
        Button fileButton = new Button("Browse");
        fileButton.setOnAction(e -> {
            resultField.clear();
            FileChooser fileChooser = new FileChooser();
            inputFile = fileChooser.showOpenDialog(stage);
            if (inputFile != null) {
                outputPreview.clear();
                selectedFileLabel.setText(inputFile.getAbsolutePath());
                inputPreview.setText(fileService.readFirstLines(inputFile, LINES_FOR_VIEW));

                selectedFileLabel.setText(inputFile.getAbsolutePath());
            }
        });

        // Cipher type
        cipherBox.getItems().addAll("Caesar", "Vigenere");
        cipherBox.setValue("Caesar");

        // Input text area
        inputPreview.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");
        inputPreview.setPrefRowCount(20);
        inputPreview.setWrapText(true);
        GridPane.setVgrow(inputPreview, Priority.ALWAYS);

        // Output text area
        outputPreview.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");
        outputPreview.setWrapText(true);
        GridPane.setVgrow(outputPreview, Priority.ALWAYS);

        // Alphabet radio buttons
        autoDetect.setToggleGroup(alphabetGroup);
        latin.setToggleGroup(alphabetGroup);
        ukrainian.setToggleGroup(alphabetGroup);
        autoDetect.setSelected(true);

        //HBox alphabetOptions = new HBox(5, autoDetect, latin, ukrainian);
        FlowPane alphabetOptions = new FlowPane(Orientation.HORIZONTAL, 10, 5);
        alphabetOptions.getChildren().addAll(autoDetect, latin, ukrainian);
        alphabetOptions.setHgap(6);
        alphabetOptions.setVgap(3);

        // Run button
        Button runButton = new Button("Run");
        runButton.setPrefWidth(150);
        GridPane.setHalignment(runButton, HPos.RIGHT);

        VBox keyBox = new VBox(3); // spacing 3 px
        Label keyLabel = new Label("Key:");
        keyLabel.setStyle("-fx-font-weight: bold;");
        keyBox.getChildren().addAll(keyLabel, keyField);

        VBox alphabetBox = new VBox(3);
        Label alphabetLabel = new Label("Alphabet:");
        alphabetLabel.setStyle("-fx-font-weight: bold;");
        alphabetBox.getChildren().addAll(alphabetLabel, alphabetOptions, checkBoxPunct);

        VBox progressBox = new VBox(3);
        progressLabel.setStyle("-fx-font-weight: bold;");
        progressBox.getChildren().add(progressLabel);

        VBox combinedBox = new VBox(20); // spacing между key и alphabet секциями
        combinedBox.getChildren().addAll(keyBox, alphabetBox, progressBox);
        GridPane.setValignment(keyField, VPos.TOP);
        GridPane.setValignment(alphabetOptions, VPos.CENTER);
        GridPane.setValignment(progressLabel, VPos.BOTTOM);

        Timeline animation = new Timeline(
                new KeyFrame(Duration.ZERO, new KeyValue(progressBar.progressProperty(), 0)),
                new KeyFrame(Duration.seconds(2), new KeyValue(progressBar.progressProperty(), 1))
        );
        animation.setCycleCount(Animation.INDEFINITE);

        runButton.setOnAction(e -> {
            if (selectedFileLabel.getText().equals("No file selected")) {
                outputPreview.setText("You should choose input file for processing!");
            } else if (!selectedMode.equals("BRUTE_FORCE") && keyField.getText().isEmpty()) {
                outputPreview.setText("You should enter the key field!");
            } else {
                progressBar.setVisible(true);
                animation.play();
                new Thread(() -> {
                    try {
                        outputPreview.clear();
                        Application app = new Application(new MainController(this));
                        result = app.run();
                        Platform.runLater(() -> {
                            try {
                                StringBuilder builder = new StringBuilder()
                                        .append("Processing mode: ").append(selectedMode).append("\n")
                                        .append("Result filename: ").append(result.getUsedCipher().getFileOutputPath()).append("\n")
                                        .append("Selected cipher algorithm: ").append(result.getUsedCipher().getAlgorithm().name()).append("\n")
                                        .append("Selected alphabet: ").append(result.getUsedCipher().getAlphabetType()).append("\n")
                                        .append("Cipher key: ").append(result.getUsedCipher().getKey()).append("\n")
                                        .append("Result of operation: ").append(result.getResultCode().name()).append("\n");

                                if (result.getResultCode() == ResultCode.ERROR) {
                                    ApplicationException ex = result.getApplicationException();
                                    String message = (ex != null && ex.getMessage() != null)
                                            ? ex.getMessage()
                                            : "Unknown error occurred.";
                                    builder.append(message);
                                    outputPreview.setText(builder.toString());
                                }

                                resultField.setText(builder.toString());

                                animation.stop();
                                progressBar.setVisible(false);
                                printResult(result);

                            } catch (Exception fxEx) {
                                outputPreview.setText("Помилка у JavaFX-потоці: " + fxEx.getMessage());
                                animation.stop();
                                progressBar.setVisible(false);
                            }
                        });
                        } catch (Exception ex) {
                        Platform.runLater(() -> {
                            outputPreview.setText("Error: " + ex.getMessage());
                            animation.stop();
                            progressBar.setVisible(false);
                        });
                    }
                }).start();
            };
        });

        // Layout components
        Label filePathLabel = new Label("File Path:");
        filePathLabel.setStyle("-fx-font-weight: bold;");
        form.add(filePathLabel,0, 0);
        form.add(fileButton, 1, 0);
        form.add(selectedFileLabel,0, 1, 2, 1);

        Label cipherLabel = new Label("Cipher:");
        cipherLabel.setStyle("-fx-font-weight: bold;");
        form.add(cipherLabel,0, 2);
        form.add(cipherBox,1,2);

        form.add(new Label("INPUT FILE PREVIEW:"), 0, 3);
        form.add(new Label("OUTPUT FILE PREVIEW:"), 1, 3);

        form.add(inputPreview,0,4);
        form.add(outputPreview,1,4);

        form.add(combinedBox, 0, 6);
        Label resultLabel = new Label("Result:");
        resultLabel.setStyle("-fx-font-weight: bold;");
        form.add(resultLabel,1,5);
        form.add(resultField,1,6);

        form.add(runButton,1,7);
        progressBar.setPrefWidth(500);
        progressBar.setVisible(false);
        form.add(progressBar, 0, 7);
        // Final layout
        BorderPane root = new BorderPane();
        root.setLeft(menu);
        root.setCenter(form);

        Scene scene = new Scene(root, 800, 600);
        scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public String[] getUIParameters() {
        String alphabet = "auto";
        if (latin.isSelected()) {
            if (checkBoxPunct.isSelected()) {
                alphabet = "ENG_WITH_PUNCT";
            } else {
                alphabet = "ENG";
            }
        } else if (ukrainian.isSelected()) {
            if (checkBoxPunct.isSelected()) {
                alphabet = "UKR_WITH_PUNCT";
            } else {
                alphabet = "UKR";
            }
        }

        // Brute-force не требует ключа
        if (selectedMode.equalsIgnoreCase("BRUTE_FORCE")) {
            return new String[] {
                    selectedMode,
                    inputFile != null ? inputFile.getAbsolutePath() : "",
                    FILE_FOR_STATISTICS,
                    cipherBox.getValue(),
                    alphabet
                    //TODO add the keyLengthField and return value
            };
        }

        return new String[] {
                selectedMode,
                inputFile != null ? inputFile.getAbsolutePath() : "",
                keyField.getText(),
                cipherBox.getValue(),
                alphabet
        };
    }

    @Override
    public void printResult(Result result) {
        File resultFile = new File(result.getUsedCipher().getFileOutputPath());
        outputPreview.setText(fileService.readFirstLines(resultFile, LINES_FOR_VIEW)
//                + "\n\n"
//                + fileService.readFirstLines(new File("bruteforce_best_keys.txt"), LINES_FOR_VIEW)
        );
    }

    public static void launchUI(String[] args) {
        launch(args);
    }

}
