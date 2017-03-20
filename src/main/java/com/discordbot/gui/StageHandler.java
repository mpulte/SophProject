package com.discordbot.gui;

import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class StageHandler {

    private final Map<String, Stage> stages = new HashMap<>();

    public synchronized boolean openStage(String tag, Scene scene, String title, boolean resizeable) {
        if (!stages.containsKey(tag)) {
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
            stage.setResizable(resizeable);
            stages.put(tag, stage);
            return true;
        }
        return false;
    } // method openStage

    public synchronized boolean openStage(String tag, URL fxmlLocation, String title, boolean resizeable) {
        if (!stages.containsKey(tag)) {
            try {
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
                stages.put(tag, stage);


                if (fxmlLoader.getController() instanceof FXMLController) {
                    FXMLController controller = fxmlLoader.getController();
                    if (!resizeable) {
                        controller.setResizeListener(stage::sizeToScene);
                    }
                    stage.setOnCloseRequest(event -> controller.stop());
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    } // method openStage

    public synchronized void closeStage(String tag) {
        if (stages.containsKey(tag)) {
            stages.get(tag).close();
            stages.remove(tag);
        }
    } // method closeStage

    public synchronized void closeStages() {
        for (Stage stage : stages.values()) {
            stage.close();
        }
        stages.clear();
    } // method closeStages

} // class StageHandler
