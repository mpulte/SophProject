package com.discordbot.util;

import com.discordbot.command.CommandListener;
import com.discordbot.command.CommandReceivedEvent;
import com.discordbot.model.ProfanityFilter;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.SimpleLog;

import java.security.InvalidKeyException;
import java.util.List;

/**
 * An implementation of {@link ListenerAdapter} that filters {@link MessageReceivedEvent}s for profanity and removes
 * offending {@link Message}s.
 *
 * @see ListenerAdapter
 */
public class ProfanityFilterListener extends ListenerAdapter {

    /**
     * The key to use for the profanity filter enabled setting
     */
    public static final String SETTING_ENABLED = "profanity_filter_enabled";
    /**
     * The key to use for the profanity filter reply guild setting
     */
    public static final String SETTING_REPLY_GUILD = "profanity_filter_reply_guild";
    /**
     * The key to use for the profanity filter reply private setting
     */
    public static final String SETTING_REPLY_PRIVATE = "profanity_filter_reply_private";

    private static final SimpleLog LOG = SimpleLog.getLog("ProfanityFilterListener");

    private ProfanityFilter filter;

    /**
     * @param filter The {@link ProfanityFilter} to use for filtering messages.
     */
    public ProfanityFilterListener(ProfanityFilter filter) {
        this.filter = filter;
    }

    /**
     * Handles a {@link MessageReceivedEvent} by creating a {@link CommandReceivedEvent} and pushing it to the
     * registered {@link CommandListener}s.
     *
     * @param event The {@link MessageReceivedEvent} to handle.
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getChannelType().equals(ChannelType.TEXT)) {
            Message message = event.getMessage();
            User author = event.getAuthor();

            List<String> words = filter.filter(message.getContent());
            if (!words.isEmpty()) {
                // delete the message
                try {
                    message.delete().queue();
                } catch (PermissionException | IllegalStateException e) {
                    LOG.warn(e.getMessage());
                    return;
                }

                // reply in guild if enabled
                try {
                    if (SettingHandler.getBoolean(SETTING_REPLY_GUILD)) {
                        event.getChannel().sendMessage(
                                new MessageBuilder()
                                        .append("Message from ")
                                        .append(author)
                                        .append(" deleted because it was naughty")
                                        .build())
                                .queue();
                    }
                } catch (InvalidKeyException e) {
                    SettingHandler.setBoolean(SETTING_REPLY_GUILD, false);
                }

                // reply in private if enabled
                try {
                    if (SettingHandler.getBoolean(SETTING_REPLY_PRIVATE)) {
                        MessageBuilder messageBuilder = new MessageBuilder();
                        messageBuilder.append(words.size() > 1 ? "The words " : "The word ");
                        words.forEach(word -> messageBuilder.append(word).append(" "));
                        messageBuilder
                                .append(words.size() > 1 ? "are " : "is ")
                                .append("not allowed in channel ")
                                .append(event.getChannel().getName())
                                .append(" on ")
                                .append(event.getGuild().getName());

                        // if we don't have a private channel open, we will have to open a new one
                        if (author.hasPrivateChannel()) {
                            author.getPrivateChannel().sendMessage(messageBuilder.build()).queue();
                        } else {
                            // best to do on new thread since we have to wait for private channel to be opened
                            new Thread(() -> {
                                author.openPrivateChannel().complete();
                                author.getPrivateChannel().sendMessage(messageBuilder.build()).queue();
                            }).run();
                        }
                    }
                } catch (InvalidKeyException e) {
                    SettingHandler.setBoolean(SETTING_REPLY_PRIVATE, false);
                }
            }
        }
    }

}
