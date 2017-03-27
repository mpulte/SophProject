package com.discordbot.command;

import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

import java.util.Random;

/**
 * A {@link CommandListener} for handling the roll command.
 *
 * @see CommandListener
 */
public class RollCommand extends CommandListener {

    private static final int DEFAULT = 100;

    /**
     * @param handler The {@link CommandHandler} the RollCommand is bound to.
     */
    public RollCommand(CommandHandler handler) {
        super(handler);
    }

    /**
     * Handles any {@link CommandReceivedEvent}. Replies on the same {@link net.dv8tion.jda.core.entities.Channel} with
     * a random number.
     *
     * @param event The {@link CommandReceivedEvent} to handle.
     */
    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        // event information
        User author = event.getMessageReceivedEvent().getAuthor();
        MessageChannel channel = event.getMessageReceivedEvent().getChannel();

        // initialize the random number
        Random rand = new Random();
        int roll;
        int mod = DEFAULT;

        // check for mod argument
        if (!event.getArgs().isEmpty()) {
            try {
                mod = Integer.parseInt(event.getArgs().get(0));
            } catch (NumberFormatException ex) {
                mod = DEFAULT;
            }
        }

        // calculate the roll
        roll = rand.nextInt(mod) + 1;

        // send the message
        channel.sendMessage(author.getName() + " rolled " + roll).queue();
    }

    /**
     * Used for accessing a description of the RollCommand.
     *
     * @return A {@link String} description of the RollCommand.
     */
    @Override
    public String getDescription() {
        return "Rolls a random number.";
    }

    /**
     * Used for accessing receiving help for using the RollCommand.
     *
     * @return A {@link String} description of help for the RollCommand.
     */
    @Override
    public String getHelp() {
        return "The number is between 1 and n, where n is the argument supplied (n = " + DEFAULT + " by default).";
    }

}