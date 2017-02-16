package com.discordbot;

import com.discordbot.command.*;
import com.discordbot.gui.CommandPane;
import com.discordbot.gui.ControlPane;
import com.discordbot.sql.CommandDB;
import com.discordbot.util.MessageListener;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class DiscordBotApp extends Application {

    private final Insets PADDING = new Insets(10, 10, 10, 10);
    private final int WIDTH = 400;
    private final int HEIGHT = 300;

    private CommandHandler commandHandler;

    @Override
    public void start(Stage primaryStage) throws Exception {
        // set title
        primaryStage.setTitle("DiscordBot");

        // setup DiscordBot
        DiscordBot.getInstance()
                .addEventListener(new MessageListener())
                .addEventListener(commandHandler = new CommandHandler());
        loadCommands();

        // setup layout
        VBox layout = new VBox();
        layout.setPadding(PADDING);
        layout.getChildren().add(buildControlPane());
        layout.getChildren().add(buildCommandPane());

        // setup scene
        Scene scene = new Scene(layout, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(WIDTH + PADDING.getLeft() + PADDING.getRight());
//        primaryStage.setMaxWidth(WIDTH + PADDING.getLeft() + PADDING.getRight());
        primaryStage.show();
    } // method start

    private Region buildControlPane() {
        ControlPane controlPane = new ControlPane();
        controlPane.setPadding(new Insets(0, 0, PADDING.getBottom(), 0));
        return controlPane;
    } // method buildControlPane

    private Region buildCommandPane() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setStyle("-fx-background-color:transparent");
        scrollPane.setFitToWidth(true);
        scrollPane.setContent(new CommandPane(commandHandler));

        TitledPane titledPane = new TitledPane("Commands", scrollPane);
        titledPane.setCollapsible(false);
        return titledPane;
    } // method buildCommandPane

    private void loadCommands() {
        List<CommandSetting> defaults = new ArrayList<>();
        defaults.add(new CommandSetting(HelpCommand.class, "help", false));
        defaults.add(new CommandSetting(KickCommand.class, "kick", false));
        defaults.add(new CommandSetting(RollCommand.class, "roll", false));
        defaults.add(new CommandSetting(EightBallCommand.class, "8ball", false));


        CommandDB database = new CommandDB();
        for (CommandSetting defaultSetting : defaults) {
            CommandSetting savedSetting = database.select(defaultSetting.getCls());
            if (savedSetting == null) {
                if (defaultSetting.isEnabled()) {
                    commandHandler.setCommandListener(defaultSetting);
                }
                database.insert(defaultSetting);
            } else if (savedSetting.isEnabled()) {
                commandHandler.setCommandListener(savedSetting);
                if (!savedSetting.isEnabled()) {
                    database.update(savedSetting);
                }
            }
        }
    } // method loadCommands

    @Override
    public void stop() {
        DiscordBot.getInstance().shutdown();
    } // method stop

    public static void main(String[] args) {
        launch(args);
    } // method main

} // class DiscordBotApp
