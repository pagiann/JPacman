package jpacman.view.edumode;

import static jpacman.controller.edumode.AIAlgorithmsInteractiveLearningController.*;

import java.awt.Point;

import jpacman.GameApplication;
import jpacman.controller.edumode.EducationalModeController;
import jpacman.model.Maze;
import jpacman.model.Pacman;
import jpacman.model.Text;
import jpacman.model.UIButton;
import jpacman.model.edumode.BeamSearchAlgorithm;
import jpacman.model.edumode.IterativeDeepeningAlgorithm;
import jpacman.model.edumode.SearchAlgorithm;
import jpacman.view.TextRenderer;
import jpacman.view.graphics.Screen;

public class AIAlgorithmsInteractiveLearningRenderer extends AIAlgorithmsLearningRenderer
{
    public static final Text nodesVisitingOrderText = new Text("Visiting order of nodes:\n NORTH=>SOUTH=>WEST=>EAST", Text.DEFAULT_FONT_SMALL, 0xFF0066);
    public static final Text oneTileSizeText = new Text("Also 1 tile = 16 (pixels)", Text.DEFAULT_FONT_SMALL, 0xFF0066);
    public static final Text easyDifficultyText = new Text("Easy", Text.DEFAULT_FONT_MEDIUM);
    public static final Text mediumDifficultyText = new Text("Medium", Text.DEFAULT_FONT_MEDIUM);
    public static final Text hardDifficultyText = new Text("Hard", Text.DEFAULT_FONT_MEDIUM);

    public Text difficultyText = new Text("Difficulty:", Text.DEFAULT_FONT_SMALL, 0x9999FF);
    public UIButton difficulty = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 160, GameApplication.Y_OFFSET + 128), false, easyDifficultyText, true);

    public AIAlgorithmsInteractiveLearningRenderer(TextRenderer textRenderer, Pacman pacman, SearchAlgorithm[] searchAlgorithms)
    {
	super(textRenderer, pacman, searchAlgorithms);
	title.setTextMessage(TITLE_STRING_2);
	description.setTextMessage(DESCRIPTION_STRING_2);
	clickToStart.setTextMessage("Left click to set\n  the goal node.");

	toggleGrid = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 30, GameApplication.Y_OFFSET + 225), false, new Text("Toggle Grid", Text.DEFAULT_FONT_SMALL), true);
	minNodeExpansionSpeed = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 70, GameApplication.Y_OFFSET + 185), false, new Text("min", Text.DEFAULT_FONT_MEDIUM), true);
	maxNodeExpansionSpeed = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 230, GameApplication.Y_OFFSET + 185), false, new Text("max", Text.DEFAULT_FONT_MEDIUM), true);
	decreaseNodeExpansionSpeed = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 145, GameApplication.Y_OFFSET + 185), false, new Text("-", Text.DEFAULT_FONT_MEDIUM), true);
	increaseNodeExpansionSpeed = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 185, GameApplication.Y_OFFSET + 185), false, new Text("+", Text.DEFAULT_FONT_MEDIUM), true);
    }

    @Override
    public void render(Screen screen)
    {
	renderLeftSidebar(screen);
	textRenderer.renderText(screen, selectAlgorithm, 35, GameApplication.Y_OFFSET, false);
	textRenderer.renderText(screen, blindAlgorithm, 40, GameApplication.Y_OFFSET + 25, false);
	textRenderer.renderText(screen, heuristicAlgorithm, 40, GameApplication.Y_OFFSET + 115, false);
	renderAlgorithmButtons(screen);

	textRenderer.renderText(screen, title, Screen.getMiddleAlignStartPositionInWindow(title.getWidth()), title.getHeight(), false);

	int selectedAlgorithmIndex = EducationalModeController.getAlgorithmIndex();

	if (searchAlgorithms[selectedAlgorithmIndex].isRunning()) {
	    if (interactiveExecutionAlgorithm != null && !interactiveExecutionAlgorithm.userInteracted) {
		clickToStart.setTextMessage("    Algorithm\n     paused.");
	    } else {
		clickToStart.setTextMessage("    Algorithm\n   is running...");
	    }
	} else {
	    clickToStart.setTextMessage("Left click to set\n    goal node.");
	}

	textRenderer.renderText(screen, clickToStart, Maze.WIDTH + 30, 5, true);
	textRenderer.renderText(screen, nodesVisitingOrderText, Maze.WIDTH + 30, 65, true);
	textRenderer.renderText(screen, oneTileSizeText, Maze.WIDTH + 30, 105, true);

	toggleGrid.render(textRenderer, screen);
	textRenderer.renderText(screen, expandedNodesSpeedText, Maze.WIDTH + 30, 165, true);
	expandedNodesSpeedValueText.setTextMessage("[" + String.valueOf(getNodeExpansionSpeed()) + "]");
	textRenderer.renderText(screen, expandedNodesSpeedValueText, Maze.WIDTH + 30 + expandedNodesSpeedText.getWidth() + 10, 165, true);
	minNodeExpansionSpeed.render(textRenderer, screen);
	decreaseNodeExpansionSpeed.render(textRenderer, screen);
	increaseNodeExpansionSpeed.render(textRenderer, screen);
	maxNodeExpansionSpeed.render(textRenderer, screen);

	textRenderer.renderText(screen, difficultyText, Maze.WIDTH + 30, 135, true);
	difficulty.render(textRenderer, screen);

	int xOffset = 30;
	int yOffset1 = 255;
	textRenderer.renderText(screen, algorithmInfoText, Maze.WIDTH + xOffset, yOffset1, true);
	textRenderer.renderText(screen, selectedAlgorithmText, Maze.WIDTH + xOffset, yOffset1 + 20, true);
	int yOffset = 0;
	if (selectedAlgorithmIndex == SearchAlgorithm.BS || selectedAlgorithmIndex == SearchAlgorithm.BF || selectedAlgorithmIndex == SearchAlgorithm.A_STAR) {
	    yOffset = 15;
	}

	if (interactiveExecutionAlgorithm == null) {
	    if (selectedAlgorithmIndex == SearchAlgorithm.IDS) {
		yOffsetRightPanel = 70;
		textRenderer.renderText(screen, initialDepthText, Maze.WIDTH + 30, yOffset1RightPanel + yOffsetRightPanel - 15, true);
		initialDepthValueText.setTextMessage("[" + ((IterativeDeepeningAlgorithm) searchAlgorithms[selectedAlgorithmIndex]).getInitialDepth() + "]");
		textRenderer.renderText(screen, initialDepthValueText, Maze.WIDTH + 30 + initialDepthText.getWidth() + 10, yOffset1RightPanel + yOffsetRightPanel - 15, true);
		textRenderer.renderText(screen, depthStepText, Maze.WIDTH + 30, yOffset1RightPanel + yOffsetRightPanel + 10, true);
		depthStepValueText.setTextMessage("[" + ((IterativeDeepeningAlgorithm) searchAlgorithms[selectedAlgorithmIndex]).getDepthStep() + "]");
		textRenderer.renderText(screen, depthStepValueText, Maze.WIDTH + 30 + depthStepText.getWidth() + 10, yOffset1RightPanel + yOffsetRightPanel + 10, true);
	    } else if (selectedAlgorithmIndex == SearchAlgorithm.BS) {
		beamWidthText.setTextMessage("Default Beam Width:");
		textRenderer.renderText(screen, beamWidthText, Maze.WIDTH + 30, yOffset1RightPanel + 70, true);
		beamWidthValueText.setTextMessage("[" + ((BeamSearchAlgorithm) searchAlgorithms[selectedAlgorithmIndex]).getBeamWidth() + "]");
		textRenderer.renderText(screen, beamWidthValueText, Maze.WIDTH + 30 + beamWidthText.getWidth() + 10, yOffset1RightPanel + 70, true);
	    } else if (selectedAlgorithmIndex == SearchAlgorithm.BF || selectedAlgorithmIndex == SearchAlgorithm.A_STAR) {
		yOffsetRightPanel = -0;
	    } else {
		yOffsetRightPanel = -15;
	    }
	} else {
	    if (selectedAlgorithmIndex == SearchAlgorithm.IDS) {
		yOffset1 += 45;
		currentIterationText.setTextMessage("Current Iteration: " + ((IterativeDeepeningAlgorithm) interactiveExecutionAlgorithm).getIteration());
		textRenderer.renderText(screen, currentIterationText, Maze.WIDTH + 30, yOffset1RightPanel + yOffsetRightPanel - 15, true);
		currentDepthLimitText.setTextMessage("Current Depth Limit: " + ((IterativeDeepeningAlgorithm) interactiveExecutionAlgorithm).getDepthLimit());
		textRenderer.renderText(screen, currentDepthLimitText, Maze.WIDTH + 30, yOffset1RightPanel + yOffsetRightPanel + 5, true);
	    } else if (selectedAlgorithmIndex == SearchAlgorithm.BS) {
		yOffset1 += 25;
		textRenderer.renderText(screen, beamWidthText, Maze.WIDTH + 30, yOffset1RightPanel + 70, true);
		beamWidthValueText.setTextMessage("[" + ((BeamSearchAlgorithm) searchAlgorithms[selectedAlgorithmIndex]).getBeamWidth() + "]");
		textRenderer.renderText(screen, beamWidthValueText, Maze.WIDTH + 30 + beamWidthText.getWidth() + 10, yOffset1RightPanel + 70, true);
	    }

	    String nodeInfo = "";
	    if (SearchAlgorithm.startNodeDrawable != null) {
		nodeInfo = SearchAlgorithm.startNodeDrawable.getMazeGridLocation();
	    }
	    startNodeText.setTextMessage("Start Node: " + nodeInfo);
	    startNodeText.setColor(Screen.YELLOW_COLOR);
	    textRenderer.renderText(screen, startNodeText, Maze.WIDTH + xOffset, yOffset1 + 50 + yOffset, true);
	    nodeInfo = "";
	    if (SearchAlgorithm.goalNodeDrawable != null) {
		nodeInfo = SearchAlgorithm.goalNodeDrawable.getMazeGridLocation();
	    }
	    goalNodeText.setTextMessage("Goal Node: " + nodeInfo);
	    goalNodeText.setColor(Screen.GREEN_COLOR);
	    textRenderer.renderText(screen, goalNodeText, Maze.WIDTH + xOffset, yOffset1 + 70 + yOffset, true);

	    nodesExpandedText.setTextMessage("Nodes expanded: " + interactiveExecutionAlgorithm.getNodesExpanded());
	    textRenderer.renderText(screen, nodesExpandedText, Maze.WIDTH + xOffset, yOffset1 + 90 + yOffset, true);
	    nodesInOpenListText.setTextMessage("Nodes in Search Frontier: " + interactiveExecutionAlgorithm.getOpenListSize());
	    textRenderer.renderText(screen, nodesInOpenListText, Maze.WIDTH + xOffset, yOffset1 + 110 + yOffset, true);

	    String currentActionString = interactiveExecutionAlgorithm.getCurrentAction();
	    textRenderer.renderText(screen, currentActionStatusLabelText, Maze.WIDTH + xOffset, yOffset1 + 140 + yOffset, true);
	    currentActionText.setTextMessage(currentActionString);
	    textRenderer.renderText(screen, currentActionText, Maze.WIDTH + xOffset, yOffset1 + 160 + yOffset, true);
	}

	textRenderer.renderText(screen, description, Screen.getMiddleAlignStartPositionInWindow(description.getWidth()), GameApplication.WINDOW_HEIGHT - 75, false);
    }
}
