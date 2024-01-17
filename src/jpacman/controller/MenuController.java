package jpacman.controller;

import jpacman.Game;
import jpacman.input.Keyboard;
import jpacman.input.Mouse;
import jpacman.model.UIButton;
import jpacman.view.MenuRenderer;
import jpacman.view.graphics.Screen;

public class MenuController implements Controller
{
    public static final int MAIN_MENU = 0;
    public static final int PLAY_MODE_MENU = 1;
    public static final int EDUCATIONAL_MODE_MENU = 2;
    public static final int ABOUT_MENU = 3;
    public static final int HIGH_SCORES_MENU = 4;
    public static final int CONTROLS_MENU = 5;

    private final MenuRenderer menuRenderer;
    private final Keyboard keyboard;
    private int currentMenu = MAIN_MENU;
    private int lastFocusedMenuItem = -1;

    public MenuController(Keyboard keyboard, MenuRenderer menuRenderer)
    {
	this.keyboard = keyboard;
	this.menuRenderer = menuRenderer;
    }

    @Override
    public void update(double delta)
    {
	switch (currentMenu) {
	    case MAIN_MENU:
		mainMenu();
		break;
	    case PLAY_MODE_MENU:
		playModeMenu();
		break;
	    case EDUCATIONAL_MODE_MENU:
		educationalModeMenu();
		break;
	    case ABOUT_MENU:
		aboutMenu();
		break;
	    case HIGH_SCORES_MENU:
		highScoresMenu();
		break;
	    case CONTROLS_MENU:
		controlsMenu();
		break;
	}
	keyboard.enter = false;
	// delay mouse click check
	Mouse.setButton(-1);
    }

    private void mainMenu()
    {
	checkAndSetButtonFocus(menuRenderer.getMainMenuItems());
	checkForMouseHover(menuRenderer.getMainMenuItems());

	for (int i = 0; i < MenuRenderer.NUMBER_OF_MAIN_MENU_BUTTONS; i++) {
	    if (menuRenderer.getMainMenuItems()[i].hasFocus() || menuRenderer.getMainMenuItems()[i].isMouseHovered()) {
		if (keyboard.enter || isMouseInsideBoundsWhenClicked(menuRenderer.getMainMenuItems()[i])) {
		    menuRenderer.getMainMenuItems()[i].setFocus(false);
		    switch (i) {
			case MenuRenderer.PLAY_MODE_BUTTON:
			    hideMenu(menuRenderer.getMainMenuItems());
			    showMenu(menuRenderer.getPlayModeMenuItems());
			    MenuRenderer.playMode.setClicked(true);
			    lastFocusedMenuItem = -1;
			    currentMenu = PLAY_MODE_MENU;
			    break;
			case MenuRenderer.EDUCATIONAL_MODE_BUTTON:
			    hideMenu(menuRenderer.getMainMenuItems());
			    showMenu(menuRenderer.getEducationalModeMenuItems());
			    MenuRenderer.educationalMode.setClicked(true);
			    lastFocusedMenuItem = -1;
			    currentMenu = EDUCATIONAL_MODE_MENU;
			    break;
			case MenuRenderer.ABOUT_BUTTON:
			    currentMenu = ABOUT_MENU;
			    break;
			case MenuRenderer.EXIT_BUTTON:
			    // TODO implement proper exit
			    System.exit(0);
		    }
		}
	    }
	}
    }

    private void playModeMenu()
    {
	checkAndSetButtonFocus(menuRenderer.getPlayModeMenuItems());
	checkForMouseHover(menuRenderer.getPlayModeMenuItems());

	for (int i = 0; i < MenuRenderer.NUMBER_OF_PLAY_MODE_BUTTONS; i++) {
	    if (menuRenderer.getPlayModeMenuItems()[i].hasFocus() || menuRenderer.getPlayModeMenuItems()[i].isMouseHovered()) {
		if (keyboard.enter || isMouseInsideBoundsWhenClicked(menuRenderer.getPlayModeMenuItems()[i])) {
		    menuRenderer.getPlayModeMenuItems()[i].setFocus(false);
		    switch (i) {
			case MenuRenderer.START_GAME_BUTTON:
			    Game.setActiveMode(Game.Mode.PLAY);
			    Game.started = true;
			    Game.ready = true;
			    lastFocusedMenuItem = -1;
			    break;
			case MenuRenderer.HIGH_SCORES_BUTTON:
			    currentMenu = HIGH_SCORES_MENU;
			    break;
			case MenuRenderer.CONTROLS_BUTTON:
			    currentMenu = CONTROLS_MENU;
			    break;
			case MenuRenderer.BACK_BUTTON:
			    MenuRenderer.playMode.setClicked(false);
			    MenuRenderer.back.setVisible(false);
			    hideMenu(menuRenderer.getPlayModeMenuItems());
			    showMenu(menuRenderer.getMainMenuItems());
			    lastFocusedMenuItem = -1;
			    currentMenu = MAIN_MENU;
			    break;
		    }
		}
	    }
	}
    }

    private void educationalModeMenu()
    {
	checkAndSetButtonFocus(menuRenderer.getEducationalModeMenuItems());
	checkForMouseHover(menuRenderer.getEducationalModeMenuItems());

	for (int i = 0; i < MenuRenderer.NUMBER_OF_EDUCATIONAL_MODE_BUTTONS; i++) {
	    if (menuRenderer.getEducationalModeMenuItems()[i].hasFocus() || menuRenderer.getEducationalModeMenuItems()[i].isMouseHovered()) {
		if (keyboard.enter || isMouseInsideBoundsWhenClicked(menuRenderer.getEducationalModeMenuItems()[i])) {
		    menuRenderer.getEducationalModeMenuItems()[i].setFocus(false);
		    switch (i) {
			case MenuRenderer.PATHFINDING_VISUALIZATION_BUTTON:
			    Game.setActiveMode(Game.Mode.EDUCATIONAL);
			    Game.setActiveSubMode(Game.SubMode.PATHFINDING_MENU);
			    break;
			case MenuRenderer.AI_ALGORITHMS_LEARNING_BUTTON:
			    Game.setActiveMode(Game.Mode.EDUCATIONAL);
			    Game.setActiveSubMode(Game.SubMode.AI_ALGORITHMS_LEARNING_MENU);
			    break;
			case MenuRenderer.TSP_BUTTON:
			    Game.setActiveMode(Game.Mode.EDUCATIONAL);
			    Game.setActiveSubMode(Game.SubMode.TSP_MENU);
			    break;
			case MenuRenderer.BACK_BUTTON:
			    MenuRenderer.educationalMode.setClicked(false);
			    MenuRenderer.back.setVisible(false);
			    hideMenu(menuRenderer.getEducationalModeMenuItems());
			    showMenu(menuRenderer.getMainMenuItems());
			    lastFocusedMenuItem = -1;
			    currentMenu = MAIN_MENU;
			    break;
		    }
		}
	    }
	}
    }

    private void aboutMenu()
    {
	MenuRenderer.about.setClicked(true);
	hideMenu(menuRenderer.getMainMenuItems());
	MenuRenderer.back.setVisible(true);
	MenuRenderer.back.setFocus(true);

	goBackToPreviousMenu(MenuRenderer.about, menuRenderer.getMainMenuItems(), MAIN_MENU);
    }

    private void highScoresMenu()
    {
	MenuRenderer.highScores.setClicked(true);
	hideMenu(menuRenderer.getPlayModeMenuItems());
	MenuRenderer.back.setVisible(true);
	MenuRenderer.back.setFocus(true);

	goBackToPreviousMenu(MenuRenderer.highScores, menuRenderer.getPlayModeMenuItems(), PLAY_MODE_MENU);
    }

    private void controlsMenu()
    {
	MenuRenderer.controls.setClicked(true);
	hideMenu(menuRenderer.getPlayModeMenuItems());
	MenuRenderer.back.setVisible(true);
	MenuRenderer.back.setFocus(true);

	goBackToPreviousMenu(MenuRenderer.controls, menuRenderer.getPlayModeMenuItems(), PLAY_MODE_MENU);
    }

    private void goBackToPreviousMenu(UIButton menuItem, UIButton[] menuItems, int previousMenu)
    {
	checkForMouseHover(menuItems);

	UIButton back = menuItems[menuItems.length - 1]; // back is last element in both menu items arrays
	if (keyboard.enter || isMouseInsideBoundsWhenClicked(back)) {
	    Mouse.setButton(-1);
	    MenuRenderer.back.setFocus(false);
	    MenuRenderer.back.setVisible(false);
	    menuItem.setClicked(false);
	    showMenu(menuItems);
	    lastFocusedMenuItem = -1;
	    switch (previousMenu) {
		case PLAY_MODE_MENU:
		    MenuRenderer.playMode.setClicked(true);
		    break;
		case EDUCATIONAL_MODE_MENU:
		    MenuRenderer.educationalMode.setClicked(true);
		    break;
	    }
	    currentMenu = previousMenu;
	}
    }

    private void checkAndSetButtonFocus(UIButton[] menuItems)
    {
	if (keyboard.arrowPressed) {
	    if (lastFocusedMenuItem == -1) {
		menuItems[0].setFocus(true);
		lastFocusedMenuItem = 0;
	    } else if (!menuItems[lastFocusedMenuItem].hasFocus()) {
		menuItems[lastFocusedMenuItem].setFocus(true);
	    } else {
		if (keyboard.down) {
		    if (lastFocusedMenuItem < menuRenderer.getMainMenuItems().length - 1) {
			menuItems[lastFocusedMenuItem].setFocus(false);
			menuItems[++lastFocusedMenuItem].setFocus(true);
		    }
		    keyboard.down = false;
		} else if (keyboard.up) {
		    if (lastFocusedMenuItem > 0) {
			menuItems[lastFocusedMenuItem].setFocus(false);
			menuItems[--lastFocusedMenuItem].setFocus(true);
		    }
		    keyboard.up = false;
		}
	    }
	    keyboard.arrowPressed = false;
	}
    }

    private void checkForMouseHover(UIButton[] menuItems)
    {
	for (int i = 0; i < menuItems.length; i++) {
	    if (menuItems[i].getBounds().contains(Mouse.getCoordinates())) {
		menuItems[i].setMouseHovered(true);
		if (lastFocusedMenuItem > -1) {
		    menuItems[lastFocusedMenuItem].setFocus(false);
		}
	    } else {
		menuItems[i].setMouseHovered(false);
	    }
	}
    }

    public static boolean isButtonClicked(UIButton button)
    {
	button.setMouseHovered(false);
	if (button.getBounds().contains(Mouse.getCoordinates())) {
	    button.setMouseHovered(true);
	    if (Mouse.getButton() == Mouse.LEFT_BUTTON) {
		Mouse.setButton(-1);
		return true;
	    }
	}

	return false;
    }

    private boolean isMouseInsideBoundsWhenClicked(UIButton menuItem)
    {
	return (Mouse.getButton() == Mouse.LEFT_BUTTON && menuItem.isMouseHovered() ? true : false);
    }

    private void hideMenu(UIButton[] menuItems)
    {
	for (int i = 0; i < menuItems.length; i++) {
	    menuItems[i].setVisible(false);
	}
    }

    private void showMenu(UIButton[] menuItems)
    {
	for (int i = 0; i < menuItems.length; i++) {
	    menuItems[i].setVisible(true);
	}
    }

    public void renderMenu(Screen screen)
    {
	menuRenderer.render(screen);
    }

    public int getCurrentMenu()
    {
	return currentMenu;
    }

    public void setCurrentMenu(int currentMenu)
    {
	this.currentMenu = currentMenu;
    }
}
