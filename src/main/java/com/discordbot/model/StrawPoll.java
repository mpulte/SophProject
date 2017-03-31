package com.discordbot.model;

import java.util.HashMap;
import java.util.Map;

/**
 * A StrawPoll that tracks responses to a prompt with options.
 */
public class StrawPoll {

    private String prompt;
    private String[] options;
    private Map<String, Integer> responses;

    /**
     * @param prompt  the StrawPoll's prompt.
     * @param options the StrawPoll's options.
     */
    public StrawPoll(String prompt, String[] options) {
        this.prompt = prompt;
        this.options = options;
        responses = new HashMap<>();
    }

    /**
     * Accessor for the StrawPoll's prompt.
     *
     * @return the StrawPoll's prompt.
     */
    public String getPrompt() {
        return prompt;
    }

    /**
     * Accessor for the StrawPoll's options.
     *
     * @return the StrawPoll's options.
     */
    public String[] getOptions() {
        return options;
    }

    /**
     * Adds or updates a response to the StrawPoll.
     *
     * @param key    A key to track the responder.
     * @param choice The responder's choice.
     * @throws IndexOutOfBoundsException if the choice is not within the bounds of the options.
     */
    public void putResponse(String key, Integer choice) throws IndexOutOfBoundsException {
        // check for index out of bounds
        if (choice < 0 || choice >= options.length) {
            throw new IndexOutOfBoundsException("Choice " + choice + " is not valid");
        }

        // add or update response
        if (responses.containsKey(key)) {
            responses.replace(key, choice);
        } else {
            responses.put(key, choice);
        }
    }

    /**
     * Calculates the total number of responses.
     *
     * @return the total number of responses.
     */
    public int totalResponses() {
        return responses.size();
    }

    /**
     * Calculates the number of responses for a given option.
     *
     * @param index The index of the option.
     * @return the number of responses for the given option.
     */
    public int responseCount(int index) {
        int total = 0;
        for (int response : responses.values()) {
            if (response == index) {
                ++total;
            }
        }
        return total;
    }

    /**
     * Calculates the percent of responses for a given option.
     *
     * @param index The index of the option.
     * @return the percent of responses for the given option.
     */
    public double percent(int index) {
        return totalResponses() > 0 ? ((double) responseCount(index)) / ((double) totalResponses()) * 100d : 0;
    }

    /**
     * Calculates the percent of responses for each option.
     *
     * @return an array containing the percent of responses for each option.
     */
    public double[] percents() {
        double[] percents = new double[options.length];
        for (int i = 0; i < percents.length; i++) {
            percents[i] = percent(i);
        }
        return percents;
    }

    /**
     * Builds a {@link String} containing the prompt, options, response counts, and response percents.
     *
     * @return the results of the poll as a {@link String}, including the prompt, options, response counts, and response
     * percents.
     */
    public String resultsAsString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Poll Results: \n")
                .append(getPrompt())
                .append('\n');
        for (int i = 0; i < getOptions().length; ++i) {
            builder.append('(')
                    .append(i)
                    .append(")\t")
                    .append(getOptions()[i])
                    .append("\n\t\tResponses: ")
                    .append(responseCount(i))
                    .append(" Percent: ")
                    .append(percent(i))
                    .append("\n");
        }
        builder.append("Total Responses: ")
                .append(totalResponses());
        return builder.toString();
    }

    /**
     * @return a description of the poll, including the prompt and options.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Poll: \n")
                .append(getPrompt())
                .append('\n');
        for (int i = 0; i < getOptions().length; ++i) {
            builder.append("(")
                    .append(i)
                    .append(")\t")
                    .append(getOptions()[i])
                    .append("\n");
        }
        builder.append("Total Responses: ")
                .append(totalResponses());
        return builder.toString();
    }

}
