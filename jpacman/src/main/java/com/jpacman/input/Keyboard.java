package com.jpacman.input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Stack;

import com.jpacman.model.MovableGameObject.Direction;

public class Keyboard implements KeyListener
{
    // user input fields
    public boolean arrowPressed, up, down, left, right, enter;
    public boolean returnedToMainMenu, mute, muteToggled, pause, pauseToggled, quit, quitted, collision;
    public boolean debug, info, grid;
    public boolean routeTiles, ghostsHouseRouteTiles, intersectionsTiles, crossroadsTiles, targetTiles;

    private Stack<Direction> userInput;
    private Direction newUserInput;

    public Keyboard()
    {
	arrowPressed = false;
	up = down = left = right = false;
	enter = false;
	returnedToMainMenu = false;
	mute = true;
	muteToggled = false;
	pause = false;
	pauseToggled = false;
	quit = false;
	quitted = false;
	collision = true;
	debug = false;
	info = false;
	grid = true;
	routeTiles = false;
	ghostsHouseRouteTiles = false;
	intersectionsTiles = false;
	crossroadsTiles = false;
	targetTiles = false;
	userInput = new Stack<Direction>();
	newUserInput = null;
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
	int key = e.getKeyCode();

	Direction input = null;
	switch (key) {
	    case KeyEvent.VK_UP:
	    case KeyEvent.VK_W:
		input = Direction.UP;
		up = true;
		arrowPressed = true;
		break;
	    case KeyEvent.VK_DOWN:
	    case KeyEvent.VK_S:
		input = Direction.DOWN;
		down = true;
		arrowPressed = true;
		break;
	    case KeyEvent.VK_LEFT:
	    case KeyEvent.VK_A:
		input = Direction.LEFT;
		arrowPressed = true;
		break;
	    case KeyEvent.VK_RIGHT:
	    case KeyEvent.VK_D:
		input = Direction.RIGHT;
		arrowPressed = true;
		break;
	    case KeyEvent.VK_ENTER:
		enter = true;
		break;
	    // case KeyEvent.VK_D:
	    // debug = debug ? false : true;
	    // break;
	    case KeyEvent.VK_0:
		info = info ? false : true;
		grid = grid ? false : true;
		routeTiles = routeTiles ? false : true;
		ghostsHouseRouteTiles = ghostsHouseRouteTiles ? false : true;
		intersectionsTiles = intersectionsTiles ? false : true;
		crossroadsTiles = crossroadsTiles ? false : true;
		targetTiles = targetTiles ? false : true;
		break;
	    case KeyEvent.VK_1:
		info = info ? false : true;
		break;
	    case KeyEvent.VK_2:
		grid = grid ? false : true;
		break;
	    case KeyEvent.VK_3:
		routeTiles = routeTiles ? false : true;
		break;
	    case KeyEvent.VK_4:
		ghostsHouseRouteTiles = ghostsHouseRouteTiles ? false : true;
		break;
	    case KeyEvent.VK_5:
		intersectionsTiles = intersectionsTiles ? false : true;
		break;
	    case KeyEvent.VK_6:
		crossroadsTiles = crossroadsTiles ? false : true;
		break;
	    case KeyEvent.VK_7:
		targetTiles = targetTiles ? false : true;
		break;
	    case KeyEvent.VK_M:
		mute = mute ? false : true;
		muteToggled = true;
		break;
	    case KeyEvent.VK_P:
		pause = pause ? false : true;
		pauseToggled = true;
		quit = false;
		break;
	    case KeyEvent.VK_Q:
		quit = true;
		pause = false;
		break;
	    case KeyEvent.VK_Y:
		if (quit) {
		    quitted = true;
		}
		break;
	    case KeyEvent.VK_N:
		if (quit) {
		    quitted = false;
		    quit = false;
		}
		break;
	    case KeyEvent.VK_C:
		collision = collision ? false : true;
	}

	if (input != null) {
	    storeUserInput(input);
	}
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
    }

    @Override
    public void keyTyped(KeyEvent e)
    {
    }

    public void resetKeys()
    {
	returnedToMainMenu = mute = muteToggled = pause = pauseToggled = quit = quitted = false;
	collision = true;
    }

    public boolean isThereNewUserInput()
    {
	if (userInput.empty())
	    return false;
	else
	    return true;
    }

    public void storeUserInput(Direction input)
    {
	// System.out.println("pushing " + input + " into stack");
	userInput.push(input);
    }

    public Direction getLastUserInput()
    {
	newUserInput = userInput.peek();
	// System.out.println("peeking " + newUserInput + " from stack");
	return newUserInput;
    }

    public void clearAllPreviousUserInputs()
    {
	// System.out.println("num of inputs = " + userInput.size() + "\n");
	userInput.clear();
    }
}
