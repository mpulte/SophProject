package com.discordbot.command;

import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.MessageChannel;

import java.util.Random;


public class JokeCommand extends CommandListener {
		
	public JokeCommand(CommandHandler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}

	private final String[] jokes = 
			{
					"What's red and bad for your teeth? A brick.",
					"Did you hear about the soldier who survived mustard gas and pepper spray? He's a seasoned veteran.",
					"What is the difference between a poorly dressed man on a tricycle and a well dressed man on a bicycle? Attire.",
					"Did you hear the joke about the scarecrow that got promoted? He was outstanding in his field.",
					"What's Irish and stays out all night? Patty O'Furniture.",
					"How do you make holy water? Put it in a pot and boil the hell out if it.",
					"A blind man walks into a bar. And a table. And a chair.",
					"Why do trees seem suspicious on sunny days? Because they're so shady.",
					"What do you call an elephant that doesn't matter? An irrelephant.",
					"What do you call a cow with no legs? Ground beef.",
					"What do you call a cow with two legs? Your mom.",
					"What is the resemblance between a green apple and a red apple? They’re both red except for the green one.",
					"What do you call a dog that does magic tricks? A labracadabrador.",
					"Why are communism jokes funny? Because everyone gets them.",
					"What did the one lawyer say to the other lawyer? 'We're both lawyers.'",
					"What starts with 'E', ends with 'E', and only has one letter in it? Envelope.",
					"Why did the can crusher quit his job? Because it was soda pressing.",
					"If you have 10 apples in one hand, and 13 oranges in the other, what do you have? Big hands.",
					"What's the first thing that clouds do when they get rich? They make it rain.",
					"What do you call a cow that just gave birth? Decalfinated."
			};
	

	@Override
	public void onCommandReceived(CommandReceivedEvent event) {
		
		MessageChannel channel = event.getMessageReceivedEvent().getChannel();
		
		Random rand = new Random();
		String joke =  jokes[rand.nextInt(jokes.length)];
		
		channel.sendMessage(joke).queue();

	}

	@Override
	public boolean usesChannel(ChannelType type) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDescription() {
		return "Offers a joke.";
	}

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

}