package com.NZGames.BlockBunny.handlers;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

/**
 * Created by zac520 on 7/10/14.
 */
public class MyContactListener implements ContactListener {

    private boolean playerOnGround;
    private int numFootContacts =0;
    private Array<Body> bodiesToRemove;

    public MyContactListener(){
        super();
        bodiesToRemove = new Array<Body>();
    }
    //called when two fixtures begin to collide
    public void beginContact (Contact c){
        //System.out.println("Begin Contact");
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        //System.out.println("contact between " + fa.getUserData() + " and " + fb.getUserData());

        //if "foot" is on ground, set playerOnGround to true
        if(fa.getUserData() != null && fa.getUserData().equals("foot")){
            //System.out.println("fa is foot");
            playerOnGround = true;
            numFootContacts ++;


        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")){
            //System.out.println("fb is foot");
            playerOnGround = true;
            numFootContacts ++;

        }
        if(fa.getUserData() != null && fa.getUserData().equals("crystal")){
            //remove crystal
            //since world is updating, we are going to queue the crystals
            //and remove them after the update for each step
            if(!bodiesToRemove.contains(fa.getBody(),true)) {
                bodiesToRemove.add(fa.getBody());
            }

        }
        if(fb.getUserData() != null && fb.getUserData().equals("crystal")){
            //remove crystal
            //since world is updating, we are going to queue the crystals
            //and remove them after the update for each step
            if(!bodiesToRemove.contains(fb.getBody(),true)) {
                bodiesToRemove.add(fb.getBody());
            }

        }
    }

    //called when two fixtures no longer collide
    public void endContact (Contact c) {
        //System.out.println("End Contact");
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        if(fa.getUserData() != null && fa.getUserData().equals("foot")){
            //System.out.println("fa is foot");
            playerOnGround = false;
            numFootContacts --;

        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")){
            //System.out.println("fb is foot");
            playerOnGround = false;
            numFootContacts --;
        }


    }

    //collision detection
    //collision handling
    public void preSolve (Contact c, Manifold m) {}

    //whatever happens after
    public void postSolve (Contact c, ContactImpulse ci) {}

    public boolean isPlayerOnGround(){
        //if there is at least one, then player is on ground. Not sure why that is better than the bool method
        return numFootContacts >0;
    }
    public Array getBodiesToRemove(){
        return bodiesToRemove;
    }
}
