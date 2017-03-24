package com.discordbot.command;

import com.discordbot.model.StrawPoll;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class StrawPollCommand extends CommandListener {

    private StrawPoll poll;
    private String channelId;

    public StrawPollCommand(CommandHandler handler, StrawPoll poll, String channelId) {
        super(handler);
        this.poll = poll;
        this.channelId = channelId;
    } // constructor

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        MessageReceivedEvent source = event.getMessageReceivedEvent();
        User author = event.getMessageReceivedEvent().getAuthor();

        // if it is a text channel, it must be the one specified in the constructor
        if (source.getChannelType() == ChannelType.TEXT && !source.getChannel().getId().equals(channelId)) {
            return;
        }

        try {
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
                }).run();
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
                }).run();
            }
        }
    } // method onCommandReceivedEvent

    @Override
    public boolean usesChannel(ChannelType type) {
        return type == ChannelType.TEXT || type == ChannelType.PRIVATE;
    } // method useChannel

    @Override
    public String getDescription() {
        return "Adds a response to a straw poll.";
    } // method getDescription

    @Override
    public String getHelp() {
        return "Takes one argument, the number of the option you would like to select for the poll.";
    } // method getHelp

} // class RollCommand
