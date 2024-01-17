package com.jpacman.util;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import com.jpacman.model.Maze;
import com.jpacman.model.MovableGameObject.Direction;
import com.jpacman.view.graphics.ScreenPainter;

// TODO remove completely and replace it with the new AI algorithms
public final class PathFinder {
	private PathFinder() {
	}

	// ********************* Class (static) variables ********************** //
	// ************************** Constants ************************** //
	private static final int TILE = Maze.TILE;
	private static final int HALF_TILE = Maze.HALF_TILE;
	private static final int WIDTH = Maze.WIDTH;

	private static ArrayList<Point> mazeRouteTiles; // the maze's route in tiles
	private static ArrayList<Point> mazeIntersectionsTiles; // the maze's intersections in tiles
	private static ArrayList<Point> mazeCrossroadsTiles; // the maze's cross roads in tiles
	private static ArrayList<Point> mazeGhostHouseRouteTiles; // the maze's ghosts' house route in tiles
	private static ArrayList<Point> mazeGhostHouseIntersectionsTiles; // the maze's ghosts' house cross roads in tiles

	public static Direction reachTargetTile(Point nextTile, Point targetTile, Direction direction) {
		Direction nextDirection = null;
		double shortestDistance = Double.MAX_VALUE;

		boolean print = false;

		if (print) {
			ScreenPainter.debug("\n\nINSIDE reachTargetTile method ------------===================");
			ScreenPainter.debug("next tile is in cross road");
			ScreenPainter.debug("current direction is " + direction);
			System.out.print("the rest directions besides the opposite of current one are: ");
		}

		Direction directions[] = computeRestDirectionsBesidesOpposite(direction);

		if (print) {
			for (int i = 0; i < directions.length; i++) {
				System.out.print(directions[i] + " ");
			}
			System.out.println();
		}

		ArrayList<Direction> validDirections = computeValidDirections(nextTile, directions);
		int numberOfValidDirections = validDirections.size();

		if (print) {
			System.out.print("the valid directions are: ");
			for (int i = 0; i < numberOfValidDirections; i++) {
				System.out.print(validDirections.get(i) + " ");
			}
			System.out.println();
		}

		if (numberOfValidDirections > 1) {
			Point tile = null;
			for (int i = 0; i < numberOfValidDirections; i++) {
				tile = computeNextTile(nextTile, validDirections.get(i));
				double distance = Utility.computeEuclideanDistance(tile, targetTile);
				if (print)
					System.out.println("distance " + i + " = " + distance);
				if (distance < shortestDistance) {
					shortestDistance = distance;
					nextDirection = validDirections.get(i);
				}
			}
			if (print)
				System.out.println("\nthe (best) direction to choose on the croossroad is " + nextDirection);
		} else if (numberOfValidDirections == 1) {
			nextDirection = validDirections.get(0);
		}

		if (print)
			ScreenPainter.debug("========================================----------------------------\n\n");
		return nextDirection;
	}

	private static Direction[] computeRestDirectionsBesidesOpposite(Direction currentDirection) {
		Direction[] validDirections = new Direction[Direction.values().length - 1];
		Direction oppositeDirection = Direction.getOpposite(currentDirection);
		for (int i = 0, j = 0; i < Direction.values().length; i++) {
			if (Direction.values()[i] != oppositeDirection) {
				validDirections[j++] = Direction.values()[i];
			}
		}
		return validDirections;
	}

	private static ArrayList<Direction> computeValidDirections(Point nextTile, Direction[] directions) {
		ArrayList<Direction> validDirections = new ArrayList<Direction>();
		for (int i = 0; i < directions.length; i++) {
			Point nextOfNextTile = computeNextTile(nextTile, directions[i]);
			if (isNextTileInRoute(nextOfNextTile) || isNextTileInGhostHouseRoute(nextTile)) {
				validDirections.add(directions[i]);
			}
		}
		return validDirections;
	}

	public static Point computeCurrentTile(Point position) {
		int tileXCoord = (position.x / TILE) * TILE + HALF_TILE;
		int tileYCoord = (position.y / TILE) * TILE + HALF_TILE;
		if (position.x < 0) {
			tileXCoord = -tileXCoord;
		}
		return (new Point(tileXCoord, tileYCoord));
	}

	public static Point computeNextTile(Point currentTile, Direction direction) {
		Point nextTile = null;
		switch (direction) {
			case UP:
				nextTile = new Point(currentTile.x, currentTile.y - TILE);
				break;
			case DOWN:
				nextTile = new Point(currentTile.x, currentTile.y + TILE);
				break;
			case LEFT:
				if (currentTile.x == -HALF_TILE) {
					nextTile = new Point(WIDTH + HALF_TILE, currentTile.y);
				} else {
					nextTile = new Point(currentTile.x - TILE, currentTile.y);
				}
				break;
			case RIGHT:
				if (currentTile.x == WIDTH + HALF_TILE) {
					nextTile = new Point(-HALF_TILE, currentTile.y);
				} else {
					nextTile = new Point(currentTile.x + TILE, currentTile.y);
				}
				break;
		}
		return nextTile;
	}

	public static Point computeNextTileInGhostsHouse(Point position, Direction direction) {
		Point nextTile = null;
		switch (direction) {
			case UP:
				nextTile = new Point(position.x, position.y - HALF_TILE);
				break;
			case DOWN:
				nextTile = new Point(position.x, position.y + HALF_TILE);
				break;
			case LEFT:
				nextTile = new Point(position.x - HALF_TILE, position.y);
				break;
			case RIGHT:
				nextTile = new Point(position.x + HALF_TILE, position.y);
				break;
		}
		return nextTile;
	}

	// returns a new pseudo-random (but valid) direction, different from the
	// opposite of the current direction
	public static Direction getNewValidRandomDirection(Point nextTile, Direction direction) {
		Direction newValidRandomDirection = null;
		// get an array, randomly filled with the rest 3 directions besides the opposite
		// of the current one
		Direction[] newDirections = computeRestDirectionsBesidesOppositeRandomized(direction);
		for (int i = 0; i < Direction.values().length - 1; i++) {
			Point nextOfNextTile = computeNextTile(nextTile, newDirections[i]);
			if (isNextTileInRoute(nextOfNextTile)) {
				newValidRandomDirection = newDirections[i];
				break;
			}
		}
		return newValidRandomDirection;
	}

	private static Direction[] computeRestDirectionsBesidesOppositeRandomized(Direction currentDirection) {
		Direction[] validDirections = computeRestDirectionsBesidesOpposite(currentDirection);
		// randomize array
		Collections.shuffle(Arrays.asList(validDirections));
		return validDirections;
	}

	public static ArrayList<Point> getMazeRouteTiles() {
		return mazeRouteTiles;
	}

	public static boolean isNextTileInRoute(Point nextTile) {
		return (mazeRouteTiles.contains(nextTile) ? true : false);
	}

	public static boolean isNextTileInGhostHouseRoute(Point nextTile) {
		return (mazeGhostHouseRouteTiles.contains(nextTile) ? true : false);
	}

	public static boolean isIntersectionTile(Point tile) {
		return (mazeIntersectionsTiles.contains(tile) ? true : false);
	}

	public static boolean isCrossroadTile(Point tile) {
		return (mazeCrossroadsTiles.contains(tile) ? true : false);
	}

	public static boolean isGhostHouseIntersectionTile(Point tile) {
		return (mazeGhostHouseIntersectionsTiles.contains(tile) ? true : false);
	}

	public static void setMazeRouteTiles(ArrayList<Point> mazeRouteTiles) {
		PathFinder.mazeRouteTiles = mazeRouteTiles;
	}

	public static void setMazeIntersectionsTiles(ArrayList<Point> mazeIntersectionsTiles) {
		PathFinder.mazeIntersectionsTiles = mazeIntersectionsTiles;
	}

	public static void setMazeCrossroadsTiles(ArrayList<Point> mazeCrossroadsTiles) {
		PathFinder.mazeCrossroadsTiles = mazeCrossroadsTiles;
	}

	public static void setMazeGhostHouseRouteTiles(ArrayList<Point> mazeGhostHouseRouteTiles) {
		PathFinder.mazeGhostHouseRouteTiles = mazeGhostHouseRouteTiles;
	}

	public static void setMazeGhostHouseIntersectionsTiles(ArrayList<Point> mazeGhostHouseIntersectionsTiles) {
		PathFinder.mazeGhostHouseIntersectionsTiles = mazeGhostHouseIntersectionsTiles;
	}
}
