package com.jpacman.model.ghost;

import java.awt.Point;

import com.jpacman.model.Maze;
import com.jpacman.util.PathFinder;
import com.jpacman.view.ghost.InkyRenderer;

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
