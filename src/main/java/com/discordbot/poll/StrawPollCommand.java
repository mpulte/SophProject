package com.discordbot.poll;

import com.discordbot.command.CommandHandler;
import com.discordbot.command.CommandListener;
import com.discordbot.command.CommandReceivedEvent;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class StrawPollCommand extends CommandListener {

    private StrawPoll poll;

    public StrawPollCommand(CommandHandler handler, StrawPoll poll) {
        super(handler);
        this.poll = poll;
    } // constructor

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        User author = event.getMessageReceivedEvent().getAuthor();
        try {
            if (!event.getArgs().isEmpty()) {
                poll.putResponse(author.getId(), Integer.parseInt(event.getArgs().get(0)));
            }
        } catch (NumberFormatException e) {
            MessageChannel channel = event.getMessageReceivedEvent().getChannel();
            if (channel.getType() == ChannelType.PRIVATE) {
                channel.sendMessage("Poll error: your response must be the number of the choice").queue();
            }
        }
    } // method onCommandReceivedEvent

    @Override
    public boolean usesChannel(ChannelType type) {
        return true;
    } // method useChannel

    @Override
    public String getDescription() {
        return "Adds a response to a straw poll";
    } // method getDescription

    @Override
    public String getHelp() {
        return ""; // TODO: add help
    } // method getHelp

} // class RollCommand
