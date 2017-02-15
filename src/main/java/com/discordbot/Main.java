package com.discordbot;

import com.discordbot.command.CommandHandler;
import com.discordbot.command.HelpCommand;
import com.discordbot.command.KickCommand;
import com.discordbot.command.MemeCommand;
import com.discordbot.command.RollCommand;
import com.discordbot.util.MessageListener;

import net.dv8tion.jda.core.JDA;

public class Main {

	public static void main(String[] args) {
		JDA jda = DiscordBot.getInstance().getJDA();
		
		if (jda == null) { // login failed, exit
			System.exit(0);
		}
		
		// TODO: create Class for building commandHandler
		CommandHandler commandHandler = new CommandHandler();
		commandHandler.addCommandListener("help", HelpCommand.class);
		commandHandler.addCommandListener("roll", RollCommand.class);
		commandHandler.addCommandListener("kick", KickCommand.class);
		commandHandler.addCommandListener("meme", MemeCommand.class);
		
		jda.addEventListener(commandHandler);
		jda.addEventListener(new MessageListener());
	} // method main

} // class Main
