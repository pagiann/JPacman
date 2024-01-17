package com.jpacman.model;

public class Font
{
    public static final float LARGE_SIZE = 24.0f;
    public static final float MEDIUM_SIZE = 18.0f;
    public static final float SMALL_SIZE = 12.0f;
    public static final float DEFAULT_SPACING_LARGE = -4.0f;
    public static final float DEFAULT_SPACING_MEDIUM = -3.0f;
    public static final float DEFAULT_SPACING_SMALL = -2.0f;

    private final String name;
    private final float pointSize;
    private boolean italic = false;
    private float spacing = 0;

    public Font(String name)
    {
	this(name, LARGE_SIZE, DEFAULT_SPACING_LARGE);
    }

    public Font(String name, float pointSize)
    {
	this(name, pointSize, DEFAULT_SPACING_SMALL);
    }

    public Font(String name, float pointSize, float spacing)
    {
	this.name = name;
	this.pointSize = pointSize;
	this.spacing = spacing;
    }

    public String getName()
    {
	return name;
    }

    public float getSize()
    {
	return pointSize;
    }

    public boolean isItalic()
    {
	return italic;
    }

    public void setItalic(boolean italic)
    {
	this.italic = italic;
    }

    public float getSpacing()
    {
	return spacing;
    }

    public void setSpacing(float spacing)
    {
	this.spacing = spacing;
    }
}
