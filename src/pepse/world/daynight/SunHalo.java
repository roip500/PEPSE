package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class SunHalo {

    private static final int HALO_SIZE = 300;

    public static GameObject create(GameObjectCollection gameObjectCollection,
                                    int sunHaloLayer,
                                    GameObject sun,
                                    Color color) {

        GameObject sunHalo = new GameObject(Vector2.ZERO, new Vector2(HALO_SIZE, HALO_SIZE),
                new OvalRenderable(color));
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.addComponent(deltaTime -> sunHalo.setCenter(sun.getCenter()));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjectCollection.addGameObject(sunHalo, sunHaloLayer);
        return sunHalo;
    }
}
