package com.discordbot.command;

import com.discordbot.music.GuildMusicManager;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A {@link CommandListener} for controlling music playback.
 * <p>
 * This file was created with help from Main on https://github.com/sedmelluq/lavaplayer
 * The original classpath was com.sedmelluq.discord.lavaplayer.track.demo.jda.Main
 *
 * @see CommandListener
 */
@Command(tag = "music")
public class MusicCommand extends CommandListener {

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;

    /**
     * Constructor sets registers the {@link AudioPlayerManager} to the {@link AudioSourceManagers}
     */
    public MusicCommand() {
        musicManagers = new HashMap<>();

        playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    /**
     * Connects to the last {@link VoiceChannel} used. Default is the first channel.
     *
     * @param audioManager The {@link AudioManager} to connect.
     */
    private static void connectToVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            List<VoiceChannel> voiceChannel = audioManager.getGuild().getVoiceChannels();
            if (voiceChannel != null && !voiceChannel.isEmpty()) {
                audioManager.openAudioConnection(voiceChannel.get(0));
            }
        }
    }

    /**
     * Handles any {@link CommandReceivedEvent}. Determines request and acts accordingly.
     *
     * @param event   The {@link CommandReceivedEvent} to handle.
     * @param handler The {@link CommandHandler} that pushed the {@link CommandReceivedEvent}.
     */
    @Override
    protected void onCommandReceived(CommandReceivedEvent event, CommandHandler handler) {
        Guild guild = event.getMessageReceivedEvent().getGuild();
        if (guild == null || event.getArgs().isEmpty()) {
            // no guild or no args, so we won't do anything
            return;
        }

        // determine the request
        TextChannel channel = event.getMessageReceivedEvent().getTextChannel();
        switch (event.getArgs().get(0)) {
            case "play":
                if (event.getArgs().size() >= 2) {
                    loadAndPlay(event.getMessageReceivedEvent().getTextChannel(), event.getArgs().get(1));
                } else {
                    resume(channel);
                }
                break;
            case "pause":
                pause(channel);
                break;
            case "skip":
                skip(channel);
                break;
        }
    }

    /**
     * Acquires the {@link GuildMusicManager} for the specified {@link Guild}. If none exists, it creates a new one.
     *
     * @param guild The {@link Guild} of the requested {@link GuildMusicManager}.
     * @return the {@link GuildMusicManager} corresponding to the provided {@link Guild}.
     */
    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        // look for an existing music manager
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        //noinspection Java8MapApi
        if (musicManager == null) {
            // no music manager exists, create a new one
            musicManager = new GuildMusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    /**
     * Loads an {@link AudioTrack} from the URL provided and adds it to the track queue.
     *
     * @param channel  The {@link TextChannel} to reply on.
     * @param trackUrl The URL of the track.
     */
    private void loadAndPlay(final TextChannel channel, final String trackUrl) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                channel.sendMessage("Adding to queue " + track.getInfo().title).queue();

                play(channel.getGuild(), musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                channel.sendMessage("Adding to queue " + firstTrack.getInfo().title
                        + " (first track of playlist " + playlist.getName() + ")").queue();

                play(channel.getGuild(), musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nothing found by " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                channel.sendMessage("Could not play: " + exception.getMessage()).queue();
            }
        });
    }

    /**
     * Connects the bot to a {@link VoiceChannel} and queues an {@link AudioTrack}.
     *
     * @param guild The {@link Guild} to play the music on.
     * @param musicManager The {@link GuildMusicManager} to use.
     * @param track The {@link AudioTrack} to queue.
     */
    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
        connectToVoiceChannel(guild.getAudioManager());
        musicManager.getTrackScheduler().queue(track);
        musicManager.getAudioPlayer().setPaused(false);
    }

    /**
     * Resumes playback.
     *
     * @param channel  The {@link TextChannel} to reply on.
     */
    private void resume(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        if (musicManager.getAudioPlayer().isPaused()) {
            musicManager.getAudioPlayer().setPaused(false);
            channel.sendMessage("Playback resumed.").queue();
        }
    }

    /**
     * Pauses playback.
     *
     * @param channel  The {@link TextChannel} to reply on.
     */
    private void pause(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        if (!musicManager.getAudioPlayer().isPaused()) {
            musicManager.getAudioPlayer().setPaused(true);
            channel.sendMessage("Playback paused.").queue();
        }
    }

    /**
     * Skips the currently playing track.
     *
     * @param channel  The {@link TextChannel} to reply on.
     */
    private void skip(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.getTrackScheduler().nextTrack();

        channel.sendMessage("Skipped to next track.").queue();
    }

    /**
     * Used for identifying if a {@link CommandReceivedEvent} should be sent to the MusicCommand. The MusicCommand only
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
     * Used for accessing a description of the MusicCommand.
     *
     * @return A {@link String} description of the MusicCommand.
     */
    @Override
    public String getDescription() {
        return "Used for controlling the bot's music player.";
    }

    /**
     * Used for receiving help using the MusicCommand.
     *
     * @return A {@link String} description of help for the MusicCommand.
     */
    @Override
    public String getHelp() {
        return "Use play <link> to add a track to the playlist. Use skip to skip the current track.";
    }

}
