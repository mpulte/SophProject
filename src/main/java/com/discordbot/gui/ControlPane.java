package com.discordbot.gui;

import com.discordbot.DiscordBot;
import com.discordbot.util.SettingsManager;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;

public class ControlPane extends HBox {

    private static final SimpleLog LOG = SimpleLog.getLog("StageHandler");

    private Pane statusPane;
    private Button startStopButton;

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
        startStopButton = new Button("Start");
        startStopButton.setOnAction(actionEvent -> {
            DiscordBot bot = DiscordBot.getInstance();
            if (!bot.isRunning()) {
                try {
                    String token = SettingsManager.getString(TokenController.TOKEN_SETTING);
                    if (token.equals("")) {
                        throw new InvalidKeyException("Token not set");
                    }
                    bot.start(token);
                    if (bot.isRunning()) {
                        startStopButton.setText("Stop");
                    }
                } catch (InvalidKeyException e1) {
                    LOG.warn(e1.getMessage());
                    try {
                        URL location = getClass().getResource("../gui/TokenPane.fxml");
                        StageHandler.getInstance().openStage("Tokens", location, "Tokens", false, 1);
                    } catch (IOException e2) {
                        LOG.log(e2);
                    }
                }
            } else {
                bot.pause();
                statusPane.setStyle("-fx-background-color: #ff0000");
                startStopButton.setText("Start");
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
            statusPane.setStyle("-fx-background-color: #00ff00");
        } // method onResume
        @Override
        public void onDisconnect(DisconnectEvent event) {
            statusPane.setStyle("-fx-background-color: #ffff00");
        } // method onDisconnect

        @Override
        public void onShutdown(ShutdownEvent event) {
            statusPane.setStyle("-fx-background-color: #ff0000");
            startStopButton.setText("Start");
        } // method onShutdown

    } // class StatusListener

} // class ControlPane
