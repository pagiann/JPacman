package jpacman.controller;

import jpacman.Game;
import jpacman.util.Timer;
import jpacman.view.TextRenderer;
import jpacman.view.graphics.Screen;

public class TextController implements Controller
{
    // ********************* Class (static) variables ********************** //
    public static Timer timer = new Timer();
    // ************************** Constants ************************** //
    private static final int LEVEL_FLAG = 1;
    private static final int READY_FLAG = 2;
    private static final int PAUSED_FLAG = 4;
    private static final int QUIT_FLAG = 8;
    private static final int GAME_OVER__FLAG = 16;

    // ************************* Instance variables ************************ //
    private final TextRenderer textRenderer;
    private final LevelController levelController;

    public TextController(TextRenderer textRenderer, LevelController levelController)
    {
	this.textRenderer = textRenderer;
	this.levelController = levelController;
    }

    @Override
    public void update(double delta)
    {
	timer.countTimeInMilliseconds();
	textRenderer.setLevelNumberString(levelController.getLevel().getValue());
    }

    public void renderText(Screen screen)
    {
	if (Game.ready) {
	    if (timer.getTimeElapsedInMilliseconds() <= Timer.TWO_SECONDS) {
		textRenderer.setLevelReadyPausedGameOverFlag(LEVEL_FLAG);
	    } else {
		textRenderer.setLevelReadyPausedGameOverFlag(READY_FLAG);
	    }
	} else if (Game.paused && timer.getTimeElapsedInMilliseconds() % Timer.ONE_SECOND > Timer.HALF_SECOND) {
	    textRenderer.setLevelReadyPausedGameOverFlag(PAUSED_FLAG);
	} else if (Game.quiting) {
	    textRenderer.setLevelReadyPausedGameOverFlag(QUIT_FLAG);
	} else if (Game.gameOver) {
	    textRenderer.setLevelReadyPausedGameOverFlag(GAME_OVER__FLAG);
	} else {
	    textRenderer.setLevelReadyPausedGameOverFlag(TextRenderer.LEVEL__READY__PAUSED__QUIT__GAME_OVER__MASK);
	}

	textRenderer.render(screen);
    }
}
