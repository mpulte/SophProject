package com.discordbot.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class CommandHandler extends ListenerAdapter {
    private static final SimpleLog LOG = SimpleLog.getLog("ComHandler");

    private Map<String, CommandListener> listeners;

    public CommandHandler() {
        listeners = new HashMap<>();
    } // constructor

    public CommandHandler addCommandListener(String tag, CommandListener listener) {
        listeners.put(tag, listener);
        LOG.info("Command loaded: " + listener.getClass().getName());
        return this;
    } // method addCommandListener

    public CommandHandler addCommandListener(String tag, Class<? extends CommandListener> cls) {
        try {
            CommandListener listener = cls.getConstructor(CommandHandler.class).newInstance(this);
            listeners.put(tag, listener);
            LOG.info("Command added: " + listener.getClass().getName());
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.log(e);
        }
        return this;
    } // method addCommandListener

    public CommandHandler addCommandListener(CommandSetting setting) {
        if (listeners.containsKey(setting.getTag())) {
            if (listeners.get(setting.getTag()).getClass() != setting.getCls()) {
                setting.setEnabled(false);
            }
            return this;
        }
        if (setting.isEnabled()) {
            addCommandListener(setting.getTag(), setting.getCls());
        }
        return this;
    } // method addCommandListener

    public CommandHandler removeCommandListener(String tag) {
        String className = listeners.get(tag).getClass().getName();
        listeners.remove(tag);
        LOG.info("Command removed: " + className);
        return this;
    } // method removeCommandListener

    public boolean isTag(String tag) {
        return listeners.containsKey(tag);
    } // method isTag

    public Map<String, CommandListener> getCommandListeners() {
        return listeners;
    } // method getCommandListeners

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContent().startsWith(CommandReceivedEvent.PREFIX)
                && !event.getAuthor().isBot()) {
            CommandReceivedEvent commandEvent = CommandReceivedEvent.buildCommand(event);
            for (String key : listeners.keySet()) {
                if (commandEvent.getTag().equals(key)) {
                    listeners.get(key).onCommandReceived(commandEvent);
                }
            }
        }
    } // method onCommandReceivedEvent

} // class CommandHandler
