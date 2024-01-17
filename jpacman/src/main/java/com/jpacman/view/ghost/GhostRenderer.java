package com.jpacman.view.ghost;

import com.jpacman.Game;
import com.jpacman.controller.ghost.GhostStatusController;
import com.jpacman.model.MovableGameObject.Direction;
import com.jpacman.model.ghost.Ghost;
import com.jpacman.util.Timer;
import com.jpacman.view.Renderer;
import com.jpacman.view.graphics.Screen;
import com.jpacman.view.graphics.Sprite;
import com.jpacman.view.graphics.SpriteSheet;

public class GhostRenderer implements Renderer {
	// ghost frightened sprites
	public static final Sprite frightened1 = new Sprite(SpriteSheet.icons, 28, 8, 5);
	public static final Sprite frightened2 = new Sprite(SpriteSheet.icons, 28, 9, 5);
	public static final Sprite frightened3 = new Sprite(SpriteSheet.icons, 28, 10, 5);
	public static final Sprite frightened4 = new Sprite(SpriteSheet.icons, 28, 11, 5);
	// "ghost have been eaten" eyes sprites
	public static final Sprite eyesUp = new Sprite(SpriteSheet.icons, 28, 12, 5);
	public static final Sprite eyesDown = new Sprite(SpriteSheet.icons, 28, 13, 5);
	public static final Sprite eyesLeft = new Sprite(SpriteSheet.icons, 28, 14, 5);
	public static final Sprite eyesRight = new Sprite(SpriteSheet.icons, 28, 15, 5);

	protected final Ghost ghost;
	protected final int size;

	protected Sprite spriteUp1;
	protected Sprite spriteUp2;
	protected Sprite spriteDown1;
	protected Sprite spriteDown2;
	protected Sprite spriteLeft1;
	protected Sprite spriteLeft2;
	protected Sprite spriteRight1;
	protected Sprite spriteRight2;

	long timer = 0;
	double timePassed = 0;

	public GhostRenderer(Ghost ghost, int size) {
		this.ghost = ghost;
		this.size = size;
	}

	@Override
	public void render(Screen screen) {
		int animationCounter = ghost.getAnimationCounter();
		int animationSpeed = ghost.getAnimationSpeed();
		Direction direction = ghost.getDirection();
		int bound = animationSpeed / 2;

		if (ghost.getAnimate()) {
			Ghost.Mode currentMode = ghost.getMode();
			if (currentMode == Ghost.Mode.SCATTER || currentMode == Ghost.Mode.CHASE) {
				if (direction == Direction.UP) {
					if (animationCounter % animationSpeed < bound) { // 0 <= animationCounter < bound
						ghost.setSprite(spriteUp1);
					} else { // bound <= animationCounter < animationSpeed
						ghost.setSprite(spriteUp2);
					}
				}
				if (direction == Direction.DOWN) {
					if (animationCounter % animationSpeed < bound) {
						ghost.setSprite(spriteDown1);
					} else {
						ghost.setSprite(spriteDown2);
					}
				}
				if (direction == Direction.LEFT) {
					if (animationCounter % animationSpeed < bound) {
						ghost.setSprite(spriteLeft1);
					} else {
						ghost.setSprite(spriteLeft2);
					}
				}
				if (direction == Direction.RIGHT) {
					if (animationCounter % animationSpeed < bound) {
						ghost.setSprite(spriteRight1);
					} else {
						ghost.setSprite(spriteRight2);
					}
				}
			} else if (currentMode == Ghost.Mode.FRIGHTENED) { // it's about to come out of frightened mode
				if (ghost.getFlash()) {
					if (timer == 0) {
						timer = System.currentTimeMillis();
					}
					if (!Game.paused && !Game.forcedPaused) {
						timePassed = System.currentTimeMillis() - timer;
					}
					double flashTimePeriod = (GhostStatusController.getFlashDuration() * Timer.ONE_SECOND)
							/ (2 * Ghost.getNumberOfFlashes());
					if (timePassed < flashTimePeriod) {
						frightenedWhite();
					} else {
						frightenedBlue();
						if (timePassed >= (2 * flashTimePeriod)) {
							timer = 0;
						}
					}
				} else {
					frightenedBlue();
				}
			} else if (currentMode == Ghost.Mode.EATEN) { // it has been eaten by pacman
				switch (direction) {
					case UP:
						ghost.setSprite(eyesUp);
						break;
					case DOWN:
						ghost.setSprite(eyesDown);
						break;
					case LEFT:
						ghost.setSprite(eyesLeft);
						break;
					case RIGHT:
						ghost.setSprite(eyesRight);
						break;
				}
			}
		}

		if (!ghost.isInsideOuterTunnel()) {
			screen.renderGameObject(ghost.getSprite(), ghost.getPosition().x, ghost.getPosition().y, size, size);
		}
	}

	private void frightenedBlue() {
		int animationCounter = ghost.getAnimationCounter();
		int animationSpeed = ghost.getAnimationSpeed();
		int bound1 = 0;
		int bound2 = animationSpeed / 2;

		if (animationCounter % animationSpeed >= bound1 && animationCounter % animationSpeed < bound2) {
			ghost.setSprite(frightened1);
		} else {
			ghost.setSprite(frightened2);
		}
	}

	private void frightenedWhite() {
		int animationCounter = ghost.getAnimationCounter();
		int animationSpeed = ghost.getAnimationSpeed();
		int bound1 = 0;
		int bound2 = animationSpeed / 2;

		if (animationCounter % animationSpeed >= bound1 && animationCounter % animationSpeed < bound2) {
			ghost.setSprite(frightened3);
		} else {
			ghost.setSprite(frightened4);
		}
	}
}
