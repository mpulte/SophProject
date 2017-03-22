package com.discordbot.util;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.SimpleLog;

public class MessageListener extends ListenerAdapter {

    private static final SimpleLog LOG = SimpleLog.getLog("Message");

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.isFromType(ChannelType.PRIVATE)) {
            LOG.info(String.format("[PM] %s: %s",
                    event.getAuthor().getName(),
                    event.getMessage().getContent()));
        } else {
            LOG.info(String.format("[%s][%s] %s: %s",
                    event.getGuild().getName(),
                    event.getTextChannel().getName(),
                    event.getMember().getEffectiveName(),
                    event.getMessage().getContent()));
        }
    } // method onMessageReceived

} // class MessageListener