package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject {

    private final Renderable STANDING;
    private final Renderable FLYING_UP;
    private final Renderable FLYING_SIDES;
    private final Renderable RUN_LEFT;
    private final Renderable RUN_RIGHT;
    private static final int USING_RIGHT = 1;
    private static final int USING_LEFT = 0;
    private int WHICH_LEG_TO_USE;

    //TODO: fix the names of the arguments

    private static final int JUMP_SPEED = 300;
    private static final int FLY_SPEED = 300;
    private static final float HIGHEST_ENERGY = 100F;
    private static final int GRAVITY_EFFECT= 10;
    private static final int WALKING_SPEED = 300;
    private static final int AVATAR_SIZE = 60;
    private static final float ENERGY_CHANGE = 0.5F;
    private final UserInputListener inputListener;
    Vector2 yMovementDir;
    private boolean inTheAir;
    private float energy;

    /**
     * Constructs the avatar.
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions Width and height in window coordinates.
     * @param renderable the starting image for the avatar
     * @param inputListener allows the gameObject to get input from the keyboard
     * @param flyingUp the image that will be used when the avatar flies
     * @param flyingSides the image that will be used when the avatar flies and moves side-ways
     * @param runLeft the image that will be used when the avatar walks - 1
     * @param runRight the image that will be used when the avatar walks - 2
     */
    public Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, Renderable flyingUp, Renderable flyingSides,
                  Renderable runLeft, Renderable runRight) {
        super(topLeftCorner, dimensions, renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        this.inputListener = inputListener;
        this.energy = HIGHEST_ENERGY;
        this.inTheAir = false;
        this.yMovementDir = Vector2.ZERO;
        this.STANDING = renderable;
        this.FLYING_UP = flyingUp;
        this.FLYING_SIDES = flyingSides;
        this.RUN_LEFT= runLeft;
        this.RUN_RIGHT= runRight;
        this.WHICH_LEG_TO_USE = 0;
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
        setVelocityX();
        setVelocityY();
        setImage();
    }

    /**
     * sets the velocity of the avatar in the x vector
     */
    private void setVelocityX(){
        Vector2 movementDir = Vector2.ZERO;
        if(inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            movementDir = movementDir.add(Vector2.LEFT);
        }
        if(inputListener. isKeyPressed(KeyEvent.VK_RIGHT) ) {
            movementDir = movementDir.add(Vector2.RIGHT);
        }
        transform().setVelocityX(movementDir.mult(WALKING_SPEED).x());
    }

    /**
     * sets the velocity of the avatar in the y vector
     */
    private void setVelocityY(){
        if(inTheAir && inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
                inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && energy > 0) {
            energy -= ENERGY_CHANGE;
            yMovementDir = new Vector2(0, -1);
            transform().setVelocityY(yMovementDir.y() * FLY_SPEED);
        }
        else if(!inTheAir && inputListener.isKeyPressed(KeyEvent.VK_SPACE)){
            yMovementDir = new Vector2(0, -1);
            inTheAir = true;
            transform().setVelocityY(yMovementDir.y()*JUMP_SPEED);
        }
        else if(!inTheAir && energy < HIGHEST_ENERGY){
            energy += ENERGY_CHANGE;
        }
        else{
            if(getVelocity().y() == 0){
                yMovementDir = new Vector2(0, 1);
            }
            transform().setVelocityY(getVelocity().y() + GRAVITY_EFFECT);
        }
    }

    /**
     * sets the image of the avatar
     */
    private void setImage(){
        if(getVelocity().x() > 0){
            if(inTheAir){
                renderer().setRenderable(FLYING_SIDES);
            }
            else if(this.WHICH_LEG_TO_USE == USING_LEFT){
                this.WHICH_LEG_TO_USE = USING_RIGHT;
                renderer().setRenderable(RUN_RIGHT);
            }
            else{
                this.WHICH_LEG_TO_USE = USING_LEFT;
                renderer().setRenderable(RUN_LEFT);
            }
            renderer().setIsFlippedHorizontally(false);
        }
        else if(getVelocity().x() < 0){
            if(inTheAir){
                renderer().setRenderable(FLYING_SIDES);
            }
            else if(this.WHICH_LEG_TO_USE == USING_LEFT){
                this.WHICH_LEG_TO_USE = USING_RIGHT;
                renderer().setRenderable(RUN_RIGHT);
            }
            else{
                this.WHICH_LEG_TO_USE = USING_LEFT;
                renderer().setRenderable(RUN_LEFT);
            }
            renderer().setIsFlippedHorizontally(true);
        }
        else{
            if(inTheAir){
                renderer().setRenderable(FLYING_UP);
            }
            else{
                renderer().setRenderable(STANDING);
            }
        }
    }

    /**
     * sets the avatars y-velocity to 0 if the avatar collided with an object and turns off the inTheAir flag
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
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
        Renderable standingImg = imageReader.readImage("assets/standing.png",true);
        Renderable flyingUpImg = imageReader.readImage("assets/flying-up.png",true);
        Renderable flyingSidesImg = imageReader.readImage("assets/flying-sides.png",true);
        Renderable runLeftImg = imageReader.readImage("assets/running-left.png",true);
        Renderable runRightImg = imageReader.readImage("assets/running-right.png",true);
        Avatar avatar = new Avatar(Vector2.ZERO, new Vector2(AVATAR_SIZE, AVATAR_SIZE),
                standingImg, inputListener, flyingUpImg, flyingSidesImg, runLeftImg, runRightImg);
        avatar.setCenter(topLeftCorner);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }
}
