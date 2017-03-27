package com.discordbot.gui;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * A singleton that handles the opening and closing of {@link Stage}s.
 */
public class StageHandler {

    private static StageHandler instance = null;

    private Map<String, List<Stage>> stages = new HashMap<>();

    /**
     * Default constructor is private for singleton class.
     */
    private StageHandler() {
    }

    /**
     * Accessor for the StageHandler instance.
     *
     * @return the StageHandler instance.
     */
    public static synchronized StageHandler getInstance() {
        if (instance == null) {
            instance = new StageHandler();
        }
        return instance;
    }

    /**
     * Opens a new {@link Stage} using the provided {@link Scene}, checking for the maximum number of instances.
     *
     * @param tag          The tag identifying the group of similar {@link Stage}s.
     * @param scene        The {@link Scene} for opening the @link Stage}.
     * @param title        The title of the {@link Stage}.
     * @param resizeable   The resizable property of the {@link Stage}
     * @param maxInstances The maximum number of instances.
     * @return the StageHandler instance.
     */
    public synchronized StageHandler openStage(String tag, Scene scene, String title, boolean resizeable,
                                               int maxInstances) {
        if (!stages.containsKey(tag)) {
            stages.put(tag, new LinkedList<>());
        }
        if (maxInstances == -1 || stages.get(tag).size() < maxInstances) {
            Stage stage = openStage(tag, scene, title, resizeable);
            stage.setOnCloseRequest(event -> {
                List<Stage> list = stages.get(tag);
                list.remove(stage);
                if (list.isEmpty()) {
                    stages.remove(tag);
                }
            });
        }
        return this;
    }

    /**
     * Opens a new {@link Stage} using the provided FXML file {@link URL} and controller, checking for the maximum
     * number of instances.
     *
     * @param tag          The tag identifying the group of similar {@link Stage}s.
     * @param fxmlLocation The {@link Scene} for opening the @link Stage}.
     * @param controller   The controller for the FXML file. Use <tt>null</tt> if no controller is needed or if the FXML
     *                     file specifies the controller.
     * @param title        The title of the {@link Stage}.
     * @param resizeable   The resizable property of the {@link Stage}
     * @param maxInstances The maximum number of instances.
     * @return the StageHandler instance.
     */
    public synchronized StageHandler openStage(String tag, URL fxmlLocation, Object controller, String title,
                                               boolean resizeable, int maxInstances) throws IOException {
        if (!stages.containsKey(tag)) {
            stages.put(tag, new LinkedList<>());
        }
        if (maxInstances == -1 || stages.get(tag).size() < maxInstances) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(fxmlLocation);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            fxmlLoader.setController(controller);

            Stage stage = openStage(tag, new Scene(fxmlLoader.load()), title, resizeable);

            // if no controller was passed as an argument, check the if the FXML file specified one
            if (controller == null) {
                controller = fxmlLoader.getController();
            }

            // if the controller is an FXMLController, utilize the setResizeListener and stop methods
            if (controller != null && controller instanceof FXMLController) {
                FXMLController fxmlController = fxmlLoader.getController();

                // if it is not resizable, use a ResizeListener to automatically resize the Stage
                if (!resizeable) {
                    fxmlController.setResizeListener(() -> {
                        stage.sizeToScene();
                        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                        stage.setHeight(Math.min(stage.getHeight(), screenBounds.getHeight()));
                        stage.setWidth(Math.min(stage.getWidth(), screenBounds.getWidth()));
                    });
                }

                // set the Stage to call the stop method of the controller on a close request
                stage.setOnCloseRequest(event -> {
                    fxmlController.stop();
                    removeFromList(tag, stage);
                });
            } else {
                stage.setOnCloseRequest(event -> removeFromList(tag, stage));
            }
        }
        return this;
    }

    /**
     * Opens a new {@link Stage} using the provided {@link Scene}.
     *
     * @param tag        The tag identifying the group of similar {@link Stage}s.
     * @param scene      The {@link Scene} for opening the @link Stage}.
     * @param title      The title of the {@link Stage}.
     * @param resizeable The resizable property of the {@link Stage}
     * @return the new stage.
     */
    private synchronized Stage openStage(String tag, Scene scene, String title, boolean resizeable) {
        // set up the Stage
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();

        stage.setResizable(resizeable);
        stage.sizeToScene();

        // if the Stage is not resizable, make sure it is not bigger than the primary screen
        if (!resizeable) {
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            stage.setHeight(Math.min(stage.getHeight(), screenBounds.getHeight()));
            stage.setWidth(Math.min(stage.getWidth(), screenBounds.getWidth()));
        }

        stages.get(tag).add(stage);

        return stage;
    }

    /**
     * Accesses a {@link List<Stage>} for a provided tag.
     *
     * @param tag The tag of the {@link List<Stage>} to access.
     * @return the {@link List<Stage>}.
     */
    public synchronized List<Stage> getStages(String tag) {
        return new ArrayList<>(stages.get(tag));
    }

    /**
     * Closes all {@link Stage}s.
     *
     * @return the StageHandler instance.
     */
    public synchronized StageHandler closeStages() {
        String[] tags = stages.keySet().toArray(new String[stages.size()]);
        for (String tag : tags) {
            closeStages(tag);
        }
        return this;
    }

    /**
     * Closes all {@link Stage}s.
     *
     * @return the StageHandler instance.
     */
    public synchronized StageHandler closeStages(String tag) {
        List<Stage> list = stages.get(tag);
        while (list != null && !list.isEmpty()) {
            Stage stage = list.get(0);
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            list.remove(stage);
        }
        stages.remove(tag);
        return this;
    }

    /**
     * Removes a {@link Stage} from {@link #stages}. Removes the {@link List} if it is empty.
     */
    private void removeFromList(String tag, Stage stage) {
        List<Stage> list = stages.get(tag);
        list.remove(stage);
        if (list.isEmpty()) {
            stages.remove(tag);
        }
    }

}
