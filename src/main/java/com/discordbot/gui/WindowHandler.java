package com.discordbot.gui;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class WindowHandler {

    private final Map<String, Stage> stages = new HashMap<String, Stage>();

    public synchronized boolean openStage(String tag, Scene scene) {
        if (!stages.containsKey(tag)) {
            Stage stage = new Stage();
        }
        return false;
    } // method openWindow

    public synchronized void closeStage(String tag) {
        if (stages.containsKey(tag)) {
            stages.get(tag).close();
            stages.remove(tag);
        }
    } // method closeWindow

} // class WindowHandler
