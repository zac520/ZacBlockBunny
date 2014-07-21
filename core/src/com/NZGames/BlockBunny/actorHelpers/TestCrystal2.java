package com.NZGames.BlockBunny.actorHelpers;

import com.NZGames.BlockBunny.BlockBunnyGame;
import com.NZGames.BlockBunny.handlers.Box2DVars;
import com.NZGames.BlockBunny.screens.GameScreen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;

/**
 * Created by zac520 on 7/21/14.
 */
public class TestCrystal2 extends Image {
    float CRYSTAL_FRAME_DURATION= 0.06f;
    Rectangle bounds;
    private float stateTime;
    private Animation spinAnimation;
    private Texture tex;
    private GameScreen gameScreen;
    TextureRegionDrawable myDrawable;
    protected Body body;
    World world;
    Array<Body> worldBodies;
    public TestCrystal2(Body myBody, GameScreen myGameScreen) {

        //set the extended Image class to be a new Texture region of the size of each frame
        super(new TextureRegion(new Texture(Gdx.files.internal("assets/images/crystal.png")),16,16));


        //set the bounds equ
        bounds=new Rectangle((int)getX(), (int)getY(), (int)getWidth(), (int)getHeight());

        //split that picture into pieces to use for animation
        tex = new Texture(Gdx.files.internal("assets/images/crystal.png"));

        TextureRegion[] sprites = TextureRegion.split(tex, 16, 16)[0];

        spinAnimation = new Animation(CRYSTAL_FRAME_DURATION,sprites);

        myDrawable = new TextureRegionDrawable(spinAnimation.getKeyFrame(this.getStateTime(), true));

        worldBodies = new Array<Body>();
        worldBodies.add(myBody);
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

    @Override
    public void act(float delta) {

        //allow the movement, etc that is set on creation elsewhere to run
        super.act(delta);

        //update the time for this class
        this.update(delta);

        //change the drawable to the current frame
        myDrawable.setRegion(spinAnimation.getKeyFrame(this.getStateTime(), true));
        this.setDrawable( myDrawable );


    }
}
