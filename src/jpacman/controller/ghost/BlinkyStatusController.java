package jpacman.controller.ghost;

import jpacman.controller.LevelController;
import jpacman.controller.PillController;
import jpacman.model.Pacman;
import jpacman.model.ghost.Blinky;
import jpacman.model.ghost.Clyde;
import jpacman.model.ghost.Ghost;

public class BlinkyStatusController extends GhostStatusController
{
    public static boolean blinkyWasElroyWhenPacmanDied = false;

    private final Clyde clyde;
    private final PillController pillsController;

    public BlinkyStatusController(Blinky blinky, LevelController levelController, Pacman pacman, Clyde clyde, PillController pillsController)
    {
	super(blinky, levelController, pacman);
	this.clyde = clyde;
	this.pillsController = pillsController;
    }

    public void ckeckIfBlinkyBecameCruiseElroyAndSetSpeed()
    {
	boolean check = false;
	if (blinkyWasElroyWhenPacmanDied) {
	    if (!clyde.isInsideHouse()) { // if Clyde has exited the house
		check = true;
		blinkyWasElroyWhenPacmanDied = false;
	    }
	} else {
	    check = true;
	}

	if (check) {
	    if (!(((Blinky) ghost).isCruiseElroy1() || ((Blinky) ghost).isCruiseElroy2()) && (pillsController.getPillsRemaining()) <= levelController.getLevel().getElroy1PillsRemaining()) {
		if (ghost.getMode() == Ghost.Mode.FRIGHTENED) {
		    ghost.setBeforeFrightenedMovingSpeedMultiplier(levelController.getLevel().getElroy1MovingSpeedMultiplier());
		} else if (ghost.getMode() != Ghost.Mode.EATEN) {
		    if ((ghost).isInsideInnerTunnel()) {
			ghost.setBeforeTunnelMovingSpeedMultiplier(levelController.getLevel().getElroy1MovingSpeedMultiplier());
		    } else {
			ghost.setMovingSpeedViaMultiplier(levelController.getLevel().getElroy1MovingSpeedMultiplier());
		    }
		}
		((Blinky) ghost).setCruiseElroy1(true);
	    } else if (!((Blinky) ghost).isCruiseElroy2() && (pillsController.getPillsRemaining()) <= levelController.getLevel().getElroy2PillsRemaining()) {
		if (ghost.getMode() == Ghost.Mode.FRIGHTENED) {
		    ghost.setBeforeFrightenedMovingSpeedMultiplier(levelController.getLevel().getElroy2MovingSpeedMultiplier());
		} else if (ghost.getMode() != Ghost.Mode.EATEN) {
		    if ((ghost).isInsideInnerTunnel()) {
			ghost.setBeforeTunnelMovingSpeedMultiplier(levelController.getLevel().getElroy2MovingSpeedMultiplier());
		    } else {
			ghost.setMovingSpeedViaMultiplier(levelController.getLevel().getElroy2MovingSpeedMultiplier());
		    }
		}
		((Blinky) ghost).setCruiseElroy1(false);
		((Blinky) ghost).setCruiseElroy2(true);
	    }
	}
    }
}
