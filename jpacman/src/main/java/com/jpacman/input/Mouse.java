package com.jpacman.input;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class Mouse implements MouseListener, MouseMotionListener {
    public static final int LEFT_BUTTON = 1;
    public static final int MIDDLE_BUTTON = 2;
    public static final int RIGHT_BUTTON = 3;

    private static int button = -1;
    private static int x = -1;
    private static int y = -1;
    private static boolean dragging = false;

    @Override
    public void mouseClicked(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        button = e.getButton();
        x = e.getX();
        y = e.getY();
        dragging = true;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        button = -1;
        dragging = false;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
    }

    public static int getButton() {
        return button;
    }

    public static void setButton(int button) {
        Mouse.button = button;
    }

    public static int getX() {
        return x;
    }

    public static int getY() {
        return y;
    }

    public static Point getCoordinates() {
        return new Point(x, y);
    }

    public static boolean isDragging() {
        return dragging;
    }
}
