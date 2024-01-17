package com.jpacman.controller.ghost;

import java.awt.Point;

import com.jpacman.Game;
import com.jpacman.controller.LevelController;
import com.jpacman.model.MovableGameObject.Direction;
import com.jpacman.model.Pacman;
import com.jpacman.model.ghost.Blinky;
import com.jpacman.model.ghost.Ghost;
import com.jpacman.model.ghost.Ghost.Mode;

public class GhostStatusController extends GhostController {
	// ********************* Class (static) variables ********************** //
	// ************************** Constants ************************** //
	public static final int ONE_SECOND = 1;
	public static final int TWO_SECONDS = 2;
	// -2*100=-200 milliseconds lead time because of code overhead
	public static final int INIT_TIME = -2;

	private static final int NUMBER_OF_SCATTER_PERIODS = 4;
	private static final int NUMBER_OF_CHASE_PERIODS = 4;

	private static int[] scatterPeriodsDurations = new int[NUMBER_OF_SCATTER_PERIODS];
	private static int[] chasePeriodsDurations = new int[NUMBER_OF_CHASE_PERIODS];
	private static int frightenedDuration;
	private static int flashDuration;

	// ************************* Instance variables ************************ //
	protected final LevelController levelController;
	protected final Pacman pacman;

	private int scatterPeriod = 0;
	private int chasePeriod = 0;

	private long timer;
	private boolean timerSet = false;
	private int oneHundredMillisecondsCounter = -2;
	private int secondsPassedInCurrentPeriod = 0;
	private int secondsPassedInLastNonFrightenedPeriod = 0;
	private int currentPeriodDuration = -1;

	private boolean frightenedModeInitFlag = false;
	private boolean frightenedModeFlashFlag = false;
	private boolean frightenedModeEndFlag = false;

	public GhostStatusController(Ghost ghost, LevelController levelController, Pacman pacman) {
		super(ghost, null);
		this.levelController = levelController;
		this.pacman = pacman;

		currentPeriodDuration = scatterPeriodsDurations[scatterPeriod];
	}

	@Override
	public void update(double delta) {
		checkIfGhostEnteredOrExitedTunnelAndSetSpeed();

		countSecondsPassedInCurrentPeriod();

		switch (ghost.getMode()) {
			case SCATTER:
				scatterModeRoutine();
				break;
			case CHASE:
				chaseModeRoutine();
				break;
			case FRIGHTENED:
				frightenedModeRoutine();
				break;
			case EATEN:
				ghost.setMovingSpeedViaMultiplier(Ghost.GHOST_HAS_BEEN_EATEN_MOVING_SPEED_MULTIPLIER);
				if (ghost.isRevived()) {
					changeGhostModeTo(ghost.getPreviousMode());
					ghost.setMovingSpeedViaMultiplier(ghost.getBeforeFrightenedMovingSpeedMultiplier());
					frightenedModeInitFlag = false;
				}
				break;
			default:
				System.err.println("Unexpected error: unsupported ghost mode!");
		}
	}

	public void checkIfGhostEnteredOrExitedTunnelAndSetSpeed() {
		Point currentPosition = ghost.getPosition();
		Direction currentDirection = ghost.getDirection();
		final int numberOfTunnels = levelController.getLevel().getMaze().getLeftTunnelEntranceTile().size();
		Point leftTunnelEntrance;
		Point rightTunnelEntrance;

		for (int i = 0; i < numberOfTunnels; i++) {
			leftTunnelEntrance = levelController.getLevel().getMaze().getLeftTunnelEntranceTile().get(i);
			rightTunnelEntrance = levelController.getLevel().getMaze().getRightTunnelEntranceTile().get(i);

			if (((currentPosition.equals(leftTunnelEntrance) && currentDirection == Direction.LEFT)
					|| (currentPosition.equals(rightTunnelEntrance) && currentDirection == Direction.RIGHT))
					&& !ghost.isInsideInnerTunnel()) {
				ghost.setInsideInnerTunnel(true);
				// System.out.println("\n" + ghost.getName() + " entering tunnel...");
				// System.out.println("current speed is " + ghost.getMovingSpeed());
				ghost.setBeforeTunnelMovingSpeedMultiplier(ghost.getMovingSpeedMultiplier());
				// System.out.println("BeforeTunnelMovingSpeedMultiplier is " +
				// ghost.getBeforeTunnelMovingSpeedMultiplier());
				ghost.setMovingSpeedViaMultiplier(levelController.getLevel().getGhostTunnelMovingSpeedMultiplier());
				// System.out.println("changing speed to " + ghost.getMovingSpeed());
			}

			if (((currentPosition.equals(leftTunnelEntrance) && currentDirection == Direction.RIGHT)
					|| (currentPosition.equals(rightTunnelEntrance) && currentDirection == Direction.LEFT))
					&& ghost.isInsideInnerTunnel()) {
				ghost.setInsideInnerTunnel(false);
				// System.out.println("\n" + ghost.getName() + " exiting tunnel...");
				// System.out.println("current speed is " + ghost.getMovingSpeed());
				// System.out.println("BeforeTunnelMovingSpeedMultiplier is " +
				// ghost.getBeforeTunnelMovingSpeedMultiplier());
				ghost.setMovingSpeedViaMultiplier(ghost.getBeforeTunnelMovingSpeedMultiplier());
				// System.out.println("changing speed to " + ghost.getMovingSpeed());
			}
		}
	}

	public void countSecondsPassedInCurrentPeriod() {
		if (!timerSet) {
			timer = System.currentTimeMillis();
			timerSet = true;
		}

		if (System.currentTimeMillis() - timer > 100) { // precision of 100 milliseconds
			if (!Game.paused && !Game.forcedPaused) {
				oneHundredMillisecondsCounter++;
				if ((oneHundredMillisecondsCounter > 0) && (oneHundredMillisecondsCounter % 10 == 0)) {
					secondsPassedInCurrentPeriod++; // one second has passed in current period
				}
			}
			timer += 100;
		}
	}

	public void scatterModeRoutine() {
		if (hasCurrentPeriodEnded()) {
			// System.out.println("DEBUGGING: " + ghost.getName() + " just got to Scatter
			// Mode");
			scatterPeriod++;
			changeGhostModeTo(Mode.CHASE);
			ghost.setReverseDirection(true);
		}
	}

	public void chaseModeRoutine() {
		if (hasCurrentPeriodEnded()) {
			// System.out.println("DEBUGGING: " + ghost.getName() + " just got to Chase
			// Mode");
			chasePeriod++;
			changeGhostModeTo(Mode.SCATTER);
			ghost.setReverseDirection(true);
		}
	}

	public void frightenedModeRoutine() {
		// frightened mode initialization block
		if (!frightenedModeInitFlag) {
			// System.out.println("DEBUGGING: " + ghost.getName() + " just got to Frightened
			// Mode");
			// System.out.println("DEBUGGING... in initialization block");
			// System.out.println("time passed = " + (oneHundredMilliseconds * 100) + "
			// ms");

			ghost.setReverseDirection(true);
			if (ghost.isInsideInnerTunnel()) {
				if (ghost instanceof Blinky) {
					if (((Blinky) ghost).isCruiseElroy1()) {
						ghost.setBeforeFrightenedMovingSpeedMultiplier(
								levelController.getLevel().getElroy1MovingSpeedMultiplier());
					} else if (((Blinky) ghost).isCruiseElroy2()) {
						ghost.setBeforeFrightenedMovingSpeedMultiplier(
								levelController.getLevel().getElroy2MovingSpeedMultiplier());
					}
				} else {
					ghost.setBeforeFrightenedMovingSpeedMultiplier(ghost.getBeforeTunnelMovingSpeedMultiplier());
				}
				ghost.setBeforeTunnelMovingSpeedMultiplier(
						levelController.getLevel().getGhostFrightenedMovingSpeedMultiplier());
			} else { // ghost is outside tunnel
				ghost.setBeforeFrightenedMovingSpeedMultiplier(ghost.getMovingSpeedMultiplier());
				ghost.setMovingSpeedViaMultiplier(levelController.getLevel().getGhostFrightenedMovingSpeedMultiplier());
			}

			frightenedModeInitFlag = true;
		}

		// frightened mode flash period started
		if (!frightenedModeFlashFlag && hasFrightenedModeFlashPeriodStarted()) {
			// System.out.println("DEBUGGING: " + ghost.getName() + " just started
			// flashing");
			// System.out.println("time passed = " + (oneHundredMilliseconds * 100) + "
			// ms");
			ghost.setFlash(true);
			frightenedModeFlashFlag = true;
		}

		// frightened mode period ended
		if (!frightenedModeEndFlag && hasCurrentPeriodEnded()) {
			// System.out.println("DEBUGGING: " + ghost.getName() + " getting out of
			// Frightened mode!");
			// System.out.println("time passed = " + (oneHundredMilliseconds * 100) + "
			// ms");
			// restore previous non-frightened mode
			changeGhostModeTo(ghost.getPreviousMode());

			if (ghost.isInsideInnerTunnel()) {
				ghost.setBeforeTunnelMovingSpeedMultiplier(ghost.getBeforeFrightenedMovingSpeedMultiplier());
			} else { // ghost is outside inner tunnel
				ghost.setMovingSpeedViaMultiplier(ghost.getBeforeFrightenedMovingSpeedMultiplier());
			}

			ghost.setFlash(false);
			if (pacman.isInPowerMode()) {
				pacman.setInPowerMode(false);
				pacman.setMovingSpeedViaMultiplier(levelController.getLevel().getPacmanNormalMovingSpeedMultiplier());
			}
			frightenedModeEndFlag = true;
			frightenedModeInitFlag = false;
		}
	}

	public boolean hasCurrentPeriodEnded() {
		boolean periodEnded = false;
		if (ghost.getMode() == Mode.FRIGHTENED
				|| (scatterPeriod < NUMBER_OF_SCATTER_PERIODS && chasePeriod < NUMBER_OF_CHASE_PERIODS)) {
			periodEnded = (secondsPassedInCurrentPeriod == currentPeriodDuration) ? true : false;
		}
		return periodEnded;
	}

	public boolean hasFrightenedModeFlashPeriodStarted() {
		return (secondsPassedInCurrentPeriod == currentPeriodDuration - flashDuration) ? true : false;
	}

	public void changeGhostModeTo(Mode newMode) {
		Mode currentMode = ghost.getMode();
		switch (newMode) {
			case SCATTER:
				if (currentMode == Mode.CHASE) {
					ghost.setPreviousMode(Mode.CHASE);
					oneHundredMillisecondsCounter = INIT_TIME;
					secondsPassedInCurrentPeriod = 0;
				} else {
					secondsPassedInCurrentPeriod = secondsPassedInLastNonFrightenedPeriod;
				}
				currentPeriodDuration = scatterPeriodsDurations[scatterPeriod];
				ghost.setMode(newMode);
				break;
			case CHASE:
				if (currentMode == Mode.SCATTER) {
					ghost.setPreviousMode(Mode.SCATTER);
					oneHundredMillisecondsCounter = INIT_TIME;
					secondsPassedInCurrentPeriod = 0;
				} else {
					secondsPassedInCurrentPeriod = secondsPassedInLastNonFrightenedPeriod;
				}
				currentPeriodDuration = chasePeriodsDurations[chasePeriod];
				ghost.setMode(newMode);
				break;
			case FRIGHTENED:
				// special case for when fright time duration is 0 sec, the only thing
				// that happens is that ghosts change direction
				if (frightenedDuration == 0) {
					ghost.setReverseDirection(true);
					return;
				}
				if (currentMode != Mode.FRIGHTENED) {
					secondsPassedInLastNonFrightenedPeriod = secondsPassedInCurrentPeriod;
					ghost.setPreviousMode(currentMode);
				}
				oneHundredMillisecondsCounter = INIT_TIME;
				secondsPassedInCurrentPeriod = 0;
				currentPeriodDuration = frightenedDuration;
				ghost.setMode(newMode);
				break;
			case EATEN:
				ghost.setMode(newMode);
		}
	}

	public void reset() {
		scatterPeriod = 0;
		chasePeriod = 0;
		timerSet = false;
		secondsPassedInCurrentPeriod = 0;
		secondsPassedInLastNonFrightenedPeriod = 0;
		currentPeriodDuration = scatterPeriodsDurations[scatterPeriod];
		frightenedModeInitFlag = false;
		frightenedModeFlashFlag = false;
		frightenedModeEndFlag = false;
	}

	public static void setModePeriodsDurations(int[] scatterPeriodsDurations, int[] chasePeriodsDurations,
			int frightenedDuration, int numberOfFlashes) {
		System.arraycopy(scatterPeriodsDurations, 0, GhostStatusController.scatterPeriodsDurations, 0,
				scatterPeriodsDurations.length);
		System.arraycopy(chasePeriodsDurations, 0, GhostStatusController.chasePeriodsDurations, 0,
				chasePeriodsDurations.length);
		GhostStatusController.frightenedDuration = frightenedDuration;

		if (numberOfFlashes == 5) {
			flashDuration = TWO_SECONDS;
		} else if (numberOfFlashes == 3) {
			flashDuration = ONE_SECOND;
		}
	}

	public static int getFlashDuration() {
		return flashDuration;
	}

	public int getScatterPeriod() {
		return scatterPeriod;
	}

	public int getChasePeriod() {
		return chasePeriod;
	}

	public int getCurrentPeriodDuration() {
		return currentPeriodDuration;
	}

	public int getOneHundredMillisecondsCounter() {
		return oneHundredMillisecondsCounter;
	}

	public void setOneHundredMillisecondsCounter(int oneHundredMillisecondsCounter) {
		this.oneHundredMillisecondsCounter = oneHundredMillisecondsCounter;
	}

	public int getSecondsPassedInCurrentPeriod() {
		return secondsPassedInCurrentPeriod;
	}

	public void setSecondsPassedInCurrentPeriod(int secondsPassedInCurrentPeriod) {
		this.secondsPassedInCurrentPeriod = secondsPassedInCurrentPeriod;
	}

	public int getSecondsPassedInLastNonFrightenedPeriod() {
		return secondsPassedInLastNonFrightenedPeriod;
	}

	public void setSecondsPassedInLastNonFrightenedPeriod(int secondsPassedInLastNonFrightenedPeriod) {
		this.secondsPassedInLastNonFrightenedPeriod = secondsPassedInLastNonFrightenedPeriod;
	}

	public void setFrightenedModeInitFlag(boolean frightenedModeInitFlag) {
		this.frightenedModeInitFlag = frightenedModeInitFlag;
	}

	public void setFrightenedModeFlashFlag(boolean frightenedModeFlashFlag) {
		this.frightenedModeFlashFlag = frightenedModeFlashFlag;
	}

	public void setFrightenedModeEndFlag(boolean frightenedModeEndFlag) {
		this.frightenedModeEndFlag = frightenedModeEndFlag;
	}
}
