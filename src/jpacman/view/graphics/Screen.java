package jpacman.view.graphics;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import jpacman.Game;
import jpacman.Game.SubMode;
import jpacman.GameApplication;
import jpacman.controller.edumode.AIAlgorithmsStepwiseLearningController;
import jpacman.controller.edumode.PathfindingController;
import jpacman.controller.edumode.TSPSolverController;
import jpacman.model.Maze;
import jpacman.model.edumode.SearchAlgorithm;

public class Screen
{
    // ********************* Class (static) variables ********************** //
    // ************************** Constants ************************** //
    public static final int ALPHA_COLOR = 0xFFFF00FF;
    public static final int ALPHA_2_COLOR = 0xFF7F007F;
    public static final int WHITE_COLOR = 0xFFFFFF;
    public static final int BLACK_COLOR = 0x000000;
    public static final int RED_COLOR = 0xFF0000;
    public static final int GREEN_COLOR = 0x00FF00;
    public static final int BLUE_COLOR = 0x0000FF;
    public static final int YELLOW_COLOR = 0xFFFF00;
    public static final int MAGENTA_COLOR = 0xFF00FF;
    public static final int CYAN_COLOR = 0x00FFFF;

    // ************************* Instance variables ************************ //
    private final int width;
    private final int height; // not used with current implementation
    private int[] pixels;
    private int xOffset = 0;
    private int yOffset = 0;

    public Screen(int width, int height)
    {
	this.width = width;
	this.height = height;
	this.pixels = new int[width * height];
    }

    public void clear()
    {
	for (int i = 0; i < pixels.length; i++) {
	    pixels[i] = 0;
	}
    }

    public void renderMaze(Maze maze, int xp, int yp)
    {
	xp += xOffset;
	yp += yOffset;
	for (int y = 0; y < maze.getHeight(); y++) {
	    // ya = y absolute
	    int ya = y + yp;
	    for (int x = 0; x < maze.getWidth(); x++) {
		// xa = x absolute
		int xa = x + xp;
		int color = maze.getImagePixels()[x + y * maze.getWidth()];
		if (color != ALPHA_COLOR) {
		    pixels[xa + ya * width] = color;
		}
	    }
	}
    }

    public void renderMazeThumbnail(Maze maze, int xp, int yp, boolean useOffsets)
    {
	if (useOffsets) {
	    xp += this.xOffset;
	    yp += this.yOffset;
	}
	for (int y = 0; y < Maze.THUMBNAIL_HEIGHT; y++) {
	    // ya = y absolute
	    int ya = y + yp;
	    for (int x = 0; x < Maze.THUMBNAIL_WIDTH; x++) {
		// xa = x absolute
		int xa = x + xp;
		pixels[xa + ya * width] = maze.resizedImagePixels[x + y * Maze.THUMBNAIL_WIDTH];
	    }
	}
    }

    // for (general) rectangular sprites
    public void renderGameObject(Sprite sprite, int xp, int yp, int xOffset, int yOffset)
    {
	int xa; // x absolute
	int ya; // y absolute
	xp += this.xOffset - xOffset;
	yp += this.yOffset - yOffset;
	for (int y = 0; y < sprite.getHeight(); y++) {
	    ya = y + yp;
	    for (int x = 0; x < sprite.getWidth(); x++) {
		xa = x + xp;
		int color = sprite.getPixels()[x + y * sprite.getWidth()];
		if (color != ALPHA_COLOR && color != ALPHA_2_COLOR) { // color not pink (with alpha channel)
		    pixels[xa + ya * width] = color;
		}
	    }
	}
    }

    public void renderGameObject(Sprite sprite, float xp, float yp, int xOffset, int yOffset)
    {
	float xa; // x absolute
	float ya; // y absolute
	xp += this.xOffset - xOffset;
	yp += this.yOffset - yOffset;
	for (int y = 0; y < sprite.getHeight(); y++) {
	    ya = y + yp;
	    for (int x = 0; x < sprite.getWidth(); x++) {
		xa = x + xp;
		int color = sprite.getPixels()[x + y * sprite.getWidth()];
		if (color != ALPHA_COLOR && color != ALPHA_2_COLOR) { // color not pink (with alpha channel)
		    pixels[(int) xa + (int) ya * width] = color;
		}
	    }
	}
    }

    public void renderSprite(Sprite sprite, int xp, int yp, int color)
    {
	for (int y = 0; y < sprite.getHeight(); y++) {
	    int ya = y + yp;
	    for (int x = 0; x < sprite.getWidth(); x++) {
		int xa = x + xp;
		if (xa < 0 || xa >= width || ya < 0 || ya >= height)
		    continue;
		int col = sprite.getPixels()[x + y * sprite.getWidth()];
		if (col != ALPHA_COLOR && col != ALPHA_2_COLOR) {
		    pixels[xa + ya * width] = color;
		}
	    }
	}
    }

    public void renderTextCharacter(int xp, int yp, Sprite sprite, int color, boolean useOffsets)
    {
	int xa; // x absolute
	int ya; // y absolute

	if (useOffsets) {
	    xp += this.xOffset;
	    yp += this.yOffset;
	}

	for (int y = 0; y < sprite.getHeight(); y++) {
	    ya = y + yp;
	    for (int x = 0; x < sprite.getWidth(); x++) {
		xa = x + xp;
		if (xa < 0 || xa >= width || ya < 0 || ya >= height)
		    continue;
		int col = sprite.getPixels()[x + y * sprite.getWidth()];
		if (col != ALPHA_COLOR && col != ALPHA_2_COLOR)
		    pixels[xa + ya * width] = color;
	    }
	}
    }

    public void renderBounds(Rectangle rectangle, int color, int xOffset, int yOffset, int xPadding, int yPadding, boolean useOffsets)
    {
	int xa; // x absolute
	int ya; // y absolute
	int xp = rectangle.x + xOffset;
	int yp = rectangle.y + yOffset;
	int rectWidth = rectangle.width + (2 * xPadding);
	int rectHeight = rectangle.height + (2 * yPadding);
	xp -= xPadding;
	yp -= yPadding;
	if (useOffsets) {
	    xp += this.xOffset;
	    yp += this.yOffset;
	}

	// draw the vertical lines
	for (int y = 0; y < rectHeight; y++) {
	    ya = y + yp;
	    for (int x = 0; x < rectWidth; x += rectWidth - 1) {
		xa = x + xp;
		if (xa < 0 || xa >= width || ya < 0 || ya >= height)
		    continue;
		if (color != ALPHA_COLOR && color != ALPHA_2_COLOR) {
		    pixels[xa + ya * width] = color;
		    pixels[(xa + 1) + ya * width] = color;
		}
	    }
	}
	// draw the horizontal lines
	for (int x = 0; x < rectWidth + 1; x++) {
	    xa = x + xp;
	    for (int y = 0; y < rectHeight; y += rectHeight - 1) {
		ya = y + yp;
		if (xa < 0 || xa >= width || ya < 0 || ya >= height)
		    continue;
		if (color != ALPHA_COLOR && color != ALPHA_2_COLOR) {
		    pixels[xa + (ya - 1) * width] = color;
		    pixels[xa + ya * width] = color;
		}
	    }
	}
    }

    public void renderScreenPainterVisuals(boolean renderMazeGrid, boolean renderHoveredMazeTileLocation)
    {
	BufferedImage bufferedImage = new BufferedImage(GameApplication.WINDOW_WIDTH, GameApplication.WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
	ScreenPainter.g2d = bufferedImage.createGraphics();

	if (Game.getActiveSubMode() == SubMode.CUSTOM_SPACE_PATHFINDING || Game.getActiveSubMode() == SubMode.MAZE_PATHFINDING) {
	    if (renderMazeGrid) {
		ScreenPainter.drawStartNode(false);
	    }
	    ScreenPainter.drawGoalNode(false);
	    ScreenPainter.drawExpandedNodes();
	    if (Game.getActiveSubMode() == SubMode.CUSTOM_SPACE_PATHFINDING) {
		if (renderMazeGrid) {
		    ScreenPainter.drawMazeGrid();
		}
		ScreenPainter.drawGoalNode(false);
		if (PathfindingController.numberOfExpandedNodes == SearchAlgorithm.expandedNodesDrawable.size()) {
		    ScreenPainter.drawLineSolutionPath();
		}
	    }
	} else if (Game.getActiveSubMode() == SubMode.AI_ALGORITHMS_STEPWISE_LEARNING) {
	    if (AIAlgorithmsStepwiseLearningController.stepwiseExecutionMode) {
		ScreenPainter.drawAlgorithmInfo();
	    } else {
		ScreenPainter.drawExpandedNodes();
		ScreenPainter.drawGhostChasePath();
	    }
	} else if (Game.getActiveSubMode() == SubMode.AI_ALGORITHMS_INTERACTIVE_LEARNING) {
	    if (renderMazeGrid) {
		ScreenPainter.drawStartNode(false);
	    }
	    ScreenPainter.drawGoalNode(false);
	    ScreenPainter.drawExpandedNodes();
	    ScreenPainter.drawUnexpandedNodes();
	    ScreenPainter.drawUserInteractionVisuals();
	} else if ((Game.getActiveSubMode() == SubMode.CLASSIC_TSP || Game.getActiveSubMode() == SubMode.MAZE_TSP) && TSPSolverController.renderVisuals) {
	    ScreenPainter.drawGraph();
	    ScreenPainter.drawLineSolutionPath();
	}

	if (renderMazeGrid && Game.getActiveSubMode() != SubMode.CUSTOM_SPACE_PATHFINDING) {
	    ScreenPainter.drawMazeGrid();
	}
	if (renderHoveredMazeTileLocation) {
	    ScreenPainter.drawHoveredMazeTileLocation();
	}

	int bufferedImagePixels[] = ((DataBufferInt) bufferedImage.getRaster().getDataBuffer()).getData();
	for (int i = 0; i < bufferedImagePixels.length; i++) {
	    if (bufferedImagePixels[i] != BLACK_COLOR) {
		pixels[i] = bufferedImagePixels[i];
	    }
	}
	ScreenPainter.g2d.dispose();

    }

    public void decreaseBrightnessOfAllPixels(int value)
    {
	int r = 0;
	int g = 0;
	int b = 0;

	for (int i = 0; i < pixels.length; i++) {
	    if (pixels[i] != BLACK_COLOR) {
		r = (pixels[i] & 0xFF0000) >> 16;
		g = (pixels[i] & 0xFF00) >> 8;
		b = pixels[i] & 0xFF;

		r = (r - value > 0) ? r - value : 0;
		g = (g - value > 0) ? g - value : 0;
		b = (b - value > 0) ? b - value : 0;

		pixels[i] = (r << 16) | (g << 8) | b;
	    }
	}
    }

    public static int decreaseBrightnessOfSingleColor(int color, int value)
    {
	if (color == BLACK_COLOR) {
	    return color;
	}

	int r, g, b;
	int newColor = 0;

	r = (color & 0xFF0000) >> 16;
	g = (color & 0xFF00) >> 8;
	b = color & 0xFF;

	r = (r - value > 0) ? r - value : 0;
	g = (g - value > 0) ? g - value : 0;
	b = (b - value > 0) ? b - value : 0;

	newColor = (r << 16) | (g << 8) | b;

	return newColor;
    }

    public static int getMiddleAlignStartPositionInWindow(int textWidth)
    {
	return (GameApplication.WINDOW_WIDTH - textWidth) / 2;
    }

    public static int getMiddleAlignStartPositionInArea(int areaWidth, int textWidth)
    {
	return (areaWidth - textWidth) / 2;
    }

    public int[] getPixels()
    {
	return pixels;
    }

    public void setPixels(int[] pixels)
    {
	this.pixels = pixels;
    }

    public int getXOffset()
    {
	return xOffset;
    }

    public int getYOffset()
    {
	return yOffset;
    }

    public void setOffsets(int xOffset, int yOffset)
    {
	this.xOffset = xOffset;
	this.yOffset = yOffset;
    }
}
