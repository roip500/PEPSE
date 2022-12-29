package pepse.world.daynight;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class MoonHalo {

    private static final int HALO_SIZE = 100;

    public static GameObject create(GameObjectCollection gameObjectCollection,
                                    int moonHaloLayer,
                                    GameObject moon,
                                    Color color) {

        GameObject moonHalo = new GameObject(Vector2.ZERO, new Vector2(HALO_SIZE, HALO_SIZE),
                new OvalRenderable(color));
        moon.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        moonHalo.addComponent(deltaTime -> moonHalo.setCenter(moon.getCenter()));
        moonHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjectCollection.addGameObject(moonHalo, moonHaloLayer);
        return moonHalo;
    }
}
