package com.jpacman.model;

import java.awt.Point;

import com.jpacman.GameApplication;

public abstract class MovableGameObject extends GameObject {
    // ******************************* Types ******************************* //
    public static enum Direction {
        UP, DOWN, LEFT, RIGHT;

        public static Direction getOpposite(Direction currentDirection) {
            Direction oppositeDirection = null;
            if (currentDirection == UP) {
                oppositeDirection = DOWN;
            } else if (currentDirection == DOWN) {
                oppositeDirection = UP;
            } else if (currentDirection == LEFT) {
                oppositeDirection = RIGHT;
            } else if (currentDirection == RIGHT) {
                oppositeDirection = LEFT;
            }
            return oppositeDirection;
        }
    }

    // ********************* Class (static) variables ********************** //
    // ************************** Constants ************************** //
    // movable objects(pacman, ghosts, fruit?) default speed (100%) in ups (updates
    // per second)
    public static final double MAX_MOVING_SPEED = GameApplication.UPDATES_PER_SECOND;

    public static final double NORMAL_MOVING_SPEED = MAX_MOVING_SPEED - (MAX_MOVING_SPEED / 3.0);
    public static final double FAST_MOVING_SPEED = MAX_MOVING_SPEED;
    public static final double SLOW_MOVING_SPEED = MAX_MOVING_SPEED / 2.0;

    // ************************* instance variables ************************ //
    // this variable determines the ups for each movable object (may be different
    // for each object)
    protected double delta = 0;
    // object's starting position in the maze (may differ from center point of tile)
    protected Point startingPosition;
    // objects's current tile in the maze
    protected Point currentTile;
    // object's next tile to move to in the maze
    protected Point nextTile;
    // object's current moving direction
    protected Direction direction = Direction.UP;
    // object's moving status
    protected boolean isMoving = false;
    // the percentage (%) with which MAX_MOVING_SPEED is multiplied to set
    // movingSpeed
    protected double movingSpeedMultiplier = 0;
    // the current speed of all (movable) objects (this variable is changing for
    // each object according to the current
    // status of all objects)
    protected double movingSpeed = 0;
    // distanced moved in pixels
    protected int distanceMoved = 0;
    // the inner tunnels of the maze that are shown on screen
    protected boolean insideInnerTunnel = false;
    // the "invisible" outer tunnels that connect the left side of the maze with the
    // right one
    protected boolean insideOuterTunnel = false;

    // move to the requested direction by one pixel, currently only orthogonal
    // movement is supported
    public void move(Direction requestedDirection) {
        final int ONE_PIXEL = 1;
        switch (requestedDirection) {
            case UP:
                position.y -= ONE_PIXEL;
                break;
            case DOWN:
                position.y += ONE_PIXEL;
                break;
            case LEFT:
                if (position.x == -Maze.HALF_TILE) {
                    insideOuterTunnel = true;
                    position.x = Maze.WIDTH + Maze.HALF_TILE;
                } else {
                    position.x -= ONE_PIXEL;
                }
                break;
            case RIGHT:
                if (position.x == Maze.WIDTH + Maze.HALF_TILE) {
                    insideOuterTunnel = true;
                    position.x = -Maze.HALF_TILE;
                } else {
                    position.x += ONE_PIXEL;
                }
                break;
            default:
        }

        distanceMoved++;
    }

    public void computeNewDirection() {
        Point currentPosition = this.getPosition();
        Point nextTile = this.getNextTile();

        if (nextTile.x == currentPosition.x && nextTile.y < currentPosition.y) {
            this.setDirection(Direction.UP);
        } else if (nextTile.x == currentPosition.x && nextTile.y > currentPosition.y) {
            this.setDirection(Direction.DOWN);
        } else if (nextTile.y == currentPosition.y && nextTile.x < currentPosition.x) {
            this.setDirection(Direction.LEFT);
        } else if (nextTile.y == currentPosition.y && nextTile.x > currentPosition.x) {
            this.setDirection(Direction.RIGHT);
        }
    }

    public double getDelta() {
        return delta;
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    public void addDelta(double delta) {
        this.delta += delta;
    }

    public void decreaseDeltaByOne() {
        delta--;
    }

    public Point getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(Point startingPosition) {
        this.startingPosition = new Point(startingPosition);
    }

    public Point getCurrentTile() {
        return currentTile;
    }

    public String getCurrentTileAsString() {
        return (currentTile != null)
                ? ("[" + new Integer(currentTile.x).toString() + "," + new Integer(currentTile.y).toString() + "]")
                : null;
    }

    public void setCurrentTile(Point currentTile) {
        this.currentTile = new Point(currentTile);
    }

    public Point getNextTile() {
        return nextTile;
    }

    public void setNextTile(Point nextTile) {
        this.nextTile = new Point(nextTile);
    }

    public String getNextTileAsString() {
        return (nextTile != null)
                ? ("[" + new Integer(nextTile.x).toString() + "," + new Integer(nextTile.y).toString() + "]")
                : null;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void setMoving(boolean isMoving) {
        this.isMoving = isMoving;
    }

    public double getMovingSpeedMultiplier() {
        return movingSpeedMultiplier;
    }

    public void setMovingSpeedMultiplier(double movingSpeedMultiplier) {
        this.movingSpeedMultiplier = movingSpeedMultiplier;
    }

    public double getMovingSpeed() {
        return movingSpeed;
    }

    public void setMovingSpeedViaMultiplier(double movingSpeedMultiplier) {
        this.movingSpeedMultiplier = movingSpeedMultiplier;
        movingSpeed = movingSpeedMultiplier * MAX_MOVING_SPEED;
    }

    public void setMovingSpeed(double movingSpeed) {
        this.movingSpeed = movingSpeed;
    }

    public void setMaxMovingSpeed() {
        movingSpeed = MAX_MOVING_SPEED;
    }

    public int getDistanceMoved() {
        return distanceMoved;
    }

    public void setDistanceMoved(int distanceMoved) {
        this.distanceMoved = distanceMoved;
    }

    public boolean isInsideInnerTunnel() {
        return insideInnerTunnel;
    }

    public void setInsideInnerTunnel(boolean insideInnerTunnel) {
        this.insideInnerTunnel = insideInnerTunnel;
    }

    public boolean isInsideOuterTunnel() {
        return insideOuterTunnel;
    }

    public void setInsideOuterTunnel(boolean insideOuterTunnel) {
        this.insideOuterTunnel = insideOuterTunnel;
    }
}
