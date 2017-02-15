package com.discordbot.command;

public class CommandSetting {

    private Class<? extends CommandListener> cls;
    private String tag;
    private boolean enabled;

    @SuppressWarnings("unchecked cast")
    public CommandSetting(String className, String tag, boolean enabled) throws ClassNotFoundException {
        cls = (Class<? extends CommandListener>) Class.forName(className);
        this.tag = tag;
        this.enabled = enabled;
    }

    public CommandSetting(Class<? extends CommandListener> cls, String tag, boolean enabled) {
        this.cls = cls;
        this.tag = tag;
        this.enabled = enabled;
    }

    public Class<? extends CommandListener> getCls() {
        return cls;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String command) {
        this.tag = command;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

} // class CommandSetting
