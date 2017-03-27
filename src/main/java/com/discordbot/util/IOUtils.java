package com.discordbot.util;

import javafx.scene.control.Alert;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;

/**
 * Contains utility methods for io access.
 */
public final class IOUtils {

    /**
     * The key to use for the resource folder setting
     */
    public static final String RESOURCE_FOLDER_SETTING = "resource_folder";
    /**
     * The default resource folder path
     */
    public static final String RESOURCE_FOLDER_DEFAULT = System.getProperty("user.home") + "\\DiscordBot";

    /**
     * Accesses the {@link Path} to the resource folder. If the resource folder setting is set and refers to an existing
     * {@link Path}, it will use that {@link Path}. If not, it will use {@link #RESOURCE_FOLDER_DEFAULT}
     *
     * @return the {@link Path} to the resource folder.
     */
    public static Path getResourceFolder() {
        Path path;
        try {
            // load the resource folder path from the settings
            path = Paths.get(SettingHandler.getString(RESOURCE_FOLDER_SETTING));

            if (!Files.isDirectory(path) && !path.equals(Paths.get(RESOURCE_FOLDER_DEFAULT))) {
                // the path doesn't exist and it isn't the default path
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Resource Folder Not Found");
                alert.setHeaderText("Unable To Find Resource Folder");
                alert.setContentText("Resource folder was set to " + path.toString()
                        + "\nDefaulting to " + RESOURCE_FOLDER_DEFAULT);
                alert.showAndWait();

                path = Paths.get(RESOURCE_FOLDER_DEFAULT);
                //noinspection ResultOfMethodCallIgnored
                path.toFile().mkdir();
            }
        } catch (InvalidKeyException e) {
            // the resource folder setting wasn't set, set the setting to the default path and use that path
            path = Paths.get(RESOURCE_FOLDER_DEFAULT);
            SettingHandler.setString(RESOURCE_FOLDER_SETTING, RESOURCE_FOLDER_DEFAULT);
        }

        return path;
    }

    /**
     * Determines the {@link Path} of a resource using the provided path relative to the resource folder.
     *
     * @param path The path of a resource relative to the resource folder.
     * @return The {@link Path} of the resource.
     */
    public static Path getResourcePath(String... path) {
        return Paths.get(getResourceFolder().toString(), path);
    }

    /**
     * Downloads the json file at the provided url.
     *
     * @param url The {@link URL} of the json file.
     * @return a {@link JSONObject} of the contents of the json file.
     * @throws IOException if the url cannot be reached.
     * @throws JSONException if the json file is malformed.
     */
    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream inStream = new URL(url).openStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, Charset.forName("UTF-8")));
            String jsonText = org.apache.commons.io.IOUtils.toString(reader);
            return new JSONObject(jsonText);
        }
    }

    /**
     * Downloads the image at the provided url.
     *
     * @param url The {@link URL} of the image.
     * @return a {@link BufferedImage} containing the image.
     * @throws IOException if the url cannot be reached.
     */
    public static BufferedImage readImageFromUrl(String url) throws IOException {
        return ImageIO.read(new URL(url));
    }

}
