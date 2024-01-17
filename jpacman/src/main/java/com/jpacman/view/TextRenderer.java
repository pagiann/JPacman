package com.jpacman.view;

import java.awt.Point;

import com.jpacman.model.Font;
import com.jpacman.model.Maze;
import com.jpacman.model.Text;
import com.jpacman.view.graphics.Screen;
import com.jpacman.view.graphics.Sprite;
import com.jpacman.view.graphics.SpriteSheet;

public class TextRenderer implements Renderer
{
    // ********************* Class (static) variables ********************** //
    // ************************** Constants ************************** //
    public static final int LEVEL__READY__PAUSED__QUIT__GAME_OVER__MASK = 31;
    public static final int LEVEL_MASK = 30;
    public static final int READY_MASK = 29;
    public static final int PAUSED_MASK = 27;
    public static final int QUIT_MASK = 23;
    public static final int GAME_OVER__MASK = 15;

    public static final int SPRITE_SHEET_TILE_SIZE_LARGE = 32;
    public static final int SPRITE_SHEET_TILE_SIZE_MEDIUM = 24;
    public static final int SPRITE_SHEET_TILE_SIZE_SMALL = 16;
    // *************************************************************** //

    public static final Text level = new Text("LEVEL ", Text.DEFAULT_FONT_MEDIUM, Screen.WHITE_COLOR);
    public static final Text ready = new Text("READY!", Text.DEFAULT_FONT_MEDIUM, Screen.YELLOW_COLOR);
    public static final Text paused = new Text("PAUSED", Text.DEFAULT_FONT_MEDIUM, Screen.WHITE_COLOR);
    public static final Text quit = new Text("QUIT? Y/N", Text.DEFAULT_FONT_MEDIUM, Screen.WHITE_COLOR);
    public static final Text gameOver = new Text("GAME OVER!", Text.DEFAULT_FONT_MEDIUM, Screen.RED_COLOR);

    // private static SpriteSheet fontSpriteSheet1 = new SpriteSheet("/fonts/Untitled.bmp", 512,
    // SPRITE_SHEET_TILE_SIZE_LARGE, SPRITE_SHEET_TILE_SIZE_LARGE);
    private static SpriteSheet fontSpriteSheet1 = new SpriteSheet("/fonts/large.png", 512, SPRITE_SHEET_TILE_SIZE_LARGE, SPRITE_SHEET_TILE_SIZE_LARGE);
    private static SpriteSheet fontSpriteSheet2 = new SpriteSheet("/fonts/medium.png", 384, SPRITE_SHEET_TILE_SIZE_MEDIUM, SPRITE_SHEET_TILE_SIZE_MEDIUM);
    private static SpriteSheet fontSpriteSheet3 = new SpriteSheet("/fonts/small.png", 256, SPRITE_SHEET_TILE_SIZE_SMALL, SPRITE_SHEET_TILE_SIZE_SMALL);
    private static Sprite[] largeCharacters = Sprite.split(fontSpriteSheet1);
    private static Sprite[] mediumCharacters = Sprite.split(fontSpriteSheet2);
    private static Sprite[] smallCharacters = Sprite.split(fontSpriteSheet3);

    private static String charIndex = "" + //
    " !\"#$%&'()*+,-./" + //
    "0123456789:;<=>?" + //
    "@ABCDEFGHIJKLMNO" + //
    "PQRSTUVWXYZ[\\]^_" + //
    "`abcdefghijklmno" + //
    "pqrstuvwxyz{|}~";

    // ************************* Instance variables ************************ //
    private final Point textLabelFixedPosition;
    private int levelReadyPausedGameOverFlag = LEVEL__READY__PAUSED__QUIT__GAME_OVER__MASK;
    private String levelNumber = "";

    public TextRenderer(Point textLabelFixedPosition)
    {
	this.textLabelFixedPosition = new Point(textLabelFixedPosition.x, textLabelFixedPosition.y - Sprite.HALF_SPRITE_SIZE);
    }

    @Override
    public void render(Screen screen)
    {
	int yOffset = 3;
	switch (levelReadyPausedGameOverFlag ^ LEVEL__READY__PAUSED__QUIT__GAME_OVER__MASK) {
	    case LEVEL_MASK:
		int xOffset = Screen.getMiddleAlignStartPositionInArea(Maze.TEXT_MESSAGE_AREA_WIDTH, level.computeWidth(level.getTextMessage() + levelNumber, level.getFont()));
		renderText(screen, level, textLabelFixedPosition.x + xOffset, textLabelFixedPosition.y + yOffset, true);
		renderText(screen, new Text(levelNumber, Text.DEFAULT_FONT_MEDIUM, Screen.YELLOW_COLOR), textLabelFixedPosition.x + xOffset + level.getWidth(), textLabelFixedPosition.y + yOffset, true);
		break;
	    case READY_MASK:
		renderText(screen, ready, textLabelFixedPosition.x + calculateXOffset(ready), textLabelFixedPosition.y + yOffset, true);
		break;
	    case PAUSED_MASK:
		renderText(screen, paused, textLabelFixedPosition.x + calculateXOffset(paused), textLabelFixedPosition.y + yOffset, true);
		break;
	    case QUIT_MASK:
		renderText(screen, quit, textLabelFixedPosition.x + calculateXOffset(quit), textLabelFixedPosition.y + yOffset, true);
		break;
	    case GAME_OVER__MASK:
		renderText(screen, gameOver, textLabelFixedPosition.x + calculateXOffset(gameOver), textLabelFixedPosition.y + yOffset, true);
		break;
	}
    }

    private int calculateXOffset(Text text)
    {
	int xOffset = Screen.getMiddleAlignStartPositionInArea(Maze.TEXT_MESSAGE_AREA_WIDTH, text.getWidth());
	xOffset += (int) (text.getFont().getSpacing() > 0 ? text.getFont().getSpacing() : -text.getFont().getSpacing());

	return xOffset;
    }

    public void renderText(Screen screen, Text text, int x, int y, boolean useOffsets)
    {
	String textString = text.getTextMessage();
	Font font = text.getFont();
	int color = text.getColor();
	float size = font.getSize();
	float spacing = font.getSpacing();
	boolean italic = font.isItalic();

	float xOffset = 0;
	float yOffset = 0;
	int line = 0;

	for (int i = 0; i < textString.length(); i++) {
	    char currentChar = textString.charAt(i);
	    if (currentChar == '\n') {
		line++;
		xOffset = 0;
	    }
	    int index = charIndex.indexOf(currentChar);
	    if (index == -1)
		continue;
	    if (size > Font.MEDIUM_SIZE) {
		if (italic) {
		    screen.renderTextCharacter(x + (int) xOffset, y + (int) (yOffset + line * size), largeCharacters[index + 128], color, useOffsets);
		} else {
		    screen.renderTextCharacter(x + (int) xOffset, y + (int) (yOffset + line * size), largeCharacters[index], color, useOffsets);
		}
	    } else if (size == Font.MEDIUM_SIZE) {
		if (italic) {
		    screen.renderTextCharacter(x + (int) xOffset, y + (int) (yOffset + 1.3 * line * size), mediumCharacters[index + 128], color, useOffsets);
		} else {
		    screen.renderTextCharacter(x + (int) xOffset, y + (int) (yOffset + 1.3 * line * size), mediumCharacters[index], color, useOffsets);
		}
	    } else {
		if (italic) {
		    screen.renderTextCharacter(x + (int) xOffset, y + (int) (yOffset + 1.4 * line * size), smallCharacters[index + 128], color, useOffsets);
		} else {
		    screen.renderTextCharacter(x + (int) xOffset, y + (int) (yOffset + 1.4 * line * size), smallCharacters[index], color, useOffsets);
		}
	    }
	    xOffset += size + spacing;
	    yOffset = 0;
	}
    }

    public int getReadyPausedGameOverFlag()
    {
	return levelReadyPausedGameOverFlag;
    }

    public void setLevelReadyPausedGameOverFlag(int levelReadyPausedGameOverFlag)
    {
	this.levelReadyPausedGameOverFlag = levelReadyPausedGameOverFlag;
    }

    public void setLevelNumberString(int value)
    {
	levelNumber = String.valueOf(value);
    }
}
