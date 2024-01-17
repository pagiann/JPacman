package jpacman.view;

import jpacman.model.Pill;
import jpacman.model.PowerPill;
import jpacman.view.graphics.Screen;
import jpacman.view.graphics.Sprite;
import jpacman.view.graphics.SpriteSheet;

public class PillRenderer implements Renderer
{
    // pill and power pill sprites
    public static final Sprite simplePill = new Sprite(SpriteSheet.icons, 28, 0, 9);
    public static final Sprite powerPill1 = new Sprite(SpriteSheet.icons, 28, 1, 9);
    public static final Sprite powerPill2 = new Sprite(SpriteSheet.icons, 28, 2, 9);
    public static final Sprite powerPill3 = new Sprite(SpriteSheet.icons, 28, 3, 9);

    private final Pill pill;
    private final int size;

    public PillRenderer(Pill pill, int size)
    {
	this.pill = pill;
	this.size = size;
    }

    @Override
    public void render(Screen screen)
    {
	if (pill instanceof PowerPill) {
	    int animationCounter = pill.getAnimationCounter();
	    int animationSpeed = pill.getAnimationSpeed();
	    int bound1 = animationSpeed / 5;
	    int bound2 = 2 * bound1;
	    int bound3 = 4 * bound1;

	    if (animationCounter % animationSpeed < bound1) {
		pill.setSprite(powerPill1);
	    } else if (animationCounter % animationSpeed >= bound1 && animationCounter % animationSpeed < bound2) {
		pill.setSprite(powerPill2);
	    } else if (animationCounter % animationSpeed >= bound2 && animationCounter % animationSpeed < bound3) {
		pill.setSprite(powerPill3);
	    } else {
		pill.setSprite(powerPill2);
	    }
	}

	screen.renderGameObject(pill.getSprite(), pill.getPosition().x, pill.getPosition().y, size, size);
    }
}
