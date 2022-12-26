package pepse.world;

import java.awt.*;
import java.util.Random;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

public class Terrain {
    private GameObjectCollection gameObjects;
    private int groundLayer;
    private float groundHeightAtX0;
    private Vector2 windowDimensions;
    private int seed;
    private static final int RANDOM_RANGE = 256;
    private static final int TERRAIN_DEPTH = 20;
    private static final int NOISE_MULTIPLIER = 75;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);

    private static int[] permutation = new int[RANDOM_RANGE];
    private static Random random;
    private NoiseGenerator noiseGenerator;


    public Terrain(GameObjectCollection gameObjects, int groundLayer, Vector2 windowDimensions, int seed) {

        this.gameObjects = gameObjects;
        this.groundLayer = groundLayer;
        groundHeightAtX0 = (windowDimensions.y() * ((float) 1 / 3));
        this.windowDimensions = windowDimensions;
        this.seed = seed;
        noiseGenerator= new NoiseGenerator(seed);
    }

    public float GroundHeightAt(float x) {
        float noiseReturn = noiseGenerator.noise(x);
        return (windowDimensions.y() - noiseReturn * NOISE_MULTIPLIER) + 350;
    }

    public void createInRange(int minX, int maxX) {
        int newMin = (int) Math.ceil((double) minX / Block.SIZE) * Block.SIZE;
        int newMax = (int) (Math.floor((double)maxX / Block.SIZE) * Block.SIZE);
        for (int i = newMin; i <= newMax; i += Block.SIZE) {
            int curMaxHeight =
                    (int) ((Math.floor(GroundHeightAt(i) / Block.SIZE) * Block.SIZE));
            fillGround(curMaxHeight, i);
        }
    }

    private void fillGround(int height, int x) {
        for (int i = 0; i < TERRAIN_DEPTH; i++) {
            Block curBlock = new Block(new Vector2(x, height - i * Block.SIZE),
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
            curBlock.setTag("ground");
            gameObjects.addGameObject(curBlock, groundLayer);
        }
    }
}

