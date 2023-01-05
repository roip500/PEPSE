package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sun{

    private static final int SIZE = 100;
    private static final String SUN_TAG = "sun";
    private static final float STARTING_ANGLE = 0.5f;
    private static final float FINISHING_ANGLE = -1.5f;

    /**
     * creates the sun object and adds it to gameObjectCollection
     * @param gameObjectCollection list of all the game objects.
     * @param windowDimension size of the window
     * @param sunLayer layer in the board
     * @return game object that represents the sun
     */
    public static GameObject create(GameObjectCollection gameObjectCollection,int sunLayer,
                                    Vector2 windowDimension,
                                    float cycleLength){
        GameObject sun = new GameObject(
                new Vector2(0, windowDimension.y()),
                new Vector2(SIZE, SIZE),
                new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);
        new Transition<>(sun,
                aFloat -> sun.setCenter(setSunCenter(aFloat, windowDimension)),
                STARTING_ANGLE,
                FINISHING_ANGLE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null);
        gameObjectCollection.addGameObject(sun, sunLayer);
        return sun;
    }

    /**
     * function calculates the center point for the sun object in the game
     * @param angle angle the sun is from the center
     * @param windowDimension dimension of the screen
     * @return Vector2 object
     */
    private static Vector2 setSunCenter(float angle,Vector2 windowDimension){
        Vector2 centerPoint = new Vector2(windowDimension.x()/2, windowDimension.y());
        float cos = (float) (Math.cos(angle * Math.PI));
        float sin = (float) (Math.sin(angle * Math.PI));
        return new Vector2(centerPoint.x() + centerPoint.x() * cos , centerPoint.y() - centerPoint.y() * sin);
    }
}


