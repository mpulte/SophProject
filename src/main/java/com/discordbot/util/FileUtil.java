package com.discordbot.util;

import javafx.scene.control.Alert;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;

public final class FileUtil {

	public static final String RESOURCE_FOLDER_SETTING = "resource_folder";
	private static final String RESOURCE_FOLDER_DEFAULT = System.getProperty("user.home") + "/DiscordBot";

	public static Path getResourceFolder() {
		Path path;
		try {
			path = Paths.get(SettingsManager.getString(RESOURCE_FOLDER_SETTING));
			if (!Files.isDirectory(path)) {
				Alert alert = new Alert(Alert.AlertType.WARNING);
				alert.setTitle("Resource Folder Not Found");
				alert.setHeaderText("Unable To Find Resource Folder");
				alert.setContentText("Resource folder was set to " + path.toString()
						+ "\nDefaulting to " + RESOURCE_FOLDER_DEFAULT);
				alert.showAndWait();

				path = Paths.get(RESOURCE_FOLDER_DEFAULT);
			}
		} catch (InvalidKeyException e) {
			path = Paths.get(RESOURCE_FOLDER_DEFAULT);
			SettingsManager.setString(RESOURCE_FOLDER_SETTING, RESOURCE_FOLDER_DEFAULT);
		}

        return path;
	}

} // class FileUtil
