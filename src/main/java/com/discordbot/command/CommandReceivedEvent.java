package com.discordbot.command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * An event fired for every tag received that is a wrapper class for a {@link MessageReceivedEvent}.
 */
public class CommandReceivedEvent {

    /**
     * The prefix for commands.
     */
    public static String PREFIX = "!";

    private MessageReceivedEvent event;
    private String tag;
    private List<String> args;

    /**
     * @param event The {@link MessageReceivedEvent} causing this CommandReceivedEvent.
     * @param tag   The tag identifying the command.
     * @param args  The arguments passed with the command.
     */
    private CommandReceivedEvent(MessageReceivedEvent event, String tag, List<String> args) {
        this.event = event;
        this.tag = tag;
        this.args = args;
    }

    /**
     * Builds a CommandReceivedEvent based on the {@link MessageReceivedEvent} provided. The {@link
     * MessageReceivedEvent} is not checked for the {@link CommandReceivedEvent#PREFIX}.
     *
     * @param event The {@link MessageReceivedEvent} to handle.
     * @return The resulting CommandReceivedEvent.
     */
    public static CommandReceivedEvent buildCommand(MessageReceivedEvent event) {
        String message = event.getMessage().getContent().replaceFirst(CommandReceivedEvent.PREFIX, "").trim();
        String command = "";
        List<String> args = new ArrayList<>();

        // split the message by spaces, the first String will be the tag, the rest are args
        Iterator<String> words = new ArrayList<>(Arrays.asList(message.split(" "))).iterator();
        if (words.hasNext()) {
            command = words.next();
        }
        while (words.hasNext()) {
            StringBuilder builder = new StringBuilder(words.next());
            String arg = builder.toString();
            while (words.hasNext() && arg.startsWith("\"") && !arg.endsWith("\"")) {
                builder.append(' ').append(words.next());
            }

            // if arg is surrounded in quotes, remove the quotes
            if (arg.startsWith("\"") && arg.length() > 1) {
                if (arg.endsWith("\"")) {
                    arg = arg.substring(1, arg.length() - 1);
                } else {
                    arg = arg.substring(1);
                }
            }

            args.add(arg);
        }
        return new CommandReceivedEvent(event, command, args);
    }

    /**
     * Accessor for {@link MessageReceivedEvent} that caused this CommandReceivedEvent.
     *
     * @return The {@link MessageReceivedEvent} that caused this CommandReceivedEvent.
     */
    public MessageReceivedEvent getMessageReceivedEvent() {
        return event;
    }

    /**
     * Accessor for the command's tag.
     *
     * @return The command's tag.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Accessor for the arguments passed with the command.
     *
     * @return The arguments passed with the command
     */
    public List<String> getArgs() {
        return args;
    }

}
