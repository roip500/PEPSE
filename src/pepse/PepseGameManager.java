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

    //classes arguments:
    private Tree tree;
    private Terrain terrain;
    private float worldsLeftEdge;
    private float worldsRightEdge;
    private float sizeOfWindowX;
    private GameObject avatar;

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
        worldsLeftEdge = -DIST_TO_ADD;
        worldsRightEdge = sizeOfWindowX + DIST_TO_ADD;

        // create the sky:
        Sky.create(gameObjects(), windowController.getWindowDimensions(), SKY_LAYER);

        // create the sun and the halo:
        GameObject sun = Sun.create(gameObjects(),windowController.getWindowDimensions(),
                SUN_AND_MOON_LAYER, CYCLE_LENGTH);
        SunHalo.create(gameObjects(), SUN_AND_MOON_HALO_LAYER, sun, SUN_HALO_COLOR);

        // create the moon and the night effect:
        GameObject moon = Moon.create(gameObjects(),windowController.getWindowDimensions(),
                SUN_AND_MOON_LAYER, CYCLE_LENGTH, imageReader);
        MoonHalo.create(gameObjects(), SUN_AND_MOON_HALO_LAYER, moon, MOON_HALO_COLOR);
        Night.create(gameObjects(), windowController.getWindowDimensions(),
                NIGHT_LAYER, CYCLE_LENGTH/2f);

        // create terrain:
        terrain = new Terrain(gameObjects(), GROUND_LAYER, EXTRA_GROUND_LAYER,
                windowController.getWindowDimensions(),SEED);
        terrain.createInRange((int) worldsLeftEdge, (int) worldsRightEdge);

        //create trees:
        tree = new Tree(gameObjects(),TREE_TRUNK_LAYER, LEAF_LAYER, SEED, terrain);
        tree.createInRange((int) worldsLeftEdge, (int) sizeOfWindowX/2-3*Block.SIZE);
        tree.createInRange((int) sizeOfWindowX/2+3*Block.SIZE, (int) worldsRightEdge);

        //create counters:
        Counter energyCounter = new Counter(AVATARS_ENERGY);
        GraphicCounter energyCounterObject = new GraphicCounter(energyCounter, Vector2.ZERO,
                new Vector2(SIZE_OF_TXT, SIZE_OF_TXT), TXT_FOR_ENERGY);
        gameObjects().addGameObject(energyCounterObject, Layer.UI);
        Counter scoreCounter = new Counter(0);
        GraphicCounter scoreCounterObject = new GraphicCounter(scoreCounter,
                new Vector2(sizeOfWindowX - SIZE_OF_TXT*6, 0),
                new Vector2(SIZE_OF_TXT, SIZE_OF_TXT), TXT_FOR_SCORE);
        gameObjects().addGameObject(scoreCounterObject, Layer.UI);


        // create avatar:
        float x = windowController.getWindowDimensions().x()/2;
        float y = windowController.getWindowDimensions().y()/2;
        avatar = Avatar.create(gameObjects(), AVATAR_LAYER, new Vector2(x, y),
                inputListener, imageReader, energyCounter, scoreCounter);

        // set collision:
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

        // infinite world:
        setCamera(new Camera(avatar, Vector2.ZERO,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
        windowController.getWindowDimensions().mult(0.5f);

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
        if(avatar.getCenter().x() < worldsLeftEdge + sizeOfWindowX/2){
            tree.removeInRange((int) (worldsRightEdge- DIST_TO_ADD), (int) (worldsRightEdge));
            terrain.removeInRange((int) (worldsRightEdge- DIST_TO_ADD), (int) (worldsRightEdge));
            worldsRightEdge -= DIST_TO_ADD;
            tree.createInRange((int) (worldsLeftEdge - DIST_TO_ADD), (int) (worldsLeftEdge));
            terrain.createInRange((int) (worldsLeftEdge - DIST_TO_ADD), (int) (worldsLeftEdge));
            worldsLeftEdge -= DIST_TO_ADD;
        }
        else if(avatar.getCenter().x() > worldsRightEdge - sizeOfWindowX/2){
            tree.removeInRange((int) (worldsLeftEdge), (int) (worldsLeftEdge + DIST_TO_ADD));
            terrain.removeInRange((int) (worldsLeftEdge), (int) (worldsLeftEdge + DIST_TO_ADD));
            worldsLeftEdge += DIST_TO_ADD;
            tree.createInRange((int) (worldsRightEdge), (int) (worldsRightEdge + DIST_TO_ADD));
            terrain.createInRange((int) (worldsRightEdge), (int) (worldsRightEdge + DIST_TO_ADD));
            worldsRightEdge += DIST_TO_ADD;
        }
    }

    /**
     * main function of the game - creates and runs the game
     * @param args arguments given to the program
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
