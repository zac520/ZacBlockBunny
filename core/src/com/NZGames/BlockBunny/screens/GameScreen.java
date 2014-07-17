package com.NZGames.BlockBunny.screens;
import com.NZGames.BlockBunny.BlockBunnyGame;
import com.NZGames.BlockBunny.handlers.Box2DVars;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
/**
 * Created by zac520 on 7/17/14.
 */
public class GameScreen implements Screen {

    BlockBunnyGame game;
    private boolean debug = true;
    TextureRegion myGolbez;

    Stage stage;
    private SpriteBatch batch;
    private World world;

    TextureAtlas atlas;

    OrthographicCamera camera;
    OrthographicCamera box2DCam;

    Box2DDebugRenderer box2DRenderer;



    //constructor
    public GameScreen(BlockBunnyGame myGame){
        game = myGame;

        //create the world
        //x and y forces, then inactive bodies should "sleep" (true)
        world = new World(new Vector2(0, -9.81f), true);

        //create a platform
        createPlatform();

        //set up box2d renderer
        box2DRenderer = new Box2DDebugRenderer();
        
        //set up box2dcam
        box2DCam = new OrthographicCamera();
        box2DCam.setToOrtho(false, BlockBunnyGame.SCREEN_WIDTH / Box2DVars.PPM, BlockBunnyGame.SCREEN_HEIGHT / Box2DVars.PPM);
    }

    @Override
    public void render(float delta) {
        // clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //draw box2d world
        if(debug) {
            box2DRenderer.render(world, box2DCam.combined);
        }
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

    }

    public void createPlatform(){
        //create platform
        //to create a body we do 5 steps:
        //1.create world
        //  2. Define Body
        //  3. Create body
        //     4. Define Fixture
        //     5. Create Fixture
        //static body does not move, unaffected by forces
        //kinematic bodies: not affected by world forces, but can change velocities (example: moving platform)
        //dynamic bodies do get affected by forces (example: sprite)


        //define platform body
        BodyDef bdef = new BodyDef();
        bdef.position.set(160 / Box2DVars.PPM, 120 / Box2DVars.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;

        //create body
        Body body = world.createBody(bdef);

        //define Fixture
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(50 / Box2DVars.PPM, 5 / Box2DVars.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        //fdef.filter.categoryBits = B2DVars.BIT_GROUND; //what it is
        //fdef.filter.maskBits = Box2DVars.BIT_PLAYER;//what it can collide with (bitwise operators)

        //create fixture and make a tag setUserData
        body.createFixture(fdef).setUserData("ground");//a tag to identify this later


    }
}
