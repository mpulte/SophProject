package com.discordbot.util;

import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

public class ProfanityFilterListener extends ListenerAdapter {

    public static final String SETTING_REPLY_GUILD = "profanity_filter_reply_guild"; // TODO: Save setting
    public static final String SETTING_REPLY_PRIVATE = "profanity_filter_reply_private"; // TODO: Save setting

    private ProfanityFilter filter;

    public ProfanityFilterListener(ProfanityFilter filter) {
        this.filter = filter;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannelType().equals(ChannelType.TEXT)) {
            Message message = event.getMessage();
            User author = event.getAuthor();

            List<String> words = filter.filter(message.getContent());
            if (!words.isEmpty()) {
                System.out.println("Profanity Found");
                message.delete().queue();

                event.getChannel().sendMessage(
                        new MessageBuilder()
                                .append("Message from ")
                                .append(author.getAsMention())
                                .append(" deleted because it was naughty")
                                .build())
                        .queue();

                new Thread(() -> {
                    MessageBuilder messageBuilder = new MessageBuilder();
                    messageBuilder.append(words.size() > 1 ? "The words " : "The word ");
                    words.forEach(word -> messageBuilder.append(word).append(" "));
                    messageBuilder
                            .append(words.size() > 1 ? "are " : "is ")
                            .append("not allowed in channel ")
                            .append(event.getChannel().getName())
                            .append(" on ")
                            .append(event.getGuild().getName());

                    author.openPrivateChannel().complete();
                    author.getPrivateChannel().sendMessage(messageBuilder.build()).queue();
                }).run();
            }
        }
    }

}
