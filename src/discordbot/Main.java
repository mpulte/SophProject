package discordbot;

import discordbot.command.CommandHandler;
import discordbot.command.KickCommand;
import discordbot.command.RollCommand;
import discordbot.util.MessageListener;
import net.dv8tion.jda.core.JDA;

public class Main {

	public static void main(String[] args) {
			JDA jda = DiscordBot.getInstance().getJDA();
			
			if (jda == null) { // login failed, exit
				System.exit(0);
			}
			
			CommandHandler commandHandler = new CommandHandler();
			commandHandler.addCommandListener("roll", new RollCommand());
			commandHandler.addCommandListener("kick", new KickCommand());
			
			jda.addEventListener(new MessageListener());
			jda.addEventListener(commandHandler);
	} // method main

} // class Main
