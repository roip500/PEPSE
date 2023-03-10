package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.util.Vector2;

public class Moon{

    private static final int SIZE = 97;
    private static final float STARTING_ANGLE = 0.5f;
    private static final float FINISHING_ANGLE = -1.5f;
    private static final String MOON_TAG = "moon";
    private static final String IMAGE_LOCATION = "assets/moon.png";


    /**
     * creates the moon object and adds it to gameObjectCollection
     * @param gameObjectCollection list of all the game objects.
     * @param windowDimension size of the window
     * @param sunLayer layer in the board
     * @return game object that represents the sun
     */
    public static GameObject create(GameObjectCollection gameObjectCollection,
                                    Vector2 windowDimension,
                                    int sunLayer,
                                    float cycleLength,
                                    ImageReader imageReader){
        GameObject moon = new GameObject(
                new Vector2(0, windowDimension.y()),
                new Vector2(SIZE, SIZE),
                imageReader.readImage(IMAGE_LOCATION,true));
        moon.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        moon.setTag(MOON_TAG);
        new Transition<>(moon,
                aFloat -> moon.setCenter(setMoonCenter(aFloat, windowDimension)),
                STARTING_ANGLE,
                FINISHING_ANGLE,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null);
        gameObjectCollection.addGameObject(moon, sunLayer);
        return moon;
    }

    /**
     * function calculates the center point for the moon object in the game
     * @param angle angle the moon is from the center
     * @param windowDimension dimension of the screen
     * @return Vector2 object
     */
    private static Vector2 setMoonCenter(float angle, Vector2 windowDimension){
        Vector2 centerPoint = new Vector2(windowDimension.x()/2, windowDimension.y());
        float cos = (float) (Math.cos(angle * Math.PI));
        float sin = (float) (Math.sin(angle * Math.PI));
        return new Vector2(centerPoint.x() - centerPoint.x() * cos , centerPoint.y() + centerPoint.y() * sin);
    }
}


