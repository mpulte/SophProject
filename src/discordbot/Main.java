package discordbot;

import discordbot.util.MessageListener;
import net.dv8tion.jda.core.JDA;

public class Main {

	public static void main(String[] args) {
			JDA jda = DiscordBot.getInstance().getJDA();
			
			if (jda == null) { // login failed, exit
				System.exit(0);
			}
			
			jda.addEventListener(new MessageListener());
	} // method main

} // class Main
