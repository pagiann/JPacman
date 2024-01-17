package jpacman.controller.pacman;

import jpacman.controller.Controller;
import jpacman.model.Pacman;
import jpacman.view.PacmanRenderer;
import jpacman.view.graphics.Screen;

public class PacmanController implements Controller
{
    protected final Pacman pacman;
    private final PacmanRenderer pacmanRenderer;

    public PacmanController(Pacman pacman, PacmanRenderer pacmanRenderer)
    {
	this.pacman = pacman;
	this.pacmanRenderer = pacmanRenderer;
    }

    @Override
    public void update(double delta)
    {
    }

    public void renderPacman(Screen screen)
    {
	pacmanRenderer.render(screen);
    }

    public Pacman getPacman()
    {
	return pacman;
    }
}
