package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sun{

    /**
     * creates the sun object and adds it to gameObjectCollection
     * @param gameObjectCollection list of all the game objects.
     * @param windowDimension size of the window
     * @param sunLayer layer in the board
     * @return game object that represents the sun
     */
    public static GameObject create(GameObjectCollection gameObjectCollection,
                                    Vector2 windowDimension,
                                    int sunLayer,
                                    float cycleLength){
        GameObject sun = new GameObject(
                new Vector2(0, windowDimension.y() * 2/3),
                new Vector2(100, 100),
                new OvalRenderable(Color.YELLOW));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag("sun");
        new Transition<Float>(sun,
                aFloat -> sun.setCenter(setSunCenter(aFloat, windowDimension)),
                1F,
                0F,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
        gameObjectCollection.addGameObject(sun, sunLayer);
        return sun;
    }

    /**
     * function calcultes the center point for the sun object in the game
     * @param angle angle the sun is from the center
     * @param windowDimension dimension of the screen
     * @return Vector2 object
     */
    private static Vector2 setSunCenter(float angle,Vector2 windowDimension){
        Vector2 centerPoint = new Vector2(windowDimension.x()/2, windowDimension.y() * 3/4);
        float cos = (float) (Math.cos(angle * Math.PI));
        float sin = (float) (Math.sin(angle * Math.PI));
        return new Vector2(centerPoint.x() + centerPoint.x() * cos , centerPoint.y() - centerPoint.y() * sin);
    }
}


