package com.jpacman.controller.ghost;

import com.jpacman.controller.Controller;
import com.jpacman.model.ghost.Ghost;
import com.jpacman.view.ghost.GhostRenderer;
import com.jpacman.view.graphics.Screen;

public class GhostController implements Controller
{
    protected final Ghost ghost;
    protected final GhostRenderer ghostRenderer;

    public GhostController(Ghost ghost, GhostRenderer ghostRenderer)
    {
	this.ghost = ghost;
	this.ghostRenderer = ghostRenderer;
    }

    @Override
    public void update(double delta)
    {
    }

    public void renderGhost(Screen screen)
    {
	ghostRenderer.render(screen);
    }

    public Ghost getGhost()
    {
	return ghost;
    }
}
