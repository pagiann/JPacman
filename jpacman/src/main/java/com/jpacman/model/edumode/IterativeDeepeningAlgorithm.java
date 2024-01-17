package com.jpacman.model.edumode;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.jpacman.Game;
import com.jpacman.Game.SubMode;
import com.jpacman.controller.edumode.AIAlgorithmsLearningController;
import com.jpacman.controller.edumode.AIAlgorithmsStepwiseLearningController;
import com.jpacman.controller.edumode.GraphicalSearchTreeController;
import com.jpacman.util.Utility;

public class IterativeDeepeningAlgorithm extends SearchAlgorithm {
	public static final String NAME = "Iterative Deepening";
	public static final int MAX_INITIAL_DEPTH = 30;
	public static final int MAX_DEPTH_STEP = 10;

	private int initialDepth;
	private int depthStep;
	private int currentDepth;
	private int depthLimit;
	private boolean visualizeIterations;

	public IterativeDeepeningAlgorithm() {
		super();
		basicAlgorithmFunction = "\"Iterative Deepening\" is a mix of DFS and BFS algorithms.\n" + //
				"It starts at initial depth and continues by running a\n" + //
				"depth-limited search repeatedly, increasing the depth limit\n" + //
				"with each iteration until it reaches the depth of the goal state.\n" + //
				"Frontier is a Stack LIFO (Last In First Out).\n";
		init();
	}

	public IterativeDeepeningAlgorithm(int initialDepth, int depthStep) {
		this();
		this.initialDepth = initialDepth;
		this.depthStep = depthStep;
	}

	private void init() {
		initialDepth = 1;
		depthStep = 1;
		iteration = 1;
		currentDepth = 0;
		visualizeIterations = false;
	}

	@Override
	public List<Point> findPath(ArrayList<Point> routeTiles, Point startPoint, Point goalPoint) {
		reset();
		// init();

		Node startNode = new Node(startPoint, null, 0, 0.0);
		int idNumber;
		depthLimit = currentDepth = initialDepth;
		iteration = 1;

		startNodeDrawable = startNode;
		// add a goal node without id yet, for drawing purposes
		goalNodeDrawable = new Node(goalPoint, null);

		if (stepByStepExecution) {
			stepwiseExecutionInitialStage(startNode,
					" with\nparameters:\n initial depth: " + initialDepth + "\n depth step: " + depthStep);
		} else if (interactiveExecution) {
			interactiveExecutionInitialStage();
		}

		while (depthLimit < Integer.MAX_VALUE && !canceled) {
			stepwiseExecutionCheckIfMultipleIterations();

			openList.clear();
			openList.add(startNode);
			closedList.clear();
			openListSize = 0;
			idNumber = 1;
			nodesVisited = 0;
			nodesExpanded = 0;
			numberOfExpandedNodes = 1;

			if (stepByStepExecution || mutltipleIterations) {
				stepwiseExecutionInitialStage2(iteration, startNode);
				iteration++;
			} else {
				graphicalSearchTree = new GraphicalSearchTree(startNode);
				nodesVisited = 1;
				iteration++;
			}

			while (!openList.isEmpty() && !canceled) {
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
				if (currentNode.depth < depthLimit) {
					if (stepByStepExecution) {
						stepwiseExecutionCheckingNodeDepthLimitStage(depthLimit);
					}
				} else if (currentNode.depth == depthLimit) {
					if (stepByStepExecution) {
						stepwiseExecutionReachedEndOfDepthLimitStage(depthLimit);
					}
					continue;
				}
				openListSize = openList.size();
				currentNode.id = String.valueOf(idNumber++);
				if (stepByStepExecution) {
					stepwiseExecutionMiddleStage1(startNode,
							"Expanding newest unexpanded\nnode " + currentNode.getMazeGridLocation() + " with ID:"
									+ currentNode.id + "\nfrom the search frontier\nand adding it to\nthe closed set");
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

				if (visualizeIterations && (Game.getActiveSubMode() == SubMode.CUSTOM_SPACE_PATHFINDING
						|| Game.getActiveSubMode() == SubMode.MAZE_PATHFINDING)) {
					expandedNodesDrawable = Collections.synchronizedList(new ArrayList<Node>(closedList));
					try {
						Thread.sleep(Game.getActiveSubMode() == SubMode.CUSTOM_SPACE_PATHFINDING ? 10 : 30);
					} catch (InterruptedException e) {
						return null;
					}
				}

				for (Point neighborTile : neighborTiles) {
					double gCost = currentNode.gCost
							+ Utility.computeEuclideanDistance(currentNode.coordinates, neighborTile);
					Node neighborNode = new Node(neighborTile, currentNode, currentNode.depth + 1, gCost);
					currentNeighborNode = neighborNode;

					Node equalNode = findEqualNodeInList(neighborTile, closedList);
					if (equalNode != null) {
						if (gCost < equalNode.gCost) { // FIXME
							equalNode.parent = currentNode;
							equalNode.depth = neighborNode.depth;
							equalNode.gCost = gCost;
							if (stepByStepExecution) {
								stepwiseExecutionNodeInClosedSetHasWorseGCost(String.valueOf(gCost));
							}
						} else {
							if (stepByStepExecution) {
								stepwiseExecutionMiddleStage3(neighborNode);
							}
							continue;
						}
					}

					if (findEqualNodeInList(neighborTile, openList) == null) {
						openList.add(0, neighborNode);
						currentNode.children.add(neighborNode);
						if (stepByStepExecution) {
							stepwiseExecutionMiddleStage4(neighborNode, "Adding neighbor/child node\n"
									+ neighborNode.getMazeGridLocation() + " to the start of\nthe search frontier");
						} else {
							nodesVisited++;
						}
					} else {
						if (stepByStepExecution) {
							stepwiseExecutionMiddleStage5();
						}
					}
				}
				if (!stepByStepExecution && !neighborTiles.isEmpty()
						&& currentNode.depth + 1 > graphicalSearchTree.getHeight()) {
					graphicalSearchTree.setHeight(currentNode.depth + 1);
				}
			}

			if (stepByStepExecution) {
				stepwiseExecutionReachedEndOfIterationStage();
			}

			if (AIAlgorithmsStepwiseLearningController.storeAllGeneratedTreesOfIDS) {
				graphicalSearchTree.computePaintableTreeCoordinates();
				GraphicalSearchTreeController.paintableNodesOfAllTreesOfIDS.add(graphicalSearchTree);
			}
			// else if (Game.getActiveSubMode() == SubMode.PATHFINDING_VISUALIZATION) {
			// graphicalSearchTree.computePaintableTreeCoordinates();
			// GraphicalSearchTreeController.graphicalSearchTree = graphicalSearchTree;
			// }

			startNode.children.clear();

			depthLimit += depthStep;
			currentDepth = depthLimit;
		}

		// a solution path not found, return null.
		return null;

	}

	private void stepwiseExecutionCheckIfMultipleIterations() {
		if (mutltipleIterations) {
			if (++skippedIterationsCounter == AIAlgorithmsStepwiseLearningController.iterationSize) {
				stepByStepExecution = true;
				nextStep = false;
				mutltipleIterations = false;
				skippedIterationsCounter = 0;
			}
		}
	}

	private void stepwiseExecutionInitialStage2(int iteration, Node startNode) {
		stepCompleted = false;
		GraphicalSearchTreeController.graphicalSearchTree = GraphicalSearchTreeController.paintableNodesOfAllTreesOfIDS
				.remove(0);
		AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).revalidate();
		AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
		waitingTimeInMilliseconds = manualExecution ? WAIT_MILLISECONDS_1 / 2 : WAIT_MILLISECONDS_2;
		stepwiseExecutionCurrentActionPerforming("=> Iteration #" + iteration + ".\nSearching in depth " + currentDepth,
				waitingTimeInMilliseconds);
		if (!mutltipleIterations) {
			stepwiseExecutionCurrentActionPerforming(
					"Adding start node " + startNode.getMazeGridLocation() + "\nto the search frontier",
					waitingTimeInMilliseconds);
		}
		nodesVisited = 1;
		openListSize = openList.size();
		currentAction = "Click Step to continue.";
		stepCompleted = true;
		waitingPoint();
	}

	private void stepwiseExecutionCheckingNodeDepthLimitStage(int depthLimit) {
		stepCompleted = false;
		currentNodeDrawable = currentNode;
		neighborNodesDrawable.clear();
		currentNeighborNodeDrawable = null;
		AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
		stepwiseExecutionCurrentActionPerforming("Current node's depth " + currentNode.getDepth()
				+ ", is\nsmaller than the current\ndepth limit of " + depthLimit, waitingTimeInMilliseconds);
		stepCompleted = true;
	}

	private void stepwiseExecutionReachedEndOfDepthLimitStage(int depthLimit) {
		stepCompleted = false;
		waitingTimeInMilliseconds = manualExecution ? WAIT_MILLISECONDS_1 / 2 : WAIT_MILLISECONDS_2;
		stepwiseExecutionCurrentActionPerforming(
				"Current node's depth is\nequal to the depth limit\nof " + depthLimit + ", continuing",
				waitingTimeInMilliseconds);
		stepCompleted = true;
	}

	private void stepwiseExecutionNodeInClosedSetHasWorseGCost(String gCostText) {
		stepCompleted = false;
		stepwiseExecutionCurrentActionPerforming(
				"The node already in the\nclosed set has a\nworse gCost of " + gCostText
						+ ".\nSetting its gCost to the\ncurrent one and updating\nits parent",
				waitingTimeInMilliseconds);
		currentAction = "Click Step to continue.";
		stepCompleted = true;
		waitingPoint();
	}

	private void stepwiseExecutionReachedEndOfIterationStage() {
		stepCompleted = false;
		stepwiseExecutionCurrentActionPerforming("Reached the end of the\niteration, continuing\nto the next",
				waitingTimeInMilliseconds);
		currentNodeDrawable = null;
		neighborNodesDrawable.clear();
		currentNeighborNodeDrawable = null;
		AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).revalidate();
		AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
		stepCompleted = true;
	}

	public int getInitialDepth() {
		return initialDepth;
	}

	public void setInitialDepth(int initialDepth) {
		this.initialDepth = initialDepth;
	}

	public int getDepthStep() {
		return depthStep;
	}

	public void setDepthStep(int depthStep) {
		this.depthStep = depthStep;
	}

	public int getCurrentDepth() {
		return currentDepth;
	}

	public void setCurrentDepth(int currentDepth) {
		this.currentDepth = currentDepth;
	}

	public int getIteration() {
		return iteration;
	}

	public int getDepthLimit() {
		return depthLimit;
	}

	public boolean isVisualizeIterations() {
		return visualizeIterations;
	}

	public void setVisualizeIterations(boolean visualizeIterations) {
		this.visualizeIterations = visualizeIterations;
	}
}
