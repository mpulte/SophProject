package com.discordbot.command;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.Map;

public class HelpCommand extends CommandListener {

	public HelpCommand(CommandHandler handler) {
		super(handler);
	} // constructor

	@Override
	public void onCommandReceived(CommandReceivedEvent event) {
		MessageChannel channel = event.getMessageReceivedEvent().getChannel();

		if (getHandler() != null) {
            Map<String, CommandListener> commands = getHandler().getCommandListeners();
            String message = "";
            for (String key : commands.keySet()) {
                message += CommandReceivedEvent.PREFIX + key + '\t' + commands.get(key).getDescription() + '\n';
            }
            if (!message.isEmpty()) {
                channel.sendMessage(message.substring(0, message.length() - 1)).queue();
            }
        } else {
		    channel.sendMessage("Unable to help at this time").queue();
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
	
	@Override
	public String getHelp() {
		return ""; // TODO: add help
	} // method getHelp

} // class HelpCommand
