package com.discordbot.gui;

import com.discordbot.DiscordBot;
import com.discordbot.command.*;
import com.discordbot.sql.CommandDB;
import com.discordbot.util.MessageListener;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

public class MainWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    private final static int WINDOW_WIDTH = 800;
    private final static int WINDOW_HEIGHT = 600;
    private static MainWindow instance;
    private CommandHandler commandHandler;

    private MainWindow() {
        // set up frame
        setTitle("DiscordBot");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new MainWindowListener());

        // initialize DiscordBot and listeners
        commandHandler = new CommandHandler();
        DiscordBot.getInstance()
                .addEventListener(new MessageListener())
                .addEventListener(commandHandler);
        loadCommands();

        // build panels
        buildPanels();

        // pack and set that size to the minimum
        pack();
        setMinimumSize(getSize());

        // show the window
        setVisible(true);
    } // constructor

    public static synchronized MainWindow getInstance() {
        if (instance == null) {
            instance = new MainWindow();
        }
        return instance;
    } // method getInstance

    public static void main(String[] args) {
        MainWindow.getInstance();
    }

    private void buildPanels() {
        setLayout(new BorderLayout());
        add(new ControlPanel(), BorderLayout.NORTH);
        add(new CommandPanel(commandHandler), BorderLayout.CENTER);
    } // method buildPanels

    private void loadCommands() {
        List<CommandSetting> defaults = new ArrayList<>();
        defaults.add(new CommandSetting(HelpCommand.class, "help", false));
        defaults.add(new CommandSetting(KickCommand.class, "kick", false));
        defaults.add(new CommandSetting(RollCommand.class, "roll", false));

        CommandDB database = new CommandDB();
        for (CommandSetting defaultSetting : defaults) {
            CommandSetting savedSetting = database.select(defaultSetting.getCls());
            if (savedSetting == null) {
                if (defaultSetting.isEnabled()) {
                    commandHandler.addCommandListener(defaultSetting);
                }
                database.insert(defaultSetting);
            } else if (savedSetting.isEnabled()) {
                commandHandler.addCommandListener(savedSetting);
                if (!savedSetting.isEnabled()) {
                    database.update(savedSetting);
                }
            }
        }
    } // method loadCommands

    private class MainWindowListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            DiscordBot.getInstance().shutdown();
            dispose();
        } // method windowClosing

    } // class MainWindowListener

    private class ExtraWindowListener extends WindowAdapter {

        @Override
        public void windowOpened(WindowEvent e) {
            System.out.println("Received open callback");
        } // method windowOpened

        @Override
        public void windowClosed(WindowEvent e) {
            System.out.println("Received close callback");
        } // method windowClosed

    } // class ExtraWindowListener

} // Class MainFrame