package com.jpacman.view;

import java.awt.Point;
import java.text.DecimalFormat;

import com.jpacman.GameApplication;
import com.jpacman.controller.ScoreController;
import com.jpacman.model.Font;
import com.jpacman.model.HighScoreEntry;
import com.jpacman.model.Text;
import com.jpacman.model.UIButton;
import com.jpacman.view.graphics.Screen;
import com.jpacman.view.graphics.Sprite;
import com.jpacman.view.graphics.SpriteSheet;

public class MenuRenderer implements Renderer
{
    // ********************* Class (static) variables ********************** //
    // ************************** Constants ************************** //
    public static final int NUMBER_OF_MAIN_MENU_BUTTONS = 4;
    public static final int PLAY_MODE_BUTTON = 0;
    public static final int EDUCATIONAL_MODE_BUTTON = 1;
    public static final int ABOUT_BUTTON = 2;
    public static final int EXIT_BUTTON = 3;
    public static final int NUMBER_OF_PLAY_MODE_BUTTONS = 4;
    public static final int START_GAME_BUTTON = 0;
    public static final int HIGH_SCORES_BUTTON = 1;
    public static final int CONTROLS_BUTTON = 2;
    public static final int NUMBER_OF_EDUCATIONAL_MODE_BUTTONS = 4;
    public static final int PATHFINDING_VISUALIZATION_BUTTON = 0;
    public static final int AI_ALGORITHMS_LEARNING_BUTTON = 1;
    public static final int TSP_BUTTON = 2;
    public static final int BACK_BUTTON = 3;
    public static final int NUMBER_OF_ALL_MENU_BUTTONS = 11;
    public static final int Y_OFFSET = 50;

    public static final int GAME_TITLE_XOFFSET = (GameApplication.WINDOW_WIDTH - SpriteSheet.NUMBER_OF_SPRITES_IN_A_ROW * Sprite.SPRITE_SIZE) / 2;
    public static final int GAME_TITLE_YOFFSET = Sprite.SPRITE_SIZE;

    public static final String ABOUT_TEXT_STRING = "" + //
    "    Pac-Man is a famous arcade game developed by\n" + //
    " Namco and first released in Japan on May 22, 1980.\n\n" + //
    " As part of my thesis, here is a hybrid version of\n" + //
    "    the game, written from scratch in pure Java.\n" + //
    "          This version includes two modes:\n" + //
    "  the \"Play Mode\", in which the game is played and\n" + //
    " the \"Educational Mode\", where the user can see how\n" + //
    "basic (search) algorithms of Artificial Intelligence\n" + //
    "  are applied in practice and learn how they work.\n\n" + //
    "              Sprites by Rando Birbo\n\n" + //
    "           (C) Panagiotis Giannakopoulos";

    public static final String ABOUT_TEXT_STRING_2 = "" + //
    "  Pac-man is an arcade game developed by\n" + //
    "Namco and first released in Japan on May 22,\n" + //
    "  1980. Here is a recreated version for the\n" + //
    "    desktop written in pure Java by Panos\n" + //
    "                Giannakopoulos.\n\n" + //
    "The idea is a simple one - guide Pacman around\n" + //
    "  the maze and eat all the little white dots\n" + //
    "whilst avoiding those nasty ghosts. If you eat\n" + //
    "   a Power Pill, you can eat the ghosts!\n" + //
    "Occasionally, a fruit appears which gives you\n" + //
    " a bonus score when eaten. So get munching!";

    public static final String CONTROLS_TEXT_STRING = "" + //
    "Arrow or WASD keys for Pac-man\n\n" + //
    "Q: Quit game\n\n" + //
    "P: Pause/Unpause game\n\n" + //
    "M: Mute/Unmute sound";
    // *************************************************************** //

    public static final Sprite[] gameTitle = new Sprite[SpriteSheet.NUMBER_OF_SPRITES_IN_A_ROW * 3];
    public static final Font quoteFont = new Font("Comic Sans MS", Font.SMALL_SIZE, Font.DEFAULT_SPACING_SMALL);
    public static final Text quoteText = new Text("\"not only for fun...\"");

    static {
	for (int i = 0; i < SpriteSheet.NUMBER_OF_SPRITES_IN_A_ROW; i++) {
	    gameTitle[i] = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, i, 0);
	    gameTitle[i + SpriteSheet.NUMBER_OF_SPRITES_IN_A_ROW] = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, i, 1);
	    gameTitle[i + 2 * SpriteSheet.NUMBER_OF_SPRITES_IN_A_ROW] = new Sprite(SpriteSheet.icons, Sprite.SPRITE_SIZE, i, 2);
	}
	quoteFont.setItalic(true);
	quoteText.setFont(quoteFont);
	quoteText.setColor(Screen.YELLOW_COLOR);
    }

    public static final Text playModeText = new Text("Play Mode");
    public static final Text educationalModeText = new Text("Educational Mode");
    public static final Text abouteText = new Text("About");
    public static final Text exitText = new Text("Exit");
    public static final Text startGameText = new Text("Start Game");
    public static final Text highScoresText = new Text("High Scores");
    public static final Text controlsText = new Text("Controls");
    public static final Text pathfindingText = new Text("Pathfinding Visualization");
    public static final Text aiAlgorithmsLearningText = new Text("AI Algorithms Learning");
    public static final Text tspText = new Text("Travelling Salesman Problem");
    public static final Text backText = new Text("Back");
    public static final Text aboutWallText = new Text(ABOUT_TEXT_STRING, Text.DEFAULT_FONT_SMALL);
    public static final Text controlsWallText = new Text(CONTROLS_TEXT_STRING, Text.DEFAULT_FONT_MEDIUM);

    // main menu items
    public static UIButton playMode = new UIButton(new Point(GameApplication.MENU_X_OFFSET, GameApplication.MENU_Y_OFFSET + Y_OFFSET), true, playModeText, true);
    public static UIButton educationalMode = new UIButton(new Point(GameApplication.MENU_X_OFFSET, GameApplication.MENU_Y_OFFSET + 2 * Y_OFFSET), true, educationalModeText, true);
    public static UIButton about = new UIButton(new Point(GameApplication.MENU_X_OFFSET, GameApplication.MENU_Y_OFFSET + 3 * Y_OFFSET), true, abouteText, true);
    public static UIButton exit = new UIButton(new Point(GameApplication.MENU_X_OFFSET, GameApplication.MENU_Y_OFFSET + 5 * Y_OFFSET), true, exitText, true);
    // play mode menu items
    public static UIButton startGame = new UIButton(new Point(GameApplication.MENU_X_OFFSET, GameApplication.MENU_Y_OFFSET + Y_OFFSET), true, startGameText, false);
    public static UIButton highScores = new UIButton(new Point(GameApplication.MENU_X_OFFSET, GameApplication.MENU_Y_OFFSET + 2 * Y_OFFSET), true, highScoresText, false);
    public static UIButton controls = new UIButton(new Point(GameApplication.MENU_X_OFFSET, GameApplication.MENU_Y_OFFSET + 3 * Y_OFFSET), true, controlsText, false);
    // educational mode menu items
    public static UIButton pathFinding = new UIButton(new Point(GameApplication.MENU_X_OFFSET, GameApplication.MENU_Y_OFFSET + Y_OFFSET), true, pathfindingText, false);
    public static UIButton aiAlgorithmsLearning = new UIButton(new Point(GameApplication.MENU_X_OFFSET, GameApplication.MENU_Y_OFFSET + 2 * Y_OFFSET), true, aiAlgorithmsLearningText, false);
    public static UIButton tsp = new UIButton(new Point(GameApplication.MENU_X_OFFSET, GameApplication.MENU_Y_OFFSET + 3 * Y_OFFSET), true, tspText, false);
    public static UIButton back = new UIButton(new Point(GameApplication.MENU_X_OFFSET, GameApplication.MENU_Y_OFFSET + 5 * Y_OFFSET), true, backText, false);

    // ************************* Instance variables ************************ //
    private final TextRenderer textRenderer;
    private final UIButton[] allMenuButtons;
    private final UIButton[] mainMenuButtons;
    private final UIButton[] playModeButtons;
    private final UIButton[] educationalModeButtons;

    public MenuRenderer(TextRenderer textRenderer)
    {
	this.textRenderer = textRenderer;
	allMenuButtons = new UIButton[NUMBER_OF_ALL_MENU_BUTTONS];
	allMenuButtons[0] = playMode;
	allMenuButtons[1] = educationalMode;
	allMenuButtons[2] = about;
	allMenuButtons[3] = exit;
	allMenuButtons[4] = startGame;
	allMenuButtons[5] = highScores;
	allMenuButtons[6] = controls;
	allMenuButtons[7] = pathFinding;
	allMenuButtons[8] = aiAlgorithmsLearning;
	allMenuButtons[9] = tsp;
	allMenuButtons[10] = back;
	mainMenuButtons = new UIButton[NUMBER_OF_MAIN_MENU_BUTTONS];
	mainMenuButtons[0] = playMode;
	mainMenuButtons[1] = educationalMode;
	mainMenuButtons[2] = about;
	mainMenuButtons[3] = exit;
	playModeButtons = new UIButton[NUMBER_OF_PLAY_MODE_BUTTONS];
	playModeButtons[0] = startGame;
	playModeButtons[1] = highScores;
	playModeButtons[2] = controls;
	playModeButtons[3] = back;
	educationalModeButtons = new UIButton[NUMBER_OF_EDUCATIONAL_MODE_BUTTONS];
	educationalModeButtons[0] = pathFinding;
	educationalModeButtons[1] = aiAlgorithmsLearning;
	educationalModeButtons[2] = tsp;
	educationalModeButtons[3] = back;
    }

    @Override
    public void render(Screen screen)
    {
	for (int i = 0; i < SpriteSheet.NUMBER_OF_SPRITES_IN_A_ROW; i++) {
	    screen.renderSprite(gameTitle[i], GAME_TITLE_XOFFSET + Sprite.SPRITE_SIZE * i, GAME_TITLE_YOFFSET, Screen.YELLOW_COLOR);
	    screen.renderSprite(gameTitle[i + SpriteSheet.NUMBER_OF_SPRITES_IN_A_ROW], GAME_TITLE_XOFFSET + Sprite.SPRITE_SIZE * i, GAME_TITLE_YOFFSET + Sprite.SPRITE_SIZE, Screen.YELLOW_COLOR);
	    screen.renderSprite(gameTitle[i + 2 * SpriteSheet.NUMBER_OF_SPRITES_IN_A_ROW], GAME_TITLE_XOFFSET + Sprite.SPRITE_SIZE * i, GAME_TITLE_YOFFSET + (2 * Sprite.SPRITE_SIZE), Screen.YELLOW_COLOR);
	}
	textRenderer.renderText(screen, quoteText, Screen.getMiddleAlignStartPositionInWindow(quoteText.getWidth()), 4 * Sprite.SPRITE_SIZE, false);

	for (UIButton menuButton : allMenuButtons) {
	    menuButton.render(textRenderer, screen);
	}

	if (playMode.isClicked()) {
	    playMode.getText().setColor(Screen.YELLOW_COLOR);
	    textRenderer.renderText(screen, playMode.getText(), playMode.getBounds().x, playMode.getBounds().y - 100, false);
	} else if (educationalMode.isClicked()) {
	    educationalMode.getText().setColor(Screen.YELLOW_COLOR);
	    textRenderer.renderText(screen, educationalMode.getText(), educationalMode.getBounds().x, playMode.getBounds().y - 100, false);
	}

	if (about.isClicked()) {
	    about.getText().setColor(Screen.YELLOW_COLOR);
	    textRenderer.renderText(screen, about.getText(), about.getBounds().x, playMode.getBounds().y - 120, false);
	    int xPos = Screen.getMiddleAlignStartPositionInWindow(aboutWallText.getWidth());
	    textRenderer.renderText(screen, aboutWallText, xPos, playMode.getBounds().y - 70, false);
	}

	if (highScores.isClicked()) {
	    playMode.setClicked(false);
	    highScores.getText().setColor(Screen.YELLOW_COLOR);
	    highScores.getText().setTextMessage("Top High Scores");
	    textRenderer.renderText(screen, highScores.getText(), Screen.getMiddleAlignStartPositionInWindow(highScores.getText().getWidth()), highScores.getBounds().y - 250, false);
	    highScores.getText().setTextMessage("High Scores");

	    textRenderer.renderText(screen, new Text("    Name        Date        Score", Text.DEFAULT_FONT_MEDIUM, 0xFFFF00), highScores.getBounds().x - 150, highScores.getBounds().y - 190, false);
	    DecimalFormat df = new DecimalFormat("#,###,###");
	    for (int i = 0; i < ScoreController.topHighScores.size(); i++) {
		HighScoreEntry entry = ScoreController.topHighScores.get(i);
		int gap1 = 4 - (String.valueOf(i + 1).length() + 1);
		int gap2 = 12 - entry.getName().length();
		int gap3 = 12 - entry.getDate().length();
		String space = " ";
		String strXGap1 = new String(new char[gap1]).replace("\0", space);
		String strXGap2 = new String(new char[gap2]).replace("\0", space);
		String strXGap3 = new String(new char[gap3]).replace("\0", space);
		String highScore = df.format(new Integer(entry.getHighScore()));
		Text entryText = new Text((i + 1) + "." + strXGap1 + entry.getName() + strXGap2 + entry.getDate() + strXGap3 + highScore, Text.DEFAULT_FONT_MEDIUM);
		textRenderer.renderText(screen, entryText, highScores.getBounds().x - 150, highScores.getBounds().y - 180 + (i + 1) * 26, false);
	    }
	}

	if (controls.isClicked()) {
	    playMode.setClicked(false);
	    controls.getText().setColor(Screen.YELLOW_COLOR);
	    textRenderer.renderText(screen, controls.getText(), controls.getBounds().x, playMode.getBounds().y - 100, false);
	    int xPos = Screen.getMiddleAlignStartPositionInWindow(controlsWallText.getWidth());
	    textRenderer.renderText(screen, controlsWallText, xPos, playMode.getBounds().y - 30, false);
	}
    }

    public UIButton[] getAllMenuItems()
    {
	return allMenuButtons;
    }

    public UIButton[] getMainMenuItems()
    {
	return mainMenuButtons;
    }

    public UIButton[] getPlayModeMenuItems()
    {
	return playModeButtons;
    }

    public UIButton[] getEducationalModeMenuItems()
    {
	return educationalModeButtons;
    }
}
