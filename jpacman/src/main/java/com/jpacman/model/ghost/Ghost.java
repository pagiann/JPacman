package com.jpacman.model.ghost;

import java.awt.Point;

import com.jpacman.model.MovableGameObject;

public class Ghost extends MovableGameObject
{
    public enum Mode {
	SCATTER, CHASE, FRIGHTENED, EATEN;
    }

    // ********************* Class (static) variables ********************** //
    // ************************** Constants ************************** //
    public static final double GHOST_INSIDE_HOUSE_MOVING_SPEED_MULTIPLIER = 0.4;
    public static final double GHOST_HAS_BEEN_EATEN_MOVING_SPEED_MULTIPLIER = 1.2;

    protected static int numberOfFlashes = 0;

    // ************************ instance variables *********************** //
    protected String name = "Casper :-)";
    protected int color = 0xFF000000;
    // the direction the ghost will go when it reaches the next valid tile
    protected Direction nextDirection;
    protected double beforeExitedHouseSpeedMultiplier;
    protected double beforeFrightenedMovingSpeedMultiplier;
    protected double beforeTunnelMovingSpeedMultiplier;
    protected Mode mode = Mode.SCATTER;
    protected Mode previousMode;
    protected Point scatterModeTargetTile;
    protected Point chaseModeTargetTile;
    protected Point targetTile;
    protected boolean insideHouse = true;
    protected int houseExitPreferenceNumber = -1;
    protected int pillsEatenByPacman;
    protected int pillsEatenLimit;
    protected boolean mustExitTheHouse;
    protected boolean mustExitTheHouseFlag;
    protected boolean reverseDirection;
    protected boolean flash;
    protected boolean mustEnterTheHouse;
    protected boolean mustEnterTheHouseFlag;
    protected boolean revived;

    // cannot be overridden in subclasses
    protected final void initializeVariables(boolean completedLevel)
    {
	if (completedLevel) {
	    mode = Mode.SCATTER;
	    previousMode = null;
	    pillsEatenByPacman = 0;
	}
	currentTile = null;
	insideInnerTunnel = false;
	insideOuterTunnel = false;
	nextDirection = null;
	targetTile = null;
	insideHouse = true;
	mustExitTheHouse = false;
	mustExitTheHouseFlag = false;
	reverseDirection = false;
	flash = false;
	mustEnterTheHouse = false;
	mustEnterTheHouseFlag = false;
	revived = false;
    }

    public void initialize(boolean levelCompleted)
    {
    }

    public static int getNumberOfFlashes()
    {
	return numberOfFlashes;
    }

    public static void setNumberOfFlashes(int numberOfFlashes)
    {
	Ghost.numberOfFlashes = numberOfFlashes;
    }

    public String getName()
    {
	return name;
    }

    public int getColor()
    {
	return color;
    }

    public Direction getNextDirection()
    {
	return nextDirection;
    }

    public void setNextDirection(Direction nextDirection)
    {
	this.nextDirection = nextDirection;
    }

    public double getBeforeExitedHouseSpeedMultiplier()
    {
	return beforeExitedHouseSpeedMultiplier;
    }

    public void setBeforeExitedHouseSpeedMultiplier(double beforeExitedHouseSpeedMultiplier)
    {
	this.beforeExitedHouseSpeedMultiplier = beforeExitedHouseSpeedMultiplier;
    }

    public double getBeforeFrightenedMovingSpeedMultiplier()
    {
	return beforeFrightenedMovingSpeedMultiplier;
    }

    public void setBeforeFrightenedMovingSpeedMultiplier(double beforeFrightenedMovingSpeedMultiplier)
    {
	this.beforeFrightenedMovingSpeedMultiplier = beforeFrightenedMovingSpeedMultiplier;
    }

    public double getBeforeTunnelMovingSpeedMultiplier()
    {
	return beforeTunnelMovingSpeedMultiplier;
    }

    public void setBeforeTunnelMovingSpeedMultiplier(double beforeTunnelMovingSpeedMultiplier)
    {
	this.beforeTunnelMovingSpeedMultiplier = beforeTunnelMovingSpeedMultiplier;
    }

    public Mode getMode()
    {
	return mode;
    }

    public void setMode(Mode mode)
    {
	this.mode = mode;
    }

    public Mode getPreviousMode()
    {
	return previousMode;
    }

    public void setPreviousMode(Mode previousMode)
    {
	this.previousMode = previousMode;
    }

    public Point getScatterModeTargetTile()
    {
	return scatterModeTargetTile;
    }

    public void setScatterModeTargetTile(Point scatterModeTargetTile)
    {
	this.scatterModeTargetTile = scatterModeTargetTile;
    }

    public Point getChaseModeTargetTile()
    {
	return chaseModeTargetTile;
    }

    public void setChaseModeTargetTile(Point chaseModeTargetTile)
    {
	this.chaseModeTargetTile = chaseModeTargetTile;
    }

    public Point getTargetTile()
    {
	return targetTile;
    }

    public String getTargetTileAsString()
    {
	return (targetTile != null) ? ("[" + new Integer(targetTile.x).toString() + ", " + new Integer(targetTile.y).toString() + "]") : null;
    }

    public void setTargetTile(Point targetTile)
    {
	this.targetTile = targetTile;
    }

    public boolean isInsideHouse()
    {
	return insideHouse;
    }

    public void setInsideHouse(boolean insideHouse)
    {
	this.insideHouse = insideHouse;
    }

    public int getHouseExitPreferenceNumber()
    {
	return houseExitPreferenceNumber;
    }

    public void setHouseExitPreferenceNumber(int houseExitPreferenceNumber)
    {
	this.houseExitPreferenceNumber = houseExitPreferenceNumber;
    }

    public int getPillsEatenByPacman()
    {
	return pillsEatenByPacman;
    }

    public void setPillsEatenByPacman(int pillsEatenByPacman)
    {
	this.pillsEatenByPacman = pillsEatenByPacman;
    }

    public void increasePillsEatenByPacmanByOne()
    {
	this.pillsEatenByPacman++;
    }

    public int getPillsEatenLimit()
    {
	return pillsEatenLimit;
    }

    public void setPillsEatenLimit(int pillsEatenLimit)
    {
	this.pillsEatenLimit = pillsEatenLimit;
    }

    public boolean getMustExitTheHouse()
    {
	return mustExitTheHouse;
    }

    public void setMustExitTheHouse(boolean mustExitTheHouse)
    {
	this.mustExitTheHouse = mustExitTheHouse;
    }

    public boolean getMustExitTheHouseFlag()
    {
	return mustExitTheHouseFlag;
    }

    public void setMustExitTheHouseFlag(boolean mustExitTheHouseFlag)
    {
	this.mustExitTheHouseFlag = mustExitTheHouseFlag;
    }

    public boolean getReverseDirection()
    {
	return reverseDirection;
    }

    public void setReverseDirection(boolean reverseDirection)
    {
	this.reverseDirection = reverseDirection;
    }

    public boolean getFlash()
    {
	return flash;
    }

    public void setFlash(boolean flash)
    {
	this.flash = flash;
    }

    public void setRevived(boolean revived)
    {
	this.revived = revived;
    }

    public boolean getMustEnterTheHouse()
    {
	return mustEnterTheHouse;
    }

    public void setMustEnterTheHouse(boolean mustEnterTheHouse)
    {
	this.mustEnterTheHouse = mustEnterTheHouse;
    }

    public boolean getMustEnterTheHouseFlag()
    {
	return mustEnterTheHouseFlag;
    }

    public void setMustEnterTheHouseFlag(boolean mustEnterTheHouseFlag)
    {
	this.mustEnterTheHouseFlag = mustEnterTheHouseFlag;
    }

    public boolean isRevived()
    {
	return revived;
    }
}
