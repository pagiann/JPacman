package com.jpacman.model.edumode;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jpacman.util.Utility;

public class AStarAlgorithm extends HeuristicAlgorithm
{
    public static final String NAME = "A* (A star)";

    public AStarAlgorithm()
    {
	super();
	basicAlgorithmFunction = "\"A* (A star)\" algorithm is like Best-First algorithm but\n" + //
	"it differs in that, it expands first the node with the best\n" + //
	"(minimum value/distance here) value F = G + H, where G is the\n" + //
	"actual distance covered so far and H is the heuristic value.\n";
    }

    public AStarAlgorithm(int heuristicFunction)
    {
	this();
	this.heuristicFunction = heuristicFunction;
    }

    @Override
    public List<Point> findPath(ArrayList<Point> routeTiles, Point startPoint, Point goalPoint)
    {
	reset();

	int idNumber = 1;
	Node startNode = new Node(startPoint, null, 0, 0.0, computeHeuristicCost(startPoint, goalPoint));

	startNodeDrawable = startNode;
	// add a goal node without id yet, for drawing purposes
	goalNodeDrawable = new Node(goalPoint, null);

	openList.add(startNode);
	graphicalSearchTree.setRootNode(startNode);

	if (stepByStepExecution) {
	    stepwiseExecutionInitialStage(startNode, ".");
	} else if (interactiveExecution) {
	    interactiveExecutionInitialStage();
	} else {
	    nodesVisited = 1;
	}

	while (!openList.isEmpty()) {
	    stepwiseExecutionCheckIfMultipleIterations1();

	    Collections.sort(openList, Node.fCostSorter);
	    currentNode = openList.get(0);
	    currentNeighborNode = null;

	    // termination check
	    if (currentNode.coordinates.equals(goalPoint)) {
		constructPath(startNode, idNumber);
		return pathInPoints;
	    }

	    if (interactiveExecution) {
		interactiveExecutionMiddleStage1();
	    }
	    openList.remove(currentNode);
	    openListSize = openList.size();
	    currentNode.id = String.valueOf(idNumber++);
	    if (stepByStepExecution) {
		String fCostText = decimalFormatForHeuristic.format(currentNode.getFCost());
		stepwiseExecutionMiddleStage1(startNode, "Expanding node " + currentNode.getMazeGridLocation() + " with\nthe lowest F cost value of\n" + fCostText + " and ID:" + currentNode.id + " from the\nsearch frontier and adding\nit to the closed set");
	    } else if (interactiveExecution) {
		interactiveExecutionMiddleStage2();
	    } else {
		nodesExpanded++;
	    }
	    closedList.add(currentNode);
	    neighborTiles = findNeighborTiles(routeTiles);
	    if (stepByStepExecution) {
		neighborTiles = Collections.synchronizedList(neighborTiles);
		stepwiseExecutionMiddleStage2();
	    } else if (interactiveExecution) {
		interactiveExecutionMiddleStage3();
	    }

	    for (Point neighborTile : neighborTiles) {
		double gCost = currentNode.gCost + Utility.computeEuclideanDistance(currentNode.coordinates, neighborTile);
		double hCost = computeHeuristicCost(neighborTile, goalPoint);
		Node neighborNode = new Node(neighborTile, currentNode, currentNode.depth + 1, gCost, hCost);
		currentNeighborNode = neighborNode;

		Node equalNode = findEqualNodeInList(neighborTile, closedList);
		if (equalNode != null && gCost >= equalNode.gCost) {
		    if (stepByStepExecution) {
			stepwiseExecutionMiddleStage3(neighborNode);
		    }
		    continue;
		}

		equalNode = findEqualNodeInList(neighborTile, openList);
		if (equalNode == null) {
		    openList.add(neighborNode);
		    currentNode.children.add(neighborNode);
		    if (stepByStepExecution) {
			String hCostText = decimalFormatForHeuristic.format(hCost);
			String fCostText = decimalFormatForHeuristic.format(gCost + hCost);
			stepwiseExecutionMiddleStage4(neighborNode, "Adding neighbor/child node\n" + neighborNode.getMazeGridLocation() + " with heuristic value\nof " + hCostText + " and total cost\ndistance F: " + fCostText + " to the\nsearch frontier");
		    } else {
			nodesVisited++;
		    }
		} else if (gCost < equalNode.gCost) {
		    equalNode.parent = currentNode;
		    equalNode.gCost = gCost;
		    equalNode.fCost = gCost + hCost;
		    if (stepByStepExecution) {
			String gCostText = decimalFormatForHeuristic.format(gCost);
			stepwiseExecutionFoundEqualNodeWithBetterGCostStage(gCostText);
		    }
		}
	    }
	    if (!neighborTiles.isEmpty() && currentNode.depth + 1 > graphicalSearchTree.getHeight()) {
		graphicalSearchTree.setHeight(currentNode.depth + 1);
	    }
	}

	// open list emptied, means that a solution path not found,
	// clear the closed list and return null.
	closedList.clear();
	return null;
    }

    private void stepwiseExecutionFoundEqualNodeWithBetterGCostStage(String gCostText)
    {
	stepCompleted = false;
	stepwiseExecutionCurrentActionPerforming("The node already in the\nsearch frontier has a\nbetter gCost of " + gCostText + ".\nSetting current neighbor\nnode to the one in the\nsearch frontier", waitingTimeInMilliseconds);
	currentAction = "Click Step to continue.";
	stepCompleted = true;
	waitingPoint();
    }
}
