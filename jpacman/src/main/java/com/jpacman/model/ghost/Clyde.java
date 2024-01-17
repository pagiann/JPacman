package com.jpacman.model.ghost;

import java.awt.Point;

import com.jpacman.model.Maze;
import com.jpacman.util.PathFinder;
import com.jpacman.view.ghost.ClydeRenderer;

public class Clyde extends Ghost {
	public Clyde() {
		name = "CLYDE";
		color = 0xFFFFB950;
		sprite = ClydeRenderer.clyde_up_1;
		scatterModeTargetTile = new Point(Maze.HALF_TILE, Maze.HEIGHT + Maze.HALF_TILE);
	}

	@Override
	public void initialize(boolean levelCompleted) {
		initializeVariables(levelCompleted);
		position = new Point(startingPosition);
		direction = Direction.UP;
		nextTile = PathFinder.computeNextTileInGhostsHouse(position, direction);
		houseExitPreferenceNumber = 3;
	}
}
