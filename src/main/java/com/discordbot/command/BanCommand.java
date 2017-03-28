package com.discordbot.command;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.GuildUnavailableException;
import net.dv8tion.jda.core.exceptions.PermissionException;

/**
 * A {@link CommandListener} for handling the ban command.
 *
 * @see CommandListener
 */
@Command(tag = "ban")
public class BanCommand extends CommandListener {

    /**
     * @param handler The {@link CommandHandler} the BanCommand is bound to.
     */
    public BanCommand(CommandHandler handler) {
        super(handler);
    }

    /**
     * Handles any {@link CommandReceivedEvent}. The BanCommand bans any {@link User} mentioned from the {@link Guild}
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
            channel.sendMessage("Ban command only available in guild").queue();
            return;
        }

        // get guild and self information
        Guild guild = event.getMessageReceivedEvent().getGuild();

        // check for author's permission
        if (!guild.getMember(message.getAuthor()).hasPermission(Permission.KICK_MEMBERS)) {
            channel.sendMessage(message.getAuthor().getName() + ", you do not have permission to ban").queue();
            return;
        }

        // loop through each member
        for (User user : message.getMentionedUsers()) {
            Member member = guild.getMember(user);

            try {
                guild.getController().ban(member, 0).queue();
                channel.sendMessage("Banned " + member.getEffectiveName()).queue();
            } catch (PermissionException ex) {
                channel.sendMessage("Cannot ban " + member.getEffectiveName() + ", I don't have permission").queue();
            } catch (IllegalArgumentException ex) {
                channel.sendMessage("Cannot ban " + member.getEffectiveName() + ", they are not a guild member").queue();
            } catch (GuildUnavailableException ex) {
                System.out.println("Cannot ban " + member.getEffectiveName() + ", guild temporarily unavailable");
            }
        }
    }

    /**
     * Used for identifying if a {@link CommandReceivedEvent} should be sent to the BanCommand. The BanCommand only
     * works on channels of type {@link ChannelType#TEXT}.
     *
     * @param type The {@link ChannelType} to use.
     * @return True if the BanCommand uses the {@link ChannelType}. False otherwise.
     */
    @Override
    public boolean usesChannel(ChannelType type) {
        return type == ChannelType.TEXT;
    }

    /**
     * Used for accessing a description of the BanCommand.
     *
     * @return A {@link String} description of the BanCommand.
     */
    @Override
    public String getDescription() {
        return "Bans all users mentioned.";
    }

    /**
     * Used for accessing receiving help for using the BanCommand.
     *
     * @return A {@link String} description of help for the BanCommand.
     */
    @Override
    public String getHelp() {
        return "The author and bot must have permission. The bot must be above users in hierarchy.";
    }

}
