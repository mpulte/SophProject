package com.discordbot.command;

/**
 * A wrapper class for holding the {@link Class}, tag, and enabled status of a {@link CommandListener}.
 */
public class CommandSetting {

    private Class<? extends CommandListener> cls;
    private String tag;
    private boolean enabled;

    /**
     * @param className A {@link String} representation of a {@link Class} extending {@link CommandListener}.
     * @param tag       The tag identifying the {@link CommandListener}.
     * @param enabled   The enabled status of the {@link CommandListener}.
     * @throws ClassNotFoundException if no class definition can be found for ClassName.
     */
    @SuppressWarnings("unchecked cast")
    public CommandSetting(String className, String tag, boolean enabled) throws ClassNotFoundException {
        cls = (Class<? extends CommandListener>) Class.forName(className);
        this.tag = tag;
        this.enabled = enabled;
    }

    /**
     * @param cls     The {@link Class Class} of the {@link CommandListener}.
     * @param tag     The tag identifying the {@link CommandListener}.
     * @param enabled The enabled status of the {@link CommandListener}.
     */
    public CommandSetting(Class<? extends CommandListener> cls, String tag, boolean enabled) {
        this.cls = cls;
        this.tag = tag;
        this.enabled = enabled;
    }

    /**
     * Accessor for the {@link Class} of the {@link CommandListener}.
     *
     * @return The {@link Class} of the {@link CommandListener}.
     */
    public Class<? extends CommandListener> getCls() {
        return cls;
    }

    /**
     * Accessor for the tag identifying the {@link CommandListener}.
     *
     * @return The tag identifying the {@link CommandListener}.
     */
    public String getTag() {
        return tag;
    }

    /**
     * Mutator for the tag identifying the {@link CommandListener}.
     *
     * @param tag The tag identifying the {@link CommandListener}.
     */
    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * Accessor for the enabled status of the {@link CommandListener}.
     *
     * @return The enabled status of the {@link CommandListener}.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Mutator for the enabled status of the {@link CommandListener}.
     *
     * @param enabled The enabled status of the {@link CommandListener}.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Checks if an {@link Object} is a CommandSetting with the same {@link Class} as this CommandSetting.
     *
     * @param obj the {@link Object} to check.
     * @return <tt>true</tt> if the {@link Object} is a CommandSetting with the same {@link Class} as this
     * CommandSetting, otherwise <tt>false</tt>.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof CommandSetting && ((CommandSetting) obj).cls.equals(cls);
    }
}
