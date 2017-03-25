package com.discordbot.gui;

import com.discordbot.util.SettingsManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.ResourceBundle;

public class SettingsController implements FXMLController {

    public static final String RESOURCE_FOLDER_SETTING = "resource_folder";

    @FXML
    public TextField resourceFolderField;
    @FXML
    public Button browseResourceFolderButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            Path settingsFolder;
            try {
                settingsFolder = Paths.get(SettingsManager.getString(RESOURCE_FOLDER_SETTING));
                if (!Files.isDirectory(settingsFolder)) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Resource Folder Not Found");
                    alert.setHeaderText("Unable To Find Resource Folder");
                    alert.setContentText("Resource folder was set to " + settingsFolder.toString()
                            + "\nDefaulting to " + System.getProperty("user.home"));
                    alert.showAndWait();

                    settingsFolder = Paths.get(System.getProperty("user.home"));
                }
            } catch (InvalidKeyException e) {
                settingsFolder = Paths.get(System.getProperty("user.home"));
                SettingsManager.setString(RESOURCE_FOLDER_SETTING, settingsFolder.toString());
            }
            resourceFolderField.setText(settingsFolder.toString());
            resourceFolderField.setOnAction(e -> saveSettings());
        }).run();
    }

    @Override
    public void stop() {
        saveSettings();
    }

    private void saveSettings() {
        SettingsManager.setString(RESOURCE_FOLDER_SETTING, resourceFolderField.getText());
    }

    @FXML
    public void handleSaveFolderButton() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Open Resource Folder");

        Path oldFolder = Paths.get(resourceFolderField.getText());
        if (Files.isDirectory(oldFolder)) {
            directoryChooser.setInitialDirectory(new File(resourceFolderField.getText()));
        } else {
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        }

        File directory = directoryChooser.showDialog(StageHandler.getInstance().getStages("Settings").get(0));
        if (directory != null) {
            resourceFolderField.setText(directory.getAbsolutePath());
        }
    }

    @Override
    public void setResizeListener(ResizeListener listener) {
    }
}
