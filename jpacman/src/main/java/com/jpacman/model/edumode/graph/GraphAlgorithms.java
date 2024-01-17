package com.jpacman.model.edumode.graph;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jpacman.model.edumode.AStarAlgorithmForTSP;
import com.jpacman.util.Utility;

/*
 * Utility class, with static only methods, that provides various graph algorithms
 */
public final class GraphAlgorithms
{
    // Suppresses default constructor, ensuring non-instantiability.
    private GraphAlgorithms()
    {
    }

    // uses Prim's algorithm
    public static Graph computeMinimumSpanningTree(Graph inputGraph)
    {
	List<Point> newVertices = new ArrayList<Point>();
	List<Edge> newEdges = new ArrayList<Edge>();

	newVertices.add(inputGraph.getVertices().get(0));

	// sort edges by their weight in ascending order
	Collections.sort(inputGraph.getEdges(), Graph.edgeWeightSorter);

	while (newVertices.size() < inputGraph.getVertices().size()) {
	    for (Edge edge : inputGraph.getEdges()) {
		if (newVertices.contains(edge.getVertexA()) && !newVertices.contains(edge.getVertexB())) {
		    newEdges.add(edge);
		    newVertices.add(edge.getVertexB());
		    break;
		} else if (newVertices.contains(edge.getVertexB()) && !newVertices.contains(edge.getVertexA())) {
		    newEdges.add(edge);
		    newVertices.add(edge.getVertexA());
		    break;
		}
	    }
	}

	return new Graph(newVertices, newEdges);
    }

    public static Graph constructCompleteGraph(List<Point> vertices, boolean sortEdges)
    {
	Graph completeGraph = new Graph();
	completeGraph.setVertices(vertices);
	List<Edge> edges = completeGraph.getEdges();

	for (Point vertexA : vertices) {
	    for (Point vertexB : vertices) {
		if (!vertexA.equals(vertexB)) {
		    Edge newEdge = new Edge(vertexA, vertexB, Utility.computeEuclideanDistance(vertexA, vertexB));
		    if (!edges.contains(newEdge)) {
			edges.add(newEdge);
		    }
		}
	    }
	}

	if (sortEdges) {
	    // sort edges by weight in ascending order
	    Collections.sort(edges, Graph.edgeWeightSorter);
	}

	return completeGraph;
    }

    public static Graph constructCompleteGraphMaze(List<Point> vertices, List<Point> pathTiles, boolean sortEdges)
    {
	Graph completeGraph = new Graph();
	completeGraph.setVertices(vertices);
	List<Edge> edges = completeGraph.getEdges();

	ArrayList<Point> validPathTiles = new ArrayList<Point>(pathTiles);

	AStarAlgorithmForTSP aStarAlgorithmForTSP = new AStarAlgorithmForTSP(AStarAlgorithmForTSP.EUCLIDEAN_DISTANCE);
	for (int i = 0; i < vertices.size(); i++) {
	    Point end1 = vertices.get(i);
	    for (int j = 0; j < vertices.size(); j++) {
		Point end2 = vertices.get(j);
		if (!end1.equals(end2)) {
		    validPathTiles.add(end1);
		    validPathTiles.add(end2);
		    List<Point> shortestPath = aStarAlgorithmForTSP.findPath(validPathTiles, end1, end2);

		    if (shortestPath != null) {
			Edge newEdge = new Edge(end1, end2, aStarAlgorithmForTSP.getTotalGCost());
			shortestPath.remove(shortestPath.size() - 1);
			newEdge.setInnerPoints(shortestPath);
			if (!edges.contains(newEdge)) {
			    edges.add(newEdge);
			}
		    }
		    validPathTiles.remove(end1);
		    validPathTiles.remove(end2);
		}
	    }
	}
	if (sortEdges) {
	    // sort edges by weight in ascending order
	    Collections.sort(edges, Graph.edgeWeightSorter);
	}

	return completeGraph;
    }
}
