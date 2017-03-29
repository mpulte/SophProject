package com.discordbot.command;

import net.dv8tion.jda.core.entities.ChannelType;

/**
 * {@link CommandHandler} pushes {@link CommandReceivedEvent} to any registered CommandListener.
 */
public abstract class CommandListener {

    private CommandHandler handler;

    /**
     * @param handler The {@link CommandHandler} the CommandListener is bound to.
     */
    public CommandListener(CommandHandler handler) {
        this.handler = handler;
    }

    /**
     * Accessor for the {@link CommandHandler} field.
     *
     * @return The {@link CommandHandler} the CommandListener is bound to.
     */
    public CommandHandler getHandler() {
        return handler;
    }

    /**
     * Accessor for the {@link CommandHandler} field.
     *
     * @param handler The {@link CommandHandler} the CommandListener is bound to.
     */
    public void setHandler(CommandHandler handler) {
        this.handler = handler;
    }

    /**
     * Handles any {@link CommandReceivedEvent}.
     *
     * @param event The {@link CommandReceivedEvent} to handle.
     */
    public abstract void onCommandReceived(CommandReceivedEvent event);

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
