package com.jpacman.model;

import java.awt.Point;
import java.awt.Rectangle;

import com.jpacman.view.graphics.Sprite;

public abstract class GameObject
{
    // ********************* Class (static) variables ********************** //
    // ************************** Constants ************************** //
    public static final int NORMAL_ANIMATION_SPEED = 20;
    public static final int FAST_ANIMATION_SPEED = 15;
    public static final int SLOW_ANIMATION_SPEED = 25;

    // ************************* Instance variables ************************ //
    protected Point position; // object's position = x,y coordinates as Point
    protected Sprite sprite; // the objects visual representation
    protected Rectangle bounds; // the rectangular bounds of the object

    protected boolean animate = true; // flag to animate or not an object
    protected int animationSpeed = NORMAL_ANIMATION_SPEED; // the speed which any animated object animates
    protected int animationCounter = 0; // a counter that is used to determine the animation speed (above)

    public Point getPosition()
    {
	return position;
    }

    public String getPositionAsString()
    {
	return ("[" + new Integer(position.x).toString() + "," + new Integer(position.y).toString() + "]");
    }

    public void setPosition(Point position)
    {
	this.position = new Point(position);
    }

    public Sprite getSprite()
    {
	return sprite;
    }

    public void setSprite(Sprite sprite)
    {
	this.sprite = sprite;
    }

    public Rectangle getBounds()
    {
	return bounds;
    }

    public void setBounds(Rectangle bounds)
    {
	this.bounds = bounds;
    }

    public boolean getAnimate()
    {
	return animate;
    }

    public void setAnimate(boolean animate)
    {
	this.animate = animate;
    }

    public int getAnimationSpeed()
    {
	return animationSpeed;
    }

    public void setAnimationSpeed(int animationSpeed)
    {
	this.animationSpeed = animationSpeed;
    }

    public int getAnimationCounter()
    {
	return animationCounter;
    }

    public void setAnimationCounter(int animationCounter)
    {
	this.animationCounter = animationCounter;
    }

    public void increaseAnimationCounterByOne()
    {
	animationCounter++;
    }
}
