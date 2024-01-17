package com.jpacman.model;

import java.awt.Point;
import java.awt.Rectangle;

import com.jpacman.view.FruitRenderer;
import com.jpacman.view.graphics.Sprite;

public class Fruit extends GameObject
{
    public enum Type {
	CHERRIES(100), STRAWBERRY(300), BANANA(500), APPLE(750), WATERMELON(1000), CARROT(1300), BANANAS(1500);

	int bonusPoints;

	private Type(int bonusPoints)
	{
	    this.bonusPoints = bonusPoints;
	}

	public int getBonusPoints()
	{
	    return bonusPoints;
	}
    }

    private static final int SIX_PIXELS = 6;
    private static final int EIGHT_PIXELS = 8;
    private static final int SIXTEEN_PIXELS = 16;

    private Type type;
    private boolean appeared = false;

    public void initialize(Type fruitType)
    {
	switch (fruitType) {
	    case CHERRIES:
		setSprite(FruitRenderer.cherries);
		setType(Type.CHERRIES);
		break;
	    case STRAWBERRY:
		setSprite(FruitRenderer.strawberry);
		setType(Type.STRAWBERRY);
		break;
	    case BANANA:
		setSprite(FruitRenderer.banana);
		setType(Type.BANANA);
		break;
	    case APPLE:
		setSprite(FruitRenderer.apple);
		setType(Type.APPLE);
		break;
	    case WATERMELON:
		setSprite(FruitRenderer.watermelon);
		setType(Type.WATERMELON);
		break;
	    case CARROT:
		setSprite(FruitRenderer.carrot);
		setType(Type.CARROT);
		break;
	    case BANANAS:
		setSprite(FruitRenderer.bananas);
		setType(Type.BANANAS);
		break;
	}

	// correct fruit position in order to be symmetrical between tiles
	position = new Point(position.x - Sprite.HALF_SPRITE_SIZE, position.y - SIX_PIXELS);
	// make fruit's bounds smaller for better visual effect in collision
	bounds = new Rectangle(position.x + EIGHT_PIXELS, position.y + EIGHT_PIXELS, Sprite.SPRITE_SIZE - SIXTEEN_PIXELS, Sprite.SPRITE_SIZE - SIXTEEN_PIXELS);
    }

    public Type getType()
    {
	return type;
    }

    public void setType(Type type)
    {
	this.type = type;
    }

    public boolean getAppeared()
    {
	return appeared;
    }

    public void setAppeared(boolean appeared)
    {
	this.appeared = appeared;
    }
}
