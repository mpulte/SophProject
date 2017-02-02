package discordbot.command;

import net.dv8tion.jda.core.entities.ChannelType;

public interface CommandListener {
	
	public void onCommandReceived(CommandReceivedEvent event);
	public boolean useChannel(ChannelType type);
	
} // class CommandFactory
