package com.jpacman.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.jpacman.Game;
import com.jpacman.model.HighScoreEntry;
import com.jpacman.view.ScoreRenderer;
import com.jpacman.view.graphics.Screen;

public class ScoreController implements Controller {
	public static final int NUMBER_OF_TOP_HIGH_SCORES = 10;
	public static List<HighScoreEntry> topHighScores;

	private static String highScoresFilePath = null;

	static {
		String userAccountName = System.getProperty("user.name");
		String OS = System.getProperty("os.name").toLowerCase();

		File gameDir;
		if (OS.indexOf("win") >= 0) {
			gameDir = new File("C:\\Users\\" + userAccountName + "\\AppData\\Local\\com.jpacman");
			highScoresFilePath = "C:\\Users\\" + userAccountName + "\\AppData\\Local\\com.jpacman\\highscores.ser";
		} else { // if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") >
					// 0) { for linux
			gameDir = new File("/home/" + userAccountName + "/.com.jpacman");
			highScoresFilePath = "/home/" + userAccountName + "/.com.jpacman/highscores.ser";
		}

		// if the directory does not exist, create it along with a default high scores
		// file
		if (!gameDir.exists()) {
			System.out.println("creating directory: " + gameDir);
			boolean result = false;
			try {
				gameDir.mkdir();
				result = true;
			} catch (SecurityException se) {
				// handle it
				se.printStackTrace();
			}
			if (result) {
				System.out.println("DIR created");
			}

			createDefaultFile();
		}
	}

	private final ScoreRenderer scoreRenderer;

	private int score = 0;
	private int highScore = 0;

	public ScoreController(ScoreRenderer scoreTextRenderer) {
		this.scoreRenderer = scoreTextRenderer;
	}

	@Override
	public void update(double delta) {
		scoreRenderer.setScore(score);
		if (Game.gameOver) {
			if (score > highScore) {
				highScore = score;
			}
			scoreRenderer.setHighScore(highScore);
		}
	}

	public static void writeHighScoresToDisk() {
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(highScoresFilePath);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(topHighScores);
			oos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readHighScoresFromDisk() {
		try {
			topHighScores = new ArrayList<HighScoreEntry>(10);

			FileInputStream streamIn = new FileInputStream(highScoresFilePath);
			ObjectInputStream ois = new ObjectInputStream(streamIn);
			Object highScores = ois.readObject();
			if (highScores instanceof List<?>) {
				for (Object entry : (List<?>) highScores) {
					if (entry instanceof HighScoreEntry) {
						topHighScores.add((HighScoreEntry) entry);
					}
				}
			} else {
				System.err.println("Error: high score list not in proper format!");
			}
			ois.close();

			// for (HighScoreEntry score : topHighScores) {
			// System.out.println("name = " + score.getName() + ", high score = " +
			// score.getHighScore() + ", date = " +
			// score.getDate());
			// }

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			createDefaultFile();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// sorts HighScoreEntrys by high score in descending order
	public static Comparator<HighScoreEntry> highScoreSorter = new Comparator<HighScoreEntry>() {
		@Override
		public int compare(HighScoreEntry hs0, HighScoreEntry hs1) {
			if (hs0.getHighScore() < hs1.getHighScore()) {
				return 1;
			} else if (hs0.getHighScore() == hs1.getHighScore()) {
				return 0;
			} else { // hs0.getHighScore() > hs1.getHighScore()
				return -1;
			}
		}
	};

	private static void createDefaultFile() {
		System.out.println("creating default file...");
		topHighScores = new ArrayList<HighScoreEntry>(10);
		for (int i = 0; i < 10; i++) {
			topHighScores.add(new HighScoreEntry("          ", "####-##-##", 0));
		}
		writeHighScoresToDisk();
	}

	public ScoreRenderer getScoreRenderer() {
		return scoreRenderer;
	}

	public List<HighScoreEntry> getTopHighScores() {
		return topHighScores;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public void increaseScoreBy(int score) {
		this.score += score;
	}

	public void renderScore(Screen screen) {
		scoreRenderer.render(screen);
	}

	public int getHighScore() {
		return highScore;
	}

	public void setHighScore(int highScore) {
		this.highScore = highScore;
	}
}
