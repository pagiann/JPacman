package com.jpacman.util;

import java.awt.Point;

public enum Utility {
    ;

    public static double computeManhattanDistance(Point p1, Point p2) {
        double dx = Math.abs(p1.x - p2.x);
        double dy = Math.abs(p1.y - p2.y);
        return (dx + dy);
    }

    public static double computeChebyshevDistance(Point p1, Point p2) {
        double dx = Math.abs(p1.x - p2.x);
        double dy = Math.abs(p1.y - p2.y);
        return Math.max(dx, dy);
    }

    public static double computeOctileDistance(Point p1, Point p2) {
        double dx = Math.abs(p1.x - p2.x);
        double dy = Math.abs(p1.y - p2.y);
        return (dx + dy) + (Math.sqrt(2) - 2) * Math.min(dx, dy);
    }

    public static double computeEuclideanDistance(Point p1, Point p2) {
        double dx = p1.x - p2.x;
        double dy = p1.y - p2.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static String getPointAsString(Point p) {
        return "[" + p.x + "," + p.y + "]";
    }
}
