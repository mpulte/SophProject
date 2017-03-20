package com.discordbot.poll;

import java.util.HashMap;
import java.util.Map;

public class StrawPoll {

    private String question;
    private String[] options;
    private Map<String, Integer> responses;

    public StrawPoll(String question, String[] options) {
        this.question = question;
        this.options = options;
        responses = new HashMap<>();
    } // constructor

    public String getQuestion() {
        return question;
    } // method getOptions

    public String[] getOptions() {
        return options;
    } // method getOptions

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
    } // method putResponse

    @SuppressWarnings("WeakerAccess")
    public int totalResponses() {
        return responses.size();
    } // method totalResponses

    @SuppressWarnings("WeakerAccess")
    public int responseCount(int index) {
        int total = 0;
        for (int response : responses.values()) {
            if (response == index) {
                ++total;
            }
        }
        return total;
    } // method responseCount

    @SuppressWarnings("WeakerAccess")
    public double percent(int index) {
        return totalResponses() > 0 ? ((double) responseCount(index)) / ((double) totalResponses()) * 100d : 0;
    } // method percent

    public double[] percents() {
        double[] percents = new double[options.length];
        for (int i = 0; i < percents.length; i++) {
            percents[i] = percent(i);
        }
        return percents;
    } // method percents

    public String resultsAsString() {
        String results = "Poll Results: \n" + getQuestion() + "\n";
        for (int i = 0; i < getOptions().length; ++i) {
            results += "(" + i +")\t" + getOptions()[i] +
                    "\n\t\tResponses: " + responseCount(i) + " Percent: " + percent(i) + "\n";
        }
        results += "Total Responses: " + totalResponses();
        return results;
    } // method resultsAsString

    @Override
    public String toString() {
        String message = "Poll: \n" + getQuestion() + "\n";
        for (int i = 0; i < getOptions().length; ++i) {
            message += "(" + i +")\t" + getOptions()[i] + "\n";
        }
        message += "Total Responses: " + totalResponses();
        return message;
    } // method String
} // class StrawPoll
