package com.NZGames.BlockBunny.handlers;

/**
 * Created by zac520 on 7/10/14.
 */
public class MyInput {
    public static boolean [] keys;
    public static boolean [] pkeys;

    public static final int NUM_KEYS = 4;
    public static final int BUTTON1 = 0;
    public static final int BUTTON2 = 1;
    public static final int BUTTON3 = 2;
    public static final int BUTTON4 = 3;

    static{
        keys = new boolean [NUM_KEYS];
        pkeys = new boolean [NUM_KEYS];
    }

    public static void update (){
        for (int i = 0; i < NUM_KEYS; i++){
            pkeys[i] = keys[i];
        }
    }

    public static void setKey(int i, boolean b){
        keys[i] = b;
    }

    public static boolean isDown(int i){
        return keys[i];
    }

    public static boolean isPressed(int i){
        return keys[i] && !pkeys[i];
    }
}
