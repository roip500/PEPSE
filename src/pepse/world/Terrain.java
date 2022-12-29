package pepse.world;

import java.awt.*;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

public class Terrain {
    private GameObjectCollection gameObjects;
    private int groundLayer;
    private float groundHeightAtX0;
    private Vector2 windowDimensions;
    private static final int TERRAIN_DEPTH = 20;
    public static final int NOISE_MULTIPLIER = 150;
    private static final int NOISE_STABLER_AT_Y_AND_Z = 200;
    private static final int GROUND_LEVEL = 350;
    private static final int NOISE_STABLER_AT_X = 15;
    private static final String GROUND_TAG = "ground";
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);

    private NoiseGenerator noiseGenerator;


    /**
     * constructs the terrain of the game
     * @param gameObjects all the game objects of the game.
     * @param groundLayer the layer which we add the ground blocks in to.
     * @param windowDimensions the size of the screen of the game.
     * @param seed the seed for the noise generator
     */
    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {

        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        groundHeightAtX0 = (windowDimensions.y() * ((float) 1 / 3));
        this.windowDimensions = windowDimensions;
        noiseGenerator= new NoiseGenerator(seed);
    }

    /**
     * receives an X coordinate on the screen and calculates the ground height at that coordinate.
     * @param x the X coordinate that we want to calculate the ground height
     * @return the calculated height.
     */
    public float GroundHeightAt(float x) {
        float noiseReturn = noiseGenerator.noise(x/NOISE_STABLER_AT_X, NOISE_STABLER_AT_Y_AND_Z,
                NOISE_STABLER_AT_Y_AND_Z);
        return (windowDimensions.y() - noiseReturn * NOISE_MULTIPLIER) - GROUND_LEVEL;
    }

    /**
     * creates the terrain at the range received
     * @param minX the left most side of the terrain
     * @param maxX the right most side of the terrain
     */
    public void createInRange(int minX, int maxX) {
        int newMin = (int) Math.ceil((double) minX / Block.SIZE) * Block.SIZE;
        int newMax = (int) (Math.ceil((double)maxX / Block.SIZE) * Block.SIZE);
        for (int i = newMin; i <= newMax; i += Block.SIZE) {
            int curMaxHeight =
                    (int) ((Math.floor(GroundHeightAt(i) / Block.SIZE) * Block.SIZE));
            fillGround(curMaxHeight, i);
        }
    }

    /**
     * adds the ground blocks that make the terrain to the screen
     * @param height the height of the top mot block
     * @param x the x coordinate of the ground patch we want to fill
     */
    private void fillGround(int height, int x) {
        for (int i = 0; i < TERRAIN_DEPTH; i++) {
            Block curBlock = new Block(new Vector2(x, height + i * Block.SIZE),
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
            curBlock.setTag(GROUND_TAG);
            if(i < 2){
                gameObjects.addGameObject(curBlock, groundLayer);
            }
            else{
                gameObjects.addGameObject(curBlock, groundLayer + 1);
            }
        }
    }
}

