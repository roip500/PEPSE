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
    private boolean flagForVelocityX;
    private int lifeSpan;

    private static final int LEAF_SIZE = 29;
    private static final int TRANSITION_CYCLE = 3;
    private static final int DELAY_RANGE = 7;
    private static final int LIFE_SPAN_RANGE = 100;
    private static final Random rand = new Random();
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    private Transition<Float> horizontalTransition;
    private Transition<Float> angleFunc;
    private Transition<Float> dimensionsFunc;

    /**
     * constructor for Leaf and adds it to the game.
     * @param topLeftCorner Vector2 object that represents the location of the object in the game
     */
    public Leaf(Vector2 topLeftCorner) {
        super(topLeftCorner,new Vector2(LEAF_SIZE,LEAF_SIZE),
                new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));
        this.topLeftCorner = topLeftCorner;
        this.lifeSpan = rand.nextInt(LIFE_SPAN_RANGE);
        new ScheduledTask(this,(float) rand.nextInt(DELAY_RANGE),
                true, this::setLeafTransitions);
        flagForVelocityX = false;
    }

    /**
     * sets the transitions of the leaf
     */
    private void setLeafTransitions(){
        angleFunc = new Transition<>(this,
                this.renderer()::setRenderableAngle,
                -20F,
                20F,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                TRANSITION_CYCLE,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                () -> lifeSpan -= 1);
        dimensionsFunc = new Transition<>(this,
                aFloat ->  this.setDimensions(new Vector2(aFloat, aFloat)),
                (float)LEAF_SIZE,
                (float)(LEAF_SIZE-5),
                Transition.CUBIC_INTERPOLATOR_FLOAT,
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
        transform().setVelocityX(0);
        transform().setVelocityY(0);
        removeComponent(horizontalTransition);
        removeComponent(angleFunc);
        removeComponent(dimensionsFunc);
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
        if (lifeSpan == 0 && this.getVelocity().y() == 0 && !flagForVelocityX){
            transform().setVelocityY(LEAF_SPEED);
            this.renderer().fadeOut(TRANSITION_CYCLE * 10, this::restoreLeaf);
            horizontalTransition = new Transition<>(this,
                    (aFloat) ->transform().setVelocityX(aFloat),
                    -LEAF_SPEED,
                    LEAF_SPEED,
                    Transition.LINEAR_INTERPOLATOR_FLOAT,
                    TRANSITION_CYCLE,
                    Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                    null);
            flagForVelocityX = true;
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
        flagForVelocityX = false;
        this.renderer().fadeIn(rand.nextInt(TRANSITION_CYCLE) * 10, null);
        new ScheduledTask(this,(float) rand.nextInt(DELAY_RANGE),
                true, this::setLeafTransitions);
    }

    public void removeTransitions(){
        removeComponent(horizontalTransition);
        removeComponent(angleFunc);
        removeComponent(dimensionsFunc);
    }
}
