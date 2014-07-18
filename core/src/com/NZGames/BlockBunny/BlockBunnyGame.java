package com.NZGames.BlockBunny;

import com.NZGames.BlockBunny.screens.GameScreen;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BlockBunnyGame extends Game {
    public static int SCREEN_WIDTH = 320;
    public static int SCREEN_HEIGHT = 240;
	@Override
	public void create () {
        //screen width and height must be set inside method. Does not work above.
        SCREEN_WIDTH = Gdx.graphics.getWidth()/3;
        SCREEN_HEIGHT = Gdx.graphics.getHeight()/3;
        setScreen(new GameScreen(this));
    }

}
