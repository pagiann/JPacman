package com.jpacman.view.graphics;

public class Sprite
{
    public static final int SPRITE_SIZE = 28; // the size of each sprite (in pixels)
    public static final int HALF_SPRITE_SIZE = SPRITE_SIZE / 2;
    public static final int QUARTER_SPRITE_SIZE = SPRITE_SIZE / 4;

    private SpriteSheet sheet;
    private final int size; // for square sprites
    private final int width; // for rectangular sprites
    private final int height; // for rectangular sprites
    private int x; // the x tile index
    private int y; // the y tile index
    private int[] pixels; // the actual pixels of the sprite

    // constructs a square sprite of size tileSize
    public Sprite(SpriteSheet sheet, int tileSize, int x, int y)
    {
	this.sheet = sheet;
	size = width = height = tileSize;
	this.x = x * tileSize;
	this.y = y * tileSize;
	pixels = new int[size * size];
	load_1();
    }

    // constructs a rectangular sprite with width = width * tileSize and height = height * tileSize
    public Sprite(SpriteSheet sheet, int tileSize, int x, int y, int width, int height)
    {
	this.sheet = sheet;
	this.x = x * tileSize;
	this.y = y * tileSize;
	size = -1;
	if (width == 0) {
	    this.width = tileSize / 2;
	} else {
	    this.width = width * tileSize;
	}
	this.height = height * tileSize;
	pixels = new int[this.width * this.height];
	load_2();
    }

    public Sprite(int[] pixels, int width, int height)
    {
	size = (width == height) ? width : -1;
	this.width = width;
	this.height = height;
	this.pixels = new int[pixels.length];
	for (int i = 0; i < pixels.length; i++) {
	    this.pixels[i] = pixels[i];
	}
    }

    public static Sprite[] split(SpriteSheet sheet)
    {
	int amount = (sheet.width * sheet.height / (sheet.spriteWidth * sheet.spriteHeight));
	Sprite[] sprites = new Sprite[amount];
	int current = 0;
	int[] pixels = new int[sheet.spriteWidth * sheet.spriteHeight];

	for (int yp = 0; yp < sheet.height / sheet.spriteHeight; yp++) {
	    for (int xp = 0; xp < sheet.width / sheet.spriteWidth; xp++) {
		for (int y = 0; y < sheet.spriteHeight; y++) {
		    for (int x = 0; x < sheet.spriteWidth; x++) {
			int xo = x + xp * sheet.spriteWidth;
			int yo = y + yp * sheet.spriteHeight;
			pixels[x + y * sheet.spriteWidth] = sheet.pixels[xo + yo * sheet.width];
		    }
		}
		sprites[current++] = new Sprite(pixels, sheet.spriteWidth, sheet.spriteHeight);
	    }
	}

	return sprites;
    }

    public static Sprite rotateSprite(Sprite sprite, double angle)
    {
	return new Sprite(Sprite.rotate(sprite.pixels, sprite.width, sprite.height, angle), sprite.width, sprite.height);
    }

    private static int[] rotate(int[] pixels, int width, int height, double angle)
    {
	int[] result = new int[width * height];

	int xc = width / 2;
	int yc = height / 2;

	for (int y = 0; y < height; y++) {
	    for (int x = 0; x < width; x++) {

		int nx = (int) rotateX(x - xc, y - yc, angle) + xc;
		int ny = (int) rotateY(x - xc, y - yc, angle) + yc;

		result[x + y * width] = Screen.ALPHA_COLOR;
		if (nx >= 0 && nx < Sprite.SPRITE_SIZE && ny >= 0 && ny < Sprite.SPRITE_SIZE) {
		    result[x + y * width] = pixels[nx + ny * width];
		}
	    }
	}

	return result;
    }

    private static double rotateX(int x, int y, double angle)
    {
	return x * Math.cos(angle) - y * Math.sin(angle);
    }

    private static double rotateY(int x, int y, double angle)
    {
	return x * Math.sin(angle) + y * Math.cos(angle);
    }

    private void load_1()
    {
	for (int y = 0; y < size; y++) {
	    for (int x = 0; x < size; x++) {
		pixels[x + y * size] = sheet.pixels[(x + this.x) + (y + this.y) * sheet.size];
	    }
	}
    }

    private void load_2()
    {
	for (int y = 0; y < height; y++) {
	    for (int x = 0; x < width; x++) {
		pixels[x + y * width] = sheet.pixels[(x + this.x) + (y + this.y) * sheet.size];
	    }
	}
    }

    public int[] getPixels()
    {
	return pixels;
    }

    public void setPixels(int[] pixels)
    {
	this.pixels = pixels;
    }

    public int getWidth()
    {
	return width;
    }

    public int getHeight()
    {
	return height;
    }
}
