package com.discordbot.gui;

import com.discordbot.util.FileUtil;
import com.discordbot.util.SettingsManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class SettingsController implements FXMLController {

    @FXML
    public TextField resourceFolderField;
    @FXML
    public Button browseResourceFolderButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            Path path = FileUtil.getResourceFolder();
            resourceFolderField.setText(path.toString());
            resourceFolderField.setOnAction(e -> saveSettings());
        }).run();
    }

    @Override
    public void stop() {
        saveSettings();
    }

    private void saveSettings() {
        SettingsManager.setString(FileUtil.RESOURCE_FOLDER_SETTING, resourceFolderField.getText());
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
