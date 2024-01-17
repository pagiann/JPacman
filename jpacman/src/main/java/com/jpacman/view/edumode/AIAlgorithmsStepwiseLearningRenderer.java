package com.jpacman.view.edumode;

import static com.jpacman.controller.edumode.AIAlgorithmsStepwiseLearningController.*;

import java.awt.Point;

import com.jpacman.GameApplication;
import com.jpacman.controller.edumode.EducationalModeController;
import com.jpacman.model.Maze;
import com.jpacman.model.Pacman;
import com.jpacman.model.Text;
import com.jpacman.model.UIButton;
import com.jpacman.model.edumode.BeamSearchAlgorithm;
import com.jpacman.model.edumode.IterativeDeepeningAlgorithm;
import com.jpacman.model.edumode.SearchAlgorithm;
import com.jpacman.view.TextRenderer;
import com.jpacman.view.graphics.Screen;

public class AIAlgorithmsStepwiseLearningRenderer extends AIAlgorithmsLearningRenderer {
	public static final Text startText = new Text("Start", Text.DEFAULT_FONT_LARGE, Screen.WHITE_COLOR);
	public static final Text resumeText = new Text("Resume", Text.DEFAULT_FONT_LARGE, Screen.WHITE_COLOR);
	public static final Text executeAlgorithmText = new Text(" Execute\nalgorithm", Text.DEFAULT_FONT_MEDIUM,
			Screen.WHITE_COLOR);
	public static final UIButton startResumeExecuteAlgorithmButton = new UIButton(
			new Point(GameApplication.X_OFFSET + Maze.WIDTH + 95, GameApplication.Y_OFFSET + 5), false, startText,
			true);

	public static final Text automaticModeText = new Text("Auto", Text.DEFAULT_FONT_MEDIUM, Screen.WHITE_COLOR);
	public static final Text manualModeText = new Text("Manual", Text.DEFAULT_FONT_MEDIUM, Screen.WHITE_COLOR);
	public static final Text executionModeText = new Text("Execution mode:", Text.DEFAULT_FONT_SMALL, 0x9999FF);
	public static final UIButton executionMode = new UIButton(
			new Point(GameApplication.X_OFFSET + Maze.WIDTH + 200, GameApplication.Y_OFFSET + 57), false,
			manualModeText, true);

	public static final Text iterationsText = new Text("Iterations:", Text.DEFAULT_FONT_SMALL, 0x9999FF);
	public static final Text iterationsValueText = new Text("", Text.DEFAULT_FONT_SMALL, 0xCCCCFF);
	public static final UIButton minStepSize = new UIButton(
			new Point(GameApplication.X_OFFSET + Maze.WIDTH + 190, GameApplication.Y_OFFSET + 105), false,
			new Text("min", Text.DEFAULT_FONT_SMALL), true);
	public static final UIButton decreaseStepSize = new UIButton(
			new Point(GameApplication.X_OFFSET + Maze.WIDTH + 235, GameApplication.Y_OFFSET + 105), false,
			new Text("-", Text.DEFAULT_FONT_SMALL), true);
	public static final UIButton increaseStepSize = new UIButton(
			new Point(GameApplication.X_OFFSET + Maze.WIDTH + 260, GameApplication.Y_OFFSET + 105), false,
			new Text("+", Text.DEFAULT_FONT_SMALL), true);
	public static final UIButton maxStepSize = new UIButton(
			new Point(GameApplication.X_OFFSET + Maze.WIDTH + 285, GameApplication.Y_OFFSET + 105), false,
			new Text("max", Text.DEFAULT_FONT_SMALL), true);
	public static final UIButton step = new UIButton(
			new Point(GameApplication.X_OFFSET + Maze.WIDTH + 215, GameApplication.Y_OFFSET + 130), false,
			new Text("Step", Text.DEFAULT_FONT_LARGE, Screen.WHITE_COLOR), false);

	private static final Text currentNodeText = new Text("", Text.DEFAULT_FONT_SMALL, 0x6666FF);
	private static final Text currentChildNodeText = new Text("", Text.DEFAULT_FONT_SMALL, 0xCC99FF);

	public AIAlgorithmsStepwiseLearningRenderer(TextRenderer textRenderer, Pacman pacman,
			SearchAlgorithm[] searchAlgorithms) {
		super(textRenderer, pacman, searchAlgorithms);
		title.setTextMessage(TITLE_STRING_1);
		description.setTextMessage(DESCRIPTION_STRING_1);

		expandedNodesColor = new UIButton(
				new Point(GameApplication.X_OFFSET + Maze.WIDTH + 245, GameApplication.Y_OFFSET + 85), false,
				defaultColorText, true);
		showSearchTree = new UIButton(
				new Point(GameApplication.X_OFFSET + Maze.WIDTH + 30, GameApplication.Y_OFFSET + 125), false,
				new Text("Show Search Tree", Text.DEFAULT_FONT_SMALL), true);
		toggleGrid = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 30, GameApplication.Y_OFFSET + 145),
				false, new Text("Toggle Grid", Text.DEFAULT_FONT_SMALL), true);
		startNodeText.setColor(Screen.RED_COLOR);
		goalNodeText.setColor(Screen.YELLOW_COLOR);
	}

	@Override
	public void render(Screen screen) {
		textRenderer.renderText(screen, title, Screen.getMiddleAlignStartPositionInWindow(title.getWidth()),
				title.getHeight(), false);

		renderLeftSidebar(screen);
		textRenderer.renderText(screen, selectAlgorithm, 35, GameApplication.Y_OFFSET, false);
		textRenderer.renderText(screen, blindAlgorithm, 40, GameApplication.Y_OFFSET + 25, false);
		textRenderer.renderText(screen, heuristicAlgorithm, 40, GameApplication.Y_OFFSET + 115, false);
		renderAlgorithmButtons(screen);

		startResumeExecuteAlgorithmButton.render(textRenderer, screen);
		textRenderer.renderText(screen, executionModeText, Maze.WIDTH + 30, 65, true);
		executionMode.render(textRenderer, screen);
		textRenderer.renderText(screen, expandedNodesColorText, Maze.WIDTH + 30, 85, true);
		expandedNodesColor.render(textRenderer, screen);
		textRenderer.renderText(screen, iterationsText, Maze.WIDTH + 30, 105, true);
		iterationsValueText.setTextMessage("[" + iterationSize + "]");
		textRenderer.renderText(screen, iterationsValueText, Maze.WIDTH + 30 + iterationsText.getWidth() + 5, 105,
				true);
		minStepSize.render(textRenderer, screen);
		decreaseStepSize.render(textRenderer, screen);
		increaseStepSize.render(textRenderer, screen);
		maxStepSize.render(textRenderer, screen);
		step.render(textRenderer, screen);
		showSearchTree.render(textRenderer, screen);
		toggleGrid.render(textRenderer, screen);

		int selectedAlgorithmIndex = EducationalModeController.getAlgorithmIndex();

		int xOffset = 30;
		int yOffset1 = 175;
		textRenderer.renderText(screen, algorithmInfoText, Maze.WIDTH + xOffset, yOffset1, true);
		textRenderer.renderText(screen, selectedAlgorithmText, Maze.WIDTH + xOffset, yOffset1 + 20, true);
		int yOffset = 0;
		if (selectedAlgorithmIndex == SearchAlgorithm.BS || selectedAlgorithmIndex == SearchAlgorithm.BF
				|| selectedAlgorithmIndex == SearchAlgorithm.A_STAR) {
			yOffset = 10;
		}

		if (stepwiseExecutionMode) {
			String nodeInfo = "";
			if (SearchAlgorithm.startNodeDrawable != null) {
				nodeInfo = SearchAlgorithm.startNodeDrawable.getMazeGridLocation();
			}
			startNodeText.setTextMessage("Start Node: " + nodeInfo);
			textRenderer.renderText(screen, startNodeText, Maze.WIDTH + xOffset, yOffset1 + 50 + yOffset, true);
			nodeInfo = "";
			if (SearchAlgorithm.goalNodeDrawable != null) {
				nodeInfo = SearchAlgorithm.goalNodeDrawable.getMazeGridLocation();
			}
			goalNodeText.setTextMessage("Goal Node: " + nodeInfo);
			textRenderer.renderText(screen, goalNodeText, Maze.WIDTH + xOffset, yOffset1 + 70 + yOffset, true);
			nodeInfo = "";
			if (SearchAlgorithm.currentNodeDrawable != null) {
				nodeInfo = SearchAlgorithm.currentNodeDrawable.getMazeGridLocation();
			}
			currentNodeText.setTextMessage("Current Node: " + nodeInfo);
			textRenderer.renderText(screen, currentNodeText, Maze.WIDTH + xOffset, yOffset1 + 90 + yOffset, true);
			nodeInfo = "";
			if (SearchAlgorithm.currentNeighborNodeDrawable != null) {
				nodeInfo = SearchAlgorithm.currentNeighborNodeDrawable.getMazeGridLocation();
			}
			currentChildNodeText.setTextMessage("Current Neighbor Node:" + nodeInfo);
			textRenderer.renderText(screen, currentChildNodeText, Maze.WIDTH + xOffset, yOffset1 + 110 + yOffset, true);

			if (stepwiseExecutionAlgorithm != null) {
				nodesVisitedText.setTextMessage("Nodes visited: " + stepwiseExecutionAlgorithm.getNodesVisited());
				textRenderer.renderText(screen, nodesVisitedText, Maze.WIDTH + xOffset, yOffset1 + 130 + yOffset, true);
				nodesExpandedText.setTextMessage("Nodes expanded: " + stepwiseExecutionAlgorithm.getNodesExpanded());
				textRenderer.renderText(screen, nodesExpandedText, Maze.WIDTH + xOffset, yOffset1 + 150 + yOffset,
						true);
				nodesInOpenListText
						.setTextMessage("Nodes in Search Frontier: " + SearchAlgorithm.unexpandedNodesDrawable.size());
				textRenderer.renderText(screen, nodesInOpenListText, Maze.WIDTH + xOffset, yOffset1 + 170 + yOffset,
						true);
				textRenderer.renderText(screen, currentActionStatusLabelText, Maze.WIDTH + xOffset,
						yOffset1 + 200 + yOffset, true);
				currentActionText.setTextMessage(stepwiseExecutionAlgorithm.getCurrentAction());
				textRenderer.renderText(screen, currentActionText, Maze.WIDTH + xOffset, yOffset1 + 220 + yOffset,
						true);
			}
		} else {
			if (selectedAlgorithmIndex == SearchAlgorithm.IDS) {
				yOffsetRightPanel = -50;
				yOffset = 70;

				textRenderer.renderText(screen, initialDepthText, Maze.WIDTH + 30,
						yOffset1RightPanel + yOffsetRightPanel + 25, true);
				initialDepthValueText.setTextMessage(
						"[" + ((IterativeDeepeningAlgorithm) searchAlgorithms[selectedAlgorithmIndex]).getInitialDepth()
								+ "]");
				textRenderer.renderText(screen, initialDepthValueText,
						Maze.WIDTH + 30 + initialDepthText.getWidth() + 10, yOffset1RightPanel + yOffsetRightPanel + 25,
						true);
				decreaseInitialDepth.moveBoundsLocationOnce(0, -75);
				decreaseInitialDepth.render(textRenderer, screen);
				increaseInitialDepth.moveBoundsLocationOnce(0, -75);
				increaseInitialDepth.render(textRenderer, screen);

				textRenderer.renderText(screen, depthStepText, Maze.WIDTH + 30,
						yOffset1RightPanel + yOffsetRightPanel + 50, true);
				depthStepValueText.setTextMessage(
						"[" + ((IterativeDeepeningAlgorithm) searchAlgorithms[selectedAlgorithmIndex]).getDepthStep()
								+ "]");
				textRenderer.renderText(screen, depthStepValueText, Maze.WIDTH + 30 + depthStepText.getWidth() + 10,
						yOffset1RightPanel + yOffsetRightPanel + 50, true);
				decreaseDepthStep.moveBoundsLocationOnce(0, -70);
				decreaseDepthStep.render(textRenderer, screen);
				increaseDepthStep.moveBoundsLocationOnce(0, -70);
				increaseDepthStep.render(textRenderer, screen);

				currentDepthText.setTextMessage("Current Depth: "
						+ ((IterativeDeepeningAlgorithm) searchAlgorithms[selectedAlgorithmIndex]).getCurrentDepth());
				textRenderer.renderText(screen, currentDepthText, Maze.WIDTH + 30,
						yOffset1RightPanel + yOffsetRightPanel + 75, true);
			} else if (selectedAlgorithmIndex == SearchAlgorithm.BS) {
				yOffsetRightPanel = -10;
				yOffset = 35;

				textRenderer.renderText(screen, beamWidthText, Maze.WIDTH + 30, yOffset1RightPanel + yOffsetRightPanel,
						true);
				beamWidthValueText.setTextMessage(
						"[" + ((BeamSearchAlgorithm) searchAlgorithms[selectedAlgorithmIndex]).getBeamWidth() + "]");
				textRenderer.renderText(screen, beamWidthValueText, Maze.WIDTH + 30 + beamWidthText.getWidth() + 10,
						yOffset1RightPanel + yOffsetRightPanel, true);
				decreaseBeamWidth.moveBoundsLocationOnce(0, -55);
				decreaseBeamWidth.render(textRenderer, screen);
				increaseBeamWidth.moveBoundsLocationOnce(0, -55);
				increaseBeamWidth.render(textRenderer, screen);
			} else if (selectedAlgorithmIndex == SearchAlgorithm.BF
					|| selectedAlgorithmIndex == SearchAlgorithm.A_STAR) {
				yOffset = 15;
			}

			nodesVisitedText.setTextMessage(
					"Nodes (tiles) visited: " + searchAlgorithms[selectedAlgorithmIndex].getNodesVisited());
			textRenderer.renderText(screen, nodesVisitedText, Maze.WIDTH + xOffset, yOffset1 + 50 + yOffset, true);
			nodesExpandedText.setTextMessage(
					"Nodes (tiles) expanded: " + searchAlgorithms[selectedAlgorithmIndex].getNodesExpanded());
			textRenderer.renderText(screen, nodesExpandedText, Maze.WIDTH + xOffset, yOffset1 + 75 + yOffset, true);
			nodesInOpenListText.setTextMessage(
					"Nodes in Search Frontier: " + searchAlgorithms[selectedAlgorithmIndex].getOpenListSize());
			textRenderer.renderText(screen, nodesInOpenListText, Maze.WIDTH + xOffset, yOffset1 + 100 + yOffset, true);
			solutionPathLengthInTiles.setTextMessage(
					"Step: " + searchAlgorithms[selectedAlgorithmIndex].getPathInPoints().size() + " tiles");
			textRenderer.renderText(screen, solutionPathLengthInTiles, Maze.WIDTH + xOffset, yOffset1 + 125 + yOffset,
					true);

			int yOffset2 = 330 + yOffset;
			textRenderer.renderText(screen, pacmanInfoText, Maze.WIDTH + xOffset, yOffset2, true);
			distanceTraveledText.setTextMessage("Distance traveled:\n " + pacman.getDistanceMoved() + " pixels");
			textRenderer.renderText(screen, distanceTraveledText, Maze.WIDTH + xOffset, yOffset2 + 20, true);
			tilesTraveledText.setTextMessage("Tiles traveled: " + tilesTraveled);
			textRenderer.renderText(screen, tilesTraveledText, Maze.WIDTH + xOffset, yOffset2 + 60, true);
		}

		textRenderer.renderText(screen, description, Screen.getMiddleAlignStartPositionInWindow(description.getWidth()),
				GameApplication.WINDOW_HEIGHT - 75, false);
	}
}
