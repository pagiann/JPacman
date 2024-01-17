package com.jpacman.model.edumode.graph;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.jpacman.model.Maze;

public class Edge {
    private Point vertexA;
    private Point vertexB;
    private List<Point> innerPoints;
    private double weight;

    public Edge(Point vertexA, Point vertexB, double weight) {
        this.vertexA = vertexA;
        this.vertexB = vertexB;
        this.innerPoints = new ArrayList<Point>();
        this.weight = weight;
    }

    public boolean isConnectedToVertex(Point vertex) {
        return ((this.getVertexA().equals(vertex) || this.getVertexB().equals(vertex)) ? true : false);
    }

    public boolean isConnectedToBothVertices(Point vertexA, Point vertexB) {
    //@formatter:off
	boolean connectedToBoth = ((this.getVertexA().equals(vertexA) && this.getVertexB().equals(vertexB))
				|| (this.getVertexA().equals(vertexB) && this.getVertexB().equals(vertexA)));
	//@formatter:on

        return connectedToBoth;
    }

    // returns the opposite vertex (end point) of this edge
    public Point getOppositeVertex(Point vertex) {
        Point oppositeVertex = null;
        if (vertex.equals(this.getVertexA())) {
            oppositeVertex = this.getVertexB();
        } else if (vertex.equals(this.getVertexB())) {
            oppositeVertex = this.getVertexA();
        } else {
            System.err.println("Edge: " + this + " does not connect to vertex: " + vertex);
        }

        return oppositeVertex;
    }

    public void print(boolean printInnerPoints) {
        System.out.println("one vertex at [" + vertexA.x + "," + vertexA.y + "] ");
        if (printInnerPoints && innerPoints.size() > 0) {
            System.out.println("passing through these inner points: ");
            int i = 1;
            for (Point innerPoint : innerPoints) {
                System.out.println((i++) + " - " + innerPoint);
            }
        }
        System.out.println("and the other vertex at [" + vertexB.x + "," + vertexB.y + "] with a weight of "
                + weight / Maze.TILE + " tile(s)");
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((vertexA == null) ? 0 : vertexA.hashCode());
        result = prime * result + ((vertexB == null) ? 0 : vertexB.hashCode());
        long temp = Double.doubleToLongBits(weight);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (object instanceof Edge) {
            Edge edge = (Edge) object;
        //@formatter:off
	    if (((this.vertexA.equals(edge.vertexA) && this.vertexB.equals(edge.vertexB)) //
	    || (this.vertexA.equals(edge.vertexB) && this.vertexB.equals(edge.vertexA))) //
            && Double.doubleToLongBits(this.weight) == Double.doubleToLongBits(edge.weight)) {
		return true;
	    }
	    //@formatter:on
        }
        return false;
    }

    @Override
    public String toString() {
        return "Edge [vertexA=" + vertexA + ", vertexB=" + vertexB + ", innerPoints=" + innerPoints + ", weight="
                + weight + "]";
    }

    public Point getVertexA() {
        return vertexA;
    }

    public void setVertexA(Point vertexA) {
        this.vertexA = vertexA;
    }

    public Point getVertexB() {
        return vertexB;
    }

    public void setVertexB(Point vertexB) {
        this.vertexB = vertexB;
    }

    public List<Point> getInnerPoints() {
        return innerPoints;
    }

    public void setInnerPoints(List<Point> innerPoints) {
        this.innerPoints = innerPoints;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
