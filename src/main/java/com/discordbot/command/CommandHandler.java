package com.discordbot.command;

import com.discordbot.model.ProfanityFilter;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link ListenerAdapter} that handles {@link MessageReceivedEvent} and pushes {@link
 * CommandReceivedEvent} to {@link CommandListener}.
 *
 * @see net.dv8tion.jda.core.hooks.ListenerAdapter
 */
public class CommandHandler extends ListenerAdapter {

    private static final SimpleLog LOG = SimpleLog.getLog("ComHandler");

    private final Map<String, CommandListener> listeners = new HashMap<>();
    private final ProfanityFilter profanityFilter;

    private boolean profanityFilterEnabled = false;

    public CommandHandler(ProfanityFilter profanityFilter) {
        this.profanityFilter = profanityFilter;
    }

    /**
     * Adds provided {@link CommandListener} to the listeners that will be used to handle {@link CommandReceivedEvent}.
     *
     * @param tag      The tag for identifying the {@link CommandListener} passed.
     * @param listener The {@link CommandListener} which will react to {@link CommandReceivedEvent}.
     * @return A reference to this CommandHandler.
     */
    public CommandHandler addCommandListener(String tag, CommandListener listener) {
        listeners.put(tag, listener);
        LOG.info("Command loaded: " + listener.getClass().getName());
        return this;
    }

    /**
     * Adds provided {@link CommandListener} to the listeners that will be used to handle {@link CommandReceivedEvent}.
     *
     * @param tag The tag for identifying the {@link CommandListener} passed.
     * @param cls The {@link Class} of the {@link CommandListener} will react to {@link CommandReceivedEvent}.
     * @return A reference to this CommandHandler.
     */
    public CommandHandler addCommandListener(String tag, Class<? extends CommandListener> cls) {
        try {
            addCommandListener(tag, cls.getConstructor(CommandHandler.class).newInstance(this));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            LOG.log(e);
        }
        return this;
    }

    /**
     * Adds provided {@link CommandSetting} for the listeners that will be used to handle {@link CommandReceivedEvent}.
     *
     * @param setting The {@link CommandSetting} containing the tag for identifying the {@link CommandListener} and the
     *                {@link Class} of the {@link CommandListener}.
     * @return A reference to this CommandHandler.
     */
    public CommandHandler setCommandListener(CommandSetting setting) {
        if (setting.isEnabled()) {
            // if key exists, we won't try to add the command
            if (listeners.containsKey(setting.getTag())) {
                // if key is from a different listener, disable the setting
                if (listeners.get(setting.getTag()).getClass() != setting.getCls()) {
                    setting.setEnabled(false);
                }
                return this;
            }
            addCommandListener(setting.getTag(), setting.getCls());
        } else if (listeners.containsKey(setting.getTag())
                && listeners.get(setting.getTag()).getClass() == setting.getCls()) {
            // only remove if the class matches the current class
            removeCommandListener(setting.getTag());
        }
        return this;
    }

    /**
     * Removes the {@link CommandListener} identified by the tag provided.
     *
     * @param tag The tag identifying the {@link CommandListener} to remove.
     * @return A reference to this CommandHandler.
     */
    public CommandHandler removeCommandListener(String tag) {
        if (listeners.containsKey(tag)) {
            String className = listeners.get(tag).getClass().getName();
            listeners.remove(tag);
            LOG.info("Command removed: " + className);
        }
        return this;
    }

    /**
     * Accessor for a {@link Map} of the registered {@link CommandListener}.
     *
     * @return A {@link Map} of the registered {@link CommandListener}
     */
    public Map<String, CommandListener> getCommandListeners() {
        return new HashMap<>(listeners);
    }

    /**
     * Checks if a tag is in use.
     *
     * @param tag The tag to check.
     * @return A reference to this CommandHandler.
     */
    public boolean isTag(String tag) {
        return listeners.containsKey(tag);
    }

    /**
     * Enables/disables the {@link ProfanityFilter} when parsing {@link MessageReceivedEvent}s.
     *
     * @param enabled <tt>true</tt> to enable the {@link ProfanityFilter}, <tt>false</tt> to disable the {@link
     *                ProfanityFilter}.
     * @return A reference to this CommandHandler.
     */
    public CommandHandler enableProfanityFilter(boolean enabled) {
        profanityFilterEnabled = enabled;
        return this;
    }

    /**
     * Handles a {@link MessageReceivedEvent} by creating a {@link CommandReceivedEvent} and pushing it to the
     * registered {@link CommandListener}.
     *
     * @param event The {@link MessageReceivedEvent} to handle.
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getMessage().getContent().startsWith(CommandReceivedEvent.PREFIX)
                && !event.getAuthor().isBot()
                && (!profanityFilterEnabled || profanityFilter.filter(event.getMessage().getContent()).isEmpty())) {
            CommandReceivedEvent commandEvent = CommandReceivedEvent.buildCommand(event);
            for (Map.Entry<String, CommandListener> entry : listeners.entrySet()) {
                if (commandEvent.getTag().equals(entry.getKey())
                        && entry.getValue().usesChannel(event.getChannelType())) {
                    entry.getValue().onCommandReceived(commandEvent);
                }
            }
        }
    }

}
