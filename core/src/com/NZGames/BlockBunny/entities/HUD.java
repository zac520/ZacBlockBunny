package com.NZGames.BlockBunny.entities;

import com.NZGames.BlockBunny.BlockBunnyGame;
import com.NZGames.BlockBunny.handlers.Box2DVars;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by zac520 on 7/17/14.
 */
public class HUD {

    private Player player;
    private BitmapFont font;
    private TextureRegion[] blocks;

    public HUD(Player player){
        this.player = player;
        Texture tex = new Texture( Gdx.files.internal("assets/images/hud.png"));

        blocks = new TextureRegion[3];
        for (int i = 0; i< blocks.length; i++){
            //this was done manually. TexturePacker will be used in the future
            blocks[i] = new TextureRegion (tex, 32 + i * 16, 0, 16, 16);
        }

        //set up font
        font = new BitmapFont();

    }

    public void render(SpriteBatch sb){
        short bits = player.getBody().getFixtureList().first().getFilterData().maskBits;
        sb.begin();

        //show the color of the box
        if((bits & Box2DVars.BIT_RED) != 0){
            sb.draw(blocks[0], 40, BlockBunnyGame.SCREEN_HEIGHT - 40);
        }
        if((bits & Box2DVars.BIT_GREEN) != 0){
            sb.draw(blocks[1], 40, BlockBunnyGame.SCREEN_HEIGHT - 40);
        }
        if((bits & Box2DVars.BIT_BLUE) != 0){
            sb.draw(blocks[2], 40, BlockBunnyGame.SCREEN_HEIGHT - 40);
        }

        //draw the total crystals collected
        font.draw(sb, String.valueOf(player.crystalCount), 80, BlockBunnyGame.SCREEN_HEIGHT-32);
        sb.end();

    }

}
