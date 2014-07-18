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
    float           stateTime = 0;
    int             crystalCount = 0;


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
