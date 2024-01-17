package jpacman.controller.edumode;

import static jpacman.view.edumode.AIAlgorithmsStepwiseLearningRenderer.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import jpacman.Game;
import jpacman.GameApplication;
import jpacman.controller.MenuController;
import jpacman.input.Mouse;
import jpacman.model.Maze;
import jpacman.model.MovableGameObject;
import jpacman.model.edumode.AStarAlgorithm;
import jpacman.model.edumode.BeamSearchAlgorithm;
import jpacman.model.edumode.BestFirstAlgorithm;
import jpacman.model.edumode.BreadthFirstSearchAlgorithm;
import jpacman.model.edumode.DepthFirstSearchAlgorithm;
import jpacman.model.edumode.GraphicalSearchTree;
import jpacman.model.edumode.IterativeDeepeningAlgorithm;
import jpacman.model.edumode.Node;
import jpacman.model.edumode.SearchAlgorithm;
import jpacman.util.PathFinder;
import jpacman.view.TextRenderer;
import jpacman.view.edumode.AIAlgorithmsStepwiseLearningRenderer;
import jpacman.view.graphics.Screen;
import jpacman.view.graphics.ScreenPainter;

public class AIAlgorithmsStepwiseLearningController extends AIAlgorithmsLearningController implements Runnable
{
    public static final int MAX_ITERATIONS = 50;

    public static volatile boolean stepwiseExecutionMode;
    public static volatile SearchAlgorithm stepwiseExecutionAlgorithm;
    public static boolean storeAllGeneratedTreesOfIDS;

    public static int iterationSize = 1;
    private static boolean stepwiseExecutionInterrupted;
    private static boolean manualExecutionMode = true;

    private final AIAlgorithmsStepwiseLearningRenderer aiAlgorithmsStepwiseLearningRenderer;

    private boolean ghostReachedNextTile = true;

    public AIAlgorithmsStepwiseLearningController(TextRenderer textRenderer)
    {
	super(textRenderer);
	aiAlgorithmsStepwiseLearningRenderer = new AIAlgorithmsStepwiseLearningRenderer(textRenderer, pacman, searchAlgorithms);
	mazeThumbnailsBounds = aiAlgorithmsStepwiseLearningRenderer.getMazeThumbnailsBounds();
	educationalModeRenderer = aiAlgorithmsStepwiseLearningRenderer;
	ghosts[0].setMovingSpeed(MovableGameObject.MAX_MOVING_SPEED / 2);
    }

    @Override
    public void update(double delta)
    {
	if (!stepwiseExecutionMode) {
	    checkIfAlgorithmButtonIsClicked();
	}

	if (isBackCancelButtonClicked()) {
	    if (stepwiseExecutionMode) {
		stepwiseExecutionMode = false;
		stepwiseExecutionInterrupted = true;
		terminateThread();
	    }
	    searchAlgorithms[algorithmIndex].setRunning(false);
	    graphicalSearchTreeController.dispose();
	    submode = -1;
	    startResumeExecuteAlgorithmButton.setClicked(false);
	    startResumeExecuteAlgorithmButton.setText(startText);
	    Node.defaultBackgroundColorShading = true;
	    Game.getKeyboard().returnedToMainMenu = true;
	    return;
	}

	checkIfButtonIsClicked();

	if (!stepwiseExecutionMode && isMazeThumbnailSelected() && searchAlgorithms[algorithmIndex].isRunning()) {
	    searchAlgorithms[algorithmIndex].reset();
	    searchAlgorithms[algorithmIndex].resetStateFlags();
	    ScreenPainter.blinkyPath.clear();
	}

	if (!searchAlgorithms[algorithmIndex].isStarted() && startResumeExecuteAlgorithmButton.isClicked()) {
	    searchAlgorithms[algorithmIndex].setStarted(true);
	    searchAlgorithms[algorithmIndex].setRunning(true);
	    startResumeExecuteAlgorithmButton.setClicked(false);
	} else if (searchAlgorithms[algorithmIndex].isRunning()) {
	    if (stepwiseExecutionMode) {
		if (algorithmThread == null) {
		    if (algorithmIndex == SearchAlgorithm.IDS) {
			GraphicalSearchTreeController.paintableNodesOfAllTreesOfIDS.clear();
		    }
		    storeAllGeneratedTreesOfIDS = true;
		    searchAlgorithms[algorithmIndex].findPath(routeTiles, ghosts[0].getCurrentTile(), pacman.getCurrentTile());
		    storeAllGeneratedTreesOfIDS = false;

		    algorithmThread = new Thread(this, "Stepwise Algorithm");
		    algorithmThread.start();
		}
	    } else {
		pacmanMoveController.update(delta);
		tilesTraveled = pacman.getDistanceMoved() / Maze.TILE;
		aiAlgorithmsStepwiseLearningRenderer.setTilesTraveled(tilesTraveled);

		if (ghosts[0].getCurrentTile().equals(pacman.getCurrentTile())) {
		    searchAlgorithms[algorithmIndex].reset();
		    SearchAlgorithm.expandedNodesDrawable.clear();
		    ScreenPainter.blinkyPath.clear();
		    return;
		}

		// ghost reached next tile, find new path
		if (ghostReachedNextTile) {
		    ghostReachedNextTile = false;
		    if (algorithmIndex == SearchAlgorithm.IDS) {
			GraphicalSearchTreeController.paintableNodesOfAllTreesOfIDS.clear();
		    }

		    List<Point> path = searchAlgorithms[algorithmIndex].findPath(routeTiles, ghosts[0].getCurrentTile(), pacman.getCurrentTile());

		    ScreenPainter.blinkyPath = new ArrayList<Point>(path);
		    ScreenPainter.blinkyPath.add(0, ghosts[0].getPosition());

		    ghosts[0].setNextTile(path.get(0));
		    ghosts[0].computeNewDirection();
		    ghosts[0].setMoving(true);
		}

		if (ghosts[0].isMoving()) {
		    ghosts[0].addDelta((ghosts[0].getMovingSpeed() / GameApplication.UPDATES_PER_SECOND) * delta);
		    if (ghosts[0].getDelta() >= 1) { // ups capped at object's current moving speed
			moveGhostToNextTile();
			ghosts[0].decreaseDeltaByOne();
		    }
		}
	    }
	} else if (searchAlgorithms[algorithmIndex].isFinished()) {
	}
    }

    private boolean isMazeThumbnailSelected()
    {
	for (int i = 0; i < Maze.educationalModeMazes.length; i++) {
	    if (mazeThumbnailsBounds[i].getBounds().contains(Mouse.getCoordinates())) {
		aiAlgorithmsStepwiseLearningRenderer.setHoveredMazeThumbnails(i, true);
		if (Mouse.getButton() == Mouse.LEFT_BUTTON) {
		    Mouse.setButton(-1);
		    levelController.getLevel().setMaze(Maze.educationalModeMazes[i]);
		    mazeRenderer.setMaze(levelController.getLevel().getMaze());
		    routeTiles = Maze.educationalModeMazes[i].getRouteTiles();
		    levelController.initializeLevel(pacman, ghosts);
		    aiAlgorithmsStepwiseLearningRenderer.setTilesTraveled(0);
		    stepwiseExecutionMode = false;
		    startResumeExecuteAlgorithmButton.setClicked(false);
		    startResumeExecuteAlgorithmButton.setText(startText);
		    return true;
		}
	    } else {
		aiAlgorithmsStepwiseLearningRenderer.getHoveredMazeThumbnails()[i] = false;
	    }
	}

	return false;
    }

    private void checkIfButtonIsClicked()
    {
	if (MenuController.isButtonClicked(startResumeExecuteAlgorithmButton)) {
	    if (startResumeExecuteAlgorithmButton.getText().getTextMessage().equals(startText.getTextMessage())) {
		startResumeExecuteAlgorithmButton.setClicked(true);
		startResumeExecuteAlgorithmButton.setText(executeAlgorithmText);
	    } else if (startResumeExecuteAlgorithmButton.getText().getTextMessage().equals(executeAlgorithmText.getTextMessage())) {
		if (!stepwiseExecutionMode && !pacman.getCurrentTile().equals(ghosts[0].getCurrentTile())) {
		    stepwiseExecutionMode = true;
		    startResumeExecuteAlgorithmButton.setText(resumeText);
		    startResumeExecuteAlgorithmButton.setClicked(true);
		    if (manualExecutionMode) {
			step.setVisible(true);
		    }
		    graphicalSearchTreeController.setVisible(true);
		    renderGrid = true;
		}
	    } else if (startResumeExecuteAlgorithmButton.getText().getTextMessage().equals(resumeText.getTextMessage())) {
		startResumeExecuteAlgorithmButton.setClicked(true);
		startResumeExecuteAlgorithmButton.setText(executeAlgorithmText);
		terminateThread();
		reset();
		SearchAlgorithm.resetDrawableVariables();
		graphicalSearchTreeController.dispose();
		renderGrid = false;
	    }
	}

	if (stepwiseExecutionMode) {
	    if (stepwiseExecutionAlgorithm != null && !stepwiseExecutionAlgorithm.mutltipleIterations && MenuController.isButtonClicked(executionMode)) {
		if (executionMode.getText().equals(manualModeText)) {
		    executionMode.setText(automaticModeText);
		    manualExecutionMode = false;
		    stepwiseExecutionAlgorithm.manualExecution = false;
		    step.setVisible(false);
		} else {
		    executionMode.setText(manualModeText);
		    manualExecutionMode = true;
		    stepwiseExecutionAlgorithm.manualExecution = true;
		    step.setVisible(true);
		}
	    }

	    if (stepwiseExecutionAlgorithm != null && stepwiseExecutionAlgorithm.stepCompleted && step.getBounds().contains(Mouse.getCoordinates())) {
		step.setMouseHovered(true);
		if (Mouse.getButton() == Mouse.LEFT_BUTTON) {
		    Mouse.setButton(-1);
		    if (iterationSize > 1) {
			stepwiseExecutionAlgorithm.stepByStepExecution = false;
			stepwiseExecutionAlgorithm.nextStep = false;
			stepwiseExecutionAlgorithm.stepCompleted = false;
			stepwiseExecutionAlgorithm.mutltipleIterations = true;
		    } else {
			stepwiseExecutionAlgorithm.nextStep = true;
		    }
		}
	    } else {
		step.setMouseHovered(false);
	    }

	    if (MenuController.isButtonClicked(minStepSize)) {
		iterationSize = 1;
	    }
	    if (MenuController.isButtonClicked(decreaseStepSize)) {
		if (iterationSize > 1) {
		    iterationSize--;
		}
	    }
	    if (MenuController.isButtonClicked(increaseStepSize)) {
		if (iterationSize < MAX_ITERATIONS) {
		    iterationSize++;
		}
	    }
	    if (MenuController.isButtonClicked(maxStepSize)) {
		iterationSize = MAX_ITERATIONS;
	    }

	    if (MenuController.isButtonClicked(aiAlgorithmsStepwiseLearningRenderer.showSearchTree)) {
		graphicalSearchTreeController.setVisible(true);
	    }
	} else {
	    if (algorithmIndex == SearchAlgorithm.IDS) {
		checkIfIDSAlgorithmParametersAreClicked(aiAlgorithmsStepwiseLearningRenderer);
	    } else if (algorithmIndex == SearchAlgorithm.BS) {
		checkIfBSAlgorithmParameterIsClicked(aiAlgorithmsStepwiseLearningRenderer);
	    }
	}

	checkIfExpandedNodesColorButtonIsClicked(aiAlgorithmsStepwiseLearningRenderer);
	AIAlgorithmsLearningController.graphicalSearchTreeController.getContentPane().getComponent(1).repaint();

	if (MenuController.isButtonClicked(aiAlgorithmsStepwiseLearningRenderer.toggleGrid)) {
	    renderGrid = renderGrid ? false : true;
	}
    }

    private void reset()
    {
	stepwiseExecutionAlgorithm = null;
	stepwiseExecutionMode = false;
	graphicalSearchTreeController.setVisible(false);

	if (stepwiseExecutionInterrupted) {
	    stepwiseExecutionInterrupted = false;
	    startResumeExecuteAlgorithmButton.setText(startText);
	} else { // thread completed normally or resumed
	    startResumeExecuteAlgorithmButton.setText(executeAlgorithmText);
	}
	startResumeExecuteAlgorithmButton.setClicked(false);
	step.setVisible(false);
    }

    private void moveGhostToNextTile()
    {
	int animationCounter = ghosts[0].getAnimationCounter();
	ghosts[0].setAnimationCounter((animationCounter < 10000) ? animationCounter + 1 : 0);

	ghosts[0].move(ghosts[0].getDirection());
	if (ghosts[0].getPosition().equals(ghosts[0].getNextTile())) {
	    ghosts[0].setCurrentTile(PathFinder.computeCurrentTile(ghosts[0].getPosition()));
	    ghostReachedNextTile = true;
	}
    }

    @Override
    public void run()
    {
	switch (algorithmIndex) {
	    case SearchAlgorithm.DFS:
		stepwiseExecutionAlgorithm = new DepthFirstSearchAlgorithm();
		break;
	    case SearchAlgorithm.BFS:
		stepwiseExecutionAlgorithm = new BreadthFirstSearchAlgorithm();
		break;
	    case SearchAlgorithm.IDS:
		IterativeDeepeningAlgorithm ita = ((IterativeDeepeningAlgorithm) searchAlgorithms[algorithmIndex]);
		stepwiseExecutionAlgorithm = new IterativeDeepeningAlgorithm(ita.getInitialDepth(), ita.getDepthStep());
		break;
	    case SearchAlgorithm.BF:
		stepwiseExecutionAlgorithm = new BestFirstAlgorithm(((BestFirstAlgorithm) searchAlgorithms[algorithmIndex]).getHeuristicFunction());
		break;
	    case SearchAlgorithm.BS:
		BeamSearchAlgorithm bsa = ((BeamSearchAlgorithm) searchAlgorithms[algorithmIndex]);
		stepwiseExecutionAlgorithm = new BeamSearchAlgorithm(bsa.getHeuristicFunction(), bsa.getBeamWidth());
		break;
	    case SearchAlgorithm.A_STAR:
		stepwiseExecutionAlgorithm = new AStarAlgorithm(((AStarAlgorithm) searchAlgorithms[algorithmIndex]).getHeuristicFunction());
		break;
	    default:
		System.err.println("Not supported algorithm!");
	}

	stepwiseExecutionAlgorithm.stepByStepExecution = true;
	stepwiseExecutionAlgorithm.manualExecution = manualExecutionMode;
	start = ghosts[0].getCurrentTile();
	destination = pacman.getCurrentTile();

	GraphicalSearchTreeController.mazeColor = levelController.getLevel().getMaze().getColor();
	if (algorithmIndex == SearchAlgorithm.IDS) {
	    GraphicalSearchTreeController.graphicalSearchTree = new GraphicalSearchTree();
	}
	graphicalSearchTreeController.getContentPane().getComponent(1).revalidate();
	graphicalSearchTreeController.getContentPane().getComponent(1).repaint();

	stepwiseExecutionAlgorithm.findPath(routeTiles, start, destination);

	reset();
	algorithmThread = null;
    }

    private void terminateThread()
    {
	if (algorithmThread != null && algorithmThread.isAlive()) {
	    stepwiseExecutionAlgorithm.stepByStepExecution = false;
	    algorithmThread.interrupt();
	    algorithmThread = null;
	}
    }

    @Override
    public void renderAll(Screen screen)
    {
	mazeRenderer.render(screen);
	aiAlgorithmsStepwiseLearningRenderer.render(screen);
	renderHoveredMazeTileLocation = stepwiseExecutionMode ? true : false;
	screen.renderScreenPainterVisuals(renderGrid, renderHoveredMazeTileLocation);
	if (stepwiseExecutionMode && !renderGrid || !stepwiseExecutionMode) {
	    ghostsControllers[0].renderGhost(screen);
	    pacmanController.renderPacman(screen);
	}
    }
}
