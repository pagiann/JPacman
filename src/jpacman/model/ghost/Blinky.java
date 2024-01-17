package jpacman.model.ghost;

import java.awt.Point;

import jpacman.model.Maze;
import jpacman.util.PathFinder;
import jpacman.view.ghost.BlinkyRenderer;

public class Blinky extends Ghost
{
    private boolean cruiseElroy1 = false;
    private boolean cruiseElroy2 = false;

    public Blinky()
    {
	name = "BLiNKY";
	color = 0xFFFF0000;
	sprite = BlinkyRenderer.blinky_up_1;
	scatterModeTargetTile = new Point(Maze.WIDTH - 3 * Maze.TILE + Maze.HALF_TILE, -4 * Maze.TILE + Maze.HALF_TILE);
    }

    @Override
    public void initialize(boolean levelCompleted)
    {
	initializeVariables(levelCompleted);
	position = new Point(startingPosition);
	direction = Direction.LEFT;
	currentTile = PathFinder.computeCurrentTile(position);
	nextTile = PathFinder.computeNextTile(currentTile, direction);
	targetTile = scatterModeTargetTile;
	nextDirection = PathFinder.reachTargetTile(nextTile, targetTile, direction);
	insideHouse = false;
	houseExitPreferenceNumber = 0;
	if (levelCompleted) {
	    cruiseElroy1 = false;
	    cruiseElroy2 = false;
	}
    }

    public boolean isCruiseElroy1()
    {
	return cruiseElroy1;
    }

    public void setCruiseElroy1(boolean cruiseElroy1)
    {
	this.cruiseElroy1 = cruiseElroy1;
    }

    public boolean isCruiseElroy2()
    {
	return cruiseElroy2;
    }

    public void setCruiseElroy2(boolean cruiseElroy2)
    {
	this.cruiseElroy2 = cruiseElroy2;
    }
}
