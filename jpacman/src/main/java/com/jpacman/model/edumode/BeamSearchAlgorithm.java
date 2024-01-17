package com.jpacman.model.edumode;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jpacman.controller.edumode.AIAlgorithmsLearningController;
import com.jpacman.util.Utility;

public class BeamSearchAlgorithm extends HeuristicAlgorithm
{
    public static final String NAME = "Beam Search";
    public static final int MAX_BEAM_WIDTH = 20;

    private int beamWidth;

    public BeamSearchAlgorithm()
    {
	super();
	basicAlgorithmFunction = "\"Beam Search\" algorithm expands first the node with the\n" + //
	"best F=G+H value (minimum value/distance here) does, but\n" + //
	"it stores in the search frontier only a limited number\n" + //
	"of nodes (equal to its beam width).\n";
	beamWidth = 3;
    }

    public BeamSearchAlgorithm(int heuristicFunction, int beamWidth)
    {
	this();
	this.heuristicFunction = heuristicFunction;
	this.beamWidth = beamWidth;
    }

    @Override
    public List<Point> findPath(ArrayList<Point> routeTiles, Point startPoint, Point goalPoint)
    {
	reset();

	Node startNode = new Node(startPoint, null, 0, 0.0, computeHeuristicCost(startPoint, goalPoint));
	int idNumber = 1;

	startNodeDrawable = startNode;
	// add a goal node without id yet, for drawing purposes
	goalNodeDrawable = new Node(goalPoint, null);

	openList.add(startNode);
	graphicalSearchTree.setRootNode(startNode);

	if (stepByStepExecution) {
	    stepwiseExecutionInitialStage(startNode, " with\n beam width: " + beamWidth);
	} else if (interactiveExecution) {
	    interactiveExecutionInitialStage();
	} else {
	    nodesVisited = 1;
	}

	while (!openList.isEmpty()) {
	    stepwiseExecutionCheckIfMultipleIterations1();

	    Collections.sort(openList, Node.fCostSorter);
	    if (openList.size() > beamWidth) {
		openList = Collections.synchronizedList(new ArrayList<Node>(openList.subList(0, beamWidth)));
		if (stepByStepExecution) {
		    stepwiseExecutionPruningExcessNodesFromOpenListStage();
		}
	    }
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

		if (findEqualNodeInList(neighborTile, openList) == null) {
		    openList.add(neighborNode);
		    currentNode.children.add(neighborNode);
		    if (stepByStepExecution) {
			String hCostText = decimalFormatForHeuristic.format(hCost);
			String fCostText = decimalFormatForHeuristic.format(gCost + hCost);
			stepwiseExecutionMiddleStage4(neighborNode, "Adding neighbor/child node\n" + neighborNode.getMazeGridLocation() + " with heuristic value\nof " + hCostText + " and total cost\ndistance F: " + fCostText + " to the\nsearch frontier");
		    } else {
			nodesVisited++;
		    }
		} else {
		    if (stepByStepExecution) {
			stepwiseExecutionMiddleStage5();
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

    private void stepwiseExecutionPruningExcessNodesFromOpenListStage()
    {
	stepCompleted = false;
	currentNodeDrawable = null;
	neighborNodesDrawable.clear();
	currentNeighborNodeDrawable = null;
	AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
	stepwiseExecutionCurrentActionPerforming("The size of the search\nfrontier, exceeds the\nbeam width (" + beamWidth + ").\nKeeping the best " + beamWidth + " nodes\nand pruning surplus nodes", waitingTimeInMilliseconds);
	unexpandedNodesDrawable = openList;
	AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
	currentAction = "Click Step to continue.";
	stepCompleted = true;
	waitingPoint();
    }

    public int getBeamWidth()
    {
	return beamWidth;
    }

    public void setBeamWidth(int beamWidth)
    {
	this.beamWidth = beamWidth;
    }
}
