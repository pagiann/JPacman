package com.jpacman.model;

import java.awt.Point;
import java.awt.Rectangle;

import com.jpacman.view.TextRenderer;
import com.jpacman.view.graphics.Screen;

public class UIButton {
    private Text text;
    private Rectangle bounds;
    private boolean visible;
    private boolean focused;
    private boolean mouseHovered;
    private boolean clicked;
    private boolean boundsLocationMovedOnce;

    public UIButton(Text text) {
        this(new Point(0, 0), false, text, false);
    }

    public UIButton(Point upperLeftCorner, Text text) {
        this(upperLeftCorner, false, text, false);
    }

    public UIButton(Point upperLeftCorner, boolean middleAligned, Text text, boolean isVisible) {
        this.text = text;
        int width = text.getWidth();
        int height = text.getHeight();

        if (middleAligned) {
            int startPosX = Screen.getMiddleAlignStartPositionInWindow(width);
            bounds = new Rectangle(startPosX, upperLeftCorner.y + Maze.HEIGHT / 3, width, height);
        } else {
            bounds = new Rectangle(upperLeftCorner.x, upperLeftCorner.y, width, height);
        }
        this.visible = isVisible;
    }

    public UIButton(int x, int y, int width, int height, Text text, boolean isVisible) {
        this(new Point(x, y), false, text, isVisible);
    }

    public void moveBoundsLocation(Point newLocation, boolean middleAligned) {
        if (middleAligned) {
            int startPosX = Screen.getMiddleAlignStartPositionInWindow(text.getWidth());
            bounds = new Rectangle(startPosX, newLocation.y + Maze.HEIGHT / 3, text.getWidth(), text.getHeight());
        } else {
            bounds = new Rectangle(newLocation.x, newLocation.y, text.getWidth(), text.getHeight());
        }
    }

    public void moveBoundsLocationOnce(int dx, int dy) {
        if (!boundsLocationMovedOnce) {
            bounds.translate(dx, dy);
            boundsLocationMovedOnce = true;
        }
    }

    // XXX
    public void render(TextRenderer textRenderer, Screen screen) {
        if (this.isVisible()) {
            if (this.hasFocus() || this.isMouseHovered()) {
                screen.renderBounds(this.getBounds(), Screen.WHITE_COLOR, -1, 0, 5, 2, false);
                this.getText().setColor(Screen.YELLOW_COLOR);
            } else {
                this.getText().setColor(Screen.WHITE_COLOR);
            }
            textRenderer.renderText(screen, this.getText(), this.getBounds().x, this.getBounds().y, false);
        }
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
        bounds = new Rectangle(bounds.x, bounds.y, text.getWidth(), text.getHeight());
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public boolean hasFocus() {
        return focused;
    }

    public void setFocus(boolean focused) {
        this.focused = focused;
    }

    public boolean isMouseHovered() {
        return mouseHovered;
    }

    public void setMouseHovered(boolean mouseHovered) {
        this.mouseHovered = mouseHovered;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }
}
