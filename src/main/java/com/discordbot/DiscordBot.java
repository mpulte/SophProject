package com.discordbot;

import com.discordbot.command.CommandHandler;
import com.discordbot.model.ProfanityFilter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.utils.SimpleLog;

import javax.security.auth.login.LoginException;
import java.util.*;

/**
 * A singleton that handles the starting and stopping of the {@link JDA} as well as the adding and removing of {@link
 * EventListener}s.
 */
public class DiscordBot {

    private static final SimpleLog LOG = SimpleLog.getLog("DiscordBot");

    private static DiscordBot instance;

    private final ProfanityFilter profanityFilter = new ProfanityFilter();
    private final CommandHandler commandHandler = new CommandHandler(profanityFilter);

    private final Map<Class<? extends EventListener>, EventListener> eventListeners = new HashMap<>();
    private final List<ChangeListener> changeListeners = new ArrayList<>();

    private JDA jda;

    private boolean canRestart = true;
    private boolean running = false;

    /**
     * Default constructor is private for singleton class.
     */
    private DiscordBot() {
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
     * Checks if the {@link JDA} is running.
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
                running = true;
                jda = new JDABuilder(AccountType.BOT).setToken(token).addListener(eventListeners.values().toArray()).buildBlocking();
                for (ChangeListener changeListener : changeListeners) {
                    changeListener.onStart();
                }
            } catch (LoginException e) {
                LOG.warn("JDA login failed");
                running = false;
            } catch (IllegalArgumentException | InterruptedException | RateLimitedException e) {
                LOG.log(e);
                running = false;
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
            for (ChangeListener changeListener : changeListeners) {
                changeListener.onStop();
            }
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
            for (ChangeListener changeListener : changeListeners) {
                changeListener.onStop();
            }
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
            if (this.eventListeners.containsKey(listener.getClass())) {
                removeEventListener(listener);
            }

            // add listener to jda if it is running
            if (isRunning()) {
                jda.addEventListener(listener);
            }
            this.eventListeners.put(listener.getClass(), listener);
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
            this.eventListeners.put(listener.getClass(), listener);
        }
        return this;
    }

    /**
     * Adds {@link ChangeListener}s to the DiscordBot.
     *
     * @param listeners The {@link ChangeListener}s to add.
     * @return the DiscordBot instance.
     */
    public DiscordBot addChangeListener(ChangeListener... listeners) {
        changeListeners.addAll(Arrays.asList(listeners));
        return this;
    }

    /**
     * Removes {@link ChangeListener}s from the DiscordBot.
     *
     * @param listeners The {@link ChangeListener}s to remove.
     * @return the DiscordBot instance.
     */
    public DiscordBot removeChangeListener(ChangeListener... listeners) {
        changeListeners.removeAll(Arrays.asList(listeners));
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

    /**
     * Accessor for the {@link ProfanityFilter}.
     *
     * @return the {@link ProfanityFilter}.
     */
    public ProfanityFilter getProfanityFilter() {
        return profanityFilter;
    }

    /**
     * Listener to push start and stop event to. These events occur after {@link JDABuilder#buildBlocking()} and
     * {@link JDA#shutdown(boolean)} have finished.
     */
    public interface ChangeListener {

        /**
         * Called after {@link JDABuilder#buildBlocking()} has finished.
         */
        void onStart();

        /**
         * Called after {@link JDA#shutdown(boolean)} has finished.
         */
        void onStop();

    }

}
