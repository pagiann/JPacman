package com.jpacman.model;

import com.jpacman.view.TextRenderer;
import com.jpacman.view.graphics.Screen;

public class Text
{
    public static final Font DEFAULT_FONT_LARGE = new Font("Default", Font.LARGE_SIZE, Font.DEFAULT_SPACING_LARGE);
    public static final Font DEFAULT_FONT_MEDIUM = new Font("Default", Font.MEDIUM_SIZE, Font.DEFAULT_SPACING_MEDIUM);
    public static final Font DEFAULT_FONT_SMALL = new Font("Default", Font.SMALL_SIZE, Font.DEFAULT_SPACING_SMALL);

    private String textMessage;
    private int numberOfLines;
    private Font font;
    private int color;
    private int width;
    private int height;

    public Text()
    {
	this("", DEFAULT_FONT_LARGE, Screen.WHITE_COLOR);
    }

    public Text(String text)
    {
	this(text, DEFAULT_FONT_LARGE);
    }

    public Text(String text, Font font)
    {
	this(text, font, Screen.WHITE_COLOR);
    }

    public Text(String text, int color)
    {
	this(text, DEFAULT_FONT_LARGE, color);
    }

    public Text(String text, Font font, int color)
    {
	this.textMessage = text;
	this.numberOfLines = 1;
	this.font = font;
	this.color = color;

	width = computeWidth(this.textMessage, this.font);
	height = computeHeight(this.font);
    }

    public int computeWidth(String textMessage, Font font)
    {
	int width = 0;

	if (textMessage.lastIndexOf('\n') == -1) {
	    width = (int) (textMessage.length() * (font.getSize() + font.getSpacing()));
	} else {
	    String[] substrings = textMessage.split("\n");
	    this.numberOfLines = substrings.length;
	    int maxSubstring = substrings[0].length();
	    for (int i = 0; i < substrings.length; i++) {
		if (substrings[i].length() > maxSubstring) {
		    maxSubstring = substrings[i].length();
		}
	    }
	    width = maxSubstring * (int) (font.getSize() + font.getSpacing());
	}

	return width;
    }

    public int computeHeight(Font font)
    {
	int height = 0;

	if (font.getSize() == Font.SMALL_SIZE) {
	    height = TextRenderer.SPRITE_SHEET_TILE_SIZE_SMALL;
	} else if (font.getSize() == Font.MEDIUM_SIZE) {
	    height = TextRenderer.SPRITE_SHEET_TILE_SIZE_MEDIUM;
	} else {
	    height = TextRenderer.SPRITE_SHEET_TILE_SIZE_LARGE;
	}

	height *= numberOfLines;

	return height;
    }

    public String getTextMessage()
    {
	return textMessage;
    }

    public void setTextMessage(String textMessage)
    {
	this.textMessage = textMessage;
	width = computeWidth(this.textMessage, font);
	height = computeHeight(font);
    }

    public void concatTextMessage(String textMessage)
    {
	this.textMessage += textMessage;
	width = computeWidth(this.textMessage, font);
    }

    public int getNumberOfLines()
    {
	return numberOfLines;
    }

    public void setNumberOfLines(int numberOfLines)
    {
	this.numberOfLines = numberOfLines;
    }

    public Font getFont()
    {
	return font;
    }

    public void setFont(Font font)
    {
	this.font = font;
    }

    public int getColor()
    {
	return color;
    }

    public void setColor(int color)
    {
	this.color = color;
    }

    public int getWidth()
    {
	return width;
    }

    public void setWidth(int width)
    {
	this.width = width;
    }

    public int getHeight()
    {
	return height;
    }

    public void setHeight(int height)
    {
	this.height = height;
    }
}
