package jpacman.model.ghost;

import java.awt.Point;

import jpacman.model.Maze;
import jpacman.util.PathFinder;
import jpacman.view.ghost.PinkyRenderer;

public class Pinky extends Ghost
{
    public Pinky()
    {
	name = "PiNKY";
	color = 0xFFFFB9FF;
	sprite = PinkyRenderer.pinky_down_1;
	scatterModeTargetTile = new Point(2 * Maze.TILE + Maze.HALF_TILE, -4 * Maze.TILE + Maze.HALF_TILE);
    }

    @Override
    public void initialize(boolean levelCompleted)
    {
	initializeVariables(levelCompleted);
	position = new Point(startingPosition);
	direction = Direction.DOWN;
	nextTile = PathFinder.computeNextTileInGhostsHouse(position, direction);
	houseExitPreferenceNumber = 1;
    }
}
