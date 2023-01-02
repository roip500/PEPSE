package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;
import pepse.world.Block;
import pepse.world.Terrain;

import java.awt.*;
import java.util.*;

public class Tree {
    private final GameObjectCollection gameObjects;
    private final int rootLayer;
    private final int leafLayer;
    private final int seed;
    private final Terrain terrain;
    private final NoiseGenerator noiseGenerator;
    private static final int TREE_SIZE = 10;
    private static final int RANDOM_RANGE = 10;
    private static final Color TREE_COLOR =new Color(100, 50, 20);
    private final HashMap<Integer, HashSet<GameObject>> map;

    /**
     * constructor for the Tree class.
     * saves the parameters needed to create all the trees in the game.
     * @param gameObjects list of game objects in the game
     * @param rootLayer Layer the tree object will be in the game
     * @param seed integer that will be used when randomly choosing the location of the tress
     * @param terrain object that represents the ground
     */
    public Tree(GameObjectCollection gameObjects,int rootLayer, int leafLayer,  int seed, Terrain terrain){
        this.gameObjects = gameObjects;
        this.rootLayer = rootLayer;
        this.leafLayer = leafLayer;
        this.seed = seed;
        this.terrain = terrain;
        noiseGenerator = new NoiseGenerator(seed);
        map = new HashMap<>();
    }

    /**
     * creates the trees in the range given
     * @param minX starting x coordination
     * @param maxX ending x coordination
     */
    public void createInRange(int minX, int maxX) {
        int middle = (minX + maxX)/2;
        int newMin = (int) Math.ceil((double) minX / Block.SIZE) * Block.SIZE;
        int newMax = (int) (Math.floor((double)maxX / Block.SIZE) * Block.SIZE);
        for (int i = newMin; i <= newMax; i += Block.SIZE) {
            Random rand = new Random(Objects.hash(i, seed));
            float plantTree = rand.nextInt(RANDOM_RANGE);
            if(plantTree == 1 && middle != i){
                int curMaxHeight = (int) ((Math.floor(terrain.GroundHeightAt(i) / Block.SIZE)
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
        HashSet<GameObject> set = new HashSet<>();
        int treeHeight = (int) (Math.abs(noiseGenerator.noise(xCord, yCord))*TREE_SIZE) + 5;
        for(int i = 0; i < treeHeight; i++){
            Block curBlock = new Block(new Vector2(xCord, yCord - (i+1) * Block.SIZE),
                    new RectangleRenderable(ColorSupplier.approximateColor(TREE_COLOR)));
            curBlock.setTag("trunk");
            gameObjects.addGameObject(curBlock, rootLayer);
            set.add(curBlock);
        }
        int leafRange = Math.min(7, treeHeight - 2);
        yCord -= ((treeHeight + leafRange/2) * Block.SIZE);
        xCord -= (leafRange/2 * Block.SIZE);
        for(int i = 0; i < leafRange * Block.SIZE; i += Block.SIZE ){
            for(int j = 0;  j < leafRange * Block.SIZE; j += Block.SIZE){
                Leaf leaf = new Leaf(new Vector2(xCord + j, yCord + i));
                leaf.setTag("leaf");
                gameObjects.addGameObject(leaf, leafLayer);
                set.add(leaf);
            }
        }
        map.put(xCord, set);
    }

    /**
     * removes all the gameObjects related to a tree in the space between minX to maxX
     * @param minX integer represents the starting point
     * @param maxX integer represents the ending point
     */
    public void removeInRange(int minX, int maxX){
        for(int i = minX; i<=maxX; i++){
            if(map.containsKey(i)){
                for (GameObject gameObject: map.get(i)) {
                    gameObjects.removeGameObject(gameObject, rootLayer);
                    gameObjects.removeGameObject(gameObject, leafLayer);
                }
                map.remove(i);
            }
        }
    }
}
