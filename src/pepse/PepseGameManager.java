package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Counter;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.*;
import pepse.world.trees.Tree;
import pepse.world.turtles.Turtles;

import java.awt.*;


public class PepseGameManager extends GameManager{

    //Layers in the game:
    private static final int SKY_LAYER = Layer.BACKGROUND;
    private static final int NIGHT_LAYER = Layer.FOREGROUND;
    private static final int GROUND_LAYER = Layer.STATIC_OBJECTS;
    private static final int EXTRA_GROUND_LAYER = Layer.STATIC_OBJECTS + 1;
    private static final int LEAF_LAYER = Layer.STATIC_OBJECTS + 5;
    private static final int TREE_TRUNK_LAYER = Layer.STATIC_OBJECTS + 1;
    private static final int SUN_AND_MOON_LAYER = Layer.BACKGROUND + 1;
    private static final int SUN_AND_MOON_HALO_LAYER = Layer.BACKGROUND + 2;
    private static final int AVATAR_LAYER = Layer.DEFAULT;
    private static final int TURTLE_LAYER = Layer.DEFAULT;

    //const arguments:
    private static final String TXT_FOR_ENERGY = "ENERGY: ";
    private static final String TXT_FOR_SCORE = "SCORE: ";
    private static final int DIST_TO_ADD = Block.SIZE * 10;
    private static final int SEED = 417;
    private static final int AVATARS_ENERGY = 200;
    private static final int CYCLE_LENGTH = 60;
    private static final int SIZE_OF_TXT = 30;
    private static final Color SUN_HALO_COLOR = new Color(255, 255, 0, 20);
    private static final Color MOON_HALO_COLOR = new Color(255, 255, 255, 100);
    private static final int RANGE_FOR_TURTLE_CREATION = 200;
    private static final int LENGTH_OF_TXT_FOR_SCORE = 7;

    //classes arguments:
    private Tree tree;
    private Terrain terrain;
    private float sizeOfWindowX;
    private GameObject avatar;
    private Turtles turtlesMain;
    private WorldEdges worldEdges;
    private WindowController windowController;
    private ImageReader imageReader;
    private Counter energyCounter;
    private Counter scoreCounter;
    private UserInputListener inputListener;

    /**
     * function sets the games collisions
     */
    private void setCollides(){
        gameObjects().layers().shouldLayersCollide(EXTRA_GROUND_LAYER,
                EXTRA_GROUND_LAYER, false);
        gameObjects().layers().shouldLayersCollide(GROUND_LAYER,
                GROUND_LAYER, false);
        gameObjects().layers().shouldLayersCollide(GROUND_LAYER,
                EXTRA_GROUND_LAYER, false);
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER,
                TREE_TRUNK_LAYER, false);
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER,
                EXTRA_GROUND_LAYER, false);
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER,
                LEAF_LAYER, false);
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER,
                AVATAR_LAYER, false);
        gameObjects().layers().shouldLayersCollide(TREE_TRUNK_LAYER,
                AVATAR_LAYER, true);
        gameObjects().layers().shouldLayersCollide(GROUND_LAYER,
                LEAF_LAYER, true);
        gameObjects().layers().shouldLayersCollide(AVATAR_LAYER,
                TURTLE_LAYER, true);
        gameObjects().layers().shouldLayersCollide(LEAF_LAYER,
                TURTLE_LAYER, false);
        gameObjects().layers().shouldLayersCollide(TREE_TRUNK_LAYER,
                TURTLE_LAYER, true);
    }

    /**
     * creates the sun and it's halo
     */
    private void createSunAndHalo(){
        GameObject sun = Sun.create(gameObjects(),windowController.getWindowDimensions(),
                SUN_AND_MOON_LAYER, CYCLE_LENGTH);
        SunHalo.create(gameObjects(), SUN_AND_MOON_HALO_LAYER, sun, SUN_HALO_COLOR);
    }

    /**
     * creates moon adn it's halo and night effect
     */
    private void createMoonAndNight(){
        GameObject moon = Moon.create(gameObjects(),windowController.getWindowDimensions(),
                SUN_AND_MOON_LAYER, CYCLE_LENGTH, imageReader);
        MoonHalo.create(gameObjects(), SUN_AND_MOON_HALO_LAYER, moon, MOON_HALO_COLOR);
        Night.create(gameObjects(), windowController.getWindowDimensions(),
                NIGHT_LAYER, CYCLE_LENGTH/2f);
    }

    /**
     * creates the terrain
     */
    private void createTerrain(){
        terrain = new Terrain(gameObjects(), GROUND_LAYER, EXTRA_GROUND_LAYER,
                windowController.getWindowDimensions(),SEED);
        terrain.createInRange(worldEdges.getWorldsLeftEdge(), worldEdges.getWorldsRightEdge());
    }

    /**
     * creates the trees
     */
    private void createTrees(){
        tree = new Tree(gameObjects(),TREE_TRUNK_LAYER, LEAF_LAYER, SEED, terrain);
        tree.createInRange(worldEdges.getWorldsLeftEdge(), (int) sizeOfWindowX/2-3*Block.SIZE);
        tree.createInRange((int) sizeOfWindowX/2+3*Block.SIZE, worldEdges.getWorldsRightEdge());
    }

    /**
     * creates the energy counter and the score counter and adds them to the screen
     */
    private void createCounters(){
        energyCounter = new Counter(AVATARS_ENERGY);
        GraphicCounter energyCounterObject = new GraphicCounter(energyCounter, Vector2.ZERO,
                new Vector2(SIZE_OF_TXT, SIZE_OF_TXT), TXT_FOR_ENERGY);
        gameObjects().addGameObject(energyCounterObject, Layer.UI);
        scoreCounter = new Counter(0);
        GraphicCounter scoreCounterObject = new GraphicCounter(scoreCounter,
                new Vector2(sizeOfWindowX - SIZE_OF_TXT*LENGTH_OF_TXT_FOR_SCORE, 0),
                new Vector2(SIZE_OF_TXT, SIZE_OF_TXT), TXT_FOR_SCORE);
        gameObjects().addGameObject(scoreCounterObject, Layer.UI);
    }

    /**
     * creates the turtles
     */
    private void createTurtles(){
        turtlesMain = new Turtles(RANGE_FOR_TURTLE_CREATION, terrain, gameObjects(), TURTLE_LAYER,
                imageReader, tree, worldEdges);
        turtlesMain.createInRange(worldEdges.getWorldsLeftEdge(), (int) sizeOfWindowX/2-3*Block.SIZE);
    }

    /**
     * creates the avatar
     */
    private void createAvatar(){
        float x = windowController.getWindowDimensions().x()/2;
        float y = windowController.getWindowDimensions().y()/2;
        avatar = Avatar.create(gameObjects(), AVATAR_LAYER, new Vector2(x, y),
                inputListener, imageReader, energyCounter, scoreCounter);
    }

    /**
     * this function sets the camera to follow the player and creates the illusion of an infinite world
     */
    private void infiniteWordHandler(){
        setCamera(new Camera(avatar, Vector2.ZERO,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
        windowController.getWindowDimensions().mult(0.5f);
    }

    /**
     * initializes the game
     * @param imageReader Contains a single method: readImage, which reads an image from disk.
     *                 See its documentation for help.
     * @param soundReader Contains a single method: readSound, which reads a wav file from
     *                    disk. See its documentation for help.
     * @param inputListener Contains a single method: isKeyPressed, which returns whether
     *                      a given key is currently pressed by the user or not. See its
     *                      documentation.
     * @param windowController Contains an array of helpful, self-explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        //arguments:
        sizeOfWindowX = windowController.getWindowDimensions().x();
        worldEdges = new WorldEdges(-DIST_TO_ADD, (int) (sizeOfWindowX + DIST_TO_ADD));
        this.windowController = windowController;
        this.imageReader = imageReader;
        this.inputListener = inputListener;

        // create the sky:
        Sky.create(gameObjects(), windowController.getWindowDimensions(), SKY_LAYER);
        createSunAndHalo();

        createMoonAndNight();

        createTerrain();

        createTrees();

        createCounters();

        createTurtles();

        createAvatar();

        setCollides();

        infiniteWordHandler();
    }

    /**
     * maintains the non-ending world according to the avatars location
     * @param deltaTime The time, in seconds, that passed since the last invocation
     *                  of this method (i.e., since the last frame). This is useful
     *                  for either accumulating the total time that passed since some
     *                  event, or for physics integration (i.e., multiply this by
     *                  the acceleration to get an estimate of the added velocity or
     *                  by the velocity to get an estimate of the difference in position).
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        int worldsLeftEdge = worldEdges.getWorldsLeftEdge();
        int worldsRightEdge = worldEdges.getWorldsRightEdge();
        if(avatar.getCenter().x() < worldsLeftEdge + sizeOfWindowX/2){
            tree.removeInRange((worldsRightEdge- DIST_TO_ADD),  (worldsRightEdge));
            terrain.removeInRange(worldsRightEdge- DIST_TO_ADD, worldsRightEdge);
            turtlesMain.removeInRange(worldsRightEdge- DIST_TO_ADD, worldsRightEdge);
            worldEdges.setWorldsRightEdge(worldsRightEdge-DIST_TO_ADD);
            tree.createInRange(worldsLeftEdge - DIST_TO_ADD, worldsLeftEdge);
            terrain.createInRange(worldsLeftEdge - DIST_TO_ADD, worldsLeftEdge);
            turtlesMain.createInRange(worldsLeftEdge - DIST_TO_ADD, worldsLeftEdge);
            worldEdges.setWorldsLeftEdge(worldsLeftEdge-DIST_TO_ADD);
        }
        else if(avatar.getCenter().x() > worldsRightEdge - sizeOfWindowX/2){
            tree.removeInRange(worldsLeftEdge, worldsLeftEdge + DIST_TO_ADD);
            terrain.removeInRange(worldsLeftEdge, worldsLeftEdge + DIST_TO_ADD);
            turtlesMain.removeInRange(worldsLeftEdge, worldsLeftEdge + DIST_TO_ADD);
            worldEdges.setWorldsLeftEdge(worldsLeftEdge + DIST_TO_ADD);
            tree.createInRange(worldsRightEdge, worldsRightEdge + DIST_TO_ADD);
            terrain.createInRange(worldsRightEdge, worldsRightEdge + DIST_TO_ADD);
            turtlesMain.createInRange(worldsRightEdge, worldsRightEdge + DIST_TO_ADD);
            worldEdges.setWorldsRightEdge(worldsRightEdge + DIST_TO_ADD);
        }
        //TODO: there are still turtles inside the ground
    }

    /**
     * main function of the game - creates and runs the game
     * @param args arguments given to the program
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
