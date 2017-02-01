package discordbot.util;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.isFromType(ChannelType.PRIVATE)) {
			System.out.printf("[PM] %s: %s\n",
					event.getAuthor().getName(),
					event.getMessage().getContent());
		} else {
			System.out.printf("[%s][%s] %s: %s",
					event.getGuild().getName(),
					event.getTextChannel().getName(),
					event.getMember().getEffectiveName(),
					event.getMessage().getContent());
		}
	} // method onMessageReceived
} // class MessageListener