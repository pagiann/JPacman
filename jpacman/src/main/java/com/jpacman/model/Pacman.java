package com.jpacman.model;

import java.awt.Point;
import java.awt.geom.Point2D;

import com.jpacman.util.PathFinder;
import com.jpacman.view.PacmanRenderer;

public class Pacman extends MovableGameObject
{
    private boolean collidedWithPill;
    private boolean atePill;
    private boolean atePowerPill;
    private boolean inPowerMode;
    private boolean died;
    private int lives = 3;
    private int ghostsEaten;

    // used in educational mode - Classic TSP
    private Point2D.Float presicePosition = new Point2D.Float();

    public void initialize()
    {
	position = new Point(startingPosition);
	sprite = PacmanRenderer.pacman_closed_mouth;
	direction = Direction.LEFT;
	animationSpeed = NORMAL_ANIMATION_SPEED;
	animationCounter = 0;
	isMoving = false;
	distanceMoved = 0;
	currentTile = PathFinder.computeCurrentTile(position);
	nextTile = PathFinder.computeNextTile(currentTile, direction);
	insideInnerTunnel = false;
	insideOuterTunnel = false;
	collidedWithPill = false;
	atePill = true;
	atePowerPill = true;
	inPowerMode = false;
	died = false;
	ghostsEaten = 0;
    }

    public boolean getCollidedWithPill()
    {
	return collidedWithPill;
    }

    public void setCollidedWithPill(boolean collidedWithPill)
    {
	this.collidedWithPill = collidedWithPill;
    }

    public boolean getAtePill()
    {
	return atePill;
    }

    public void setAtePill(boolean atePill)
    {
	this.atePill = atePill;
    }

    public boolean getAtePowerPill()
    {
	return atePowerPill;
    }

    public void setAtePowerPill(boolean atePowerPill)
    {
	this.atePowerPill = atePowerPill;
    }

    public boolean isInPowerMode()
    {
	return inPowerMode;
    }

    public void setInPowerMode(boolean inPowerMode)
    {
	this.inPowerMode = inPowerMode;
    }

    public boolean getDied()
    {
	return died;
    }

    public void setDied(boolean died)
    {
	this.died = died;
    }

    public int getLives()
    {
	return lives;
    }

    public void setLives(int lives)
    {
	this.lives = lives;
    }

    public void increaseLivesByOne()
    {
	lives++;
    }

    public void decreaseLivesByOne()
    {
	lives--;
    }

    public int getGhostsEaten()
    {
	return ghostsEaten;
    }

    public void setGhostsEaten(int ghostsEaten)
    {
	this.ghostsEaten = ghostsEaten;
    }

    public void increaseGhostsEatenByOne()
    {
	ghostsEaten++;
    }

    public Point2D.Float getPresicePosition()
    {
	return presicePosition;
    }

    public void setPresicePosition(Point2D.Float presicePosition)
    {
	this.presicePosition = presicePosition;
    }
}
