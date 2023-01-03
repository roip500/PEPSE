package pepse.world;

import java.awt.Color;
import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;

public class GraphicCounter extends GameObject {

    private final Counter counter;
    private final String textToPrint;

    /**
     * Construct of NumericLifeCounter.
     *
     * @param counter       Counter object that.
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     */
    public GraphicCounter(danogl.util.Counter counter, danogl.util.Vector2 topLeftCorner,
                              danogl.util.Vector2 dimensions, String textToPrint) {
        super(topLeftCorner, dimensions, null);
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        this.counter = counter;
        this.textToPrint = textToPrint;
    }


    /**
     * when the livesCounter has changed then this function changes the Text accordingly
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
        TextRenderable txt = new TextRenderable(textToPrint + Integer.toString(counter.value()));
        txt.setColor(Color.black);
        this.renderer().setRenderable(txt);
    }

}
