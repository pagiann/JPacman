package com.jpacman.controller.edumode;

import static com.jpacman.view.edumode.EducationalModeRenderer.*;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import com.jpacman.Game;
import com.jpacman.GameApplication;
import com.jpacman.controller.MenuController;
import com.jpacman.input.Mouse;
import com.jpacman.model.Level;
import com.jpacman.model.Maze;
import com.jpacman.model.Pacman;
import com.jpacman.model.edumode.GraphicalSearchTree;
import com.jpacman.model.edumode.Node;
import com.jpacman.model.edumode.SearchAlgorithm;
import com.jpacman.util.PathFinder;
import com.jpacman.util.Timer;
import com.jpacman.view.TextRenderer;
import com.jpacman.view.TileRenderer;
import com.jpacman.view.edumode.PathfindingRenderer;
import com.jpacman.view.graphics.Screen;
import com.jpacman.view.graphics.ScreenPainter;
import com.jpacman.view.graphics.Sprite;

public class PathfindingController extends EducationalModeController implements Runnable
{
    public static final int NUM_OF_VARIATIONS = 2;
    public static final int NONE = -1;
    public static final int MAZE = 0;
    public static final int CUSTOM_SPACE = 1;

    public int selectedVariation = NONE;

    public static boolean pause;
    public static volatile int numberOfExpandedNodes;
    public static volatile boolean lastIteration;
    public static List<TileRenderer> obstaclesRenderers;
    private static boolean addingNewObstacle;
    private static boolean removingObstacle;

    private static int currentObstacleSprite = 0;

    static {
	graphicalSearchTreeController = new GraphicalSearchTreeController();
    }

    private final PathfindingRenderer pathfindingRenderer;

    private boolean newPath;
    private boolean goalReached;
    private List<Node> pathInNodes;
    private Node currentNode;
    private Node nextNode;
    private Point2D.Float nextPoint;
    private Timer nextExpandedNodeTimer;
    private Thread algorithmThread;

    public PathfindingController(TextRenderer textRenderer)
    {
	super();
	PathfindingRenderer.selectedAlgorithmText.setTextMessage("=> " + PathfindingRenderer.algorithms[0].getText().getTextMessage());
	pathfindingRenderer = new PathfindingRenderer(textRenderer, pacman, searchAlgorithms);
	educationalModeRenderer = pathfindingRenderer;
	nextExpandedNodeTimer = new Timer();
	obstaclesRenderers = new ArrayList<TileRenderer>();
    }

    @Override
    public void update(double delta)
    {
	if (selectedVariation == NONE) {
	    checkIfAVariationIsSelected();
	} else {
	    if (!(searchAlgorithms[algorithmIndex].isRunning() || backCancelButton == cancelButton || pacman.isMoving())) {
		checkIfAlgorithmButtonIsClicked();
		if (selectedVariation == MAZE) {
		    checkIfMazeThumbnailIsSelected(null);
		}
	    }

	    if (isBackCancelButtonClicked()) {
		if (backCancelButton == backButton) {
		    clearObstacles();
		    graphicalSearchTreeController.dispose();
		    Node.defaultBackgroundColorShading = true;
		    SearchAlgorithm.useDiagonalTiles = false;
		    Game.getKeyboard().returnedToMainMenu = true;
		} else if (backCancelButton == cancelButton) {
		    if (algorithmIndex == SearchAlgorithm.IDS) {
			terminateThread();
		    }
		    searchAlgorithms[algorithmIndex].reset();
		    searchAlgorithms[algorithmIndex].resetStateFlags();
		    pacman.setMoving(false);
		    destination = null;
		    SearchAlgorithm.startNodeDrawable = new Node(pacman.getCurrentTile(), null);
		    GraphicalSearchTreeController.graphicalSearchTree = new GraphicalSearchTree();
		    pause = false;
		    pauseResumeButton = pauseButton;
		    backCancelButton = backButton;
		}
		return;
	    }

	    checkWhichButtonIsClicked();

	    if (!Mouse.isDragging()) {
		addingNewObstacle = false;
		removingObstacle = false;
	    }

	    if (!searchAlgorithms[algorithmIndex].isStarted() && Mouse.getButton() == Mouse.RIGHT_BUTTON) {
		Point clickCoordinates = Mouse.getCoordinates();
		clickCoordinates.translate(-GameApplication.X_OFFSET, -GameApplication.Y_OFFSET);
		if (clickCoordinates.x > Maze.TILE && clickCoordinates.y > Maze.TILE && clickCoordinates.x < Maze.WIDTH - Maze.TILE && clickCoordinates.y < Maze.HEIGHT - Maze.TILE) {
		    Point obstacleLocation = new Point(PathFinder.computeCurrentTile(clickCoordinates));
		    if (obstacleLocation.equals(PathFinder.computeCurrentTile(pacman.getPosition())) || obstacleLocation.equals(destination)) {
			return;
		    }

		    if (!removingObstacle && Mouse.isDragging()) {
			if (routeTiles.contains(obstacleLocation)) {
			    routeTiles.remove(obstacleLocation);
			    addObstacle(obstacleLocation);
			    addingNewObstacle = true;
			}
		    }

		    if (!addingNewObstacle && Mouse.isDragging()) {
			if (!routeTiles.contains(obstacleLocation)) {
			    routeTiles.add(obstacleLocation);
			    removeObstacle(obstacleLocation);
			    removingObstacle = true;
			}
		    }
		}
	    }

	    if (!searchAlgorithms[algorithmIndex].isStarted() && Mouse.getButton() == Mouse.LEFT_BUTTON) {
		Mouse.setButton(-1);
		Point clickCoordinates = Mouse.getCoordinates();
		clickCoordinates.translate(-GameApplication.X_OFFSET, -GameApplication.Y_OFFSET);
		if (clickCoordinates.x > 0 && clickCoordinates.y > 0 && clickCoordinates.x < Maze.WIDTH && clickCoordinates.y < Maze.HEIGHT) {
		    start = new Point(PathFinder.computeCurrentTile(pacman.getPosition()));
		    destination = new Point(PathFinder.computeCurrentTile(clickCoordinates));
		    if (routeTiles.contains(destination) && !start.equals(destination)) {
			searchAlgorithms[algorithmIndex].setStarted(true);
			searchAlgorithms[algorithmIndex].setRunning(true);
			numberOfExpandedNodes = 0;
			backCancelButton = cancelButton;

			if (algorithmIndex != SearchAlgorithm.IDS) {

			    if (searchAlgorithms[algorithmIndex].findPath(routeTiles, start, destination) == null) {
				new Thread(() -> {
				    showSolutionNotFoundWindow();
				}).start();

				SearchAlgorithm.goalNodeDrawable = null;
				reset();
				return;
			    }

			    pathInNodes = Collections.synchronizedList(searchAlgorithms[algorithmIndex].getPathInNodes());

			    searchAlgorithms[algorithmIndex].setRunning(false);
			    searchAlgorithms[algorithmIndex].setFinished(true);

			    currentNode = pathInNodes.remove(0);
			    nextNode = pathInNodes.get(0);

			    initPacman();

			    newPath = true;

			    SearchAlgorithm.currentNodeDrawable = currentNode;
			    SearchAlgorithm.solutionPathNodesDrawable.add(currentNode);
			    // SearchAlgorithm.solutionPathPointsDrawable.remove(currentNode.getCoordinates());
			    GraphicalSearchTreeController.scrolledToCurrentNode = false;

			}
		    }
		}
	    } else if (searchAlgorithms[algorithmIndex].isRunning()) {
		// stuff to do while algorithm is running
		if (algorithmIndex == SearchAlgorithm.IDS) {
		    if (algorithmThread == null) {
			algorithmThread = new Thread(this, "Algorithm");
			algorithmThread.start();
		    }
		}
	    } else if (searchAlgorithms[algorithmIndex].isFinished()) {
		if (newPath) {
		    graphicalSearchTreeController.getContentPane().getComponent(1).revalidate();
		    graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
		    nextExpandedNodeTimer.reset();
		    newPath = false;
		}

		if (pause) {
		    return;
		}

		if (pacman.isMoving()) {
		    pacman.addDelta((pacman.getMovingSpeed() / GameApplication.UPDATES_PER_SECOND) * delta);
		    if (pacman.getDelta() >= 1) {
			movePacmanToNextTile();
			pathfindingRenderer.setTilesTraveled(tilesTraveled);
			pacman.decreaseDeltaByOne();
		    }
		} else {
		    nextExpandedNodeTimer.countTimeInMillisecondsPrecise();
		    if (nextExpandedNodeTimer.getTimeElapsedInMilliseconds() == nodeExpansionSpeeds[nodeExpansionSpeedIndex]) {
			if (numberOfExpandedNodes < SearchAlgorithm.expandedNodesDrawable.size()) {
			    numberOfExpandedNodes++;
			}
			nextExpandedNodeTimer.reset();
			if (!lastIteration) {
			    lastIteration = true;
			}
		    }
		    if (numberOfExpandedNodes == SearchAlgorithm.expandedNodesDrawable.size()) {
			pacman.setMoving(true);
			goalReached = false;
		    }
		}
	    }
	}
    }

    private void checkIfAVariationIsSelected()
    {
	for (int i = 0; i < PathfindingRenderer.modes.length; i++) {
	    if (PathfindingRenderer.modes[i].getBounds().contains(Mouse.getCoordinates())) {
		PathfindingRenderer.modes[i].setMouseHovered(true);
		if (Mouse.getButton() == Mouse.LEFT_BUTTON) {
		    Mouse.setButton(-1);
		    // if "Back" menu item was clicked
		    if (i == PathfindingRenderer.modes.length - 1) {
			Game.getKeyboard().returnedToMainMenu = true;
		    } else {
			PathfindingRenderer.modes[i].setClicked(true);
			selectedVariation = i;
			setDataFromSelectedVariation();
		    }
		}
	    } else {
		PathfindingRenderer.modes[i].setMouseHovered(false);
	    }
	}
    }

    private void setDataFromSelectedVariation()
    {
	switch (selectedVariation) {
	    case CUSTOM_SPACE:
		Game.setActiveSubMode(Game.SubMode.CUSTOM_SPACE_PATHFINDING);
		angle = 2;
		break;
	    case MAZE:
		Game.setActiveSubMode(Game.SubMode.MAZE_PATHFINDING);
		mazeThumbnailsBounds = pathfindingRenderer.getMazeThumbnailsBounds();
		break;
	    default:
		Game.setActiveSubMode(Game.SubMode.PATHFINDING_MENU);
	}
	// initialize level data
	levelController.setLevel(new Level(0));
	mazeRenderer.setMaze(levelController.getLevel().getMaze());
	routeTiles = levelController.getLevel().getMaze().getRouteTiles();
	levelController.initializeLevel(pacman);
	if (selectedVariation == CUSTOM_SPACE) {
	    pacman.getPosition().translate(Maze.HALF_TILE, 0);
	    pacman.setStartingPosition(pacman.getPosition());
	    pacman.setPresicePosition(new Point.Float(pacman.getStartingPosition().x, pacman.getStartingPosition().y));
	}
	pacman.setMovingSpeed(pacmanSpeeds[pacmanSpeedIndex]);
	pacman.setAnimationSpeed(Pacman.NORMAL_ANIMATION_SPEED);
	SearchAlgorithm.startNodeDrawable = new Node(pacman.getCurrentTile(), null);
    }

    private void checkWhichButtonIsClicked()
    {
	if (MenuController.isButtonClicked(pauseResumeButton)) {
	    pause = pause ? false : true;
	    pauseResumeButton = pauseResumeButton == pauseButton ? resumeButton : pauseButton;
	}

	if (selectedVariation == CUSTOM_SPACE) {
	    if (goalReached && MenuController.isButtonClicked(PathfindingRenderer.clearPathButton)) {
		goalReached = false;
		clearPath();
		PathfindingRenderer.clearPathButton.setFocus(false);
		PathfindingRenderer.clearPathButton.setMouseHovered(false);
	    }
	    if (MenuController.isButtonClicked(PathfindingRenderer.clearAllButton)) {
		reset();
		clearPath();
		clearObstacles();
	    }

	    if (!searchAlgorithms[algorithmIndex].isStarted() && MenuController.isButtonClicked(pathfindingRenderer.useDiagonalTiles)) {
		if (pathfindingRenderer.useDiagonalTiles.getText() == PathfindingRenderer.allowDiagonalText) {
		    pathfindingRenderer.useDiagonalTiles.setText(PathfindingRenderer.disallowDiagonalText);
		    SearchAlgorithm.useDiagonalTiles = true;
		    setDiagonalMovementObstacles();
		} else {
		    pathfindingRenderer.useDiagonalTiles.setText(PathfindingRenderer.allowDiagonalText);
		    SearchAlgorithm.useDiagonalTiles = false;
		    setNonDiagonalMovementObstacles();
		}
	    }
	} else {
	    if (MenuController.isButtonClicked(pathfindingRenderer.showSearchTree)) {
		graphicalSearchTreeController.setVisible(true);
	    }
	}

	if (MenuController.isButtonClicked(pathfindingRenderer.toggleGrid)) {
	    renderGrid = renderGrid ? false : true;
	}

	checkIfExpandedNodesColorButtonIsClicked(pathfindingRenderer);

	checkIfNodeExpansionSpeedButtonsIsClicked(pathfindingRenderer, nextExpandedNodeTimer);

	checkIfPacmanSpeedButtonsIsClicked(pathfindingRenderer);

	if (algorithmIndex == SearchAlgorithm.IDS) {
	    checkIfIDSAlgorithmParametersAreClicked(pathfindingRenderer);
	} else if (algorithmIndex == SearchAlgorithm.BS && backCancelButton != cancelButton) {
	    checkIfBSAlgorithmParameterIsClicked(pathfindingRenderer);
	}
    }

    private void movePacmanToNextTile()
    {
	int animationCounter = pacman.getAnimationCounter();
	pacman.setAnimationCounter((animationCounter < 10000) ? animationCounter + 1 : 0);

	if (selectedVariation == CUSTOM_SPACE) {
	    Point pacmanCurrentPresicePosition = new Point((int) Math.rint(pacman.getPresicePosition().x), (int) Math.rint(pacman.getPresicePosition().y));

	    if (pacmanCurrentPresicePosition.equals(destination)) {
		goalReached = true;
		start = new Point();
		pacman.setCurrentTile(pacman.getNextTile());
		tilesTraveled++;
		reset();
		return;
	    }

	    if (pacmanCurrentPresicePosition.equals(pacman.getNextTile())) {
		currentNode = pathInNodes.remove(0);
		pacman.setCurrentTile(pacman.getNextTile());
		if (pathInNodes.size() > 0) {
		    pacman.setNextTile(pathInNodes.get(0).getCoordinates());
		    nextPoint = new Point2D.Float(pathInNodes.get(0).getCoordinates().x, pathInNodes.get(0).getCoordinates().y);
		}
		computeAngle(nextPoint);
		tilesTraveled++;
		SearchAlgorithm.currentNodeDrawable = currentNode;
		// SearchAlgorithm.solutionPathNodesDrawable.add(currentNode);
		// SearchAlgorithm.solutionPathPointsDrawable.remove(currentNode.getCoordinates());
	    }

	    double newAngle = Math.atan2(nextPoint.y - pacman.getPresicePosition().y, nextPoint.x - pacman.getPresicePosition().x);
	    pacman.getPresicePosition().x += Math.cos(newAngle);
	    pacman.getPresicePosition().y += Math.sin(newAngle);
	    pacman.setPosition(new Point((int) Math.rint(pacman.getPresicePosition().x), (int) Math.rint(pacman.getPresicePosition().y)));
	} else {
	    if (pacman.getPosition().equals(destination)) {
		goalReached = true;
		tilesTraveled++;
		reset();
		return;
	    }

	    if (pacman.getPosition().equals(pacman.getNextTile())) {
		currentNode = pathInNodes.remove(0);
		pacman.setCurrentTile(pacman.getNextTile());
		if (pathInNodes.size() > 0) {
		    pacman.setNextTile(pathInNodes.get(0).getCoordinates());
		}
		pacman.computeNewDirection();
		tilesTraveled++;
		SearchAlgorithm.currentNodeDrawable = currentNode;
		SearchAlgorithm.solutionPathNodesDrawable.add(currentNode);
		GraphicalSearchTreeController.scrolledToCurrentNode = false;
		graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
	    }

	    pacman.move(pacman.getDirection());
	}
    }

    private void clearPath()
    {
	SearchAlgorithm.goalNodeDrawable = null;
	SearchAlgorithm.expandedNodesDrawable.clear();
	SearchAlgorithm.solutionPathPointsDrawable.clear();
	SearchAlgorithm.startNodeDrawable.setCoordinates(PathFinder.computeCurrentTile(pacman.getPosition()));
    }

    private void addObstacle(Point obstacleLocation)
    {
	if (SearchAlgorithm.useDiagonalTiles) {
	    obstaclesRenderers.add(new TileRenderer(TileRenderer.diagonalMovementObstacles[currentObstacleSprite % 6], obstacleLocation, Sprite.HALF_SPRITE_SIZE, Sprite.HALF_SPRITE_SIZE));
	    if (++currentObstacleSprite == 6) {
		currentObstacleSprite = 0;
	    }
	} else {
	    obstaclesRenderers.add(new TileRenderer(TileRenderer.nonDiagonalMovementObstacle, obstacleLocation, Sprite.HALF_SPRITE_SIZE, Sprite.HALF_SPRITE_SIZE));
	}

	synchronized (SearchAlgorithm.expandedNodesDrawable) {
	    Iterator<Node> it = SearchAlgorithm.expandedNodesDrawable.iterator();
	    while (it.hasNext()) {
		if (it.next().getCoordinates().equals(obstacleLocation)) {
		    it.remove();
		}
	    }
	}
	if (SearchAlgorithm.startNodeDrawable != null && obstacleLocation.equals(SearchAlgorithm.startNodeDrawable.getCoordinates())) {
	    SearchAlgorithm.goalNodeDrawable = null;
	    SearchAlgorithm.startNodeDrawable.setCoordinates(pacman.getCurrentTile());
	}
    }

    private void removeObstacle(Point obstacleLocation)
    {
	Iterator<TileRenderer> it = obstaclesRenderers.iterator();
	while (it.hasNext()) {
	    if (it.next().getLocation().equals(obstacleLocation)) {
		it.remove();
		break;
	    }
	}
    }

    private void clearObstacles()
    {
	Iterator<TileRenderer> it = obstaclesRenderers.iterator();
	while (it.hasNext()) {
	    routeTiles.add(it.next().getLocation());
	    it.remove();
	}
    }

    private void setNonDiagonalMovementObstacles()
    {
	List<TileRenderer> nonDiagonalMovementObstaclesRenderers = new ArrayList<TileRenderer>();
	Iterator<TileRenderer> it = obstaclesRenderers.iterator();
	while (it.hasNext()) {
	    Point obstacleLocation = it.next().getLocation();
	    it.remove();
	    nonDiagonalMovementObstaclesRenderers.add(new TileRenderer(TileRenderer.nonDiagonalMovementObstacle, obstacleLocation, Sprite.HALF_SPRITE_SIZE, Sprite.HALF_SPRITE_SIZE));
	}
	obstaclesRenderers = nonDiagonalMovementObstaclesRenderers;
    }

    private void setDiagonalMovementObstacles()
    {
	List<TileRenderer> diagonalMovementObstaclesRenderers = new ArrayList<TileRenderer>();
	Iterator<TileRenderer> it = obstaclesRenderers.iterator();
	while (it.hasNext()) {
	    Point obstacleLocation = it.next().getLocation();
	    it.remove();
	    diagonalMovementObstaclesRenderers.add(new TileRenderer(TileRenderer.diagonalMovementObstacles[currentObstacleSprite % 6], obstacleLocation, Sprite.HALF_SPRITE_SIZE, Sprite.HALF_SPRITE_SIZE));
	    if (++currentObstacleSprite == 6) {
		currentObstacleSprite = 0;
	    }
	}
	obstaclesRenderers = diagonalMovementObstaclesRenderers;
    }

    private void reset()
    {
	if (selectedVariation != CUSTOM_SPACE) {
	    if (pathInNodes != null) {
		SearchAlgorithm.currentNodeDrawable = pathInNodes.get(0);
		SearchAlgorithm.solutionPathNodesDrawable.add(pathInNodes.get(0));
	    }
	    GraphicalSearchTreeController.scrolledToCurrentNode = false;
	    graphicalSearchTreeController.getContentPane().getComponent(1).repaint();
	}
	searchAlgorithms[algorithmIndex].setStarted(false);
	searchAlgorithms[algorithmIndex].setRunning(false);
	searchAlgorithms[algorithmIndex].setFinished(false);
	pacman.setMoving(false);
	destination = null;
	backCancelButton = backButton;
    }

    private void initPacman()
    {
	pacman.setMovingSpeed(pacmanSpeeds[pacmanSpeedIndex]);
	pacman.setAnimationSpeed(Pacman.NORMAL_ANIMATION_SPEED);
	pacman.setAnimationCounter(0);
	if (selectedVariation == CUSTOM_SPACE) {
	    pacman.setPresicePosition(new Point.Float(pacman.getPosition().x, pacman.getPosition().y));
	    pacman.setNextTile(nextNode.getCoordinates());
	    nextPoint = new Point2D.Float(nextNode.getCoordinates().x, nextNode.getCoordinates().y);
	    computeAngle(nextPoint);
	} else {
	    pacman.setCurrentTile(currentNode.getCoordinates());
	    pacman.setNextTile(nextNode.getCoordinates());
	    pacman.computeNewDirection();
	}
    }

    @Override
    public void run()
    {
	lastIteration = false;
	numberOfExpandedNodes = -1;
	ScreenPainter.mazeColor = levelController.getLevel().getMaze().getColor();
	GraphicalSearchTreeController.graphicalSearchTree = new GraphicalSearchTree();

	List<Point> idsPath = searchAlgorithms[algorithmIndex].findPath(routeTiles, start, destination);
	if (idsPath != null) {
	    pathInNodes = searchAlgorithms[algorithmIndex].getPathInNodes();

	    searchAlgorithms[algorithmIndex].setRunning(false);
	    searchAlgorithms[algorithmIndex].setFinished(true);

	    currentNode = pathInNodes.remove(0);
	    nextNode = pathInNodes.get(0);

	    initPacman();

	    newPath = true;

	    SearchAlgorithm.currentNodeDrawable = currentNode;
	    SearchAlgorithm.solutionPathNodesDrawable.add(currentNode);
	    GraphicalSearchTreeController.scrolledToCurrentNode = false;
	}

	algorithmThread = null;
    }

    private void terminateThread()
    {
	searchAlgorithms[algorithmIndex].setCanceled(true);
	if (algorithmThread != null && algorithmThread.isAlive()) {
	    algorithmThread.interrupt();
	    algorithmThread = null;
	}
    }

    private void showSolutionNotFoundWindow()
    {
	JOptionPane pane = new JOptionPane("Algorithm couldn't find a solution!", JOptionPane.INFORMATION_MESSAGE);
	JDialog dialog = pane.createDialog(null, "Information");
	dialog.setModal(true);
	dialog.setVisible(true);
    }

    public void renderAll(Screen screen)
    {
	pathfindingRenderer.render(screen);
	renderHoveredMazeTileLocation = searchAlgorithms[algorithmIndex].isStarted() ? false : true;

	if (selectedVariation != NONE) {
	    mazeRenderer.render(screen);
	    for (TileRenderer obstacleRenderer : obstaclesRenderers) {
		obstacleRenderer.render(screen);
	    }
	    screen.renderScreenPainterVisuals(renderGrid, renderHoveredMazeTileLocation);
	    if (!renderGrid) {
		pacmanController.renderPacman(screen);
	    }
	}
    }
}
