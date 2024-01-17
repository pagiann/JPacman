package jpacman.model.ghost;

import java.awt.Point;

import jpacman.model.Maze;
import jpacman.util.PathFinder;
import jpacman.view.ghost.InkyRenderer;

public class Inky extends Ghost
{
    public Inky()
    {
	name = "iNKY";
	color = 0xFF00FFFF;
	sprite = InkyRenderer.inky_up_1;
	scatterModeTargetTile = new Point(Maze.WIDTH - Maze.HALF_TILE, Maze.HEIGHT + Maze.HALF_TILE);
    }

    @Override
    public void initialize(boolean levelCompleted)
    {
	initializeVariables(levelCompleted);
	position = new Point(startingPosition);
	direction = Direction.UP;
	nextTile = PathFinder.computeNextTileInGhostsHouse(position, direction);
	houseExitPreferenceNumber = 2;
    }
}
