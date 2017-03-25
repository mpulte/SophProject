package com.discordbot.gui;

import com.discordbot.util.SettingsManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.ResourceBundle;

public class SettingsController implements FXMLController {

    public static final String SAVE_FOLDER_SETTING = "save_folder";

    @FXML
    public TextField saveFolderField;
    @FXML
    public Button saveFolderButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Path settingsFolder;
        try {
            settingsFolder = Paths.get(SettingsManager.getString(SAVE_FOLDER_SETTING));
            if (!Files.isDirectory(settingsFolder)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Settings Folder Not Found");
                alert.setHeaderText("Unable To Find Settings Folder");
                alert.setContentText("Settings folder was set to " + settingsFolder.toString()
                        + ".\nDefaulting to " + System.getProperty("user.home"));

                settingsFolder = Paths.get(System.getProperty("user.home"));
            }
        } catch (InvalidKeyException e) {
            settingsFolder = Paths.get(System.getProperty("user.home"));
            SettingsManager.setString(SAVE_FOLDER_SETTING, settingsFolder.toString());
        }
        saveFolderField.setText(settingsFolder.toString());
        saveFolderField.setOnAction(e -> saveSettings());
    }

    private void saveSettings() {
        SettingsManager.setString(SAVE_FOLDER_SETTING, saveFolderField.toString());
    }

    @Override
    public void stop() {
        saveSettings();
    }

    @Override
    public void setResizeListener(ResizeListener listener) {
    }

    public void handleSaveFolderButton() {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.showOpenDialog(StageHandler.getInstance().getStages()).get(0);
    }
}
