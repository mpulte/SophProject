package com.discordbot.gui;

import com.discordbot.DiscordBot;
import com.discordbot.util.SettingHandler;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.*;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidKeyException;

/**
 * A JavaFX {@link javafx.scene.layout.Pane Pane} used to start and pause the {@link DiscordBot DiscordBot).
 */
public class ControlPane extends HBox {

    private static final SimpleLog LOG = SimpleLog.getLog("StageHandler");

    private Pane statusPane = new Pane();
    private Button startStopButton = new Button();

    /**
     * Default constructor builds the ControlPane.
     */
    public ControlPane() {
        // set up status pane
        statusPane.setPrefSize(25, 25);
        statusPane.styleProperty();

        if (DiscordBot.getInstance().getJDA() != null) {
            updateColor(DiscordBot.getInstance().getJDA().getStatus(), null);
        } else {
            updateColor(PaneColor.RED);
        }

        // set up start/stop button
        startStopButton.setText(DiscordBot.getInstance().isRunning() ? "Stop" : "Start");
        startStopButton.setOnAction(actionEvent ->
                new Thread(() -> {
                    DiscordBot bot = DiscordBot.getInstance();
                    if (!bot.isRunning()) {
                        try {
                            String token = SettingHandler.getString(TokenController.TOKEN_SETTING);
                            if (token.equals("")) {
                                throw new InvalidKeyException("Token not set");
                            }
                            bot.start(token);
                            if (bot.isRunning()) {
                                Platform.runLater(() -> startStopButton.setText("Stop"));
                            }
                        } catch (InvalidKeyException e1) {
                            Platform.runLater(() -> {
                                LOG.warn(e1.getMessage());
                                try {
                                    URL location = getClass().getResource("../gui/TokenPane.fxml");
                                    StageHandler.getInstance().openStage("Tokens", location, null, "Tokens", false, 1);
                                } catch (IOException e2) {
                                    LOG.log(e2);
                                }
                            });
                        }
                    } else {
                        bot.pause();
                        Platform.runLater(() -> startStopButton.setText("Start"));
                    }
                }).start());

        // set up layout
        setStyle("-fx-background-color:transparent");
        Pane divider = new Pane();
        divider.setPrefWidth(10);
        getChildren().add(statusPane);
        getChildren().add(divider);
        getChildren().add(startStopButton);

        // set up status listener
        ListenerAdapter listener = new ListenerAdapter() {
            @Override
            public void onStatusChange(StatusChangeEvent event) {
                updateColor(event.getStatus(), event.getOldStatus());
                updateButton(event.getStatus());
            }
        };
        if (isVisible()) {
            DiscordBot.getInstance().addEventListener(listener);
        }

        // enable/disable the status listener when the control panel is/isn't visible
        visibleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && !oldValue) {
                DiscordBot.getInstance().addEventListener(listener);
                if (DiscordBot.getInstance().getJDA() != null) {
                    updateColor(DiscordBot.getInstance().getJDA().getStatus(), null);
                } else {
                    updateColor(PaneColor.RED);
                }
                startStopButton.setText(DiscordBot.getInstance().isRunning() ? "Stop" : "Start");
            } else if (!newValue && oldValue) {
                DiscordBot.getInstance().removeEventListener(listener);
            }
        });
    }

    /**
     * Changes the text of the startStopButton.
     *
     * @param status The new {@link JDA.Status}.
     */
    private void updateButton(JDA.Status status) {
        if (status == JDA.Status.INITIALIZED) {
            Platform.runLater(() -> startStopButton.setText("Stop"));
        } else if (status == JDA.Status.SHUTDOWN) {
            Platform.runLater(() -> startStopButton.setText("Start"));
        }
    }

    /**
     * Changes the color of the status pane.
     *
     * @param newStatus The new {@link JDA.Status}.
     * @param oldStatus The old {@link JDA.Status}.
     */
    private void updateColor(JDA.Status newStatus, JDA.Status oldStatus) {
        if (oldStatus == null) {
            // no old status, update it
            updateColor(getColor(newStatus));
        } else {
            // update only if color has changed and we are not switching from SHUTDOWN to SHUTTING_DOWN or DISCONNECTED
            if (getColor(newStatus) != getColor(oldStatus)
                    && !(oldStatus == JDA.Status.SHUTDOWN
                    && (newStatus == JDA.Status.SHUTTING_DOWN || newStatus == JDA.Status.DISCONNECTED))) {
                updateColor(getColor(newStatus));
            }
        }
    }

    /**
     * Changes the color of the status pane.
     *
     * @param color The {@link PaneColor} corresponding to the color to set the status pane.
     */
    private void updateColor(PaneColor color) {
        switch (color) {
            case GREEN:
                Platform.runLater(() -> statusPane.setStyle("-fx-background-color:#00ff00"));
                break;
            case YELLOW:
                Platform.runLater(() -> statusPane.setStyle("-fx-background-color:#ffff00"));
                break;
            case RED:
                Platform.runLater(() -> statusPane.setStyle("-fx-background-color:#ff0000"));
                break;
        }
    }

    /**
     * Determines the {@link PaneColor} based on the {@link JDA.Status}.
     *
     * @param status The {@link JDA.Status}.
     * @return the {@link PaneColor} corresponding to the {@link JDA.Status}.
     */
    private PaneColor getColor(JDA.Status status) {
        switch (status) {
            case CONNECTED:
                return PaneColor.GREEN;
            case INITIALIZING:
            case SHUTDOWN:
                return PaneColor.RED;
            default:
                return PaneColor.YELLOW;
        }
    }

    /**
     * The possible colors of the status pane.
     */
    private enum PaneColor {
        RED, YELLOW, GREEN
    }

}
