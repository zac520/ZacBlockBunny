package com.NZGames.BlockBunny.entities;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by zac520 on 7/17/14.
 */
public class Player {
    protected       Body body;
    public boolean  facingLeft = true;
    public boolean  isWalking = false;
    public static final float      HEIGHT = 40;
    public static final float      WIDTH = 30;
    public static final float       DAMP_EFFECT = 0.0f;
    float           stateTime = 0;
    int             crystalCount = 0;
    public static  int PLAYER_MAX_SPEED = 3;
    public static int FORWARD_FORCE = 8;

    public Player(Body body){
        this.body = body;

    }

    public void update(float delta) {
        stateTime += delta;
    }

    public Body getBody(){
        return body;
    }
    public boolean isFacingLeft(){
        return facingLeft;
    }
    public boolean getIsWalking(){
        return isWalking;
    }

    public float getStateTime(){
        return stateTime;
    }
    public void collectCrystal(){
        this.crystalCount++;
    }
}
