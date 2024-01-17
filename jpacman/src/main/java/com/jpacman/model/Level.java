package com.jpacman.model;

import com.jpacman.Game;

public class Level {
	// ************************* Instance variables ************************ //
	private int value; // current level
	private Maze maze; // current level's maze

	private Fruit.Type fruitType; // current level's fruit type (bonus symbol)

	private int[] scatterModePeriodsDurations; // array that holds the ghosts scatter mode durations in seconds
	private int[] chaseModePeriodsDurations; // array that holds ghosts chase mode durations in seconds
	private int frightTimeDuration; // duration (in sec) of ghosts frightened mode
	private int numberOfGhostsFlashes; // number of ghosts flashes before exiting frightened mode
	private int elroy1PillsRemaining; // number of pills remaining so that blinky to become "cruise elroy 1"
	private int elroy2PillsRemaining; // number of pills remaining so that blinky to become "cruise elroy 2"

	// moving speeds according to game status
	private double pacmanNormalMovingSpeedMultiplier;
	private double pacmanPillEatingMovingSpeedMultiplier;
	private double pacmanPowerPillEatingMovingSpeedMultiplier;
	private double pacmanGhostsFrightenedMovingSpeedMultiplier;
	private double pacmanGhostsFrightenedAndPillEatingMovingSpeedMultiplier;
	private double ghostNormalMovingSpeedMultiplier;
	private double ghostTunnelMovingSpeedMultiplier;
	private double ghostFrightenedMovingSpeedMultiplier;
	private double elroy1MovingSpeedMultiplier;
	private double elroy2MovingSpeedMultiplier;

	private int pillsEatenForPinkyToGetOutOfHouse;
	private int pillsEatenForInkyToGetOutOfHouse;
	private int pillsEatenForClydeToGetOutOfHouse;

	public Level(int level) {
		value = level;
		setLevelSpecifications();
	}

	private void setGhostsModesDurations() {
		switch (value) {
			case 1:
				scatterModePeriodsDurations = new int[] { 7, 7, 5, 5 };
				chaseModePeriodsDurations = new int[] { 20, 20, 20 };
				break;
			case 2:
			case 3:
			case 4:
				scatterModePeriodsDurations = new int[] { 7, 7, 5, 0 };
				chaseModePeriodsDurations = new int[] { 20, 20, 1033 };
				break;
			case 5:
			default:
				scatterModePeriodsDurations = new int[] { 5, 5, 5, 0 };
				chaseModePeriodsDurations = new int[] { 20, 20, 1037 };
				break;
		}
	}

	private void setPillsEateForGhostsToGetOutOfHouse() {
		switch (value) {
			case 1:
				pillsEatenForPinkyToGetOutOfHouse = 0;
				pillsEatenForInkyToGetOutOfHouse = 30;
				pillsEatenForClydeToGetOutOfHouse = 60;
				break;
			case 2:
				pillsEatenForPinkyToGetOutOfHouse = 0;
				pillsEatenForInkyToGetOutOfHouse = 0;
				pillsEatenForClydeToGetOutOfHouse = 50;
				break;
			case 3:
			default:
				pillsEatenForPinkyToGetOutOfHouse = 0;
				pillsEatenForInkyToGetOutOfHouse = 0;
				pillsEatenForClydeToGetOutOfHouse = 0;
				break;
		}
	}

	private void setLevelSpecifications() {
		setGhostsModesDurations();
		setPillsEateForGhostsToGetOutOfHouse();

		switch (value) {
			case 0:
				if (Game.getActiveSubMode() == Game.SubMode.CUSTOM_SPACE_PATHFINDING
						|| Game.getActiveSubMode() == Game.SubMode.CLASSIC_TSP) {
					maze = Maze.mazeCanvas;
				} else {
					maze = Maze.maze1;
				}
				if (Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_LEARNING_MENU) {
					setMovingSpeedMultipliers(1.0, 1.0, 1.0, 1.0, 1.0, 0.6, 0.5, 0.5, 0.5, 0.5);
				}
				break;
			case 1:
				maze = Maze.maze1;
				setFruitType(Fruit.Type.CHERRIES);
				setMovingSpeedMultipliers(0.8, 0.71, 0.53, 0.9, 0.79, 0.75, 0.4, 0.5, 0.8, 0.85);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(20, 10);
				frightTimeDuration = 6;
				numberOfGhostsFlashes = 5;
				break;
			case 2:
				maze = Maze.maze2;
				setFruitType(Fruit.Type.STRAWBERRY);
				setMovingSpeedMultipliers(0.85, 0.75, 0.55, 0.9, 0.79, 0.8, 0.4, 0.5, 0.85, 0.90);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(30, 15);
				frightTimeDuration = 5;
				numberOfGhostsFlashes = 5;
				break;
			case 3:
				maze = Maze.maze3;
				setFruitType(Fruit.Type.BANANA);
				setMovingSpeedMultipliers(0.9, 0.79, 0.57, 0.95, 0.83, 0.85, 0.45, 0.55, 0.9, 0.95);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(40, 20);
				frightTimeDuration = 4;
				numberOfGhostsFlashes = 5;
				break;
			case 4:
				maze = Maze.maze4;
				setFruitType(Fruit.Type.BANANA);
				setMovingSpeedMultipliers(0.95, 0.83, 0.59, 0.95, 0.85, 0.9, 0.45, 0.55, 0.95, 1.0);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(40, 20);
				frightTimeDuration = 3;
				numberOfGhostsFlashes = 5;
				break;
			case 5:
				maze = Maze.maze5;
				setFruitType(Fruit.Type.APPLE);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(40, 20);
				frightTimeDuration = 2;
				numberOfGhostsFlashes = 5;
				break;
			case 6:
				maze = Maze.maze1;
				setFruitType(Fruit.Type.APPLE);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(50, 25);
				frightTimeDuration = 5;
				numberOfGhostsFlashes = 5;
				break;
			case 7:
				maze = Maze.maze2;
				setFruitType(Fruit.Type.WATERMELON);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(50, 25);
				frightTimeDuration = 2;
				numberOfGhostsFlashes = 5;
				break;
			case 8:
				maze = Maze.maze3;
				setFruitType(Fruit.Type.WATERMELON);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(50, 25);
				frightTimeDuration = 2;
				numberOfGhostsFlashes = 5;
				break;
			case 9:
				maze = Maze.maze4;
				setFruitType(Fruit.Type.CARROT);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(60, 30);
				frightTimeDuration = 1;
				numberOfGhostsFlashes = 3;
				break;
			case 10:
				maze = Maze.maze5;
				setFruitType(Fruit.Type.CARROT);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(60, 30);
				frightTimeDuration = 5;
				numberOfGhostsFlashes = 5;
				break;
			case 11:
				maze = Maze.maze1;
				setFruitType(Fruit.Type.BANANAS);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(60, 30);
				frightTimeDuration = 2;
				numberOfGhostsFlashes = 5;
				break;
			case 12:
				maze = Maze.maze2;
				setFruitType(Fruit.Type.BANANAS);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(80, 40);
				frightTimeDuration = 1;
				numberOfGhostsFlashes = 3;
				break;
			case 13:
				maze = Maze.maze3;
				setFruitType(Fruit.Type.BANANAS);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(80, 40);
				frightTimeDuration = 1;
				numberOfGhostsFlashes = 3;
				break;
			case 14:
				maze = Maze.maze4;
				setFruitType(Fruit.Type.BANANAS);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(80, 40);
				frightTimeDuration = 3;
				numberOfGhostsFlashes = 5;
				break;
			case 15:
				maze = Maze.maze5;
				setFruitType(Fruit.Type.BANANAS);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(100, 50);
				frightTimeDuration = 1;
				numberOfGhostsFlashes = 3;
				break;
			case 16:
				maze = Maze.maze5;
				setFruitType(Fruit.Type.BANANAS);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(100, 50);
				frightTimeDuration = 1;
				numberOfGhostsFlashes = 3;
				break;
			case 17:
				maze = Maze.maze5;
				setFruitType(Fruit.Type.BANANAS);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(100, 50);
				frightTimeDuration = 0;
				numberOfGhostsFlashes = 0;
				break;
			case 18:
				maze = Maze.maze5;
				setFruitType(Fruit.Type.BANANAS);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(100, 50);
				frightTimeDuration = 1;
				numberOfGhostsFlashes = 3;
				break;
			case 19:
				maze = Maze.maze5;
				setFruitType(Fruit.Type.BANANAS);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(120, 60);
				frightTimeDuration = 0;
				numberOfGhostsFlashes = 0;
				break;
			case 20:
				maze = Maze.maze5;
				setFruitType(Fruit.Type.BANANAS);
				setMovingSpeedMultipliers(1.0, 0.87, 0.61, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(120, 60);
				frightTimeDuration = 0;
				numberOfGhostsFlashes = 0;
				break;
			case 21:
			default:
				maze = Maze.maze5;
				setFruitType(Fruit.Type.BANANAS);
				setMovingSpeedMultipliers(0.9, 0.79, 0.57, 1.0, 0.87, 0.95, 0.50, 0.6, 1.0, 1.05);
				setPillsRemainingforBlinkyToBecomeCruiseElroy(120, 60);
				frightTimeDuration = 0;
				numberOfGhostsFlashes = 0;
		}
	}

	// sets the speeds of movable objects
	private void setMovingSpeedMultipliers(double mult1, double mult2, double mult3, double mult4, double mult5,
			double mult6, double mult7, double mult8, double mult9, double mult10) {
		pacmanNormalMovingSpeedMultiplier = mult1;
		pacmanPillEatingMovingSpeedMultiplier = mult2;
		pacmanPowerPillEatingMovingSpeedMultiplier = mult3;
		pacmanGhostsFrightenedMovingSpeedMultiplier = mult4;
		pacmanGhostsFrightenedAndPillEatingMovingSpeedMultiplier = mult5;

		ghostNormalMovingSpeedMultiplier = mult6;
		ghostTunnelMovingSpeedMultiplier = mult7;
		ghostFrightenedMovingSpeedMultiplier = mult8;
		elroy1MovingSpeedMultiplier = mult9;
		elroy2MovingSpeedMultiplier = mult10;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void increaseValueByOne() {
		value++;
	}

	public Maze getMaze() {
		return maze;
	}

	public void setMaze(Maze maze) {
		this.maze = maze;
	}

	public Fruit.Type getFruitType() {
		return fruitType;
	}

	public void setFruitType(Fruit.Type fruitType) {
		this.fruitType = fruitType;
	}

	public int[] getScatterModePeriodsDurations() {
		return scatterModePeriodsDurations;
	}

	public int[] getChaseModePeriodsDurations() {
		return chaseModePeriodsDurations;
	}

	public int getFrightTimeDuration() {
		return frightTimeDuration;
	}

	public int getNumberOfGhostsFlashes() {
		return numberOfGhostsFlashes;
	}

	public int getElroy1PillsRemaining() {
		return elroy1PillsRemaining;
	}

	public int getElroy2PillsRemaining() {
		return elroy2PillsRemaining;
	}

	private void setPillsRemainingforBlinkyToBecomeCruiseElroy(int pillsRemaining1, int pillsRemaining2) {
		elroy1PillsRemaining = pillsRemaining1;
		elroy2PillsRemaining = pillsRemaining2;
	}

	public double getPacmanNormalMovingSpeedMultiplier() {
		return pacmanNormalMovingSpeedMultiplier;
	}

	public double getPacmanPillEatingMovingSpeedMultiplier() {
		return pacmanPillEatingMovingSpeedMultiplier;
	}

	public double getPacmanPowerPillEatingMovingSpeedMultiplier() {
		return pacmanPowerPillEatingMovingSpeedMultiplier;
	}

	public double getPacmanGhostsFrightenedMovingSpeedMultiplier() {
		return pacmanGhostsFrightenedMovingSpeedMultiplier;
	}

	public double getPacmanGhostsFrightenedAndPillEatingMovingSpeedMultiplier() {
		return pacmanGhostsFrightenedAndPillEatingMovingSpeedMultiplier;
	}

	public double getGhostNormalMovingSpeedMultiplier() {
		return ghostNormalMovingSpeedMultiplier;
	}

	public double getGhostTunnelMovingSpeedMultiplier() {
		return ghostTunnelMovingSpeedMultiplier;
	}

	public double getGhostFrightenedMovingSpeedMultiplier() {
		return ghostFrightenedMovingSpeedMultiplier;
	}

	public double getElroy1MovingSpeedMultiplier() {
		return elroy1MovingSpeedMultiplier;
	}

	public double getElroy2MovingSpeedMultiplier() {
		return elroy2MovingSpeedMultiplier;
	}

	public int getPillsEatenForPinkyToGetOutOfHouse() {
		return pillsEatenForPinkyToGetOutOfHouse;
	}

	public int getPillsEatenForInkyToGetOutOfHouse() {
		return pillsEatenForInkyToGetOutOfHouse;
	}

	public int getPillsEatenForClydeToGetOutOfHouse() {
		return pillsEatenForClydeToGetOutOfHouse;
	}
}
