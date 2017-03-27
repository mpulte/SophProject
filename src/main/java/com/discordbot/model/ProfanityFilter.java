package com.discordbot.model;

import java.util.*;

/**
 * A profanity filter containing a blacklist of words to filter out of messages.
 */
public class ProfanityFilter {

    private Set<String> blacklist = new LowerCaseTreeSet();
    private ChangeListener listener = null;

    /**
     * Filters a {@link String} for words in the blacklist.
     *
     * @param s The {@link String} to filter.
     * @return a {@link List<String>} of words from the blacklist found in the provided {@link String}. Returns an empty
     * {@link List<String>} if no words were found.
     */
    public List<String> filter(String s) {
        List<String> badWords = new ArrayList<>();
        for (String word : s.split("\\W+")) {
            for (String wordBL : blacklist) {
                if (wordBL.equalsIgnoreCase(word)) {
                    badWords.add(word.toLowerCase());
                }
            }
        }
        return badWords;
    }

    /**
     * Adds one or more words to the blacklist.
     *
     * @param words The words to add.
     * @return a reference to this ProfanityFilter.
     */
    public ProfanityFilter add(String... words) {
        blacklist.addAll(Arrays.asList(words));
        if (listener != null) {
            listener.onChange(this, ChangeType.ADD, words);
        }
        return this;
    }

    /**
     * Removes one or more words from the blacklist.
     *
     * @param words The words to remove.
     * @return a reference to this ProfanityFilter.
     */
    public ProfanityFilter remove(String... words) {
        blacklist.removeAll(Arrays.asList(words));
        if (listener != null) {
            listener.onChange(this, ChangeType.REMOVE, words);
        }
        return this;
    }

    /**
     * Accesses the blacklist.
     *
     * @return the blacklist.
     */
    public List<String> asList() {
        return new ArrayList<>(blacklist);
    }

    /**
     * Sets the {@link ChangeListener} for handling changes to the blacklist.
     *
     * @param listener The {@link ChangeListener} to set.
     * @return a reference to this ProfanityFilter.
     */
    public ProfanityFilter setChangeListener(ChangeListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * The types of changes to the blacklist that can occur.
     */
    public enum ChangeType {
        ADD, REMOVE
    }

    /**
     * Handles changes to the blacklist.
     */
    public interface ChangeListener {
        /**
         * Called when changes to the blacklist occur.
         *
         * @param filter The {@link ProfanityFilter} that changed.
         * @param type   The {@link ChangeType} that occurred.
         * @param words  The word or words that changed.
         */
        void onChange(ProfanityFilter filter, ChangeType type, String... words);
    }

    /**
     * A {@link TreeSet<String>} that converts all {@link String}s to lower case.
     *
     * @see TreeSet
     */
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
