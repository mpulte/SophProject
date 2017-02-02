package discordbot.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.exceptions.GuildUnavailableException;
import net.dv8tion.jda.core.exceptions.PermissionException;

public class KickCommand implements CommandListener {
	
	public final String COMMAND = "kick";

	@Override
	public void onCommandReceived(CommandReceivedEvent event) {
		// check for correct command
		if (!event.getCommand().equals(COMMAND)) {
			return;
		}
		
		// event information
		Message message = event.getMessageReceivedEvent().getMessage();
		MessageChannel channel = event.getMessageReceivedEvent().getChannel();
		
		// only messages from guild
		if (!useChannel(message.getChannelType())) {
			channel.sendMessage("Kick command only available in guild").queue();
			return;
		}
		
		// get guild and self information
		Guild guild = event.getMessageReceivedEvent().getGuild();
		
		// check for author's permission
		if (!guild.getMember(message.getAuthor()).hasPermission(Permission.KICK_MEMBERS)) {
			channel.sendMessage(message.getAuthor().getName() + ", you do not have permission to kick").queue();
			return;
		}
		
		// loop through each member
		for (User user : message.getMentionedUsers()){
			Member member = guild.getMember(user);
			
			try {
				guild.getController().kick(member).queue();
				channel.sendMessage("Kicked " + member.getEffectiveName()).queue();
			} catch (PermissionException ex) {
				channel.sendMessage("Cannot kick " + member.getEffectiveName() + ", I don't have permission")
						.queue();
			} catch (IllegalArgumentException ex) {
				channel.sendMessage("Cannot kick " + member.getEffectiveName() + ", they are not a guild member")
						.queue();
			} catch (GuildUnavailableException ex) {
				System.out.println("Cannot kick " + member.getEffectiveName() + ", guild temporarily unavailable");
			}
		} // for
	} // method onCommandReceived

	@Override
	public boolean useChannel(ChannelType type) {
		return type == ChannelType.TEXT;
	} // method useChannel
	
} // class KickCommand
