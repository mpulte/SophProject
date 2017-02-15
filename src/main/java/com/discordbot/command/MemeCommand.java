package com.discordbot.command;

import net.dv8tion.jda.core.entities.ChannelType;

public class MemeCommand extends CommandListener {

	public MemeCommand(CommandHandler handler) {
		super(handler);
	}

	@Override
	public void onCommandReceived(CommandReceivedEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean usesChannel(ChannelType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
