package pepse.world.trees;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;
import pepse.world.Block;
import pepse.world.GroundHeightCalculator;

import java.awt.*;
import java.util.*;

public class Tree implements TreeChecker {

    private static final int PLANT = 1;
    private static final String TRUNK_TAG = "trunk";
    private static final int MIN_TREE_HEIGHT = 5;
    private static final String LEAF_TAG = "leaf";
    private static final int MAX_NUM_OF_LEAVES_IN_ROW = 7;
    private static final Color TREE_COLOR =new Color(100, 50, 20);
    private static final int TREE_SIZE = 10;
    private static final int RANDOM_RANGE = 10;
    private static final int MIN_SPACE_BETWEEN_TREES = 2*Block.SIZE;

    private final GameObjectCollection gameObjects;
    private final int rootLayer;
    private final int leafLayer;
    private final int seed;
    private final GroundHeightCalculator HeightFunc;
    private final NoiseGenerator noiseGenerator;
    private final HashMap<Integer, HashSet<Block>> trunkMap;
    private final HashMap<Integer, HashSet<Leaf>> leafMap;


    /**
     * constructor for the Tree class.
     * saves the parameters needed to create all the trees in the game.
     * @param gameObjects list of game objects in the game
     * @param rootLayer Layer the tree object will be in the game
     * @param seed integer that will be used when randomly choosing the location of the tress
     * @param HeightFunc functional interface that calculates the height of the ground at a certain
     *                   coordinate.
     */
    public Tree(GameObjectCollection gameObjects,int rootLayer, int leafLayer,  int seed,
                GroundHeightCalculator HeightFunc){
        this.gameObjects = gameObjects;
        this.rootLayer = rootLayer;
        this.leafLayer = leafLayer;
        this.seed = seed;
        this.HeightFunc = HeightFunc;
        noiseGenerator = new NoiseGenerator(seed);
        trunkMap = new HashMap<>();
        leafMap = new HashMap<>();
    }

    /**
     * creates the trees in the range given
     * @param minX starting x coordination
     * @param maxX ending x coordination
     */
    public void createInRange(int minX, int maxX) {
        int newMin = (int) Math.ceil((double) minX / Block.SIZE) * Block.SIZE;
        int newMax = (int) (Math.floor((double)maxX / Block.SIZE) * Block.SIZE);
        for (int i = newMin; i <= newMax; i += MIN_SPACE_BETWEEN_TREES) {
            Random rand = new Random(Objects.hash(i, seed));
            float plantTree = rand.nextInt(RANDOM_RANGE);
            if(plantTree == PLANT){
                int curMaxHeight = (int) ((Math.floor(HeightFunc.GroundHeightAt(i) / Block.SIZE)
                        * Block.SIZE));
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

        //check tree doesn't exist in location:
        if(trunkMap.containsKey(xCord)){
            return;
        }

        //create trunk:
        HashSet<Block> truckSet = new HashSet<>();
        int treeHeight = (int) (Math.abs(noiseGenerator.noise(xCord, yCord))*TREE_SIZE) + MIN_TREE_HEIGHT;
        for(int i = 0; i < treeHeight; i++){
            Block curBlock = new Block(new Vector2(xCord, yCord - (i+1) * Block.SIZE),
                    new RectangleRenderable(ColorSupplier.approximateColor(TREE_COLOR)));
            curBlock.setTag(TRUNK_TAG);
            gameObjects.addGameObject(curBlock, rootLayer);
            truckSet.add(curBlock);
        }
        trunkMap.put(xCord, truckSet);

        //create leafs:
        HashSet<Leaf> leafSet = new HashSet<>();
        int leafRange = Math.min(MAX_NUM_OF_LEAVES_IN_ROW, treeHeight - 2);
        int y = yCord - ((treeHeight + leafRange/2) * Block.SIZE);
        int x = xCord - (leafRange/2 * Block.SIZE) + (1 - leafRange % 2) * Block.SIZE/2;
        for(int i = 0; i < leafRange * Block.SIZE; i += Block.SIZE ){
            for(int j = 0;  j < leafRange * Block.SIZE; j += Block.SIZE){
                Leaf leaf = new Leaf(new Vector2(x + j, y + i));
                leaf.setTag(LEAF_TAG);
                gameObjects.addGameObject(leaf, leafLayer);
                leafSet.add(leaf);
            }
        }
        leafMap.put(xCord, leafSet);
    }

    /**
     * removes all the gameObjects related to a tree in the space between minX to maxX
     * @param minX integer represents the starting point
     * @param maxX integer represents the ending point
     */
    public void removeInRange(int minX, int maxX){
        for(int i = minX; i<=maxX; i++){
            if(trunkMap.containsKey(i)){
                for (Block block: trunkMap.get(i)) {
                    gameObjects.removeGameObject(block, rootLayer);
                }
                for (Leaf leaf:leafMap.get(i)) {
                    leaf.removeTransitions();
                    gameObjects.removeGameObject(leaf, leafLayer);
                }
                trunkMap.remove(i);
                leafMap.remove(i);
            }
        }
    }



    /**
     * returns true if there is a tree in the location given
     * @param x - x coordination of the location to check
     * @return true is yes, false if no
     */
    @Override
    public boolean isThereATreeHere(int x){
        return trunkMap.containsKey(x);
    }
}
