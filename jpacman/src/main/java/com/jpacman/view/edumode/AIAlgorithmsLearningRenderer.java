package com.jpacman.view.edumode;

import java.awt.Point;

import com.jpacman.Game;
import com.jpacman.GameApplication;
import com.jpacman.controller.edumode.AIAlgorithmsLearningController;
import com.jpacman.model.Maze;
import com.jpacman.model.Pacman;
import com.jpacman.model.Text;
import com.jpacman.model.UIButton;
import com.jpacman.model.edumode.SearchAlgorithm;
import com.jpacman.view.TextRenderer;
import com.jpacman.view.graphics.Screen;

public class AIAlgorithmsLearningRenderer extends EducationalModeRenderer
{
    // ********************* Class (static) variables ********************** //
    // ************************** Constants ************************** //
    protected static final String TITLE_STRING = "Learning of AI Search Algorithms";
    protected static final String TITLE_STRING_1 = "Stepwise Execution of Algorithms";
    protected static final String TITLE_STRING_2 = "Interactive Execution of Algorithms";
    protected static final String EMPTY_DESCRIPTION_STRING = "";
    protected static final String DESCRIPTION_STRING_1 = "" + //
    " In this mode, you (try to) learn how each algoritmh works, by executing it\n" + //
    "stepwise and observing the action that is taking place in each step alongside\n" + //
    "                       with the generated search tree.";
    protected static final String DESCRIPTION_STRING_2 = "" + //
    "In this mode, you test your knowledge of the AI algorithms, by executing each\n" + //
    "algorithm interactively (following the given instructions), and trying to make\n" + //
    "                         as few mistakes as possible.";

    public static final UIButton[] modes = new UIButton[AIAlgorithmsLearningController.NUMBER_OF_SUBMODES + 1];
    public static final String[] descriptions = new String[AIAlgorithmsLearningController.NUMBER_OF_SUBMODES + 1];
    public static final Text modeSelectionText = new Text("Select a mode", Text.DEFAULT_FONT_LARGE, Screen.YELLOW_COLOR);
    public static final Text stepwiseExecutionText = new Text(TITLE_STRING_1, Text.DEFAULT_FONT_LARGE);
    public static final Text interactiveExecutionText = new Text(TITLE_STRING_2, Text.DEFAULT_FONT_LARGE);
    public static final UIButton stepwiseExecutionButton = new UIButton(new Point(Maze.WIDTH, Maze.HEIGHT / 2 + 15), true, stepwiseExecutionText, true);
    public static final UIButton interactiveExecutionButton = new UIButton(new Point(Maze.WIDTH, (Maze.HEIGHT / 2) + 65), true, interactiveExecutionText, true);

    protected static final Text currentActionStatusLabelText = new Text("Current Action:\n", Text.DEFAULT_FONT_SMALL, 0xCCCC7A);
    protected static final Text currentActionText = new Text("", Text.DEFAULT_FONT_SMALL, 0xFFFF99);
    protected static final Text startNodeText = new Text("", Text.DEFAULT_FONT_SMALL, Screen.YELLOW_COLOR);
    protected static final Text goalNodeText = new Text("", Text.DEFAULT_FONT_SMALL, Screen.GREEN_COLOR);
    protected static final Text nodesInOpenListText = new Text("", Text.DEFAULT_FONT_SMALL, 0xFFBC44);

    // static initializer
    static {
	descriptions[AIAlgorithmsLearningController.STEPWISE_EXECUTION] = DESCRIPTION_STRING_1;
	descriptions[AIAlgorithmsLearningController.INTERACTIVE_EXECUTION] = DESCRIPTION_STRING_2;
	descriptions[AIAlgorithmsLearningController.NUMBER_OF_SUBMODES] = EMPTY_DESCRIPTION_STRING;
	modes[AIAlgorithmsLearningController.STEPWISE_EXECUTION] = stepwiseExecutionButton;
	modes[AIAlgorithmsLearningController.INTERACTIVE_EXECUTION] = interactiveExecutionButton;
	modes[AIAlgorithmsLearningController.NUMBER_OF_SUBMODES] = backButton;
    }

    protected final SearchAlgorithm[] searchAlgorithms;

    public AIAlgorithmsLearningRenderer(TextRenderer textRenderer, Pacman pacman, SearchAlgorithm[] searchAlgorithms)
    {
	super(textRenderer, pacman);
	this.searchAlgorithms = searchAlgorithms;
	title.setTextMessage(TITLE_STRING);
	description.setTextMessage(EMPTY_DESCRIPTION_STRING);
    }

    public void renderSubmodesTextStrings(Screen screen)
    {
	textRenderer.renderText(screen, modeSelectionText, Screen.getMiddleAlignStartPositionInArea(Maze.WIDTH, modeSelectionText.getWidth()), GameApplication.WINDOW_HEIGHT / 3, true);

	for (int i = 0; i < modes.length; i++) {
	    if (modes[i].isVisible()) {
		if (modes[i].hasFocus() || modes[i].isMouseHovered()) {
		    screen.renderBounds(modes[i].getBounds(), Screen.WHITE_COLOR, -1, 0, 8, 4, false);
		    modes[i].getText().setColor(Screen.YELLOW_COLOR);
		    description.setTextMessage(descriptions[i]);
		} else {
		    modes[i].getText().setColor(Screen.WHITE_COLOR);
		}
		textRenderer.renderText(screen, modes[i].getText(), modes[i].getBounds().x, modes[i].getBounds().y, false);
	    }
	}
    }

    @Override
    public void render(Screen screen)
    {
	if (Game.getActiveSubMode() == Game.SubMode.AI_ALGORITHMS_LEARNING_MENU) {

	    textRenderer.renderText(screen, title, Screen.getMiddleAlignStartPositionInWindow(title.getWidth()), title.getHeight(), false);

	    if (AIAlgorithmsLearningController.submode == -1) {
		// center align back menu item
		backButton.getBounds().x = Screen.getMiddleAlignStartPositionInWindow(backButton.getText().getWidth());
		renderSubmodesTextStrings(screen);
		textRenderer.renderText(screen, description, Screen.getMiddleAlignStartPositionInWindow(description.getWidth()), GameApplication.WINDOW_HEIGHT - 75, false);
	    }
	}
    }
}
