package com.discordbot.command;

import net.dv8tion.jda.core.entities.ChannelType;

public abstract class CommandListener {
	
	private CommandHandler handler;
	
	public CommandListener(CommandHandler handler) {
		this.handler = handler;
	} // constructor
	
	public CommandHandler getHandler() {
		return handler;
	} // method getHandler
	
	public abstract void onCommandReceived(CommandReceivedEvent event);
	public abstract boolean usesChannel(ChannelType type);
	public abstract String getDescription();
	
} // class CommandFactory
