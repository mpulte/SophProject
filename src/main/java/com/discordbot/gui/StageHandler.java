package com.discordbot.gui;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class StageHandler {

    private final Map<String, List<Stage>> stages = new HashMap<>();

    public synchronized boolean openStage(String tag, Scene scene, String title, boolean resizeable, int maxCopies) {
        if (!stages.containsKey(tag)) {
            stages.put(tag, new LinkedList<>());
        }
        if (maxCopies == -1 || stages.get(tag).size() < maxCopies) {
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
            stage.setResizable(resizeable);
            stage.setOnCloseRequest(event -> {
                List<Stage> list = stages.get(tag);
                list.remove(stage);
                if (list.isEmpty()) {
                    stages.remove(tag);
                }
            });
            stages.get(tag).add(stage);
            return true;
        }
        return false;
    } // method openStage

    public synchronized boolean openStage(String tag, URL fxmlLocation, String title, boolean resizeable, int maxCopies)
            throws IOException {
        if (!stages.containsKey(tag)) {
            stages.put(tag, new LinkedList<>());
        }
        if (maxCopies == -1 || stages.get(tag).size() < maxCopies) {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(fxmlLocation);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            Parent root = fxmlLoader.load(fxmlLocation.openStream());
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
            stage.setResizable(resizeable);
            stages.get(tag).add(stage);

            if (fxmlLoader.getController() instanceof FXMLController) {
                FXMLController controller = fxmlLoader.getController();
                if (!resizeable) {
                    controller.setResizeListener(stage::sizeToScene);
                }
                stage.setOnCloseRequest(event -> {
                    controller.stop();
                    removeFromList(tag, stage);
                    System.out.println("called");
                });
            } else {
                stage.setOnCloseRequest(event -> removeFromList(tag, stage));
            }

            return true;
        }
        return false;
    } // method openStage

    public synchronized void closeStages() {
        for (List<Stage> list : stages.values()) {
            while (!list.isEmpty()) {
                Stage stage = list.get(0);
                stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
                list.remove(stage);
            }
        }
        stages.clear();
    } // method closeStages

    public synchronized void closeStages(String tag) {
        List<Stage> list = stages.get(tag);
        while (list != null && !list.isEmpty()) {
            Stage stage = list.get(0);
            stage.fireEvent(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
            list.remove(stage);
        }
        stages.remove(tag);
    } // method closeStages

    private void removeFromList(String tag, Stage stage) {
        List<Stage> list = stages.get(tag);
        list.remove(stage);
        if (list.isEmpty()) {
            stages.remove(tag);
        }
    } // method removeFromList

} // class StageHandler
