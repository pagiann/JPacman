package com.jpacman.view.ghost;

import com.jpacman.model.ghost.Blinky;
import com.jpacman.view.graphics.Sprite;
import com.jpacman.view.graphics.SpriteSheet;

public class BlinkyRenderer extends GhostRenderer {
    // blinky sprites
    public static final Sprite blinky_up_1 = new Sprite(SpriteSheet.icons, 28, 0, 5);
    public static final Sprite blinky_up_2 = new Sprite(SpriteSheet.icons, 28, 1, 5);
    public static final Sprite blinky_down_1 = new Sprite(SpriteSheet.icons, 28, 2, 5);
    public static final Sprite blinky_down_2 = new Sprite(SpriteSheet.icons, 28, 3, 5);
    public static final Sprite blinky_left_1 = new Sprite(SpriteSheet.icons, 28, 4, 5);
    public static final Sprite blinky_left_2 = new Sprite(SpriteSheet.icons, 28, 5, 5);
    public static final Sprite blinky_right_1 = new Sprite(SpriteSheet.icons, 28, 6, 5);
    public static final Sprite blinky_right_2 = new Sprite(SpriteSheet.icons, 28, 7, 5);

    public BlinkyRenderer(Blinky blinky, int size) {
        super(blinky, size);
        spriteUp1 = blinky_up_1;
        spriteUp2 = blinky_up_2;
        spriteDown1 = blinky_down_1;
        spriteDown2 = blinky_down_2;
        spriteLeft1 = blinky_left_1;
        spriteLeft2 = blinky_left_2;
        spriteRight1 = blinky_right_1;
        spriteRight2 = blinky_right_2;
    }
}
