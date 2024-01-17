package com.jpacman.controller;

import java.util.ArrayList;
import java.util.List;

import com.jpacman.Game;
import com.jpacman.util.Timer;
import com.jpacman.view.BonusPointsRenderer;
import com.jpacman.view.graphics.Screen;

public class BonusPointsController implements Controller
{
    public static final int ONE_GHOST_EATEN_BONUS_POINTS = 200;
    public static final int TWO_GHOSTS_EATEN_BONUS_POINTS = 400;
    public static final int THREE_GHOSTS_EATEN_BONUS_POINTS = 800;
    public static final int FOUR_GHOSTS_EATEN_BONUS_POINTS = 1600;

    public static Timer timer;

    private List<BonusPointsRenderer> bonusPointsRenderers;

    public BonusPointsController()
    {
	bonusPointsRenderers = new ArrayList<BonusPointsRenderer>();
    }

    @Override
    public void update(double delta)
    {
	if (Game.freezed) {
	    timer.countTimeInMilliseconds();
	    if (Game.freezed && timer.getTimeElapsedInMilliseconds() >= Timer.HALF_SECOND) {
		// System.out.println("unfreezing game!\n");
		Game.freezed = false;
		Game.forcedPaused = false;
		Game.ghostEatenIndex = -1;
	    }
	}

	for (BonusPointsRenderer renderer : bonusPointsRenderers) {
	    if (renderer.isActive()) {
		renderer.getTimer().countTimeInMilliseconds();
		if (renderer.getTimer().getTimeElapsedInMilliseconds() >= Timer.TWO_AND_A_HALF_SECONDS) {
		    renderer.setActive(false);
		}
	    }
	}

	for (int i = 0; i < bonusPointsRenderers.size(); i++) {
	    if (!bonusPointsRenderers.get(i).isActive()) {
		bonusPointsRenderers.remove(i);
	    }
	}
    }

    public void renderBonusPoints(Screen screen)
    {
	for (BonusPointsRenderer renderer : bonusPointsRenderers) {
	    renderer.render(screen);
	}
    }

    public List<BonusPointsRenderer> getBonusPointsRenderers()
    {
	return bonusPointsRenderers;
    }
}
