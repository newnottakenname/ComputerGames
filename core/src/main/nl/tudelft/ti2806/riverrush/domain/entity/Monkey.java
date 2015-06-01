package nl.tudelft.ti2806.riverrush.domain.entity;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AlphaAction;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.actions.RotateByAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.google.inject.Inject;
import nl.tudelft.ti2806.riverrush.domain.entity.state.AnimalOnBoat;
import nl.tudelft.ti2806.riverrush.domain.event.EventDispatcher;

import java.util.Timer;
import java.util.TimerTask;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

/**
 * Game object representing a monkey.
 */
public class Monkey extends AbstractAnimal {

    private static final float JUMP_HEIGHT = 100;
    private static final int END_REGIONX = 432;
    private static final int END_REGIONY = 432;
    private static final int FALL_DISTANCEX = 200;
    private static final int FALL_DISTANCEY = -520;
    private static final float FALL_VELOCITY = 0.5f;
    private static final float JUMP_UP_DURATION = 0.3f;
    private static final float JUMP_DOWN_DURATION = 0.15f;
    private static final float DELAY_DURATION = 5f;
    private static final float WIGGLE_DURATION = 0.5f;
    private static final float WIGGLE_BACK_DURATION = 0.125f;
    private static final float WIGGLE_RIGHT_DURATION = 0.25f;
    private static final float WIGGLE_LEFT_DURATION = 0.125f;
    private static final float WIGGLE_DISTANCE = 5f;
    private static final int RESPAWN_DELAY = 2000;

    /**
     * Number of milliseconds in a second.
     */
    public static final int SECOND = 1000;

    private AssetManager manager;
    private float origX;
    private float origY;

    /**
     * Creates a monkey object that represents player characters.
     *
     * @param assetManager enables the object to retrieve its assets
     * @param xpos         represents the position of the monkey on the x axis
     * @param ypos         represents the position of the monkey on the y axis
     * @param width        represents the width of the monkey object
     * @param height       represents the height of the monkey object
     * @param dispatcher   Event dispatcher for dispatching events
     */
    @Inject
    public Monkey(
        final AssetManager assetManager,
        final float xpos,
        final float ypos,
        final float width,
        final float height,
        final EventDispatcher dispatcher
    ) {
        this.manager = assetManager;
        this.setX(xpos);
        this.setY(ypos);
        this.setWidth(width);
        this.setHeight(height);

        this.origX = xpos;
        this.origY = ypos;
        this.setState(new AnimalOnBoat(this, dispatcher));
    }

    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        Texture tex = this.manager.get("data/raccoon.png", Texture.class);
        TextureRegion region = new TextureRegion(tex, 0, 0, END_REGIONX, END_REGIONY);

        Color color = this.getColor();
        batch.setColor(color.r, color.g, color.b, color.a * parentAlpha);

        batch.enableBlending();
        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        batch.draw(region, this.getX(), this.getY(), this.getOriginX(), this.getOriginY(),
            this.getWidth(), this.getHeight(), this.getScaleX(), this.getScaleY(), this.getRotation());

        batch.setColor(Color.WHITE);

        batch.disableBlending();
    }

    @Override
    public void act(final float delta) {
        super.act(delta);

    }

    /**
     * Changes the state to that having been collided.
     */
    public void collide() {
        this.setState(this.getState().collide());
    }

    /**
     * Changes the state to that having jumped.
     */
    public void jump() {
        this.setState(this.getState().jump());
    }

    /**
     * Changes the state to that having returned to the boat.
     */
    public void returnToBoat() {
        this.setState(this.getState().returnToBoat());
    }

    /**
     * Respawn the monkey.
     */
    public void respawn() {
        Timer tmr = new Timer();
        tmr.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Monkey.this.returnToBoat();
                tmr.cancel();
            }
        }, RESPAWN_DELAY, RESPAWN_DELAY);
    }

    /**
     * Creates an action that represents getting hit graphically (falling off the boat).
     *
     * @return an action that can be added to the actor
     */
    public Action collideAction() {
        MoveToAction fall = new MoveToAction();
        fall.setPosition(this.getX() + FALL_DISTANCEX, this.getY() + FALL_DISTANCEY);
        fall.setDuration(FALL_VELOCITY);

        // AlphaAction fade = Actions.fadeOut(FALL_VELOCITY);
        AlphaAction fade = new AlphaAction();
        fade.setAlpha(0f);
        fade.setDuration(FALL_VELOCITY);

        return Actions.parallel(fade, fall);
    }

    /**
     * Creates an action that represents returning to the boat graphically.
     *
     * @return an action that can be added to the actor
     */
    public Action returnAction() {
        MoveToAction ret = new MoveToAction();
        ret.setPosition(this.origX, this.origY);

        AlphaAction fade = new AlphaAction();
        fade.setAlpha(1f);
        fade.setDuration(0f);

        return Actions.parallel(fade, ret);
    }

    /**
     * Create action to move back on the boat.
     *
     * @return Action to return
     */
    public Action returnMove() {
        MoveToAction ret = new MoveToAction();
        ret.setPosition(this.origX, this.origY);
        return ret;
    }

    /**
     * Create action to fade.
     *
     * @return Action to fade
     */
    public Action returnFade() {
        AlphaAction fade = new AlphaAction();
        fade.setAlpha(1f);
        fade.setDuration(0f);
        return fade;
    }

    @Override
    public Action jumpAction() {
        MoveToAction jumpUp = new MoveToAction();
        jumpUp.setPosition(this.getX(), this.getY() + JUMP_HEIGHT);
        jumpUp.setDuration(JUMP_UP_DURATION);

        MoveToAction drop = new MoveToAction();
        drop.setPosition(this.getX(), this.origY);
        drop.setDuration(JUMP_DOWN_DURATION);

        this.setOrigin((this.getWidth() / 2), (this.getHeight() / 2));

        RotateByAction wiggleLeft = Actions.rotateBy(WIGGLE_DISTANCE);
        wiggleLeft.setDuration(WIGGLE_LEFT_DURATION);

        RotateByAction wiggleRight = Actions.rotateBy(-(WIGGLE_DISTANCE * 2));
        wiggleRight.setDuration(WIGGLE_RIGHT_DURATION);

        RotateByAction wiggleBack = Actions.rotateBy(WIGGLE_DISTANCE);
        wiggleBack.setDuration(WIGGLE_BACK_DURATION);

        SequenceAction wiggle = sequence(wiggleLeft, wiggleRight, wiggleBack);

        SequenceAction jump = sequence(jumpUp,
            Actions.repeat((int) (DELAY_DURATION / WIGGLE_DURATION), wiggle), drop);

        int time = (int) ((JUMP_DOWN_DURATION + JUMP_UP_DURATION + DELAY_DURATION) * SECOND);
        Timer tmr = new Timer();
        tmr.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Monkey.this.setState(Monkey.this.getState().drop());
                tmr.cancel();
            }
        }, time, time);
        return jump;
    }
}
