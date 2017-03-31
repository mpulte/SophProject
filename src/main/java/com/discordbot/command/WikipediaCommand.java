package com.discordbot.command;

import com.discordbot.util.IOUtils;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.utils.SimpleLog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A {@link CommandListener} for handling the wikipedia command.
 *
 * @see CommandListener
 */
@Command(tag = "wiki")
public class WikipediaCommand extends CommandListener {

    private static final SimpleLog LOG = SimpleLog.getLog("WikipediaCommand");

    /**
     * @param handler The {@link CommandHandler} the CommandListener is bound to.
     */
    public WikipediaCommand(CommandHandler handler) {
        super(handler);
    }

    /**
     * Handles any {@link CommandReceivedEvent}. Replies on the same {@link net.dv8tion.jda.core.entities.Channel} with
     * an extract from Wikipedia. If there are multiple pages with the same title, a list will be sent instead.
     *
     * @param event The {@link CommandReceivedEvent} to handle.
     */
    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        MessageChannel channel = event.getMessageReceivedEvent().getChannel();
        User author = event.getMessageReceivedEvent().getAuthor();

        if (event.getArgs().isEmpty()) {
            channel.sendMessage(
                    new MessageBuilder()
                            .append("Silly ")
                            .append(author)
                            .append(", you didn't give me a topic!")
                            .build())
                    .queue();
            return;
        }

        String topic = String.join("%20", event.getArgs());

        // going to have to connect to a website, so do this in a new thread
        new Thread(() -> {
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
        }).start();
    }

    /**
     * Performs a query for the extract from the Wikipedia page corresponding to the topic provided.
     *
     * @param topic The topic to query for.
     * @return A {@link String} containing an extract from the Wikipedia page corresponding to the topic. Returns
     * <tt>null</tt> if no page is found.
     * @throws IOException if Wikipedia cannot be reached.
     */
    private String queryExtracts(String topic) throws IOException {
        String url = "https://en.wikipedia.org/w/api.php?" +
                "format=json&action=query&redirects=true&prop=extracts&exintro=&explaintext=&titles=" + topic;
        JSONObject pages = IOUtils.readJsonFromUrl(url).getJSONObject("query").getJSONObject("pages");
        int pageNumber = Integer.parseInt(pages.keys().next());
        return pageNumber >= 0 ? pages.getJSONObject(Integer.toString(pageNumber)).getString("extract") : null;
    }

    /**
     * Performs a query for the links on the Wikipedia page corresponding to the topic provided. It is used to query all
     * possible pages where a naming conflict occurs.
     *
     * @param topic The topic to query for.
     * @return A {@link String} containing a list of all links on a page except for the last one.
     * @throws IOException if Wikipedia cannot be reached.
     */
    private String queryLinks(String topic) throws IOException {
        String url = "https://en.wikipedia.org/w/api.php?format=json&action=query&prop=links&titles=" + topic;
        JSONObject pages = IOUtils.readJsonFromUrl(url).getJSONObject("query").getJSONObject("pages");
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
    }

    /**
     * Used for accessing a description of the CommandListener.
     *
     * @return A {@link String} description of the CommandListener.
     */
    @Override
    public String getDescription() {
        return "Searches Wikipedia for a topic.";
    }

    /**
     * Used for accessing receiving help for using the CommandListener.
     *
     * @return A {@link String} description of help for the CommandListener.
     */
    @Override
    public String getHelp() {
        return "The only argument is the topic to search.";
    }

}
