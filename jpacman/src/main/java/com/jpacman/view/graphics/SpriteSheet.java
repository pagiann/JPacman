package com.jpacman.view.graphics;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class SpriteSheet {
    public static final int NUMBER_OF_SPRITES_IN_A_ROW = 16;
    public static final SpriteSheet icons = new SpriteSheet("/graphics/spritesheet.png",
            Sprite.SPRITE_SIZE * NUMBER_OF_SPRITES_IN_A_ROW, Sprite.SPRITE_SIZE, Sprite.SPRITE_SIZE);

    final String path;
    final int size;
    int[] pixels;
    int width, height;
    final int spriteWidth;
    final int spriteHeight;

    public SpriteSheet(String path, int size, int spriteWidth, int spriteHeight) {
        this.path = path;
        this.size = size;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        pixels = new int[size * size];
        load();
    }

    private void load() {
        try {
            BufferedImage image = ImageIO.read(SpriteSheet.class.getResource(path));
            width = image.getWidth();
            height = image.getHeight();
            image.getRGB(0, 0, width, height, pixels, 0, width);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
