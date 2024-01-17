package com.jpacman.controller.edumode;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import com.jpacman.Game;
import com.jpacman.GameApplication;
import com.jpacman.controller.MenuController;
import com.jpacman.controller.PillController;
import com.jpacman.input.Mouse;
import com.jpacman.model.Level;
import com.jpacman.model.Maze;
import com.jpacman.model.Pacman;
import com.jpacman.model.Pill;
import com.jpacman.model.PowerPill;
import com.jpacman.model.SimplePill;
import com.jpacman.model.edumode.SearchAlgorithm;
import com.jpacman.model.edumode.TSPSolver;
import com.jpacman.util.PathFinder;
import com.jpacman.view.TextRenderer;
import com.jpacman.view.edumode.EducationalModeRenderer;
import com.jpacman.view.edumode.TSPSolverRenderer;
import com.jpacman.view.graphics.Screen;

public class TSPSolverController extends EducationalModeController implements Runnable {
	public static final int MIN_NUM_OF_RANDOM_POWER_PILLS = 10;
	public static final int MIN_NUM_OF_RANDOM_SIMPLE_PILLS = 20;
	public static final int MAX_RANDOM_BOUND_1 = 15;
	public static final int MAX_RANDOM_BOUND_2 = 20;

	public static boolean renderVisuals = true;

	private final TSPSolver tspSolver;
	private final TSPSolverRenderer tspSolverRenderer;
	private final PillController pillController;
	private final Random random;
	private Thread algorithmThread;
	private Point2D.Float nextPill;

	public TSPSolverController(TextRenderer textRenderer) {
		super();
		tspSolver = new TSPSolver();
		pillController = new PillController();
		tspSolverRenderer = new TSPSolverRenderer(textRenderer, pacman, tspSolver, pillController);
		educationalModeRenderer = tspSolverRenderer;
		random = new Random();
	}

	@Override
	public void update(double delta) {
		if (tspSolver.getSelectedVariation() == TSPSolver.NONE) {
			checkIfAVariationIsSelected();
		} else {
			if (tspSolver.getSelectedVariation() == TSPSolver.MAZE && !tspSolver.isRunning() && !pacman.isMoving()) {
				checkIfMazeThumbnailIsSelected(pillController);
			}

			if (isResetButtonClicked(tspSolver.isFinished())) {
				tspSolver.initialize();
				EducationalModeRenderer.resetButton.setVisible(false);
				tspSolverRenderer.setTilesTraveled(0);
				pillController.setPillsEaten(0);
			}

			if (!tspSolver.isRunning() && !pacman.isMoving()) {
				if (MenuController.isButtonClicked(TSPSolverRenderer.randomizeButton)) {
					EducationalModeRenderer.resetButton.setClicked(true);
					tspSolver.initialize();
					createAndSetRandomPills();
					tspSolverRenderer.setTilesTraveled(0);
					pillController.setPillsEaten(0);
				}
				if (EducationalModeRenderer.resetButton.isVisible()) {
					EducationalModeRenderer.randomizeButton.setVisible(true);
				}
			} else {
				EducationalModeRenderer.randomizeButton.setVisible(false);
			}

			if (isBackCancelButtonClicked()) {
				terminateThread();
				EducationalModeRenderer.resetButton.setVisible(false);
				EducationalModeRenderer.randomizeButton.setVisible(true);
				EducationalModeRenderer.backCancelButton = EducationalModeRenderer.backButton;
				if (!tspSolver.isRunning()) {
					Game.getKeyboard().returnedToMainMenu = true;
				}
			}

			if (MenuController.isButtonClicked(TSPSolverRenderer.toggleVisuals)) {
				renderVisuals = renderVisuals ? false : true;
			}

			if (MenuController.isButtonClicked(tspSolverRenderer.toggleGrid)) {
				renderGrid = renderGrid ? false : true;
			}

			checkIfPacmanSpeedButtonsIsClicked(tspSolverRenderer);

			if (!tspSolver.isStarted()) {
				if (Mouse.getButton() == Mouse.LEFT_BUTTON) {
					Mouse.setButton(-1);
					Point clickCoordinates = Mouse.getCoordinates();
					clickCoordinates.x -= GameApplication.X_OFFSET;
					clickCoordinates.y -= GameApplication.Y_OFFSET;

					if ((clickCoordinates.x > Maze.TILE && clickCoordinates.x < Maze.WIDTH - Maze.TILE) && //
							(clickCoordinates.y > Maze.TILE && clickCoordinates.y < Maze.HEIGHT - Maze.TILE)) {
						Point clickTile = new Point(PathFinder.computeCurrentTile(clickCoordinates));

						if (!clickTile.equals(PathFinder.computeCurrentTile(pacman.getPosition()))) {
							int index = pillController.getPillsPositions().indexOf(clickTile);
							if (index != -1) { // if pill exists remove it
								pillController.removePill(index);
							} else { // else add a new pill at the specified position
								if ((tspSolver.getSelectedVariation() == TSPSolver.CLASSIC)) {
									pillController.addPill(new PowerPill(clickTile, PowerPill.SIZE));
								} else if ((tspSolver.getSelectedVariation() == TSPSolver.MAZE)
										&& (routeTiles.contains(clickTile))) {
									pillController.addPill(new SimplePill(clickTile, Pill.SIZE));
								}
							}
						}
					}
				} else if ((pillController.getPills().size() > 0) && Mouse.getButton() == Mouse.RIGHT_BUTTON) {
					Mouse.setButton(-1);
					if (pillController.getPills().size() == 0) {
						System.err.println("O pills!");
						return;
					}

					tspSolver.setStarted(true);
					tspSolver.setRunning(true);
					EducationalModeRenderer.resetButton.setVisible(false);
					EducationalModeRenderer.backCancelButton = EducationalModeRenderer.cancelButton;

					algorithmThread = new Thread(this, "TSP Algorithm");
					algorithmThread.start();
				}
			} else if (tspSolver.isRunning()) {
				// stuff to do while algorithm is running
			} else if (tspSolver.isFinished()) {

				if ((tspSolver.getShortestPath() != null) && (Mouse.getButton() == Mouse.RIGHT_BUTTON)) {
					if (tspSolver.getSelectedVariation() == TSPSolver.CLASSIC) {
						Mouse.setButton(-1);
						nextPill = new Point2D.Float(tspSolver.getShortestPath().get(0).x,
								tspSolver.getShortestPath().get(0).y);
						computeAngle(nextPill);
						pacman.setMoving(true);
					} else {
						Mouse.setButton(-1);
						destination = tspSolver.getShortestPath().get(tspSolver.getShortestPath().size() - 1);
						pacman.setDistanceMoved(0);
						tilesTraveled = 0;
						pacman.setNextTile(tspSolver.getShortestPath().remove(0));
						pacman.computeNewDirection();
						pacman.setMoving(true);
					}
				}

				if (pacman.isMoving()) {
					EducationalModeRenderer.resetButton.setVisible(false);
					pacman.addDelta((pacman.getMovingSpeed() / GameApplication.UPDATES_PER_SECOND) * delta);
					if (pacman.getDelta() >= 1) { // ups capped at object's current moving speed
						movePacmanToNextTile();
						tspSolverRenderer.setTilesTraveled(tilesTraveled);
						pacman.decreaseDeltaByOne();
					}
				} else {
					EducationalModeRenderer.resetButton.setVisible(true);
				}
				EducationalModeRenderer.backCancelButton = EducationalModeRenderer.backButton;
			}
		}
	}

	@Override
	protected void checkIfMazeThumbnailIsSelected(PillController pillController) {
		for (int i = 0; i < Maze.educationalModeMazes.length; i++) {
			if (mazeThumbnailsBounds[i].getBounds().contains(Mouse.getCoordinates())) {
				educationalModeRenderer.setHoveredMazeThumbnails(i, true);
				if (Mouse.getButton() == Mouse.LEFT_BUTTON) {
					levelController.getLevel().setMaze(Maze.educationalModeMazes[i]);
					mazeRenderer.setMaze(levelController.getLevel().getMaze());
					routeTiles = levelController.getLevel().getMaze().getRouteTiles();
					levelController.initializeLevel(pacman);
					pacman.setMovingSpeed(pacmanSpeeds[pacmanSpeedIndex]);
					createAndSetRandomPills();
					tspSolver.initialize();
					pillController.setPillsEaten(0);
					SearchAlgorithm.expandedNodesDrawable.clear();
					educationalModeRenderer.setTilesTraveled(0);
					EducationalModeRenderer.resetButton.setVisible(false);
				}
			} else {
				educationalModeRenderer.getHoveredMazeThumbnails()[i] = false;
			}
		}
	}

	private void checkIfAVariationIsSelected() {
		for (int i = 0; i < TSPSolverRenderer.tspVariations.length; i++) {
			if (TSPSolverRenderer.tspVariations[i].getBounds().contains(Mouse.getCoordinates())) {
				TSPSolverRenderer.tspVariations[i].setMouseHovered(true);
				if (Mouse.getButton() == Mouse.LEFT_BUTTON) {
					Mouse.setButton(-1);
					// if "Back" menu item was clicked
					if (i == TSPSolverRenderer.tspVariations.length - 1) {
						Game.getKeyboard().returnedToMainMenu = true;
					} else {
						TSPSolverRenderer.tspVariations[i].setClicked(true);
						tspSolver.setSelectedVariation(i);
						setDataFromSelectedVariation();
					}
				}
			} else {
				TSPSolverRenderer.tspVariations[i].setMouseHovered(false);
			}
		}
	}

	private void setDataFromSelectedVariation() {
		switch (tspSolver.getSelectedVariation()) {
			case TSPSolver.CLASSIC:
				Game.setActiveSubMode(Game.SubMode.CLASSIC_TSP);
				angle = 2;
				break;
			case TSPSolver.MAZE:
				Game.setActiveSubMode(Game.SubMode.MAZE_TSP);
				routeTiles = levelController.getLevel().getMaze().getRouteTiles();
				mazeThumbnailsBounds = tspSolverRenderer.getMazeThumbnailsBounds();
				break;
			default:
				Game.setActiveSubMode(Game.SubMode.TSP_MENU);
		}
		// initialize level data
		levelController.setLevel(new Level(0));
		mazeRenderer.setMaze(levelController.getLevel().getMaze());
		levelController.initializeLevel(pacman);
		if (tspSolver.getSelectedVariation() == TSPSolver.CLASSIC) {
			pacman.getPosition().translate(Maze.HALF_TILE, 0);
			pacman.setStartingPosition(pacman.getPosition());
			pacman.setPresicePosition(new Point.Float(pacman.getStartingPosition().x, pacman.getStartingPosition().y));
		}
		pacman.setMovingSpeed(pacmanSpeeds[pacmanSpeedIndex]);
		pacman.setAnimationSpeed(Pacman.NORMAL_ANIMATION_SPEED);
		createAndSetRandomPills();
	}

	private void createAndSetRandomPills() {
		if (tspSolver.getSelectedVariation() == TSPSolver.CLASSIC) {
			int numberOfPowerPills = MIN_NUM_OF_RANDOM_POWER_PILLS + random.nextInt(MAX_RANDOM_BOUND_1);
			int offset = Maze.HALF_TILE;
			ArrayList<Point> powerPillsPositions = new ArrayList<Point>(numberOfPowerPills);
			ArrayList<Pill> powerPills = new ArrayList<Pill>(numberOfPowerPills);
			while (powerPillsPositions.size() < numberOfPowerPills) {
				Point nextRandomPowerPillPosition = new Point((1 + random.nextInt(25)) * Maze.TILE + offset,
						(1 + random.nextInt(28)) * Maze.TILE + offset);
				if (!nextRandomPowerPillPosition.equals(pacman.getPosition())
						&& !powerPillsPositions.contains(nextRandomPowerPillPosition)) {
					powerPillsPositions.add(nextRandomPowerPillPosition);
					powerPills.add(new PowerPill(nextRandomPowerPillPosition, PowerPill.SIZE));
				}
			}

			// set pills in pillController
			pillController.createPills(powerPills);
		}

		if (tspSolver.getSelectedVariation() == TSPSolver.MAZE) {
			Collections.shuffle(routeTiles);
			int numberOfSimplePills = MIN_NUM_OF_RANDOM_SIMPLE_PILLS + random.nextInt(MAX_RANDOM_BOUND_2);

			ArrayList<Pill> simplePills = new ArrayList<Pill>(numberOfSimplePills);
			for (int i = 0; i < numberOfSimplePills; i++) {
				if (!routeTiles.get(i).equals(PathFinder.computeCurrentTile(pacman.getPosition()))) {
					simplePills.add(new SimplePill(routeTiles.get(i), Pill.SIZE));
				}
			}

			// set pills in pillController
			pillController.createPills(simplePills);
		}
	}

	private void movePacmanToNextTile() {
		if (tspSolver.getSelectedVariation() == TSPSolver.CLASSIC) {
			int animationCounter = pacman.getAnimationCounter();
			pacman.setAnimationCounter((animationCounter < 10000) ? animationCounter + 1 : 0);

			Point currentPacmanPosition = new Point((int) Math.rint(pacman.getPresicePosition().x),
					(int) Math.rint(pacman.getPresicePosition().y));
			if (currentPacmanPosition.equals(tspSolver.getShortestPath().get(0))) {
				nextPill = new Point2D.Float(tspSolver.getShortestPath().get(1).x,
						tspSolver.getShortestPath().get(1).y);
				tspSolver.getShortestPath().remove(0);

				int index = pillController.getPillsPositions().indexOf(currentPacmanPosition);
				pillController.removePill(index);
				pillController.increasePillsEatenByOne();

				computeAngle(nextPill);
			}

			double newAngle = Math.atan2(nextPill.y - pacman.getPresicePosition().y,
					nextPill.x - pacman.getPresicePosition().x);
			pacman.getPresicePosition().x += Math.cos(newAngle);
			pacman.getPresicePosition().y += Math.sin(newAngle);

			if (Math.rint(pacman.getPresicePosition().x) == pacman.getStartingPosition().x
					&& Math.rint(pacman.getPresicePosition().y) == pacman.getStartingPosition().y) {
				pacman.setMoving(false);
			}
		} else {
			movePacman(tspSolver.getShortestPath());

			Point currentPacmanPosition = pacman.getPosition();
			if (pillController.getPillsPositions().contains(currentPacmanPosition)) {
				int index = pillController.getPillsPositions().indexOf(currentPacmanPosition);
				pillController.removePill(index);
				pillController.increasePillsEatenByOne();
			}

			if (pacman.getPosition().equals(destination)) {
				pacman.setMoving(false);
			}
		}
	}

	@Override
	public void run() {
		start = new Point(PathFinder.computeCurrentTile(pacman.getPosition()));
		tspSolver.computeShortestPath(pillController.getPillsPositions(), routeTiles, start);
		tspSolver.setRunning(false);
		tspSolver.setFinished(true);
	}

	private void terminateThread() {
		tspSolver.setCanceled(true);
		if (algorithmThread != null && algorithmThread.isAlive()) {
			algorithmThread.interrupt();
			algorithmThread = null;
		}
	}

	public void renderAll(Screen screen) {
		tspSolverRenderer.render(screen);
		renderHoveredMazeTileLocation = tspSolver.isStarted() ? false : true;

		if (tspSolver.getSelectedVariation() != TSPSolver.NONE) {
			mazeRenderer.render(screen);
			// if the hovered maze tile location info text must be above everything else
			// render in this order
			if (renderHoveredMazeTileLocation) {
				pillController.renderPills(screen);
				pacmanController.renderPacman(screen);
				screen.renderScreenPainterVisuals(renderGrid, renderHoveredMazeTileLocation);
			} else { // else render in this order
				screen.renderScreenPainterVisuals(renderGrid, renderHoveredMazeTileLocation);
				pillController.renderPills(screen);
				pacmanController.renderPacman(screen);
			}
		}
	}
}
