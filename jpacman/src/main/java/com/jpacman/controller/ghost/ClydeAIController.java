package com.jpacman.controller.ghost;

import java.awt.Point;

import com.jpacman.model.Maze;
import com.jpacman.model.Pacman;
import com.jpacman.model.ghost.Clyde;
import com.jpacman.util.Utility;

public class ClydeAIController extends GhostAIController {
	public ClydeAIController(Clyde clyde, Pacman pacman) {
		super(clyde, pacman);
	}

	@Override
	public Point computeChaseModeTargetTile() {
		ghost.setChaseModeTargetTile(new Point());

		final int eightTiles = 8 * Maze.TILE;
		Point clydeCurrentTile = ghost.getCurrentTile();
		Point pacmanCurrentTile = pacman.getCurrentTile();
		double distance = Utility.computeEuclideanDistance(clydeCurrentTile, pacmanCurrentTile);
		if (distance >= eightTiles) {
			// chaseModeTargetTile = pacmanCurrentPosition;
			ghost.setChaseModeTargetTile(pacmanCurrentTile);
		} else {
			ghost.setChaseModeTargetTile(ghost.getScatterModeTargetTile());
		}

		return ghost.getChaseModeTargetTile();
	}
}
