package com.discordbot.command;

import java.util.HashMap;
import java.util.Map;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandHandler extends ListenerAdapter {
	
	private Map<String, CommandListener> listeners;
	
	public CommandHandler() {
		listeners = new HashMap<>();
	} // constructor
	
	public void addCommandListener(String command, Class<? extends CommandListener> listenerClass) {
		try {
			listeners.put(command, listenerClass.getConstructor(CommandHandler.class).newInstance(this));
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	} // method addCommandListener
	
	public Map<String, CommandListener> getCommandsListeners() {
		return listeners;
	}

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getMessage().getContent().startsWith(CommandReceivedEvent.PREFIX)
				&& !event.getAuthor().isBot()) {
			CommandReceivedEvent commandEvent = CommandReceivedEvent.buildCommand(event);
			for (String key : listeners.keySet()) {
				if (commandEvent.getCommand().equals(key)) {
					listeners.get(key).onCommandReceived(commandEvent);
				}
			}
		}
	} // method onCommandReceivedEvent

} // class CommandHandler
