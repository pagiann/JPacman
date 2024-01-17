package com.jpacman.model;

import java.awt.Point;

public abstract class Pill extends GameObject
{
    public static final int SIZE = 4; // the size of the small pills (in pixels)

    protected int size;

    public Pill(Point position, int size)
    {
	this.position = position;
	this.size = size;
    }

    public int getSize()
    {
	return size;
    }

    public void setSize(int size)
    {
	this.size = size;
    }
}
