package com.jpacman.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class Maze {
    // ********************* Class (static) variables ********************** //
    // ************************** Constants ************************** //
    public static final int TILE = 16; // the size of each tile (in pixels)
    public static final int HALF_TILE = TILE / 2;
    public static final int QUARTER_TILE = TILE / 4;

    public static final int NUM_OF_ROWS = 31;
    public static final int NUM_OF_COLUMNS = 28;
    public static final double ASPECT_RATIO = (double) NUM_OF_COLUMNS / (double) NUM_OF_ROWS;

    public static final int WIDTH = NUM_OF_COLUMNS * TILE; // the width of the maze (in pixels)
    public static final int HEIGHT = NUM_OF_ROWS * TILE; // the height of the maze (in pixels)

    public static final int THUMBNAIL_WIDTH = 3 * TILE;
    public static final int THUMBNAIL_HEIGHT = (int) (THUMBNAIL_WIDTH / ASPECT_RATIO);

    private static final String MAZE_1 = "" + //
            "############################" + //
            "#............##............#" + //
            "#.####.#####.##.#####.####.#" + //
            "#O####.#####.##.#####.####O#" + //
            "#.####.#####.##.#####.####.#" + //
            "#..........................#" + //
            "#.####.##.########.##.####.#" + //
            "#.####.##.########.##.####.#" + //
            "#......##....##....##......#" + //
            "######.##### ## #####.######" + //
            "######.##### ## #####.######" + //
            "######.##     g    ##.######" + //
            "######.## ######## ##.######" + //
            "######.## ####H### ##.######" + //
            "L    l.   ##G=G=G#   .r    R" + //
            "######.## ######## ##.######" + //
            "######.## ######## ##.######" + //
            "######.##m    f    ##.######" + //
            "######.## ######## ##.######" + //
            "######.## ######## ##.######" + //
            "#............##............#" + //
            "#.####.#####.##.#####.####.#" + //
            "#.####.#####.##.#####.####.#" + //
            "#O..##....... P.......##..O#" + //
            "###.##.##.########.##.##.###" + //
            "###.##.##.########.##.##.###" + //
            "#......##....##....##......#" + //
            "#.##########.##.##########.#" + //
            "#.##########.##.##########.#" + //
            "#..........................#" + //
            "############################";

    private static final String MAZE_2 = "" + //
            "############################" + //
            "#......##..........##......#" + //
            "#O####.##.########.##.####O#" + //
            "#.####.##.########.##.####.#" + //
            "#..........................#" + //
            "###.##.#####.##.#####.##.###" + //
            "###.##.#####.##.#####.##.###" + //
            "###.##.#####.##.#####.##.###" + //
            "###.##.......##.......##.###" + //
            "L l.##### ######## #####.r R" + //
            "###.##### ######## #####.###" + //
            "###.          g         .###" + //
            "###.##### ######## #####.###" + //
            "###.##### ####H### #####.###" + //
            "###.##    ##G=G=G#    ##.###" + //
            "###.## ## ######## ## ##.###" + //
            "###.## ## ######## ## ##.###" + //
            "L l.   ##m    f    ##   .r R" + //
            "###.######## ## ########.###" + //
            "###.######## ## ########.###" + //
            "###.......   ##   .......###" + //
            "###.#####.########.#####.###" + //
            "###.#####.########.#####.###" + //
            "#............ P............#" + //
            "#.####.#####.##.#####.####.#" + //
            "#.####.#####.##.#####.####.#" + //
            "#.####.##....##....##.####.#" + //
            "#O####.##.########.##.####O#" + //
            "#.####.##.########.##.####.#" + //
            "#..........................#" + //
            "############################";

    private static final String MAZE_3 = "" + //
            "############################" + //
            "#..........................#" + //
            "#.##.####.########.####.##.#" + //
            "#O##.####.########.####.##O#" + //
            "#.##.####.##....##.####.##.#" + //
            "#.##......##.##.##......##.#" + //
            "#.####.##.##.##.##.##.####.#" + //
            "#.####.##.##.##.##.##.####.#" + //
            "#......##....##....##......#" + //
            "###.######## ## ########.###" + //
            "###.######## ## ########.###" + //
            "###....##     g    ##....###" + //
            "### ##.## ######## ##.## ###" + //
            "L l ##.## ####H### ##.## r R" + //
            "######.   ##G=G=G#   .######" + //
            "######.## ######## ##.######" + //
            "L l ##.## ######## ##.## r R" + //
            "### ##.##m    f    ##.## ###" + //
            "###....##### ## #####....###" + //
            "###.##.##### ## #####.##.###" + //
            "###.##....   ##   ....##.###" + //
            "###.#####.## ## ##.#####.###" + //
            "###.#####.## ## ##.#####.###" + //
            "#.........##  P ##.........#" + //
            "#.####.##.########.##.####.#" + //
            "#.####.##.########.##.####.#" + //
            "#.##...##..........##...##.#" + //
            "#O##.#######.##.#######.##O#" + //
            "#.##.#######.##.#######.##.#" + //
            "#............##............#" + //
            "############################";

    private static final String MAZE_4 = "" + //
            "############################" + //
            "L     l##..........##r     R" + //
            "###### ##.########.## ######" + //
            "###### ##.########.## ######" + //
            "#O...........##...........O#" + //
            "#.#######.##.##.##.#######.#" + //
            "#.#######.##.##.##.#######.#" + //
            "#.##......##.##.##......##.#" + //
            "#.##.#### ##....## ####.##.#" + //
            "#.##.#### ######## ####.##.#" + //
            "#......## ######## ##......#" + //
            "######.##     g    ##.######" + //
            "######.## ######## ##.######" + //
            "#......## ####H### ##......#" + //
            "#.####.## ##G=G=G# ##.####.#" + //
            "#.####.   ########   .####.#" + //
            "#...##.## ######## ##.##...#" + //
            "###.##.##m    f    ##.##.###" + //
            "###.##.#### #### ####.##.###" + //
            "###.##.#### #### ####.##.###" + //
            "###.........####.........###" + //
            "###.#######.####.#######.###" + //
            "###.#######.####.#######.###" + //
            "L l....##.... P....##....r R" + //
            "###.##.##.########.##.##.###" + //
            "###.##.##.########.##.##.###" + //
            "#O..##.......##.......##..O#" + //
            "#.####.#####.##.#####.####.#" + //
            "#.####.#####.##.#####.####.#" + //
            "#..........................#" + //
            "############################";

    private static final String MAZE_5 = "" + //
            "############################" + //
            "#.........##....##.........#" + //
            "#.#######.##.##.##.#######.#" + //
            "#O#######.##.##.##.#######O#" + //
            "#.##.........##.........##.#" + //
            "#.##.##.####.##.####.##.##.#" + //
            "#....##.####.##.####.##....#" + //
            "####.##.####.##.####.##.####" + //
            "####.##..............##.####" + //
            "4....#### ######## ####....6" + //
            "#.## #### ######## #### ##.#" + //
            "#.##          g         ##.#" + //
            "#.#### ## ######## ## ####.#" + //
            "#.#### ## ####H### ## ####.#" + //
            "#.     ## ##G=G=G# ##     .#" + //
            "#.## #### ######## #### ##.#" + //
            "#.## #### ######## #### ##.#" + //
            "#.##     m    f         ##.#" + //
            "#.#### ##### ## ##### ####.#" + //
            "#.#### ##### ## ##### ####.#" + //
            "#......##....##....##......#" + //
            "###.##.##.########.##.##.###" + //
            "###.##.##.########.##.##.###" + //
            "#O..##....... P.......##..O#" + //
            "#.####.#####.##.#####.####.#" + //
            "#.####.#####.##.#####.####.#" + //
            "#......##....##....##......#" + //
            "#.####.##.########.##.####.#" + //
            "#.####.##.########.##.####.#" + //
            "#......##..........##......#" + //
            "############################";

    private static final String MAZE_CANVAS = "" + //
            "############################" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#            P             #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "#                          #" + //
            "############################";

    private static final String MAZE_1_EDU = "" + //
            "############################" + //
            "#            ##            #" + //
            "# ########## ######## #### #" + //
            "# ########## ######## #### #" + //
            "# ########## ######## #### #" + //
            "#      ##          ##      #" + //
            "# #### ## ######## ####### #" + //
            "# #### ## ######## ####### #" + //
            "#      ##    ##    ##      #" + //
            "###### ##### ######## ######" + //
            "###### ##### ######## ######" + //
            "###### ## ## ##    ## ######" + //
            "###### ## ## ##### ## ######" + //
            "###### ## ## ##### ## ######" + //
            "#      ##     g ##    ######" + //
            "###### ## ##### ##### ##GGG#" + //
            "###### ## ##### ##### ######" + //
            "###### ##    ##    ## ######" + //
            "###### ########### ## ######" + //
            "###### ########### ## ######" + //
            "#            ## ##         #" + //
            "# #### ##### ## ########## #" + //
            "# #### ##### ## ########## #" + //
            "#   ## ##    ##       ##   #" + //
            "###### ## ######## ## ## ###" + //
            "###### ## ######## ## ## ###" + //
            "#      ## ## ##    ##      #" + //
            "# ########## ## ########## #" + //
            "# ########## ## ########## #" + //
            "#            ##          P #" + //
            "############################";

    private static final String MAZE_2_EDU = "" + //
            "############################" + //
            "#      ##          ##      #" + //
            "# #### ########### ## #### #" + //
            "# #### ########### ## #### #" + //
            "#   ## ##             ##   #" + //
            "### ## ##### ## ######## ###" + //
            "### ## ##### ## ######## ###" + //
            "### ## ##### ## ######## ###" + //
            "### ##       ##       ## ###" + //
            "### ############## ##### ###" + //
            "### ############## ##### ###" + //
            "###       ##  g          ###" + //
            "### ##### ## ########### ###" + //
            "### ##### ## ########### ###" + //
            "### ##    ## ##       ## ###" + //
            "### ## ## ## ## ##### ## ###" + //
            "### ## ## ## ## ##### ## ###" + //
            "### ## ## ## ##    ##    ###" + //
            "### ######## ########### ###" + //
            "### ######## ########### ###" + //
            "###          ##          ###" + //
            "### ##### ############## ###" + //
            "### ##### ############## ###" + //
            "#         ## ##            #" + //
            "# #### ##### ## ##### #### #" + //
            "# #### ##### ## ##### #G## #" + //
            "# #### ##    ##    ## #G## #" + //
            "# #### ## ######## ## #G## #" + //
            "# #### ## ######## ## #### #" + //
            "#      ##                P #" + //
            "############################";

    private static final String MAZE_3_EDU = "" + //
            "############################" + //
            "#                          #" + //
            "# ####### ######## #### ## #" + //
            "# ####### ######## #### ## #" + //
            "# ####### ##  g ## #### ## #" + //
            "# ##      ## ## ## ##   ## #" + //
            "# ####### ## ## ## ## #### #" + //
            "# ####### ## ## ## ## #### #" + //
            "#      ## ## ##    ##      #" + //
            "# ########## ## ######## ###" + //
            "# ########## ## ######## ###" + //
            "#      ##    ## ## ## ## ###" + //
            "###### ## ## ## ## ## ## ###" + //
            "###### ## ## ## ## ## ## ###" + //
            "###### ## ## ## ## ## ## ###" + //
            "###### ## ## ## ## ## ## ###" + //
            "#      ## ## ## ## ## ## ###" + //
            "# #### ## ## ##    ## ## ###" + //
            "# #### ##### ######## ## ###" + //
            "# #### ##### ######## ## ###" + //
            "# ####    ## ##       ## ###" + //
            "# ####### ## ## ## ##### ###" + //
            "# ####### ## ## ## ##### ###" + //
            "# ##   ## ##    ##         #" + //
            "# #### ## ########### #### #" + //
            "# #### ## ##GGG###### #### #" + //
            "# ##   ##          ##   ## #" + //
            "# ## ####### ########## ## #" + //
            "# ## ####### ########## ## #" + //
            "#            ##          P #" + //
            "############################";

    private static final String MAZE_4_EDU = "" + //
            "############################" + //
            "#      ##          ##      #" + //
            "###### ## ######## ## ######" + //
            "###### ## ######## ## ######" + //
            "#            ##            #" + //
            "# ####### ## ## ## ####### #" + //
            "# ####### ## ## ## ####### #" + //
            "# ##      ## ## ##      ## #" + //
            "# ## #### ##  g ## #### ## #" + //
            "# ## #### ######## #### ## #" + //
            "#      ##############      #" + //
            "###### ############## ######" + //
            "###### ############## ######" + //
            "#           ####           #" + //
            "# #### #### #### #### #### #" + //
            "# #### #### #### #### #### #" + //
            "#   ## #### #### #### ##   #" + //
            "### ## #### #GG# #### ## ###" + //
            "### ## #### ##G# #### ## ###" + //
            "### ## #### #### #### ## ###" + //
            "###         ####         ###" + //
            "### ####### #### ####### ###" + //
            "### ####### #### ####### ###" + //
            "#      ##     P    ##      #" + //
            "### ## ## ######## ## ## ###" + //
            "### ## ## ######## ## ## ###" + //
            "#   ##       ##       ##   #" + //
            "# #### ##### ## ##### #### #" + //
            "# #### ##### ## ##### #### #" + //
            "#                          #" + //
            "############################";

    private static final String MAZE_5_EDU = "" + //
            "############################" + //
            "#         ##    ##         #" + //
            "# ####### ## ## ## ####### #" + //
            "# ####### ## ## ## ####### #" + //
            "# ##         ##         ## #" + //
            "# ## ## #### ## #### ## ## #" + //
            "#    ## #### ## #### ##    #" + //
            "#### ## #### ## #### ## ####" + //
            "#### ##       g      ## ####" + //
            "#    #### ######## ####    #" + //
            "# ## #### ######## #### ## #" + //
            "# ##                    ## #" + //
            "# #### ## ######## ## #### #" + //
            "# #### ## ######## ## #### #" + //
            "#      ## ##G#G#G# ##      #" + //
            "# ## #### ######## #### ## #" + //
            "# ## #### ######## #### ## #" + //
            "# ##          P         ## #" + //
            "# #### ############## #### #" + //
            "# #### ############## #### #" + //
            "#      ##          ##      #" + //
            "### ## ## ######## ## ## ###" + //
            "### ## ## ######## ## ## ###" + //
            "#   ##       ##       ##   #" + //
            "# #### ## ######## ## #### #" + //
            "# #### ## ######## ## #### #" + //
            "#      ##    ##    ##      #" + //
            "# #### ## ######## ## #### #" + //
            "# #### ## ######## ## #### #" + //
            "#      ##          ##      #" + //
            "############################";

    public static final int TEXT_MESSAGE_AREA_WIDTH = 10 * TILE;

    public static Maze maze1 = new Maze(MAZE_1, "/graphics/maze_1.png", WIDTH, HEIGHT, new Color(0, 0, 157));
    public static Maze maze2 = new Maze(MAZE_2, "/graphics/maze_2.png", WIDTH, HEIGHT, new Color(150, 0, 0));
    public static Maze maze3 = new Maze(MAZE_3, "/graphics/maze_3.png", WIDTH, HEIGHT, new Color(0, 144, 0));
    public static Maze maze4 = new Maze(MAZE_4, "/graphics/maze_4.png", WIDTH, HEIGHT, new Color(0, 157, 157));
    public static Maze maze5 = new Maze(MAZE_5, "/graphics/maze_5.png", WIDTH, HEIGHT, new Color(150, 0, 150));
    public static Maze mazeCanvas = new Maze(MAZE_CANVAS, "/graphics/maze_canvas.png", WIDTH, HEIGHT,
            new Color(0, 0, 157));
    public static Maze maze2edu = new Maze(MAZE_1_EDU, "/graphics/maze_1_de.png", WIDTH, HEIGHT, new Color(0, 0, 157));
    public static Maze maze3edu = new Maze(MAZE_2_EDU, "/graphics/maze_2_de.png", WIDTH, HEIGHT, new Color(150, 0, 0));
    public static Maze maze4edu = new Maze(MAZE_3_EDU, "/graphics/maze_3_de.png", WIDTH, HEIGHT, new Color(0, 144, 0));
    public static Maze maze5edu = new Maze(MAZE_4_EDU, "/graphics/maze_4_rnd.png", WIDTH, HEIGHT,
            new Color(0, 157, 157));
    public static Maze maze6edu = new Maze(MAZE_5_EDU, "/graphics/maze_5_rnd.png", WIDTH, HEIGHT,
            new Color(150, 0, 150));

    public static Maze[] educationalModeMazes = new Maze[10];

    // class constructor
    static {
        educationalModeMazes[0] = maze1;
        educationalModeMazes[1] = maze2;
        educationalModeMazes[2] = maze3;
        educationalModeMazes[3] = maze4;
        educationalModeMazes[4] = maze5;
        educationalModeMazes[5] = maze2edu;
        educationalModeMazes[6] = maze3edu;
        educationalModeMazes[7] = maze4edu;
        educationalModeMazes[8] = maze5edu;
        educationalModeMazes[9] = maze6edu;
    }

    // ************************* Instance variables ************************ //
    private final char[][] mazeDataArray; // an array used to store the maze as char characters
    private final String imagePath; // the path in the disk where the maze's image is located
    private final int width; // the width of the maze's created image
    private final int height; // the height of the maze's created image
    private final int[] imagePixels; // the pixels of maze's image
    private final Color color; // the base color of the maze (used for fun stuff in some cases such as the
                               // search tree)

    public int[] resizedImagePixels;
    // /////////////////////////////////////////////////////////////////////////
    // all (center) points of tiles that form the route
    private final ArrayList<Point> routeTiles = new ArrayList<Point>(300);
    // all (center) points of route's intersections' tiles (= all tiles with at
    // least 1 possible direction change)
    private final ArrayList<Point> intersectionsTiles = new ArrayList<Point>(50);
    // all (center) points of route's cross roads' tiles (= all tiles with at least
    // 2 possible direction changes)
    private final ArrayList<Point> crossroadsTiles = new ArrayList<Point>(50);
    // all (center) points of tiles (half size of default) that form the route
    // inside ghosts house
    private final ArrayList<Point> ghostsHouseRouteTiles = new ArrayList<Point>(15);
    // all (center) points of ghost house route's intersections' tiles
    private final ArrayList<Point> ghostsHouseIntersectionsTiles = new ArrayList<Point>(3);
    // all (center) points of pills' tiles
    private final ArrayList<Point> simplePillsTiles = new ArrayList<Point>(240);
    // all (center) points of power pills' tiles
    private final ArrayList<Point> powerPillsTiles = new ArrayList<Point>(4);
    // all (center) points of ghosts' starting tiles
    private final ArrayList<Point> ghostsStartingTiles = new ArrayList<Point>(4);
    // the (center) point of tunnel's left entrance tile
    private final ArrayList<Point> leftTunnelEntranceTile = new ArrayList<Point>(2);
    // the (center) point of tunnel's right entrance tile
    private final ArrayList<Point> rightTunnelEntranceTile = new ArrayList<Point>(2);
    // the center point of the ghosts' house
    private Point ghostHouseCenter;
    // the (center) point of ghosts' house door tile
    private Point ghostsHouseInFrontOfDoorTile;
    // the (center) point of ghosts' house behind door tile
    private Point ghostsHouseBehindDoorTile;
    // the (center) point of message's tile
    private Point textMessageTile;
    // the (center) point of fruit's starting tile
    private Point fruitStartingTile;
    // the (center) point of pacman's starting tile
    private Point pacmanStartingTile;

    public Maze(String mazeString, String imagePath, int width, int height, Color color) {
        mazeDataArray = new char[NUM_OF_ROWS][NUM_OF_COLUMNS];
        int i = 0;
        for (int r = 0; r < NUM_OF_ROWS; r++) {
            for (int c = 0; c < NUM_OF_COLUMNS; c++) {
                mazeDataArray[r][c] = mazeString.charAt(i);
                i++;
            }
        }

        parseMazeDataArray();
        computeIntersectionsTiles();

        this.imagePath = imagePath;
        this.width = width;
        this.height = height;
        this.color = color;
        imagePixels = new int[width * height];
        loadImage();

        resizedImagePixels = new int[THUMBNAIL_WIDTH * THUMBNAIL_HEIGHT];
        createThumbnailsImage();
    }

    private void parseMazeDataArray() {
        // scan the array left->right, up->down and extract maze info
        for (int r = 0; r < NUM_OF_ROWS; r++) {
            for (int c = 0; c < NUM_OF_COLUMNS; c++) {
                char tile = mazeDataArray[r][c];
                if (tile == '.') {
                    simplePillsTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                    routeTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                }
                if (tile == 'O') {
                    powerPillsTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                    routeTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                }
                if (tile == ' ') {
                    routeTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                }
                if (tile == 'g' || tile == 'G' || tile == 'H' || tile == '=') {
                    if (tile == 'g') {
                        ghostsStartingTiles.add(new Point(c * TILE, r * TILE + HALF_TILE));
                        routeTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                        ghostsHouseInFrontOfDoorTile = new Point(c * TILE, r * TILE + HALF_TILE);
                        ghostsHouseRouteTiles.add(ghostsHouseInFrontOfDoorTile);
                    }
                    if (tile == 'G') {
                        ghostsStartingTiles.add(new Point(c * TILE, r * TILE + HALF_TILE));
                        ghostsHouseRouteTiles.add(new Point(c * TILE, r * TILE + HALF_TILE)); // center tiles
                        ghostsHouseRouteTiles.add(new Point(c * TILE, r * TILE + 2 * HALF_TILE)); // bottom tiles
                        ghostsHouseRouteTiles.add(new Point(c * TILE, r * TILE)); // upper tiles
                        ghostsHouseIntersectionsTiles.add(new Point(c * TILE, r * TILE + HALF_TILE));
                    }
                    if (tile == '=') {
                        ghostsHouseRouteTiles.add(new Point(c * TILE, r * TILE + HALF_TILE));
                        ghostsHouseRouteTiles.add(new Point(c * TILE - HALF_TILE, r * TILE + HALF_TILE)); // left tiles
                        ghostsHouseRouteTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE)); // right tiles
                    }
                    if (tile == 'H') {
                        ghostHouseCenter = new Point(c * TILE, r * TILE + TILE + HALF_TILE);
                        ghostsHouseRouteTiles.add(new Point(c * TILE, r * TILE)); // center tile
                        ghostsHouseRouteTiles.add(new Point(c * TILE, r * TILE + 2 * HALF_TILE)); // bottom tile
                        ghostsHouseBehindDoorTile = new Point(c * TILE, r * TILE + HALF_TILE); // behind door tile
                        ghostsHouseRouteTiles.add(ghostsHouseBehindDoorTile); // upper tile
                        ghostsHouseRouteTiles.add(new Point(c * TILE, r * TILE - HALF_TILE)); // upper tile
                        ghostsHouseRouteTiles.add(new Point(c * TILE, r * TILE - 2 * HALF_TILE)); // upper tile
                    }
                }
                if (tile == 'l') {
                    leftTunnelEntranceTile.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                    routeTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                }
                if (tile == 'L') {
                    routeTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                    routeTiles.add(new Point(c * TILE + HALF_TILE - TILE, r * TILE + HALF_TILE));
                }
                if (tile == 'r') {
                    rightTunnelEntranceTile.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                    routeTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                }
                if (tile == 'R') {
                    routeTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                    routeTiles.add(new Point(c * TILE + HALF_TILE + TILE, r * TILE + HALF_TILE));
                }
                if (tile == '4') {
                    leftTunnelEntranceTile.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                    routeTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                    routeTiles.add(new Point(c * TILE + HALF_TILE - TILE, r * TILE + HALF_TILE));
                }
                if (tile == '6') {
                    rightTunnelEntranceTile.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                    routeTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                    routeTiles.add(new Point(c * TILE + HALF_TILE + TILE, r * TILE + HALF_TILE));
                }
                if (tile == 'm') {
                    textMessageTile = new Point(c * TILE, r * TILE + HALF_TILE);
                    routeTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                }
                if (tile == 'f') {
                    fruitStartingTile = new Point(c * TILE, r * TILE);
                    routeTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                }
                if (tile == 'P') {
                    pacmanStartingTile = new Point(c * TILE, r * TILE + HALF_TILE);
                    routeTiles.add(new Point(c * TILE + HALF_TILE, r * TILE + HALF_TILE));
                }
            }
        }
    }

    // Includes crossroads tiles which are intersections as well
    private void computeIntersectionsTiles() {
        Point tile;
        Point[] adjacentTiles = new Point[4];
        for (int i = 0; i < routeTiles.size(); i++) {
            tile = routeTiles.get(i);
            for (int j = 0; j < 4; j++) {
                adjacentTiles[j] = new Point(tile);
            }
            adjacentTiles[0].translate(0, -TILE);
            adjacentTiles[1].translate(0, TILE);
            adjacentTiles[2].translate(-TILE, 0);
            adjacentTiles[3].translate(TILE, 0);

            if (routeTiles.contains(adjacentTiles[0]) && routeTiles.contains(adjacentTiles[1]) //
                    && routeTiles.contains(adjacentTiles[2])
                    || routeTiles.contains(adjacentTiles[0]) //
                            && routeTiles.contains(adjacentTiles[1]) && routeTiles.contains(adjacentTiles[3]) //
                    || routeTiles.contains(adjacentTiles[0]) && routeTiles.contains(adjacentTiles[2]) //
                            && routeTiles.contains(adjacentTiles[3])
                    || routeTiles.contains(adjacentTiles[1]) //
                            && routeTiles.contains(adjacentTiles[2]) && routeTiles.contains(adjacentTiles[3])) {
                crossroadsTiles.add(tile);
                intersectionsTiles.add(tile);
                continue;
            }

            if (routeTiles.contains(adjacentTiles[0]) && routeTiles.contains(adjacentTiles[2]) //
                    || routeTiles.contains(adjacentTiles[0]) && routeTiles.contains(adjacentTiles[3]) //
                    || routeTiles.contains(adjacentTiles[1]) && routeTiles.contains(adjacentTiles[2]) //
                    || routeTiles.contains(adjacentTiles[1]) && routeTiles.contains(adjacentTiles[3])) {
                intersectionsTiles.add(tile);
            }
        }
        // System.out.println("routeTiles size = "+routeTiles.size());
        // System.out.println("crossroadsTiles size = "+crossroadsTiles.size());
        // System.out.println("intersectionsTiles size = "+intersectionsTiles.size());
    }

    private void loadImage() {
        try {
            BufferedImage image = ImageIO.read(Maze.class.getResource(imagePath));
            int w = image.getWidth();
            int h = image.getHeight();
            image.getRGB(0, 0, w, h, imagePixels, 0, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createThumbnailsImage() {
        try {
            BufferedImage originalImage = ImageIO.read(Maze.class.getResource(imagePath));
            int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_RGB : originalImage.getType();

            BufferedImage thumbnail = resizeImage(originalImage, type);
            int w = thumbnail.getWidth();
            int h = thumbnail.getHeight();
            thumbnail.getRGB(0, 0, w, h, resizedImagePixels, 0, w);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int type) {
        BufferedImage resizedImage = new BufferedImage(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, null);
        g.dispose();

        return resizedImage;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color getColor() {
        return color;
    }

    public int[] getImagePixels() {
        return imagePixels;
    }

    public ArrayList<Point> getRouteTiles() {
        return routeTiles;
    }

    public ArrayList<Point> getIntersectionsTiles() {
        return intersectionsTiles;
    }

    public ArrayList<Point> getCrossroadsTiles() {
        return crossroadsTiles;
    }

    public ArrayList<Point> getGhostsHouseRouteTiles() {
        return ghostsHouseRouteTiles;
    }

    public ArrayList<Point> getGhostsHouseIntersectionsTiles() {
        return ghostsHouseIntersectionsTiles;
    }

    public ArrayList<Point> getSimplePillsTiles() {
        return simplePillsTiles;
    }

    public ArrayList<Point> getPowerPillsTiles() {
        return powerPillsTiles;
    }

    public ArrayList<Point> getGhostsStartingTiles() {
        return ghostsStartingTiles;
    }

    public ArrayList<Point> getLeftTunnelEntranceTile() {
        return leftTunnelEntranceTile;
    }

    public ArrayList<Point> getRightTunnelEntranceTile() {
        return rightTunnelEntranceTile;
    }

    public Point getGhostHouseCenter() {
        return ghostHouseCenter;
    }

    public Point getGhostsHouseInFrontOfDoorTile() {
        return ghostsHouseInFrontOfDoorTile;
    }

    public Point getGhostsHouseBehindDoorTile() {
        return ghostsHouseBehindDoorTile;
    }

    public Point getTextMessageTile() {
        return textMessageTile;
    }

    public Point getFruitStartingTile() {
        return fruitStartingTile;
    }

    public Point getPacmanStartingTile() {
        return pacmanStartingTile;
    }
}
