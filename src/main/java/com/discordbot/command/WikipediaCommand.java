package com.discordbot.command;

import com.discordbot.util.Util;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class WikipediaCommand extends CommandListener {

    private static final SimpleLog LOG = SimpleLog.getLog("WikipediaCommand");

    public WikipediaCommand(CommandHandler handler) {
        super(handler);
    } // constructor

    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        MessageChannel channel = event.getMessageReceivedEvent().getChannel();
        User author = event.getMessageReceivedEvent().getAuthor();

        if (event.getArgs().isEmpty()) {
            channel.sendMessage("Silly " + author.getName() + ", you didn't give me a topic!");
            return;
        }

        String topic = String.join("%20", event.getArgs());

        try {
            // get pages with extracts for the topic
            String query = queryExtracts(topic);
            if (query == null) {
                // there is no page for the topic
                channel.sendMessage(
                        new MessageBuilder()
                                .append("Sorry ")
                                .append(author.getAsMention())
                                .append(", Wikipedia doesn't know what you're talking about")
                                .build())
                        .queue();
            } else if (query.endsWith(" refer to:") || query.endsWith(" refers to:")) {
                channel.sendMessage(query + "\n" + queryLinks(topic)).queue();
            } else if (query.length() >= 2000) {
                // max message length is 2000 characters, so we may have to split up the query
                for (String paragraph : query.split("\n")) {
                    while (paragraph.length() >= 2000) {
                        int cut = paragraph.lastIndexOf(" ", 2000);
                        channel.sendMessage(paragraph.substring(0, cut));
                        paragraph = paragraph.substring(cut + 1);
                    }
                    channel.sendMessage(paragraph).queue();
                }
            } else {
                channel.sendMessage(query).queue();
            }
        } catch (IOException e) {
            LOG.log(e);
        }
    } // method onCommandReceived

    private String queryExtracts(String topic) throws IOException {
        String url = "https://en.wikipedia.org/w/api.php?" +
                "format=json&action=query&redirects=true&prop=extracts&exintro=&explaintext=&titles=" + topic;
        JSONObject pages = Util.readJsonFromUrl(url).getJSONObject("query").getJSONObject("pages");
        int pageNumber = Integer.parseInt(pages.keys().next());
        return pageNumber >= 0 ? pages.getJSONObject(Integer.toString(pageNumber)).getString("extract") : null;
    } // method queryExtracts

    private String queryLinks(String topic) throws IOException {
        String url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=links&titles=" + topic;
        JSONObject pages = Util.readJsonFromUrl(url).getJSONObject("query").getJSONObject("pages");
        JSONArray links = pages.getJSONObject(pages.keys().next()).getJSONArray("links");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < links.length() - 1; i++) {
            try {
                builder.append(links.getJSONObject(i).getString("title")).append('\n');
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return builder.length() == 0 ? "" : builder.substring(0, builder.length() - 1);
    } // method queryLinks

    @Override
    public boolean usesChannel(ChannelType type) {
        return true;
    } // method useChannel

    @Override
    public String getDescription() {
        return "Searches Wikipedia for a topic.";
    } // method getDescription

    @Override
    public String getHelp() {
        return "The only argument is the topic to search.";
    } // method getHelp

} // WikipediaCommand
