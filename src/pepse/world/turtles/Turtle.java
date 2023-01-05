package pepse.world.turtles;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Avatar;


public class Turtle extends GameObject {

    private static final int USING_RIGHT = 1;
    private static final int USING_LEFT = 0;
    private static final int JUMP_SPEED = 300;
    private static final int FALLING_MAX_SPEED = 400;
    private static final int GRAVITY_EFFECT= 10;
    private static final int WALKING_SPEED = 300;
    private static final int CHANCE_TO_JUMP = 10;
    private static final int TRANSITION_CYCLE = 30;
    private static final float FADE_OUT_TIME = 0.5f;

    private final Renderable leftSideRun;
    private final Renderable rightSideRun;
    private int whichLegToUse;
    Vector2 yMovementDir;
    private GameObjectCollection gameObjects;
    private int turtleLayer;


    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param leftSideRun   the image of the turtle running with left food front
     * @param rightSideRun  the image of the turtle running with rihgt food front
     */
    public Turtle(Vector2 topLeftCorner, Vector2 dimensions, Renderable leftSideRun,
                  Renderable rightSideRun, GameObjectCollection gameObjects, int turtleLayer) {
        super(topLeftCorner, dimensions, leftSideRun);
        this.gameObjects = gameObjects;
        this.turtleLayer = turtleLayer;
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.rightSideRun = rightSideRun;
        this.leftSideRun = leftSideRun;
        yMovementDir = Vector2.ZERO;
        whichLegToUse = USING_LEFT;
    }

    /**
     * in charge of the turtles movments
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
        if(other instanceof Avatar){
            this.renderer().fadeOut(FADE_OUT_TIME, ()->gameObjects.removeGameObject(this, turtleLayer));
        }
    }
}
