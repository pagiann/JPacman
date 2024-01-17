package com.jpacman.view.ghost;

import com.jpacman.model.ghost.Pinky;
import com.jpacman.view.graphics.Sprite;
import com.jpacman.view.graphics.SpriteSheet;

public class PinkyRenderer extends GhostRenderer {
    // pinky sprites
    public static final Sprite pinky_up_1 = new Sprite(SpriteSheet.icons, 28, 0, 6);
    public static final Sprite pinky_up_2 = new Sprite(SpriteSheet.icons, 28, 1, 6);
    public static final Sprite pinky_down_1 = new Sprite(SpriteSheet.icons, 28, 2, 6);
    public static final Sprite pinky_down_2 = new Sprite(SpriteSheet.icons, 28, 3, 6);
    public static final Sprite pinky_left_1 = new Sprite(SpriteSheet.icons, 28, 4, 6);
    public static final Sprite pinky_left_2 = new Sprite(SpriteSheet.icons, 28, 5, 6);
    public static final Sprite pinky_right_1 = new Sprite(SpriteSheet.icons, 28, 6, 6);
    public static final Sprite pinky_right_2 = new Sprite(SpriteSheet.icons, 28, 7, 6);

    public PinkyRenderer(Pinky pinky, int size) {
        super(pinky, size);
        spriteUp1 = pinky_up_1;
        spriteUp2 = pinky_up_2;
        spriteDown1 = pinky_down_1;
        spriteDown2 = pinky_down_2;
        spriteLeft1 = pinky_left_1;
        spriteLeft2 = pinky_left_2;
        spriteRight1 = pinky_right_1;
        spriteRight2 = pinky_right_2;
    }
}
