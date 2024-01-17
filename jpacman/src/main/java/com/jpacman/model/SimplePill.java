package com.jpacman.model;

import java.awt.Point;
import java.awt.Rectangle;

import com.jpacman.view.PillRenderer;

public class SimplePill extends Pill {
    public static final int BONUS_POINTS = 10;

    public SimplePill(Point position, int size) {
        super(position, size);
        sprite = PillRenderer.simplePill;
        bounds = new Rectangle(position.x - size / 2 - 1, position.y - size / 2 - 1, size + 2, size + 2);
    }
}
