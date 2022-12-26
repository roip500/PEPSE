package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject{

    private static final int JUMP_SPEED = 300;
    private static final int FLY_SPEED = 200;
    private static final float HIGHEST_ENERGY = 100F;
    private static final int GRAVITY_EFFECT= 2;
    private static final int WALKING_SPEED = 300;
    private static final int AVATAR_SIZE = 40;
    private static final float ENERGY_CHANGE = 0.5F;
    private final UserInputListener inputListener;
    Vector2 yMovementDir;
    private boolean inTheAir;
    private float energy;
    private final ImageReader imageReader;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, dimensions, renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.inputListener = inputListener;
        this.imageReader = imageReader;
        this.energy = HIGHEST_ENERGY;
        this.inTheAir = false;
        this.yMovementDir = Vector2.ZERO;
    }

    /**
     * function moves the avatar according to the keyboard
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
        Vector2 movementDir = Vector2.ZERO;
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            movementDir = movementDir.add(Vector2.LEFT);
        }
        if(inputListener. isKeyPressed(KeyEvent.VK_RIGHT) ) {
            movementDir = movementDir.add(Vector2.RIGHT);
        }
        transform().setVelocityX(movementDir.mult(WALKING_SPEED).x());
        if(inTheAir){
            if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
                    inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && energy > 0){
                energy -= ENERGY_CHANGE;
                yMovementDir = new Vector2(0, -1);
                transform().setVelocityY(yMovementDir.y()*FLY_SPEED);
            }
            else{
                if(getVelocity().y() == 0){
                    yMovementDir = new Vector2(0, 1);
                }
                transform().setVelocityY(getVelocity().y() + GRAVITY_EFFECT);
            }
        }
        if(inputListener.isKeyPressed(KeyEvent.VK_SPACE) && !inTheAir){
            yMovementDir = new Vector2(0, -1);
            inTheAir = true;
            transform().setVelocityY(yMovementDir.y()*JUMP_SPEED);
        }
        if(getVelocity().y() == 0 && energy < HIGHEST_ENERGY){
            energy += ENERGY_CHANGE;
        }
    }

    /**
     *
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        //TODO: if the other object is bellow our object only then stop the y velocity
        yMovementDir = Vector2.ZERO;
        transform().setVelocityY(0);
        inTheAir = false;
    }

    /**
     * creates the avatar and returns it after adding it to the game
     * @param gameObjects list of all the gameObjects in the game
     * @param layer integer represents the layer of the gameObject is the game
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
                            Note that (0,0) is the top-left corner of the window.
     * @param inputListener actions done by the keyboard
     * @param imageReader image renderer in the game
     * @return Avatar object
     */
    public static Avatar create(GameObjectCollection gameObjects,
                                int layer, Vector2 topLeftCorner,
                                UserInputListener inputListener,
                                ImageReader imageReader){
        Renderable avatarImage = imageReader.readImage("assets/ball.png",true);
        Avatar avatar = new Avatar(Vector2.ZERO, new Vector2(AVATAR_SIZE, AVATAR_SIZE),
                avatarImage, inputListener, imageReader);
        avatar.setCenter(topLeftCorner);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }
}
