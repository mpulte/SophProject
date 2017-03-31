package com.discordbot.command;

import com.discordbot.util.IOUtils;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A {@link CommandListener} for handling the eight ball command.
 *
 * @see CommandListener
 */
@Command(tag = "8ball")
public class EightBallCommand extends CommandListener {

    private static final SimpleLog LOG = SimpleLog.getLog("EightBallCommand");

    private static final String[] defaultResponses = {
            "As I see it, yes.",
            "Ask again later.",
            "Better not to tell you now.",
            "Cannot predict now.",
            "Concentrate and try again.",
            "Don't count on it.",
            "It is certain.",
            "It is decidedly so.",
            "Most likely.",
            "My reply is no.",
            "My sources say no.",
            "Outlook is good.",
            "Outlook is not so good.",
            "Reply hazy, try again.",
            "Signs point to yes.",
            "Very doubtful.",
            "Without a doubt.",
            "Yes.",
            "Yes, definitely.",
            "You may rely on it."
    };

    private final List<String> responses = new ArrayList<>();

    /**
     * @param handler The {@link CommandHandler} the EightBallCommand is bound to.
     */
    public EightBallCommand(CommandHandler handler) {
        super(handler);

        Path path = IOUtils.getResourcePath("command", "8ball", "fortunes.txt");
        try {
            // try to lead the response list from the file
            responses.addAll(Files.readAllLines(path));

            // make sure file wasn't empty
            if (responses.isEmpty()) {
                responses.addAll(Arrays.asList(defaultResponses));
                LOG.warn("fortunes file " + path.toFile() + " was empty, using default fortunes");
            }
        } catch (IOException e1) {
            // failed to open file, so create it and load it with the default responses
            try {
                Path directoryPath = IOUtils.getResourcePath("command", "8ball");
                if (directoryPath.toFile().exists() || directoryPath.toFile().mkdirs()) {
                    Files.write(path, Arrays.asList(defaultResponses));
                } else {
                    throw new IOException("Unable to make directory " + directoryPath.toString());
                }
            } catch (IOException e2) {
                // we couldn't open the directory or create the file for some reason
                LOG.log(e2);
            }
            responses.addAll(Arrays.asList(defaultResponses));
        }
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
        String response = responses.get(rand.nextInt(responses.size()));

        channel.sendMessage("The magic 8-ball says: " + response).queue();
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
