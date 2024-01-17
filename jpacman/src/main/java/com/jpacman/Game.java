package com.jpacman;

import java.awt.Point;

import com.jpacman.controller.BonusPointsController;
import com.jpacman.controller.FruitController;
import com.jpacman.controller.GameStatusController;
import com.jpacman.controller.IntroAnimationController;
import com.jpacman.controller.LevelController;
import com.jpacman.controller.MenuController;
import com.jpacman.controller.PillController;
import com.jpacman.controller.ScoreController;
import com.jpacman.controller.StatusController;
import com.jpacman.controller.TextController;
import com.jpacman.controller.edumode.AIAlgorithmsInteractiveLearningController;
import com.jpacman.controller.edumode.AIAlgorithmsLearningController;
import com.jpacman.controller.edumode.AIAlgorithmsStepwiseLearningController;
import com.jpacman.controller.edumode.GraphicalSearchTreeController;
import com.jpacman.controller.edumode.PathfindingController;
import com.jpacman.controller.edumode.TSPSolverController;
import com.jpacman.controller.ghost.BlinkyAIController;
import com.jpacman.controller.ghost.BlinkyStatusController;
import com.jpacman.controller.ghost.ClydeAIController;
import com.jpacman.controller.ghost.GhostAIController;
import com.jpacman.controller.ghost.GhostController;
import com.jpacman.controller.ghost.GhostStatusController;
import com.jpacman.controller.ghost.InkyAIController;
import com.jpacman.controller.ghost.PinkyAIController;
import com.jpacman.controller.pacman.PacmanCollisionController;
import com.jpacman.controller.pacman.PacmanController;
import com.jpacman.controller.pacman.PacmanMoveController;
import com.jpacman.controller.pacman.PacmanStatusController;
import com.jpacman.input.Keyboard;
import com.jpacman.model.Fruit;
import com.jpacman.model.Level;
import com.jpacman.model.Maze;
import com.jpacman.model.Pacman;
import com.jpacman.model.edumode.SearchAlgorithm;
import com.jpacman.model.ghost.Blinky;
import com.jpacman.model.ghost.Clyde;
import com.jpacman.model.ghost.Ghost;
import com.jpacman.model.ghost.Inky;
import com.jpacman.model.ghost.Pinky;
import com.jpacman.util.Timer;
import com.jpacman.view.FruitRenderer;
import com.jpacman.view.MazeRenderer;
import com.jpacman.view.MenuRenderer;
import com.jpacman.view.PacmanRenderer;
import com.jpacman.view.ScoreRenderer;
import com.jpacman.view.StatusRenderer;
import com.jpacman.view.TextRenderer;
import com.jpacman.view.ghost.BlinkyRenderer;
import com.jpacman.view.ghost.ClydeRenderer;
import com.jpacman.view.ghost.InkyRenderer;
import com.jpacman.view.ghost.PinkyRenderer;
import com.jpacman.view.graphics.Screen;
import com.jpacman.view.graphics.ScreenPainter;
import com.jpacman.view.graphics.Sprite;

public class Game
{
    public enum Mode {
	MENU, PLAY, EDUCATIONAL;
    }

    public enum SubMode {
	NONE, TSP_MENU, CLASSIC_TSP, MAZE_TSP, //
	PATHFINDING_MENU, CUSTOM_SPACE_PATHFINDING, MAZE_PATHFINDING, //
	AI_ALGORITHMS_LEARNING_MENU, AI_ALGORITHMS_STEPWISE_LEARNING, AI_ALGORITHMS_INTERACTIVE_LEARNING;
    }

    // ********************* Class (static) variables ********************** //
    public static final int NUMBER_OF_GHOSTS = 4;

    private static Mode activeMode = Mode.MENU;
    private static SubMode activeSubMode = SubMode.NONE;

    // used only in Play mode
    public static boolean started;
    public static boolean ready;
    public static boolean paused;
    public static boolean forcedPaused;
    public static boolean pacmanDying;
    public static boolean gameOver;
    public static boolean completedLevel;
    public static boolean quiting;
    public static boolean returnedToMainMenu;
    public static boolean freezed;
    public static int ghostEatenIndex = -1;

    private static double delta = 0;
    private static int updates = 0;

    private static Timer gameOverTimer = new Timer();

    private static Keyboard keyboard;
    // current level
    private static Level level;
    private static MazeRenderer mazeRenderer;
    // game objects
    private static Ghost[] ghosts;
    private static Pacman pacman;
    private static Fruit currentFruit;

    private static MenuController menuController;
    private static IntroAnimationController introAnimationController;

    private static TextController textController;
    private static TextRenderer textRenderer;

    private static LevelController levelController;

    private static PillController pillController;

    private static GhostController[] ghostsControllers;
    private static GhostAIController[] ghostsAIControllers;
    private static GhostStatusController[] ghostsStatusControllers;

    private static PacmanController pacmanController;
    private static PacmanMoveController pacmanMoveController;
    private static PacmanCollisionController pacmanCollisionController;
    private static PacmanStatusController pacmanStatusController;

    private static FruitController fruitController;
    private static BonusPointsController bonusPointsController;
    private static ScoreController scoreController;
    private static StatusController statusController;

    private static GameStatusController gameStatusController;

    // educational mode controllers
    private static PathfindingController pathfindingController;
    private static AIAlgorithmsLearningController aiAlgorithmsLearningController;
    private static AIAlgorithmsStepwiseLearningController aiAlgorithmsStepwiseLearningController;
    private static AIAlgorithmsInteractiveLearningController aiAlgorithmsInteractiveLearningController;
    private static TSPSolverController tspSolverController;
    // /////////////////////////////////////////////////////////////////

    public Game(Keyboard keyboard)
    {
	Game.keyboard = keyboard;
	// create game objects
	initializePOJOs();
	// create the textRenderer which is used by many controllers
	textRenderer = new TextRenderer(level.getMaze().getTextMessageTile());
	// create menu controller
	menuController = new MenuController(keyboard, new MenuRenderer(textRenderer));
	introAnimationController = new IntroAnimationController();
	SoundEffect.init();
	ScoreController.readHighScoresFromDisk();

	// ScreenPainter.maze = Game.getLevel().getMaze();

	Screen.decreaseBrightnessOfSingleColor(0x99ccff, 50);
    }

    private void createNewPlayModeControllers(boolean firstPlay)
    {
	// create level controller
	levelController = new LevelController(level);
	// create the maze renderer
	mazeRenderer = new MazeRenderer(level.getMaze());
	// create pills controller (with renderers)
	pillController = new PillController();
	// initialize level data
	levelController.initializeLevel(pillController, pacman, ghosts, currentFruit);

	// create text controller for all the text message that appear in game
	textController = new TextController(textRenderer, levelController);

	// create ghosts controllers with corresponding renderers
	ghostsControllers = new GhostController[NUMBER_OF_GHOSTS];
	ghostsControllers[0] = new GhostController(ghosts[0], new BlinkyRenderer((Blinky) ghosts[0], Sprite.HALF_SPRITE_SIZE));
	ghostsControllers[1] = new GhostController(ghosts[1], new InkyRenderer((Inky) ghosts[1], Sprite.HALF_SPRITE_SIZE));
	ghostsControllers[2] = new GhostController(ghosts[2], new PinkyRenderer((Pinky) ghosts[2], Sprite.HALF_SPRITE_SIZE));
	ghostsControllers[3] = new GhostController(ghosts[3], new ClydeRenderer((Clyde) ghosts[3], Sprite.HALF_SPRITE_SIZE));

	// create ghosts AI controllers
	ghostsAIControllers = new GhostAIController[NUMBER_OF_GHOSTS];
	ghostsAIControllers[0] = new BlinkyAIController((Blinky) ghosts[0], pacman);
	ghostsAIControllers[1] = new InkyAIController((Inky) ghosts[1], pacman);
	ghostsAIControllers[2] = new PinkyAIController((Pinky) ghosts[2], pacman);
	ghostsAIControllers[3] = new ClydeAIController((Clyde) ghosts[3], pacman);

	// create ghosts status controllers
	ghostsStatusControllers = new GhostStatusController[NUMBER_OF_GHOSTS];
	ghostsStatusControllers[0] = new BlinkyStatusController((Blinky) ghosts[0], levelController, pacman, (Clyde) ghosts[3], pillController);
	for (int i = 1; i < ghosts.length; i++) {
	    ghostsStatusControllers[i] = new GhostStatusController(ghosts[i], levelController, pacman);
	}

	if (firstPlay) {
	    // create score controller which controls score and high score
	    scoreController = new ScoreController(new ScoreRenderer(textRenderer));
	} else {
	    scoreController.setScore(0);
	}

	// create bonus points controller
	bonusPointsController = new BonusPointsController();

	// create fruit controller with renderer
	fruitController = new FruitController(currentFruit, new FruitRenderer(currentFruit, 0));

	// create pacman controllers and renderer
	pacmanController = new PacmanController(pacman, new PacmanRenderer(pacman, Sprite.HALF_SPRITE_SIZE));
	pacmanMoveController = new PacmanMoveController(pacman, keyboard);
	pacmanCollisionController = new PacmanCollisionController(pacman, ghosts, ghostsAIControllers, fruitController, pillController, levelController, scoreController, bonusPointsController);
	pacmanStatusController = new PacmanStatusController(pacman, ghostsStatusControllers, levelController);

	// create status controller (displays pacman lives and current fruit at the bottom of the screen)
	statusController = new StatusController(new StatusRenderer(new Point(Maze.TILE, Maze.HEIGHT + Maze.HALF_TILE), Sprite.SPRITE_SIZE, fruitController.getFruit().getSprite()), pacmanStatusController, fruitController);

	// finally create the game status controller
	gameStatusController = new GameStatusController(levelController, ghostsStatusControllers, pacmanStatusController, fruitController, pillController, scoreController, mazeRenderer);
    }

    private void createNewEducationalModeControllers()
    {
	// create text controller for all the text message that appear in game
	textController = new TextController(textRenderer, null);

	// educational mode controllers
	pathfindingController = new PathfindingController(textRenderer);
	aiAlgorithmsLearningController = new AIAlgorithmsLearningController(textRenderer);
	aiAlgorithmsStepwiseLearningController = new AIAlgorithmsStepwiseLearningController(textRenderer);
	aiAlgorithmsInteractiveLearningController = new AIAlgorithmsInteractiveLearningController(textRenderer);
	tspSolverController = new TSPSolverController(textRenderer);
    }

    private static void initializePOJOs()
    {
	// create first level
	level = new Level(1);

	// create ghosts POJOs
	ghosts = new Ghost[NUMBER_OF_GHOSTS];
	ghosts[0] = new Blinky();
	ghosts[1] = new Inky();
	ghosts[2] = new Pinky();
	ghosts[3] = new Clyde();

	// create pacman POJO
	pacman = new Pacman();

	// create fruit POJO
	currentFruit = new Fruit();
    }

    private void resetPlayMode()
    {
	setActiveMode(Mode.MENU);
	started = ready = paused = forcedPaused = pacmanDying = gameOver = quiting = returnedToMainMenu = freezed = false;
	ghostEatenIndex = -1;
	delta = 0;
	gameOverTimer = new Timer();

	GameStatusController.reset();
	GhostAIController.reset();
	BlinkyStatusController.blinkyWasElroyWhenPacmanDied = false;
	PacmanCollisionController.reset();
	PacmanStatusController.reset();
	BonusPointsController.timer = new Timer();
	TextController.timer = new Timer();

	initializePOJOs();
	createNewPlayModeControllers(false);
    }

    private void resetEducationalMode()
    {
	setActiveMode(Mode.MENU);
	setActiveSubMode(SubMode.NONE);
	quiting = returnedToMainMenu = false;
	delta = 0;

	SearchAlgorithm.resetDrawableVariables();
	GraphicalSearchTreeController.graphicalSearchTree = null;
	ScreenPainter.blinkyPath.clear();

	createNewEducationalModeControllers();
    }

    private void updateGameStatusFlagsAndCheckQuitting()
    {
	if (!ready) {
	    if (activeMode == Mode.PLAY) {
		paused = (keyboard.pause) ? true : false;
		if (forcedPaused) {
		    paused = false;
		}
	    }
	    quiting = (keyboard.quit) ? true : false;
	}
	returnedToMainMenu = (keyboard.quitted || keyboard.returnedToMainMenu) ? true : false;
	forcedPaused = (ready || freezed || quiting || pacmanDying || gameOver || completedLevel) ? true : false;

	if (returnedToMainMenu) {
	    keyboard.resetKeys();
	    if (activeMode == Mode.PLAY) {
		SoundEffect.SIREN.getClip().stop();
		resetPlayMode();
	    } else if (activeMode == Mode.EDUCATIONAL) {
		resetEducationalMode();
	    }
	}
    }

    private void checkIfSoundIsMuted()
    {
	if (keyboard.mute || keyboard.pause) {
	    SoundEffect.volume = SoundEffect.Volume.MUTE;
	    if (SoundEffect.SIREN.getClip().isRunning()) {
		SoundEffect.SIREN.getClip().stop();
	    }
	} else if ((!keyboard.mute && keyboard.muteToggled) || (!keyboard.pause && keyboard.pauseToggled)) {
	    SoundEffect.volume = SoundEffect.Volume.MEDIUM;
	    if (!ready && !SoundEffect.SIREN.getClip().isRunning()) {
		SoundEffect.SIREN.play();
	    }
	    if (keyboard.muteToggled) {
		keyboard.muteToggled = false;
	    }
	    if (keyboard.pauseToggled) {
		keyboard.pauseToggled = false;
	    }
	}
    }

    // updates the whole world
    // this method is being called "FPS" times per second (many times)
    public void updateWorld(double delta)
    {
	updateGameStatusFlagsAndCheckQuitting();

	if (activeMode == Mode.PLAY) {
	    introAnimationController.reset();
	    if (gameOver) {
		gameOverTimer.countTimeInMilliseconds();
		if (GameStatusController.newTopHighScore) {
		    if (gameOverTimer.getTimeElapsedInMilliseconds() == Timer.THREE_SECONDS) {
			keyboard.resetKeys();
			resetPlayMode();
			menuController.setCurrentMenu(MenuController.HIGH_SCORES_MENU);
		    }
		} else {
		    gameOverTimer.reset();
		}
	    } else if (started) {
		checkIfSoundIsMuted();

		// ///////////////////////////////////////////////////////////////////////
		// movable game objects updates go here because of their variable speed //
		pacmanMoveController.update(delta);

		((InkyAIController) ghostsAIControllers[1]).setBlinkyCurrentTile(ghostsAIControllers[0].getGhost().getCurrentTile());
		for (GhostAIController controller : ghostsAIControllers) {
		    controller.update(delta);
		}
		// -------------------------------------------------------------------- //
		// ///////////////////////////////////////////////////////////////////////

		Game.delta += delta;
		if (Game.delta >= 1.0) {
		    // pacman controllers must come before ghosts controllers, for time reasons
		    if (!pacman.getDied()) {
			pacmanCollisionController.update(delta);

			pacmanStatusController.update(delta);
		    }

		    // pills controller must come before ghosts controllers
		    pillController.update(delta);

		    for (GhostStatusController controller : ghostsStatusControllers) {
			controller.update(delta);
		    }
		    ((BlinkyStatusController) ghostsStatusControllers[0]).ckeckIfBlinkyBecameCruiseElroyAndSetSpeed();

		    fruitController.update(delta);

		    bonusPointsController.update(delta);

		    gameStatusController.update(delta);

		    textController.update(delta);

		    scoreController.update(delta);

		    statusController.update(delta);

		    Game.delta--;
		    updates++;
		}
	    }
	} else if (activeMode == Mode.EDUCATIONAL) {
	    introAnimationController.reset();
	    if (activeSubMode == SubMode.PATHFINDING_MENU || activeSubMode == SubMode.CUSTOM_SPACE_PATHFINDING || activeSubMode == SubMode.MAZE_PATHFINDING) {
		pathfindingController.update(delta);
	    } else if (activeSubMode == SubMode.AI_ALGORITHMS_LEARNING_MENU) {
		aiAlgorithmsLearningController.update(delta);
	    } else if (activeSubMode == SubMode.AI_ALGORITHMS_STEPWISE_LEARNING) {
		aiAlgorithmsStepwiseLearningController.update(delta);
	    } else if (activeSubMode == SubMode.AI_ALGORITHMS_INTERACTIVE_LEARNING) {
		aiAlgorithmsInteractiveLearningController.update(delta);
	    } else if (activeSubMode == SubMode.TSP_MENU || activeSubMode == SubMode.CLASSIC_TSP || activeSubMode == SubMode.MAZE_TSP) {
		tspSolverController.update(delta);
	    }
	} else if (activeMode == Mode.MENU) {
	    menuController.update(delta);
	    if (menuController.getCurrentMenu() != MenuController.HIGH_SCORES_MENU) {
		introAnimationController.update(delta);
	    }

	    if (activeMode == Mode.PLAY && started) {
		// create all other controllers
		createNewPlayModeControllers(true);
	    } else if (activeMode == Mode.EDUCATIONAL) {
		// create educational mode controllers
		createNewEducationalModeControllers();
	    }
	}
    }

    // renders the whole world
    public void renderWorld(Screen screen)
    {
	if (activeMode == Mode.PLAY) {
	    if (started) {
		mazeRenderer.render(screen);

		pillController.renderPills(screen);

		fruitController.renderFruit(screen);

		scoreController.renderScore(screen);

		bonusPointsController.renderBonusPoints(screen);

		for (int i = 0; i < NUMBER_OF_GHOSTS; i++) {
		    if ((freezed && i == ghostEatenIndex) || pacman.getDied() || completedLevel) {
			continue;
		    }
		    ghostsControllers[i].renderGhost(screen);
		}

		if (!freezed) {
		    pacmanController.renderPacman(screen);
		}

		textController.renderText(screen);

		statusController.renderStatus(screen);
	    }
	} else if (activeMode == Mode.EDUCATIONAL) {
	    if (activeSubMode == SubMode.PATHFINDING_MENU || activeSubMode == SubMode.CUSTOM_SPACE_PATHFINDING || activeSubMode == SubMode.MAZE_PATHFINDING) {
		pathfindingController.renderAll(screen);
	    } else if (activeSubMode == SubMode.AI_ALGORITHMS_LEARNING_MENU || activeSubMode == SubMode.AI_ALGORITHMS_STEPWISE_LEARNING || activeSubMode == SubMode.AI_ALGORITHMS_INTERACTIVE_LEARNING) {
		if (activeSubMode == SubMode.AI_ALGORITHMS_STEPWISE_LEARNING) {
		    aiAlgorithmsStepwiseLearningController.renderAll(screen);
		} else if (activeSubMode == SubMode.AI_ALGORITHMS_INTERACTIVE_LEARNING) {
		    aiAlgorithmsInteractiveLearningController.renderAll(screen);
		} else {
		    aiAlgorithmsLearningController.renderAll(screen);
		}
	    } else if (activeSubMode == SubMode.TSP_MENU || activeSubMode == SubMode.CLASSIC_TSP || activeSubMode == SubMode.MAZE_TSP) {
		tspSolverController.renderAll(screen);
	    }
	    textController.renderText(screen);
	} else if (activeMode == Mode.MENU) {
	    menuController.renderMenu(screen);
	    if (menuController.getCurrentMenu() != MenuController.HIGH_SCORES_MENU) {
		introAnimationController.renderAll(screen);
	    }
	}
    }

    public static Mode getActiveMode()
    {
	return activeMode;
    }

    public static void setActiveMode(Mode activeMode)
    {
	Game.activeMode = activeMode;
    }

    public static SubMode getActiveSubMode()
    {
	return activeSubMode;
    }

    public static void setActiveSubMode(SubMode activeSubMode)
    {
	Game.activeSubMode = activeSubMode;
    }

    public static int getUpdates()
    {
	return updates;
    }

    public static void setUpdates(int updates)
    {
	Game.updates = updates;
    }

    public static Keyboard getKeyboard()
    {
	return keyboard;
    }

    public static void setKeyboard(Keyboard keyboard)
    {
	Game.keyboard = keyboard;
    }

    public static Level getLevel()
    {
	return level;
    }

    public static void setLevel(Level level)
    {
	Game.level = level;
    }

    public static Ghost[] getGhosts()
    {
	return ghosts;
    }

    public static Blinky getBlinky()
    {
	return (Blinky) ghosts[0];
    }

    public static Inky getInky()
    {
	return (Inky) ghosts[1];
    }

    public static Pinky getPinky()
    {
	return (Pinky) ghosts[2];
    }

    public static Clyde getClyde()
    {
	return (Clyde) ghosts[3];
    }

    public static Pacman getPacman()
    {
	return pacman;
    }

    public static Fruit getFruit()
    {
	return currentFruit;
    }

    public static void setFruit(Fruit fruit)
    {
	Game.currentFruit = fruit;
    }
}
