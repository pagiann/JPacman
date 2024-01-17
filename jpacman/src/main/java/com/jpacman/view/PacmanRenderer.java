package com.jpacman.view;

import com.jpacman.Game;
import com.jpacman.controller.edumode.EducationalModeController;
import com.jpacman.model.MovableGameObject.Direction;
import com.jpacman.model.Pacman;
import com.jpacman.view.graphics.Screen;
import com.jpacman.view.graphics.Sprite;
import com.jpacman.view.graphics.SpriteSheet;

public class PacmanRenderer implements Renderer {
	// ********************* Class (static) variables ********************** //
	public static final int NUMBER_OF_DYING_SPRITES = 14;

	// pacman sprites
	public static final Sprite pacman_closed_mouth = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, 0, 3);
	public static final Sprite pacman_up_1 = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, 1, 3);
	public static final Sprite pacman_up_2 = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, 2, 3);
	public static final Sprite pacman_down_1 = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, 3, 3);
	public static final Sprite pacman_down_2 = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, 4, 3);
	public static final Sprite pacman_left_1 = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, 5, 3);
	public static final Sprite pacman_left_2 = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, 6, 3);
	public static final Sprite pacman_right_1 = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, 7, 3);
	public static final Sprite pacman_right_2 = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, 8, 3);

	// pacman dying sprites
	public static final Sprite[] pacman_dying = new Sprite[NUMBER_OF_DYING_SPRITES];

	static {
		for (int i = 0; i < NUMBER_OF_DYING_SPRITES; i++) {
			pacman_dying[i] = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, i, 4);
		}
	}

	// ************************* Instance variables ************************ //
	private final Pacman pacman;
	private final int offset;
	int[] diedBounds;

	public PacmanRenderer(Pacman pacman, int offset) {
		this.pacman = pacman;
		this.offset = offset;
		diedBounds = new int[NUMBER_OF_DYING_SPRITES + 1];
	}

	@Override
	public void render(Screen screen) {
		int animationCounter = pacman.getAnimationCounter();
		int animationSpeed = pacman.getAnimationSpeed();
		Direction direction = pacman.getDirection();
		int bound1 = animationSpeed / 5;
		int bound2 = 3 * bound1;
		int bound3 = 4 * bound1;

		diedBounds[0] = animationSpeed / (NUMBER_OF_DYING_SPRITES + 1);
		for (int i = 1; i < diedBounds.length; i++) {
			diedBounds[i] = (i + 1) * diedBounds[0];
		}

		if (pacman.getAnimate()) {
			if (pacman.isMoving()) {
				if (direction == Direction.UP) {
					// 0 <= animationCounter < bound1
					if (animationCounter % animationSpeed < bound1) {
						pacman.setSprite(pacman_up_1);
						// bound1 <= animationCounter < bound2
					} else if (animationCounter % animationSpeed >= bound1
							&& animationCounter % animationSpeed < bound2) {
						pacman.setSprite(pacman_up_2);
						// bound2 <= animationCounter < bound3
					} else if (animationCounter % animationSpeed >= bound2
							&& animationCounter % animationSpeed < bound3) {
						pacman.setSprite(pacman_up_1);
						// bound3 <= animationCounter < animationSpeed
					} else {
						pacman.setSprite(pacman_closed_mouth);
					}
				}
				if (direction == Direction.DOWN) {
					if (animationCounter % animationSpeed < bound1) {
						pacman.setSprite(pacman_down_1);
					} else if (animationCounter % animationSpeed >= bound1
							&& animationCounter % animationSpeed < bound2) {
						pacman.setSprite(pacman_down_2);
					} else if (animationCounter % animationSpeed >= bound2
							&& animationCounter % animationSpeed < bound3) {
						pacman.setSprite(pacman_down_1);
					} else {
						pacman.setSprite(pacman_closed_mouth);
					}
				}
				if (direction == Direction.LEFT) {
					if (animationCounter % animationSpeed < bound1) {
						pacman.setSprite(pacman_left_1);
					} else if (animationCounter % animationSpeed >= bound1
							&& animationCounter % animationSpeed < bound2) {
						pacman.setSprite(pacman_left_2);
					} else if (animationCounter % animationSpeed >= bound2
							&& animationCounter % animationSpeed < bound3) {
						pacman.setSprite(pacman_left_1);
					} else {
						pacman.setSprite(pacman_closed_mouth);
					}
				}
				if (direction == Direction.RIGHT) {
					if (animationCounter % animationSpeed < bound1) {
						pacman.setSprite(pacman_right_1);
					} else if (animationCounter % animationSpeed >= bound1
							&& animationCounter % animationSpeed < bound2) {
						pacman.setSprite(pacman_right_2);
					} else if (animationCounter % animationSpeed >= bound2
							&& animationCounter % animationSpeed < bound3) {
						pacman.setSprite(pacman_right_1);
					} else {
						pacman.setSprite(pacman_closed_mouth);
					}
				}

				if (Game.getActiveMode() == Game.Mode.EDUCATIONAL
						&& (Game.getActiveSubMode() == Game.SubMode.CUSTOM_SPACE_PATHFINDING
								|| Game.getActiveSubMode() == Game.SubMode.CLASSIC_TSP)) {
					if (EducationalModeController.angle != Math.PI) {
						pacman.setSprite(Sprite.rotateSprite(pacman.getSprite(), EducationalModeController.angle));
					}
				}
			} else {

				if (pacman.getDied()) {
					if (animationCounter % animationSpeed < diedBounds[0]) {
						pacman.setSprite(pacman_closed_mouth);
					} else {
						for (int i = 0; i < NUMBER_OF_DYING_SPRITES; i++) {
							if (animationCounter % animationSpeed >= diedBounds[i]
									&& animationCounter % animationSpeed < diedBounds[i + 1]) {
								pacman.setSprite(pacman_dying[i]);
							}
						}
					}
					if (Game.pacmanDying && animationCounter == animationSpeed) {
						Game.pacmanDying = false;
						Game.forcedPaused = false;
					}
				} else { // if pacman is not moving
					if (Game.getActiveMode() == Game.Mode.PLAY
							|| Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_STEPWISE_LEARNING) {
						if (direction == Direction.UP)
							pacman.setSprite(pacman_up_1);
						else if (direction == Direction.DOWN)
							pacman.setSprite(pacman_down_1);
						else if (direction == Direction.LEFT) {
							pacman.setSprite(pacman_left_1);
						} else if (direction == Direction.RIGHT)
							pacman.setSprite(pacman_right_1);

						if (pacman.getPosition().equals(pacman.getStartingPosition())) {
							pacman.setSprite(pacman_closed_mouth);
						}
					} else {
						pacman.setSprite(pacman_closed_mouth);
					}
				}
			}
		}

		if (Game.getActiveMode() == Game.Mode.EDUCATIONAL
				&& (Game.getActiveSubMode() == Game.SubMode.CUSTOM_SPACE_PATHFINDING
						|| Game.getActiveSubMode() == Game.SubMode.CLASSIC_TSP)) {
			screen.renderGameObject(pacman.getSprite(), pacman.getPresicePosition().x, pacman.getPresicePosition().y,
					offset, offset);
		} else if (!pacman.isInsideOuterTunnel() && !Game.gameOver) {
			screen.renderGameObject(pacman.getSprite(), pacman.getPosition().x, pacman.getPosition().y, offset, offset);
		}
	}
}
