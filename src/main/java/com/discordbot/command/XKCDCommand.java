package com.discordbot.command;

import com.discordbot.util.IOUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.impl.MessageEmbedImpl;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * A {@link CommandListener} for handling the wikipedia command.
 *
 * @see CommandListener
 */
@Command(tag = "xkcd")
public class XKCDCommand extends CommandListener {

    /**
     * Handles a {@link CommandReceivedEvent}. Replies on the same {@link net.dv8tion.jda.core.entities.Channel} with
     * an xkcd comic.
     *
     * @param event   The {@link CommandReceivedEvent} to handle.
     * @param handler The {@link CommandHandler} that pushed the {@link CommandReceivedEvent}.
     */
    @Override
    public void onCommandReceived(CommandReceivedEvent event, CommandHandler handler) {
        MessageChannel channel = event.getMessageReceivedEvent().getChannel();
        User author = event.getMessageReceivedEvent().getAuthor();

        // start the url
        StringBuilder urlBuilder = new StringBuilder().append("https://xkcd.com/");

        // if there is an arg, determine the selected comic
        if (!event.getArgs().isEmpty()) {
            if (NumberUtils.isDigits(event.getArgs().get(0))) {
                // append the url
                urlBuilder.append(event.getArgs().get(0)).append('/');
            } else {
                // the arg wasn't a number, send an error message
                channel.sendMessage(
                        new MessageBuilder()
                                .append(author.getAsMention())
                                .append(", just give me the comic number")
                                .build())
                        .queue();
                return;
            }
        }

        // finish the url
        urlBuilder.append("info.0.json");

        // going to have to connect to a website, so do this in a new thread
        new Thread(() -> {

            try {
                JSONObject comic = IOUtils.readJsonFromUrl(urlBuilder.toString());

                Integer comicNum = comic.getInt("num");
                String title = comic.getString("title");
                String altText = comic.getString("alt");
                String imageURL = comic.getString("img");
                String comicURL = "https://xkcd.com/" + comicNum + "/";

                // build the message
                channel.sendMessage(
                        new MessageEmbedImpl()
                                .setAuthor(new MessageEmbed.AuthorInfo("xkcd", "https://xkcd.com/",
                                        "https://xkcd.com/s/0b7742.png", "https://xkcd.com/s/0b7742.png"))
                                .setTitle(title)
                                .setUrl(comicURL)
                                .setImage(new MessageEmbed.ImageInfo(
                                        imageURL, imageURL, 1, 1))
                                .setFields(new ArrayList<>())
                                .setFooter(new MessageEmbed.Footer(altText, null, null)))
                        .queue();
            } catch (IOException | JSONException e) {
                channel.sendMessage(
                        new MessageBuilder()
                                .append(author.getAsMention())
                                .append(", that comic doesn't exist")
                                .build())
                        .queue();
            }
        }).start();
    }

    /**
     * Used for accessing a description of the CommandListener.
     *
     * @return A {@link String} description of the CommandListener.
     */
    @Override
    public String getDescription() {
        return "Loads an xkcd comic.";
    }

    /**
     * Used for receiving help for using the CommandListener.
     *
     * @return A {@link String} description of help for the CommandListener.
     */
    @Override
    public String getHelp() {
        return "Enter the comic number to pull a specific comic or leave it blank to pull the most recent comic.";
    }

}