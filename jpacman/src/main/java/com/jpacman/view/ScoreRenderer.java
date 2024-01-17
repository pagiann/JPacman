package com.jpacman.view;

import java.awt.Point;

import com.jpacman.model.Maze;
import com.jpacman.model.Text;
import com.jpacman.view.graphics.Screen;
import com.jpacman.view.graphics.Sprite;

public class ScoreRenderer implements Renderer
{
    public static final Text scoreText = new Text("SCORE");
    public static final Text highScoreText = new Text("HIGH SCORE");

    private final TextRenderer textRenderer;
    private Point scoreLabelPosition = new Point(0, 0);
    private Point scorePosition;
    private Point highScoreLabelPosition = new Point(Maze.WIDTH, 0);
    private Point highScorePosition;
    private String score;
    private String highScore;

    public ScoreRenderer(TextRenderer textRenderer)
    {
	this.textRenderer = textRenderer;
	int yOffset = 2; // 2 pixels vertical offset
	scoreLabelPosition = new Point(scoreLabelPosition.x + 3 * Sprite.QUARTER_SPRITE_SIZE, scoreLabelPosition.y - 9 * Sprite.QUARTER_SPRITE_SIZE);
	scorePosition = new Point(this.scoreLabelPosition.x + Sprite.SPRITE_SIZE, this.scoreLabelPosition.y + 4 * Sprite.QUARTER_SPRITE_SIZE + yOffset);
	highScoreLabelPosition = new Point(highScoreLabelPosition.x - 15 * Sprite.HALF_SPRITE_SIZE, highScoreLabelPosition.y - 9 * Sprite.QUARTER_SPRITE_SIZE);
	highScorePosition = new Point(this.highScoreLabelPosition.x + Sprite.SPRITE_SIZE, this.highScoreLabelPosition.y + 4 * Sprite.QUARTER_SPRITE_SIZE + yOffset);
	score = "0";
	highScore = "0";
    }

    @Override
    public void render(Screen screen)
    {
	textRenderer.renderText(screen, scoreText, scoreLabelPosition.x, scoreLabelPosition.y, true);
	textRenderer.renderText(screen, new Text(score, Screen.YELLOW_COLOR), scorePosition.x, scorePosition.y, true);
	textRenderer.renderText(screen, highScoreText, highScoreLabelPosition.x, highScoreLabelPosition.y, true);
	textRenderer.renderText(screen, new Text(highScore, Screen.YELLOW_COLOR), highScorePosition.x, highScorePosition.y, true);
    }

    public void setScore(int score)
    {
	this.score = String.valueOf(score);
    }

    public void setHighScore(int highScore)
    {
	this.highScore = String.valueOf(highScore);
    }
}
