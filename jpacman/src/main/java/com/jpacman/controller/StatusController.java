package com.jpacman.controller;

import com.jpacman.controller.pacman.PacmanStatusController;
import com.jpacman.view.StatusRenderer;
import com.jpacman.view.graphics.Screen;

public class StatusController implements Controller {
    private final StatusRenderer statusRenderer;
    private final PacmanStatusController pacmanStatusController;
    private final FruitController fruitController;

    public StatusController(StatusRenderer statusRenderer, PacmanStatusController pacmanStatusController,
            FruitController fruitController) {
        this.statusRenderer = statusRenderer;
        this.pacmanStatusController = pacmanStatusController;
        this.fruitController = fruitController;
    }

    @Override
    public void update(double delta) {
        statusRenderer.setPacmanLives(pacmanStatusController.getPacman().getLives());
        statusRenderer.setCurrentFruit(fruitController.getFruit().getSprite());
    }

    public void renderStatus(Screen screen) {
        statusRenderer.render(screen);
    }
}
