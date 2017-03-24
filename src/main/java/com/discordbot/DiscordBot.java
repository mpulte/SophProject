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

public class DiscordBot {

	private static final SimpleLog LOG = SimpleLog.getLog("DiscordBot");

	private static DiscordBot instance;

	private JDA jda;
	private CommandHandler commandHandler;

    private Map<Class<? extends EventListener>, EventListener> listeners;
    private boolean canRestart;
	
	private DiscordBot() {
	    listeners = new HashMap<>();
	    canRestart = true;
	    commandHandler = new CommandHandler();
	    addEventListener(commandHandler);
	}
	
	public static synchronized DiscordBot getInstance() {
		if (instance == null) {
			instance = new DiscordBot();
		}
		return instance;
	}

	public JDA getJDA() {
		return jda;
	} // method getInstance

    public synchronized boolean isRunning() {
	    return jda != null
                && jda.getStatus() != JDA.Status.SHUTDOWN
                && jda.getStatus() != JDA.Status.SHUTTING_DOWN;
    }
	
	public synchronized DiscordBot start(String token) {
	    // if we shutdown and freed api, we can't restart it
	    if (!canRestart) {
	        throw new IllegalStateException("JDA completely shutdown, can't restart");
        }

        // make sure bot isn't running
		if (!isRunning()) {
			try {
				jda = new JDABuilder(AccountType.BOT).setToken(token).addListener(listeners.values().toArray()).buildBlocking();
				jda.setAutoReconnect(true);
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

	public synchronized DiscordBot pause() {
		if (isRunning()) {
			jda.shutdown(false);
		}
		return this;
	}

	public synchronized DiscordBot shutdown() {
		if (jda != null && canRestart) {
			jda.shutdown();
            canRestart = false;
		}
		return this;
	}

	public synchronized DiscordBot reboot(String token) {
		if (jda != null) {
			jda.shutdown(false);
		}
		start(token);
		return this;
	}

    public DiscordBot addEventListener(EventListener...listeners) {
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

    public DiscordBot removeEventListener(EventListener...listeners) {
        for (EventListener listener : listeners) {
            if (isRunning()) {
                jda.removeEventListener(listener);
            }
            this.listeners.put(listener.getClass(), listener);
        }
        return this;
    }

    public CommandHandler getCommandHandler() {
        return commandHandler;
    } // method getCommandHandler

}
