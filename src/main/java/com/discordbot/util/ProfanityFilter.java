package com.discordbot.util;

import java.util.ArrayList;
import java.util.List;

public class ProfanityFilter {

    List<String> blackList = new ArrayList<>();

    public ProfanityFilter() {
        blackList.add("fuck");
        blackList.add("shit");
        blackList.add("damn");
    }

    public List<String> filter(String s) {
        List<String> badWords = new ArrayList<>();
        String[] words = s.split("\\W+");
        for (String word : words) {
            for (String wordBL : blackList) {
                if (wordBL.equalsIgnoreCase(word)) {
                    badWords.add(word.toLowerCase());
                }
            }
        }
        return badWords;
    }

}
