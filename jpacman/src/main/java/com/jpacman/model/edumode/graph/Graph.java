package com.jpacman.model.edumode.graph;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.jpacman.model.Maze;
import com.jpacman.util.Utility;

public class Graph {
	private List<Point> vertices;
	private List<Edge> edges;
	private double cost;

	// default constructor, creates an empty graph with no vertices and edges.
	public Graph() {
		vertices = new ArrayList<Point>();
		edges = new ArrayList<Edge>();
		cost = 0.0;
	}

	// second constructor, creates a new graph from another graph (parameter).
	public Graph(Graph graph) {
		vertices = new ArrayList<Point>(graph.vertices);
		edges = new ArrayList<Edge>(graph.edges);
		cost = graph.getCost();
	}

	// third constructor, creates a new graph whose vertices and edges are taken
	// from the parameters.
	public Graph(List<Point> vertices, List<Edge> edges) {
		this.vertices = new ArrayList<Point>(vertices);
		this.edges = new ArrayList<Edge>(edges);
		cost = computeGraphCost();
	}

	public static Comparator<Edge> edgeWeightSorter = new Comparator<Edge>() {
		@Override
		public int compare(Edge e0, Edge e1) {
			if (e0.getWeight() < e1.getWeight()) {
				return -1;
			} else if (e0.getWeight() == e1.getWeight()) {
				return 0;
			} else {
				return 1;
			}
		}
	};

	// adds the specified vertex to the graph, connecting it with all existing
	// vertices and optionaly sorts the new
	// edges of the graph
	public boolean addVertex(Point vertexToBeAdded, boolean sortEdges) {
		boolean vertexAdded = false;

		if (!vertices.contains(vertexToBeAdded)) {
			for (Point vertex : vertices) {
				Edge edge = new Edge(vertexToBeAdded, vertex,
						Utility.computeEuclideanDistance(vertexToBeAdded, vertex));
				edges.add(edge);
			}

			vertices.add(vertexToBeAdded);
			cost = computeGraphCost();
			vertexAdded = true;

			if (sortEdges) {
				// sort edges by weight in ascending order
				Collections.sort(edges, Graph.edgeWeightSorter);
			}
		}

		return vertexAdded;
	}

	// removes the specified vertex from the graph along with all the edges that
	// were connected to it and optionally
	// sorts the new edges of the graph
	public boolean removeVertex(Point vertexToBeRemoved, boolean sortEdges) {
		boolean vertexRemoved = false;

		if (vertices.contains(vertexToBeRemoved)) {
			vertices.remove(vertexToBeRemoved);

			Iterator<Edge> i = edges.iterator();
			while (i.hasNext()) {
				Edge edge = i.next();
				if (edge.isConnectedToVertex(vertexToBeRemoved)) {
					i.remove();
				}
			}
			cost = computeGraphCost();
			vertexRemoved = true;

			if (sortEdges) {
				// sort edges by weight in ascending order
				Collections.sort(edges, Graph.edgeWeightSorter);
			}
		}

		return vertexRemoved;
	}

	public void print() {
		System.out.println("====================================================================");
		System.out.println("==> Printing graph info...");
		System.out.println("Graph consists of " + vertices.size() + " vertices:");
		for (int i = 0; i < vertices.size(); i++) {
			Point vertex = vertices.get(i);
			if (i < vertices.size() - 1) {
				System.out.print("[" + vertex.x + "," + vertex.y + "], ");
			} else {
				System.out.println("[" + vertex.x + "," + vertex.y + "]");
			}
		}
		System.out.println("... and " + edges.size() + " edges:");
		Point lastVertex;
		for (Edge edge : edges) {
			Point vertexA = edge.getVertexA();
			Point vertexB = edge.getVertexB();
			lastVertex = edge.getVertexB();
			if (vertexA.equals(lastVertex) || vertexB.equals(lastVertex)) {
				System.out.println("vertex [" + vertexA.x + "," + vertexA.y + "] connects to vertex [" + vertexB.x + ","
						+ vertexB.y + "] with weight of " + edge.getWeight() / Maze.TILE + " tile(s)");
			}
		}
		System.out.println("... and has a cost of: " + cost / Maze.TILE + " tiles");
		System.out.println("====================================================================");
	}

	private double computeGraphCost() {
		double cost = 0.0;
		for (Edge edge : edges) {
			cost += edge.getWeight();
		}

		return cost;
	}

	public List<Point> getVertices() {
		return vertices;
	}

	public void setVertices(List<Point> vertices) {
		this.vertices = new ArrayList<Point>(vertices);
	}

	public List<Edge> getEdges() {
		return edges;
	}

	public void setEdges(List<Edge> edges) {
		this.edges = new ArrayList<Edge>(edges);
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}
}