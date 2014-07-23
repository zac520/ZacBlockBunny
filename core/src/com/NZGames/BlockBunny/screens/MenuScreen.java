package com.NZGames.BlockBunny.screens;
import com.NZGames.BlockBunny.BlockBunnyGame;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by zac520 on 7/17/14.
 */
public class MenuScreen implements Screen {

    BitmapFont font;
    BlockBunnyGame game;
    OrthographicCamera camera;
    Stage stage;
    Skin skin;
    SpriteBatch batch;
    private static final int SCREEN_WIDTH = Gdx.graphics.getWidth();
    private static final int SCREEN_HEIGHT = Gdx.graphics.getHeight();

    //constructor
    public MenuScreen(BlockBunnyGame pGame){


        //need a batch set to write the score to screen
        batch = new SpriteBatch();

        //do the normal menu stuff
        game = pGame;
        makeMenu();
    }

    @Override
    public void render(float delta) {
        // clear the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }
    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose(){
        stage.dispose();
    }
    public void makeMenu(){
        // create viewport
        camera=new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        stage=new Stage();
        stage.getViewport().setCamera(camera);


        skin = new Skin( Gdx.files.internal( "assets/ui/defaultskin.json" ));

        //make table for all the buttons
        Table table=new Table();
        table.setSize(800,480);

        //add start game button
        TextButton startGame=new TextButton("Easy Level",skin);
        startGame.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {

                GameScreen gameScreen = new GameScreen(game, "assets/maps/easyLevel.tmx");
                game.setScreen(gameScreen);
            }
        });
        table.add(startGame).width(200).height(50);
        table.row();

        //add start game button
        TextButton startGame2=new TextButton("Hard Level",skin);
        startGame2.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, "assets/maps/test3.tmx"));
            }
        });
        table.add(startGame2).width(200).height(50);
        table.row();


        //add quit button
        TextButton quit=new TextButton("quit",skin);
        quit.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stage.addAction(Actions.fadeOut(0.7f));
            }
        });
        table.add(quit).width(200).padTop(10);

        //add table to the stage
        stage.addActor(table);

        //create font
        font = new BitmapFont(Gdx.files.internal("assets/ui/test.fnt"), false);
        font.scale(0.1f);


        // give input to the stage
        Gdx.input.setInputProcessor(stage);
    }
}