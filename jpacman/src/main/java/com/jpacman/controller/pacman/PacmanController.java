package com.jpacman.controller.pacman;

import com.jpacman.controller.Controller;
import com.jpacman.model.Pacman;
import com.jpacman.view.PacmanRenderer;
import com.jpacman.view.graphics.Screen;

public class PacmanController implements Controller {
    protected final Pacman pacman;
    private final PacmanRenderer pacmanRenderer;

    public PacmanController(Pacman pacman, PacmanRenderer pacmanRenderer) {
        this.pacman = pacman;
        this.pacmanRenderer = pacmanRenderer;
    }

    @Override
    public void update(double delta) {
    }

    public void renderPacman(Screen screen) {
        pacmanRenderer.render(screen);
    }

    public Pacman getPacman() {
        return pacman;
    }
}
