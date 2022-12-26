package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class Night {

    /**
     * creates the night - black screen that its transparency
     * @param gameObjectCollection list of all the game objects.
     * @param windowDimension size of the window
     * @param nightLayer layer in the board
     * @return game object that represents the sun
     */
    public static GameObject create(GameObjectCollection gameObjectCollection,
                                    Vector2 windowDimension,
                                    int nightLayer,
                                    float cycleLength){

        GameObject night = new GameObject(Vector2.ZERO, windowDimension,
                new RectangleRenderable(Color.BLACK));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag("night");
        new Transition<Float>(night,
                night.renderer()::setOpaqueness,
                0F,
                0.5F,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
        gameObjectCollection.addGameObject(night, nightLayer);
        return night;
    }
}
