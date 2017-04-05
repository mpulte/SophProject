package com.discordbot.command;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A {@link CommandListener} for handling the help command.
 *
 * @see CommandListener
 */
@Command(tag = "help", enabled = true)
public class HelpCommand extends CommandListener {

    /**
     * Handles any {@link CommandReceivedEvent}. Replies on the same {@link net.dv8tion.jda.core.entities.Channel} with
     * a list of {@link CommandListener} and their respective descriptions. If there are arguments, it lists the help
     * for the {@link CommandListener} passed as arguments.
     *
     * @param event   The {@link CommandReceivedEvent} to handle.
     * @param handler The {@link CommandHandler} that pushed the {@link CommandReceivedEvent}.
     */
    @Override
    public void onCommandReceived(CommandReceivedEvent event, CommandHandler handler) {
        MessageChannel channel = event.getMessageReceivedEvent().getChannel();

        if (handler == null) {
            channel.sendMessage("Unable to help at this time").queue();
        } else if (event.getArgs().isEmpty()) {
            Map<String, CommandListener> commands = handler.getCommandListeners();

            MessageBuilder builder = new MessageBuilder();
            List<String> keys = new LinkedList<>(commands.keySet());
            Collections.sort(keys);
            for (String key : keys) {
                builder.append(CommandReceivedEvent.PREFIX)
                        .append(key).append('\t')
                        .append(commands.get(key).getDescription())
                        .append('\n');
            }
            if (!builder.isEmpty()) {
                builder.replaceLast("\n", "");
                channel.sendMessage(builder.build()).queue();
            }
        } else {
            for (String argument : event.getArgs()) {
                if (handler.isTag(argument)) {
                    CommandListener command = handler.getCommandListeners().get(argument);
                    channel.sendMessage(
                            new MessageBuilder()
                                    .append(CommandReceivedEvent.PREFIX)
                                    .append(argument).append('\t')
                                    .append(command.getDescription())
                                    .append(" ")
                                    .append(command.getHelp())
                                    .build())
                            .queue();
                }
            }
        }
    }

    /**
     * Used for accessing a description of the HelpCommand.
     *
     * @return A {@link String} description of the HelpCommand.
     */
    @Override
    public String getDescription() {
        return "Lists available commands and their descriptions. Enter a command as an argument for additional help.";
    }

    /**
     * Used for receiving help using the HelpCommand.
     *
     * @return A {@link String} description of help for the HelpCommand.
     */
    @Override
    public String getHelp() {
        return "";
    }

}
