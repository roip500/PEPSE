package pepse.world.turtles;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.GroundHeightCalculator;
import pepse.world.WorldEdges;

import java.util.Random;


public class Turtle extends GameObject {

    private static final int USING_RIGHT = 1;
    private static final int USING_LEFT = 0;
    private static final int JUMP_SPEED = 300;
    private static final int FALLING_MAX_SPEED = 400;
    private static final int GRAVITY_EFFECT= 10;
    private static final int WALKING_SPEED = 300;
    private static final int TRANSITION_CYCLE = 10;
    private static final float FADE_OUT_TIME = 0.5f;
    private static final int PERCENT_FOR_JUMP = 50;
    private static final int INTEGER_REP_JUMP = 1;
    private static final String TURTLE_TAG = "turtle";
    private static final String AVATAR_TAG = "avatar";

    private final GameObjectCollection gameObjects;
    private final int turtleLayer;
    private final WorldEdges worldEdges;
    private GroundHeightCalculator heightFunc;
    private final Random rand;
    private final Renderable leftSideRun;
    private final Renderable rightSideRun;
    private int whichLegToUse;
    private final Vector2 yMovementDir;
    private Vector2 xMovementDir;
    private boolean inTheAir;


    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param leftSideRun   the image of the turtle running with left food front
     * @param rightSideRun  the image of the turtle running with right food front
     */
    public Turtle(Vector2 topLeftCorner, Vector2 dimensions, Renderable leftSideRun,
                  Renderable rightSideRun, GameObjectCollection gameObjects,
                  int turtleLayer, WorldEdges worldEdges, GroundHeightCalculator heightFunc) {
        super(topLeftCorner, dimensions, leftSideRun);
        this.gameObjects = gameObjects;
        this.turtleLayer = turtleLayer;
        this.worldEdges = worldEdges;
        this.heightFunc = heightFunc;
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.rightSideRun = rightSideRun;
        this.leftSideRun = leftSideRun;
        yMovementDir = Vector2.UP;
        xMovementDir = Vector2.LEFT;
        whichLegToUse = USING_LEFT;
        rand = new Random();
        inTheAir = false;
        this.setTag(TURTLE_TAG);
        new Transition<>(this,
                aFloat->transform().setVelocityX(xMovementDir.x()*WALKING_SPEED),
                0f, 1f,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                TRANSITION_CYCLE,
                Transition.TransitionType.TRANSITION_LOOP,
                ()->xMovementDir =  new Vector2(xMovementDir.x()*(-1),0));
    }

    /**
     * in charge of the turtles movements
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if(this.getCenter().y() > heightFunc.GroundHeightAt(this.getCenter().x())){
            gameObjects.removeGameObject(this, turtleLayer);
        }
        setVelocityY();
        setImage();
        setMovementX();
    }

    /**
     * makes sure the turtle doesn't fall off the world
     */
    private void setMovementX() {
        if(this.getCenter().x() == worldEdges.getWorldsLeftEdge()){
            xMovementDir = Vector2.RIGHT;
        }
        if(this.getCenter().x() == worldEdges.getWorldsRightEdge()){
            xMovementDir = Vector2.LEFT;
        }

    }

    /**
     * sets the velocity y of the turtle
     */
    private void setVelocityY(){
        if(rand.nextInt(PERCENT_FOR_JUMP) == INTEGER_REP_JUMP && !inTheAir){
            transform().setVelocityY(yMovementDir.y()*JUMP_SPEED);
            inTheAir = true;
        }
        else if(transform().getVelocity().y() < FALLING_MAX_SPEED){
            transform().setVelocityY(getVelocity().y() + GRAVITY_EFFECT);
        }
    }

    /**
     * sets the image of the turtle
     */
    private void setImage(){
        if(whichLegToUse == USING_LEFT){
            renderer().setRenderable(leftSideRun);
            whichLegToUse = USING_RIGHT;
        }
        else {
            renderer().setRenderable(rightSideRun);
            whichLegToUse = USING_LEFT;
        }
        renderer().setIsFlippedHorizontally(!(getVelocity().x() > 0));
    }

    /**
     * when turtle collides with avatar it fades out of the screen and is removed from the game
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(other.getTag().equals(AVATAR_TAG)){
            this.renderer().fadeOut(FADE_OUT_TIME, ()->gameObjects.removeGameObject(this, turtleLayer));
        }
        if(collision.getNormal().y() < 0){
            inTheAir = false;
        }
        if(collision.getNormal().x() !=0){
            xMovementDir =  new Vector2(xMovementDir.x()*(-1),0);
        }
    }
}
