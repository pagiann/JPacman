package com.jpacman.view.edumode;

import java.awt.Point;

import com.jpacman.Game;
import com.jpacman.GameApplication;
import com.jpacman.controller.PillController;
import com.jpacman.model.Maze;
import com.jpacman.model.Pacman;
import com.jpacman.model.Text;
import com.jpacman.model.UIButton;
import com.jpacman.model.edumode.TSPSolver;
import com.jpacman.view.TextRenderer;
import com.jpacman.view.graphics.Screen;

public class TSPSolverRenderer extends EducationalModeRenderer {
	// ********************* Class (static) variables ********************** //
	// ************************** Constants ************************** //
	private static final String TITLE_STRING = "Visualization of the Traveling Salesman Problem";
	private static final String EMPTY_DESCRIPTION_STRING = "";
	private static final String DESCRIPTION_STRING_1 = "" + //
			"     In the classic TSP, pacman must eat all pills using the shortest route possible,\n" + //
			"by travelling to each pill's location exactly once and returning to its original location\n" + //
			"                        (all pills are connecting to each other)";
	private static final String DESCRIPTION_STRING_2 = "" + //
			"In this variation, pacman must eat all pills in the maze using the shortest path possible\n" + //
			"                             (without any extra restrictions)";
	private static final String WAIT_STRING = "" + //
			"It may take a while,\n" + //
			"  depending on the\n" + //
			"   number of pills\n" + //
			" and their positions";
	// *************************************************************** //

	public static final UIButton toggleVisuals = new UIButton(
			new Point(GameApplication.X_OFFSET + Maze.WIDTH + 30, GameApplication.Y_OFFSET + 30), false,
			new Text("Toggle Visuals", Text.DEFAULT_FONT_SMALL), true);
	public static final UIButton[] tspVariations = new UIButton[TSPSolver.NUM_OF_VARIATIONS + 1];
	public static final String[] descriptions = new String[TSPSolver.NUM_OF_VARIATIONS + 1];
	private static final Text variationSelection = new Text("Select a TSP variation", Text.DEFAULT_FONT_LARGE,
			Screen.YELLOW_COLOR);
	private static final Text classicVariation = new Text("Classic TSP", Text.DEFAULT_FONT_LARGE);
	private static final Text customVariation = new Text("Maze TSP", Text.DEFAULT_FONT_LARGE);
	private static final UIButton classicTSP = new UIButton(new Point(Maze.WIDTH, Maze.HEIGHT / 2 + 15), true,
			classicVariation, true);
	private static final UIButton customTSP = new UIButton(new Point(Maze.WIDTH, (Maze.HEIGHT / 2) + 65), true,
			customVariation, true);
	private static final String startComputing = "   Right click\nto start computing";
	private static final String computing = "   Algorithm is\n    running...";
	private static final String startEating = "   Right click\n to start eating";
	private static final String eating = "    Pacman is\n     eating...";
	private static final String done = "       Done!";
	private static final Text togglePill = new Text("  Left click\nto toggle pill", Text.DEFAULT_FONT_MEDIUM, 0xFF9933);
	private static final Text wait = new Text(WAIT_STRING, Text.DEFAULT_FONT_SMALL, 0xFFFF99);
	private static final Text selectedAlgorithmText = new Text(
			"=> Custom A* algorithm with\n   custom heuristic function", Text.DEFAULT_FONT_SMALL, 0x66FFFF);

	// static initializer
	static {
		descriptions[TSPSolver.CLASSIC] = DESCRIPTION_STRING_1;
		descriptions[TSPSolver.MAZE] = DESCRIPTION_STRING_2;
		descriptions[TSPSolver.NUM_OF_VARIATIONS] = EMPTY_DESCRIPTION_STRING;
		tspVariations[TSPSolver.CLASSIC] = classicTSP;
		tspVariations[TSPSolver.MAZE] = customTSP;
		tspVariations[TSPSolver.NUM_OF_VARIATIONS] = backButton;
	}

	// ************************* Instance variables ************************ //
	private final TSPSolver tspSolver;
	private final PillController pillController;

	public TSPSolverRenderer(TextRenderer textRenderer, Pacman pacman, TSPSolver tspSolver,
			PillController pillController) {
		super(textRenderer, pacman);
		this.tspSolver = tspSolver;
		this.pillController = pillController;
		title.setTextMessage(TITLE_STRING);
		description.setTextMessage(EMPTY_DESCRIPTION_STRING);
		clickToStart.setTextMessage(startComputing);

		toggleGrid = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 30, GameApplication.Y_OFFSET + 40),
				false, new Text("Toggle Grid", Text.DEFAULT_FONT_SMALL), true);
		minPacmanSpeed = new UIButton(
				new Point(GameApplication.X_OFFSET + Maze.WIDTH + 70, GameApplication.Y_OFFSET + 90), false,
				new Text("min", Text.DEFAULT_FONT_MEDIUM), true);
		maxPacmanSpeed = new UIButton(
				new Point(GameApplication.X_OFFSET + Maze.WIDTH + 230, GameApplication.Y_OFFSET + 90), false,
				new Text("max", Text.DEFAULT_FONT_MEDIUM), true);
		decreasePacmanSpeed = new UIButton(
				new Point(GameApplication.X_OFFSET + Maze.WIDTH + 145, GameApplication.Y_OFFSET + 90), false,
				new Text("-", Text.DEFAULT_FONT_MEDIUM), true);
		increasePacmanSpeed = new UIButton(
				new Point(GameApplication.X_OFFSET + Maze.WIDTH + 185, GameApplication.Y_OFFSET + 90), false,
				new Text("+", Text.DEFAULT_FONT_MEDIUM), true);
	}

	public void renderVariationTextStrings(Screen screen) {
		textRenderer.renderText(screen, variationSelection,
				Screen.getMiddleAlignStartPositionInArea(Maze.WIDTH, variationSelection.getWidth()),
				GameApplication.WINDOW_HEIGHT / 3, true);

		for (int i = 0; i < tspVariations.length; i++) {
			if (tspVariations[i].isVisible()) {
				if (tspVariations[i].hasFocus() || tspVariations[i].isMouseHovered()) {
					screen.renderBounds(tspVariations[i].getBounds(), Screen.WHITE_COLOR, -1, 0, 8, 4, false);
					tspVariations[i].getText().setColor(Screen.YELLOW_COLOR);
					description.setTextMessage(descriptions[i]);
				} else {
					tspVariations[i].getText().setColor(Screen.WHITE_COLOR);
				}
				textRenderer.renderText(screen, tspVariations[i].getText(), tspVariations[i].getBounds().x,
						tspVariations[i].getBounds().y, false);
			}
		}
	}

	@Override
	public void render(Screen screen) {
		textRenderer.renderText(screen, title, Screen.getMiddleAlignStartPositionInWindow(title.getWidth()),
				title.getHeight(), false);

		if (Game.getActiveSubMode() == Game.SubMode.TSP_MENU) {
			// center align back menu item
			backButton.getBounds().x = Screen.getMiddleAlignStartPositionInWindow(backButton.getText().getWidth());
			renderVariationTextStrings(screen);
			textRenderer.renderText(screen, description,
					Screen.getMiddleAlignStartPositionInWindow(description.getWidth()),
					GameApplication.WINDOW_HEIGHT - 70, false);
		} else {
			// left align back menu item
			backButton.getBounds().x = 115;

			if (!tspSolver.isStarted()) {
				clickToStart.setTextMessage(startComputing);
			} else if (tspSolver.isStarted() && tspSolver.isRunning()) {
				clickToStart.setTextMessage(computing);
			} else if (tspSolver.isFinished()) {
				if (pacman.isMoving()) {
					clickToStart.setTextMessage(eating);
				} else if (pillController.getPills().size() == 0) {
					clickToStart.setTextMessage(done);
				} else {
					clickToStart.setTextMessage(startEating);
				}
			}

			textRenderer.renderText(screen, clickToStart, 25, GameApplication.Y_OFFSET + 10, false);

			if (!tspSolver.isStarted()) {
				textRenderer.renderText(screen, togglePill, 50, GameApplication.Y_OFFSET + 100, false);
			}

			if (tspSolver.isRunning()) {
				textRenderer.renderText(screen, wait, 60, GameApplication.Y_OFFSET + 100, false);
			}

			int xOffset = 30;
			int yOffset1 = 150;
			int yOffset2 = 350;
			// if (tspSolver.getSelectedVariation() == TSPSolver.CLASSIC) {
			// toggleVisuals.changeUpperLeftCorner(new Point(GameApplication.X_OFFSET +
			// Maze.WIDTH + 30,
			// GameApplication.Y_OFFSET + 30), false);
			// } else {
			toggleVisuals.moveBoundsLocation(
					new Point(GameApplication.X_OFFSET + Maze.WIDTH + 30, GameApplication.Y_OFFSET + 10), false);
			toggleGrid.render(textRenderer, screen);
			// }
			toggleVisuals.render(textRenderer, screen);

			textRenderer.renderText(screen, pacmanSpeedText, Maze.WIDTH + 30, 70, true);
			pacmanSpeedValueText.setTextMessage("[" + String.valueOf((int) pacman.getMovingSpeed()) + "]");
			textRenderer.renderText(screen, pacmanSpeedValueText, Maze.WIDTH + 30 + pacmanSpeedText.getWidth() + 10, 70,
					true);
			minPacmanSpeed.render(textRenderer, screen);
			decreasePacmanSpeed.render(textRenderer, screen);
			increasePacmanSpeed.render(textRenderer, screen);
			maxPacmanSpeed.render(textRenderer, screen);

			textRenderer.renderText(screen, algorithmInfoText, Maze.WIDTH + xOffset, yOffset1, true);
			textRenderer.renderText(screen, selectedAlgorithmText, Maze.WIDTH + xOffset, yOffset1 + 25, true);

			float finalGCost = tspSolver.getFinalGCost() - Maze.TILE;
			if (!tspSolver.isStarted()) {
				finalGCost += Maze.TILE;
			}
			String pathLength = "Total distance:\n " + finalGCost + " pixels";
			if (tspSolver.getSelectedVariation() == TSPSolver.MAZE) {
				renderLeftSidebar(screen);
				int gCostInTiles = (int) (finalGCost / Maze.TILE);
				pathLength = "Total distance: " + gCostInTiles + " tiles";
			} else {
				backCancelButton.render(textRenderer, screen);
			}
			resetButton.render(textRenderer, screen);
			randomizeButton.render(textRenderer, screen);

			textRenderer.renderText(screen, nodesVisitedText, Maze.WIDTH + xOffset, yOffset1 + 70, true);
			nodesExpandedText.setTextMessage("Nodes expanded: " + tspSolver.getNodesExpanded());
			textRenderer.renderText(screen, nodesExpandedText, Maze.WIDTH + xOffset, yOffset1 + 95, true);
			secondsElapsedText.setTextMessage("Seconds elapsed: " + tspSolver.getSecondsElapsed());
			textRenderer.renderText(screen, secondsElapsedText, Maze.WIDTH + xOffset, yOffset1 + 120, true);
			solutionPathLengthInTiles.setTextMessage(pathLength);
			textRenderer.renderText(screen, solutionPathLengthInTiles, Maze.WIDTH + xOffset, yOffset1 + 145, true);
			nodesVisitedText.setTextMessage("Nodes visited: " + tspSolver.getNodesVisited());

			textRenderer.renderText(screen, pacmanInfoText, Maze.WIDTH + xOffset, yOffset2, true);
			if (tspSolver.getSelectedVariation() == TSPSolver.MAZE) {
				tilesTraveledText.setTextMessage("Tiles traveled: " + tilesTraveled);
				textRenderer.renderText(screen, tilesTraveledText, Maze.WIDTH + xOffset, yOffset2 + 25, true);
				uneatenPillsText.setTextMessage("Pills eaten: " + pillController.getPillsEaten());
				textRenderer.renderText(screen, uneatenPillsText, Maze.WIDTH + xOffset, yOffset2 + 55, true);
			} else {
				distanceTraveledText.setTextMessage(
						"Distance traveled:\n " + ((pacman.getDistanceMoved() == 0) ? pacman.getDistanceMoved()
								: pacman.getDistanceMoved() - Maze.HALF_TILE) + " pixels");
				textRenderer.renderText(screen, distanceTraveledText, Maze.WIDTH + xOffset, yOffset2 + 25, true);
				uneatenPillsText.setTextMessage("Pills eaten: " + pillController.getPillsEaten());
				textRenderer.renderText(screen, uneatenPillsText, Maze.WIDTH + xOffset, yOffset2 + 70, true);
			}

			textRenderer.renderText(screen, description,
					Screen.getMiddleAlignStartPositionInWindow(description.getWidth()),
					GameApplication.WINDOW_HEIGHT - 70, false);
		}
	}
}
