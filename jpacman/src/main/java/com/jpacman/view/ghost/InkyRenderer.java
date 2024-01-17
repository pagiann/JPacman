package com.jpacman.view.ghost;

import com.jpacman.model.ghost.Inky;
import com.jpacman.view.graphics.Sprite;
import com.jpacman.view.graphics.SpriteSheet;

public class InkyRenderer extends GhostRenderer
{
    // inky sprites
    public static final Sprite inky_up_1 = new Sprite(SpriteSheet.icons, 28, 0, 7);
    public static final Sprite inky_up_2 = new Sprite(SpriteSheet.icons, 28, 1, 7);
    public static final Sprite inky_down_1 = new Sprite(SpriteSheet.icons, 28, 2, 7);
    public static final Sprite inky_down_2 = new Sprite(SpriteSheet.icons, 28, 3, 7);
    public static final Sprite inky_left_1 = new Sprite(SpriteSheet.icons, 28, 4, 7);
    public static final Sprite inky_left_2 = new Sprite(SpriteSheet.icons, 28, 5, 7);
    public static final Sprite inky_right_1 = new Sprite(SpriteSheet.icons, 28, 6, 7);
    public static final Sprite inky_right_2 = new Sprite(SpriteSheet.icons, 28, 7, 7);

    public InkyRenderer(Inky inky, int size)
    {
	super(inky, size);
	spriteUp1 = inky_up_1;
	spriteUp2 = inky_up_2;
	spriteDown1 = inky_down_1;
	spriteDown2 = inky_down_2;
	spriteLeft1 = inky_left_1;
	spriteLeft2 = inky_left_2;
	spriteRight1 = inky_right_1;
	spriteRight2 = inky_right_2;
    }
}
