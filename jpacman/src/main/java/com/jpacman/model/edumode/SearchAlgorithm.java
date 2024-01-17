package com.jpacman.model.edumode;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jpacman.Game;
import com.jpacman.Game.SubMode;
import com.jpacman.controller.edumode.AIAlgorithmsInteractiveLearningController;
import com.jpacman.controller.edumode.AIAlgorithmsLearningController;
import com.jpacman.controller.edumode.AIAlgorithmsStepwiseLearningController;
import com.jpacman.controller.edumode.GraphicalSearchTreeController;
import com.jpacman.model.Maze;

public abstract class SearchAlgorithm {
	public static final int NONE = -1;
	public static final int DFS = 0;
	public static final int BFS = 1;
	public static final int IDS = 2;
	public static final int BF = 3;
	public static final int BS = 4;
	public static final int A_STAR = 5;

	public static final int WAIT_MILLISECONDS_1 = 4000;
	public static final int WAIT_MILLISECONDS_2 = 500;

	public static boolean useDiagonalTiles;

	protected String basicAlgorithmFunction;
	protected List<Point> pathInPoints;
	protected List<Node> pathInNodes;
	protected List<Node> openList;
	protected List<Node> closedList;
	protected Node currentNode;
	protected Node currentNeighborNode;
	protected List<Point> neighborTiles;
	protected int nodesVisited;
	protected int nodesExpanded;
	protected int openListSize;
	protected float totalGCost;
	protected float secondsElapsed;
	protected GraphicalSearchTree graphicalSearchTree;
	protected String currentAction;
	protected volatile boolean started;
	protected volatile boolean running;
	protected volatile boolean canceled;
	protected volatile boolean finished;

	protected SearchAlgorithm() {
		basicAlgorithmFunction = "";
		pathInPoints = new ArrayList<Point>();
		pathInNodes = new ArrayList<Node>();
		openList = Collections.synchronizedList(new ArrayList<Node>());
		closedList = Collections.synchronizedList(new ArrayList<Node>());
		neighborTiles = Collections.synchronizedList(new ArrayList<Point>());
		init();
	}

	private void init() {
		pathInPoints.clear();
		pathInNodes.clear();
		openList.clear();
		closedList.clear();
		currentNode = null;
		currentNeighborNode = null;
		neighborTiles.clear();
		nodesVisited = 0;
		nodesExpanded = 0;
		openListSize = 0;
		totalGCost = 0f;
		secondsElapsed = 0f;
		graphicalSearchTree = new GraphicalSearchTree();
		currentAction = "";
	}

	public void reset() {
		resetDrawableVariables();
		init();
	}

	public void resetStateFlags() {
		started = false;
		running = false;
		canceled = false;
		finished = false;
	}

	public abstract List<Point> findPath(ArrayList<Point> routeTiles, Point startPoint, Point goalPoint);

	// construct the path found by backtracking from the goal node to the root node
	protected void constructPath(Node startNode, int idNumber) {
		nodesExpanded++;
		stepwiseExecutionCheckIfMultipleIterations2();

		currentNode.id = String.valueOf(idNumber++);
		openList.remove(currentNode);
		openListSize = openList.size();
		closedList.add(currentNode);

		if (stepByStepExecution) {
			stepwiseExecutionFinalStage1();
		}

		totalGCost = (float) currentNode.gCost;
		while (currentNode.parent != null) {
			pathInNodes.add(0, currentNode);
			pathInPoints.add(0, currentNode.coordinates);
			solutionPathPointsDrawable.add(0, currentNode.coordinates);
			currentNode = currentNode.parent;

			if (stepByStepExecution) {
				stepwiseExecutionFinalStage2();
			}
		}

		pathInNodes.add(0, startNode);
		solutionPathPointsDrawable.add(0, startNode.coordinates);

		if (!stepByStepExecution) {
			expandedNodesDrawable = Collections.synchronizedList(new ArrayList<Node>(closedList));
			if (Game.getActiveSubMode() == SubMode.MAZE_PATHFINDING
					|| Game.getActiveSubMode() == SubMode.AI_ALGORITHMS_STEPWISE_LEARNING) {
				graphicalSearchTree.computePaintableTreeCoordinates();
			}
			if (AIAlgorithmsStepwiseLearningController.storeAllGeneratedTreesOfIDS) {
				GraphicalSearchTreeController.paintableNodesOfAllTreesOfIDS.add(graphicalSearchTree);
			}
			GraphicalSearchTreeController.graphicalSearchTree = graphicalSearchTree;
		}

		openList.clear();
		closedList.clear();
	}

	protected List<Point> findNeighborTiles(ArrayList<Point> routeTiles) {
		List<Point> neighborTiles = new ArrayList<Point>();

		final int[] fourMovementIndices = { 1, 7, 3, 5 }; // for movement in 4 directions
		final int[] eightMovementIndices1 = { 0, 8, 2, 6, 1, 7, 3, 5 }; // for movement in 8 directions
		final int[] eightMovementIndices2 = { 1, 7, 3, 5, 0, 8, 2, 6 }; // for movement in 8 directions
		int[] indices = fourMovementIndices;

		if (useDiagonalTiles) {
			if (this instanceof DepthFirstSearchAlgorithm || this instanceof IterativeDeepeningAlgorithm) {
				indices = eightMovementIndices1;
			} else {
				indices = eightMovementIndices2;
			}
		}

		int x = currentNode.coordinates.x;
		int y = currentNode.coordinates.y;

		for (int i = 0; i < indices.length; i++) {
			int xi = ((indices[i] % 3) - 1) * Maze.TILE;
			int yi = ((indices[i] / 3) - 1) * Maze.TILE;
			Point neighborTile = new Point(x + xi, y + yi);
			if (routeTiles.contains(neighborTile)) {
				neighborTiles.add(neighborTile);
			}
		}

		return neighborTiles;
	}

	// used in A* algorithm
	protected Node findEqualNodeInList(Point nodeLocation, List<Node> list) {
		for (Node node : list) {
			if (node.coordinates.equals(nodeLocation)) {
				return node;
			}
		}

		return null;
	}

	// used in all the other algorithms
	protected boolean isThereAnEqualNodeInList(Node nodeToCheck, List<Node> list) {
		for (Node node : list) {
			if (node.equals(nodeToCheck)) {
				return true;
			}
		}

		return false;
	}

	public String getBasicAlgorithmFunction() {
		return basicAlgorithmFunction;
	}

	public List<Point> getPathInPoints() {
		return pathInPoints;
	}

	public List<Node> getPathInNodes() {
		return pathInNodes;
	}

	public float getTotalGCost() {
		return totalGCost;
	}

	public int getNodesVisited() {
		return nodesVisited;
	}

	public int getNodesExpanded() {
		return nodesExpanded;
	}

	public int getOpenListSize() {
		return openListSize;
	}

	public float getSecondsElapsed() {
		return secondsElapsed;
	}

	public String getCurrentAction() {
		return currentAction;
	}

	public boolean isStarted() {
		return started;
	}

	public void setStarted(boolean started) {
		this.started = started;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public boolean isCanceled() {
		return canceled;
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	/////////////////////////////////////////////////////////////////
	////////// stepwise execution variables and methods /////////////
	/////////////////////////////////////////////////////////////////

	// public static variables used for drawing in maze and graphical search tree
	public static volatile Node startNodeDrawable;
	public static volatile Node goalNodeDrawable;
	public static volatile Node currentNodeDrawable;
	public static volatile Node currentNeighborNodeDrawable;
	public static List<Node> expandedNodesDrawable;
	public static List<Node> unexpandedNodesDrawable;
	public static List<Point> neighborNodesDrawable;
	public static List<Node> solutionPathNodesDrawable;
	public static List<Point> solutionPathPointsDrawable;

	protected static int waitingTimeInMilliseconds = WAIT_MILLISECONDS_1;

	static {
		expandedNodesDrawable = Collections.synchronizedList(new ArrayList<Node>());
		unexpandedNodesDrawable = Collections.synchronizedList(new ArrayList<Node>());
		neighborNodesDrawable = Collections.synchronizedList(new ArrayList<Point>());
		solutionPathNodesDrawable = Collections.synchronizedList(new ArrayList<Node>());
		solutionPathPointsDrawable = Collections.synchronizedList(new ArrayList<Point>());
	}

	public static synchronized void resetDrawableVariables() {
		startNodeDrawable = null;
		goalNodeDrawable = null;
		currentNodeDrawable = null;
		currentNeighborNodeDrawable = null;
		expandedNodesDrawable.clear();
		unexpandedNodesDrawable.clear();
		neighborNodesDrawable.clear();
		solutionPathNodesDrawable.clear();
		solutionPathPointsDrawable.clear();
	}

	public volatile boolean manualExecution;
	public volatile boolean stepByStepExecution;
	public volatile boolean mutltipleIterations;
	public volatile boolean nextStep;
	public volatile boolean stepCompleted;

	protected int iteration = 0;
	protected int skippedIterationsCounter = 0;
	protected String actionDotsTail = "";

	protected void stepwiseExecutionCheckIfMultipleIterations1() {
		if (stepByStepExecution || mutltipleIterations) {
			stepwiseExecutionCurrentActionPerforming("Iteration #" + (++iteration) + " of algorithm",
					waitingTimeInMilliseconds / 4);
		}

		if (mutltipleIterations) {
			neighborTiles.clear();
			currentNodeDrawable = null;
			currentNeighborNodeDrawable = null;
			unexpandedNodesDrawable = openList;
			openListSize = openList.size();
			if (++skippedIterationsCounter == AIAlgorithmsStepwiseLearningController.iterationSize) {
				stepCompleted = true;
				stepByStepExecution = true;
				nextStep = false;
				mutltipleIterations = false;
				skippedIterationsCounter = 0;
			}
		}
	}

	protected void stepwiseExecutionCheckIfMultipleIterations2() {
		if (mutltipleIterations) {
			stepByStepExecution = true;
			mutltipleIterations = false;
		}
	}

	protected void stepwiseExecutionCurrentActionPerforming(String action, int waitMilliseconds) {
		for (int i = 0; i < waitMilliseconds / WAIT_MILLISECONDS_2; i++) {
			actionDotsTail = actionDotsTail.equals("...") ? "." : actionDotsTail + ".";
			currentAction = action + actionDotsTail;
			try {
				Thread.sleep(WAIT_MILLISECONDS_2);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

	protected void stepwiseExecutionInitialStage(Node startNode, String additionalMessage) {
		// "disable" the Step UI button
		stepCompleted = false;
		unexpandedNodesDrawable = openList;
		expandedNodesDrawable = closedList;
		AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).revalidate();
		AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
		currentAction = "Algorithm started" + additionalMessage;
		try {
			Thread.sleep(waitingTimeInMilliseconds);
		} catch (InterruptedException e) {
			return;
		}
		if (!(this instanceof IterativeDeepeningAlgorithm)) {
			stepwiseExecutionCurrentActionPerforming(
					"Adding start node " + startNode.getMazeGridLocation() + "\nto the search frontier",
					waitingTimeInMilliseconds);
			openListSize = openList.size();
			nodesVisited = 1;
		}
		currentAction = "Click Step to continue.";
		// "enable" the Step UI button
		stepCompleted = true;
		waitingPoint();
	}

	protected void stepwiseExecutionMiddleStage1(Node startNode, String nodeExpansionActionString) {
		stepCompleted = false;
		stepwiseExecutionCurrentActionPerforming(nodeExpansionActionString, waitingTimeInMilliseconds);
		nodesExpanded++;
		currentNodeDrawable = currentNode;
		neighborNodesDrawable.clear();
		currentNeighborNodeDrawable = null;
		AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
		openListSize = openList.size();
		currentAction = "Click Step to continue.";
		stepCompleted = true;
		waitingPoint();
	}

	protected void stepwiseExecutionMiddleStage2() {
		stepCompleted = false;
		stepwiseExecutionCurrentActionPerforming("Finding neighbor nodes of\nthe current node",
				waitingTimeInMilliseconds);
		neighborNodesDrawable = neighborTiles;
		AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
		currentAction = "Click Step to continue.";
		stepCompleted = true;
		waitingPoint();
	}

	protected void stepwiseExecutionMiddleStage3(Node neighborNode) {
		stepCompleted = false;
		currentNeighborNodeDrawable = neighborNode;
		AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
		stepwiseExecutionCurrentActionPerforming("An equal node of the current\nneighbor node "
				+ neighborNode.getMazeGridLocation() + "\nis in the closed set,\ncontinuing",
				waitingTimeInMilliseconds);
		currentAction = "Click Step to continue.";
		stepCompleted = true;
		waitingPoint();
	}

	protected void stepwiseExecutionMiddleStage4(Node neighborNode, String addToSearchFrontierActionString) {
		stepCompleted = false;
		currentNeighborNodeDrawable = neighborNode;
		AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
		stepwiseExecutionCurrentActionPerforming(addToSearchFrontierActionString, waitingTimeInMilliseconds);
		nodesVisited++;
		openListSize = openList.size();
		currentAction = "Click Step to continue.";
		stepCompleted = true;
		waitingPoint();
	}

	protected void stepwiseExecutionMiddleStage5() {
		stepCompleted = false;
		AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
		stepwiseExecutionCurrentActionPerforming("An equal node is in the\nsearch frontier, continuing",
				waitingTimeInMilliseconds);
		currentAction = "Click Step to continue.";
		stepCompleted = true;
		waitingPoint();
	}

	protected void stepwiseExecutionFinalStage1() {
		stepCompleted = false;
		neighborTiles.clear();
		currentNodeDrawable = currentNode;
		currentNeighborNodeDrawable = null;
		AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
		currentAction = "Goal reached!";
		waitingTimeInMilliseconds = manualExecution ? WAIT_MILLISECONDS_1 / 2 : WAIT_MILLISECONDS_2;
		try {
			Thread.sleep(waitingTimeInMilliseconds);
		} catch (InterruptedException e) {
			return;
		}
		stepCompleted = true;
	}

	protected void stepwiseExecutionFinalStage2() {
		stepCompleted = false;
		stepwiseExecutionCurrentActionPerforming("Backtracking", waitingTimeInMilliseconds = WAIT_MILLISECONDS_2);
		solutionPathNodesDrawable.add(currentNode);
		AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
		stepCompleted = true;
	}

	protected void waitingPoint() {
		GraphicalSearchTreeController.scrolledToCurrentNode = false;
		if (manualExecution) {
			if (waitingTimeInMilliseconds != WAIT_MILLISECONDS_1) {
				waitingTimeInMilliseconds = WAIT_MILLISECONDS_1;
			}
			while (stepByStepExecution) {
				if (!manualExecution) {
					break;
				}
				if (nextStep) {
					nextStep = false;
					break;
				}
			}
		} else if (waitingTimeInMilliseconds != WAIT_MILLISECONDS_2) {
			waitingTimeInMilliseconds = WAIT_MILLISECONDS_2;
		}
	}

	////////////////////////////////////////////////////////////////
	////////// interactive execution variable and methods //////////
	////////////////////////////////////////////////////////////////

	private static final int MIN_NUMBER_OF_EXPANDED_NODES = 1;
	private static final int MAX_NUMBER_OF_EXPANDED_NODES = 10;

	public volatile boolean interactiveExecution;
	public volatile boolean userInteracted = true;
	public volatile int nextNodeExpansionWaitingTime; // in milliseconds

	protected int numberOfExpandedNodes = 1;

	protected void interactiveExecutionInitialStage() {
		currentAction = "Algorithm started.";
		try {
			Thread.sleep(WAIT_MILLISECONDS_1 / 2);
		} catch (InterruptedException e) {
			return;
		}
	}

	protected void interactiveExecutionMiddleStage1() {
		if (nodesExpanded - numberOfExpandedNodes == 0 && openList.size() > 1) {
			userInteracted = false;
			unexpandedNodesDrawable = Collections.synchronizedList(new ArrayList<Node>(openList));
			openListSize = openList.size();
			AIAlgorithmsInteractiveLearningController.nextNodeToBeExpanded = currentNode;
			pause();
			unexpandedNodesDrawable.clear();
			numberOfExpandedNodes += MIN_NUMBER_OF_EXPANDED_NODES
					+ AIAlgorithmsInteractiveLearningController.randomNumberOfNodesToBeExpanded
							.nextInt(MAX_NUMBER_OF_EXPANDED_NODES);
		}
	}

	protected void interactiveExecutionMiddleStage2() {
		actionDotsTail = actionDotsTail.equals("...") ? "." : actionDotsTail + ".";
		currentAction = "Expanding next node\nfrom the search frontier" + actionDotsTail;
		try {
			Thread.sleep(nextNodeExpansionWaitingTime);
		} catch (InterruptedException e) {
			return;
		}
		nodesExpanded++;
	}

	protected void interactiveExecutionMiddleStage3() {
		expandedNodesDrawable = closedList;
	}

	protected void pause() {
		while (interactiveExecution) {
			stepwiseExecutionCurrentActionPerforming("Waiting for user\ninteraction", WAIT_MILLISECONDS_2);
			if (userInteracted) {
				break;
			}
		}
	}
}
