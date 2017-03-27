package com.discordbot.command;

import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.Random;

/**
 * A {@link CommandListener} for handling the eight ball command.
 *
 * @see CommandListener
 */
public class EightBallCommand extends CommandListener {

    private final String[] responses = {
            "The magic 8-ball says: As I see it, yes.",
            "The magic 8-ball says: Ask again later.",
            "The magic 8-ball says: Better not to tell you now.",
            "The magic 8-ball says: Cannot predict now.",
            "The magic 8-ball says: Concentrate and try again.",
            "The magic 8-ball says: Don't count on it.",
            "The magic 8-ball says: It is certain.",
            "The magic 8-ball says: It is decidedly so.",
            "The magic 8-ball says: Most likely.",
            "The magic 8-ball says: My reply is no.",
            "The magic 8-ball says: My sources say no.",
            "The magic 8-ball says: Outlook is good.",
            "The magic 8-ball says: Outlook is not so good.",
            "The magic 8-ball says: Reply hazy, try again.",
            "The magic 8-ball says: Signs point to yes.",
            "The magic 8-ball says: Very doubtful.",
            "The magic 8-ball says: Without a doubt.",
            "The magic 8-ball says: Yes.",
            "The magic 8-ball says: Yes, definitely.",
            "The magic 8-ball says: You may rely on it."
    };

    /**
     * @param handler The {@link CommandHandler} the EightBallCommand is bound to.
     */
    public EightBallCommand(CommandHandler handler) {
        super(handler);
    }

    /**
     * Handles any {@link CommandReceivedEvent}. Replies on the same {@link
     * net.dv8tion.jda.core.entities.Channel Channel} with a random fortune.
     *
     * @param event The {@link CommandReceivedEvent} to handle.
     */
    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        MessageChannel channel = event.getMessageReceivedEvent().getChannel();

        Random rand = new Random();
        String response = responses[rand.nextInt(responses.length)];

        channel.sendMessage(response).queue();
    }

    /**
     * Used for accessing a description of the EightBallCommand.
     *
     * @return A {@link String} description of the EightBallCommand.
     */
    @Override
    public String getDescription() {
        return "Tells you your fortune.";
    }

    /**
     * Used for accessing receiving help for using the EightBallCommand.
     *
     * @return A {@link String} description of help for the EightBallCommand.
     */
    @Override
    public String getHelp() {
        return "It's not that hard to use.";
    }

}
