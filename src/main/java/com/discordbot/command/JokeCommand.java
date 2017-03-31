package com.discordbot.command;

import com.discordbot.util.IOUtils;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * A {@link CommandListener} for handling the joke command.
 *
 * @see CommandListener
 */
@Command(tag = "joke")
public class JokeCommand extends CommandListener {

    private static final SimpleLog LOG = SimpleLog.getLog("JokeCommand");

    private static final String[] defaultJokes = {
            "What's red and bad for your teeth? A brick.",
            "Did you hear about the soldier who survived mustard gas and pepper spray? He's a seasoned veteran.",
            "What is the difference between a poorly dressed man on a tricycle and a well dressed man on a bicycle? Attire.",
            "Did you hear the joke about the scarecrow that got promoted? He was outstanding in his field.",
            "What's Irish and stays out all night? Patty O'Furniture.",
            "How do you make holy water? Put it in a pot and boil the hell out if it.",
            "A blind man walks into a bar. And a table. And a chair.",
            "Why do trees seem suspicious on sunny days? Because they're so shady.",
            "What do you call an elephant that doesn't matter? An irrelephant.",
            "What do you call a cow with no legs? Ground beef.",
            "What do you call a cow with two legs? Your mom.",
            "What is the resemblance between a green apple and a red apple? They�re both red except for the green one.",
            "What do you call a dog that does magic tricks? A labracadabrador.",
            "Why are communism jokes funny? Because everyone gets them.",
            "What did the one lawyer say to the other lawyer? 'We're both lawyers.'",
            "What starts with 'E', ends with 'E', and only has one letter in it? Envelope.",
            "Why did the can crusher quit his job? Because it was soda pressing.",
            "If you have 10 apples in one hand, and 13 oranges in the other, what do you have? Big hands.",
            "What's the first thing that clouds do when they get rich? They make it rain.",
            "What do you call a cow that just gave birth? Decalfinated."
    };

    private final List<String> jokes = new ArrayList<>();

    /**
     * Constructor initializes the jokes list.
     */
    public JokeCommand() {
        Path path = IOUtils.getResourcePath("command", "joke", "jokes.txt");
        try {
            // try to lead the response list from the file
            jokes.addAll(Files.readAllLines(path));

            // make sure file wasn't empty
            if (jokes.isEmpty()) {
                jokes.addAll(Arrays.asList(defaultJokes));
                LOG.warn("jokes file " + path.toFile() + " was empty, using default jokes");
            }
        } catch (IOException e1) {
            // failed to open file, so create it and load it with the default responses
            try {
                Path directoryPath = IOUtils.getResourcePath("command", "joke");
                if (directoryPath.toFile().exists() || directoryPath.toFile().mkdirs()) {
                    Files.write(path, Arrays.asList(defaultJokes));
                } else {
                    throw new IOException("Unable to make directory " + directoryPath.toString());
                }
            } catch (IOException e2) {
                // we couldn't open the directory or create the file for some reason
                LOG.log(e2);
            }
            jokes.addAll(Arrays.asList(defaultJokes));
        }
    }

    /**
     * Handles any {@link CommandReceivedEvent}. Replies on the same {@link net.dv8tion.jda.core.entities.Channel} with
     * a random joke.
     *
     * @param event   The {@link CommandReceivedEvent} to handle.
     * @param handler The {@link CommandHandler} that pushed the {@link CommandReceivedEvent}.
     */
    @Override
    public void onCommandReceived(CommandReceivedEvent event, CommandHandler handler) {
        MessageChannel channel = event.getMessageReceivedEvent().getChannel();

        Random rand = new Random();
        String joke = jokes.get(rand.nextInt(jokes.size()));

        channel.sendMessage(joke).queue();
    }

    /**
     * Used for accessing a description of the JokeCommand.
     *
     * @return A {@link String} description of the JokeCommand.
     */
    @Override
    public String getDescription() {
        return "Offers a joke.";
    }

    /**
     * Used for receiving help for using the JokeCommand.
     *
     * @return A {@link String} description of help for the JokeCommand.
     */
    @Override
    public String getHelp() {
        return "It's not that hard to use.";
    }

}