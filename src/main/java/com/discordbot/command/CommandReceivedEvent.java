package com.discordbot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public class CommandReceivedEvent {
	
	public static String PREFIX = "!";
	
	private MessageReceivedEvent event;
	private String command;
	private List<String> args;

	public CommandReceivedEvent(MessageReceivedEvent event, String command, List<String> args) {
		this.event = event;
		this.command = command;
		this.args = args;
	} // constructor
	
	public static CommandReceivedEvent buildCommand(MessageReceivedEvent e) {
		String message = e.getMessage().getContent().replaceFirst(CommandReceivedEvent.PREFIX, "").trim();
		
		// split the message by spaces, the first String will be the command, the rest are args
		List<String> args = new ArrayList<>(Arrays.asList(message.split(" ")));
		String command = args.get(0); // first item split is the command
		args.remove(0); // remove command from args list
		
		return new CommandReceivedEvent(e, command, args);
	} // method buildCommand

	public MessageReceivedEvent getMessageReceivedEvent() {
		return event;
	} // method getEvent

	public String getCommand() {
		return command;
	} // method getCommand

	public List<String> getArgs() {
		return args;
	} // method getArgs
	
} // class Command
