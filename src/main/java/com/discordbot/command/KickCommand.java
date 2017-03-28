package com.discordbot.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.GuildUnavailableException;
import net.dv8tion.jda.core.exceptions.PermissionException;

/**
 * A {@link CommandListener} for handling the kick command.
 *
 * @see CommandListener
 */
@Command(tag = "kick")
public class KickCommand extends CommandListener {

    /**
     * @param handler The {@link CommandHandler} the KickCommand is bound to.
     */
    public KickCommand(CommandHandler handler) {
        super(handler);
    }

    /**
     * Handles any {@link CommandReceivedEvent}. The KickCommand kicks any {@link User} mentioned from the {@link Guild}
     * the of the {@link Channel} the {@link CommandReceivedEvent} was received on.
     *
     * @param event The {@link CommandReceivedEvent} to handle.
     */
    @Override
    public void onCommandReceived(CommandReceivedEvent event) {
        // event information
        Message message = event.getMessageReceivedEvent().getMessage();
        MessageChannel channel = event.getMessageReceivedEvent().getChannel();

        // only messages from guild
        if (!usesChannel(message.getChannelType())) {
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
        for (User user : message.getMentionedUsers()) {
            Member member = guild.getMember(user);

            try {
                guild.getController().kick(member).queue();
                channel.sendMessage("Kicked " + member.getEffectiveName()).queue();
            } catch (PermissionException ex) {
                channel.sendMessage("Cannot kick " + member.getEffectiveName() + ", I don't have permission").queue();
            } catch (IllegalArgumentException ex) {
                channel.sendMessage("Cannot kick " + member.getEffectiveName() + ", they are not a guild member").queue();
            } catch (GuildUnavailableException ex) {
                System.out.println("Cannot kick " + member.getEffectiveName() + ", guild temporarily unavailable");
            }
        }
    }

    /**
     * Used for identifying if a {@link CommandReceivedEvent} should be sent to the KickCommand. The KickCommand only
     * works on channels of type {@link ChannelType#TEXT}.
     *
     * @param type The {@link ChannelType} to use.
     * @return True if the KickCommand uses the {@link ChannelType}. False otherwise.
     */
    @Override
    public boolean usesChannel(ChannelType type) {
        return type == ChannelType.TEXT;
    }

    /**
     * Used for accessing a description of the KickCommand.
     *
     * @return A {@link String} description of the KickCommand.
     */
    @Override
    public String getDescription() {
        return "Kicks all users mentioned.";
    }

    /**
     * Used for accessing receiving help for using the KickCommand.
     *
     * @return A {@link String} description of help for the KickCommand.
     */
    @Override
    public String getHelp() {
        return "The author and bot must have permission. The bot must be above users in hierarchy.";
    }

}
