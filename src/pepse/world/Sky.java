package pepse.world;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sky {

    private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");
    private static final String SKY = "sky";

    /**
     * creates the sky object and adds it to gameObjectCollection
     * @param gameObjectCollection list of all the game objects.
     * @param windowDimension size of the window
     * @param skyLayer layer in the board
     * @return game object that represents the sky
     */
    public static GameObject create(GameObjectCollection gameObjectCollection,
                                    Vector2 windowDimension,
                                    int skyLayer){
        GameObject sky = new GameObject(Vector2.ZERO, windowDimension,
                new RectangleRenderable(BASIC_SKY_COLOR));
        sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sky.setTag(SKY);
        gameObjectCollection.addGameObject(sky, skyLayer);
        return sky;
    }

}
