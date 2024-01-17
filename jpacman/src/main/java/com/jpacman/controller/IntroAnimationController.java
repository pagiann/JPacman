package com.jpacman.controller;

import java.awt.Point;

import com.jpacman.Game;
import com.jpacman.GameApplication;
import com.jpacman.controller.ghost.GhostController;
import com.jpacman.controller.pacman.PacmanController;
import com.jpacman.model.Maze;
import com.jpacman.model.MovableGameObject;
import com.jpacman.model.MovableGameObject.Direction;
import com.jpacman.model.Pacman;
import com.jpacman.model.PowerPill;
import com.jpacman.model.Text;
import com.jpacman.model.ghost.Blinky;
import com.jpacman.model.ghost.Clyde;
import com.jpacman.model.ghost.Ghost;
import com.jpacman.model.ghost.Ghost.Mode;
import com.jpacman.model.ghost.Inky;
import com.jpacman.model.ghost.Pinky;
import com.jpacman.util.Timer;
import com.jpacman.view.BonusPointsRenderer;
import com.jpacman.view.PacmanRenderer;
import com.jpacman.view.PillRenderer;
import com.jpacman.view.TextRenderer;
import com.jpacman.view.TileRenderer;
import com.jpacman.view.ghost.BlinkyRenderer;
import com.jpacman.view.ghost.ClydeRenderer;
import com.jpacman.view.ghost.InkyRenderer;
import com.jpacman.view.ghost.PinkyRenderer;
import com.jpacman.view.graphics.Screen;
import com.jpacman.view.graphics.Sprite;

public class IntroAnimationController implements Controller
{
    private static Point startPosition = new Point(0, 130);
    private static Point endPosition = new Point(Maze.WIDTH, 130);
    private static int phase = 1;
    private static double defaultMovingSpeed = 100.0;
    private static Timer timer = new Timer();
    private static Timer[] bonusPointsTimers = new Timer[Game.NUMBER_OF_GHOSTS];

    static {
	for (int i = 0; i < bonusPointsTimers.length; i++) {
	    bonusPointsTimers[i] = new Timer();
	}
    }

    private final Pacman pacman;
    private final Ghost[] ghosts;
    private final PowerPill powerPill;
    private final PillRenderer pillRenderer;
    private final PacmanController pacmanController;
    private final GhostController[] ghostsControllers;
    private final BonusPointsController bonusPointsController;

    private final TileRenderer leftEntranceVoidTileRenderer;
    private final TileRenderer rightEntranceVoidTileRenderer;
    private final TextRenderer ghostNameTextRenderer;
    private Text ghostNameText;
    private Point ghostNameTextLocation = new Point(startPosition.x + Maze.WIDTH / 3 - Sprite.HALF_SPRITE_SIZE, startPosition.y - (Sprite.QUARTER_SPRITE_SIZE + 6));
    private boolean ghostReachedPresentationPoint;

    public IntroAnimationController()
    {
	pacman = new Pacman();
	pacman.setPosition(new Point(0, startPosition.y));
	pacman.setDirection(Direction.RIGHT);
	pacman.setMovingSpeed(defaultMovingSpeed);
	pacman.setAnimationSpeed(MovableGameObject.NORMAL_ANIMATION_SPEED);
	pacman.setAnimate(true);
	pacman.setMoving(true);
	pacmanController = new PacmanController(pacman, new PacmanRenderer(pacman, Sprite.HALF_SPRITE_SIZE));

	ghosts = new Ghost[Game.NUMBER_OF_GHOSTS];
	ghosts[0] = new Blinky();
	ghosts[1] = new Pinky();
	ghosts[2] = new Inky();
	ghosts[3] = new Clyde();
	int i = 2;
	for (Ghost ghost : ghosts) {
	    ghost.setPosition(new Point(endPosition.x + Sprite.SPRITE_SIZE * (i++), endPosition.y));
	    ghost.setDirection(Direction.LEFT);
	    ghost.setMovingSpeed(defaultMovingSpeed);
	    ghost.setAnimationSpeed(MovableGameObject.NORMAL_ANIMATION_SPEED);
	    ghost.setAnimate(true);
	}
	ghostsControllers = new GhostController[Game.NUMBER_OF_GHOSTS];
	ghostsControllers[0] = new GhostController(ghosts[0], new BlinkyRenderer((Blinky) ghosts[0], Sprite.HALF_SPRITE_SIZE));
	ghostsControllers[1] = new GhostController(ghosts[1], new PinkyRenderer((Pinky) ghosts[1], Sprite.HALF_SPRITE_SIZE));
	ghostsControllers[2] = new GhostController(ghosts[2], new InkyRenderer((Inky) ghosts[2], Sprite.HALF_SPRITE_SIZE));
	ghostsControllers[3] = new GhostController(ghosts[3], new ClydeRenderer((Clyde) ghosts[3], Sprite.HALF_SPRITE_SIZE));

	powerPill = new PowerPill(new Point(startPosition.x + Sprite.SPRITE_SIZE, startPosition.y + 2), PowerPill.SIZE);
	powerPill.setAnimationSpeed(60);
	powerPill.setAnimate(false);
	pillRenderer = new PillRenderer(powerPill, PowerPill.SIZE);

	bonusPointsController = new BonusPointsController();
	leftEntranceVoidTileRenderer = new TileRenderer(TileRenderer.blackVoidTile, startPosition, Sprite.HALF_SPRITE_SIZE, Sprite.SPRITE_SIZE);
	rightEntranceVoidTileRenderer = new TileRenderer(TileRenderer.blackVoidTile, endPosition, Sprite.HALF_SPRITE_SIZE, Sprite.SPRITE_SIZE);
	ghostNameTextRenderer = new TextRenderer(new Point());
    }

    @Override
    public void update(double delta)
    {
	pacman.addDelta((pacman.getMovingSpeed() / GameApplication.UPDATES_PER_SECOND) * delta);

	if (pacman.getDelta() >= 1) { // ups capped at object's current moving speed
	    if (pacman.isMoving()) {
		if (pacman.getDirection() == Direction.RIGHT) {
		    pacman.move(Direction.RIGHT);
		} else if (pacman.getDirection() == Direction.LEFT) {
		    pacman.move(Direction.LEFT);
		}
	    }
	    pacman.setAnimationCounter((pacman.getAnimationCounter() < 10000) ? pacman.getAnimationCounter() + 1 : 0);
	    pacman.decreaseDeltaByOne();
	    powerPill.setAnimationCounter((powerPill.getAnimationCounter() < 10000) ? powerPill.getAnimationCounter() + 1 : 0);
	}

	for (Ghost ghost : ghosts) {
	    ghost.addDelta((ghost.getMovingSpeed() / GameApplication.UPDATES_PER_SECOND) * delta);

	    if (ghost.getDelta() >= 1) { // ups capped at object's current moving speed
		if (ghost.isMoving()) {
		    if (ghost.getDirection() == Direction.RIGHT) {
			ghost.move(Direction.RIGHT);
		    } else if (ghost.getDirection() == Direction.LEFT) {
			ghost.move(Direction.LEFT);
		    }
		}
		ghost.setAnimationCounter((ghost.getAnimationCounter() < 10000) ? ghost.getAnimationCounter() + 1 : 0);
		ghost.decreaseDeltaByOne();
	    }
	}

	if (phase == 1 && pacman.getPosition().x == Maze.WIDTH) {
	    pacman.setMoving(false);
	    timer.countTimeInMilliseconds();
	    if (timer.getTimeElapsedInMilliseconds() == Timer.ONE_SECOND) {
		phase = 2;
		timer.reset();
		pacman.setDirection(Direction.LEFT);
		pacman.setMoving(true);
		for (Ghost ghost : ghosts) {
		    ghost.setMoving(true);
		}
		powerPill.setAnimate(true);
	    }
	}

	if (phase == 2 && pacman.getPosition().x == powerPill.getPosition().x) {
	    phase = 3;
	    pacman.setDirection(Direction.RIGHT);
	    for (Ghost ghost : ghosts) {
		ghost.setMode(Ghost.Mode.FRIGHTENED);
		ghost.setDirection(Direction.RIGHT);
		ghost.setMovingSpeed(defaultMovingSpeed / 2);
	    }
	    powerPill.setAnimate(false);
	}

	for (int i = 0; i < ghosts.length; i++) {
	    Ghost ghost = ghosts[i];
	    if (phase == 3) {
		if (pacman.getPosition().equals(ghost.getPosition())) {
		    ghost.setMode(Mode.EATEN);
		    int eatenGhostBonusPoints = 0;
		    switch (i + 1) {
			case 1:
			    eatenGhostBonusPoints = BonusPointsController.ONE_GHOST_EATEN_BONUS_POINTS;
			    break;
			case 2:
			    eatenGhostBonusPoints = BonusPointsController.TWO_GHOSTS_EATEN_BONUS_POINTS;
			    break;
			case 3:
			    eatenGhostBonusPoints = BonusPointsController.THREE_GHOSTS_EATEN_BONUS_POINTS;
			    break;
			case 4:
			    eatenGhostBonusPoints = BonusPointsController.FOUR_GHOSTS_EATEN_BONUS_POINTS;
			    break;
			default:
			    eatenGhostBonusPoints = BonusPointsController.ONE_GHOST_EATEN_BONUS_POINTS;
		    }
		    BonusPointsRenderer newRenderer = new BonusPointsRenderer(new TextRenderer(new Point(0, 0)), ghost.getPosition(), eatenGhostBonusPoints, -Sprite.HALF_SPRITE_SIZE, -Sprite.QUARTER_SPRITE_SIZE);
		    if (bonusPointsController.getBonusPointsRenderers().size() == i) {
			bonusPointsController.getBonusPointsRenderers().add(newRenderer);
		    }
		}
		if (ghost.getPosition().x == Maze.WIDTH) {
		    ghost.setMoving(false);
		}
	    }
	}

	if (phase == 3 && pacman.getPosition().x == Maze.WIDTH) {
	    if (pacman.isMoving()) {
		pacman.setMoving(false);
		for (Ghost ghost : ghosts) {
		    ghost.setPosition(startPosition);
		    ghost.setMode(Ghost.Mode.SCATTER);
		    ghost.setDirection(Direction.RIGHT);
		    ghost.setMovingSpeed(defaultMovingSpeed);
		    ghost.setMoving(false);
		}
	    }
	    timer.countTimeInMilliseconds();
	    if (timer.getTimeElapsedInMilliseconds() == Timer.ONE_AND_A_HALF_SECOND) {
		phase = 4;
		timer.reset();
	    }
	}

	if (phase == 4 || phase == 5 || phase == 6 || phase == 7) {
	    Ghost ghost = ghosts[phase - Game.NUMBER_OF_GHOSTS];
	    if (ghost.getPosition().x == startPosition.x && !ghost.isMoving()) {
		ghost.setMoving(true);
	    }

	    if (!ghostReachedPresentationPoint && ghost.getPosition().x == Maze.WIDTH / 2 + (2 * Sprite.SPRITE_SIZE)) {
		ghostNameText = new Text("\"" + ghost.getName() + "\"", Text.DEFAULT_FONT_MEDIUM, ghost.getColor());
		ghost.setMoving(false);
		ghostReachedPresentationPoint = true;
	    }

	    if (ghostReachedPresentationPoint) {
		timer.countTimeInMilliseconds();
		if (timer.getTimeElapsedInMilliseconds() == Timer.TWO_SECONDS) {
		    ghostNameText = null;
		    ghost.setMoving(true);
		    timer.reset();
		}
	    }

	    if (ghost.getPosition().x == Maze.WIDTH) {
		ghost.setMoving(false);
		ghostReachedPresentationPoint = false;
		if (phase < 7) {
		    phase++;
		    timer.reset();
		} else {
		    reset();
		}
	    }
	}
    }

    public void reset()
    {
	phase = 1;

	pacman.setPosition(new Point(0, startPosition.y));
	pacman.setDirection(Direction.RIGHT);
	pacman.setMoving(true);

	int i = 2;
	for (Ghost ghost : ghosts) {
	    ghost.setPosition(new Point(endPosition.x + Sprite.SPRITE_SIZE * (i++), endPosition.y));
	    ghost.setMode(Ghost.Mode.SCATTER);
	    ghost.setDirection(Direction.LEFT);
	    ghost.setMovingSpeed(defaultMovingSpeed);
	    ghost.setMoving(false);
	}
	ghostNameText = null;

	powerPill.setAnimate(false);
	bonusPointsController.getBonusPointsRenderers().clear();

	timer.reset();
	for (int j = 0; j < bonusPointsTimers.length; j++) {
	    bonusPointsTimers[j].reset();
	}
    }

    public void renderAll(Screen screen)
    {
	if (powerPill.getAnimate()) {
	    pillRenderer.render(screen);
	}

	for (GhostController ghostsController : ghostsControllers) {
	    if (ghostsController.getGhost().getMode() == Mode.EATEN) {
		for (int i = 0; i < bonusPointsController.getBonusPointsRenderers().size(); i++) {
		    BonusPointsRenderer bonusPointsRenderer = bonusPointsController.getBonusPointsRenderers().get(i);
		    bonusPointsTimers[i].countTimeInMilliseconds();
		    if (bonusPointsTimers[i].getTimeElapsedInMilliseconds() < Timer.ONE_SECOND) {
			bonusPointsRenderer.render(screen);
		    }
		}
	    } else if (ghostsController.getGhost().getPosition().x > startPosition.x && ghostsController.getGhost().getPosition().x < endPosition.x) {
		ghostsController.renderGhost(screen);
	    }
	}

	if (pacman.isMoving() && pacman.getPosition().x > startPosition.x && pacman.getPosition().x < endPosition.x) {
	    pacmanController.renderPacman(screen);
	}

	if (phase == 1) {
	    leftEntranceVoidTileRenderer.render(screen);
	}
	rightEntranceVoidTileRenderer.render(screen);

	if (ghostNameText != null) {
	    ghostNameTextRenderer.renderText(screen, ghostNameText, ghostNameTextLocation.x, ghostNameTextLocation.y, true);
	}
    }
}
