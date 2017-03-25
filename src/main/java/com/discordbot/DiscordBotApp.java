package com.discordbot;

import com.discordbot.command.*;
import com.discordbot.gui.FXMLController;
import com.discordbot.gui.ProfanityFilterController;
import com.discordbot.gui.StageHandler;
import com.discordbot.sql.CommandDB;
import com.discordbot.util.MessageListener;
import com.discordbot.util.ProfanityFilter;
import com.discordbot.util.ProfanityFilterListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DiscordBotApp extends Application {

    private static final SimpleLog LOG = SimpleLog.getLog("DiscordBotApp");

    private ProfanityFilter filter = new ProfanityFilter();

    @Override
    public void start(Stage primaryStage) {
        // setup DiscordBot
        loadListeners();
        loadCommands();
        Platform.setImplicitExit(true);

        // set up scene
        try {
            Parent root = FXMLLoader.load(getClass().getResource("gui/MainPane.fxml"));
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
        } catch (IOException e) {
            LOG.log(e);
            Platform.exit();
        }

        // set up stage
        primaryStage.setTitle("DiscordBot");
        primaryStage.setOnCloseRequest(event -> {
            StageHandler.getInstance().closeStages();
            DiscordBot.getInstance().shutdown();
        });
        primaryStage.setResizable(false);
        primaryStage.show();

        // set size of stage
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setHeight(Math.min(primaryStage.getHeight(), primaryScreenBounds.getHeight()));
        primaryStage.setWidth(Math.min(primaryStage.getWidth(), primaryScreenBounds.getWidth()));
    }

    private void loadListeners() {
        DiscordBot.getInstance()
                .addEventListener(new MessageListener())
                .addEventListener(new ProfanityFilterListener(filter));
    }

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
    }

    @Override
    public void stop() {
        DiscordBot.getInstance().shutdown();
    }

    @FXML
    public void handleTokensMenu() {
        try {
            URL location = getClass().getResource("gui/TokenPane.fxml");
            StageHandler.getInstance().openStage("Tokens", location, null, "Tokens", false, 1);
        } catch (IOException e) {
            LOG.log(e);
        }
    }

    @FXML
    public void handleProfanityFilterMenu(ActionEvent actionEvent) {
        try {
            URL location = getClass().getResource("gui/ProfanityFilterPane.fxml");
            FXMLController controller = new ProfanityFilterController(filter);
            StageHandler.getInstance().openStage("StrawPoll", location, controller, "Straw Poll", false, -1);
        } catch (IOException e) {
            LOG.log(e);
        }
    }

    @FXML
    public void handleStrawPollMenu() {
        try {
            URL location = getClass().getResource("gui/StrawPollPane.fxml");
            StageHandler.getInstance().openStage("StrawPoll", location, null, "Straw Poll", false, -1);
        } catch (IOException e) {
            LOG.log(e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    } // method main
}
