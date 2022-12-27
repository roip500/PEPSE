package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;
import pepse.world.Terrain;

import java.awt.*;
import java.util.Random;

public class Tree {
    private GameObjectCollection gameObjects;
    private int rootLayer;
    private Terrain terrain;
    private Random rand;
    private static final int TREE_SIZE = 10;
    private static final Color TREE_COLOR =new Color(100, 50, 20);


    public Tree(GameObjectCollection gameObjects,int rootLayer, int seed, Terrain terrain){
        this.gameObjects = gameObjects;
        this.rootLayer = rootLayer;
        this.terrain = terrain;
        rand = new Random(seed);
    }
    /**
     *
     * @param minX
     * @param maxX
     */
    public void createInRange(int minX, int maxX, Terrain terrain) {
        int newMin = (int) Math.ceil((double) minX / Block.SIZE) * Block.SIZE;
        int newMax = (int) (Math.floor((double)maxX / Block.SIZE) * Block.SIZE);
        for (int i = newMin; i <= newMax; i += Block.SIZE) {
            int curMaxHeight =
                    (int) ((Math.floor(terrain.GroundHeightAt(i) / Block.SIZE) * Block.SIZE));
            if(rand.nextInt(10) == 1){
                buildTree(curMaxHeight, i);
            }
        }
    }

    private void buildTree(int yCoord, int xCoord) {
        int treeHeight = rand.nextInt(TREE_SIZE) + 5;
        for(int i = 0; i < treeHeight; i++){
            Block curBlock = new Block(new Vector2(xCoord, yCoord - (i+1) * Block.SIZE),
                    new RectangleRenderable(ColorSupplier.approximateColor(TREE_COLOR)));
            curBlock.setTag("tree");
            gameObjects.addGameObject(curBlock, rootLayer);
        }
    }
}
