package com.discordbot.command;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.Random;


public class EightBallCommand extends CommandListener {
		
	private final String[] responses = 
			{
					"The magic 8-ball says: As I see it, yes.",
					"The magic 8-ball says: Ask again later.",
					"The magic 8-ball says: Better not to tell you now.",
					"The magic 8-ball says: Cannot predict now.",
					"The magic 8-ball says: Concentrate and try again.",
					"The magic 8-ball says: Don't count on it.",
					"The magic 8-ball says: It is certain.",
					"It is decidedly so.",
					"The magic 8-ball says: Most likely.",
					"The magic 8-ball says: My reply is no.",
					"The magic 8-ball says: My sources say no.",
					"The magic 8-ball says: Outlook is good.",
					"The magic 8-ball says: Outlook is not so good.",
					"The magic 8-ball says: Reply hazy, try again.",
					"The magic 8-ball says: Signs point to yes.",
					"The magic 8-ball says: Very doubtful.",
					"The magic 8-ball says: Without a doubt.",
					"The magic 8-ball says: Yes.",
					"The magic 8-ball says: Yes, definitely.",
					"The magic 8-ball says: You may rely on it."
			};
	
	public EightBallCommand(CommandHandler handler) {
		super(handler); 
	}

	@Override
	public void onCommandReceived(CommandReceivedEvent event) {
		
		MessageChannel channel = event.getMessageReceivedEvent().getChannel();
		
		Random rand = new Random();
		String response =  responses[rand.nextInt(responses.length)];
		
		channel.sendMessage(response).queue();

	}

	@Override
	public boolean usesChannel(ChannelType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDescription() {
		return "Returns your fortune.";
	}

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}
