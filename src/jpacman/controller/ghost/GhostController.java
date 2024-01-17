package jpacman.controller.ghost;

import jpacman.controller.Controller;
import jpacman.model.ghost.Ghost;
import jpacman.view.ghost.GhostRenderer;
import jpacman.view.graphics.Screen;

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
