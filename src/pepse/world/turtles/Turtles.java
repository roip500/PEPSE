package pepse.world.turtles;

import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;
import pepse.world.GroundHeightCalculator;
import pepse.world.TreeChecker;
import pepse.world.WorldEdges;

import java.util.*;

public class Turtles {

    private static final String RUNNING_LEFT_IMAGE_LOCATION = "assets/turtle-left-foot.png";
    private static final String RUNNING_RIGHT_IMAGE_LOCATION = "assets/turtle-right-foot.png";
    private static final int CREATE_TURTLE = 10;
    private static final int SIZE_OF_TURTLE = 70;
    private static final int MIN_DIST_BETWEEN_TURTLES = 300;
    private final int randomRange;
    private final HashSet<Turtle> setOfTurtles;
    private final GroundHeightCalculator heightFunc;
    private final GameObjectCollection gameObjects;
    private final int turtleLayer;
    private final TreeChecker treeChecker;
    private final WorldEdges worldEdges;
    private final Renderable leftSideRun;
    private final Renderable rightSideRun;
    private final Random rand;


    /**
     * constructor for Turtles - in charge of creating the turtles in the game
     * @param randomRange - range for random to create a turtle
     * @param heightFunc - function from Terrain that tells us the height of the ground at x coordination
     * @param gameObjects - list of all the game objects in the game
     * @param turtleLayer - layer the turtles are in the game
     * @param imageReader - allows us to add images to the Game Objects
     */
    public Turtles(int randomRange, GroundHeightCalculator heightFunc,
                   GameObjectCollection gameObjects, int turtleLayer,
                   ImageReader imageReader, TreeChecker treeChecker, WorldEdges worldEdges){
        this.gameObjects = gameObjects;
        this.turtleLayer = turtleLayer;
        this.treeChecker = treeChecker;
        this.worldEdges = worldEdges;
        setOfTurtles = new HashSet<>();
        this.randomRange = randomRange;
        this.heightFunc = heightFunc;
        this.rand = new Random();
        this.leftSideRun = imageReader.readImage(RUNNING_LEFT_IMAGE_LOCATION,true);
        this.rightSideRun = imageReader.readImage(RUNNING_RIGHT_IMAGE_LOCATION,true);
    }

    /**
     * creates the turtles in the range given
     * @param minX - starting x coordination
     * @param maxX - ending x coordination
     */
    public void createInRange(int minX, int maxX){
        int newMin = (int) Math.ceil((double) minX / Block.SIZE) * Block.SIZE;
        int newMax = (int) (Math.floor((double)maxX / Block.SIZE) * Block.SIZE);
        for (int x = newMin; x <= newMax; x ++) {
            if(rand.nextInt(randomRange) == CREATE_TURTLE && !treeChecker.isThereATreeHere(x)){
                int y = (int) ((Math.floor(heightFunc.GroundHeightAt(x) / Block.SIZE)
                        * Block.SIZE) - SIZE_OF_TURTLE);
                Turtle turtle = new Turtle(new Vector2(x, y), new Vector2(SIZE_OF_TURTLE, SIZE_OF_TURTLE),
                        leftSideRun, rightSideRun, gameObjects, turtleLayer, worldEdges, heightFunc);
                gameObjects.addGameObject(turtle, turtleLayer);
                setOfTurtles.add(turtle);
                x += MIN_DIST_BETWEEN_TURTLES;
            }
        }
    }

    /**
     * removes all the Turtles in the space between minX to maxX
     * @param minX integer represents the starting point
     * @param maxX integer represents the ending point
     */
    public void removeInRange(int minX, int maxX){
        ArrayList<Turtle> turtles = new ArrayList<>();
        for (Turtle turtle: setOfTurtles) {
            if(turtle.getCenter().x() >= minX && turtle.getCenter().x() <= maxX){
                gameObjects.removeGameObject(turtle, turtleLayer);
                turtles.add(turtle);
            }
        }
        for(Turtle turtle: turtles){
            setOfTurtles.remove(turtle);
        }
    }
}
