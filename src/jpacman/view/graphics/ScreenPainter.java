package jpacman.view.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import jpacman.Game;
import jpacman.GameApplication;
import jpacman.controller.edumode.AIAlgorithmsInteractiveLearningController;
import jpacman.controller.edumode.AIAlgorithmsStepwiseLearningController;
import jpacman.controller.edumode.EducationalModeController;
import jpacman.controller.edumode.PathfindingController;
import jpacman.input.Mouse;
import jpacman.model.Maze;
import jpacman.model.edumode.Node;
import jpacman.model.edumode.SearchAlgorithm;
import jpacman.model.edumode.TSPSolver;
import jpacman.model.edumode.graph.Edge;
import jpacman.util.PathFinder;

public final class ScreenPainter
{
    // Don't let anyone instantiate this class.
    private ScreenPainter()
    {
    }

    public static final Font smallFixedBoldFont1 = new Font("Courier", Font.BOLD, 10);
    public static final Font smallFixedBoldFont2 = new Font("Courier", Font.BOLD, 9);
    public static final Font smallFixedBoldFont3 = new Font("Courier", Font.BOLD, 7);
    public static final Font smallFixedBoldFont4 = new Font("Courier", Font.BOLD, 5);
    public static final Font normalFixedBoldFont = new Font("Courier", Font.BOLD, 13);
    // public static final Font smallFixedBoldFont2 = new Font("Liberation Mono", Font.BOLD, 9);
    public static final Font smallFixedPlainFont = new Font("Courier", Font.PLAIN, 11);

    public static boolean debug;
    public static boolean info;
    public static boolean grid;
    public static boolean routeTiles;
    public static boolean ghostsHouseRouteTiles;
    public static boolean intersectionsTiles;
    public static boolean crossroadsTiles;
    public static boolean targetTiles;

    public static Maze maze;

    // drawing related variables
    public static Graphics2D g2d;
    public static RenderingHints rh1;
    public static RenderingHints rh2;
    public static BasicStroke normal;
    public static BasicStroke thick;
    public static BasicStroke thicker;
    public static BasicStroke dashed;
    public static Random randomColors;
    public static int xOffset;
    public static int yOffset;

    public static volatile List<Point> blinkyPath;
    public static volatile Node hoveredNode;

    public static Color mazeColor;

    static {
	rh1 = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	rh2 = new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	normal = new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	thick = new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	thicker = new BasicStroke(3.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	final float dash1[] = { 10.0f };
	dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dash1, 0.0f);

	blinkyPath = new ArrayList<Point>();

	xOffset = GameApplication.X_OFFSET;
	yOffset = GameApplication.Y_OFFSET;
    }

    public static void debug(Object o)
    {
	if (debug) {
	    System.out.println(o.toString());
	}
    }

    public static void drawDebugString(Object o, int x, int y)
    {
	g2d.drawString(o.toString(), x, y);
    }

    public static void drawMazeGrid()
    {
	g2d.setStroke(normal);
	g2d.setFont(smallFixedPlainFont);
	g2d.setPaint(Color.LIGHT_GRAY);
	g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

	int xTile = 1;
	int xTileOffset;
	int yTile = 1;
	for (int y = yOffset; y < yOffset + Maze.HEIGHT; y += Maze.TILE) {
	    xTileOffset = (yTile < 10) ? xOffset - Maze.HALF_TILE - 3 : xOffset - Maze.TILE;
	    g2d.drawString(new Integer(yTile++).toString(), xTileOffset, y + 3 * Maze.QUARTER_TILE);
	    for (int x = xOffset; x < xOffset + Maze.WIDTH; x += Maze.TILE) {
		if (xTile <= Maze.NUM_OF_COLUMNS) {
		    xTileOffset = (xTile < 10) ? x + 6 : x + 3;
		    g2d.drawString(new Integer(xTile++).toString(), xTileOffset, yOffset - Maze.HALF_TILE + 3);
		}
		g2d.drawRect(x, y, Maze.TILE, Maze.TILE);
	    }
	}
    }

    public static void drawHoveredMazeTileLocation()
    {
	Point mouseHoverCoordinates = Mouse.getCoordinates();
	mouseHoverCoordinates.translate(-GameApplication.X_OFFSET, -GameApplication.Y_OFFSET);
	Point currentMazeTile = PathFinder.computeCurrentTile(mouseHoverCoordinates);
	String tileLocation = new String("node(" + ((currentMazeTile.x / Maze.TILE) + 1) + "," + ((currentMazeTile.y / Maze.TILE) + 1) + ")");

	if (mouseHoverCoordinates.x > 0 && mouseHoverCoordinates.y > 0 && mouseHoverCoordinates.x < Maze.WIDTH && mouseHoverCoordinates.y < Maze.HEIGHT) {
	    g2d.setFont(normalFixedBoldFont);
	    g2d.setStroke(thick);
	    g2d.setPaint(Color.GREEN);
	    g2d.drawRect(currentMazeTile.x + xOffset - Maze.HALF_TILE, currentMazeTile.y - Maze.HALF_TILE + yOffset, Maze.TILE, Maze.TILE);
	    if (hoveredNode == null || Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_INTERACTIVE_LEARNING //
	    && (EducationalModeController.algorithmIndex == SearchAlgorithm.DFS //
	    || EducationalModeController.algorithmIndex == SearchAlgorithm.BFS //
	    || EducationalModeController.algorithmIndex == SearchAlgorithm.IDS)) {
		g2d.setPaint(Color.DARK_GRAY);
		g2d.fillRect(mouseHoverCoordinates.x + xOffset + 22, mouseHoverCoordinates.y + 18 + yOffset, 5 * Maze.TILE - Maze.QUARTER_TILE, 18);
		g2d.setPaint(Color.WHITE);
		g2d.drawString(tileLocation, mouseHoverCoordinates.x + xOffset + 3 * Maze.HALF_TILE, mouseHoverCoordinates.y + yOffset + 4 * Maze.HALF_TILE);
	    } else if (hoveredNode != null && Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_INTERACTIVE_LEARNING && !AIAlgorithmsInteractiveLearningController.hardDifficulty //
	    && (EducationalModeController.algorithmIndex == SearchAlgorithm.BF //
	    || EducationalModeController.algorithmIndex == SearchAlgorithm.BS //
	    || EducationalModeController.algorithmIndex == SearchAlgorithm.A_STAR)) {
		g2d.setPaint(Color.DARK_GRAY);
		g2d.fillRect(mouseHoverCoordinates.x + xOffset + 22, mouseHoverCoordinates.y + 18 + yOffset, 5 * Maze.TILE - Maze.QUARTER_TILE, 34);
		g2d.setPaint(Color.WHITE);
		g2d.drawString(tileLocation, mouseHoverCoordinates.x + xOffset + 3 * Maze.HALF_TILE, mouseHoverCoordinates.y + yOffset + 4 * Maze.HALF_TILE);
		DecimalFormat df = new DecimalFormat("0.000");
		String fCostText = "F = " + df.format(hoveredNode.getFCost());
		String hCostText = "H = " + df.format(hoveredNode.getHCost());
		String function = fCostText; // for easy difficulty
		if (AIAlgorithmsInteractiveLearningController.mediumDifficulty) {
		    function = hCostText;
		}
		g2d.drawString(function, mouseHoverCoordinates.x + xOffset + 3 * Maze.HALF_TILE, mouseHoverCoordinates.y + yOffset + 6 * Maze.HALF_TILE);
	    }
	}
    }

    public static void drawStartNode(boolean drawID)
    {
	if (SearchAlgorithm.startNodeDrawable != null) {
	    g2d.setStroke(normal);
	    if (Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_STEPWISE_LEARNING) {
		g2d.setPaint(Color.RED);
		SearchAlgorithm.startNodeDrawable.setBackgroundColor(Color.RED);
	    } else {
		g2d.setPaint(Color.YELLOW);
		SearchAlgorithm.startNodeDrawable.setBackgroundColor(Color.YELLOW);
	    }
	    g2d.fillRect(SearchAlgorithm.startNodeDrawable.getCoordinates().x + xOffset - Maze.HALF_TILE, SearchAlgorithm.startNodeDrawable.getCoordinates().y + yOffset - Maze.HALF_TILE, Maze.TILE, Maze.TILE);
	    if (drawID) {
		drawNodeID(SearchAlgorithm.startNodeDrawable);
	    }
	}
    }

    public static void drawGoalNode(boolean drawID)
    {
	if (SearchAlgorithm.goalNodeDrawable != null) {
	    g2d.setStroke(normal);
	    if (Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_STEPWISE_LEARNING) {
		g2d.setPaint(Color.YELLOW);
		SearchAlgorithm.goalNodeDrawable.setBackgroundColor(Color.YELLOW);
	    } else {
		g2d.setPaint(Color.GREEN);
		SearchAlgorithm.goalNodeDrawable.setBackgroundColor(Color.GREEN);
	    }
	    g2d.fillRect(SearchAlgorithm.goalNodeDrawable.getCoordinates().x + xOffset - Maze.HALF_TILE, SearchAlgorithm.goalNodeDrawable.getCoordinates().y + yOffset - Maze.HALF_TILE, Maze.TILE, Maze.TILE);
	    if (drawID) {
		drawNodeID(SearchAlgorithm.goalNodeDrawable);
	    }
	}
    }

    private static void drawNodeID(Node node)
    {
	String nodeID = node.getID();
	if (nodeID == null || nodeID.isEmpty()) {
	    return;
	}

	int nodeIdInt = Integer.parseInt(nodeID);
	if (nodeIdInt < 100) {
	    g2d.setFont(smallFixedBoldFont1);
	} else if (nodeIdInt >= 100 && nodeIdInt < 1000) {
	    g2d.setFont(smallFixedBoldFont2);
	} else if (nodeIdInt >= 1000 && nodeIdInt < 10000) {
	    g2d.setFont(smallFixedBoldFont3);
	} else { // nodeIdInt >= 10000
	    g2d.setFont(smallFixedBoldFont4);
	}

	FontRenderContext frc = g2d.getFontRenderContext();
	GlyphVector gv = g2d.getFont().createGlyphVector(frc, nodeID);
	float idXOffset = gv.getPixelBounds(null, node.getCoordinates().x, node.getCoordinates().y).width / 2f - xOffset;
	float idYOffset = gv.getPixelBounds(null, node.getCoordinates().x, node.getCoordinates().y).height / 2f + yOffset;

	g2d.setPaint(Color.BLACK);
	g2d.drawString(nodeID, node.getCoordinates().x - idXOffset, node.getCoordinates().y + idYOffset);
    }

    public static void drawExpandedNodes()
    {
	g2d.setRenderingHints(rh1);
	g2d.setStroke(normal);

	synchronized (SearchAlgorithm.expandedNodesDrawable) {
	    int upperBound = //
	    ((Game.getActiveSubMode() == Game.SubMode.CUSTOM_SPACE_PATHFINDING || Game.getActiveSubMode() == Game.SubMode.MAZE_PATHFINDING) //
	    && PathfindingController.numberOfExpandedNodes >= 0) //
	    ? PathfindingController.numberOfExpandedNodes : SearchAlgorithm.expandedNodesDrawable.size();
	    int expandedNodesSoFar = 0;
	    int colorShadeCounter = 0;

	    float step = 0f;
	    if (Game.getActiveSubMode() == Game.SubMode.CUSTOM_SPACE_PATHFINDING) {
		if (Node.defaultBackgroundColorShading) {
		    step = 0.80f / upperBound;
		} else {
		    step = 0.2f / upperBound;
		}
	    } else {
		step = 0.75f / upperBound;
	    }
	    for (Node node : SearchAlgorithm.expandedNodesDrawable) {
		if (expandedNodesSoFar++ < upperBound) {
		    if (Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_INTERACTIVE_LEARNING && AIAlgorithmsInteractiveLearningController.hardDifficulty) {
			g2d.setPaint(Color.WHITE);
		    } else {
			colorShadeCounter = computeAndSetColorForExpandedNode(node, step, colorShadeCounter);
		    }
		    g2d.fillRect(node.getCoordinates().x + xOffset - Maze.HALF_TILE, node.getCoordinates().y - Maze.HALF_TILE + yOffset, Maze.TILE, Maze.TILE);
		    if (Game.getActiveSubMode() == Game.SubMode.CUSTOM_SPACE_PATHFINDING || (Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_STEPWISE_LEARNING && !AIAlgorithmsStepwiseLearningController.stepwiseExecutionMode)//
		    || (Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_INTERACTIVE_LEARNING && (AIAlgorithmsInteractiveLearningController.mediumDifficulty || AIAlgorithmsInteractiveLearningController.hardDifficulty))) {
			continue;
		    }
		    drawNodeID(node);
		}
	    }
	}
	drawSolutionPathNodes();
    }

    private static int computeAndSetColorForExpandedNode(Node node, float step, int colorShadeIndex)
    {
	Color color = Color.WHITE;

	if (Game.getActiveSubMode() == Game.SubMode.CUSTOM_SPACE_PATHFINDING) {
	    if (Node.defaultBackgroundColorShading) {
		color = new Color(0.9f - (colorShadeIndex * step), 0.9f - (colorShadeIndex * step), 0.9f - (colorShadeIndex * step));
		if (colorShadeIndex * step <= 0.80f) {
		    colorShadeIndex++;
		}
	    } else {
		color = new Color(0.2f - (colorShadeIndex * step) / 2.75f, 1f - (colorShadeIndex * step) * 4.5f, 1f - (colorShadeIndex * step) * 4.5f);
		if (colorShadeIndex * step <= 0.2f) {
		    colorShadeIndex++;
		}
	    }
	} else if (Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_STEPWISE_LEARNING) {
	    if (Node.defaultBackgroundColorShading) {
		color = new Color(1f - (colorShadeIndex * step), 1f - (colorShadeIndex * step), 1f - (colorShadeIndex * step));
	    } else {
		color = new Color(1f, (colorShadeIndex * step), 0f);
	    }
	    if (colorShadeIndex * step <= 0.75f) {
		colorShadeIndex++;
	    }
	} else {
	    if (node.equals(SearchAlgorithm.startNodeDrawable) && node.getID().equals(SearchAlgorithm.startNodeDrawable.getID())) {
		if (Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_STEPWISE_LEARNING) {
		    color = Color.RED;
		} else {
		    color = Color.YELLOW;
		}
	    } else if (node.equals(SearchAlgorithm.goalNodeDrawable)) {
		if (Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_STEPWISE_LEARNING) {
		    color = Color.YELLOW;
		} else {
		    color = Color.GREEN;
		}
	    } else {
		if (Node.defaultBackgroundColorShading) {
		    color = new Color(1f - (colorShadeIndex * step), 1f - (colorShadeIndex * step), 1f - (colorShadeIndex * step));
		} else if (mazeColor == Maze.maze1.getColor() || mazeColor == Maze.maze2edu.getColor()) {
		    color = new Color(1f - (colorShadeIndex * step), 1f - (colorShadeIndex * step), 1f);
		} else if (mazeColor == Maze.maze2.getColor() || mazeColor == Maze.maze3edu.getColor()) {
		    color = new Color(1f, 1f - (colorShadeIndex * step), 1f - (colorShadeIndex * step));
		} else if (mazeColor == Maze.maze3.getColor() || mazeColor == Maze.maze4edu.getColor()) {
		    color = new Color(1f - (colorShadeIndex * step), 1f, 1f - (colorShadeIndex * step));
		} else if (mazeColor == Maze.maze4.getColor() || mazeColor == Maze.maze5edu.getColor()) {
		    color = new Color(1f - (colorShadeIndex * step), 1f, 1f);
		} else if (mazeColor == Maze.maze5.getColor() || mazeColor == Maze.maze6edu.getColor()) {
		    color = new Color(1f, 1f - (colorShadeIndex * step), 1f);
		}
		if (colorShadeIndex * step <= 0.75f) {
		    colorShadeIndex++;
		}
	    }
	}
	if (EducationalModeController.algorithmIndex == SearchAlgorithm.IDS && !PathfindingController.lastIteration) {
	    ; // do nothing
	} else {
	    node.setBackgroundColor(color);
	}
	g2d.setPaint(color);

	return colorShadeIndex;
    }

    private static void drawSolutionPathNodes()
    {
	synchronized (SearchAlgorithm.solutionPathNodesDrawable) {
	    for (Node node : SearchAlgorithm.solutionPathNodesDrawable) {
		if (Game.getActiveSubMode() == Game.SubMode.CUSTOM_SPACE_PATHFINDING || Game.getActiveSubMode() == Game.SubMode.MAZE_PATHFINDING || Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_INTERACTIVE_LEARNING) {
		    g2d.setPaint(Color.YELLOW);
		} else if (Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_STEPWISE_LEARNING) {
		    g2d.setPaint(Color.GREEN);
		}
		g2d.fillRect(node.getCoordinates().x + xOffset - Maze.HALF_TILE, node.getCoordinates().y - Maze.HALF_TILE + yOffset, Maze.TILE, Maze.TILE);
		if (Game.getActiveSubMode() != Game.SubMode.CUSTOM_SPACE_PATHFINDING) {
		    drawNodeID(node);
		}
	    }
	}
    }

    public static void drawLineSolutionPath()
    {
	g2d.setRenderingHints(rh1);
	randomColors = null;

	if (Game.getActiveSubMode() == Game.SubMode.CUSTOM_SPACE_PATHFINDING) {
	    g2d.setStroke(thicker);
	    g2d.setPaint(Color.YELLOW);
	} else if (Game.getActiveSubMode() == Game.SubMode.CLASSIC_TSP) {
	    g2d.setStroke(thicker);
	    randomColors = new Random();
	} else if (Game.getActiveSubMode() == Game.SubMode.MAZE_TSP) {
	    g2d.setStroke(normal);
	    g2d.setPaint(Color.YELLOW);
	}

	synchronized (SearchAlgorithm.solutionPathPointsDrawable) {
	    Point vertex1 = null;
	    for (Point vertex2 : SearchAlgorithm.solutionPathPointsDrawable) {
		if (vertex1 == null) {
		    vertex1 = vertex2;
		    continue;
		}
		if (randomColors != null) {
		    g2d.setPaint(new Color(randomColors.nextInt(255), randomColors.nextInt(255), randomColors.nextInt(255)));
		}
		g2d.drawLine(vertex1.x + xOffset, vertex1.y + yOffset, vertex2.x + xOffset, vertex2.y + yOffset);
		vertex1 = vertex2;
	    }
	}
    }

    public static void drawGhostChasePath()
    {
	if (blinkyPath != null) {
	    g2d.setRenderingHints(rh1);
	    g2d.setStroke(thicker);
	    g2d.setPaint(new Color(102, 255, 102));
	    int numberOfEdges = blinkyPath.size() - 1;
	    Point vertex1 = null;
	    Point vertex2 = null;
	    for (int i = 0; i < numberOfEdges; i++) {
		vertex1 = blinkyPath.get(i);
		vertex2 = blinkyPath.get(i + 1);
		g2d.drawLine(vertex1.x + xOffset, vertex1.y + yOffset, vertex2.x + xOffset, vertex2.y + yOffset);
	    }
	}
    }

    public static void drawAlgorithmInfo()
    {
	drawExpandedNodes();
	drawUnexpandedNodes();
	if (SearchAlgorithm.expandedNodesDrawable.size() == 0) {
	    drawStartNode(false);
	} else {
	    drawStartNode(true);
	}
	drawGoalNode(false);

	g2d.setStroke(normal);

	if (SearchAlgorithm.currentNodeDrawable != null) {
	    if (SearchAlgorithm.currentNodeDrawable.equals(SearchAlgorithm.goalNodeDrawable)) {
		SearchAlgorithm.goalNodeDrawable.setID(SearchAlgorithm.currentNodeDrawable.getID());
		drawGoalNode(true);
	    } else {
		Point currentNodeLocation = SearchAlgorithm.currentNodeDrawable.getCoordinates();
		g2d.setPaint(Node.CURRENT_NODE_COLOR);
		g2d.fillRect(currentNodeLocation.x + xOffset - Maze.HALF_TILE, currentNodeLocation.y + yOffset - Maze.HALF_TILE, Maze.TILE, Maze.TILE);
		drawNodeID(SearchAlgorithm.currentNodeDrawable);
	    }
	}

	synchronized (SearchAlgorithm.neighborNodesDrawable) {
	    for (Point neighborNode : SearchAlgorithm.neighborNodesDrawable) {
		g2d.setPaint(Node.NEIGHBOR_NODE_COLOR);
		g2d.fillRect(neighborNode.x + xOffset - Maze.HALF_TILE, neighborNode.y - Maze.HALF_TILE + yOffset, Maze.TILE, Maze.TILE);
	    }
	}

	if (SearchAlgorithm.currentNeighborNodeDrawable != null) {
	    Point currentChildLocation = SearchAlgorithm.currentNeighborNodeDrawable.getCoordinates();
	    g2d.setPaint(Node.CURRENT_NEIGHBOR_NODE_COLOR);
	    g2d.fillRect(currentChildLocation.x + xOffset - Maze.HALF_TILE, currentChildLocation.y + yOffset - Maze.HALF_TILE, Maze.TILE, Maze.TILE);
	}
    }

    public static void drawUnexpandedNodes()
    {
	synchronized (SearchAlgorithm.unexpandedNodesDrawable) {
	    g2d.setRenderingHints(rh1);
	    g2d.setStroke(normal);
	    if (Node.defaultBackgroundColorShading) {
		g2d.setPaint(Node.UNEXPANDED_NODE_COLOR);
	    } else {
		g2d.setPaint(Color.WHITE);
	    }
	    for (Node node : SearchAlgorithm.unexpandedNodesDrawable) {
		g2d.fillRect(node.getCoordinates().x + xOffset - Maze.HALF_TILE, node.getCoordinates().y - Maze.HALF_TILE + yOffset, Maze.TILE, Maze.TILE);
	    }
	}
    }

    public static void drawUserInteractionVisuals()
    {
	if (hoveredNode != null) {
	    g2d.setStroke(thick);
	    g2d.setPaint(new Color(0xE1FF00));
	    g2d.fillRect(hoveredNode.getCoordinates().x + xOffset - Maze.HALF_TILE, hoveredNode.getCoordinates().y + yOffset - Maze.HALF_TILE, Maze.TILE, Maze.TILE);
	    g2d.setPaint(Color.BLACK);
	    g2d.fillOval(hoveredNode.getCoordinates().x + xOffset - Maze.QUARTER_TILE, hoveredNode.getCoordinates().y + yOffset - Maze.QUARTER_TILE, Maze.HALF_TILE + 1, Maze.HALF_TILE + 1);
	}
    }

    public static void drawGraph()
    {
	List<Edge> edges = new ArrayList<Edge>();
	if (TSPSolver.graphDrawable != null) {
	    edges = Collections.synchronizedList(TSPSolver.graphDrawable.getEdges());
	}

	g2d.setRenderingHints(rh1);
	if (Game.getActiveSubMode() == Game.SubMode.CLASSIC_TSP) {
	    g2d.setStroke(dashed);
	} else if (Game.getActiveSubMode() == Game.SubMode.MAZE_TSP) {
	    g2d.setStroke(normal);
	}
	g2d.setPaint(Color.WHITE);

	synchronized (edges) {
	    for (Edge edge : edges) {
		if (edge.getInnerPoints().size() > 0) {
		    Point innerPoint = edge.getInnerPoints().get(0);
		    g2d.drawLine(edge.getVertexA().x + xOffset, edge.getVertexA().y + yOffset, innerPoint.x + xOffset, innerPoint.y + yOffset);
		    int numberOfInnerEdges = edge.getInnerPoints().size() - 1;
		    for (int i = 0; i < numberOfInnerEdges; i++) {
			Point innerPoint1 = edge.getInnerPoints().get(i);
			Point innerPoint2 = edge.getInnerPoints().get(i + 1);
			g2d.drawLine(innerPoint1.x + xOffset, innerPoint1.y + yOffset, innerPoint2.x + xOffset, innerPoint2.y + yOffset);
		    }
		    g2d.drawLine(edge.getInnerPoints().get(numberOfInnerEdges).x + xOffset, edge.getInnerPoints().get(numberOfInnerEdges).y + yOffset, edge.getVertexB().x + xOffset, edge.getVertexB().y + yOffset);
		} else {
		    g2d.drawLine(edge.getVertexA().x + xOffset, edge.getVertexA().y + yOffset, edge.getVertexB().x + xOffset, edge.getVertexB().y + yOffset);
		}
	    }
	}
    }
}
