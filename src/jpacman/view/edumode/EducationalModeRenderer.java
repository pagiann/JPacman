package jpacman.view.edumode;

import java.awt.Point;
import java.awt.Rectangle;

import jpacman.Game;
import jpacman.Game.SubMode;
import jpacman.GameApplication;
import jpacman.model.Maze;
import jpacman.model.Pacman;
import jpacman.model.Text;
import jpacman.model.UIButton;
import jpacman.model.edumode.AStarAlgorithm;
import jpacman.model.edumode.BeamSearchAlgorithm;
import jpacman.model.edumode.BestFirstAlgorithm;
import jpacman.model.edumode.BreadthFirstSearchAlgorithm;
import jpacman.model.edumode.DepthFirstSearchAlgorithm;
import jpacman.model.edumode.HeuristicAlgorithm;
import jpacman.model.edumode.IterativeDeepeningAlgorithm;
import jpacman.view.MazeRenderer;
import jpacman.view.Renderer;
import jpacman.view.TextRenderer;
import jpacman.view.graphics.Screen;

public class EducationalModeRenderer implements Renderer
{
    // ********************* Class (static) variables ********************** //
    public static final UIButton backButton = new UIButton(new Point(0, GameApplication.WINDOW_HEIGHT - GameApplication.Y_OFFSET - 35), false, new Text("Back"), true);
    public static final UIButton cancelButton = new UIButton(new Point(105, GameApplication.WINDOW_HEIGHT - GameApplication.Y_OFFSET - 35), false, new Text("Cancel"), true);
    public static final UIButton randomizeButton = new UIButton(new Point(65, GameApplication.WINDOW_HEIGHT - GameApplication.Y_OFFSET - 70), false, new Text("Randomize"), true);
    public static final UIButton resetButton = new UIButton(new Point(105, GameApplication.WINDOW_HEIGHT - GameApplication.Y_OFFSET - 105), false, new Text("Reset"), false);
    public static final UIButton pauseButton = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 110, GameApplication.Y_OFFSET + 15), false, new Text("Pause"), true);
    public static final UIButton resumeButton = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 100, GameApplication.Y_OFFSET + 15), false, new Text("Resume"), true);
    public static final Text defaultColorText = new Text("Default", Text.DEFAULT_FONT_SMALL);
    public static final Text mazeColorText = new Text("Maze", Text.DEFAULT_FONT_SMALL);
    public static final Text mixedColorText = new Text("Mixed", Text.DEFAULT_FONT_SMALL);
    public static final Text visualizeIterationsText = new Text("Visualize Iterations", Text.DEFAULT_FONT_SMALL);
    public static final Text dontVisualizeIterationsText = new Text("Don't Visualize Iterations", Text.DEFAULT_FONT_SMALL);
    public static UIButton backCancelButton = backButton;
    public static UIButton pauseResumeButton = pauseButton;
    public static int mazeThumbnailsBoundsYOffset = GameApplication.Y_OFFSET + Maze.HEIGHT / 2;

    public UIButton showSearchTree = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 30, GameApplication.Y_OFFSET + 55), false, new Text("Show Search Tree", Text.DEFAULT_FONT_SMALL), true);
    public UIButton toggleGrid = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 30, GameApplication.Y_OFFSET + 80), false, new Text("Toggle Grid", Text.DEFAULT_FONT_SMALL), true);
    public Text expandedNodesColorText = new Text("Expanded Nodes Color:", Text.DEFAULT_FONT_SMALL, 0x9999FF);
    public UIButton expandedNodesColor = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 245, GameApplication.Y_OFFSET + 105), false, defaultColorText, true);
    public Text expandedNodesSpeedText = new Text("Expanded Nodes Speed:", Text.DEFAULT_FONT_SMALL, 0x9999FF);
    public Text expandedNodesSpeedValueText = new Text("", Text.DEFAULT_FONT_SMALL, 0xCCCCFF);
    public UIButton minNodeExpansionSpeed = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 70, GameApplication.Y_OFFSET + 150), false, new Text("min", Text.DEFAULT_FONT_MEDIUM), true);
    public UIButton decreaseNodeExpansionSpeed = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 145, GameApplication.Y_OFFSET + 150), false, new Text("-", Text.DEFAULT_FONT_MEDIUM), true);
    public UIButton increaseNodeExpansionSpeed = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 185, GameApplication.Y_OFFSET + 150), false, new Text("+", Text.DEFAULT_FONT_MEDIUM), true);
    public UIButton maxNodeExpansionSpeed = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 230, GameApplication.Y_OFFSET + 150), false, new Text("max", Text.DEFAULT_FONT_MEDIUM), true);
    public Text pacmanSpeedText = new Text("Pacman Speed:", Text.DEFAULT_FONT_SMALL, 0x9999FF);
    public Text pacmanSpeedValueText = new Text("", Text.DEFAULT_FONT_SMALL, 0xCCCCFF);
    public UIButton minPacmanSpeed = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 70, GameApplication.Y_OFFSET + 200), false, new Text("min", Text.DEFAULT_FONT_MEDIUM), true);
    public UIButton decreasePacmanSpeed = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 145, GameApplication.Y_OFFSET + 200), false, new Text("-", Text.DEFAULT_FONT_MEDIUM), true);
    public UIButton increasePacmanSpeed = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 185, GameApplication.Y_OFFSET + 200), false, new Text("+", Text.DEFAULT_FONT_MEDIUM), true);
    public UIButton maxPacmanSpeed = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 230, GameApplication.Y_OFFSET + 200), false, new Text("max", Text.DEFAULT_FONT_MEDIUM), true);

    public UIButton visualizeIterations = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 30, GameApplication.Y_OFFSET + yOffset1RightPanel + 30), false, visualizeIterationsText, true);
    public Text initialDepthText = new Text("Initial Depth:", Text.DEFAULT_FONT_SMALL, 0x9999FF);
    public Text initialDepthValueText = new Text("", Text.DEFAULT_FONT_SMALL, 0xCCCCFF);
    public UIButton decreaseInitialDepth = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 240, GameApplication.Y_OFFSET + yOffset2RightPanel + 5), false, new Text("-", Text.DEFAULT_FONT_SMALL), true);
    public UIButton increaseInitialDepth = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 270, GameApplication.Y_OFFSET + yOffset2RightPanel + 5), false, new Text("+", Text.DEFAULT_FONT_SMALL), true);
    public Text depthStepText = new Text("Depth Step:", Text.DEFAULT_FONT_SMALL, 0x9999FF);
    public Text depthStepValueText = new Text("", Text.DEFAULT_FONT_SMALL, 0xCCCCFF);
    public UIButton decreaseDepthStep = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 240, GameApplication.Y_OFFSET + yOffset2RightPanel + 25), false, new Text("-", Text.DEFAULT_FONT_SMALL), true);
    public UIButton increaseDepthStep = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 270, GameApplication.Y_OFFSET + yOffset2RightPanel + 25), false, new Text("+", Text.DEFAULT_FONT_SMALL), true);
    public Text currentDepthText = new Text("Current Depth:", Text.DEFAULT_FONT_SMALL, 0xBF9B30);
    public Text currentIterationText = new Text("", Text.DEFAULT_FONT_SMALL, 0xCCCFF66);
    public Text currentDepthLimitText = new Text("", Text.DEFAULT_FONT_SMALL, 0xBF9B30);

    public Text beamWidthText = new Text("Beam Width:", Text.DEFAULT_FONT_SMALL, 0x9999FF);
    public Text beamWidthValueText = new Text("", Text.DEFAULT_FONT_SMALL, 0xCCCCFF);
    public UIButton decreaseBeamWidth = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 240, GameApplication.Y_OFFSET + yOffset2RightPanel), false, new Text("-", Text.DEFAULT_FONT_SMALL), true);
    public UIButton increaseBeamWidth = new UIButton(new Point(GameApplication.X_OFFSET + Maze.WIDTH + 270, GameApplication.Y_OFFSET + yOffset2RightPanel), false, new Text("+", Text.DEFAULT_FONT_SMALL), true);

    public static final UIButton[] algorithms = new UIButton[6];
    public static final UIButton[] heuristicFunctions1 = new UIButton[4];
    public static final UIButton[] heuristicFunctions2 = new UIButton[4];
    public static final UIButton[] heuristicFunctions3 = new UIButton[4];
    public static final Text selectedAlgorithmText = new Text("", Text.DEFAULT_FONT_SMALL, 0x66FFFF);

    protected static int xOffsetRightPanel = 30;
    protected static int yOffsetRightPanel = 0;
    protected static int yOffset1RightPanel = 245;
    protected static int yOffset2RightPanel = 290;

    protected static final int Y_OFFSET = GameApplication.Y_OFFSET + 25;

    private static final int xOffset = 55;

    protected static final Text selectAlgorithm = new Text("Select algorithm", Text.DEFAULT_FONT_MEDIUM, 0x9999FF);
    protected static final Text blindAlgorithm = new Text("Blind:", Text.DEFAULT_FONT_MEDIUM, 0xCCCCFF);
    protected static final Text dfsText = new Text(DepthFirstSearchAlgorithm.NAME, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton dfs = new UIButton(new Point(xOffset, Y_OFFSET + 30), false, dfsText, true);
    protected static final Text bfsText = new Text(BreadthFirstSearchAlgorithm.NAME, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton bfs = new UIButton(new Point(xOffset, Y_OFFSET + 50), false, bfsText, true);
    protected static final Text iterativeDeepeningText = new Text(IterativeDeepeningAlgorithm.NAME, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton iterativeDeepening = new UIButton(new Point(xOffset, Y_OFFSET + 70), false, iterativeDeepeningText, true);
    protected static final Text heuristicAlgorithm = new Text("Heuristic:", Text.DEFAULT_FONT_MEDIUM, 0xCCCCFF);
    protected static final Text bestFirstText = new Text(BestFirstAlgorithm.NAME, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton bestFirst = new UIButton(new Point(xOffset, Y_OFFSET + 120), false, bestFirstText, true);
    protected static final Text manhattanHeuristicText1 = new Text(HeuristicAlgorithm.MANHATTAN_HEURISTIC_STRING, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton manhattanHeuristic1 = new UIButton(new Point(xOffset + 20, Y_OFFSET + 140), false, manhattanHeuristicText1, true);
    protected static final Text chebyshevHeuristicText1 = new Text(HeuristicAlgorithm.CHEBYSHEV_HEURISTIC_STRING, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton chebyshevHeuristic1 = new UIButton(new Point(xOffset + 20, Y_OFFSET + 160), false, chebyshevHeuristicText1, true);
    protected static final Text octileHeuristicText1 = new Text(HeuristicAlgorithm.OCTILE_HEURISTIC_STRING, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton octileHeuristic1 = new UIButton(new Point(xOffset + 20, Y_OFFSET + 180), false, octileHeuristicText1, true);
    protected static final Text euclideanHeuristicText1 = new Text(HeuristicAlgorithm.EUCLIDEAN_HEURISTIC_STRING, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton euclideanHeuristic1 = new UIButton(new Point(xOffset + 20, Y_OFFSET + 200), false, euclideanHeuristicText1, true);
    static int beamSearchYOffset = 80;
    static boolean beamSearchBoundsMoved = false;
    protected static final Text beamSearchText = new Text(BeamSearchAlgorithm.NAME, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton beamSearch = new UIButton(new Point(xOffset, Y_OFFSET + 140), false, beamSearchText, true);
    protected static final Text manhattanHeuristicText2 = new Text(HeuristicAlgorithm.MANHATTAN_HEURISTIC_STRING, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton manhattanHeuristic2 = new UIButton(new Point(xOffset + 20, Y_OFFSET + 160), false, manhattanHeuristicText2, true);
    protected static final Text chebyshevHeuristicText2 = new Text(HeuristicAlgorithm.CHEBYSHEV_HEURISTIC_STRING, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton chebyshevHeuristic2 = new UIButton(new Point(xOffset + 20, Y_OFFSET + 180), false, chebyshevHeuristicText2, true);
    protected static final Text octileHeuristicText2 = new Text(HeuristicAlgorithm.OCTILE_HEURISTIC_STRING, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton octileHeuristic2 = new UIButton(new Point(xOffset + 20, Y_OFFSET + 200), false, octileHeuristicText2, true);
    protected static final Text euclideanHeuristicText2 = new Text(HeuristicAlgorithm.EUCLIDEAN_HEURISTIC_STRING, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton euclideanHeuristic2 = new UIButton(new Point(xOffset + 20, Y_OFFSET + 220), false, euclideanHeuristicText2, true);
    static int aStarYOffset = 80;
    static boolean beamSearchAndAStarBoundsMoved = false;
    static boolean aStarBoundsMoved = false;
    protected static final Text aStarText = new Text(AStarAlgorithm.NAME, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton aStar = new UIButton(new Point(xOffset, Y_OFFSET + 160), false, aStarText, true);
    protected static final Text manhattanHeuristicText3 = new Text(HeuristicAlgorithm.MANHATTAN_HEURISTIC_STRING, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton manhattanHeuristic3 = new UIButton(new Point(xOffset + 20, Y_OFFSET + 180), false, manhattanHeuristicText3, true);
    protected static final Text chebyshevHeuristicText3 = new Text(HeuristicAlgorithm.CHEBYSHEV_HEURISTIC_STRING, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton chebyshevHeuristic3 = new UIButton(new Point(xOffset + 20, Y_OFFSET + 200), false, chebyshevHeuristicText3, true);
    protected static final Text octileHeuristicText3 = new Text(HeuristicAlgorithm.OCTILE_HEURISTIC_STRING, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton octileHeuristic3 = new UIButton(new Point(xOffset + 20, Y_OFFSET + 220), false, octileHeuristicText3, true);
    protected static final Text euclideanHeuristicText3 = new Text(HeuristicAlgorithm.EUCLIDEAN_HEURISTIC_STRING, Text.DEFAULT_FONT_SMALL);
    protected static final UIButton euclideanHeuristic3 = new UIButton(new Point(xOffset + 20, Y_OFFSET + 240), false, euclideanHeuristicText3, true);

    protected static final Text selectMazeText = new Text("Select Maze", Text.DEFAULT_FONT_MEDIUM, 0x9999FF);

    protected static final Text algorithmInfoText = new Text("------ ALGORITHM INFO ------", Text.DEFAULT_FONT_SMALL, 0x9999FF);
    protected static final Text solutionPathLengthInTiles = new Text("", Text.DEFAULT_FONT_SMALL, 0xCCFF99);
    protected static final Text solutionPathLengthInPixels = new Text("", Text.DEFAULT_FONT_SMALL, 0xCCFF99);
    protected static final Text nodesVisitedText = new Text("", Text.DEFAULT_FONT_SMALL, 0xf17d55);
    protected static final Text nodesExpandedText = new Text("", Text.DEFAULT_FONT_SMALL, 0xFF009B);
    protected static final Text secondsElapsedText = new Text("", Text.DEFAULT_FONT_SMALL, 0x00BFFF);

    protected static final Text pacmanInfoText = new Text("------- PACMAN INFO --------", Text.DEFAULT_FONT_SMALL, 0x9999FF);
    protected static final Text distanceTraveledText = new Text("", Text.DEFAULT_FONT_SMALL, 0xFFFF66);
    protected static final Text tilesTraveledText = new Text("", Text.DEFAULT_FONT_SMALL, 0xFFFF66);
    protected static final Text uneatenPillsText = new Text("", Text.DEFAULT_FONT_SMALL, 0xFFFF66);

    static {
	heuristicFunctions1[0] = manhattanHeuristic1;
	heuristicFunctions1[1] = chebyshevHeuristic1;
	heuristicFunctions1[2] = octileHeuristic1;
	heuristicFunctions1[3] = euclideanHeuristic1;

	heuristicFunctions2[0] = manhattanHeuristic2;
	heuristicFunctions2[1] = chebyshevHeuristic2;
	heuristicFunctions2[2] = octileHeuristic2;
	heuristicFunctions2[3] = euclideanHeuristic2;

	heuristicFunctions3[0] = manhattanHeuristic3;
	heuristicFunctions3[1] = chebyshevHeuristic3;
	heuristicFunctions3[2] = octileHeuristic3;
	heuristicFunctions3[3] = euclideanHeuristic3;

	algorithms[0] = dfs;
	algorithms[1] = bfs;
	algorithms[2] = iterativeDeepening;
	algorithms[3] = bestFirst;
	algorithms[4] = beamSearch;
	algorithms[5] = aStar;
    }

    // ************************* Instance variables ************************ //
    protected final Text title = new Text("", Text.DEFAULT_FONT_LARGE, 0x66CCFF);
    protected final Text description = new Text("", Text.DEFAULT_FONT_SMALL, 0xFF0066);
    protected final Text clickToStart = new Text("", Text.DEFAULT_FONT_MEDIUM, 0xFF9933);

    protected final TextRenderer textRenderer;
    protected final Pacman pacman;
    protected int tilesTraveled;

    protected Rectangle[] mazeThumbnailsBounds = new Rectangle[Maze.educationalModeMazes.length];
    protected boolean[] hoveredMazeThumbnails = new boolean[Maze.educationalModeMazes.length];

    public EducationalModeRenderer(TextRenderer textRenderer, Pacman pacman)
    {
	this.textRenderer = textRenderer;
	this.pacman = pacman;

	for (int i = 0; i < Maze.educationalModeMazes.length; i++) {
	    hoveredMazeThumbnails[i] = false;
	}
    }

    protected void renderLeftSidebar(Screen screen)
    {
	// left align back menu item
	backButton.getBounds().x = 115;

	renderMazeThumbnails(screen, Maze.educationalModeMazes);
	backCancelButton.render(textRenderer, screen);
    }

    protected void renderMazeThumbnails(Screen screen, Maze[] educationalModeMazes)
    {
	int yOffset = (Game.getActiveSubMode() == SubMode.MAZE_TSP) ? -30 : 45;
	textRenderer.renderText(screen, selectMazeText, 80, mazeThumbnailsBoundsYOffset + yOffset, false);

	int xStart = 30;
	int x = xStart;
	int yStart = mazeThumbnailsBoundsYOffset + yOffset + 30;
	int y = yStart;
	for (int i = 0; i < educationalModeMazes.length; i++) {
	    if (i == 5) {
		x = xStart;
		y = yStart + (i / 5) * (Maze.THUMBNAIL_HEIGHT + 5);
	    }
	    mazeThumbnailsBounds[i] = new Rectangle(x, y, Maze.THUMBNAIL_WIDTH + 1, Maze.THUMBNAIL_HEIGHT + 1);
	    MazeRenderer.renderThumbnail(screen, educationalModeMazes[i], new Point(x, y), false);
	    x += Maze.THUMBNAIL_WIDTH + 5;

	    if (hoveredMazeThumbnails[i]) {
		screen.renderBounds(mazeThumbnailsBounds[i], Screen.YELLOW_COLOR, -1, 0, 2, 1, false);
	    }
	}
    }

    protected void renderAlgorithmButtons(Screen screen)
    {
	for (UIButton algorithmMenuButton : algorithms) {
	    if (algorithmMenuButton.isVisible()) {
		if (algorithmMenuButton.hasFocus() || algorithmMenuButton.isMouseHovered()) {
		    screen.renderBounds(algorithmMenuButton.getBounds(), Screen.WHITE_COLOR, -1, 0, 5, 2, false);
		    algorithmMenuButton.getText().setColor(Screen.YELLOW_COLOR);
		} else {
		    algorithmMenuButton.getText().setColor(Screen.WHITE_COLOR);
		}
		textRenderer.renderText(screen, algorithmMenuButton.getText(), algorithmMenuButton.getBounds().x, algorithmMenuButton.getBounds().y, false);

		if (algorithmMenuButton.equals(bestFirst)) {
		    if (algorithmMenuButton.isClicked()) {
			for (UIButton heuristicMenuButton : heuristicFunctions1) {
			    heuristicMenuButton.render(textRenderer, screen);
			}
			if (!beamSearchBoundsMoved) {
			    beamSearch.getBounds().y += beamSearchYOffset;
			    beamSearchBoundsMoved = true;
			}
			if (!beamSearchAndAStarBoundsMoved) {
			    aStar.getBounds().y += aStarYOffset;
			    beamSearchAndAStarBoundsMoved = true;
			}
		    } else {
			if (beamSearchBoundsMoved) {
			    beamSearch.getBounds().y -= beamSearchYOffset;
			    beamSearchBoundsMoved = false;
			}
			if (beamSearchAndAStarBoundsMoved) {
			    aStar.getBounds().y -= aStarYOffset;
			    beamSearchAndAStarBoundsMoved = false;
			}
		    }
		}

		if (algorithmMenuButton.equals(beamSearch)) {
		    if (algorithmMenuButton.isClicked()) {
			for (UIButton heuristicMenuButton : heuristicFunctions2) {
			    heuristicMenuButton.render(textRenderer, screen);
			}
			if (!aStarBoundsMoved) {
			    aStar.getBounds().y += aStarYOffset;
			    aStarBoundsMoved = true;
			}
		    } else {
			if (aStarBoundsMoved) {
			    aStar.getBounds().y -= aStarYOffset;
			    aStarBoundsMoved = false;
			}
		    }
		}

		if (algorithmMenuButton.equals(aStar) && algorithmMenuButton.isClicked()) {
		    for (UIButton heuristicMenuButton : heuristicFunctions3) {
			heuristicMenuButton.render(textRenderer, screen);
		    }
		}
	    }
	}
    }

    @Override
    public void render(Screen screen)
    {

    }

    public void setTilesTraveled(int tilesTraveled)
    {
	this.tilesTraveled = tilesTraveled;
    }

    public Rectangle[] getMazeThumbnailsBounds()
    {
	return mazeThumbnailsBounds;
    }

    public boolean[] getHoveredMazeThumbnails()
    {
	return hoveredMazeThumbnails;
    }

    public void setHoveredMazeThumbnails(int index, boolean value)
    {
	hoveredMazeThumbnails[index] = value;
    }

    public Text getTitle()
    {
	return title;
    }
}
