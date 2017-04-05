package com.discordbot.command;

import net.dv8tion.jda.core.entities.ChannelType;

/**
 * {@link CommandHandler} pushes {@link CommandReceivedEvent} to any registered CommandListener.
 */
public abstract class CommandListener {

    /**
     * Handles any {@link CommandReceivedEvent}. Calls {@link #onCommandReceived(CommandReceivedEvent, CommandHandler)}
     * if and only if the {@link ChannelType} of the {@link net.dv8tion.jda.core.events.message.MessageReceivedEvent}
     * passes {@link #usesChannel(ChannelType)};
     *
     * @param event The {@link CommandReceivedEvent} to handle.
     * @param handler The {@link CommandHandler} that pushed the {@link CommandReceivedEvent}.
     * @throws IllegalArgumentException if the {@link ChannelType} is not allowed.
     */
    public final void handleCommandReceived(CommandReceivedEvent event, CommandHandler handler)
            throws IllegalArgumentException {
        if (!usesChannel(event.getMessageReceivedEvent().getChannelType())) {
            throw new IllegalArgumentException("Channel type not allowed");
        }
        onCommandReceived(event, handler);
    }

    /**
     * Handles any {@link CommandReceivedEvent}. Called by {@link #handleCommandReceived(CommandReceivedEvent,
     * CommandHandler)}} if and only if the {@link ChannelType} of the
     * {@link net.dv8tion.jda.core.events.message.MessageReceivedEvent} passes {@link #usesChannel(ChannelType)};
     *
     * @param event   The {@link CommandReceivedEvent} to handle.
     * @param handler The {@link CommandHandler} that pushed the {@link CommandReceivedEvent}.
     */
    protected abstract void onCommandReceived(CommandReceivedEvent event, CommandHandler handler);

    /**
     * Used for identifying if a {@link CommandReceivedEvent} should be sent to the CommandListener.
     *
     * @param type The {@link ChannelType} to use.
     * @return True if the CommandListener uses the {@link ChannelType}. False otherwise. The default value is true.
     */
    public boolean usesChannel(ChannelType type) {
        return true;
    }

    /**
     * Used for accessing a description of the command this CommandListener listens handles.
     *
     * @return A {@link String} description of the command this CommandListener handles.
     */
    public abstract String getDescription();

    /**
     * Used for accessing receiving help for using the command this CommandListener listens handles.
     *
     * @return A {@link String} description of help for the command this CommandListener handles.
     */
    public abstract String getHelp();

}
