package com.discordbot;

import com.discordbot.util.Util;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;

public class DiscordBot {
	private static DiscordBot instance;

	private JDA jda;
    private Map<Class<? extends EventListener>, EventListener> listeners;
    private boolean canRestart;
	
	private DiscordBot() {
	    listeners = new HashMap<>();
	    canRestart = true;
	} // constructor
	
	public static synchronized DiscordBot getInstance() {
		if (instance == null) {
			instance = new DiscordBot();
		}
		return instance;
	} // method DiscordBot

	public JDA getJDA() {
		return jda;
	} // method getInstance

    public boolean isRunning() {
	    return jda != null
                && jda.getStatus() != JDA.Status.SHUTDOWN
                && jda.getStatus() != JDA.Status.SHUTTING_DOWN;
    } // method isRunning
	
	public DiscordBot start() {
	    // if we shutdown and freed api, we can't restart it
	    if (!canRestart) {
	        throw new IllegalStateException("JDA completely shutdown, can't restart");
        }

        // make sure bot isn't running
		if (!isRunning()) {
			try {
				jda = new JDABuilder(AccountType.BOT).setToken(Util.TOKEN).addListener(listeners.values().toArray()).buildBlocking();
				jda.setAutoReconnect(true);
			} catch (LoginException e) {
				System.err.println("Error: JDA login failed");
			} catch (IllegalArgumentException | InterruptedException | RateLimitedException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("JDA already running");
		}
		return this;
	} // method start

	public DiscordBot pause() {
		if (isRunning()) {
			jda.shutdown(false);
		}
		return this;
	} // method pause
	
	public DiscordBot shutdown() {
		if (jda != null && canRestart) {
			jda.shutdown();
            canRestart = false;
		}
		return this;
	} // method shutdown

	public DiscordBot reboot() {
		if (jda != null) {
			jda.shutdown(false);
		}
		start();
		return this;
	} // method reboot

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
    } // method addEventListener

    public DiscordBot removeEventListener(EventListener...listeners) {
        for (EventListener listener : listeners) {
            if (isRunning()) {
                jda.removeEventListener(listener);
            }
            this.listeners.put(listener.getClass(), listener);
        }
        return this;
    } // method addEventListener

} // class DiscordBot
