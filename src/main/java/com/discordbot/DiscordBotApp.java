package com.discordbot;

import com.discordbot.command.*;
import com.discordbot.gui.StageHandler;
import com.discordbot.sql.CommandDB;
import com.discordbot.util.MessageListener;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DiscordBotApp extends Application {

    private static StageHandler stageHandler = new StageHandler();

    @Override
    public void start(Stage primaryStage) {
        // setup DiscordBot
        DiscordBot.getInstance().addEventListener(new MessageListener());
        loadCommands();

        // set up scene
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = FXMLLoader.load(getClass().getResource("gui/MainPane.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        // set up stage
        primaryStage.setTitle("DiscordBot");
        primaryStage.setOnCloseRequest(event -> {
            stageHandler.closeStages();
            DiscordBot.getInstance().shutdown();
        });
        primaryStage.setResizable(false);
        primaryStage.show();

        // set size of stage
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setHeight(Math.min(primaryStage.getHeight(), primaryScreenBounds.getHeight()));
        primaryStage.setWidth(Math.min(primaryStage.getWidth(), primaryScreenBounds.getWidth()));
    } // method start

    private void loadCommands() {
        List<CommandSetting> defaults = new ArrayList<>();
        defaults.add(new CommandSetting(HelpCommand.class, "help", false));
        defaults.add(new CommandSetting(KickCommand.class, "kick", false));
        defaults.add(new CommandSetting(RollCommand.class, "roll", false));
        defaults.add(new CommandSetting(EightBallCommand.class, "8ball", false));
        defaults.add(new CommandSetting(WikipediaCommand.class, "wiki", false));

        CommandDB database = new CommandDB();
        for (CommandSetting defaultSetting : defaults) {
            CommandSetting savedSetting = database.select(defaultSetting.getCls());
            if (savedSetting == null) {
                if (defaultSetting.isEnabled()) {
                    DiscordBot.getInstance().getCommandHandler().setCommandListener(defaultSetting);
                }
                database.insert(defaultSetting);
            } else if (savedSetting.isEnabled()) {
                DiscordBot.getInstance().getCommandHandler().setCommandListener(savedSetting);
                if (!savedSetting.isEnabled()) {
                    database.update(savedSetting);
                }
            }
        }
    } // method loadCommands

    public void handleStrawPollAction(ActionEvent actionEvent) {
        try {
            URL location = getClass().getResource("gui/StrawPollPane.fxml");
            stageHandler.openStage("StrawPoll", location, "Straw Poll", false, -1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } // method handleStrawPollAction

    public static void main(String[] args) {
        launch(args);
    } // method main

} // class DiscordBotApp
