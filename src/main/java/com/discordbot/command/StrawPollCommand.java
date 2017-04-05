package com.discordbot.command;

import com.discordbot.model.StrawPoll;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.utils.SimpleLog;

/**
 * A {@link CommandListener} for handling the straw poll command.
 *
 * @see CommandListener
 */
public class StrawPollCommand extends CommandListener {

    private static final SimpleLog LOG = SimpleLog.getLog("StrawPollCommand");

    private StrawPoll poll;
    private String channelId;

    /**
     * @param poll      The {@link StrawPoll} to post results to.
     * @param channelId The id of the {@link net.dv8tion.jda.core.entities.TextChannel} to use for polling.
     */
    public StrawPollCommand(StrawPoll poll, String channelId) {
        this.poll = poll;
        this.channelId = channelId;
    }

    /**
     * Handles any {@link CommandReceivedEvent}. Tallies responses to the poll on the specified {@link
     * net.dv8tion.jda.core.entities.TextChannel} or any {@link net.dv8tion.jda.core.entities.PrivateChannel}. Posts the
     * poll prompt if no argument is supplied.
     *
     * @param event   The {@link CommandReceivedEvent} to handle.
     * @param handler The {@link CommandHandler} that pushed the {@link CommandReceivedEvent}.
     */
    @Override
    public void onCommandReceived(CommandReceivedEvent event, CommandHandler handler) {
        MessageReceivedEvent source = event.getMessageReceivedEvent();
        User author = event.getMessageReceivedEvent().getAuthor();

        // if it is a text channel, it must be the one specified in the constructor
        if (source.getChannelType() == ChannelType.TEXT && !source.getChannel().getId().equals(channelId)) {
            return;
        }

        // if it is a text channel, mark the message for deletion to prevent spam
        if (source.getChannelType() == ChannelType.TEXT) {
            try {
                event.getMessageReceivedEvent().getMessage().delete().queue();
            } catch (PermissionException | IllegalStateException e) {
                LOG.warn(e.getMessage());
                return;
            }
        }

        try {
            // if the event has arguments try to parse the response, otherwise post the poll
            if (!event.getArgs().isEmpty()) {
                poll.putResponse(author.getId(), Integer.parseInt(event.getArgs().get(0)));
            } else {
                source.getChannel().sendMessage(poll.toString()).queue();
            }
        } catch (NumberFormatException e) {
            // if we don't have a private channel open, we will have to open a new one
            if (author.hasPrivateChannel()) {
                author.getPrivateChannel()
                        .sendMessage("Poll error: your response must be the number of the option").queue();
            } else {
                // best to do on new thread since we have to wait for private channel to be opened
                new Thread(() -> {
                    author.openPrivateChannel().complete();
                    author.getPrivateChannel()
                            .sendMessage("Poll error: your response must be the number of the option").queue();
                }).start();
            }
        } catch (IndexOutOfBoundsException e) {
            // if we don't have a private channel open, we will have to open a new one
            if (author.hasPrivateChannel()) {
                author.getPrivateChannel()
                        .sendMessage("Poll error: your choice was not a valid option").queue();
            } else {
                // best to do on new thread since we have to wait for private channel to be opened
                new Thread(() -> {
                    author.openPrivateChannel().complete();
                    author.getPrivateChannel()
                            .sendMessage("Poll error: your choice was not a valid option").queue();
                }).start();
            }
        }
    }

    /**
     * Used for identifying if a {@link CommandReceivedEvent} should be sent to the StrawPollCommand. The
     * StrawPollCommand only works on channels of type {@link ChannelType#TEXT} and {@link ChannelType#PRIVATE}.
     *
     * @param type The {@link ChannelType} to use.
     * @return True if the StrawPollCommand uses the {@link ChannelType}. False otherwise.
     */
    @Override
    public boolean usesChannel(ChannelType type) {
        return type == ChannelType.TEXT || type == ChannelType.PRIVATE;
    }

    /**
     * Used for accessing a description of the StrawPollCommand.
     *
     * @return A {@link String} description of the StrawPollCommand.
     */
    @Override
    public String getDescription() {
        return "Adds a response to a straw poll.";
    }

    /**
     * Used for receiving help for using the StrawPollCommand.
     *
     * @return A {@link String} description of help for the StrawPollCommand.
     */
    @Override
    public String getHelp() {
        return "Takes one argument, the number of the option you would like to select for the poll.";
    }

}
