package com.NZGames.BlockBunny.entities;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by zac520 on 7/17/14.
 */
public class Player {
    protected  Body body;

    public Player(Body body){
        this.body = body;
    }
    public Body getBody(){
        return body;
    }

}
