package com.discordbot.model;

import java.util.*;

public class ProfanityFilter {

    private Set<String> blackList = new LowerCaseTreeSet();
    private ChangeListener listener = null;

    public ProfanityFilter() {
    }

    public Collection<String> filter(String s) {
        Collection<String> badWords = new ArrayList<>();
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

    public ProfanityFilter add(String...words) {
        blackList.addAll(Arrays.asList(words));
        if (listener != null) {
            listener.onChange(this, ChangeType.ADD, words);
        }
        return this;
    }

    public ProfanityFilter remove(String...words) {
        blackList.removeAll(Arrays.asList(words));
        if (listener != null) {
            listener.onChange(this, ChangeType.REMOVE, words);
        }
        return this;
    }

    public List<String> asList() {
        return new ArrayList<>(blackList);
    }

    public ProfanityFilter setChangeListener(ChangeListener listener) {
        this.listener = listener;
        return this;
    }

    public interface ChangeListener {
        void onChange(ProfanityFilter filter, ChangeType type, String...words);
    }

    public enum ChangeType { ADD, REMOVE }

    private class LowerCaseTreeSet extends TreeSet<String> {

        LowerCaseTreeSet() {
            super(String.CASE_INSENSITIVE_ORDER);
        }

        @Override
        public boolean add(String s) {
            return super.add(s.toLowerCase());
        }

        @Override
        public boolean addAll(Collection<? extends String> c) {
            boolean changed = false;
            for (String s : c) {
                changed = add(s) || changed;
            }
            return changed;
        }
    }

}
