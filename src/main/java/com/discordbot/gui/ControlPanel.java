package com.discordbot.gui;

import com.discordbot.DiscordBot;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ResumedEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import javax.swing.*;
import java.awt.*;

public class ControlPanel extends JPanel {

    private Panel statusPanel;

    public ControlPanel() {
        DiscordBot.getInstance().addEventListener(new StatusListener());

        Button startStopButton = new Button("Start/Stop");
        startStopButton.addActionListener(e -> {
            DiscordBot bot = DiscordBot.getInstance();
            if (!bot.isRunning()) {
                bot.start();
            } else {
                bot.pause();
            }
        });

        // create the inner panel
        statusPanel = new Panel();
        statusPanel.setPreferredSize(new Dimension(25, 25));
        statusPanel.setBackground(DiscordBot.getInstance().isRunning()
                ? DiscordBot.getInstance().getJDA().getStatus() == JDA.Status.CONNECTED
                        ? Color.GREEN
                        : Color.YELLOW
                : Color.RED);

        // create the wrapper panel
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new FlowLayout());
        wrapperPanel.add(statusPanel);
        wrapperPanel.add(startStopButton);

        // set the layout and add the wrapper panel
        setLayout(new BorderLayout());
        add(wrapperPanel, BorderLayout.WEST);
    } // constructor

    private class StatusListener extends ListenerAdapter {

        @Override
        public void onReady(ReadyEvent event) {
            statusPanel.setBackground(Color.GREEN);
        } // method onReady

        @Override
        public void onResume(ResumedEvent event) {
            statusPanel.setBackground(Color.GREEN);
        } // method onResume

//        @Override
//        public void onReconnect(ReconnectedEvent event) {
//            statusPanel.setBackground(Color.GREEN);
//        } // method onReconnect

        @Override
        public void onDisconnect(DisconnectEvent event) {
            statusPanel.setBackground(Color.YELLOW);
        } // method onDisconnect

        @Override
        public void onShutdown(ShutdownEvent event) {
            statusPanel.setBackground(Color.RED);
        } // method onShutdown

    } // class StatusListener

} // class ConrolPane
