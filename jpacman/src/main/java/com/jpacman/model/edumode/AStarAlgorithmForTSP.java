package com.jpacman.model.edumode;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jpacman.util.Utility;

public class AStarAlgorithmForTSP extends HeuristicAlgorithm
{
    public AStarAlgorithmForTSP(int heuristicFunction)
    {
	super();
	this.heuristicFunction = heuristicFunction;
    }

    @Override
    public List<Point> findPath(ArrayList<Point> routeTiles, Point startPoint, Point goalPoint)
    {
	Node startNode = new Node(startPoint, null, 0, 0.0, computeHeuristicCost(startPoint, goalPoint));
	openList.add(startNode);
	neighborTiles = null;

	while (!openList.isEmpty()) {
	    Collections.sort(openList, Node.fCostSorter);
	    currentNode = openList.get(0);

	    // termination check
	    if (currentNode.coordinates.equals(goalPoint)) {
		pathInPoints = new ArrayList<Point>();
		totalGCost = (float) currentNode.gCost;
		while (currentNode.parent != null) {
		    pathInPoints.add(0, currentNode.coordinates);
		    currentNode = currentNode.parent;
		}

		openList.clear();
		closedList.clear();
		return pathInPoints;
	    }

	    openList.remove(currentNode);
	    closedList.add(currentNode);
	    neighborTiles = findNeighborTiles(routeTiles);

	    for (Point neighborTile : neighborTiles) {
		if (findEqualNodeInList(neighborTile, closedList) != null) {
		    continue;
		}
		double gCost = currentNode.gCost + Utility.computeManhattanDistance(currentNode.coordinates, neighborTile);
		double hCost = computeHeuristicCost(neighborTile, goalPoint);
		Node neighborNode = new Node(neighborTile, currentNode, currentNode.depth + 1, gCost, hCost);

		Node equalNode = findEqualNodeInList(neighborTile, openList);
		if (equalNode == null) {
		    openList.add(neighborNode);
		} else if (gCost < equalNode.gCost) {
		    equalNode.parent = currentNode;
		    equalNode.gCost = gCost;
		    equalNode.fCost = gCost + hCost;
		}
	    }
	}
	closedList.clear();
	return null;
    }
}
