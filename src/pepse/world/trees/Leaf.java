package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.Random;

public class Leaf extends GameObject{
    private GameObjectCollection gameObjects;
    private Vector2 topLeftCorner;
    private int lifeSpan;

    private static final int LEAF_SIZE = 29;
    private static final int TRANSITION_CYCLE = 3;
    private static final Color LEAF_COLOR = new Color(50, 200, 30);
    public Leaf(GameObjectCollection gameObjects, Vector2 topLeftCorner, int lifeSpan) {
        super(topLeftCorner,new Vector2(LEAF_SIZE,LEAF_SIZE),
                new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));
        this.gameObjects = gameObjects;
        this.topLeftCorner = topLeftCorner;
        this.lifeSpan = lifeSpan;
        gameObjects.addGameObject(this, Layer.STATIC_OBJECTS);
        Random rand = new Random();
        new ScheduledTask(this,(float) rand.nextInt(7), true,
                this::leafTransitions);
    }

    private void leafTransitions(){
        var angleFunc = new Transition<Float>(this,
                this.renderer()::setRenderableAngle,
                -10F,
                10F,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                TRANSITION_CYCLE,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
        var widthFunc =new Transition<Float>(this,
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
        return false;
    }
}
