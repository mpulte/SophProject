package com.discordbot.model;

/**
 * A setting containing a key and a value.
 */
public class Setting {

    private String key;
    private String value;

    /**
     * @param key   The Setting's key.
     * @param value The Setting's value.
     */
    public Setting(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * Accesses the Setting's key.
     *
     * @return the Setting's key.
     */
    public String getKey() {
        return key;
    }

    /**
     * Accesses the Setting's value.
     *
     * @return the Setting's value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Checks an {@link Object} is a Setting with the same key as this Setting.
     *
     * @param obj The {@link Object} to check.
     * @return <tt>true</tt> if the {@link Object} is a Setting with the same key as this Setting, otherwise
     * <tt>false</tt>.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Setting && ((Setting) obj).key.equals(key);
    }

}
