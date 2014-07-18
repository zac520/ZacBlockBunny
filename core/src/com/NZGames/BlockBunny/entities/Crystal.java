package com.NZGames.BlockBunny.entities;

import com.NZGames.BlockBunny.BlockBunnyGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by zac520 on 7/17/14.
 */


public class Crystal {
    protected       Body body;
    float           stateTime = 0;
    float           CRYSTAL_FRAME_DURATION = 0.06f;
    public Animation crystalAnimation;

    public Crystal(Body body){

        this.body = body;

        Texture tex = new Texture(Gdx.files.internal("assets/images/crystal.png"));

        //split that picture into pieces to use for animation
        TextureRegion[] sprites = TextureRegion.split(tex, 16, 16)[0];

        crystalAnimation = new Animation(CRYSTAL_FRAME_DURATION,sprites);

    }

    public void update(float delta) {
        stateTime += delta;
    }

    public Body getBody(){
        return body;
    }

}
