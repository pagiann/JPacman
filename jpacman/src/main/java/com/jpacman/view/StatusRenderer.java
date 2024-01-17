package com.jpacman.view;

import java.awt.Point;

import com.jpacman.model.Maze;
import com.jpacman.view.graphics.Screen;
import com.jpacman.view.graphics.Sprite;

public class StatusRenderer implements Renderer
{
    private final Point position;
    private final int size;
    private final Point position2;
    private int pacmanLives;
    private Sprite currentFruit;

    public StatusRenderer(Point position, int size, Sprite currentFruit)
    {
	this.position = new Point(position);
	this.size = size;
	position2 = new Point(position.x, position.y);
	this.currentFruit = currentFruit;
    }

    @Override
    public void render(Screen screen)
    {
	for (int i = 0; i < pacmanLives - 1; i++) {
	    screen.renderGameObject(PacmanRenderer.pacman_left_1, position2.x + Sprite.SPRITE_SIZE + i * Sprite.SPRITE_SIZE, position2.y + Sprite.SPRITE_SIZE, size, size);
	}
	screen.renderGameObject(currentFruit, position.x + Maze.WIDTH - Sprite.SPRITE_SIZE, position.y + Sprite.SPRITE_SIZE, size, size);
    }

    public void setPacmanLives(int pacmanLives)
    {
	this.pacmanLives = pacmanLives;
    }

    public void setCurrentFruit(Sprite currentFruit)
    {
	this.currentFruit = currentFruit;
    }
}
