package discordbot.command;

import java.util.Random;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;

public class RollCommand implements CommandListener {
	
	private final int DEFAULT = 100;

	@Override
	public void onCommandReceived(CommandReceivedEvent event) {
		// event information
		User author = event.getMessageReceivedEvent().getAuthor();
		MessageChannel channel = event.getMessageReceivedEvent().getChannel();
		
		// initialize the random number
		Random rand = new Random();
		int roll;
		int mod = DEFAULT;
		
		// check for mod argument
		if (!event.getArgs().isEmpty()) {
			try {
				mod = Integer.parseInt(event.getArgs().get(0));
			} catch (NumberFormatException ex) { }
		}
		
		// calculate the roll
		roll = rand.nextInt(mod) + 1;
		
		// send the message
		channel.sendMessage(author.getName() + " rolled " + roll).queue();
	} // method onCommandReceivedEvent
	
	@Override
	public boolean useChannel(ChannelType type) {
		return true;
	} // method useChannel

} // class RollCommand