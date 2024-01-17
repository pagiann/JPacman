package jpacman.controller.pacman;

import java.awt.Point;

import jpacman.Game;
import jpacman.GameApplication;
import jpacman.input.Keyboard;
import jpacman.model.MovableGameObject.Direction;
import jpacman.model.Pacman;
import jpacman.util.PathFinder;

public class PacmanMoveController extends PacmanController
{
    private final Keyboard keyboard;

    public PacmanMoveController(Pacman pacman, Keyboard keyboard)
    {
	super(pacman, null);
	this.keyboard = keyboard;
    }

    @Override
    public void update(double delta)
    {
	// amount of SECONDS to delay when inside outer tunnel (= off screen)
	final double SECONDS = 0.5;

	if (pacman.isInsideOuterTunnel()) {
	    pacman.addDelta((1.0 / (SECONDS * GameApplication.UPDATES_PER_SECOND)) * delta);
	    if (pacman.getDelta() <= 1) {
		return;
	    } else {
		pacman.decreaseDeltaByOne();
		pacman.setInsideOuterTunnel(false);
	    }
	}

	pacman.addDelta((pacman.getMovingSpeed() / GameApplication.UPDATES_PER_SECOND) * delta);

	if (pacman.getDelta() >= 1) { // ups capped at object's current moving speed
	    movePacman();
	    pacman.decreaseDeltaByOne();
	}
    }

    private void movePacman()
    {
	if (Game.paused || (!Game.pacmanDying && Game.forcedPaused))
	    return;

	int animationCounter = pacman.getAnimationCounter();
	pacman.setAnimationCounter((animationCounter < 10000) ? animationCounter + 1 : 0);

	if (Game.pacmanDying)
	    return;

	// TODO change this when "cornering" will be implemented
	if (keyboard.isThereNewUserInput()) {
	    Direction lastInput = keyboard.getLastUserInput();
	    Point requestedTile = PathFinder.computeNextTile(pacman.getCurrentTile(), lastInput);

	    if (PathFinder.isNextTileInRoute(requestedTile)) {
		if (PathFinder.isIntersectionTile(pacman.getCurrentTile())) {
		    if (pacman.getPosition().equals(pacman.getCurrentTile())) {
			pacman.setDirection(lastInput);
			pacman.setNextTile(requestedTile);
			keyboard.clearAllPreviousUserInputs();
		    }
		} else {
		    pacman.setDirection(lastInput);
		    pacman.setNextTile(requestedTile);
		    keyboard.clearAllPreviousUserInputs();
		}
	    }
	}

	if (pacman.getPosition().equals(pacman.getNextTile())) {
	    pacman.setNextTile(PathFinder.computeNextTile(pacman.getCurrentTile(), pacman.getDirection()));
	}

	if (PathFinder.isNextTileInRoute(pacman.getNextTile())) {
	    pacman.setMoving(true);
	    pacman.move(pacman.getDirection());
	    pacman.setCurrentTile(PathFinder.computeCurrentTile(pacman.getPosition()));
	} else {
	    pacman.setMoving(false);
	}
    }

    public Keyboard getKeyboard()
    {
	return keyboard;
    }
}
