package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Avatar;
import pepse.world.Sky;
import pepse.world.Terrain;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.daynight.Night;

import java.awt.*;

public class PepseGameManager extends GameManager{

    private static final int CYCLE_LENGTH = 60;
    private static final Color BASE_GROUND_COLOR = new Color(212,123,74);
    private static final Color HALO_COLOR = new Color(255, 255, 0, 20);
    private static final Renderable BLOCK_RENDERER =
            new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));

    /**
     * initializes the game
     * @param imageReader Contains a single method: readImage, which reads an image from disk.
     *                 See its documentation for help.
     * @param soundReader Contains a single method: readSound, which reads a wav file from
     *                    disk. See its documentation for help.
     * @param inputListener Contains a single method: isKeyPressed, which returns whether
     *                      a given key is currently pressed by the user or not. See its
     *                      documentation.
     * @param windowController Contains an array of helpful, self explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        // create the sky:
        GameObject sky = Sky.create(gameObjects(), windowController.getWindowDimensions(), Layer.BACKGROUND);

        // create the sun:
        GameObject sun = Sun.create(gameObjects(),windowController.getWindowDimensions(),
                Layer.BACKGROUND + 1, CYCLE_LENGTH);

        // create the sun halo:
        GameObject halo = SunHalo.create(gameObjects(), Layer.BACKGROUND + 2, sun, HALO_COLOR);

        // create the night:
        GameObject night = Night.create(gameObjects(), windowController.getWindowDimensions(),
                Layer.DEFAULT+3, CYCLE_LENGTH);

        // create terrain:
        Terrain terrain = new Terrain(gameObjects(), Layer.STATIC_OBJECTS, windowController.getWindowDimensions()
                ,777);
        terrain.createInRange(0, (int) windowController.getWindowDimensions().x());

        // create avatar:
        float x = windowController.getWindowDimensions().x()/2;
        float y = windowController.getWindowDimensions().y()/2;
        GameObject avatar = Avatar.create(gameObjects(), Layer.DEFAULT, new Vector2(x, y),
                inputListener, imageReader);

        // infinite world:
        setCamera(new Camera(avatar, Vector2.ZERO,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
        windowController.getWindowDimensions().mult(0.5f);
    }

    /**
     * main function of the game - creates and runs the game
     * @param args arguments given to the program
     */
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
