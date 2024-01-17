package jpacman.model.edumode;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jpacman.util.Utility;

public class BreadthFirstSearchAlgorithm extends SearchAlgorithm
{
    public static final String NAME = "Breadth First Seacrh";

    public BreadthFirstSearchAlgorithm()
    {
	super();
	basicAlgorithmFunction = "\"Breadth First Search\" algorithm expands first\n the oldest unexpanded node from the search frontier!\nFrontier is a Queue FIFO (First In First Out).\n";
    }

    @Override
    public List<Point> findPath(ArrayList<Point> routeTiles, Point startPoint, Point goalPoint)
    {
	reset();

	Node startNode = new Node(startPoint, null, 0, 0.0);
	int idNumber = 1;

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
	    openListSize = openList.size();
	    openList.remove(currentNode);
	    currentNode.id = String.valueOf(idNumber++);
	    if (stepByStepExecution) {
		stepwiseExecutionMiddleStage1(startNode, "Expanding oldest unexpanded\nnode " + currentNode.getMazeGridLocation() + " with ID:" + currentNode.id + "\nfrom the search frontier\nand adding it to\nthe closed set");
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
		Node neighborNode = new Node(neighborTile, currentNode, currentNode.depth + 1, gCost);
		currentNeighborNode = neighborNode;

		if (findEqualNodeInList(neighborTile, closedList) != null) {
		    if (stepByStepExecution) {
			stepwiseExecutionMiddleStage3(neighborNode);
		    }
		    continue;
		}

		if (findEqualNodeInList(neighborTile, openList) == null) {
		    openList.add(neighborNode);
		    currentNode.children.add(neighborNode);
		    if (stepByStepExecution) {
			stepwiseExecutionMiddleStage4(neighborNode, "Adding neighbor/child node\n" + neighborNode.getMazeGridLocation() + " to the end of\nthe search frontier");
		    } else {
			nodesVisited++;
		    }
		} else {
		    if (stepByStepExecution) {
			stepwiseExecutionMiddleStage5();
		    }
		}
	    }
	    if (!stepByStepExecution && !neighborTiles.isEmpty() && currentNode.depth + 1 > graphicalSearchTree.getHeight()) {
		graphicalSearchTree.setHeight(currentNode.depth + 1);
	    }
	}

	// open list emptied, means that a solution path not found,
	// clear the closed list and return null.
	closedList.clear();
	return null;
    }
}
