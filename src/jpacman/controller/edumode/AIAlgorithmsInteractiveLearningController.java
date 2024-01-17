package jpacman.controller.edumode;

import static jpacman.view.edumode.EducationalModeRenderer.*;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

import jpacman.Game;
import jpacman.GameApplication;
import jpacman.controller.LevelController;
import jpacman.controller.MenuController;
import jpacman.input.Mouse;
import jpacman.model.Level;
import jpacman.model.Maze;
import jpacman.model.Pacman;
import jpacman.model.edumode.AStarAlgorithm;
import jpacman.model.edumode.BeamSearchAlgorithm;
import jpacman.model.edumode.BestFirstAlgorithm;
import jpacman.model.edumode.BreadthFirstSearchAlgorithm;
import jpacman.model.edumode.DepthFirstSearchAlgorithm;
import jpacman.model.edumode.IterativeDeepeningAlgorithm;
import jpacman.model.edumode.Node;
import jpacman.model.edumode.SearchAlgorithm;
import jpacman.util.PathFinder;
import jpacman.view.MazeRenderer;
import jpacman.view.TextRenderer;
import jpacman.view.edumode.AIAlgorithmsInteractiveLearningRenderer;
import jpacman.view.graphics.Screen;
import jpacman.view.graphics.ScreenPainter;

public class AIAlgorithmsInteractiveLearningController extends AIAlgorithmsLearningController implements Runnable
{
    public static volatile boolean userInteractionWindowShowed;
    public static volatile SearchAlgorithm interactiveExecutionAlgorithm;
    public static volatile Node nextNodeToBeExpanded;
    public static volatile boolean mediumDifficulty;
    public static volatile boolean hardDifficulty;

    private static boolean interactiveExecutionInterrupted;
    public static Random randomNumberOfNodesToBeExpanded;

    private static final String info = "In this mode you test your knowledge of the AI search algorithms, in the following way:\n" + //
    "After the algorithm has started, the start node will be expanded from the search frontier and the\n" + //
    "algorithm will pause, waiting for you to click at the node you think will expand next. The same thing\n" + //
    "will keep on happening (after the expansion of a random number [1-10] of nodes each time) until the\n" + //
    "goal node is reached. Your goal is, to click at as many correct nodes as possible.\n\n" + //
    "Also keep in mind that the order in which the neighbor nodes are being visited (each time) is:\n" + //
    "NORTH => WEST => EAST => SOUTH and that the size of each tile in the maze is 16 (pixels)\n" + //
    "(check the right panel in case you forget).";

    private final AIAlgorithmsInteractiveLearningRenderer aiAlgorithmsInteractiveLearningRenderer;

    private int correctAnswers;
    private int wrongAnswers;

    private List<Node> pathInNodes;
    private Node currentNode;
    private Node nextNode;

    public AIAlgorithmsInteractiveLearningController(TextRenderer textRenderer)
    {
	super(textRenderer);
	AIAlgorithmsInteractiveLearningRenderer.selectedAlgorithmText.setTextMessage("=> " + AIAlgorithmsInteractiveLearningRenderer.algorithms[0].getText().getTextMessage());
	// set level 0 maze as default
	levelController = new LevelController(new Level(0));
	mazeRenderer = new MazeRenderer(levelController.getLevel().getMaze());
	// initialize level data
	levelController.initializeLevel(pacman);
	routeTiles = levelController.getLevel().getMaze().getRouteTiles();
	aiAlgorithmsInteractiveLearningRenderer = new AIAlgorithmsInteractiveLearningRenderer(textRenderer, pacman, searchAlgorithms);
	mazeThumbnailsBounds = aiAlgorithmsInteractiveLearningRenderer.getMazeThumbnailsBounds();
	educationalModeRenderer = aiAlgorithmsInteractiveLearningRenderer;
	correctAnswers = wrongAnswers = 0;
	randomNumberOfNodesToBeExpanded = new Random();
	SearchAlgorithm.startNodeDrawable = new Node(PathFinder.computeCurrentTile(pacman.getPosition()), null);
    }

    @Override
    public void update(double delta)
    {
	if (!searchAlgorithms[algorithmIndex].isStarted()) {
	    checkIfAlgorithmButtonIsClicked();
	    checkIfMazeThumbnailIsSelected(null);
	}

	if (isBackCancelButtonClicked()) {
	    if (backCancelButton == backButton) {
		Game.getKeyboard().returnedToMainMenu = true;
	    } else if (backCancelButton == cancelButton) {
		backCancelButton = backButton;
		if (interactiveExecutionAlgorithm != null) {
		    interactiveExecutionInterrupted = true;
		    terminateThread();
		}
	    }
	    correctAnswers = wrongAnswers = 0;
	    renderGrid = false;
	    userInteractionWindowShowed = false;
	    submode = -1;
	    return;
	}

	checkIfButtonIsClicked();

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

		    if (algorithmThread == null) {
			correctAnswers = wrongAnswers = 0;
			renderGrid = true;
			interactiveExecutionInterrupted = false;
			backCancelButton = cancelButton;

			algorithmThread = new Thread(this, "Interactive Algorithm");
			algorithmThread.start();
		    }
		}
	    }
	} else if (searchAlgorithms[algorithmIndex].isRunning()) {
	    if (interactiveExecutionAlgorithm != null) {
		interactiveExecutionAlgorithm.nextNodeExpansionWaitingTime = nodeExpansionSpeeds[nodeExpansionSpeedIndex];

		if (!interactiveExecutionAlgorithm.userInteracted) {
		    checkWhichTileWasClicked();
		}

		if (!userInteractionWindowShowed && wrongAnswers != 0) {
		    String wrongChoice = "You clicked at a wrong node!";
		    String algorithmFunction = "";
		    String expansionOrder = "";
		    if (wrongAnswers == 1 || (wrongAnswers % 5 == 0)) {
			algorithmFunction = "\n\nRemember:\n" + interactiveExecutionAlgorithm.getBasicAlgorithmFunction();
			expansionOrder = "\nAlso see the visiting order of (neighbor) nodes in the right panel.\n";
		    }

		    showUserInteractionWindow(wrongChoice + algorithmFunction + expansionOrder);
		    userInteractionWindowShowed = true;
		    interactiveExecutionAlgorithm.userInteracted = true;
		}
	    }

	} else if (searchAlgorithms[algorithmIndex].isFinished()) {
	    if (pacman.isMoving()) {
		pacman.addDelta((pacman.getMovingSpeed() / GameApplication.UPDATES_PER_SECOND) * delta);
		if (pacman.getDelta() >= 1) {
		    movePacmanToNextTile();
		    pacman.decreaseDeltaByOne();
		}
	    }
	}
    }

    private void checkIfButtonIsClicked()
    {
	if (MenuController.isButtonClicked(aiAlgorithmsInteractiveLearningRenderer.toggleGrid)) {
	    renderGrid = renderGrid ? false : true;
	}

	checkIfNodeExpansionSpeedButtonsIsClicked(aiAlgorithmsInteractiveLearningRenderer, null);

	if (interactiveExecutionAlgorithm == null && !pacman.isMoving() && MenuController.isButtonClicked(aiAlgorithmsInteractiveLearningRenderer.difficulty)) {
	    if (aiAlgorithmsInteractiveLearningRenderer.difficulty.getText() == AIAlgorithmsInteractiveLearningRenderer.easyDifficultyText) {
		aiAlgorithmsInteractiveLearningRenderer.difficulty.setText(AIAlgorithmsInteractiveLearningRenderer.mediumDifficultyText);
		mediumDifficulty = true;
		hardDifficulty = false;
	    } else if (aiAlgorithmsInteractiveLearningRenderer.difficulty.getText() == AIAlgorithmsInteractiveLearningRenderer.mediumDifficultyText) {
		aiAlgorithmsInteractiveLearningRenderer.difficulty.setText(AIAlgorithmsInteractiveLearningRenderer.hardDifficultyText);
		mediumDifficulty = false;
		hardDifficulty = true;
	    } else {
		aiAlgorithmsInteractiveLearningRenderer.difficulty.setText(AIAlgorithmsInteractiveLearningRenderer.easyDifficultyText);
		mediumDifficulty = false;
		hardDifficulty = false;
	    }
	}
    }

    private void checkWhichTileWasClicked()
    {
	Point mouseCoordinates = Mouse.getCoordinates();
	mouseCoordinates.translate(-(GameApplication.X_OFFSET - Maze.HALF_TILE), -(GameApplication.Y_OFFSET - Maze.HALF_TILE));

	for (Node neighborNode : SearchAlgorithm.unexpandedNodesDrawable) {
	    Rectangle userSelection = new Rectangle(neighborNode.getCoordinates(), new Dimension(Maze.TILE, Maze.TILE));
	    if (userSelection.contains(mouseCoordinates)) {
		ScreenPainter.hoveredNode = neighborNode;
		if (Mouse.getButton() == Mouse.LEFT_BUTTON) {
		    Mouse.setButton(-1);
		    if (neighborNode.getCoordinates().equals(nextNodeToBeExpanded.getCoordinates())) {
			correctAnswers++;
			interactiveExecutionAlgorithm.userInteracted = true;
		    } else {
			wrongAnswers++;
			userInteractionWindowShowed = false;
		    }
		    ScreenPainter.hoveredNode = null;
		}
		break;
	    } else {
		ScreenPainter.hoveredNode = null;
	    }
	}
    }

    @Override
    public void run()
    {
	showUserInteractionWindow(info);

	int currentHeuristicFunction;
	switch (algorithmIndex) {
	    case SearchAlgorithm.DFS:
		interactiveExecutionAlgorithm = new DepthFirstSearchAlgorithm();
		break;
	    case SearchAlgorithm.BFS:
		interactiveExecutionAlgorithm = new BreadthFirstSearchAlgorithm();
		break;
	    case SearchAlgorithm.IDS:
		interactiveExecutionAlgorithm = new IterativeDeepeningAlgorithm();
		break;
	    case SearchAlgorithm.BF:
		currentHeuristicFunction = ((BestFirstAlgorithm) searchAlgorithms[algorithmIndex]).getHeuristicFunction();
		BestFirstAlgorithm newBestFirstAlgorithm = new BestFirstAlgorithm();
		newBestFirstAlgorithm.setHeuristicFunction(currentHeuristicFunction);
		interactiveExecutionAlgorithm = newBestFirstAlgorithm;
		break;
	    case SearchAlgorithm.BS:
		currentHeuristicFunction = ((BeamSearchAlgorithm) searchAlgorithms[algorithmIndex]).getHeuristicFunction();
		BeamSearchAlgorithm newBeamSearchAlgorithm = new BeamSearchAlgorithm();
		newBeamSearchAlgorithm.setHeuristicFunction(currentHeuristicFunction);
		interactiveExecutionAlgorithm = newBeamSearchAlgorithm;
		break;
	    case SearchAlgorithm.A_STAR:
		currentHeuristicFunction = ((AStarAlgorithm) searchAlgorithms[algorithmIndex]).getHeuristicFunction();
		AStarAlgorithm newAStarAlgorithm = new AStarAlgorithm();
		newAStarAlgorithm.setHeuristicFunction(currentHeuristicFunction);
		interactiveExecutionAlgorithm = newAStarAlgorithm;
		break;
	    default:
	}

	interactiveExecutionAlgorithm.interactiveExecution = true;

	interactiveExecutionAlgorithm.findPath(routeTiles, start, destination);

	searchAlgorithms[algorithmIndex].setRunning(false);
	searchAlgorithms[algorithmIndex].setFinished(true);

	pathInNodes = Collections.synchronizedList(interactiveExecutionAlgorithm.getPathInNodes());

	currentNode = pathInNodes.remove(0);
	nextNode = pathInNodes.get(0);

	pacman.setMovingSpeed(pacmanSpeeds[pacmanSpeeds.length / 2]);
	pacman.setAnimationSpeed(Pacman.NORMAL_ANIMATION_SPEED);
	pacman.setAnimationCounter(0);
	pacman.setCurrentTile(currentNode.getCoordinates());
	pacman.setNextTile(nextNode.getCoordinates());
	pacman.computeNewDirection();

	SearchAlgorithm.solutionPathNodesDrawable.add(currentNode);

	if (interactiveExecutionInterrupted) {
	    interactiveExecutionAlgorithm.reset();
	    interactiveExecutionAlgorithm.resetStateFlags();
	    searchAlgorithms[algorithmIndex].reset();
	    searchAlgorithms[algorithmIndex].resetStateFlags();
	} else {
	    showUserAnswersWindow();
	    pacman.setMoving(true);
	    renderGrid = false;
	    algorithmThread = null;
	}
	interactiveExecutionAlgorithm = null;
    }

    private void showUserInteractionWindow(String message)
    {
	JOptionPane pane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE);
	JDialog dialog = pane.createDialog(null, "User interaction information");
	dialog.setModal(true);
	dialog.setVisible(true);
    }

    private void showUserAnswersWindow()
    {
	String results = "You clicked at:\n     " + String.valueOf(correctAnswers) + " correct and\n     " + String.valueOf(wrongAnswers) + " wrong nodes";

	double wrongAnswersRatio = (double) wrongAnswers / (correctAnswers + wrongAnswers);
	String oneOrFewMistakes = wrongAnswers == 1 ? "one mistake" : "a few mistakes";
	if (wrongAnswersRatio == 0) {
	    results += "\n\nBravo, you made no mistakes at all!\nIt seems you know how this algorithm works.";
	} else if (wrongAnswersRatio < 0.25) {
	    results += "\n\nYou made " + oneOrFewMistakes + " but you did\nvery well overall.";
	} else if (wrongAnswersRatio >= 0.25 && wrongAnswersRatio < 0.5) {
	    results += "\n\nYou did OK.";
	} else if (wrongAnswersRatio >= 0.5 && wrongAnswersRatio < 0.75) {
	    results += "\n\nYou made many mistakes, you have to\nstudy more.";
	} else {
	    results += "\n\nIt seems that you don't know how this\nalgorithm works.You must study harder!";
	}

	JOptionPane pane = new JOptionPane(results, JOptionPane.INFORMATION_MESSAGE);
	JDialog dialog = pane.createDialog(null, "User Interaction Results");
	dialog.setModal(true);
	dialog.setVisible(true);
	correctAnswers = 0;
	wrongAnswers = 0;
    }

    private void movePacmanToNextTile()
    {
	if (pacman.getPosition().equals(destination)) {
	    pacman.setMoving(false);
	    backCancelButton = backButton;
	    searchAlgorithms[algorithmIndex].resetStateFlags();
	    SearchAlgorithm.solutionPathNodesDrawable.add(pathInNodes.remove(0));
	    return;
	}

	int animationCounter = pacman.getAnimationCounter();
	pacman.setAnimationCounter((animationCounter < 10000) ? animationCounter + 1 : 0);

	if (pacman.getPosition().equals(pacman.getNextTile())) {
	    currentNode = pathInNodes.remove(0);
	    pacman.setCurrentTile(pacman.getNextTile());
	    pacman.setNextTile(pathInNodes.get(0).getCoordinates());
	    pacman.computeNewDirection();
	    SearchAlgorithm.solutionPathNodesDrawable.add(currentNode);
	}

	pacman.move(pacman.getDirection());
    }

    private void terminateThread()
    {
	if (algorithmThread != null && algorithmThread.isAlive()) {
	    interactiveExecutionAlgorithm.interactiveExecution = false;
	    algorithmThread.interrupt();
	    algorithmThread = null;
	}
    }

    @Override
    public void renderAll(Screen screen)
    {
	mazeRenderer.render(screen);
	aiAlgorithmsInteractiveLearningRenderer.render(screen);

	screen.renderScreenPainterVisuals(renderGrid, true);
	if (!renderGrid) {
	    pacmanController.renderPacman(screen);
	}
    }

    public static int getNodeExpansionSpeed()
    {
	return nodeExpansionSpeed;
    }
}
