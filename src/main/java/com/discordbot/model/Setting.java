package com.discordbot.model;

public class Setting {

    private String key;
    private String value;

    public Setting(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Setting && ((Setting) obj).getKey().equals(key);
    }

}
