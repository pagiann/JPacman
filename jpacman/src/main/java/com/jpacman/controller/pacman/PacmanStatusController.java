package com.jpacman.controller.pacman;

import com.jpacman.Game;
import com.jpacman.SoundEffect;
import com.jpacman.controller.GameStatusController;
import com.jpacman.controller.LevelController;
import com.jpacman.controller.ghost.GhostAIController;
import com.jpacman.controller.ghost.GhostStatusController;
import com.jpacman.model.Pacman;
import com.jpacman.model.ghost.Ghost;
import com.jpacman.util.Timer;

public class PacmanStatusController extends PacmanController
{
    public static Timer timeElapsedSinceAtePillTimer = new Timer();
    public static Timer timer = new Timer();

    public static int secondsSincePillEaten = -1;

    private static boolean pacmanInPowerModeFlag;
    private static boolean pacmanDiedFlag;

    private final GhostStatusController[] ghostsStatusControllers;
    private final LevelController levelController;

    public PacmanStatusController(Pacman pacman, GhostStatusController[] ghostsStatusControllers, LevelController levelController)
    {
	super(pacman, null);
	this.ghostsStatusControllers = ghostsStatusControllers;
	this.levelController = levelController;
    }

    @Override
    public void update(double delta)
    {
	if (!pacmanInPowerModeFlag && pacman.isInPowerMode()) {
	    // ghosts got in frightened mode for the first time, set respective counters
	    for (GhostStatusController controller : ghostsStatusControllers) {
		// controller.setFrightenedModeInitFlag(false);
		controller.setFrightenedModeFlashFlag(false);
		controller.setFrightenedModeEndFlag(false);
		controller.getGhost().setFlash(false);
		if (controller.getGhost().getMode() != Ghost.Mode.EATEN) {
		    controller.changeGhostModeTo(Ghost.Mode.FRIGHTENED);
		}
	    }
	    pacmanInPowerModeFlag = true;
	}

	if (pacmanDiedFlag && timer.getTimeElapsedInMilliseconds() == Timer.ONE_SECOND) {
	    GameStatusController.setLifeLost(true);
	    pacman.setDied(true);
	    pacman.setMoving(false);
	    pacman.setAnimate(true);
	    pacman.setAnimationCounter(0);
	    pacman.setAnimationSpeed(160); // longer = slower dying animation
	    pacmanDiedFlag = false;
	    SoundEffect.PACMAN_DIES.play();
	}

	if (pacmanDiedFlag) {
	    timer.countTimeInMilliseconds();
	    return;
	}

	if (Game.paused || Game.forcedPaused) {
	    timeElapsedSinceAtePillTimer.setStarted(false);
	} else {
	    timeElapsedSinceAtePillTimer.countTimeInMilliseconds();
	}

	if (levelController.getLevel().getValue() < 5) {
	    secondsSincePillEaten = Timer.FOUR_SECONDS;
	} else {
	    secondsSincePillEaten = Timer.THREE_SECONDS;
	}
	if (timeElapsedSinceAtePillTimer.getTimeElapsedInMilliseconds() == secondsSincePillEaten) {
	    timeElapsedSinceAtePillTimer.setTimeElapsedInMilliseconds(0);
	    GhostAIController.setSecElapsedSincePacmanAtePillExceedsLimit(true);
	}
    }

    public static void reset()
    {
	timeElapsedSinceAtePillTimer = new Timer();
	timer = new Timer();
	pacmanInPowerModeFlag = false;
	pacmanDiedFlag = false;
    }

    public static void setPacmanInPowerModeFlag(boolean pacmanInPowerModeFlag)
    {
	PacmanStatusController.pacmanInPowerModeFlag = pacmanInPowerModeFlag;
    }

    public static void setPacmanDiedFlag(boolean pacmanDiedFlag)
    {
	PacmanStatusController.pacmanDiedFlag = pacmanDiedFlag;
    }
}
