package com.jpacman.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JOptionPane;

import com.jpacman.Game;
import com.jpacman.SoundEffect;
import com.jpacman.controller.ghost.GhostAIController;
import com.jpacman.controller.ghost.GhostAIController.PreferredGhost;
import com.jpacman.controller.ghost.GhostStatusController;
import com.jpacman.controller.pacman.PacmanCollisionController;
import com.jpacman.controller.pacman.PacmanStatusController;
import com.jpacman.model.HighScoreEntry;
import com.jpacman.model.ghost.Ghost;
import com.jpacman.util.Timer;
import com.jpacman.view.MazeRenderer;

public class GameStatusController implements Controller, Runnable {
	// ********************* Class (static) variables ********************** //
	// ************************** Constants ************************** //
	public static final int NEW_PACMAN_LIVE_THRESHOLD_1 = 10000;
	public static final int NEW_PACMAN_LIVE_THRESHOLD_2 = 100000;
	public static final int PILLS_EATEN_FOR_FRUIT_APPEARANCE_1 = 70;
	public static final int PILLS_EATEN_FOR_FRUIT_APPEARANCE_2 = 170;

	public static boolean newTopHighScore;
	public static Timer readyOrGameOverTimer = new Timer();
	private static Timer completeLevelTimer = new Timer();

	private static int totalSecondsPassed = 0; // amount of total seconds passed since game started
	private static int totalPillsEaten = 0;
	private static boolean lifeLost = false;

	private static boolean pacmanEarnedASingleLife = false;
	private static boolean pacmanEarnedASecondLife = false;
	private static boolean fruitAppearedOnce = false; // flag to run code only once
	private static boolean fruitAppearedTwice = false; // flag to run code only once

	private static boolean openingSongSoundPlayed = false;
	private static int waitingTime = Timer.FOUR_SECONDS;

	// ************************* Instance variables ************************ //
	private final LevelController levelController;
	private final GhostStatusController[] ghostsStatusControllers;
	private final PacmanStatusController pacmanStatusController;
	private final FruitController fruitController;
	private final PillController pillsController;
	private final ScoreController scoreController;
	private final MazeRenderer mazeRenderer;

	public GameStatusController(LevelController levelController, GhostStatusController[] ghostsStatusControllers,
			PacmanStatusController pacmanStatusController, FruitController fruitController,
			PillController pillsController, ScoreController scoreController, MazeRenderer mazeRenderer) {
		this.levelController = levelController;
		this.ghostsStatusControllers = ghostsStatusControllers;
		this.pacmanStatusController = pacmanStatusController;
		this.fruitController = fruitController;
		this.pillsController = pillsController;
		this.scoreController = scoreController;
		this.mazeRenderer = mazeRenderer;
	}

	@Override
	public void update(double delta) {
		checkFruitAppearance();

		checkIfPacmanEarnedNewLife();

		readyOrGameOverTimer.countTimeInMilliseconds();

		if (Game.completedLevel) {
			completeLevelTimer.countTimeInMilliseconds();
		}

		if (lifeLost && !Game.pacmanDying) {
			pacmanStatusController.getPacman().decreaseLivesByOne();

			if (pacmanStatusController.getPacman().getLives() == 0) {
				Game.gameOver = true;
				fruitController.getFruit().setAppeared(false);
				new Thread(this).start();
				return;
			}
			pacmanLostALife();
			Game.ready = true;
			readyOrGameOverTimer.reset();
			lifeLost = false;
			waitingTime = Timer.TWO_SECONDS;
		}

		if (pillsController.getPillsRemaining() == 0 && !Game.completedLevel) {
			SoundEffect.SIREN.getClip().stop();
			completeLevelTimer.reset();
			Game.completedLevel = true;
		}

		if (Game.completedLevel && completeLevelTimer.getTimeElapsedInMilliseconds() == Timer.ONE_SECOND) {
			Game.completedLevel = false;
			Game.ready = true;
			readyOrGameOverTimer.reset();
			pacmanCompletedLevel();
		}

		if (Game.ready && !openingSongSoundPlayed) {
			SoundEffect.OPENING_SONG.play();
			openingSongSoundPlayed = true;
		}
		if (Game.ready && readyOrGameOverTimer.getTimeElapsedInMilliseconds() == waitingTime) {
			Game.ready = false;
			SoundEffect.SIREN.play();
		}
	}

	private void checkFruitAppearance() {
		if ((!fruitAppearedOnce && pillsController.getPillsEaten() == PILLS_EATEN_FOR_FRUIT_APPEARANCE_1) //
				|| (!fruitAppearedTwice && pillsController.getPillsEaten() == PILLS_EATEN_FOR_FRUIT_APPEARANCE_2)) {
			fruitController.getFruit().setAppeared(true);
			fruitController.setFruitMustDisappear(false);
			if (!fruitAppearedOnce) {
				fruitAppearedOnce = true;
			} else if (fruitAppearedOnce && !fruitAppearedTwice) {
				fruitAppearedTwice = true;
			}
		}

		if (fruitController.getFruitMustDisappear()) {
			fruitController.getFruit().setAppeared(false);
			fruitController.setTimerSet(false);
		}
	}

	private void checkIfPacmanEarnedNewLife() {
		if (!pacmanEarnedASingleLife && scoreController.getScore() >= NEW_PACMAN_LIVE_THRESHOLD_1) {
			pacmanStatusController.getPacman().increaseLivesByOne();
			SoundEffect.EXTRA_LIVE.play();
			pacmanEarnedASingleLife = true;
		}

		if (!pacmanEarnedASecondLife && scoreController.getScore() >= NEW_PACMAN_LIVE_THRESHOLD_2) {
			pacmanStatusController.getPacman().increaseLivesByOne();
			pacmanEarnedASecondLife = true;
			SoundEffect.EXTRA_LIVE.play();
		}
	}

	private void pacmanLostALife() {
		// reset ghosts
		for (GhostStatusController controller : ghostsStatusControllers) {
			Ghost ghost = controller.getGhost();
			ghost.initialize(false);
			if (ghost.getMode() == Ghost.Mode.FRIGHTENED || ghost.getMode() == Ghost.Mode.EATEN) {
				ghost.setMode(controller.getGhost().getPreviousMode());
			}
			// reset speeds
			ghost.setMovingSpeedViaMultiplier(levelController.getLevel().getGhostNormalMovingSpeedMultiplier());
			controller.setFrightenedModeInitFlag(false);
			controller.setFrightenedModeFlashFlag(false);
			controller.setFrightenedModeEndFlag(false);
			controller.setOneHundredMillisecondsCounter(GhostStatusController.INIT_TIME);
			controller.setSecondsPassedInCurrentPeriod(0);
			controller.setSecondsPassedInLastNonFrightenedPeriod(0);
		}

		GhostAIController.setGlobalCounterActive(true);
		GhostAIController.setPreferredGhostToExitHouse(PreferredGhost.PINKY);

		// reset fruit
		fruitController.getFruit().setAppeared(false);

		// reset pacman
		pacmanStatusController.getPacman().initialize();
		pacmanStatusController.getPacman()
				.setMovingSpeedViaMultiplier(levelController.getLevel().getPacmanNormalMovingSpeedMultiplier());
		PacmanStatusController.timeElapsedSinceAtePillTimer.setTimeElapsedInMilliseconds(0);
		PacmanCollisionController.setPacmanCollidedWithPillFlag(false);
	}

	private void pacmanCompletedLevel() {
		// System.out.println("We completed the level!");
		totalPillsEaten += pillsController.getPillsEaten();
		fruitAppearedOnce = false;
		fruitAppearedTwice = false;
		openingSongSoundPlayed = false;
		waitingTime = Timer.FOUR_SECONDS;

		for (GhostStatusController controller : ghostsStatusControllers) {
			controller.reset();
		}

		levelController.nextLevel();
		levelController.initializeLevel(pillsController, Game.getPacman(), Game.getGhosts(), Game.getFruit());
		pillsController.setPillsEaten(0);
		mazeRenderer.setMaze(levelController.getLevel().getMaze());
	}

	public static void reset() {
		readyOrGameOverTimer = new Timer();
		totalSecondsPassed = 0;
		totalPillsEaten = 0;
		lifeLost = false;
		pacmanEarnedASingleLife = false;
		pacmanEarnedASecondLife = false;
		fruitAppearedOnce = false;
		fruitAppearedTwice = false;
		openingSongSoundPlayed = false;
		waitingTime = Timer.FOUR_SECONDS;
		newTopHighScore = false;
	}

	public static int getTotalSecondsPassed() {
		return totalSecondsPassed;
	}

	public static void setTotalSecondsPassed(int totalSecondsPassed) {
		GameStatusController.totalSecondsPassed = totalSecondsPassed;
	}

	public static void increaseTotalSecondsPassedByOne() {
		totalSecondsPassed++;
	}

	public static int getTotalPillsEaten() {
		return totalPillsEaten;
	}

	public static void setTotalPillsEaten(int totalPillsEaten) {
		GameStatusController.totalPillsEaten = totalPillsEaten;
	}

	public static boolean isLifeLost() {
		return lifeLost;
	}

	public static void setLifeLost(boolean lifeLost) {
		GameStatusController.lifeLost = lifeLost;
	}

	@Override
	public void run() {
		int numberOfTopHighScores = ScoreController.topHighScores.size();
		if (numberOfTopHighScores < ScoreController.NUMBER_OF_TOP_HIGH_SCORES || scoreController
				.getHighScore() >= ScoreController.topHighScores.get(numberOfTopHighScores - 1).getHighScore()) {

			String username = (String) JOptionPane.showInputDialog(null,
					"Please enter your (nick)name: \n(only the first 10 characters will be kept)",
					"New Top High Score!", JOptionPane.PLAIN_MESSAGE, null, null, "player");

			String[] currentDateTime = LocalDateTime.now().toString().split("T");
			String currentDate = currentDateTime[0];

			if (username != null) {
				if (username.length() == 0) {
					username = "player";
				} else if (username.length() > 10) {
					username = username.substring(0, 10);
				}

				ScoreController.topHighScores
						.add(new HighScoreEntry(username, currentDate, scoreController.getHighScore()));
				Collections.sort(ScoreController.topHighScores, ScoreController.highScoreSorter);
				if (ScoreController.topHighScores.size() > ScoreController.NUMBER_OF_TOP_HIGH_SCORES) {
					ScoreController.topHighScores = new ArrayList<HighScoreEntry>(
							ScoreController.topHighScores.subList(0, ScoreController.NUMBER_OF_TOP_HIGH_SCORES));
				}
				ScoreController.writeHighScoresToDisk();
			}
		}
		newTopHighScore = true;
	}
}
