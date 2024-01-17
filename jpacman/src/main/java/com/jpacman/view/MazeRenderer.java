package com.jpacman.view;

import java.awt.Point;

import com.jpacman.model.Maze;
import com.jpacman.view.graphics.Screen;

public class MazeRenderer implements Renderer {
    private Maze maze;

    public MazeRenderer(Maze maze) {
        this.maze = maze;
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
    }

    @Override
    public void render(Screen screen) {
        screen.renderMaze(maze, 0, 0);
    }

    public static void renderThumbnail(Screen screen, Maze maze, Point location, boolean useOffsets) {
        screen.renderMazeThumbnail(maze, location.x, location.y, useOffsets);
    }
}
