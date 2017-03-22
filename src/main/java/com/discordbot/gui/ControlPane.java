package com.discordbot.gui;

import com.discordbot.DiscordBot;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ControlPane extends HBox {

    private Pane statusPane;

    public ControlPane() {
        DiscordBot.getInstance().addEventListener(new StatusListener());

        // set up status pane
        statusPane = new Pane();
        statusPane.setPrefSize(25, 25);
        statusPane.setStyle("-fx-background-color: #" + (
                DiscordBot.getInstance().isRunning()
                ? DiscordBot.getInstance().getJDA().getStatus() == JDA.Status.CONNECTED ? "00ff00" : "ffff00"
                : "ff0000"));

        // set up start/stop button
        Button startStopButton = new Button("Start/Stop");
        startStopButton.setOnAction(e -> {
            DiscordBot bot = DiscordBot.getInstance();
            if (!bot.isRunning()) {
                bot.start();
            } else {
                bot.pause();
            }
        });

        // set up layout
        setStyle("-fx-background-color:transparent");
        getChildren().add(statusPane);
        getChildren().add(startStopButton);
    } // constructor

    private class StatusListener extends ListenerAdapter {

        @Override
        public void onReady(ReadyEvent event) {
            statusPane.setStyle("-fx-background-color: #00ff00");
        } // method onReady

        @Override
        public void onResume(ResumedEvent event) {
            statusPane.setStyle("-fx-background-color: #ffff00");
        } // method onResume
        @Override
        public void onDisconnect(DisconnectEvent event) {
            statusPane.setStyle("-fx-background-color: #ffff00");
        } // method onDisconnect

        @Override
        public void onShutdown(ShutdownEvent event) {
            statusPane.setStyle("-fx-background-color: #ff0000");
        } // method onShutdown

    } // class StatusListener

} // class ControlPane
