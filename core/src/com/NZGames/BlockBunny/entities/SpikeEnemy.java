package com.NZGames.BlockBunny.entities;

import com.NZGames.BlockBunny.handlers.Box2DVars;
import com.NZGames.BlockBunny.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by zac520 on 7/21/14.
 */
public class SpikeEnemy extends Image {
    float CRYSTAL_FRAME_DURATION= 0.06f;
    Rectangle bounds;
    private float stateTime;
    private Animation rightAnimation;
    private Animation leftAnimation;
    private Texture tex;
    private GameScreen gameScreen;
    TextureRegionDrawable myDrawable;
    protected Body body;
    private boolean facingRight = true;

    public SpikeEnemy(Body myBody, GameScreen myGameScreen, boolean standardMovement) {

        //set the extended Image class to be a new Texture region of the size of each frame
        super(new TextureRegion(new Texture(Gdx.files.internal("assets/images/spikes.png")),32,32));

        //set the box2d body and the world it lives in
        this.body = myBody;
        this.gameScreen = myGameScreen;

        //set the bounds equ
        bounds=new Rectangle((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());

        //grab the texture
        tex = new Texture(Gdx.files.internal("assets/images/spikes.png"));

        //split that picture into pieces to use for animation
        TextureRegion[] rightSprites = TextureRegion.split(tex, 32, 32)[0];

        //set the animation
        rightAnimation = new Animation(CRYSTAL_FRAME_DURATION,rightSprites);

        //set the current drawable to the animation
        myDrawable = new TextureRegionDrawable(rightAnimation.getKeyFrame(this.getStateTime(), true));

        //set up the action that spikes make
        if(standardMovement) {
            addMovement();
        }
    }

    private void addMovement(){
        this.addAction(
                forever(
                        sequence(
                                moveTo(body.getPosition().x  * Box2DVars.PPM + 50, body.getPosition().y * Box2DVars.PPM + 50, 1),
                                moveTo(body.getPosition().x  * Box2DVars.PPM + 100, body.getPosition().y * Box2DVars.PPM, 1),
                                moveTo(body.getPosition().x  * Box2DVars.PPM + 50, body.getPosition().y * Box2DVars.PPM - 50, 1),
                                moveTo(body.getPosition().x  * Box2DVars.PPM, body.getPosition().y * Box2DVars.PPM, 1)
                        )
                )
        );
    }
    public Rectangle getBounds(){
        return bounds;
    }

    public void update(float delta) {
        stateTime += delta;
    }
    public float getStateTime(){
        return stateTime;
    }
    public Body getBody(){
        return body;
    }
    @Override
    public void act(float delta) {

        //allow the movement, etc that is set on creation elsewhere to run
        super.act(delta);

        //update the time for this class
        this.update(delta);

        //change the drawable to the current frame
        myDrawable.setRegion(rightAnimation.getKeyFrame(this.getStateTime(), true));
        this.setDrawable(myDrawable);


        //update the box2dbody

        //this.body.getPosition().set(getX(), getY());
        //gameScreen.testCrystals.get(gameScreen.testCrystals.indexOf(body,true) ).applyForceToCenter(1f/gameScreen.testCrystals.size,9.81f,false);
        if(gameScreen.enemies.get(gameScreen.enemies.indexOf(body,true)).isAwake()) {
            gameScreen.enemies.get(gameScreen.enemies.indexOf(body, true)).setTransform(
                    (getX() + (getWidth()/2)) / Box2DVars.PPM,
                    (getY() + (getHeight() /2)) / Box2DVars.PPM,
                    0);
            //gameScreen.testCrystals.get(gameScreen.testCrystals.indexOf(body, true)).setAwake(false);
        }
    }
}
