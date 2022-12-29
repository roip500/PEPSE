package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.Objects;
import java.util.Random;

public class Leaf extends GameObject{
    private final GameObjectCollection gameObjects;
    private final Vector2 topLeftCorner;
    private int lifeSpan;

    private static final int LEAF_SIZE = 29;
    private static final int TRANSITION_CYCLE = 3;
    private static final int DELAY_RANGE = 7;
    private static final int LIFE_SPAN_RANGE = 100;
    private static final Random rand = new Random();
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    public Leaf(GameObjectCollection gameObjects, Vector2 topLeftCorner) {
        super(topLeftCorner,new Vector2(LEAF_SIZE,LEAF_SIZE),
                new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));
        this.gameObjects = gameObjects;
        this.topLeftCorner = topLeftCorner;
        gameObjects.addGameObject(this, Layer.DEFAULT);
        new ScheduledTask(this,(float) rand.nextInt(DELAY_RANGE),
                true, this::leafTransitions);
        this.lifeSpan = rand.nextInt(LIFE_SPAN_RANGE);
    }

    private void leafTransitions(){
        new Transition<Float>(this,
                this.renderer()::setRenderableAngle,
                -10F,
                10F,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                TRANSITION_CYCLE,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                () -> lifeSpan -=1);
        new Transition<Float>(this,
                aFloat ->  this.setDimensions(new Vector2(aFloat, aFloat)),
                (float)LEAF_SIZE,
                (float)(LEAF_SIZE-5),
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                TRANSITION_CYCLE,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
    }


    @Override
    public boolean shouldCollideWith(GameObject other) {
        return Objects.equals(other.getTag(), "ground");
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        transform().setVelocityY(0);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (lifeSpan == 0 && this.getVelocity().y() == 0){
            transform().setVelocityY(15);
            new Transition<Float>(this,
                    this.renderer()::setOpaqueness,
                    1F,
                    0F,
                    Transition.LINEAR_INTERPOLATOR_FLOAT,
                    TRANSITION_CYCLE * 10,
                    Transition.TransitionType.TRANSITION_ONCE,
                    this::restoreLeaf);
        }
    }
    private void restoreLeaf(){
        this.lifeSpan = rand.nextInt(LIFE_SPAN_RANGE);
        this.setTopLeftCorner(topLeftCorner);
        transform().setVelocityY(0);
        new Transition<Float>(this,
                this.renderer()::setOpaqueness,
                0F,
                1F,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                TRANSITION_CYCLE * 5,
                Transition.TransitionType.TRANSITION_ONCE,
                this::restoreLeaf);
    }
}
