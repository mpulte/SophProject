package com.discordbot.command;

import java.util.Map;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;

public class HelpCommand extends CommandListener {

	public HelpCommand(CommandHandler handler) {
		super(handler);
	} // constructor

	@Override
	public void onCommandReceived(CommandReceivedEvent event) {
		MessageChannel channel = event.getMessageReceivedEvent().getChannel();

		Map<String, CommandListener> commands = getHandler().getCommandsListeners();
		for (String key : commands.keySet()) {
			channel.sendMessage(CommandReceivedEvent.PREFIX + key + "     "
					+ commands.get(key).getDescription()).queue();
		}
	} // method onCommandReceived

	@Override
	public boolean usesChannel(ChannelType type) {
		return true;
	} // method useChannel

	@Override
	public String getDescription() {
		return "Lists available commands and their descriptions.";
	} // method getDescription

} // class HelpCommand
