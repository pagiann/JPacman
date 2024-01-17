package com.jpacman.model.ghost;

import java.awt.Point;

import com.jpacman.model.Maze;
import com.jpacman.util.PathFinder;
import com.jpacman.view.ghost.PinkyRenderer;

public class Pinky extends Ghost {
	public Pinky() {
		name = "PiNKY";
		color = 0xFFFFB9FF;
		sprite = PinkyRenderer.pinky_down_1;
		scatterModeTargetTile = new Point(2 * Maze.TILE + Maze.HALF_TILE, -4 * Maze.TILE + Maze.HALF_TILE);
	}

	@Override
	public void initialize(boolean levelCompleted) {
		initializeVariables(levelCompleted);
		position = new Point(startingPosition);
		direction = Direction.DOWN;
		nextTile = PathFinder.computeNextTileInGhostsHouse(position, direction);
		houseExitPreferenceNumber = 1;
	}
}
