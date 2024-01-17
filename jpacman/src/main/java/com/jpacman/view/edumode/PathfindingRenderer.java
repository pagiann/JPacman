package com.jpacman.view.edumode;

import java.awt.Point;
import java.text.DecimalFormat;

import com.jpacman.Game;
import com.jpacman.Game.SubMode;
import com.jpacman.GameApplication;
import com.jpacman.controller.edumode.PathfindingController;
import com.jpacman.model.Maze;
import com.jpacman.model.Pacman;
import com.jpacman.model.Text;
import com.jpacman.model.UIButton;
import com.jpacman.model.edumode.BeamSearchAlgorithm;
import com.jpacman.model.edumode.IterativeDeepeningAlgorithm;
import com.jpacman.model.edumode.SearchAlgorithm;
import com.jpacman.view.TextRenderer;
import com.jpacman.view.graphics.Screen;

public class PathfindingRenderer extends EducationalModeRenderer {
	private static final String TITLE_STRING = "Pathfinding Visualization";
	private static final String TITLE_STRING_1 = "Maze Pathfinding Visualization";
	private static final String TITLE_STRING_2 = "Custom Space Pathfinding Visualization";
	private static final String EMPTY_DESCRIPTION_STRING = "";
	private static final String DESCRIPTION_STRING_1 = "" + //
			"In this mode, pacman is inside a fixed maze and travels to the destination you set each time, using\n" + //
			" the computed path from the selected algorithm. The visualization of the expanded nodes is a good\n" + //
			"                             indication of how each algorithm works.\n";
	private static final String DESCRIPTION_STRING_2 = "" + //
			"In this mode, pacman is inside an empty space and travels to the destination you set each time, using\n" + //
			" the computed path from the selected algorithm. You can also add obstacles anywhere you want inside\n" + //
			"this space. The visualization of the expanded nodes is a good indication of how each algorithm works.\n";

	public static final UIButton[] modes = new UIButton[PathfindingController.NUM_OF_VARIATIONS + 1];
	public static final String[] descriptions = new String[PathfindingController.NUM_OF_VARIATIONS + 1];
	private static final Text modeSelectionText = new Text("Select a mode", Text.DEFAULT_FONT_LARGE,
			Screen.YELLOW_COLOR);
	private static final Text customSpaceText = new Text("Custom Space", Text.DEFAULT_FONT_LARGE);
	private static final Text mazeText = new Text("Maze", Text.DEFAULT_FONT_LARGE);
	private static final UIButton mazePathfindingButton = new UIButton(new Point(Maze.WIDTH, (Maze.HEIGHT / 2) + 15),
			true, mazeText, true);
	private static final UIButton customSpacePathfindingButton = new UIButton(
			new Point(Maze.WIDTH, Maze.HEIGHT / 2 + 65), true, customSpaceText, true);

	private static final Text toggleObstacleText = new Text("Right click and\ndrag to toggle\n   obstacles",
			Text.DEFAULT_FONT_MEDIUM, 0xFF9933);
	public static final UIButton clearPathButton = new UIButton(
			new Point(85, GameApplication.WINDOW_HEIGHT - GameApplication.Y_OFFSET - 110), false,
			new Text("Clear Path", Text.DEFAULT_FONT_MEDIUM), true);
	public static final UIButton clearAllButton = new UIButton(
			new Point(92, GameApplication.WINDOW_HEIGHT - GameApplication.Y_OFFSET - 80), false,
			new Text("Clear All", Text.DEFAULT_FONT_MEDIUM), true);

	// static initializer
	static {
		descriptions[PathfindingController.MAZE] = DESCRIPTION_STRING_1;
		descriptions[PathfindingController.CUSTOM_SPACE] = DESCRIPTION_STRING_2;
		descriptions[PathfindingController.NUM_OF_VARIATIONS] = EMPTY_DESCRIPTION_STRING;
		modes[PathfindingController.MAZE] = mazePathfindingButton;
		modes[PathfindingController.CUSTOM_SPACE] = customSpacePathfindingButton;
		modes[PathfindingController.NUM_OF_VARIATIONS] = backButton;
	}

	public static final Text allowDiagonalText = new Text("Allow Diagonal Movement", Text.DEFAULT_FONT_SMALL);
	public static final Text disallowDiagonalText = new Text("Disallow Diagonal Movement", Text.DEFAULT_FONT_SMALL);
	public UIButton useDiagonalTiles = new UIButton(
			new Point(GameApplication.X_OFFSET + Maze.WIDTH + 30, GameApplication.Y_OFFSET + 55), false,
			allowDiagonalText, true);

	private final SearchAlgorithm[] searchAlgorithms;

	public PathfindingRenderer(TextRenderer textRenderer, Pacman pacman, SearchAlgorithm[] searchAlgorithms) {
		super(textRenderer, pacman);
		this.searchAlgorithms = searchAlgorithms;
		title.setTextMessage(TITLE_STRING);
		description.setTextMessage(DESCRIPTION_STRING_1);
		clickToStart.setTextMessage("Left click to set\npacman destination");
	}

	public void renderVariationTextStrings(Screen screen) {
		textRenderer.renderText(screen, modeSelectionText,
				Screen.getMiddleAlignStartPositionInArea(Maze.WIDTH, modeSelectionText.getWidth()),
				GameApplication.WINDOW_HEIGHT / 3, true);

		for (int i = 0; i < modes.length; i++) {
			if (modes[i].isVisible()) {
				if (modes[i].hasFocus() || modes[i].isMouseHovered()) {
					screen.renderBounds(modes[i].getBounds(), Screen.WHITE_COLOR, -1, 0, 8, 4, false);
					modes[i].getText().setColor(Screen.YELLOW_COLOR);
					description.setTextMessage(descriptions[i]);
				} else {
					modes[i].getText().setColor(Screen.WHITE_COLOR);
				}
				textRenderer.renderText(screen, modes[i].getText(), modes[i].getBounds().x, modes[i].getBounds().y,
						false);
			}
		}
	}

	@Override
	public void render(Screen screen) {
		textRenderer.renderText(screen, title, Screen.getMiddleAlignStartPositionInWindow(title.getWidth()),
				title.getHeight(), false);

		if (Game.getActiveSubMode() == Game.SubMode.PATHFINDING_MENU) {
			// center align back menu item
			backButton.getBounds().x = Screen.getMiddleAlignStartPositionInWindow(backButton.getText().getWidth());
			renderVariationTextStrings(screen);
			textRenderer.renderText(screen, description,
					Screen.getMiddleAlignStartPositionInWindow(description.getWidth()),
					GameApplication.WINDOW_HEIGHT - 70, false);
		} else {
			if (Game.getActiveSubMode() == SubMode.MAZE_PATHFINDING) {
				title.setTextMessage(TITLE_STRING_1);
				renderLeftSidebar(screen);
			} else {
				title.setTextMessage(TITLE_STRING_2);
				textRenderer.renderText(screen, toggleObstacleText, 50, GameApplication.Y_OFFSET + 305, false);
				// left align back menu item
				backButton.getBounds().x = 115;
				clearPathButton.render(textRenderer, screen);
				clearAllButton.render(textRenderer, screen);
				backCancelButton.render(textRenderer, screen);
			}
			textRenderer.renderText(screen, selectAlgorithm, 30, GameApplication.Y_OFFSET, false);
			textRenderer.renderText(screen, blindAlgorithm, 40, GameApplication.Y_OFFSET + 25, false);
			textRenderer.renderText(screen, heuristicAlgorithm, 40, GameApplication.Y_OFFSET + 115, false);
			renderAlgorithmButtons(screen);

			int selectedAlgorithmIndex = PathfindingController.getAlgorithmIndex();
			if (searchAlgorithms[selectedAlgorithmIndex].isFinished()) {
				pauseResumeButton.render(textRenderer, screen);
			} else {
				if (searchAlgorithms[selectedAlgorithmIndex].isRunning()) {
					clickToStart.setTextMessage("   Algorithm is\n    running...");
				} else {
					clickToStart.setTextMessage("Left click to set\npacman destination");
				}
				textRenderer.renderText(screen, clickToStart, Maze.WIDTH + 30, 0, true);
			}

			if (Game.getActiveSubMode() == SubMode.CUSTOM_SPACE_PATHFINDING) {
				useDiagonalTiles.render(textRenderer, screen);
			} else {
				showSearchTree.render(textRenderer, screen);
			}
			toggleGrid.render(textRenderer, screen);
			textRenderer.renderText(screen, expandedNodesColorText, Maze.WIDTH + 30, 105, true);
			expandedNodesColor.render(textRenderer, screen);
			textRenderer.renderText(screen, expandedNodesSpeedText, Maze.WIDTH + 30, 130, true);
			expandedNodesSpeedValueText
					.setTextMessage("[" + String.valueOf(PathfindingController.getNodeExpansionSpeed()) + "]");
			textRenderer.renderText(screen, expandedNodesSpeedValueText,
					Maze.WIDTH + 30 + expandedNodesSpeedText.getWidth() + 10, 130, true);
			minNodeExpansionSpeed.render(textRenderer, screen);
			decreaseNodeExpansionSpeed.render(textRenderer, screen);
			increaseNodeExpansionSpeed.render(textRenderer, screen);
			maxNodeExpansionSpeed.render(textRenderer, screen);
			textRenderer.renderText(screen, pacmanSpeedText, Maze.WIDTH + 30, 180, true);
			pacmanSpeedValueText.setTextMessage("[" + String.valueOf((int) pacman.getMovingSpeed()) + "]");
			textRenderer.renderText(screen, pacmanSpeedValueText, Maze.WIDTH + 30 + pacmanSpeedText.getWidth() + 10,
					180, true);
			minPacmanSpeed.render(textRenderer, screen);
			decreasePacmanSpeed.render(textRenderer, screen);
			increasePacmanSpeed.render(textRenderer, screen);
			maxPacmanSpeed.render(textRenderer, screen);

			textRenderer.renderText(screen, algorithmInfoText, Maze.WIDTH + xOffsetRightPanel, yOffset1RightPanel - 15,
					true);
			textRenderer.renderText(screen, selectedAlgorithmText, Maze.WIDTH + xOffsetRightPanel,
					yOffset1RightPanel + 5, true);

			if (selectedAlgorithmIndex == SearchAlgorithm.IDS) {
				yOffsetRightPanel = 65;

				visualizeIterations.render(textRenderer, screen);

				textRenderer.renderText(screen, initialDepthText, Maze.WIDTH + 30,
						yOffset1RightPanel + yOffsetRightPanel - 15, true);
				initialDepthValueText.setTextMessage(
						"[" + ((IterativeDeepeningAlgorithm) searchAlgorithms[selectedAlgorithmIndex]).getInitialDepth()
								+ "]");
				textRenderer.renderText(screen, initialDepthValueText,
						Maze.WIDTH + 30 + initialDepthText.getWidth() + 10, yOffset1RightPanel + yOffsetRightPanel - 15,
						true);
				decreaseInitialDepth.render(textRenderer, screen);
				increaseInitialDepth.render(textRenderer, screen);

				textRenderer.renderText(screen, depthStepText, Maze.WIDTH + 30,
						yOffset1RightPanel + yOffsetRightPanel + 5, true);
				depthStepValueText.setTextMessage(
						"[" + ((IterativeDeepeningAlgorithm) searchAlgorithms[selectedAlgorithmIndex]).getDepthStep()
								+ "]");
				textRenderer.renderText(screen, depthStepValueText, Maze.WIDTH + 30 + depthStepText.getWidth() + 10,
						yOffset1RightPanel + yOffsetRightPanel + 5, true);
				decreaseDepthStep.render(textRenderer, screen);
				increaseDepthStep.render(textRenderer, screen);

				currentDepthText.setTextMessage("Current Depth: "
						+ ((IterativeDeepeningAlgorithm) searchAlgorithms[selectedAlgorithmIndex]).getCurrentDepth());
				textRenderer.renderText(screen, currentDepthText, Maze.WIDTH + 30,
						yOffset1RightPanel + yOffsetRightPanel + 25, true);

			} else if (selectedAlgorithmIndex == SearchAlgorithm.BS) {
				yOffsetRightPanel = 20;
				textRenderer.renderText(screen, beamWidthText, Maze.WIDTH + 30, yOffset1RightPanel + 45, true);
				beamWidthValueText.setTextMessage(
						"[" + ((BeamSearchAlgorithm) searchAlgorithms[selectedAlgorithmIndex]).getBeamWidth() + "]");
				textRenderer.renderText(screen, beamWidthValueText, Maze.WIDTH + 30 + beamWidthText.getWidth() + 10,
						yOffset1RightPanel + 45, true);
				decreaseBeamWidth.render(textRenderer, screen);
				increaseBeamWidth.render(textRenderer, screen);
			} else if (selectedAlgorithmIndex == SearchAlgorithm.BF
					|| selectedAlgorithmIndex == SearchAlgorithm.A_STAR) {
				yOffsetRightPanel = -0;
			} else {
				yOffsetRightPanel = -15;
			}

			int yOffset = 355 + yOffsetRightPanel;
			nodesVisitedText.setTextMessage(
					"Nodes (tiles) visited: " + searchAlgorithms[selectedAlgorithmIndex].getNodesVisited());
			textRenderer.renderText(screen, nodesVisitedText, Maze.WIDTH + xOffsetRightPanel,
					yOffset1RightPanel + yOffsetRightPanel + 45, true);
			nodesExpandedText.setTextMessage(
					"Nodes (tiles) expanded: " + (searchAlgorithms[selectedAlgorithmIndex].getNodesExpanded()));
			textRenderer.renderText(screen, nodesExpandedText, Maze.WIDTH + xOffsetRightPanel,
					yOffset1RightPanel + yOffsetRightPanel + 65, true);
			solutionPathLengthInTiles.setTextMessage(
					"Step: " + searchAlgorithms[selectedAlgorithmIndex].getPathInPoints().size() + " tiles");
			textRenderer.renderText(screen, solutionPathLengthInTiles, Maze.WIDTH + xOffsetRightPanel,
					yOffset1RightPanel + yOffsetRightPanel + 85, true);
			if (Game.getActiveSubMode() == SubMode.CUSTOM_SPACE_PATHFINDING) {
				DecimalFormat df = new DecimalFormat("0.000");
				String gCost = df.format(searchAlgorithms[selectedAlgorithmIndex].getTotalGCost());
				solutionPathLengthInPixels.setTextMessage("Actual Distance: " + gCost + " pxs");
				textRenderer.renderText(screen, solutionPathLengthInPixels, Maze.WIDTH + xOffsetRightPanel,
						yOffset1RightPanel + yOffsetRightPanel + 105, true);
				yOffset += 20;
			}

			textRenderer.renderText(screen, pacmanInfoText, Maze.WIDTH + xOffsetRightPanel, yOffset, true);
			tilesTraveledText.setTextMessage("Total tiles traveled: " + tilesTraveled);
			textRenderer.renderText(screen, tilesTraveledText, Maze.WIDTH + xOffsetRightPanel, yOffset + 25, true);
			// distanceTraveledText.setTextMessage("Total distance traveled:\n " +
			// pacman.getDistanceMoved() + "
			// pixels");
			// textRenderer.renderText(screen, distanceTraveledText, Maze.WIDTH +
			// xOffsetRightPanel, yOffset + 40,
			// true);

			textRenderer.renderText(screen, description,
					Screen.getMiddleAlignStartPositionInWindow(description.getWidth()),
					GameApplication.WINDOW_HEIGHT - 70, false);
		}
	}
}
