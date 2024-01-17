package jpacman.controller.pacman;

import java.awt.Point;

import jpacman.Game;
import jpacman.SoundEffect;
import jpacman.controller.BonusPointsController;
import jpacman.controller.FruitController;
import jpacman.controller.LevelController;
import jpacman.controller.PillController;
import jpacman.controller.ScoreController;
import jpacman.controller.ghost.GhostAIController;
import jpacman.model.Fruit;
import jpacman.model.Pacman;
import jpacman.model.Pill;
import jpacman.model.PowerPill;
import jpacman.model.SimplePill;
import jpacman.model.ghost.Ghost;
import jpacman.util.Timer;
import jpacman.view.BonusPointsRenderer;
import jpacman.view.TextRenderer;
import jpacman.view.graphics.Sprite;

public class PacmanCollisionController extends PacmanController
{
    // ********************* Class (static) variables ********************** //
    private static int collidedGhostIndex = -1;
    private static int lastCollidedGhostIndex = -1;
    private static int collidedPillIndex = -1;
    private static boolean pacmanCollidedWithFruitFlag = false;
    private static boolean pacmanCollidedWithPillFlag = false;

    // ************************* Instance variables ************************ //
    private final LevelController levelController;
    private final Ghost[] ghosts;
    private final GhostAIController[] ghostsAIControllers;
    private final FruitController fruitController;
    private final PillController pillsController;
    private final ScoreController scoreController;
    private final BonusPointsController bonusPointsController;

    public PacmanCollisionController(Pacman pacman, Ghost[] ghosts, GhostAIController[] ghostsAIControllers,
				     FruitController fruitController, PillController pillController,
				     LevelController levelController, ScoreController scoreController,
				     BonusPointsController bonusPointsController)
    {
	super(pacman, null);
	this.ghosts = ghosts;
	this.ghostsAIControllers = ghostsAIControllers;
	this.fruitController = fruitController;
	this.pillsController = pillController;
	this.levelController = levelController;
	this.scoreController = scoreController;
	this.bonusPointsController = bonusPointsController;
    }

    @Override
    public void update(double delta)
    {
	if (Game.pacmanDying) {
	    return;
	}

	if (!pacman.isInsideOuterTunnel() && pacmanCollidedWithGhost()) {
	    Ghost.Mode currentMode = ghosts[collidedGhostIndex].getMode();
	    if (currentMode == Ghost.Mode.SCATTER || currentMode == Ghost.Mode.CHASE) {
		PacmanStatusController.setPacmanDiedFlag(true);
		PacmanStatusController.timer.reset();
		Game.pacmanDying = true;
		SoundEffect.SIREN.getClip().stop();
		pacman.setAnimate(false);
	    } else if (currentMode == Ghost.Mode.FRIGHTENED) {
		ghosts[collidedGhostIndex].setMode(Ghost.Mode.EATEN);
		ghosts[collidedGhostIndex].setRevived(false);
		pacman.increaseGhostsEatenByOne();
		SoundEffect.EATING_GHOST.play();
		int xOffset = Sprite.HALF_SPRITE_SIZE;
		int yOffset = TextRenderer.SPRITE_SHEET_TILE_SIZE_SMALL / 2;
		switch (pacman.getGhostsEaten()) {
		    case 1:
			// set and display the ghost's bonus points sprite
			bonusPointsController.getBonusPointsRenderers().add(new BonusPointsRenderer(new TextRenderer(new Point(0, 0)), ghosts[collidedGhostIndex].getPosition(), BonusPointsController.ONE_GHOST_EATEN_BONUS_POINTS, -xOffset, -yOffset));
			scoreController.increaseScoreBy(BonusPointsController.ONE_GHOST_EATEN_BONUS_POINTS);
			break;
		    case 2:
			// set and display the ghost's bonus points sprite
			bonusPointsController.getBonusPointsRenderers().add(new BonusPointsRenderer(new TextRenderer(new Point(0, 0)), ghosts[collidedGhostIndex].getPosition(), BonusPointsController.TWO_GHOSTS_EATEN_BONUS_POINTS, -xOffset, -yOffset));
			scoreController.increaseScoreBy(BonusPointsController.TWO_GHOSTS_EATEN_BONUS_POINTS);
			break;
		    case 3:
			// set and display the ghost's bonus points sprite
			bonusPointsController.getBonusPointsRenderers().add(new BonusPointsRenderer(new TextRenderer(new Point(0, 0)), ghosts[collidedGhostIndex].getPosition(), BonusPointsController.THREE_GHOSTS_EATEN_BONUS_POINTS, -xOffset, -yOffset));
			scoreController.increaseScoreBy(BonusPointsController.THREE_GHOSTS_EATEN_BONUS_POINTS);
			break;
		    case 4:
			// set and display the ghost's bonus points sprite
			bonusPointsController.getBonusPointsRenderers().add(new BonusPointsRenderer(new TextRenderer(new Point(0, 0)), ghosts[collidedGhostIndex].getPosition(), BonusPointsController.FOUR_GHOSTS_EATEN_BONUS_POINTS, -xOffset, -yOffset));
			scoreController.increaseScoreBy(BonusPointsController.FOUR_GHOSTS_EATEN_BONUS_POINTS);
			break;
		}
		BonusPointsController.timer = new Timer();
		// System.out.println("freezing game!");
		Game.freezed = true;
		Game.forcedPaused = true;
		Game.ghostEatenIndex = collidedGhostIndex;
	    }
	}

	if (fruitController.getFruit().getAppeared() && pacmanCollidedWithFruit()) {
	    // System.out.println("pacman ate fruit!");
	    fruitController.getFruit().setAppeared(false);
	    fruitController.setTimerSet(false);
	    // set and display the fruit's bonus points sprite
	    int offset = TextRenderer.SPRITE_SHEET_TILE_SIZE_SMALL / 2;
	    bonusPointsController.getBonusPointsRenderers().add(new BonusPointsRenderer(new TextRenderer(new Point(0, 0)), fruitController.getFruit().getPosition(), fruitController.getFruit().getType().getBonusPoints(), offset / 4, offset));
	    // update score
	    scoreController.increaseScoreBy(fruitController.getFruit().getType().getBonusPoints());
	    pacmanCollidedWithFruitFlag = false;
	}

	if (pacmanCollidedWithPill()) {
	    pacman.setCollidedWithPill(true);
	    pacman.setDistanceMoved(0);
	    if (pillsController.getPills().get(collidedPillIndex) instanceof SimplePill) {
		pacman.setAtePill(false);
		// System.out.println("pacman JUST collided with a pill");
	    } else if (pillsController.getPills().get(collidedPillIndex) instanceof PowerPill) {
		pacman.setAtePowerPill(false);
		// System.out.println("pacman JUST collided with a power pill");
	    }
	}

	if (!pacman.getAtePill()) {
	    if (pacman.getCollidedWithPill()) {
		if (pacman.isInPowerMode()) {
		    // System.out.println("\npacman collided with pill and ghosts are frightened, set speed
		    // accordingly");
		    pacman.setMovingSpeedViaMultiplier(levelController.getLevel().getPacmanGhostsFrightenedAndPillEatingMovingSpeedMultiplier());
		    // System.out.println("changing pacman speed to " + pacman.getMovingSpeed());
		} else {
		    // System.out.println("\npacman collided with pill, set speed accordingly");
		    pacman.setMovingSpeedViaMultiplier(levelController.getLevel().getPacmanPillEatingMovingSpeedMultiplier());
		    // System.out.println("changing pacman speed to " + pacman.getMovingSpeed());
		}
		pacman.setCollidedWithPill(false);
	    }

	    // System.out.println("DEBUG: distance moved = " + pacman.getDistanceMoved());
	    if (pacman.getDistanceMoved() >= Pill.SIZE / 2) { // if pacman reached the center point of pill
		pillsController.removePill(collidedPillIndex);
		pillsController.increasePillsEatenByOne();
		if (GhostAIController.isGlobalCounterActive()) {
		    GhostAIController.increasePillsEatenByPacmanGlobalByOne();
		} else {
		    for (int i = 1; i < ghostsAIControllers.length; i++) {
			ghostsAIControllers[i].increasePillsEatenByPacmanByOne();
		    }
		}
		pacman.setAtePill(true);
		// System.out.println("\nremoving pill...");
		if (pacman.isInPowerMode()) {
		    pacman.setMovingSpeedViaMultiplier(levelController.getLevel().getPacmanGhostsFrightenedMovingSpeedMultiplier());
		} else {
		    pacman.setMovingSpeedViaMultiplier(levelController.getLevel().getPacmanNormalMovingSpeedMultiplier());
		}
		// System.out.println("changing pacman speed to " + pacman.getMovingSpeed());
		PacmanStatusController.timeElapsedSinceAtePillTimer.setTimeElapsedInMilliseconds(0);
		scoreController.increaseScoreBy(SimplePill.BONUS_POINTS);
		pacmanCollidedWithPillFlag = false;
		SoundEffect.EATING_SIMPLE_PILL_SHORT.play();
	    }
	}

	if (!pacman.getAtePowerPill()) {
	    if (pacman.getCollidedWithPill()) {
		pacman.setMovingSpeedViaMultiplier(levelController.getLevel().getPacmanPowerPillEatingMovingSpeedMultiplier());
		pacman.setCollidedWithPill(false);
	    }

	    // System.out.println("DEBUG: distance moved = "+pacman.getDistanceMoved());
	    if (pacman.getDistanceMoved() >= PowerPill.SIZE / 2) { // if pacman reached the center point of power pill
		pillsController.removePill(collidedPillIndex);
		pillsController.increasePillsEatenByOne();
		pacman.setAtePowerPill(true);
		pacman.setInPowerMode(true);
		pacman.setGhostsEaten(0);
		// System.out.println("\npacman ate power pill, set speed accordingly");
		pacman.setMovingSpeedViaMultiplier(levelController.getLevel().getPacmanGhostsFrightenedMovingSpeedMultiplier());
		// System.out.println("changing pacman speed to " + pacman.getMovingSpeed());
		scoreController.increaseScoreBy(PowerPill.BONUS_POINTS);
		pacmanCollidedWithPillFlag = false;
		PacmanStatusController.setPacmanInPowerModeFlag(false);
		SoundEffect.EATING_POWER_PILL.play();
	    }
	}
    }

    private boolean pacmanCollidedWithGhost()
    {
	boolean collision = false;
	for (int i = 0; i < ghosts.length; i++) {
	    // "!ghosts[i].isInsideHouse()" is required because when ghost enters house,
	    // its current tile is the house's doors which collides with pacman (bug).
	    if (!ghosts[i].isInsideHouse() && pacman.getCurrentTile().equals(ghosts[i].getCurrentTile())) {
		collidedGhostIndex = i;
		if (lastCollidedGhostIndex != collidedGhostIndex) { // in order to print the collision only once
		    // System.out.println("\n\nPacman COLLIDED with " + ghosts[i].getName());
		    lastCollidedGhostIndex = collidedGhostIndex;
		}
		collision = true;
		break;
	    }
	}
	return collision;
    }

    private boolean pacmanCollidedWithFruit()
    {
	boolean collision = false;
	if (!pacmanCollidedWithFruitFlag) { // used to run following code only once
	    Fruit currentFruit = fruitController.getFruit();
	    if (currentFruit.getAppeared() && currentFruit.getBounds().contains(pacman.getPosition())) {
		// System.out.println("\n\nPacman COLLIDED with " + fruitController.getFruit().getType());
		collision = true;
		pacmanCollidedWithFruitFlag = true;
		SoundEffect.EATING_FRUIT.play();
	    }
	}
	return collision;
    }

    private boolean pacmanCollidedWithPill()
    {
	boolean collision = false;
	if (!pacmanCollidedWithPillFlag) { // used to run following code only once
	    for (int i = 0; i < pillsController.getPills().size(); i++) {
		if (pillsController.getPills().get(i).getBounds().contains(pacman.getPosition())) {
		    collidedPillIndex = i;
		    // System.out.println("\n\nPacman COLLIDED with pill #" + i);
		    collision = true;
		    pacmanCollidedWithPillFlag = true;
		    break;
		}
	    }
	}
	return collision;
    }

    public static void reset()
    {
	collidedGhostIndex = -1;
	lastCollidedGhostIndex = -1;
	collidedPillIndex = -1;
	pacmanCollidedWithFruitFlag = false;
	pacmanCollidedWithPillFlag = false;
    }

    public ScoreController getScoreController()
    {
	return scoreController;
    }

    public static void setPacmanCollidedWithPillFlag(boolean pacmanCollidedWithPillFlag)
    {
	PacmanCollisionController.pacmanCollidedWithPillFlag = pacmanCollidedWithPillFlag;
    }
}
