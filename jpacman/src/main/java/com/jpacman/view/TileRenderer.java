package com.jpacman.view;

import java.awt.Point;

import com.jpacman.view.graphics.Screen;
import com.jpacman.view.graphics.Sprite;
import com.jpacman.view.graphics.SpriteSheet;

public class TileRenderer implements Renderer
{
    public static final Sprite blackVoidTile = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, 0, 10, 1, 2);
    public static final Sprite nonDiagonalMovementObstacle = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, 0, 12, 1, 1);
    public static final Sprite[] diagonalMovementObstacles = new Sprite[6];

    static {
	for (int i = 0; i < diagonalMovementObstacles.length; i++) {
	    diagonalMovementObstacles[i] = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, i + 1, 12, 1, 1);
	}
    }

    private Sprite sprite;
    private Point location;
    private int xOffset, yOffset;

    public TileRenderer(Sprite sprite, Point location, int xOffset, int yOffset)
    {
	this.sprite = sprite;
	this.location = location;
	this.xOffset = xOffset;
	this.yOffset = yOffset;
    }

    @Override
    public void render(Screen screen)
    {
	screen.renderGameObject(sprite, location.x, location.y, xOffset, yOffset);
    }

    public Point getLocation()
    {
	return location;
    }

    public void setLocation(Point location)
    {
	this.location = location;
    }
}
