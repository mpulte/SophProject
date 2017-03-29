package com.discordbot;

import com.discordbot.command.*;
import com.discordbot.gui.FXMLController;
import com.discordbot.gui.ProfanityFilterController;
import com.discordbot.gui.StageHandler;
import com.discordbot.gui.TokenController;
import com.discordbot.model.ProfanityFilter;
import com.discordbot.sql.CommandDB;
import com.discordbot.util.IOUtils;
import com.discordbot.util.MessageListener;
import com.discordbot.util.ProfanityFilterListener;
import com.discordbot.util.SettingHandler;
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
import java.security.InvalidKeyException;
import java.util.ArrayList;
import java.util.List;

/**
 * DiscordBotApp is the entry point for the DiscordBot application. It starts the application and handles any JavaFX
 * actions from the MainPane.
 */
public class DiscordBotApp extends Application {

    private static final SimpleLog LOG = SimpleLog.getLog("DiscordBotApp");

    @FXML
    private MenuItem tokensMenuItem;
    @FXML
    private MenuItem settingsMenuItem;
    @FXML
    private MenuItem profanityFilterMenuItem;
    @FXML
    private MenuItem strawPollMenuItem;

    private ProfanityFilter profanityFilter = new ProfanityFilter();

    /**
     * Starts DiscordBotApp
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The main entry point for the JavaFX application.
     *
     * @param primaryStage The primary {@link Stage} for this application, onto which the application scene can be set.
     */
    @Override
    public void start(Stage primaryStage) {
        // setup DiscordBot
        initializeListeners();
        loadCommands();
        initializeProfanityFilter();

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

    /**
     * Initializes the event listeners to be used by {@link DiscordBot}.
     */
    private void initializeListeners() {
        // load message listener
        DiscordBot.getInstance().addEventListener(new MessageListener());

        // load profanity filter listener
        final ProfanityFilterListener profanityFilterListener = new ProfanityFilterListener(profanityFilter);
        try {
            // use enabled setting
            if (SettingHandler.getBoolean(ProfanityFilterListener.SETTING_ENABLED)) {
                DiscordBot.getInstance().addEventListener(profanityFilterListener);
            }
        } catch (InvalidKeyException e) {
            // default is enabled
            SettingHandler.setBoolean(ProfanityFilterListener.SETTING_ENABLED, true);
            DiscordBot.getInstance().addEventListener(profanityFilterListener);
        }

        // set up change listener for profanity filter enabled setting
        try {
            final boolean profanityFilterEnabled = SettingHandler.getBoolean(ProfanityFilterListener.SETTING_ENABLED);
            SettingHandler.addBooleanChangeListener(new SettingHandler.ChangeListener<Boolean>() {

                boolean enabled = profanityFilterEnabled;

                @Override
                public void onChange(String key, Boolean value) {
                    if (key.equals(ProfanityFilterListener.SETTING_ENABLED)) {
                        if (value && !enabled) {
                            DiscordBot.getInstance().addEventListener(profanityFilterListener);
                            enabled = true;

                        } else if (!value && enabled) {
                            DiscordBot.getInstance().removeEventListener(profanityFilterListener);
                            enabled = false;
                        }
                    }
                }
            });
        } catch (InvalidKeyException e) {
            LOG.warn("This should never be reached!");
            LOG.log(e);
        }

        // set up change listener for token setting
        try {
            final String initialToken = SettingHandler.getString(TokenController.TOKEN_SETTING);
            SettingHandler.addStringChangeListener(new SettingHandler.ChangeListener<String>() {

                String token = initialToken;

                @Override
                public void onChange(String key, String value) {
                    // reboot bot if token changed
                    if (key.equals(TokenController.TOKEN_SETTING) && DiscordBot.getInstance().isRunning()
                            && !value.isEmpty() && !value.equals(token)) {
                        DiscordBot.getInstance().reboot(value);
                        token = value;
                    }
                }
            });
        } catch (InvalidKeyException e) {
            LOG.warn(e.getMessage());
        }
    }

    /**
     * Loads {@link CommandSetting} from the {@link CommandDB} database. If {@link CommandSetting}s are not yet stored
     * in the database, the default values will be inserted in the database.
     */
    private void loadCommands() {
        new Thread(() -> {
            // use CommandLoader to load annotated commands
            List<CommandSetting> defaults = new CommandLoader("com.discordbot").getCommandSettings();

            // load each command that is listed in defaults
            CommandDB database = new CommandDB();
            for(
            CommandSetting defaultSetting :defaults)

            {
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

            // remove commands not listed in defaults from database
            for(
            CommandSetting savedSetting :database.selectAll())

            {
                boolean found = false;
                for (CommandSetting defaultSetting : defaults) {
                    if (savedSetting.equals(defaultSetting)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    database.delete(savedSetting.getCls());
                }
            }
        }).start();
    }

    /**
     * Initializes the {@link ProfanityFilter}. Loads the file "profanity_filter.txt" from the resource folder set in
     * {@link SettingHandler}. Adds a {@link com.discordbot.model.ProfanityFilter.ChangeListener}
     * for saving changes to the "profanity_filter.txt" file.
     */
    private void initializeProfanityFilter() {
        // load list from file
        try {
            List<String> lines = Files.readAllLines(IOUtils.getResourcePath("profanity_filter.txt"));
            if (!lines.isEmpty()) {
                profanityFilter.add(lines.toArray(new String[lines.size()]));
            }
        } catch (IOException e) {
            LOG.warn("profanity_filter.txt not found");
        }

        // set up profanity filter change listener
        profanityFilter.setChangeListener((filter, type, words) -> {
            try {
                Files.write(IOUtils.getResourcePath("profanity_filter.txt"), filter.asList(),
                        Charset.forName("UTF-8"));
            } catch (IOException e) {
                LOG.log(e);
            }
        });
    }

    /**
     * Sets each {@link MenuItem}'s {@link java.awt.event.ActionListener}.
     */
    private void initializeMenu() {
        tokensMenuItem.setOnAction(e -> handleTokensMenu());
        settingsMenuItem.setOnAction(e -> handleSettingsMenu());
        profanityFilterMenuItem.setOnAction(e -> handleProfanityFilterMenu());
        strawPollMenuItem.setOnAction(e -> handleStrawPollMenu());
    }

    /**
     * This method is called when the application should stop. It calls {@link DiscordBot#shutdown()
     * DiscordBot.getInstance().shutdown()}.
     */
    @Override
    public void stop() {
        DiscordBot.getInstance().shutdown();
    }

    /**
     * Opens a new window of the Token Pane using {@link StageHandler}. Only allows one instance of the pane.
     */
    @FXML
    public void handleTokensMenu() {
        try {
            URL location = getClass().getResource("gui/TokenPane.fxml");
            StageHandler.getInstance().openStage("Tokens", location, null, "Tokens", false, 1);
        } catch (IOException e) {
            LOG.log(e);
        }
    }


    /**
     * Opens a new window of the Settings Pane using {@link StageHandler}. Only allows one instance of the pane.
     */
    public void handleSettingsMenu() {
        try {
            URL location = getClass().getResource("gui/SettingsPane.fxml");
            StageHandler.getInstance().openStage("Settings", location, null, "Settings", false, 1);
        } catch (IOException e) {
            LOG.log(e);
        }
    }

    /**
     * Opens a new window of the Profanity Filter Pane using {@link StageHandler}. Only allows one instance of the pane.
     */
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

    /**
     * Opens a new window of the Straw Poll Pane using {@link StageHandler}. Allows unlimited instances of the pane.
     */
    @FXML
    public void handleStrawPollMenu() {
        try {
            URL location = getClass().getResource("gui/StrawPollPane.fxml");
            StageHandler.getInstance().openStage("StrawPoll", location, null, "Straw Poll", false, -1);
        } catch (IOException e) {
            LOG.log(e);
        }
    }

}
