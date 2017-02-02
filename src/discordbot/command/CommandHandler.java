package discordbot.command;

import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class CommandHandler extends ListenerAdapter {
	
	private List<CommandListener> listeners;
	
	public CommandHandler() {
		listeners = new ArrayList<>();
	} // constructor
	
	public void addCommandListener(CommandListener listener) {
		listeners.add(listener);
	} // method addCommandListener

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		if (event.getMessage().getContent().startsWith(CommandReceivedEvent.PREFIX)) {
			CommandReceivedEvent commandEvent = CommandReceivedEvent.buildCommand(event);
			for (CommandListener listener : listeners) {
				listener.onCommandReceived(commandEvent);
			}
		}
	} // method onCommandReceivedEvent

} // class CommandHandler
