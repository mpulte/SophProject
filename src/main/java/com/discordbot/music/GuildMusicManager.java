package com.discordbot.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;

/**
 * Holds n {@link AudioPlayer} and a {@link TrackScheduler} for a guild.
 * <p>
 * This file was modified from a AudioPlayerSendHandler at https://github.com/sedmelluq/lavaplayer
 * The original classpath was com.sedmelluq.discord.lavaplayer.track.demo.jda.GuildMusicManager
 */
public class GuildMusicManager {

    private final AudioPlayer player;
    private final TrackScheduler scheduler;

    /**
     * Creates an {@link AudioPlayer} and a {@link TrackScheduler}.
     *
     * @param manager Audio player manager to use for creating the player.
     */
    public GuildMusicManager(AudioPlayerManager manager) {
        player = manager.createPlayer();
        scheduler = new TrackScheduler(player);
        player.addListener(scheduler);
    }

    /**
     * @return Wrapper around AudioPlayer to use it as an AudioSendHandler.
     */
    public AudioPlayerSendHandler getSendHandler() {
        return new AudioPlayerSendHandler(player);
    }

    /**
     * Accessor for the {@link AudioPlayer}.
     *
     * @return The {@link AudioPlayer}. for the guild.
     */
    public AudioPlayer getAudioPlayer() {
        return player;
    }

    /**
     * Accessor for the {@link TrackScheduler}.
     *
     * @return The {@link TrackScheduler}. for the guild.
     */
    public TrackScheduler getTrackScheduler() {
        return scheduler;
    }


}
