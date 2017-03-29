package com.discordbot.command;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates a {@link CommandListener} that can be automatically loaded by {@link CommandLoader}
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Command {
    /**
     * @return the default tag for the Command
     */
    String tag();

    /**
     * @return the default enabled status for the Command
     */
    boolean enabled() default false;
}
