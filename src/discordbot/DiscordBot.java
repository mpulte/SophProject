package discordbot;

import javax.security.auth.login.LoginException;

import discordbot.util.Util;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;

public class DiscordBot {
	private static DiscordBot instance;
	private JDA jda;
	
	private DiscordBot() {
		initializeJDA();
	} // constructor
	
	public static DiscordBot getInstance() {
		if (instance == null) {
			instance = new DiscordBot();
		}
		return instance;
	} // method DiscordBot
	
	private void initializeJDA() {
		if (jda == null) {
			try {
				jda = new JDABuilder(AccountType.BOT).setToken(Util.TOKEN).buildBlocking();
				jda.setAutoReconnect(true);
			} catch (LoginException e) {
				System.err.println("Error: JDA login failed");
			} catch (IllegalArgumentException | InterruptedException | RateLimitedException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println("JDA already running");
		}
	} // method initializeJDA
	
	public JDA getJDA() {
		return jda;
	} // method getInstance
	
	public void rebootJDA() {
		if (jda != null) {
			jda.shutdown(false);
		}
		initializeJDA();
	} // method rebootJDA
	
	public void shutdown() {
		if (jda != null) {
			jda.shutdown();
		}
		instance = null;
	} // method shutdown

} // class DiscordBot
