package com.jpacman.controller.edumode;

import static com.jpacman.view.edumode.EducationalModeRenderer.*;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.jpacman.Game;
import com.jpacman.Game.SubMode;
import com.jpacman.controller.Controller;
import com.jpacman.controller.LevelController;
import com.jpacman.controller.MenuController;
import com.jpacman.controller.PillController;
import com.jpacman.controller.pacman.PacmanController;
import com.jpacman.input.Mouse;
import com.jpacman.model.Level;
import com.jpacman.model.Maze;
import com.jpacman.model.Pacman;
import com.jpacman.model.UIButton;
import com.jpacman.model.edumode.AStarAlgorithm;
import com.jpacman.model.edumode.BeamSearchAlgorithm;
import com.jpacman.model.edumode.BestFirstAlgorithm;
import com.jpacman.model.edumode.BreadthFirstSearchAlgorithm;
import com.jpacman.model.edumode.DepthFirstSearchAlgorithm;
import com.jpacman.model.edumode.GraphicalSearchTree;
import com.jpacman.model.edumode.IterativeDeepeningAlgorithm;
import com.jpacman.model.edumode.Node;
import com.jpacman.model.edumode.SearchAlgorithm;
import com.jpacman.util.Timer;
import com.jpacman.view.MazeRenderer;
import com.jpacman.view.PacmanRenderer;
import com.jpacman.view.edumode.EducationalModeRenderer;
import com.jpacman.view.graphics.ScreenPainter;
import com.jpacman.view.graphics.Sprite;

public class EducationalModeController implements Controller
{
    public static GraphicalSearchTreeController graphicalSearchTreeController;
    public static int algorithmIndex;
    public static double angle = Math.PI;

    protected static int nodeExpansionMaxSpeed = 3200;
    protected static int nodeExpansionSpeed;

    public static final int[] nodeExpansionSpeeds = new int[] { 10, 50, 100, 200, 500, 1000, 2000 };
    public static final double[] pacmanSpeeds = new double[] { 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0, 110.0, 120.0, 130.0, 140.0, 150.0 };
    protected int nodeExpansionSpeedIndex = 3;
    protected int pacmanSpeedIndex = 7;

    protected final DepthFirstSearchAlgorithm depthFirstSearchAlgorithm;
    protected final BreadthFirstSearchAlgorithm breadthFirstSearchAlgorithm;
    protected final IterativeDeepeningAlgorithm iterativeDeepeningAlgorithm;
    protected final BestFirstAlgorithm bestFirstAlgorithm;
    protected final BeamSearchAlgorithm beamSearchAlgorithm;
    protected final AStarAlgorithm aStarAlgorithm;
    protected final SearchAlgorithm[] searchAlgorithms;

    protected final Pacman pacman;
    protected final PacmanController pacmanController;

    protected EducationalModeRenderer educationalModeRenderer;
    protected LevelController levelController;
    protected MazeRenderer mazeRenderer;
    protected Rectangle[] mazeThumbnailsBounds;
    protected ArrayList<Point> routeTiles;

    protected Point start;
    protected Point destination;
    protected int tilesTraveled;

    protected boolean renderGrid;
    protected boolean renderHoveredMazeTileLocation;

    public EducationalModeController()
    {
	depthFirstSearchAlgorithm = new DepthFirstSearchAlgorithm();
	breadthFirstSearchAlgorithm = new BreadthFirstSearchAlgorithm();
	iterativeDeepeningAlgorithm = new IterativeDeepeningAlgorithm();
	beamSearchAlgorithm = new BeamSearchAlgorithm();
	bestFirstAlgorithm = new BestFirstAlgorithm();
	aStarAlgorithm = new AStarAlgorithm();

	searchAlgorithms = new SearchAlgorithm[6];
	searchAlgorithms[0] = depthFirstSearchAlgorithm;
	searchAlgorithms[1] = breadthFirstSearchAlgorithm;
	searchAlgorithms[2] = iterativeDeepeningAlgorithm;
	searchAlgorithms[3] = bestFirstAlgorithm;
	searchAlgorithms[4] = beamSearchAlgorithm;
	searchAlgorithms[5] = aStarAlgorithm;

	algorithmIndex = 0;
	nodeExpansionSpeed = nodeExpansionMaxSpeed / nodeExpansionSpeeds[nodeExpansionSpeedIndex];

	pacman = new Pacman();
	pacmanController = new PacmanController(pacman, new PacmanRenderer(pacman, Sprite.HALF_SPRITE_SIZE));
	// create a default educational mode level controller
	levelController = new LevelController(new Level(0));
	mazeRenderer = new MazeRenderer(levelController.getLevel().getMaze());
	ScreenPainter.mazeColor = levelController.getLevel().getMaze().getColor();
	GraphicalSearchTreeController.mazeColor = levelController.getLevel().getMaze().getColor();
	start = null;
	destination = null;
	tilesTraveled = 0;
    }

    @Override
    public void update(double delta)
    {
    }

    protected void checkIfAlgorithmButtonIsClicked()
    {
	for (int i = 0; i < algorithms.length; i++) {
	    if (algorithms[i].getBounds().contains(Mouse.getCoordinates())) {
		if (!(i == SearchAlgorithm.BF || i == SearchAlgorithm.BS || i == SearchAlgorithm.A_STAR)) {
		    algorithms[i].setMouseHovered(true);
		}
		if (Mouse.getButton() == Mouse.LEFT_BUTTON) {
		    Mouse.setButton(-1);
		    if (!(i == SearchAlgorithm.BF || i == SearchAlgorithm.BS || i == SearchAlgorithm.A_STAR)) {
			transferAlgorithmRunningStatus(searchAlgorithms[algorithmIndex], searchAlgorithms[i]);
			algorithmIndex = i;
			selectedAlgorithmText.setTextMessage("=> " + algorithms[i].getText().getTextMessage());
			setButtonsClickedStatus(i, SearchAlgorithm.BF, SearchAlgorithm.BS, SearchAlgorithm.A_STAR, false, false);
			searchAlgorithms[algorithmIndex].reset();
			SearchAlgorithm.startNodeDrawable = new Node(pacman.getCurrentTile(), null);
			GraphicalSearchTreeController.graphicalSearchTree = new GraphicalSearchTree();
		    }
		} else if (i == SearchAlgorithm.BF) {
		    setButtonsClickedStatus(i, SearchAlgorithm.BS, SearchAlgorithm.A_STAR, SearchAlgorithm.NONE, true, false);
		} else if (i == SearchAlgorithm.BS) {
		    setButtonsClickedStatus(i, SearchAlgorithm.BF, SearchAlgorithm.A_STAR, SearchAlgorithm.NONE, true, false);
		} else if (i == SearchAlgorithm.A_STAR) {
		    setButtonsClickedStatus(i, SearchAlgorithm.BF, SearchAlgorithm.BS, SearchAlgorithm.NONE, true, false);
		}
	    } else {
		algorithms[i].setMouseHovered(false);
	    }
	}

	if (algorithms[SearchAlgorithm.BF].isClicked()) {
	    for (int i = 0; i < heuristicFunctions1.length; i++) {
		checkHeuristicButton(heuristicFunctions1[i], "=> " + BestFirstAlgorithm.NAME + " with\n   ", i, SearchAlgorithm.BF);
	    }
	} else if (algorithms[SearchAlgorithm.BS].isClicked()) {
	    for (int i = 0; i < heuristicFunctions2.length; i++) {
		checkHeuristicButton(heuristicFunctions2[i], "=> " + BeamSearchAlgorithm.NAME + " with\n   ", i, SearchAlgorithm.BS);
	    }
	} else if (algorithms[SearchAlgorithm.A_STAR].isClicked()) {
	    for (int i = 0; i < heuristicFunctions3.length; i++) {
		checkHeuristicButton(heuristicFunctions3[i], "=> " + AStarAlgorithm.NAME + " with\n   ", i, SearchAlgorithm.A_STAR);
	    }
	}
    }

    private void setButtonsClickedStatus(int activeAlgorithmIndex, int inactiveAlgorithmIndex1, int inactiveAlgorithmIndex2, //
    int inactiveAlgorithmIndex3, boolean enabledStatus, boolean selectedStatus)
    {
	algorithms[activeAlgorithmIndex].setClicked(true);
	algorithms[inactiveAlgorithmIndex1].setClicked(false);
	algorithms[inactiveAlgorithmIndex2].setClicked(false);
	if (inactiveAlgorithmIndex3 != SearchAlgorithm.NONE) {
	    algorithms[inactiveAlgorithmIndex3].setClicked(false);
	}
    }

    private void checkHeuristicButton(UIButton heuristicMenuItem, String textMessage, int i, int SearchAlgorithmIndex)
    {
	if (MenuController.isButtonClicked(heuristicMenuItem)) {
	    transferAlgorithmRunningStatus(searchAlgorithms[algorithmIndex], searchAlgorithms[SearchAlgorithmIndex]);
	    algorithmIndex = SearchAlgorithmIndex;
	    searchAlgorithms[algorithmIndex].reset();
	    SearchAlgorithm.startNodeDrawable = new Node(pacman.getCurrentTile(), null);
	    GraphicalSearchTreeController.graphicalSearchTree = new GraphicalSearchTree();
	    EducationalModeRenderer.selectedAlgorithmText.setTextMessage(textMessage + heuristicMenuItem.getText().getTextMessage());
	    if (searchAlgorithms[algorithmIndex] instanceof BestFirstAlgorithm) {
		((BestFirstAlgorithm) searchAlgorithms[algorithmIndex]).setHeuristicFunction(i);
	    } else if (searchAlgorithms[algorithmIndex] instanceof BeamSearchAlgorithm) {
		((BeamSearchAlgorithm) searchAlgorithms[algorithmIndex]).setHeuristicFunction(i);
	    } else if (searchAlgorithms[algorithmIndex] instanceof AStarAlgorithm) {
		((AStarAlgorithm) searchAlgorithms[algorithmIndex]).setHeuristicFunction(i);
	    }
	}
    }

    protected void checkIfMazeThumbnailIsSelected(PillController pillController)
    {
	for (int i = 0; i < Maze.educationalModeMazes.length; i++) {
	    if (mazeThumbnailsBounds[i].getBounds().contains(Mouse.getCoordinates())) {
		educationalModeRenderer.setHoveredMazeThumbnails(i, true);
		if (Mouse.getButton() == Mouse.LEFT_BUTTON) {
		    Mouse.setButton(-1);
		    levelController.getLevel().setMaze(Maze.educationalModeMazes[i]);
		    Maze currentMaze = levelController.getLevel().getMaze();
		    mazeRenderer.setMaze(currentMaze);
		    routeTiles = currentMaze.getRouteTiles();
		    if (pillController == null) {
			levelController.initializeLevel(pacman, currentMaze);
		    } else {
			levelController.initializeLevel(pillController, pacman, currentMaze);
		    }
		    pacman.setMovingSpeed(pacmanSpeeds[pacmanSpeedIndex]);
		    educationalModeRenderer.setTilesTraveled(0);
		    searchAlgorithms[algorithmIndex].reset();
		    searchAlgorithms[algorithmIndex].resetStateFlags();
		    if (searchAlgorithms[algorithmIndex] instanceof IterativeDeepeningAlgorithm) {
			((IterativeDeepeningAlgorithm) searchAlgorithms[algorithmIndex]).setCurrentDepth(0);
		    }
		    SearchAlgorithm.startNodeDrawable = new Node(pacman.getCurrentTile(), null);
		    ScreenPainter.mazeColor = currentMaze.getColor();
		    GraphicalSearchTreeController.mazeColor = currentMaze.getColor();
		    pauseResumeButton = pauseButton;
		}
	    } else {
		educationalModeRenderer.getHoveredMazeThumbnails()[i] = false;
	    }
	}
    }

    protected void checkIfExpandedNodesColorButtonIsClicked(EducationalModeRenderer renderer)
    {
	if (MenuController.isButtonClicked(renderer.expandedNodesColor)) {
	    if (renderer.expandedNodesColor.getText() == EducationalModeRenderer.defaultColorText) {
		if (Game.getActiveSubMode() == SubMode.AI_ALGORITHMS_STEPWISE_LEARNING) {
		    renderer.expandedNodesColor.setText(EducationalModeRenderer.mixedColorText);
		} else {
		    renderer.expandedNodesColor.setText(EducationalModeRenderer.mazeColorText);
		}
	    } else {
		renderer.expandedNodesColor.setText(EducationalModeRenderer.defaultColorText);
	    }
	    Node.defaultBackgroundColorShading = Node.defaultBackgroundColorShading ? false : true;
	}
    }

    protected void checkIfNodeExpansionSpeedButtonsIsClicked(EducationalModeRenderer renderer, Timer timer)
    {
	if (MenuController.isButtonClicked(renderer.minNodeExpansionSpeed)) {
	    if (nodeExpansionSpeedIndex != nodeExpansionSpeeds.length - 1) {
		nodeExpansionSpeedIndex = nodeExpansionSpeeds.length - 1;
		nodeExpansionSpeed = nodeExpansionMaxSpeed / nodeExpansionSpeeds[nodeExpansionSpeedIndex];
		if (timer != null) {
		    timer.reset();
		}
	    }
	}

	if (MenuController.isButtonClicked(renderer.decreaseNodeExpansionSpeed)) {
	    if (nodeExpansionSpeedIndex < nodeExpansionSpeeds.length - 1) {
		nodeExpansionSpeedIndex++;
		nodeExpansionSpeed = nodeExpansionMaxSpeed / nodeExpansionSpeeds[nodeExpansionSpeedIndex];
		if (timer != null) {
		    timer.reset();
		}
	    }
	}

	if (MenuController.isButtonClicked(renderer.increaseNodeExpansionSpeed)) {
	    if (nodeExpansionSpeedIndex > 0) {
		nodeExpansionSpeedIndex--;
		nodeExpansionSpeed = nodeExpansionMaxSpeed / nodeExpansionSpeeds[nodeExpansionSpeedIndex];
		if (timer != null) {
		    timer.reset();
		}
	    }
	}

	if (MenuController.isButtonClicked(renderer.maxNodeExpansionSpeed)) {
	    if (nodeExpansionSpeedIndex != 0) {
		nodeExpansionSpeedIndex = 0;
		nodeExpansionSpeed = nodeExpansionMaxSpeed / nodeExpansionSpeeds[nodeExpansionSpeedIndex];
		if (timer != null) {
		    timer.reset();
		}
	    }
	}
    }

    protected void checkIfPacmanSpeedButtonsIsClicked(EducationalModeRenderer renderer)
    {
	if (MenuController.isButtonClicked(renderer.minPacmanSpeed)) {
	    if (pacmanSpeedIndex != 0) {
		pacmanSpeedIndex = 0;
		pacman.setMovingSpeed(pacmanSpeeds[pacmanSpeedIndex]);
	    }
	}

	if (MenuController.isButtonClicked(renderer.decreasePacmanSpeed)) {
	    if (pacmanSpeedIndex > 0) {
		pacmanSpeedIndex--;
		pacman.setMovingSpeed(pacmanSpeeds[pacmanSpeedIndex]);
	    }
	}

	if (MenuController.isButtonClicked(renderer.increasePacmanSpeed)) {
	    if (pacmanSpeedIndex < pacmanSpeeds.length - 1) {
		pacmanSpeedIndex++;
		pacman.setMovingSpeed(pacmanSpeeds[pacmanSpeedIndex]);
	    }
	}

	if (MenuController.isButtonClicked(renderer.maxPacmanSpeed)) {
	    if (pacmanSpeedIndex != pacmanSpeeds.length - 1) {
		pacmanSpeedIndex = pacmanSpeeds.length - 1;
		pacman.setMovingSpeed(pacmanSpeeds[pacmanSpeedIndex]);
	    }
	}
    }

    protected void checkIfIDSAlgorithmParametersAreClicked(EducationalModeRenderer renderer)
    {
	if (!searchAlgorithms[algorithmIndex].isRunning() && MenuController.isButtonClicked(renderer.visualizeIterations)) {
	    if (renderer.visualizeIterations.getText() == EducationalModeRenderer.visualizeIterationsText) {
		renderer.visualizeIterations.setText(EducationalModeRenderer.dontVisualizeIterationsText);
		((IterativeDeepeningAlgorithm) searchAlgorithms[algorithmIndex]).setVisualizeIterations(true);
	    } else {
		renderer.visualizeIterations.setText(EducationalModeRenderer.visualizeIterationsText);
		((IterativeDeepeningAlgorithm) searchAlgorithms[algorithmIndex]).setVisualizeIterations(false);
	    }
	}

	if (!searchAlgorithms[algorithmIndex].isRunning() && MenuController.isButtonClicked(renderer.decreaseInitialDepth)) {
	    int currentInitialDepth = ((IterativeDeepeningAlgorithm) searchAlgorithms[algorithmIndex]).getInitialDepth();
	    if (currentInitialDepth > 0) {
		((IterativeDeepeningAlgorithm) searchAlgorithms[algorithmIndex]).setInitialDepth(currentInitialDepth - 1);
	    }
	}

	if (!searchAlgorithms[algorithmIndex].isRunning() && MenuController.isButtonClicked(renderer.increaseInitialDepth)) {
	    int currentInitialDepth = ((IterativeDeepeningAlgorithm) searchAlgorithms[algorithmIndex]).getInitialDepth();
	    if (currentInitialDepth < IterativeDeepeningAlgorithm.MAX_INITIAL_DEPTH) {
		((IterativeDeepeningAlgorithm) searchAlgorithms[algorithmIndex]).setInitialDepth(currentInitialDepth + 1);
	    }
	}

	if (MenuController.isButtonClicked(renderer.decreaseDepthStep)) {
	    int currentDepthStep = ((IterativeDeepeningAlgorithm) searchAlgorithms[algorithmIndex]).getDepthStep();
	    if (currentDepthStep > 1) {
		((IterativeDeepeningAlgorithm) searchAlgorithms[algorithmIndex]).setDepthStep(currentDepthStep - 1);
	    }
	}

	if (MenuController.isButtonClicked(renderer.increaseDepthStep)) {
	    int currentDepthStep = ((IterativeDeepeningAlgorithm) searchAlgorithms[algorithmIndex]).getDepthStep();
	    if (currentDepthStep < IterativeDeepeningAlgorithm.MAX_DEPTH_STEP) {
		((IterativeDeepeningAlgorithm) searchAlgorithms[algorithmIndex]).setDepthStep(currentDepthStep + 1);
	    }
	}
    }

    protected void checkIfBSAlgorithmParameterIsClicked(EducationalModeRenderer renderer)
    {
	if (MenuController.isButtonClicked(renderer.decreaseBeamWidth)) {
	    int currentBeamWidth = ((BeamSearchAlgorithm) searchAlgorithms[algorithmIndex]).getBeamWidth();
	    if (currentBeamWidth > 1) {
		((BeamSearchAlgorithm) searchAlgorithms[algorithmIndex]).setBeamWidth(currentBeamWidth - 1);
	    }
	}

	if (MenuController.isButtonClicked(renderer.increaseBeamWidth)) {
	    int currentBeamWidth = ((BeamSearchAlgorithm) searchAlgorithms[algorithmIndex]).getBeamWidth();
	    if (currentBeamWidth < BeamSearchAlgorithm.MAX_BEAM_WIDTH) {
		((BeamSearchAlgorithm) searchAlgorithms[algorithmIndex]).setBeamWidth(currentBeamWidth + 1);
	    }
	}
    }

    protected boolean isBackCancelButtonClicked()
    {
	if (MenuController.isButtonClicked(EducationalModeRenderer.backCancelButton)) {
	    Mouse.setButton(-1);
	    algorithms[SearchAlgorithm.BF].setClicked(false);
	    algorithms[SearchAlgorithm.BS].setClicked(false);
	    algorithms[SearchAlgorithm.A_STAR].setClicked(false);
	    return true;
	}

	return false;
    }

    protected boolean isResetButtonClicked(boolean algorithmFinished)
    {
	if (MenuController.isButtonClicked(EducationalModeRenderer.resetButton)) {
	    if (algorithmFinished) {
		return true;
	    }
	}

	return false;
    }

    protected void movePacman(List<Point> path)
    {
	int animationCounter = pacman.getAnimationCounter();
	pacman.setAnimationCounter((animationCounter < 10000) ? animationCounter + 1 : 0);

	if (pacman.getPosition().equals(pacman.getNextTile())) {
	    pacman.setNextTile(path.remove(0));
	    pacman.computeNewDirection();
	    tilesTraveled++;
	}

	pacman.move(pacman.getDirection());
    }

    private void transferAlgorithmRunningStatus(SearchAlgorithm currentSearchAlgorithm, SearchAlgorithm newSearchAlgorithm)
    {
	if (currentSearchAlgorithm.isRunning()) {
	    newSearchAlgorithm.setStarted(true);
	    newSearchAlgorithm.setRunning(true);
	}
    }

    protected void computeAngle(Point2D.Float nextPointToMove)
    {
	double dx = nextPointToMove.x - pacman.getPresicePosition().x;
	double dy = nextPointToMove.y - pacman.getPresicePosition().y;
	angle = Math.atan(dy / dx);

	if (dx > 0 && dy > 0) {
	    angle = Math.PI - angle;
	} else if ((dx < 0 && dy < 0) || (dx < 0 && dy > 0)) {
	    angle = -angle;
	} else if (dx > 0 && dy < 0) {
	    angle = Math.PI + Math.abs(angle);
	} else if (dx > 0 && dy == 0) {
	    angle -= Math.PI;
	}

	// System.out.println("angle (in radians) - 2 = " + angle);
	// System.out.println("angle (in degrees) - 2 = " + Math.toDegrees(angle));
    }

    public static int getAlgorithmIndex()
    {
	return algorithmIndex;
    }

    public static int getNodeExpansionSpeed()
    {
	return nodeExpansionSpeed;
    }

    public PacmanController getPacmanController()
    {
	return pacmanController;
    }

    public LevelController getLevelController()
    {
	return levelController;
    }

    public void setLevelController(LevelController levelController)
    {
	this.levelController = levelController;
    }
}
