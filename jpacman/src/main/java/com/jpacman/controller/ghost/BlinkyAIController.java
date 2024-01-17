package com.jpacman.controller.ghost;

import java.awt.Point;

import com.jpacman.model.Pacman;
import com.jpacman.model.ghost.Blinky;

public class BlinkyAIController extends GhostAIController
{
    public BlinkyAIController(Blinky blinky, Pacman pacman)
    {
	super(blinky, pacman);
    }

    @Override
    public Point computeChaseModeTargetTile()
    {
	return pacman.getCurrentTile();
    }
}
