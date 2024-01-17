package com.jpacman.controller.ghost;

import java.awt.Point;

import com.jpacman.Game;
import com.jpacman.GameApplication;
import com.jpacman.model.Maze;
import com.jpacman.model.MovableGameObject.Direction;
import com.jpacman.model.Pacman;
import com.jpacman.model.ghost.Blinky;
import com.jpacman.model.ghost.Clyde;
import com.jpacman.model.ghost.Ghost;
import com.jpacman.model.ghost.Inky;
import com.jpacman.model.ghost.Pinky;
import com.jpacman.util.PathFinder;

public class GhostAIController extends GhostController {
	// ********************* Class (static) variables ********************** //
	public enum PreferredGhost {
		PINKY(1), INKY(2), CLYDE(3), NONE(4);

		private int value;

		private PreferredGhost(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	private static PreferredGhost preferredGhostToExitHouse = PreferredGhost.PINKY;
	private static boolean secElapsedSincePacmanAtePillExceedsLimit = false;
	private static int pillsEatenByPacmanGlobal = 0;

	private static Maze currentMaze;

	private static boolean globalCounterActive = false;
	private static boolean globalCounterActiveFlag = false;

	// ************************* instance variables ************************ //
	protected final Pacman pacman;

	public GhostAIController(Ghost ghost, Pacman pacman) {
		super(ghost, null);
		this.pacman = pacman;
	}

	@Override
	public void update(double delta) {
		// amount of SECONDS to delay when inside outer tunnel (= off screen)
		final double SECONDS = 0.5;

		if (ghost.isInsideOuterTunnel()) {
			ghost.addDelta((1.0 / (SECONDS * GameApplication.UPDATES_PER_SECOND)) * delta);
			if (ghost.getDelta() <= 1) {
				return;
			} else {
				ghost.decreaseDeltaByOne();
				ghost.setInsideOuterTunnel(false);
			}
		}

		// find a proper way to implement this
		/*
		 * if (ghost.isInsideHouse() && ghost.getMovingSpeedMultiplier() !=
		 * Ghost.GHOST_INSIDE_HOUSE_MOVING_SPEED_MULTIPLIER) {
		 * ghost.setBeforeExitedHouseSpeedMultiplier(ghost.getMovingSpeedMultiplier());
		 * ghost.setMovingSpeedViaMultiplier(Ghost.
		 * GHOST_INSIDE_HOUSE_MOVING_SPEED_MULTIPLIER); } else if
		 * (!ghost.isInsideHouse() && ghost.getMovingSpeedMultiplier() ==
		 * Ghost.GHOST_INSIDE_HOUSE_MOVING_SPEED_MULTIPLIER) { if
		 * (ghost.getBeforeExitedHouseSpeedMultiplier() != 0.0) {
		 * ghost.setMovingSpeedViaMultiplier(ghost.getBeforeExitedHouseSpeedMultiplier()
		 * );
		 * ghost.setBeforeExitedHouseSpeedMultiplier(0.0); } }
		 */

		ghost.addDelta((ghost.getMovingSpeed() / GameApplication.UPDATES_PER_SECOND) * delta);

		if (ghost.getDelta() >= 1) { // ups capped at object's current moving speed
			updateGhost();
			ghost.decreaseDeltaByOne();
		}
	}

	public void increasePillsEatenByPacmanByOne() {
		if (ghost.isInsideHouse()) {
			if (ghost instanceof Pinky && (preferredGhostToExitHouse == PreferredGhost.PINKY)) {
				ghost.increasePillsEatenByPacmanByOne();
			} else if (ghost instanceof Inky && (preferredGhostToExitHouse == PreferredGhost.INKY)) {
				ghost.increasePillsEatenByPacmanByOne();
			} else if (ghost instanceof Clyde && (preferredGhostToExitHouse == PreferredGhost.CLYDE)) {
				ghost.increasePillsEatenByPacmanByOne();
			}
		}
	}

	protected Point computeChaseModeTargetTile() {
		return ghost.getChaseModeTargetTile();
	}

	private void updateGhost() {
		ghost.setAnimationCounter((ghost.getAnimationCounter() < 10000) ? ghost.getAnimationCounter() + 1 : 0);

		if (Game.paused || Game.forcedPaused)
			return;

		// ghost stays inside the house until the number of pills eaten by pacman
		// reaches the individual ghost's limit
		// so it gets out
		if (ghost.isInsideHouse()) {
			if (!ghost.getMustExitTheHouseFlag()) {
				if (ghost instanceof Blinky) {
					ghost.setMustExitTheHouse(true);
					ghost.setTargetTile(currentMaze.getGhostsHouseBehindDoorTile());
					ghost.setMustExitTheHouseFlag(true);
				} else {
					setPreferredGhostToExitHouse();

					if (globalCounterActive) {
						if (!globalCounterActiveFlag) {
							pillsEatenByPacmanGlobal = 0;
							globalCounterActiveFlag = true;
						}
						if ((ghost instanceof Pinky && pillsEatenByPacmanGlobal == 7)
								|| (ghost instanceof Inky && pillsEatenByPacmanGlobal == 17)) {
							ghost.setMustExitTheHouse(true);
							ghost.setTargetTile(currentMaze.getGhostsHouseBehindDoorTile());
							ghost.setMustExitTheHouseFlag(true);
						} else if (ghost instanceof Clyde && pillsEatenByPacmanGlobal == 32) {
							pillsEatenByPacmanGlobal = 0;
							globalCounterActive = false;
							globalCounterActiveFlag = false;
						}
					} else if (ghost.getPillsEatenByPacman() >= ghost.getPillsEatenLimit()) {
						// System.out.println(ghost.getClass() + " is exiting house..");
						ghost.setMustExitTheHouse(true);
						ghost.setTargetTile(currentMaze.getGhostsHouseBehindDoorTile());
						ghost.setMustExitTheHouseFlag(true);
					}

					if (secElapsedSincePacmanAtePillExceedsLimit
							&& ghost.getHouseExitPreferenceNumber() == preferredGhostToExitHouse.getValue()) {
						ghost.setMustExitTheHouse(true);
						ghost.setTargetTile(currentMaze.getGhostsHouseBehindDoorTile());
						ghost.setMustExitTheHouseFlag(true);
						secElapsedSincePacmanAtePillExceedsLimit = false;
					}
				}
			}

			if (ghost.getMustExitTheHouse()) {
				exitTheHouse();
			} else {
				stayInsideTheHouse();
			}
		} else { // ghost is outside the house
			if (!ghost.getMustEnterTheHouseFlag() && (ghost.getMode() == Ghost.Mode.EATEN)
					&& ghost.getPosition().equals(currentMaze.getGhostsHouseInFrontOfDoorTile())) {
				ghost.setDirection(Direction.DOWN);
				if (ghost instanceof Blinky) {
					ghost.setTargetTile(currentMaze.getGhostHouseCenter());
				} else {
					ghost.setTargetTile(ghost.getStartingPosition());
				}
				ghost.setMustEnterTheHouse(true);
				ghost.setMustEnterTheHouseFlag(true);
			}

			if (ghost.getMustEnterTheHouse()) {
				enterTheHouse();
			} else {
				decideStrategy();
			}
		}
	}

	private void setPreferredGhostToExitHouse() {
		if (ghost instanceof Pinky && (ghost.getHouseExitPreferenceNumber() < preferredGhostToExitHouse.getValue())) {
			preferredGhostToExitHouse = PreferredGhost.PINKY;
		} else if (ghost instanceof Inky
				&& (ghost.getHouseExitPreferenceNumber() < preferredGhostToExitHouse.getValue())) {
			preferredGhostToExitHouse = PreferredGhost.INKY;
		} else if (ghost instanceof Clyde
				&& (ghost.getHouseExitPreferenceNumber() < preferredGhostToExitHouse.getValue())) {
			preferredGhostToExitHouse = PreferredGhost.CLYDE;
		}
	}

	private void exitTheHouse() {
		ghost.move(ghost.getDirection());

		// only for Inky and Clyde because Pinky only moves up-and-down inside house
		if (!(ghost instanceof Pinky) && PathFinder.isGhostHouseIntersectionTile(ghost.getPosition())) {
			// System.out.println("We're at an intersection");
			// System.out.println("current ghost.getPosition() = " + ghost.getPosition());
			// System.out.println("next tile = " + ghost.getNextTile());
			ghost.setDirection(
					PathFinder.reachTargetTile(ghost.getPosition(), ghost.getTargetTile(), ghost.getDirection()));
			// System.out.println("ghost.getDirection() = " + ghost.getDirection());
		}

		hasReachedNextTileInsideHouse(true);

		if (ghost.getPosition().equals(currentMaze.getGhostsHouseInFrontOfDoorTile())) {
			// System.out.println("We're in front of the house's door");
			ghost.setCurrentTile(PathFinder.computeCurrentTile(ghost.getPosition()));
			ghost.setDirection(Direction.LEFT);
			ghost.setNextTile(PathFinder.computeNextTile(ghost.getCurrentTile(), ghost.getDirection()));
			ghost.setTargetTile(ghost.getScatterModeTargetTile());
			ghost.setNextDirection(
					PathFinder.reachTargetTile(ghost.getNextTile(), ghost.getTargetTile(), ghost.getDirection()));

			ghost.setInsideHouse(false);
			ghost.setMustExitTheHouse(false);
			ghost.setMustExitTheHouseFlag(false);

			preferredGhostToExitHouse = PreferredGhost.NONE;
		}
	}

	private void stayInsideTheHouse() {
		ghost.move(ghost.getDirection());
		hasReachedNextTileInsideHouse(false);
	}

	private void enterTheHouse() {
		ghost.move(ghost.getDirection());

		// only for Inky and Clyde because Blinky and Pinky only moves up-and-down
		// inside house
		if ((ghost instanceof Inky || ghost instanceof Clyde)
				&& PathFinder.isGhostHouseIntersectionTile(ghost.getPosition())) {
			ghost.setDirection(
					PathFinder.reachTargetTile(ghost.getPosition(), ghost.getTargetTile(), ghost.getDirection()));
		}

		hasReachedNextTileInsideHouse(true);

		if (ghost.getPosition().equals(ghost.getTargetTile())) {
			ghost.setInsideHouse(true);
			ghost.setRevived(true);
			ghost.setMustEnterTheHouse(false);
			ghost.setMustEnterTheHouseFlag(false);
			ghost.setNextTile(PathFinder.computeNextTileInGhostsHouse(ghost.getPosition(), ghost.getDirection()));
		}
	}

	private void hasReachedNextTileInsideHouse(boolean mustGetOutOfTheHouse) {
		if (ghost.getPosition().equals(ghost.getNextTile())) {
			// System.out.println("we reached next tile");
			Point nextOfNextTile = PathFinder.computeNextTileInGhostsHouse(ghost.getPosition(), ghost.getDirection());
			if (!PathFinder.isNextTileInGhostHouseRoute(nextOfNextTile)
					|| (!mustGetOutOfTheHouse && nextOfNextTile.equals(currentMaze.getGhostsHouseBehindDoorTile()))) {
				// System.out.println("next tile is not in route, go back");
				ghost.setDirection(Direction.getOpposite(ghost.getDirection()));
				ghost.setNextTile(PathFinder.computeNextTileInGhostsHouse(ghost.getPosition(), ghost.getDirection()));
				// System.out.println("next tile 2 = " + ghost.getNextTile());
			} else {
				// System.out.println("ghost.getDirection() = " + ghost.getDirection());
				ghost.setNextTile(PathFinder.computeNextTileInGhostsHouse(ghost.getNextTile(), ghost.getDirection()));
			}
		}
	}

	private void decideStrategy() {
		switch (ghost.getMode()) {
			case SCATTER:
				ghost.setTargetTile(ghost.getScatterModeTargetTile());
				if (ghost instanceof Blinky
						&& (((Blinky) ghost).isCruiseElroy1() || ((Blinky) ghost).isCruiseElroy2())) {
					ghost.setTargetTile(computeChaseModeTargetTile());
				}
				if (PathFinder.isIntersectionTile(ghost.getNextTile())) {
					ghost.setNextDirection(PathFinder.reachTargetTile(ghost.getNextTile(), ghost.getTargetTile(),
							ghost.getDirection()));
				}
				break;
			case CHASE:
				ghost.setTargetTile(computeChaseModeTargetTile());
				if (PathFinder.isIntersectionTile(ghost.getNextTile())) {
					ghost.setNextDirection(PathFinder.reachTargetTile(ghost.getNextTile(), ghost.getTargetTile(),
							ghost.getDirection()));
				}
				break;
			case FRIGHTENED:
				if (PathFinder.isIntersectionTile(ghost.getNextTile())) {
					ghost.setNextDirection(
							PathFinder.getNewValidRandomDirection(ghost.getNextTile(), ghost.getDirection()));
				}
				break;
			case EATEN:
				ghost.setTargetTile(currentMaze.getGhostsHouseInFrontOfDoorTile());
				ghost.setNextDirection(
						PathFinder.reachTargetTile(ghost.getNextTile(), ghost.getTargetTile(), ghost.getDirection()));
		}

		if (ghost.getPosition().equals(ghost.getNextTile())) {
			if (ghost.getReverseDirection() && !(ghost.getPosition().equals(currentMaze.getLeftTunnelEntranceTile())
					|| ghost.getPosition().equals(currentMaze.getRightTunnelEntranceTile()))) {
				ghost.setNextDirection(Direction.getOpposite(ghost.getDirection()));
				ghost.setReverseDirection(false);
			}
			ghost.setDirection(ghost.getNextDirection());
			ghost.setNextTile(PathFinder.computeNextTile(ghost.getNextTile(), ghost.getDirection()));
		}

		ghost.move(ghost.getDirection());
		ghost.setCurrentTile(PathFinder.computeCurrentTile(ghost.getPosition()));
	}

	public static void reset() {
		preferredGhostToExitHouse = PreferredGhost.PINKY;
		secElapsedSincePacmanAtePillExceedsLimit = false;
		globalCounterActive = false;
		globalCounterActiveFlag = false;
		pillsEatenByPacmanGlobal = 0;
	}

	public static PreferredGhost getPreferredGhostToExitHouse() {
		return preferredGhostToExitHouse;
	}

	public static void setPreferredGhostToExitHouse(PreferredGhost preferredGhostToExitHouse) {
		GhostAIController.preferredGhostToExitHouse = preferredGhostToExitHouse;
	}

	public static boolean getSecElapsedSincePacmanAtePillExceedsLimit() {
		return secElapsedSincePacmanAtePillExceedsLimit;
	}

	public static void setSecElapsedSincePacmanAtePillExceedsLimit(boolean secElapsedSincePacmanAtePillExceedsLimit) {
		GhostAIController.secElapsedSincePacmanAtePillExceedsLimit = secElapsedSincePacmanAtePillExceedsLimit;
	}

	public static void setCurrentMaze(Maze currentMaze) {
		GhostAIController.currentMaze = currentMaze;
	}

	public static boolean isGlobalCounterActive() {
		return globalCounterActive;
	}

	public static void setGlobalCounterActive(boolean globalCounterActive) {
		GhostAIController.globalCounterActive = globalCounterActive;
		globalCounterActiveFlag = false;
	}

	public static boolean isGlobalCounterActiveFlag() {
		return globalCounterActiveFlag;
	}

	public static void setGlobalCounterActiveFlag(boolean globalCounterActiveFlag) {
		GhostAIController.globalCounterActiveFlag = globalCounterActiveFlag;
	}

	public static void increasePillsEatenByPacmanGlobalByOne() {
		pillsEatenByPacmanGlobal++;
	}
}
