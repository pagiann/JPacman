package com.jpacman.controller;

import java.util.ArrayList;

import com.jpacman.Game;
import com.jpacman.controller.ghost.GhostAIController;
import com.jpacman.controller.ghost.GhostAIController.PreferredGhost;
import com.jpacman.controller.ghost.GhostStatusController;
import com.jpacman.controller.pacman.PacmanStatusController;
import com.jpacman.model.Fruit;
import com.jpacman.model.Level;
import com.jpacman.model.Maze;
import com.jpacman.model.Pacman;
import com.jpacman.model.Pill;
import com.jpacman.model.PowerPill;
import com.jpacman.model.SimplePill;
import com.jpacman.model.ghost.Ghost;
import com.jpacman.util.PathFinder;

public class LevelController
{
    private Level level;

    public LevelController(Level level)
    {
	this.level = level;
    }

    public void initializeLevel(Pacman pacman)
    {
	// set initial static data
	initializeStaticData();

	// set pacman's initial level data
	ititializePacmanData(pacman);
    }

    public void initializeLevel(Pacman pacman, Maze maze)
    {
	// set initial static data
	initializeStaticData(maze);

	// set pacman's initial level data
	ititializePacmanData(pacman);
    }

    public void initializeLevel(PillController pillController, Pacman pacman)
    {
	// set initial static data
	initializeStaticData();

	// create and set pills
	initializePills(pillController);

	// set pacman's initial level data
	ititializePacmanData(pacman);
    }

    public void initializeLevel(PillController pillController, Pacman pacman, Maze maze)
    {
	// set initial static data
	initializeStaticData(maze);

	// create and set pills
	initializePills(pillController);

	// set pacman's initial level data
	ititializePacmanData(pacman);
    }

    public void initializeLevel(Pacman pacman, Ghost[] ghosts)
    {
	// set initial static data
	initializeStaticData();

	// set ghosts' initial level data
	for (int i = 0; i < Game.NUMBER_OF_GHOSTS; i++) {
	    ghosts[i].setStartingPosition(level.getMaze().getGhostsStartingTiles().get(i));
	}
	for (Ghost ghost : ghosts) {
	    ghost.initialize(true);
	    ghost.setMovingSpeedViaMultiplier(level.getGhostNormalMovingSpeedMultiplier());
	}

	// set pacman's initial level data
	ititializePacmanData(pacman);
    }

    public void initializeLevel(PillController pillController, Pacman pacman, Ghost[] ghosts, Fruit currentFruit)
    {
	// set initial static data
	initializeStaticData();

	// create and set pills
	initializePills(pillController);

	// set ghosts' initial level data
	for (int i = 0; i < Game.NUMBER_OF_GHOSTS; i++) {
	    ghosts[i].setStartingPosition(level.getMaze().getGhostsStartingTiles().get(i));
	}
	for (Ghost ghost : ghosts) {
	    ghost.initialize(true);
	    ghost.setMovingSpeedViaMultiplier(level.getGhostNormalMovingSpeedMultiplier());
	}
	ghosts[1].setPillsEatenLimit(level.getPillsEatenForInkyToGetOutOfHouse());
	ghosts[2].setPillsEatenLimit(level.getPillsEatenForPinkyToGetOutOfHouse());
	ghosts[3].setPillsEatenLimit(level.getPillsEatenForClydeToGetOutOfHouse());

	// set fruit's initial level data
	currentFruit.setPosition(level.getMaze().getFruitStartingTile());
	currentFruit.initialize(level.getFruitType());

	// set pacman's initial level data
	ititializePacmanData(pacman);
    }

    private void initializeStaticData()
    {
	PathFinder.setMazeRouteTiles(level.getMaze().getRouteTiles());
	PathFinder.setMazeIntersectionsTiles(level.getMaze().getIntersectionsTiles());
	PathFinder.setMazeCrossroadsTiles(level.getMaze().getCrossroadsTiles());
	PathFinder.setMazeGhostHouseRouteTiles(level.getMaze().getGhostsHouseRouteTiles());
	PathFinder.setMazeGhostHouseIntersectionsTiles(level.getMaze().getGhostsHouseIntersectionsTiles());

	Ghost.setNumberOfFlashes(level.getNumberOfGhostsFlashes());
	GhostStatusController.setModePeriodsDurations(level.getScatterModePeriodsDurations(), level.getChaseModePeriodsDurations(), level.getFrightTimeDuration(), level.getNumberOfGhostsFlashes());
	GhostAIController.setCurrentMaze(level.getMaze());
	GhostAIController.setSecElapsedSincePacmanAtePillExceedsLimit(false);
	GhostAIController.setGlobalCounterActive(false);
	GhostAIController.setGlobalCounterActiveFlag(false);
	GhostAIController.setPreferredGhostToExitHouse(PreferredGhost.PINKY);
	PacmanStatusController.timeElapsedSinceAtePillTimer.setTimeElapsedInMilliseconds(0);
	PacmanStatusController.setPacmanInPowerModeFlag(false);
	TextController.timer.setTimeElapsedInMilliseconds(0);
	TextController.timer.setStarted(false);
    }

    private void initializeStaticData(Maze maze)
    {
	// System.out.println("inside initializeStaticData...");
	PathFinder.setMazeRouteTiles(maze.getRouteTiles());
	PathFinder.setMazeIntersectionsTiles(maze.getIntersectionsTiles());
	PathFinder.setMazeCrossroadsTiles(maze.getCrossroadsTiles());
	PathFinder.setMazeGhostHouseRouteTiles(maze.getGhostsHouseRouteTiles());
	PathFinder.setMazeGhostHouseIntersectionsTiles(maze.getGhostsHouseIntersectionsTiles());
    }

    public void initializePills(PillController pillController)
    {
	// create pills and power pills
	int totalNumberOfSimplePills = level.getMaze().getSimplePillsTiles().size();
	int totalNumberOfPowerPills = level.getMaze().getPowerPillsTiles().size();
	ArrayList<Pill> pills = new ArrayList<Pill>(totalNumberOfSimplePills + totalNumberOfPowerPills);
	for (int i = 0; i < totalNumberOfSimplePills; i++) {
	    pills.add(new SimplePill(level.getMaze().getSimplePillsTiles().get(i), Pill.SIZE));
	}
	for (int i = 0; i < totalNumberOfPowerPills; i++) {
	    pills.add(new PowerPill(level.getMaze().getPowerPillsTiles().get(i), PowerPill.SIZE));
	}
	// set pills in pillController
	pillController.createPills(pills);
    }

    private void ititializePacmanData(Pacman pacman)
    {
	pacman.setStartingPosition(level.getMaze().getPacmanStartingTile());
	pacman.initialize();
	pacman.setMovingSpeedViaMultiplier(level.getPacmanNormalMovingSpeedMultiplier());
    }

    public Level getLevel()
    {
	return level;
    }

    public void setLevel(Level level)
    {
	this.level = level;
    }

    public void increaseLevelByOne()
    {
	level.increaseValueByOne();
    }

    public void nextLevel()
    {
	level = new Level(level.getValue() + 1);
    }
}
