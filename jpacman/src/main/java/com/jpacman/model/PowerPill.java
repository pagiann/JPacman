package com.jpacman.model;

import java.awt.Point;
import java.awt.Rectangle;

import com.jpacman.view.PillRenderer;

public class PowerPill extends Pill
{
    public static final int SIZE = Maze.TILE;
    public static final int BONUS_POINTS = 50;

    public PowerPill(Point position, int size)
    {
	super(position, size);
	sprite = PillRenderer.powerPill1;
	animationSpeed = 80;
	bounds = new Rectangle(position.x - size / 2 - 1, position.y - size / 2 - 1, size + 2, size + 2);
    }
}
