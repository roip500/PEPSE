package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;
import pepse.world.Block;
import pepse.world.Terrain;

import java.awt.*;
import java.util.Random;

public class Tree {
    private GameObjectCollection gameObjects;
    private int rootLayer;
    private Terrain terrain;
    private NoiseGenerator noiseGenerator;
    private static final int TREE_SIZE = 10;
    private static final Color TREE_COLOR =new Color(100, 50, 20);

    /**
     * constructor for the Tree class.
     * saves the parameters needed to create all the trees in the game.
     * @param gameObjects list of game objects in the game
     * @param rootLayer Layer the tree object will be in the game
     * @param seed integer that will be used when randomly choosing the location of the tress
     * @param terrain object that represents the ground
     */
    public Tree(GameObjectCollection gameObjects,int rootLayer, int seed, Terrain terrain){
        this.gameObjects = gameObjects;
        this.rootLayer = rootLayer;
        this.terrain = terrain;
        noiseGenerator = new NoiseGenerator(seed);
    }

    /**
     * creates the trees in the range given
     * @param minX starting x coordination
     * @param maxX ending x coordination
     */
    public void createInRange(int minX, int maxX, Terrain terrain) {
        int newMin = (int) Math.ceil((double) minX / Block.SIZE) * Block.SIZE;
        int newMax = (int) (Math.floor((double)maxX / Block.SIZE) * Block.SIZE);
        for (int i = newMin; i <= newMax; i += Block.SIZE) {
            int curMaxHeight =
                    (int) ((Math.floor(terrain.GroundHeightAt(i) / Block.SIZE) * Block.SIZE));
            float plantTree = noiseGenerator.noise(i);
            if(plantTree >= 0.1f && plantTree <= 0.2f){ //TODO: use noise
                buildTree(curMaxHeight, i);
            }
        }
    }

    /**
     * creates a tree - branch and leafs
     * @param yCord grounds y coordination
     * @param xCord grounds x coordination
     */
    private void buildTree(int yCord, int xCord) {
//        int treeHeight = rand.nextInt(TREE_SIZE) + 5; //TODO: use noise
        int treeHeight = (int) (Math.abs(noiseGenerator.noise(xCord, yCord))*TREE_SIZE + 5);
        //todo check with roi what we want to do with the leaves now that we use noise, look a bit
        // different than before
        for(int i = 0; i < treeHeight; i++){
            Block curBlock = new Block(new Vector2(xCord, yCord - (i+1) * Block.SIZE),
                    new RectangleRenderable(ColorSupplier.approximateColor(TREE_COLOR)));
            curBlock.setTag("tree");
            gameObjects.addGameObject(curBlock, rootLayer);
        }
        int leafRange = Math.min(7, treeHeight - 2);
        yCord -= ((treeHeight + 3) * Block.SIZE);
        xCord -= (leafRange/2 * Block.SIZE);
        for(int i = 0; i < leafRange * Block.SIZE; i += Block.SIZE ){
            for(int j = 0;  j < leafRange * Block.SIZE; j += Block.SIZE){
                new Leaf(gameObjects, new Vector2(xCord + j, yCord + i));
            }
        }
    }
}
