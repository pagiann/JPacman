package jpacman.controller.ghost;

import java.awt.Point;

import jpacman.model.Maze;
import jpacman.model.Pacman;
import jpacman.model.ghost.Pinky;

public class PinkyAIController extends GhostAIController
{
    public PinkyAIController(Pinky pinky, Pacman pacman)
    {
	super(pinky, pacman);
    }

    @Override
    public Point computeChaseModeTargetTile()
    {
	ghost.setChaseModeTargetTile(new Point(pacman.getCurrentTile()));
	final int FOUR_TILES = 4 * Maze.TILE;
	switch (pacman.getDirection()) {
	    case UP:
		ghost.getChaseModeTargetTile().y -= FOUR_TILES;
		break;
	    case DOWN:
		ghost.getChaseModeTargetTile().y += FOUR_TILES;
		break;
	    case LEFT:
		ghost.getChaseModeTargetTile().x -= FOUR_TILES;
		break;
	    case RIGHT:
		ghost.getChaseModeTargetTile().x += FOUR_TILES;
		break;
	}
	return ghost.getChaseModeTargetTile();
    }
}
