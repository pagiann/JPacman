package jpacman.model;

import java.io.Serializable;

public class HighScoreEntry implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final String name;
    private final String date;
    private final int highScore;

    public HighScoreEntry(String name, String date, int highScore)
    {
	this.name = name;
	this.date = date;
	this.highScore = highScore;
    }

    public String getName()
    {
	return name;
    }

    public int getHighScore()
    {
	return highScore;
    }

    public String getDate()
    {
	return date;
    }
}
