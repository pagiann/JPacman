package jpacman.model.edumode;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import jpacman.model.Maze;
import jpacman.model.edumode.graph.Edge;
import jpacman.model.edumode.graph.Graph;
import jpacman.model.edumode.graph.GraphAlgorithms;
import jpacman.util.Utility;

public class TSPSolver
{
    public static final int NUM_OF_VARIATIONS = 2;
    public static final int NONE = -1;
    public static final int CLASSIC = 0;
    public static final int MAZE = 1;

    public static volatile Graph graphDrawable;

    private int selectedVariation;
    private List<Point> shortestPath;
    private List<TSPNode> openList;
    private List<Point> neighborTiles;
    private Point currentNeighborTile;
    private float finalGCost;
    private int nodesVisited;
    private int nodesExpanded;
    private float secondsElapsed;
    private volatile boolean started;
    private volatile boolean running;
    private volatile boolean canceled;
    private volatile boolean finished;

    public TSPSolver()
    {
	selectedVariation = -1;
	initialize();
    }

    public void initialize()
    {
	shortestPath = null;
	openList = new ArrayList<TSPNode>();
	finalGCost = 0f;
	nodesVisited = 0;
	nodesExpanded = 0;
	secondsElapsed = 0f;
	started = false;
	running = false;
	canceled = false;
	finished = false;
	graphDrawable = null;
	SearchAlgorithm.solutionPathPointsDrawable.clear();
    }

    public void computeShortestPath(ArrayList<Point> unvisitedPoints, ArrayList<Point> routeTiles, Point startPoint)
    {
	long tStart = System.currentTimeMillis();

	int numberOfUnvisitedPoints = unvisitedPoints.size();
	// System.out.println("TSP Solver: computing shortest possible route...");
	// System.out.println("Number of points to visit = " + numberOfUnvisitedPoints);
	// System.out.println("Start position (pacman) = " + startPoint);

	ArrayList<Point> unvisitedPointsPlusStartPoint = new ArrayList<Point>(unvisitedPoints);
	unvisitedPointsPlusStartPoint.add(startPoint);

	Graph initialGraph = null;
	if ((selectedVariation == CLASSIC)) {
	    initialGraph = GraphAlgorithms.constructCompleteGraph(unvisitedPointsPlusStartPoint, true);
	} else if ((selectedVariation == MAZE)) {
	    initialGraph = GraphAlgorithms.constructCompleteGraphMaze(unvisitedPointsPlusStartPoint, routeTiles, true);
	} else {
	    System.err.println("not supported TSP variation!");
	    return;
	}

	List<Point> path = new ArrayList<Point>();
	path.add(startPoint);
	List<Edge> pathEdges = new ArrayList<Edge>();
	// root node, initial heuristic cost does not matter
	TSPNode currentNode = new TSPNode(path, pathEdges, 0.0, 0.0);

	openList.add(currentNode);
	nodesVisited = 1;
	nodesExpanded = 0;

	while (!openList.isEmpty() && !canceled) {
	    Collections.sort(openList, TSPNode.fCostSorter);
	    currentNode = openList.get(0);

	    finalGCost = (float) currentNode.gCost;

	    long tDelta = System.currentTimeMillis() - tStart;
	    secondsElapsed = tDelta / 1000f;

	    // update the current drawable path
	    SearchAlgorithm.solutionPathPointsDrawable = Collections.synchronizedList(currentNode.path);

	    // termination check
	    if (currentNode.path.size() == numberOfUnvisitedPoints + 1) {
		nodesExpanded++;
		if ((selectedVariation == CLASSIC)) {
		    shortestPath = currentNode.path;
		} else if ((selectedVariation == MAZE)) {
		    shortestPath = extractPathPointsFromEdges(currentNode.pathEdges, startPoint);
		    // remove the start point because pacman start position is at the edge of his current tile
		    // and with the start point in the path he travels half tile to the right first (in some solution
		    // cases).
		    shortestPath.remove(0);
		}
		finalGCost = (float) currentNode.gCost;

		// clear the drawable graph, we want to draw only the solution at the end
		graphDrawable = null;
		SearchAlgorithm.solutionPathPointsDrawable = Collections.synchronizedList(new ArrayList<Point>(shortestPath));
		// add again the start point to the drawable path, in order to draw the last edge of the path
		SearchAlgorithm.solutionPathPointsDrawable.add(0, startPoint);

		openList.clear();
		return;
	    }

	    openList.remove(currentNode);
	    nodesExpanded++;

	    List<Edge> edgesToNeighborPoints = findNeighborPoints(initialGraph, currentNode);

	    for (Edge edgeToNeighborPoint : edgesToNeighborPoints) {
		Point neighborPoint = edgeToNeighborPoint.getOppositeVertex(currentNode.path.get(0));

		path = new ArrayList<Point>(currentNode.path);
		path.add(0, neighborPoint);

		pathEdges = new ArrayList<Edge>(currentNode.pathEdges);
		pathEdges.add(edgeToNeighborPoint);
		double gCost = currentNode.gCost + edgeToNeighborPoint.getWeight();
		double hCost = computeHeuristicCost(initialGraph, currentNode.path, neighborPoint, startPoint);
		TSPNode neighborNode = new TSPNode(path, pathEdges, gCost, hCost);

		TSPNode nodeWithSimilarPath = findNodeWithSimilarPathInList(neighborNode.path, openList);

		// if optimal/smallest path not found change "<=" with "<"
		if ((nodeWithSimilarPath == null) || neighborNode.fCost <= nodeWithSimilarPath.fCost) {
		    if (nodeWithSimilarPath != null && neighborNode.fCost <= nodeWithSimilarPath.fCost) {
			openList.remove(nodeWithSimilarPath);
			nodesVisited--;
		    }
		    openList.add(neighborNode);
		    nodesVisited++;
		}
	    }
	}

	initialize();
    }

    private List<Edge> findNeighborPoints(Graph initialGraph, TSPNode currentNode)
    {
	Point currentPoint = currentNode.path.get(0);

	List<Edge> edgesToNeighborPoints = new ArrayList<Edge>();
	List<Point> neighborPoints = new ArrayList<Point>();
	List<Point> visitedPoints = new ArrayList<Point>(currentNode.path);

	Graph currentGraph = new Graph(initialGraph);
	currentGraph.getVertices().removeAll(visitedPoints);

	for (Edge edge : currentGraph.getEdges()) {
	    boolean invalidConnection = false;
	    if ((selectedVariation == MAZE) && edge.getInnerPoints().size() > 0) {
		for (Point innerPoint : edge.getInnerPoints()) {
		    if (currentGraph.getVertices().contains(innerPoint)) {
			invalidConnection = true;
			break;
		    }
		}
	    }
	    if (!invalidConnection && edge.isConnectedToVertex(currentPoint) && !visitedPoints.contains(edge.getOppositeVertex(currentPoint))) {
		neighborPoints.add(edge.getOppositeVertex(currentPoint));
		edgesToNeighborPoints.add(edge);
	    }
	}

	return edgesToNeighborPoints;
    }

    private double computeHeuristicCost(Graph initialGraph, List<Point> path, Point neighborPoint, Point startPoint)
    {
	double hCost = Double.MAX_VALUE;
	double distanceToNearestPoint = 0.0;
	double mstCost = 0.0;
	double nearestDistanceFromUnvisitedPointToStartPoint = 0.0;

	Graph currentGraph = new Graph(initialGraph);
	// remove all visited points from the initial graph
	for (Point vertex : path) {
	    currentGraph.removeVertex(vertex, false);
	}
	// currentGraph.print();

	// continue only if we have at least two vertices
	if (currentGraph.getVertices().size() > 1) {
	    Edge edgeToNearestUnvisitedPoint = getEdgeToNearestPoint(currentGraph, neighborPoint);
	    distanceToNearestPoint = edgeToNearestUnvisitedPoint.getWeight();
	    currentGraph.removeVertex(neighborPoint, false);
	    // currentGraph.print();
	    Graph mst = GraphAlgorithms.computeMinimumSpanningTree(currentGraph);
	    // mst.print();

	    // update the drawable graph
	    graphDrawable = mst;

	    mstCost = mst.getCost();
	}

	// continue only if we have at least one vertex (start vertex is also added)
	if ((selectedVariation == CLASSIC) && (currentGraph.getVertices().size() > 0)) {
	    currentGraph.addVertex(startPoint, false);
	    Edge edgeToStartPoint = getEdgeToNearestPoint(currentGraph, startPoint);
	    nearestDistanceFromUnvisitedPointToStartPoint = edgeToStartPoint.getWeight();
	}

	if ((selectedVariation == CLASSIC)) {
	    hCost = distanceToNearestPoint + mstCost + nearestDistanceFromUnvisitedPointToStartPoint;
	} else if ((selectedVariation == MAZE)) {
	    hCost = distanceToNearestPoint + mstCost;
	}

	return hCost;
    }

    private Edge getEdgeToNearestPoint(Graph graph, Point currentPoint)
    {
	// sort edges by their weight in ascending order
	Collections.sort(graph.getEdges(), Graph.edgeWeightSorter);

	// edges are sorted by weight in ascending order so the first one is the one we want
	for (Edge edge : graph.getEdges()) {
	    if (edge.isConnectedToVertex(currentPoint)) {
		return edge;
	    }
	}

	return null;
    }

    // Returns a "similar" node from the list, if there is one. Similar is any node that has an equal path list, meaning
    // it has the same visited points but (possibly) in a different order.
    private TSPNode findNodeWithSimilarPathInList(List<Point> path, List<TSPNode> list)
    {
	for (TSPNode node : list) {
	    boolean equalLists = (node.path.size() == path.size()) && (node.path.containsAll(path));
	    if (equalLists) {
		return node;
	    }
	}

	return null;
    }

    private List<Point> extractPathPointsFromEdges(List<Edge> pathEdges, Point startPoint)
    {
	List<Point> path = new ArrayList<Point>();

	Point currentPoint = startPoint;
	for (Edge edge : pathEdges) {
	    path.add(currentPoint);
	    List<Point> edgeInnerPoints = edge.getInnerPoints();
	    if (edgeInnerPoints.size() > 0) {
		if ((int) Utility.computeManhattanDistance(currentPoint, edgeInnerPoints.get(0)) != Maze.TILE) {
		    Collections.reverse(edgeInnerPoints);
		}
		for (Point innerPoint : edgeInnerPoints) {
		    path.add(innerPoint);
		}
	    }
	    currentPoint = edge.getOppositeVertex(currentPoint);
	}
	// add the final point
	path.add(currentPoint);

	// System.out.println("\n\nFinal path in points:");
	// int j = 1;
	// for (Point p : path) {
	// System.out.println((j++) + " - " + p);
	// }

	return path;
    }

    public List<Point> getShortestPath()
    {
	return shortestPath;
    }

    public void setShortestPath(List<Point> shortestPath)
    {
	this.shortestPath = shortestPath;
    }

    public List<Point> getNeighborTiles()
    {
	return neighborTiles;
    }

    public void setNeighborTiles(List<Point> neighborTiles)
    {
	this.neighborTiles = neighborTiles;
    }

    public Point getCurrentNeighborTile()
    {
	return currentNeighborTile;
    }

    public void setCurrentNeighborTile(Point currentNeighborTile)
    {
	this.currentNeighborTile = currentNeighborTile;
    }

    public float getFinalGCost()
    {
	return finalGCost;
    }

    public void setFinalGCost(float finalGCost)
    {
	this.finalGCost = finalGCost;
    }

    public int getNodesVisited()
    {
	return nodesVisited;
    }

    public void setNodesVisited(int nodesVisited)
    {
	this.nodesVisited = nodesVisited;
    }

    public int getNodesExpanded()
    {
	return nodesExpanded;
    }

    public void setNodesExpanded(int nodesExpanded)
    {
	this.nodesExpanded = nodesExpanded;
    }

    public float getSecondsElapsed()
    {
	return secondsElapsed;
    }

    public void setSecondsElapsed(float secondsElapsed)
    {
	this.secondsElapsed = secondsElapsed;
    }

    public boolean isStarted()
    {
	return started;
    }

    public void setStarted(boolean started)
    {
	this.started = started;
    }

    public boolean isRunning()
    {
	return running;
    }

    public void setRunning(boolean running)
    {
	this.running = running;
    }

    public boolean isCanceled()
    {
	return canceled;
    }

    public void setCanceled(boolean canceled)
    {
	this.canceled = canceled;
    }

    public boolean isFinished()
    {
	return finished;
    }

    public void setFinished(boolean finished)
    {
	this.finished = finished;
    }

    public int getSelectedVariation()
    {
	return selectedVariation;
    }

    public void setSelectedVariation(int selectedVariation)
    {
	this.selectedVariation = selectedVariation;
    }
}
