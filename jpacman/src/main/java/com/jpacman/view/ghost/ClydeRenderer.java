package com.jpacman.view.ghost;

import com.jpacman.model.ghost.Clyde;
import com.jpacman.view.graphics.Sprite;
import com.jpacman.view.graphics.SpriteSheet;

public class ClydeRenderer extends GhostRenderer {
    // clyde sprites
    public static final Sprite clyde_up_1 = new Sprite(SpriteSheet.icons, 28, 0, 8);
    public static final Sprite clyde_up_2 = new Sprite(SpriteSheet.icons, 28, 1, 8);
    public static final Sprite clyde_down_1 = new Sprite(SpriteSheet.icons, 28, 2, 8);
    public static final Sprite clyde_down_2 = new Sprite(SpriteSheet.icons, 28, 3, 8);
    public static final Sprite clyde_left_1 = new Sprite(SpriteSheet.icons, 28, 4, 8);
    public static final Sprite clyde_left_2 = new Sprite(SpriteSheet.icons, 28, 5, 8);
    public static final Sprite clyde_right_1 = new Sprite(SpriteSheet.icons, 28, 6, 8);
    public static final Sprite clyde_right_2 = new Sprite(SpriteSheet.icons, 28, 7, 8);

    public ClydeRenderer(Clyde clyde, int size) {
        super(clyde, size);
        spriteUp1 = clyde_up_1;
        spriteUp2 = clyde_up_2;
        spriteDown1 = clyde_down_1;
        spriteDown2 = clyde_down_2;
        spriteLeft1 = clyde_left_1;
        spriteLeft2 = clyde_left_2;
        spriteRight1 = clyde_right_1;
        spriteRight2 = clyde_right_2;
    }
}
