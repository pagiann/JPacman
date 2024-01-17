package com.jpacman;

import java.io.IOException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * This enum encapsulates all the sound effects of a game, so as to separate the
 * sound playing codes from the game
 * codes. 1. Define all your sound effect names and the associated wave file. 2.
 * To play a specific sound, simply invoke
 * SoundEffect.SOUND_NAME.play(). 3. You might optionally invoke the static
 * method SoundEffect.init() to pre-load all
 * the sound files, so that the play is not paused while loading the file for
 * the first time. 4. You can use the static
 * variable SoundEffect.volume to mute the sound.
 */
public enum SoundEffect {
	//@formatter:off
    SIREN("sounds/siren.wav"),
    OPENING_SONG("sounds/opening_song.wav"),
    EATING_SIMPLE_PILL("sounds/eating_pill.wav"),
    EATING_SIMPLE_PILL_SHORT("sounds/eating_pill_short.wav"),
    EATING_POWER_PILL("sounds/eating_power_pill.wav"),
    EATING_FRUIT("sounds/eating_fruit.wav"),
    EATING_GHOST("sounds/eating_ghost.wav"),
    EXTRA_LIVE("sounds/extra_live.wav"),
    INTERMISSION("sounds/intermission.wav"),
    PACMAN_DIES("sounds/pacman_dies.wav");
    //@formatter:on

	// Nested class for specifying volume
	public static enum Volume {
		MUTE, LOW, MEDIUM, HIGH
	}

	public static Volume volume = Volume.MEDIUM;

	private String soundFileName;
	// Each sound effect has its own clip, loaded with its own sound file.
	private Clip clip;

	public Clip getClip() {
		return clip;
	}

	// Constructor to construct each element of the enumeration with its own sound
	// file.
	SoundEffect(String soundFileName) {
		try {
			this.soundFileName = soundFileName;
			// Use URL (instead of File) to read from disk and jar file.
			URL url = this.getClass().getClassLoader().getResource(soundFileName);
			// Set up an audio input stream piped from the sound file.
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(url);
			// Get a clip resource.
			clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioInputStream);
		} catch (UnsupportedAudioFileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	// Optional static method to pre-load all the sound files.
	static void init() {
		values(); // calls the constructor for all the elements
	}

	// Play or Re-play the sound effect from the beginning, by rewinding.
	public void play() {
		if (volume != Volume.MUTE) {
			if (soundFileName.equals("sounds/siren.wav")) {
				// max frame 38178 for siren
				clip.setLoopPoints(2000, 37000);
				FloatControl volume = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				volume.setValue(-10.0f);
				clip.loop(Clip.LOOP_CONTINUOUSLY);
			} else if (soundFileName.equals("sounds/eating_pill.wav")) {
				if (!clip.isRunning()) {
					clip.setFramePosition(-600); // rewind to the beginning
					clip.start(); // Start playing
				}
			} else if (soundFileName.equals("sounds/pacman_dies.wav")) {
				clip.setFramePosition(0); // rewind to the beginning
				clip.start(); // Start playing
			} else {
				if (clip.isRunning()) {
					clip.stop(); // Stop the player if it is still running
				}
				clip.setFramePosition(0); // rewind to the beginning
				clip.start(); // Start playing
			}
		}
	}
}
