package com.discordbot;

import com.discordbot.command.*;
import com.discordbot.gui.FXMLController;
import com.discordbot.gui.ProfanityFilterController;
import com.discordbot.gui.StageHandler;
import com.discordbot.model.ProfanityFilter;
import com.discordbot.sql.CommandDB;
import com.discordbot.util.FileUtil;
import com.discordbot.util.MessageListener;
import com.discordbot.util.ProfanityFilterListener;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Screen;
import javafx.stage.Stage;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class DiscordBotApp extends Application {

    private static final SimpleLog LOG = SimpleLog.getLog("DiscordBotApp");

    private ProfanityFilter profanityFilter = new ProfanityFilter();

    @FXML
    MenuItem tokensMenuItem;
    @FXML
    MenuItem settingsMenuItem;
    @FXML
    MenuItem profanityFilterMenuItem;
    @FXML
    MenuItem strawPollMenuItem;

    @Override
    public void start(Stage primaryStage) {
        // set up scene
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("gui/MainPane.fxml"));
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            fxmlLoader.setController(this);
            primaryStage.setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            LOG.log(e);
            Platform.exit();
        }

        // setup DiscordBot
        initializeListeners();
        loadCommands();
        initializeProfanityFilter();

        // set up stage
        primaryStage.setTitle("DiscordBot");
        primaryStage.setOnCloseRequest(event -> {
            StageHandler.getInstance().closeStages();
            DiscordBot.getInstance().shutdown();
        });
        primaryStage.setResizable(false);
        primaryStage.show();
        initializeMenu();

        // set size of stage
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setHeight(Math.min(primaryStage.getHeight(), primaryScreenBounds.getHeight()));
        primaryStage.setWidth(Math.min(primaryStage.getWidth(), primaryScreenBounds.getWidth()));
    }

    private void initializeListeners() {
        DiscordBot.getInstance()
                .addEventListener(new MessageListener())
                .addEventListener(new ProfanityFilterListener(profanityFilter));
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

    private void initializeProfanityFilter() {
        // load list from file
        try {
            List<String> lines = Files.readAllLines(FileUtil.getResourcePath("profanity_filter.txt"));
            if (!lines.isEmpty()) {
                profanityFilter.add(lines.toArray(new String[lines.size()]));
            }
        } catch (IOException e) {
            LOG.warn("profanity_filter.txt not found");
        }

        // set up change listener
        profanityFilter.setChangeListener((filter, type, words) -> {
            try {
                Files.write(FileUtil.getResourcePath("profanity_filter.txt"), filter.asList(),
                        Charset.forName("UTF-8"));
            } catch (IOException e) {
                LOG.log(e);
            }
        });
    }

    private void initializeMenu() {
        tokensMenuItem.setOnAction(e -> handleTokensMenu());
        settingsMenuItem.setOnAction(e -> handleSettingsMenu());
        profanityFilterMenuItem.setOnAction(e -> handleProfanityFilterMenu());
        strawPollMenuItem.setOnAction(e -> handleStrawPollMenu());
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

    public void handleSettingsMenu() {
        try {
            URL location = getClass().getResource("gui/SettingsPane.fxml");
            StageHandler.getInstance().openStage("Settings", location, null, "Settings", false, 1);
        } catch (IOException e) {
            LOG.log(e);
        }
    }

    @FXML
    public void handleProfanityFilterMenu() {
        try {
            URL location = getClass().getResource("gui/ProfanityFilterPane.fxml");
            FXMLController controller = new ProfanityFilterController(profanityFilter);
            StageHandler.getInstance().openStage("ProfanityFilter", location, controller, "Profanity Filter", false, 1);
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
    }

}
