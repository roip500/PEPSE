package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject {

    //images for avatar:
    private static final String STANDING_IMAGE_LOCATION = "assets/standing.png";
    private static final String FLYING_UP_IMAGE_LOCATION = "assets/flying-up.png";
    private static final String FLYING_SIDES_IMAGE_LOCATION = "assets/flying-sides.png";
    private static final String RUN_LEFT_IMAGE_LOCATION = "assets/running-left.png";
    private static final String RUN_RIGHT_IMAGE_LOCATION = "assets/running-right.png";
    private static final String SEMI_RUN_LEFT_IMAGE_LOCATION = "assets/semi-running-left.png";
    private static final String SEMI_RUN_RIGHT_IMAGE_LOCATION = "assets/semi-running-right.png";

    // global consts:
    private static final int USING_RIGHT = 3;
    private static final int USING_SEMI_RIGHT = 2;
    private static final int USING_LEFT = 1;
    private static final int USING_SEMI_LEFT = 0;
    private static final int JUMP_SPEED = 300;
    private static final int FLY_SPEED = 300;
    private static final int FALLING_MAX_SPEED = 400;
    private static final int GRAVITY_EFFECT= 10;
    private static final int WALKING_SPEED = 300;
    private static final int AVATAR_SIZE = 60;

    // parameters to be used:
    private final int maxEnergy;
    private final Counter energyCounter;
    private final Counter scoreCounter;
    private final Renderable standing;
    private final Renderable flyingUp;
    private final Renderable flyingSides;
    private final Renderable runLeft;
    private final Renderable runRight;
    private final Renderable semiRunLeft;
    private final Renderable semiRunRight;
    private int WHICH_LEG_TO_USE;
    private final UserInputListener inputListener;
    Vector2 yMovementDir;
    private boolean inTheAir;
    private boolean flying;


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
     * @param energyCounter counter object that represents the energy the avatar has
     * @param scoreCounter counter object that represents the score the avatar has
     */
    private Avatar(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                  UserInputListener inputListener, Renderable flyingUp, Renderable flyingSides,
                  Renderable runLeft, Renderable runRight, danogl.util.Counter energyCounter,
                   danogl.util.Counter scoreCounter, Renderable semiRunLeft, Renderable semiRunRight) {
        super(topLeftCorner, dimensions, renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        maxEnergy = energyCounter.value();
        this.inputListener = inputListener;
        this.inTheAir = false;
        this.flying = false;
        this.yMovementDir = Vector2.ZERO;
        this.standing = renderable;
        this.flyingUp = flyingUp;
        this.flyingSides = flyingSides;
        this.runLeft = runLeft;
        this.runRight = runRight;
        this.semiRunLeft = semiRunLeft;
        this.semiRunRight = semiRunRight;
        this.WHICH_LEG_TO_USE = USING_SEMI_LEFT;
        this.energyCounter = energyCounter;
        this.scoreCounter = scoreCounter;
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
                inputListener.isKeyPressed(KeyEvent.VK_SHIFT) && energyCounter.value() > 0) {
            flying = true;
            energyCounter.decrement();
            yMovementDir = Vector2.UP;
            transform().setVelocityY(yMovementDir.y() * FLY_SPEED);
        }
        else if(!inTheAir && inputListener.isKeyPressed(KeyEvent.VK_SPACE)){
            yMovementDir = Vector2.UP;
            inTheAir = true;
            transform().setVelocityY(yMovementDir.y()*JUMP_SPEED);
        }
        else{
            if(!inTheAir && energyCounter.value() < maxEnergy){
                energyCounter.increment();
            }
            if(getVelocity().y() == 0){
                yMovementDir = Vector2.DOWN;
            }
            if(transform().getVelocity().y() < FALLING_MAX_SPEED){
                transform().setVelocityY(getVelocity().y() + GRAVITY_EFFECT);
            }
        }
    }

    /**
     * sets the image of the avatar
     */
    private void setImage(){
        if(getVelocity().x() == 0){
            if(flying){
                renderer().setRenderable(flyingUp);
            }
            else{
                renderer().setRenderable(standing);
            }
            return;
        }
        if(flying){
            renderer().setRenderable(flyingSides);
        }
        else if(this.WHICH_LEG_TO_USE == USING_SEMI_LEFT){
            renderer().setRenderable(semiRunLeft);
        }
        else if(this.WHICH_LEG_TO_USE == USING_LEFT){
            renderer().setRenderable(runLeft);
        }
        else if(this.WHICH_LEG_TO_USE == USING_SEMI_RIGHT){
            renderer().setRenderable(semiRunRight);
        }
        else if(this.WHICH_LEG_TO_USE == USING_RIGHT){
            renderer().setRenderable(runRight);
        }
        this.WHICH_LEG_TO_USE++;
        if(this.WHICH_LEG_TO_USE > USING_RIGHT){
            this.WHICH_LEG_TO_USE = USING_SEMI_LEFT;
        }
        renderer().setIsFlippedHorizontally(!(getVelocity().x() > 0));
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
        inTheAir = false;
        flying = false;
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
                                ImageReader imageReader,
                                danogl.util.Counter energyCounter,
                                danogl.util.Counter scoreCounter){
        Renderable standingImg = imageReader.readImage(STANDING_IMAGE_LOCATION,true);
        Renderable flyingUpImg = imageReader.readImage(FLYING_UP_IMAGE_LOCATION,true);
        Renderable flyingSidesImg = imageReader.readImage(FLYING_SIDES_IMAGE_LOCATION,true);
        Renderable runLeftImg = imageReader.readImage(RUN_LEFT_IMAGE_LOCATION,true);
        Renderable runRightImg = imageReader.readImage(RUN_RIGHT_IMAGE_LOCATION,true);
        Renderable semiRunLeftImg = imageReader.readImage(SEMI_RUN_LEFT_IMAGE_LOCATION,true);
        Renderable semiRunRightImg = imageReader.readImage(SEMI_RUN_RIGHT_IMAGE_LOCATION,true);
        Avatar avatar = new Avatar(Vector2.ZERO, new Vector2(AVATAR_SIZE, AVATAR_SIZE),
                standingImg, inputListener, flyingUpImg, flyingSidesImg, runLeftImg,
                runRightImg, energyCounter, scoreCounter, semiRunLeftImg, semiRunRightImg);
        avatar.setCenter(topLeftCorner);
        gameObjects.addGameObject(avatar, layer);
        return avatar;
    }
}
