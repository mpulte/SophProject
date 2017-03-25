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

public class StageHandler {

    private static StageHandler instance = null;

    private Map<String, List<Stage>> stages;

    private StageHandler() {
        stages = new HashMap<>();
    }

    public static synchronized StageHandler getInstance() {
        if (instance == null) {
            instance = new StageHandler();
        }
        return instance;
    }

    public synchronized StageHandler openStage(String tag, Scene scene, String title, boolean resizeable,
                                               int maxCopies) {
        if (!stages.containsKey(tag)) {
            stages.put(tag, new LinkedList<>());
        }
        if (maxCopies == -1 || stages.get(tag).size() < maxCopies) {
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

    public synchronized StageHandler openStage(String tag, URL fxmlLocation, Object controller, String title,
                                               boolean resizeable, int maxCopies) throws IOException {
        if (!stages.containsKey(tag)) {
            stages.put(tag, new LinkedList<>());
        }
        if (maxCopies == -1 || stages.get(tag).size() < maxCopies) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(fxmlLocation);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            fxmlLoader.setController(controller);

            Stage stage = openStage(tag, new Scene(fxmlLoader.load(fxmlLocation.openStream())), title, resizeable);
            if (controller == null) {
                controller = fxmlLoader.getController();
            }
            if (controller != null && controller instanceof FXMLController) {
                FXMLController fxmlController = fxmlLoader.getController();
                if (!resizeable) {
                    fxmlController.setResizeListener(() -> {
                        stage.sizeToScene();
                        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                        stage.setHeight(Math.min(stage.getHeight(), screenBounds.getHeight()));
                        stage.setWidth(Math.min(stage.getWidth(), screenBounds.getWidth()));
                    });
                }
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

    private synchronized Stage openStage(String tag, Scene scene, String title, boolean resizeable) {
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();

        stage.setResizable(resizeable);
        stage.sizeToScene();

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        if (!resizeable) {
            stage.setHeight(Math.min(stage.getHeight(), screenBounds.getHeight()));
            stage.setWidth(Math.min(stage.getWidth(), screenBounds.getWidth()));
        }

        stages.get(tag).add(stage);

        return stage;
    }

    public synchronized List<Stage> getStages(String tag) {
        return stages.get(tag);
    }

    public synchronized StageHandler closeStages() {
        String[] tags = stages.keySet().toArray(new String[stages.size()]);
        for (String tag : tags) {
            closeStages(tag);
        }
        return this;
    }

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

    private void removeFromList(String tag, Stage stage) {
        List<Stage> list = stages.get(tag);
        list.remove(stage);
        if (list.isEmpty()) {
            stages.remove(tag);
        }
    }

}
