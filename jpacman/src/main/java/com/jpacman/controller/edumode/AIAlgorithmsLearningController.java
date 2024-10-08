package com.jpacman.controller.edumode;

import com.jpacman.Game;
import com.jpacman.controller.LevelController;
import com.jpacman.controller.ghost.GhostController;
import com.jpacman.controller.pacman.PacmanMoveController;
import com.jpacman.input.Mouse;
import com.jpacman.model.Level;
import com.jpacman.model.ghost.Blinky;
import com.jpacman.model.ghost.Clyde;
import com.jpacman.model.ghost.Ghost;
import com.jpacman.model.ghost.Inky;
import com.jpacman.model.ghost.Pinky;
import com.jpacman.view.MazeRenderer;
import com.jpacman.view.TextRenderer;
import com.jpacman.view.edumode.AIAlgorithmsLearningRenderer;
import com.jpacman.view.ghost.BlinkyRenderer;
import com.jpacman.view.ghost.ClydeRenderer;
import com.jpacman.view.ghost.InkyRenderer;
import com.jpacman.view.ghost.PinkyRenderer;
import com.jpacman.view.graphics.Screen;
import com.jpacman.view.graphics.Sprite;

public class AIAlgorithmsLearningController extends EducationalModeController {
	public static final int NUMBER_OF_SUBMODES = 2;
	public static final int NONE = -1;
	public static final int STEPWISE_EXECUTION = 0;
	public static final int INTERACTIVE_EXECUTION = 1;
	public static int submode = NONE;

	static {
		graphicalSearchTreeController = new GraphicalSearchTreeController();
	}

	private final AIAlgorithmsLearningRenderer aiAlgorithmsLearningRenderer;
	protected PacmanMoveController pacmanMoveController;
	protected Ghost[] ghosts;
	protected GhostController[] ghostsControllers;
	protected Thread algorithmThread;

	public AIAlgorithmsLearningController(TextRenderer textRenderer) {
		pacmanMoveController = new PacmanMoveController(pacman, Game.getKeyboard());

		ghosts = new Ghost[Game.NUMBER_OF_GHOSTS];
		ghosts[0] = new Blinky();
		ghosts[1] = new Pinky();
		ghosts[2] = new Inky();
		ghosts[3] = new Clyde();
		ghostsControllers = new GhostController[Game.NUMBER_OF_GHOSTS];
		ghostsControllers[0] = new GhostController(ghosts[0],
				new BlinkyRenderer((Blinky) ghosts[0], Sprite.HALF_SPRITE_SIZE));
		ghostsControllers[1] = new GhostController(ghosts[1],
				new PinkyRenderer((Pinky) ghosts[1], Sprite.HALF_SPRITE_SIZE));
		ghostsControllers[2] = new GhostController(ghosts[2],
				new InkyRenderer((Inky) ghosts[2], Sprite.HALF_SPRITE_SIZE));
		ghostsControllers[3] = new GhostController(ghosts[3],
				new ClydeRenderer((Clyde) ghosts[3], Sprite.HALF_SPRITE_SIZE));

		levelController = new LevelController(new Level(0));
		levelController.initializeLevel(pacman, ghosts);
		mazeRenderer = new MazeRenderer(levelController.getLevel().getMaze());
		routeTiles = levelController.getLevel().getMaze().getRouteTiles();

		aiAlgorithmsLearningRenderer = new AIAlgorithmsLearningRenderer(textRenderer, pacman, searchAlgorithms);
		educationalModeRenderer = aiAlgorithmsLearningRenderer;
	}

	@Override
	public void update(double delta) {
		if (submode == NONE) {
			checkWhichSubmodeIsSelected();
		}
	}

	private void checkWhichSubmodeIsSelected() {
		for (int i = 0; i < AIAlgorithmsLearningRenderer.modes.length; i++) {
			if (AIAlgorithmsLearningRenderer.modes[i].getBounds().contains(Mouse.getCoordinates())) {
				AIAlgorithmsLearningRenderer.modes[i].setMouseHovered(true);
				if (Mouse.getButton() == Mouse.LEFT_BUTTON) {
					Mouse.setButton(-1);
					// if "Back" menu item was clicked
					if (i == AIAlgorithmsLearningRenderer.modes.length - 1) {
						Game.getKeyboard().returnedToMainMenu = true;
					} else {
						AIAlgorithmsLearningRenderer.modes[i].setClicked(true);
						submode = i;
						setDataFromSelectedVariation();
					}
				}
			} else {
				AIAlgorithmsLearningRenderer.modes[i].setMouseHovered(false);
				AIAlgorithmsLearningRenderer.modes[i].setMouseHovered(false);
			}
		}
	}

	private void setDataFromSelectedVariation() {
		switch (submode) {
			case STEPWISE_EXECUTION:
				Game.setActiveSubMode(Game.SubMode.AI_ALGORITHMS_STEPWISE_LEARNING);
				break;
			case INTERACTIVE_EXECUTION:
				Game.setActiveSubMode(Game.SubMode.AI_ALGORITHMS_INTERACTIVE_LEARNING);
				break;
			default:
				System.err.println("submode not supported!");
		}
	}

	public void renderAll(Screen screen) {
		aiAlgorithmsLearningRenderer.render(screen);
	}
}
