package com.discordbot.command;

import com.discordbot.poll.StrawPoll;
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
        // if it is a text channel, it must be the one specified in the constructor
        MessageReceivedEvent source = event.getMessageReceivedEvent();
        if (source.getChannelType() == ChannelType.TEXT && !source.getChannel().getId().equals(channelId)) {
            return;
        }

        User author = event.getMessageReceivedEvent().getAuthor();
        try {
            if (!event.getArgs().isEmpty()) {
                poll.putResponse(author.getId(), Integer.parseInt(event.getArgs().get(0)));
            } else {
                source.getChannel().sendMessage(poll.toString()).queue();
            }
        } catch (NumberFormatException e) {
            event.getMessageReceivedEvent().getAuthor().getPrivateChannel()
                    .sendMessage("Poll error: your response must be the number of the option").queue();
        } catch (IndexOutOfBoundsException e) {
            event.getMessageReceivedEvent().getAuthor().getPrivateChannel()
                    .sendMessage("Poll error: your choice was not a valid option").queue();
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
