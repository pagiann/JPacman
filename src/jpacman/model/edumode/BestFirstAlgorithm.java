package jpacman.model.edumode;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jpacman.util.Utility;

public class BestFirstAlgorithm extends HeuristicAlgorithm
{
    public static final String NAME = "Best-First";

    public BestFirstAlgorithm()
    {
	super();
	basicAlgorithmFunction = "\"Best-First\" algorithm expands first the node with the\n" + //
	"best heuristic value (minimum value/distance here)\n" + //
	"from the search frontier!.\n";
    }

    public BestFirstAlgorithm(int heuristicFunction)
    {
	this();
	this.heuristicFunction = heuristicFunction;
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
	    stepwiseExecutionInitialStage(startNode, ".");
	} else if (interactiveExecution) {
	    interactiveExecutionInitialStage();
	} else {
	    nodesVisited = 1;
	}

	while (!openList.isEmpty()) {
	    stepwiseExecutionCheckIfMultipleIterations1();

	    Collections.sort(openList, Node.hCostSorter);
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
		String hCostText = decimalFormatForHeuristic.format(currentNode.getHCost());
		stepwiseExecutionMiddleStage1(startNode, "Expanding node " + currentNode.getMazeGridLocation() + " with\nbest heuristic value of\n" + hCostText + " and ID:" + currentNode.id + " from the\nsearch frontier and adding\nit to the closed set");
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
			String hCostText = decimalFormatForHeuristic.format(hCost);
			stepwiseExecutionMiddleStage4(neighborNode, "Adding neighbor/child node\n" + neighborNode.getMazeGridLocation() + " with heuristic value\nof " + hCostText + " to the\nsearch frontier");
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
}
