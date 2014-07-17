package com.NZGames.BlockBunny.screens;
import com.NZGames.BlockBunny.BlockBunnyGame;
import com.NZGames.BlockBunny.entities.Player;
import com.NZGames.BlockBunny.handlers.Box2DVars;
import com.NZGames.BlockBunny.handlers.MyContactListener;
import com.NZGames.BlockBunny.handlers.MyInput;
import com.NZGames.BlockBunny.handlers.MyInputProcessor;
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
    Player player;
    TextureAtlas atlas;

    OrthographicCamera camera;
    OrthographicCamera box2DCam;

    Box2DDebugRenderer box2DRenderer;
    private MyContactListener cl;
    private float accelx=0;


    //constructor
    public GameScreen(BlockBunnyGame myGame){
        game = myGame;

        //create the world
        //x and y forces, then inactive bodies should "sleep" (true)
        world = new World(new Vector2(0, -9.81f), true);

        //set the contact listener
        cl = new MyContactListener();
        world.setContactListener(cl);

        //set the input processor
        Gdx.input.setInputProcessor(new MyInputProcessor());

        //create a platform
        createPlatform();

        //create player
        createPlayer();

        //set up box2d renderer
        box2DRenderer = new Box2DDebugRenderer();

        //set up box2dcam
        box2DCam = new OrthographicCamera();
        box2DCam.setToOrtho(false, BlockBunnyGame.SCREEN_WIDTH / Box2DVars.PPM, BlockBunnyGame.SCREEN_HEIGHT / Box2DVars.PPM);
    }

    public void update(float delta) {
        //(step, accuracy of collisions (6 or 8 steps recommended), accuracy
        //of setting bodies after collision (2 recommended))
        world.step(delta, 6, 2);
    }
    @Override
    public void render(float delta) {
        // clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //handle the input from user
        handleInput();

        //update the world
        update(delta);


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

    public void handleInput() {

        //handle accelerometer input
        accelx = Gdx.input.getAccelerometerY();
        player.getBody().applyForceToCenter(accelx*3,0,true);

        //playerJump
        if (MyInput.isPressed(MyInput.BUTTON1)) {
            //System.out.println("pressed Z");
            if (cl.isPlayerOnGround()) {
                //force is in newtons
                player.getBody().applyForceToCenter(0, 100, true);
            }
        }
        //switch block color
        if (MyInput.isPressed(MyInput.BUTTON2)) {
            //System.out.println("hold X");
            //switchBlocks();
        }

        if (MyInput.isDown(MyInput.BUTTON3)) {
            player.getBody().applyForceToCenter(10,0,true);
        }

        if (MyInput.isDown(MyInput.BUTTON4)) {
            player.getBody().applyForceToCenter(-10,0,true);
        }


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
        bdef.position.set(300 / Box2DVars.PPM, 120 / Box2DVars.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;

        //create body
        Body body = world.createBody(bdef);

        //define Fixture
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(250 / Box2DVars.PPM, 5 / Box2DVars.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.friction = 1;

        //fdef.filter.categoryBits = B2DVars.BIT_GROUND; //what it is
        //fdef.filter.maskBits = Box2DVars.BIT_PLAYER;//what it can collide with (bitwise operators)

        //create fixture and make a tag setUserData
        body.createFixture(fdef).setUserData("ground");//a tag to identify this later


    }
    public void createPlayer() {
//create player
        //define platform body
        BodyDef bdef = new BodyDef();
        bdef.position.set(75 / Box2DVars.PPM, 200 / Box2DVars.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.linearVelocity.set(0,0);
        //create body
        Body body = world.createBody(bdef);

        //define Fixture
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(13 / Box2DVars.PPM, 13 / Box2DVars.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        //fdef.restitution = 0.7f;//1= perfectly bouncy 0 = not at all bouncy
        //fdef.filter.categoryBits = Box2DVars.BIT_PLAYER;
        //fdef.filter.maskBits = B2DVars.BIT_BLUE| BIT_CRYSTAL;//what it can collide with (bitwise operators)

        //create fixture
        body.createFixture(fdef).setUserData("box");


        //create foot sensor
        shape.setAsBox(13 / Box2DVars.PPM, 13 / Box2DVars.PPM, new Vector2(0, -5 / Box2DVars.PPM), 0);//set the box down
        fdef.shape = shape;
        //fdef.filter.categoryBits = BIT_PLAYER;
        //fdef.filter.maskBits = BIT_BLUE;
        fdef.isSensor = true;//make the foot go through ground for easier contact determining
        body.createFixture(fdef).setUserData("foot");
        shape.dispose();


        //create player
        player = new Player(body);
        body.setUserData(player);//used to provide reference later

    }

}
