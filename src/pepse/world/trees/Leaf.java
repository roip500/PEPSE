package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.Random;

public class Leaf extends GameObject{

    private static final float LEAF_SPEED = 15;
    private final Vector2 topLeftCorner;
    private int lifeSpan;

    private static final float LEAF_ANGLE = 10;
    private static final float FADE_IN_TIME = 10;
    private static final float LEAF_SMALLER_SIZE = 24;
    private static final int LEAF_BIGGER_SIZE = 29;
    private static final int TRANSITION_CYCLE = 2;
    private static final float FADE_OUT_CYCLE = TRANSITION_CYCLE * 15;
    private static final int DELAY_RANGE = 7;
    private static final int LIFE_SPAN_RANGE = 50;
    private static final Random rand = new Random();
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    private final Transition<Float> horizontalTransition;
    private Transition<Float> angleFunc;
    private Transition<Float> dimensionsFunc;
    private boolean hasCollided = false;

    /**
     * constructor for Leaf and adds it to the game.
     * @param topLeftCorner Vector2 object that represents the location of the object in the game
     */
    public Leaf(Vector2 topLeftCorner) {
        super(topLeftCorner,new Vector2(LEAF_BIGGER_SIZE, LEAF_BIGGER_SIZE),
                new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));
        this.topLeftCorner = topLeftCorner;
        this.renderer().setRenderableAngle(-LEAF_ANGLE);
        this.lifeSpan = rand.nextInt(LIFE_SPAN_RANGE);
        new ScheduledTask(this,(float) rand.nextInt(DELAY_RANGE),
                false, this::setLeafTransitions);
        horizontalTransition = new Transition<>(this,
                (aFloat) ->transform().setVelocityX(aFloat),
                -LEAF_SPEED,
                LEAF_SPEED,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                TRANSITION_CYCLE,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
        removeComponent(horizontalTransition);
    }

    /**
     * sets the transitions of the leaf
     */
    private void setLeafTransitions(){
        angleFunc = new Transition<>(this,
                this.renderer()::setRenderableAngle,
                -1 * LEAF_ANGLE,
                LEAF_ANGLE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                TRANSITION_CYCLE,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                () -> lifeSpan -= 1);
        dimensionsFunc = new Transition<>(this,
                aFloat ->  this.setDimensions(new Vector2(aFloat, aFloat)),
                (float) LEAF_BIGGER_SIZE,
                (float)(LEAF_SMALLER_SIZE),
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                TRANSITION_CYCLE,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }

    /**
     * if the leaf had collided with the ground, then the func sets its y-velocity to 0
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        removeTransitions();
        transform().setVelocityX(0);
        transform().setVelocityY(0);
        hasCollided = true;
    }

    /**
     * checks if the life span of the leaf has reached 0. if yes then it adds a y-velocity
     * and starts the fading transition
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
        if (lifeSpan <= 0 && this.getVelocity().y() == 0 && !hasCollided){
            addComponent(horizontalTransition);
            transform().setVelocityX(LEAF_SPEED);
            transform().setVelocityY(LEAF_SPEED);
            this.renderer().fadeOut(FADE_OUT_CYCLE, this::restoreLeaf);
        }
        else if (hasCollided && getVelocity().x() != 0) {
            transform().setVelocityX(0);
        }
    }

    /**
     * returns the leaf to its original left corner Vector2 and starts a transition
     * to re-appear on the tree
     */
    private void restoreLeaf(){
        this.lifeSpan = rand.nextInt(LIFE_SPAN_RANGE);
        this.setTopLeftCorner(topLeftCorner);
        transform().setVelocityY(0);
        transform().setVelocityX(0);
        this.renderer().fadeIn(rand.nextInt(TRANSITION_CYCLE) * FADE_IN_TIME, null);
        new ScheduledTask(this,(float) rand.nextInt(DELAY_RANGE),
                false, this::setLeafTransitions);
        hasCollided = false;
    }

    /**
     * funtion removes all the transitions the leaf has
     */
    public void removeTransitions(){
        removeComponent(horizontalTransition);
        removeComponent(angleFunc);
        removeComponent(dimensionsFunc);
    }
}
