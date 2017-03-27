package com.discordbot.gui;

import com.discordbot.util.IOUtils;
import com.discordbot.util.SettingHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

/**
 * A {@link FXMLController} implementation for controlling SettingsPane.fxml.
 *
 * @see FXMLController
 */
public class SettingsController implements FXMLController {

    @FXML
    private TextField resourceFolderField;

    /**
     * Initializes the SettingsController.
     *
     * @param location  The location used to resolve relative paths for the root object, or <tt>null</tt> if the
     *                  location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if the root object was not
     *                  localized.
     * @see javafx.fxml.Initializable#initialize(URL, ResourceBundle)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new Thread(() -> {
            Path path = IOUtils.getResourceFolder();
            resourceFolderField.setText(path.toString());
            resourceFolderField.setOnAction(e -> saveSettings());
        }).run();
    }

    /**
     * Calls {@link #saveSettings()}
     */
    @Override
    public void stop() {
        saveSettings();
    }

    /**
     * Saves the settings using {@link SettingHandler}.
     */
    private void saveSettings() {
        SettingHandler.setString(IOUtils.RESOURCE_FOLDER_SETTING, resourceFolderField.getText());
    }

    /**
     * Handles browseResourceFolderButton clicks. Uses a file chooser to select the folder.
     */
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
            saveSettings();
        }
    }

    /**
     * Not implemented
     */
    @Override
    public void setResizeListener(ResizeListener listener) {
    }

}
