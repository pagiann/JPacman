package com.jpacman;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

import com.jpacman.controller.GameStatusController;
import com.jpacman.input.Keyboard;
import com.jpacman.input.Mouse;
import com.jpacman.model.Maze;
import com.jpacman.view.graphics.Screen;

public class GameApplication extends Canvas implements Runnable {
	// ********************* Class (static) variables ********************** //
	// ************************** Constants ************************** //
	public static final double UPDATES_PER_SECOND = 120.0;
	public static final int X_OFFSET = 20 * Maze.TILE;
	public static final int Y_OFFSET = 6 * Maze.TILE;
	// public static final int Y_OFFSET_2 = 2 * Y_OFFSET - 3 * Maze.TILE;
	// the width of the main window (in pixels)
	public static final int WINDOW_WIDTH = Maze.WIDTH + 2 * X_OFFSET;
	// the height of the main window (in pixels)
	public static final int WINDOW_HEIGHT = Maze.HEIGHT + 2 * Y_OFFSET;
	public static final int MENU_X_OFFSET = X_OFFSET;
	public static int MENU_Y_OFFSET = WINDOW_HEIGHT / 3 - Maze.TILE;
	private static final double SCALE = 1.0;
	private static final long serialVersionUID = 1L;
	private static final String title = "Jpacman Educational Edition";

	// ************************* Instance variables ************************ //
	private final JFrame frame;
	private final Thread gameThread;
	private BufferedImage image;
	private final int[] pixels;
	private final Screen screen;
	private final Keyboard keyboard;
	private final Mouse mouse;
	private final Game game;
	private boolean running = false;

	public GameApplication() {
		setPreferredSize(new Dimension((int) (WINDOW_WIDTH * SCALE), (int) (WINDOW_HEIGHT * SCALE)));
		frame = new JFrame();
		gameThread = new Thread(this, "Display");
		image = new BufferedImage(WINDOW_WIDTH, WINDOW_HEIGHT, BufferedImage.TYPE_INT_RGB);
		pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
		screen = new Screen(WINDOW_WIDTH, WINDOW_HEIGHT);
		screen.setOffsets(X_OFFSET, Y_OFFSET);
		keyboard = new Keyboard();
		addKeyListener(keyboard);
		mouse = new Mouse();
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		game = new Game(keyboard);
	}

	public synchronized void start() {
		if (running) {
			return;
		}

		running = true;
		gameThread.start();
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		long now;
		long timer = System.currentTimeMillis();
		final double nsPerTick = 1000000000.0 / UPDATES_PER_SECOND;
		double delta = 0;
		int frames = 0;

		// main game loop
		while (running) {
			now = System.nanoTime();
			delta = (now - lastTime) / nsPerTick;
			lastTime = now;

			// update game logic (capped at UPS)
			update(delta);

			// uncomment to save CPU cycles
			// try {
			// Thread.sleep(2);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// }

			// render game graphics (at unlimited FPS)
			render();
			frames++;

			// display UPS and FPS every second
			if (System.currentTimeMillis() - timer > 1000) {
				if (Game.started && !Game.paused && !Game.forcedPaused) {
					GameStatusController.increaseTotalSecondsPassedByOne();
				}
				timer += 1000;
				frame.setTitle(title + "  |  " + frames + " FPS");
				Game.setUpdates(0);
				frames = 0;
			}
		}

	}

	public void update(double delta) {
		// ScreenPainter.debug = (keyboard.debug) ? true : false;
		// ScreenPainter.info = (keyboard.info) ? true : false;
		// ScreenPainter.grid = (keyboard.grid) ? true : false;
		// ScreenPainter.routeTiles = (keyboard.routeTiles) ? true : false;
		// ScreenPainter.ghostsHouseRouteTiles = (keyboard.ghostsHouseRouteTiles) ? true
		// : false;
		// ScreenPainter.intersectionsTiles = (keyboard.intersectionsTiles) ? true :
		// false;
		// ScreenPainter.crossroadsTiles = (keyboard.crossroadsTiles) ? true : false;
		// ScreenPainter.targetTiles = (keyboard.targetTiles) ? true : false;

		game.updateWorld(delta);
	}

	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if (bs == null) {
			createBufferStrategy(3); // triple buffering
			return;
		}

		// clear all previously rendered pixels
		screen.clear();

		// render all game world objects
		game.renderWorld(screen);

		// set the new pixels to be drawn
		if (Game.paused) {
			screen.decreaseBrightnessOfAllPixels(75);
		}
		for (int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.getPixels()[i];
		}

		Graphics g = bs.getDrawGraphics();
		// //////////// all graphics are drown here /////////////
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		// //////////////////////////////////////////////////////////
		g.dispose();
		bs.show();
	}

	public static void main(String[] args) {
		GameApplication gameApp = new GameApplication();
		gameApp.frame.setResizable(false);
		gameApp.frame.setTitle(title);
		gameApp.frame.add(gameApp);
		gameApp.frame.pack();
		gameApp.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gameApp.frame.setLocationRelativeTo(null);
		gameApp.requestFocusInWindow();
		gameApp.frame.setVisible(true);

		gameApp.start();
	}
}
