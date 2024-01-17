package com.jpacman.controller.ghost;

import java.awt.Point;

import com.jpacman.model.Maze;
import com.jpacman.model.Pacman;
import com.jpacman.model.ghost.Inky;

public class InkyAIController extends GhostAIController
{
    private Point blinkyCurrentTile;

    public InkyAIController(Inky inky, Pacman pacman)
    {
	super(inky, pacman);
	blinkyCurrentTile = new Point();
    }

    public Point centerTile = new Point(); // used in debugger class for debugging, remove at the end

    @Override
    public Point computeChaseModeTargetTile()
    {
	ghost.setChaseModeTargetTile(new Point());
	// centerTile = new Point(pacmanCurrentPosition);
	centerTile = new Point(pacman.getCurrentTile());
	// Point centerTile = new Point(pacmanCurrentPosition);
	// Point centerTile = new Point(pacmanCurrentTile);
	final int TWO_TILES = 2 * Maze.TILE;
	switch (pacman.getDirection()) {
	    case UP:
		centerTile.y -= TWO_TILES;
		break;
	    case DOWN:
		centerTile.y += TWO_TILES;
		break;
	    case LEFT:
		centerTile.x -= TWO_TILES;
		break;
	    case RIGHT:
		centerTile.x += TWO_TILES;
		break;
	}

	if (blinkyCurrentTile.x > centerTile.x) {
	    ghost.getChaseModeTargetTile().x = centerTile.x - (blinkyCurrentTile.x - centerTile.x);
	} else {
	    ghost.getChaseModeTargetTile().x = centerTile.x + (centerTile.x - blinkyCurrentTile.x);
	}
	if (blinkyCurrentTile.y > centerTile.y) {
	    ghost.getChaseModeTargetTile().y = centerTile.y - (blinkyCurrentTile.y - centerTile.y);
	} else {
	    ghost.getChaseModeTargetTile().y = centerTile.y + (centerTile.y - blinkyCurrentTile.y);
	}

	return ghost.getChaseModeTargetTile();
    }

    public void setBlinkyCurrentTile(Point blinkyCurrentTile)
    {
	this.blinkyCurrentTile = new Point(blinkyCurrentTile);
    }
}
