package jpacman.view;

import java.awt.Point;

import jpacman.model.Text;
import jpacman.util.Timer;
import jpacman.view.graphics.Screen;

public class BonusPointsRenderer implements Renderer
{
    private TextRenderer textRenderer;
    private String bonusPointsString;
    private Text bonusPointText;
    private Point position;
    private boolean active;
    private Timer timer;

    public BonusPointsRenderer(TextRenderer textRenderer)
    {
	this.textRenderer = textRenderer;
	bonusPointText = new Text("", Text.DEFAULT_FONT_SMALL, Screen.CYAN_COLOR);
    }

    public BonusPointsRenderer(TextRenderer textRenderer, Point position, int bonusPoints, int xOffset, int yOffset)
    {
	this.textRenderer = textRenderer;
	this.position = new Point(position.x + xOffset, position.y + yOffset);
	bonusPointText = new Text("", Text.DEFAULT_FONT_SMALL, Screen.CYAN_COLOR);
	bonusPointsString = String.valueOf(bonusPoints);
	bonusPointText.setTextMessage(bonusPointsString);
	active = true;
	timer = new Timer();
    }

    @Override
    public void render(Screen screen)
    {
	textRenderer.renderText(screen, bonusPointText, position.x, position.y, true);
    }

    public boolean isActive()
    {
	return active;
    }

    public void setActive(boolean active)
    {
	this.active = active;
    }

    public Timer getTimer()
    {
	return timer;
    }

    public void setTimer(Timer timer)
    {
	this.timer = timer;
    }
}
