package com.discordbot;

import com.discordbot.command.CommandHandler;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.utils.SimpleLog;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;

/**
 * A singleton that handles the starting and stopping of the {@link JDA} as well as the adding and removing of {@link
 * EventListener}s.
 */
public class DiscordBot {

    private static final SimpleLog LOG = SimpleLog.getLog("DiscordBot");

    private static DiscordBot instance;

    private JDA jda;
    private CommandHandler commandHandler;
    private Map<Class<? extends EventListener>, EventListener> listeners;

    private boolean canRestart = true;
    private boolean running = false;

    /**
     * Default constructor is private for singleton class.
     */
    private DiscordBot() {
        listeners = new HashMap<>();
        commandHandler = new CommandHandler();
        addEventListener(commandHandler);
    }

    /**
     * Accessor for the DiscordBot instance.
     *
     * @return the DiscordBot instance.
     */
    public static synchronized DiscordBot getInstance() {
        if (instance == null) {
            instance = new DiscordBot();
        }
        return instance;
    }

    /**
     * Accessor for the {@link JDA}.
     *
     * @return the {@link JDA}.
     */
    public JDA getJDA() {
        return jda;
    }

    /**
     * Checks if the {@link JDA}.
     *
     * @return <tt>true</tt> if the {@link JDA} is running, <tt>false</tt> if the {@link JDA} was paused, shutdown, or
     * not yet initialized.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Starts the {@link JDA}.
     *
     * @param token The token to use for logging into Discord.
     * @return the DiscordBot instance.
     */
    public synchronized DiscordBot start(String token) {
        // if we shutdown and freed api, we can't restart it
        if (!canRestart) {
            throw new IllegalStateException("JDA completely shutdown, can't restart");
        }

        // make sure bot isn't running
        if (!running) {
            try {
                jda = new JDABuilder(AccountType.BOT).setToken(token).addListener(listeners.values().toArray()).buildBlocking();
                jda.setAutoReconnect(true);
                running = true;
            } catch (LoginException e) {
                LOG.warn("JDA login failed");
            } catch (IllegalArgumentException | InterruptedException | RateLimitedException e) {
                LOG.log(e);
            }
        } else {
            LOG.warn("JDA already running");
        }
        return this;
    }

    /**
     * Pauses the {@link JDA}.
     *
     * @return the DiscordBot instance.
     */
    public synchronized DiscordBot pause() {
        if (running) {
            jda.shutdown(false);
            running = false;
        }
        return this;
    }

    /**
     * Shuts down the {@link JDA}. The {@link JDA} cannot be restarted after shutdown has been called.
     *
     * @return the DiscordBot instance.
     */
    public synchronized DiscordBot shutdown() {
        if (jda != null && canRestart) {
            jda.shutdown();
            canRestart = false;
            running = false;
        }
        return this;
    }

    /**
     * Reboots the {@link JDA}
     *
     * @param token The token to use for logging into Discord.
     * @return the DiscordBot instance.
     */
    public synchronized DiscordBot reboot(String token) {
        if (running) {
            pause();
        }
        start(token);
        return this;
    }

    /**
     * Adds {@link EventListener}s to the DiscordBot and the {@link JDA} if it is running.
     *
     * @param listeners The {@link EventListener}s to add.
     * @return the DiscordBot instance.
     */
    public DiscordBot addEventListener(EventListener... listeners) {
        for (EventListener listener : listeners) {
            // will overwrite old listener, make sure its removed first
            if (this.listeners.containsKey(listener.getClass())) {
                removeEventListener(listener);
            }

            // add listener to jda if it is running
            if (isRunning()) {
                jda.addEventListener(listener);
            }
            this.listeners.put(listener.getClass(), listener);
        }
        return this;
    }

    /**
     * Removes {@link EventListener}s from the DiscordBot and the {@link JDA} if it is running.
     *
     * @param listeners The {@link EventListener}s to remove.
     * @return the DiscordBot instance.
     */
    public DiscordBot removeEventListener(EventListener... listeners) {
        for (EventListener listener : listeners) {
            if (isRunning()) {
                jda.removeEventListener(listener);
            }
            this.listeners.put(listener.getClass(), listener);
        }
        return this;
    }

    /**
     * Accessor for the {@link CommandHandler}.
     *
     * @return the {@link CommandHandler}.
     */
    public CommandHandler getCommandHandler() {
        return commandHandler;
    }

}
