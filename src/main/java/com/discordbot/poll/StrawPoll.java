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

    public void putResponse(String key, Integer choice) {
        // check for index out of bounds
        if (choice < 0 || choice >= options.length) {
            return;
        }

        // add or update response
        if (responses.containsKey(key)) {
            responses.replace(key, choice);
        } else {
            responses.put(key, choice);
        }
    } // method putResponse

    public double percent(int index) {
        if (responses.size() == 0) {
            return 0;
        }
        int total = 0;
        for (int response : responses.values()) {
            if (response == index) {
                ++total;
            }
        }
        return ((double)total) / ((double) responses.size()) * 100d;
    } // method percent

    public double[] percents() {
        double[] percents = new double[options.length];
        for (int i = 0; i < percents.length; i++) {
            percents[i] = percent(i);
        }
        return percents;
    } // method percents

} // class StrawPoll
