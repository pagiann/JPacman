package com.jpacman.controller.edumode;

import static com.jpacman.model.edumode.GraphicalSearchTree.*;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jpacman.Game;
import com.jpacman.model.edumode.GraphicalSearchTree;
import com.jpacman.model.edumode.Node;
import com.jpacman.model.edumode.SearchAlgorithm;

public class GraphicalSearchTreeController extends JFrame implements ChangeListener, MouseWheelListener
{
    private static final long serialVersionUID = 1L;

    public static final Color F_COST_COLOR = new Color(0x5A2D0C);
    public static final Color G_COST_COLOR = new Color(0x8B4513);
    public static final Color H_COST_COLOR = new Color(0xCA641C);

    public static GraphicalSearchTree graphicalSearchTree;
    public static List<GraphicalSearchTree> paintableNodesOfAllTreesOfIDS;
    public static Color mazeColor;
    public static volatile boolean scrolledToCurrentNode;
    private static boolean scrolledToCurrentNode2;
    private static RenderingHints hints;
    private static BasicStroke normal;
    private static BasicStroke dashed;
    private static BasicStroke dashedThick;

    static {
	paintableNodesOfAllTreesOfIDS = new ArrayList<GraphicalSearchTree>();

	hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	hints.add(new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY));
	hints.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));
	hints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
	hints.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON));
	normal = new BasicStroke(2.0f);
	final float dash[] = { 5.0f };
	dashed = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dash, 0.0f);
	dashedThick = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, dash, 0.0f);
    }

    private final int SCALE_MIN = -90;
    private final int SCALE_MAX = 90;
    private final int SCALE_INIT = 0;

    private JPanel zoomButtons;
    private JCheckBox alwaysOnTopCheckBox;
    private JLabel downscaleSliderLabel;
    private JLabel upscaleSliderLabel;
    private JSlider scaleSlider;

    private JPanel topPanel;
    private JPanel innerPanel;
    private JCheckBox autoscrollingCheckBox;
    private JCheckBox nodeMazeLocationCheckBox;
    private JCheckBox heuristicFunctionCheckBox;

    private DrawCanvas canvas;
    private final JScrollPane treeScrollPane;
    private double scale = 1.0;

    private boolean autoscrolling;
    private boolean showNodeLocation;
    private boolean showHeuristicFunction;

    public GraphicalSearchTreeController()
    {
	// Create the zoom panel.
	zoomButtons = new JPanel();

	// Create the always on top check box.
	alwaysOnTopCheckBox = new JCheckBox("Always On Top", true);
	// using Lambda expression
	alwaysOnTopCheckBox.addItemListener(e -> {
	    if (alwaysOnTopCheckBox.isSelected()) {
		setAlwaysOnTop(true);
	    } else {
		setAlwaysOnTop(false);
	    }
	});

	// Create the zoom buttons label.
	downscaleSliderLabel = new JLabel("Downscale (%)", JLabel.CENTER);
	upscaleSliderLabel = new JLabel("Upscale (%)", JLabel.CENTER);

	// Create the zoom slider.
	scaleSlider = new JSlider(JSlider.HORIZONTAL, SCALE_MIN, SCALE_MAX, SCALE_INIT);
	scaleSlider.addChangeListener(this);
	scaleSlider.addMouseWheelListener(this);

	// Turn on labels at major tick marks.
	scaleSlider.setMajorTickSpacing(30);
	scaleSlider.setMinorTickSpacing(10);
	scaleSlider.setPaintTicks(true);
	scaleSlider.setPaintLabels(true);

	// Add components to panel
	zoomButtons.add(alwaysOnTopCheckBox);
	zoomButtons.add(Box.createRigidArea(new Dimension(20, 0)));
	zoomButtons.add(downscaleSliderLabel);
	zoomButtons.add(scaleSlider);
	zoomButtons.add(upscaleSliderLabel);

	// Create the autoscrolling check box.
	autoscrollingCheckBox = new JCheckBox("Autoscrolling", (autoscrolling = true));
	autoscrollingCheckBox.addItemListener(e -> {
	    if (autoscrollingCheckBox.isSelected()) {
		autoscrolling = true;
	    } else {
		autoscrolling = false;
	    }
	});
	// Create the show node maze location check box.
	nodeMazeLocationCheckBox = new JCheckBox("Show node maze (tile) location", false);
	nodeMazeLocationCheckBox.addItemListener(e -> {
	    if (nodeMazeLocationCheckBox.isSelected()) {
		showNodeLocation = true;
	    } else {
		showNodeLocation = false;
	    }
	    getContentPane().getComponent(1).repaint();
	});
	// Create the show heuristic function check box.
	heuristicFunctionCheckBox = new JCheckBox("Show heuristic function", false);
	heuristicFunctionCheckBox.addItemListener(e -> {
	    if (heuristicFunctionCheckBox.isSelected()) {
		showHeuristicFunction = true;
	    } else {
		showHeuristicFunction = false;
	    }
	    getContentPane().getComponent(1).repaint();
	});

	innerPanel = new JPanel();
	innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));

	innerPanel.add(Box.createRigidArea(new Dimension(25, 0)));
	innerPanel.add(autoscrollingCheckBox);
	innerPanel.add(Box.createRigidArea(new Dimension(20, 0)));
	innerPanel.add(nodeMazeLocationCheckBox);
	innerPanel.add(Box.createRigidArea(new Dimension(20, 0)));
	innerPanel.add(heuristicFunctionCheckBox);
	innerPanel.add(Box.createRigidArea(new Dimension(25, 0)));

	topPanel = new JPanel();
	topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
	topPanel.add(zoomButtons);
	topPanel.add(innerPanel);

	canvas = new DrawCanvas(); // Construct the drawing canvas
	treeScrollPane = new JScrollPane(canvas);
	treeScrollPane.getVerticalScrollBar().setUnitIncrement(nodeTileSize);
	treeScrollPane.getHorizontalScrollBar().setUnitIncrement(nodeTileSize);

	setTitle("Search Tree");
	setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
	setLayout(new BorderLayout());
	getContentPane().add(topPanel, BorderLayout.PAGE_START);
	getContentPane().add(treeScrollPane, BorderLayout.CENTER);
	setMinimumSize(new Dimension(550, 500));
	setSize(550, 700);
	setVisible(false);
	setAlwaysOnTop(true);
    }

    @Override
    public void stateChanged(ChangeEvent e)
    {
	JSlider source = (JSlider) e.getSource();
	if (!source.getValueIsAdjusting()) {
	    double notch = source.getValue() / 10.0;
	    if (notch == 0) {
		scale = 1.0;
	    } else {
		scale = 1.0 + (notch * 0.1);
	    }
	    this.getContentPane().getComponent(1).revalidate();
	    this.getContentPane().getComponent(1).repaint();
	}
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e)
    {
	int notches = e.getWheelRotation();
	if (notches > 0 && scaleSlider.getValue() > scaleSlider.getMinimum()) {
	    scaleSlider.setValue(scaleSlider.getValue() - 10);
	} else if (notches < 0 && scaleSlider.getValue() < scaleSlider.getMaximum()) {
	    scaleSlider.setValue(scaleSlider.getValue() + 10);
	}
	this.getContentPane().getComponent(1).revalidate();
	this.getContentPane().getComponent(1).repaint();
    }

    public static GraphicalSearchTree getGraphicalSearchTree()
    {
	return graphicalSearchTree;
    }

    /**
     * Define a inner class called DrawCanvas which is a JPanel used for custom drawing
     */
    private class DrawCanvas extends JPanel
    {
	private static final long serialVersionUID = 1L;

	private final Font font1 = new Font("Liberation Mono", Font.PLAIN, 15);
	private final Font font2 = new Font("Liberation Mono", Font.PLAIN, 12);
	private final Font font3 = new Font("Liberation Mono", Font.BOLD, 8);

	@Override
	public void paintComponent(Graphics g)
	{
	    // paint parent's background
	    super.paintComponent(g);

	    if (graphicalSearchTree != null) {
		Graphics2D g2d = (Graphics2D) g;
		// set nice rendering hints like antialiasing etc.
		g2d.setRenderingHints(hints);
		// set the stroke
		g2d.setStroke(normal);
		// scale the tree
		g2d.scale(scale, scale);

		// disable the heuristicFunctionCheckBox if the algorithm is not a heuristic one
		if (EducationalModeController.algorithmIndex == SearchAlgorithm.DFS || //
		EducationalModeController.algorithmIndex == SearchAlgorithm.BFS || //
		EducationalModeController.algorithmIndex == SearchAlgorithm.IDS) {
		    heuristicFunctionCheckBox.setSelected(false);
		    heuristicFunctionCheckBox.setEnabled(false);
		} else {
		    heuristicFunctionCheckBox.setEnabled(true);
		}

		// paint the whole tree
		for (Node node : graphicalSearchTree.getPaintableNodes()) {
		    if (Game.getActiveSubMode() == Game.SubMode.MAZE_PATHFINDING) {
			paintNodeForPathfindingMode(g2d, node);
			repaint();
		    } else if (Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_STEPWISE_LEARNING) {
			scrolledToCurrentNode2 = false;
			paintNodeForAIAlgorithmsLearningMode(g2d, node);
		    }
		    if (!scrolledToCurrentNode && node.equals(SearchAlgorithm.currentNodeDrawable)) {
			scrollToCurrentNode(node);
		    }
		}
	    }
	}

	private void paintNodeForPathfindingMode(Graphics2D g2d, Node node)
	{
	    BasicStroke edgeStroke = null;
	    if (node.equals(SearchAlgorithm.startNodeDrawable) && node.getID().equals(SearchAlgorithm.startNodeDrawable.getID())) {
		// set background color for start node
		node.setBackgroundColor(Color.YELLOW);
	    } else if (node.equals(SearchAlgorithm.goalNodeDrawable)) {
		// set background color for goal node
		node.setBackgroundColor(Color.GREEN);
	    } else if (node.getID().equals("")) { // if node is an unexpanded one set dashed edge stroke
		edgeStroke = dashed;
	    }
	    // paint node
	    paintAll(g2d, node, node.getBackgroundColor(), edgeStroke, true);
	    // paint solution path nodes
	    synchronized (SearchAlgorithm.solutionPathNodesDrawable) {
		for (Node pathNode : SearchAlgorithm.solutionPathNodesDrawable) {
		    if (node.equals(pathNode) && node.getID().equals(pathNode.getID())) {
			paintNode(g2d, node, Color.YELLOW);
			paintNodeID(g2d, node);
			break;
		    }
		}
	    }
	}

	private void paintNodeForAIAlgorithmsLearningMode(Graphics2D g2d, Node node)
	{
	    // paint expanded nodes
	    synchronized (SearchAlgorithm.expandedNodesDrawable) {
		for (Node expandedNode : SearchAlgorithm.expandedNodesDrawable) {
		    if (node.equals(expandedNode)) {
			paintAll(g2d, node, expandedNode.getBackgroundColor(), null, true);
			break;
		    }
		}
	    }
	    // paint unexpanded nodes (those currently in the search frontier)
	    if (SearchAlgorithm.unexpandedNodesDrawable.contains(node)) {
		if (Node.defaultBackgroundColorShading) {
		    paintAll(g2d, node, Node.UNEXPANDED_NODE_COLOR, dashed, false);
		} else {
		    paintAll(g2d, node, Color.WHITE, dashed, false);
		}
	    }
	    // paint goal node
	    if (node.equals(SearchAlgorithm.currentNodeDrawable) && node.equals(SearchAlgorithm.goalNodeDrawable)) {
		paintAll(g2d, node, Color.YELLOW, null, true);
	    }
	    // paint current node
	    if (node.equals(SearchAlgorithm.currentNodeDrawable) && node.getID().equals(SearchAlgorithm.currentNodeDrawable.getID()) && !SearchAlgorithm.currentNodeDrawable.equals(SearchAlgorithm.goalNodeDrawable)) {
		paintAll(g2d, node, Node.CURRENT_NODE_COLOR, null, true);
	    }
	    // paint current node's children
	    synchronized (SearchAlgorithm.neighborNodesDrawable) {
		// Must be in synchronized block
		for (Point neighborNode : SearchAlgorithm.neighborNodesDrawable) {
		    if (node.getCoordinates().equals(neighborNode)) {
			paintAll(g2d, node, Node.NEIGHBOR_NODE_COLOR, dashed, false);
			break;
		    }
		}
	    }
	    // paint current child node
	    if (SearchAlgorithm.currentNeighborNodeDrawable != null && node.getCoordinates().equals(SearchAlgorithm.currentNeighborNodeDrawable.getCoordinates())) {
		paintAll(g2d, node, Node.CURRENT_NEIGHBOR_NODE_COLOR, dashedThick, false);
	    }
	    // paint solution path nodes
	    synchronized (SearchAlgorithm.solutionPathNodesDrawable) {
		for (Node pathNode : SearchAlgorithm.solutionPathNodesDrawable) {
		    if (!pathNode.equals(SearchAlgorithm.startNodeDrawable) && node.getID().equals(pathNode.getID())) {
			paintNode(g2d, node, Color.GREEN);
			paintNodeID(g2d, node);
			if (!scrolledToCurrentNode2) {
			    scrollToCurrentNode(node);
			    scrolledToCurrentNode2 = true;
			}
			break;
		    }
		}
	    }
	}

	private void paintParentNodesEdge(Graphics2D g2d, Node node)
	{
	    // set proper color for edges which will be painted first
	    if (showNodeLocation || showHeuristicFunction) {
		g2d.setPaint(Color.LIGHT_GRAY);
	    } else {
		g2d.setPaint(Color.DARK_GRAY);
	    }
	    if (node.getParentEdgeDrawCoordinates() != null) {
		g2d.draw(node.getParentEdgeDrawCoordinates());
	    }
	}

	private void paintNode(Graphics2D g2d, Node node, Color color)
	{
	    g2d.setPaint(color);
	    g2d.fillRect(node.getDrawCoordinates().x, node.getDrawCoordinates().y, nodeTileSize, nodeTileSize);
	    g2d.setPaint(mazeColor);
	    g2d.drawRect(node.getDrawCoordinates().x, node.getDrawCoordinates().y, nodeTileSize, nodeTileSize);
	}

	private void paintNodeID(Graphics2D g2d, Node node)
	{
	    String nodeID = node.getID();
	    if (nodeID == null || nodeID.isEmpty()) {
		return;
	    }
	    g2d.setPaint(Color.BLACK);
	    int id = Integer.parseInt(nodeID);
	    if (id < 1000) {
		g2d.setFont(font1);
	    } else {
		g2d.setFont(font2);
	    }
	    FontMetrics metrics = g2d.getFontMetrics(g2d.getFont());
	    int textWidth = metrics.stringWidth(nodeID);
	    int xDrawLocation = node.getDrawCoordinates().x - (textWidth - nodeTileSize) / 2;
	    int yDrawLocation = node.getDrawCoordinates().y - gapBetweenNodeAndStringLabel + 26;
	    g2d.drawString(nodeID, xDrawLocation, yDrawLocation);
	}

	private void paintNodeLocationAndHeuristicFunctions(Graphics2D g2d, Node node)
	{
	    g2d.setFont(font3);
	    FontMetrics metrics = g2d.getFontMetrics(font3);
	    if (showNodeLocation) {
		g2d.setPaint(mazeColor);
		int textWidth = metrics.stringWidth(node.getMazeGridLocation());
		int xDrawLocation = node.getDrawCoordinates().x - (textWidth - nodeTileSize) / 2;
		int yDrawLocation;
		if (heuristicFunctionCheckBox.isSelected()) {
		    if (EducationalModeController.algorithmIndex == SearchAlgorithm.BS || EducationalModeController.algorithmIndex == SearchAlgorithm.A_STAR) {
			yDrawLocation = node.getDrawCoordinates().y - 4 * gapBetweenNodeAndStringLabel - 3;
		    } else {
			yDrawLocation = node.getDrawCoordinates().y - 2 * gapBetweenNodeAndStringLabel - 1;
		    }
		} else {
		    yDrawLocation = node.getDrawCoordinates().y - gapBetweenNodeAndStringLabel;
		}
		g2d.drawString(node.getMazeGridLocation(), xDrawLocation, yDrawLocation);
	    }
	    if (showHeuristicFunction) {
		DecimalFormat df = new DecimalFormat("0.000");
		String fCostText = "f:" + df.format(node.getFCost());
		String gCostText = "g:" + df.format(node.getGCost());
		String hCostText = "h:" + df.format(node.getHCost());
		int textWidth = metrics.stringWidth(fCostText);
		int textWidth2 = metrics.stringWidth(hCostText);
		int xDrawLocation = node.getDrawCoordinates().x - (textWidth - nodeTileSize) / 2;
		int xDrawLocation2 = node.getDrawCoordinates().x - (textWidth2 - nodeTileSize) / 2;
		g2d.setPaint(F_COST_COLOR);
		if (EducationalModeController.algorithmIndex == SearchAlgorithm.BS || EducationalModeController.algorithmIndex == SearchAlgorithm.A_STAR) {
		    g2d.drawString(fCostText, xDrawLocation, node.getDrawCoordinates().y - 3 * gapBetweenNodeAndStringLabel);
		    g2d.setPaint(G_COST_COLOR);
		    g2d.drawString(gCostText, xDrawLocation, node.getDrawCoordinates().y - 2 * gapBetweenNodeAndStringLabel + 1);
		    g2d.setPaint(H_COST_COLOR);
		    g2d.drawString(hCostText, xDrawLocation, node.getDrawCoordinates().y - gapBetweenNodeAndStringLabel + 3);
		} else {
		    g2d.drawString(hCostText, xDrawLocation2, node.getDrawCoordinates().y - gapBetweenNodeAndStringLabel + 2);
		}
	    }
	}

	private void paintAll(Graphics2D g2d, Node node, Color color, BasicStroke edgeStroke, boolean paintID)
	{
	    if (edgeStroke != null) {
		g2d.setStroke(edgeStroke);
	    }
	    // paint node's parent edge first, so node and strings will be painted above it.
	    paintParentNodesEdge(g2d, node);
	    g2d.setStroke(normal);
	    paintNode(g2d, node, color);
	    if (paintID) {
		paintNodeID(g2d, node);
	    }
	    paintNodeLocationAndHeuristicFunctions(g2d, node);
	}

	private void scrollToCurrentNode(Node node)
	{
	    if (autoscrolling) {
		Point viewableCurrentNodeDrawCoordinatesScaled = new Point((int) (node.getDrawCoordinates().x * scale), (int) (node.getDrawCoordinates().y * scale));
		Dimension viewableCurrentNodeBoundsScaled = new Dimension((int) (nodeTileSize * scale), (int) (nodeTileSize * scale));
		JViewport vp = (JViewport) this.getParent();
		vp.setViewPosition(new Point());
		vp.getParent().validate();
		viewableCurrentNodeDrawCoordinatesScaled.translate(vp.getWidth() / 2, vp.getHeight() / 2);
		vp.scrollRectToVisible(new Rectangle(viewableCurrentNodeDrawCoordinatesScaled, viewableCurrentNodeBoundsScaled));
		// System.out.println("Scrolling...");
		scrolledToCurrentNode = true;
	    }
	}

	@Override
	public Dimension getPreferredSize()
	{
	    if (graphicalSearchTree != null) {
		return new Dimension((int) ((graphicalSearchTree.getMaxDrawWidth() + 100) * scale), (int) ((graphicalSearchTree.getMaxDrawHeight() + 100) * scale));
	    } else {
		return new Dimension(400, 400);
	    }
	}
    }
}
