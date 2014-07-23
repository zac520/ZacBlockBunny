package com.NZGames.BlockBunny.screens;
import com.NZGames.BlockBunny.BlockBunnyGame;
import com.NZGames.BlockBunny.entities.BatEnemy;
import com.NZGames.BlockBunny.entities.Crystal;
import com.NZGames.BlockBunny.entities.HUD;
import com.NZGames.BlockBunny.entities.Player;
import com.NZGames.BlockBunny.handlers.Box2DVars;
import com.NZGames.BlockBunny.handlers.MyContactListener;
import com.NZGames.BlockBunny.handlers.MyInput;
import com.NZGames.BlockBunny.handlers.MyInputProcessor;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import javafx.beans.property.MapProperty;
import jdk.nashorn.internal.ir.Block;

import java.lang.management.ThreadMXBean;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.*;

/**
 * Created by zac520 on 7/17/14.
 */
public class GameScreen implements Screen {

    BlockBunnyGame game;
    private boolean debug = false;
    TextureRegion myGolbez;
    int testCounter = 0;
    Stage stage;
    private SpriteBatch batch;
    private World world;
    Player player;
    private HUD hud;
    TextureAtlas atlas;

    OrthographicCamera camera;
    OrthographicCamera hudCam;
    OrthographicCamera box2DCam;

    Box2DDebugRenderer box2DRenderer;
    private MyContactListener cl;
    private float accelx=0;

    /** Tilemap stuff**/
    private TiledMap tileMap;
    private float tileSize;
    private OrthogonalTiledMapRenderer tmr;


    /** Textures **/
    private TextureRegion currentPlayerFrame;

    /** Animations **/
    private Animation walkLeftAnimation;
    private Animation walkRightAnimation;
    private static final float RUNNING_FRAME_DURATION = 0.06f;

    private Array<Crystal> crystals;
    Array<Body> bodies;
    public Array<Body> testCrystals;
    private float crystalX;
    private float crystalY;
    private Body tempBody;
    String tileMapLocation;



    //constructor
    public GameScreen(BlockBunnyGame myGame, String myTileMapLocation){
        tileMapLocation = myTileMapLocation;
        game = myGame;
        batch = new SpriteBatch();

        //create the world
        //x and y forces, then inactive bodies should "sleep" (true)
        world = new World(new Vector2(0, -9.81f), true);

        //set the contact listener
        cl = new MyContactListener();
        world.setContactListener(cl);

        //set the input processor
        Gdx.input.setInputProcessor(new MyInputProcessor());



        //set up the main camera
        camera=new OrthographicCamera();
        camera.setToOrtho(false, BlockBunnyGame.SCREEN_WIDTH, BlockBunnyGame.SCREEN_HEIGHT);
        stage=new Stage();
        stage.getViewport().setCamera(camera);


        //create a platform
        //createPlatform();

        createTiles();


        //create player
        createPlayer();

        //create crystals
        createCrystals();

        //create enemies
        //createBatEnemies();

        //set up the HUD camera
        hudCam = new OrthographicCamera();
        hudCam.setToOrtho(false, BlockBunnyGame.SCREEN_WIDTH, BlockBunnyGame.SCREEN_HEIGHT);

        //set up box2d renderer
        box2DRenderer = new Box2DDebugRenderer();

        //set up box2dcam
        box2DCam = new OrthographicCamera();
        box2DCam.setToOrtho(false, BlockBunnyGame.SCREEN_WIDTH / Box2DVars.PPM, BlockBunnyGame.SCREEN_HEIGHT / Box2DVars.PPM);

        //set up animation
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("assets/textures/golbez.txt"));
        walkLeftAnimation = new Animation(RUNNING_FRAME_DURATION,atlas.findRegions("golbezleft"));
        walkRightAnimation = new Animation(RUNNING_FRAME_DURATION,atlas.findRegions("golbezright"));

        //set up hud
        hud = new HUD(player);
    }

    public void update(float delta) {


        //(step, accuracy of collisions (6 or 8 steps recommended), accuracy
        //of setting bodies after collision (2 recommended))
        world.step(delta, 6, 2);

        //remove crystals if necessary
        bodies = cl.getBodiesToRemove();
        for(int i = 0; i< bodies.size; i++){
            //get the body
            tempBody = bodies.get(i);

            //use the crystal stored in the UserData to remove from the stage
            Crystal tempCrystal = (Crystal) tempBody.getUserData();
            tempCrystal.remove();

            //destroy the box2d body
            world.destroyBody(tempBody);

            //add one to the player's crystal count
            player.collectCrystal();
        }
        bodies.clear();

        //find out if fell off level. reset if true
        if(player.getBody().getPosition().y < 0){

            //trash everything
            //dispose();
            //go back to menu
            game.setScreen(new MenuScreen(game));
        }

        //handle the input from user
        handleInput();


        //update the player
        player.update(delta);

        //update the stage
        stage.act();
        stage.draw();
    }
    @Override
    public void render(float delta) {
        // clear screen
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //update the world
        update(delta);


        //set camera to follow player
        camera.position.set(
                player.getBody().getPosition().x * Box2DVars.PPM,
                BlockBunnyGame.SCREEN_HEIGHT/2,
                0
        );

        //draw tilemap
        tmr.setView(camera);
        tmr.render();

        //draw player
        batch.setProjectionMatrix(camera.combined);

        //render the player
        batch.begin();
        //if player is not walking, we just give the first frame of facing right or left. Otherwise, we cycle through
        if(player.getIsWalking()) {
            currentPlayerFrame = player.isFacingLeft() ? walkLeftAnimation.getKeyFrame(player.getStateTime(), true) : walkRightAnimation.getKeyFrame(player.getStateTime(), true);
        }
        else{
            currentPlayerFrame = player.isFacingLeft() ? walkLeftAnimation.getKeyFrame(0,true) : walkRightAnimation.getKeyFrame(0,true);
        }
        batch.draw(currentPlayerFrame,
                player.getBody().getPosition().x * Box2DVars.PPM - Player.WIDTH /2,
                player.getBody().getPosition().y * Box2DVars.PPM -Player.HEIGHT/2,
                player.WIDTH,
                player.HEIGHT);

        batch.end();
        camera.update();



        //draw hud
        batch.setProjectionMatrix(hudCam.combined);
        hud.render(batch);

        //draw box2d world
        if(debug) {

            //not currently working
            //batch.setProjectionMatrix(box2DCam.combined);
            box2DCam.position.set(
                    (player.getBody().getPosition().x) + BlockBunnyGame.SCREEN_WIDTH,
                    BlockBunnyGame.SCREEN_HEIGHT/2,
                    0
            );
            //box2DCam.update();
            box2DRenderer.render(world, box2DCam.combined);
            //box2DCam.update();

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
        //stage.dispose();
        //batch.dispose();



    }

    public void handleInput() {

        //handle accelerometer input
        accelx = Gdx.input.getAccelerometerY();
        //player.getBody().applyForceToCenter(accelx * 2.5f, 0, true);
        if(accelx >0.3){
            if(player.getBody().getLinearVelocity().x < Player.PLAYER_MAX_SPEED) {
                player.getBody().applyForceToCenter(12, 0, true);
            }
        }
        else if (accelx < -0.3){
            if(player.getBody().getLinearVelocity().x > -Player.PLAYER_MAX_SPEED) {
                player.getBody().applyForceToCenter(-12, 0, true);
            }
        }


        //set player direction
        if(accelx !=0) {
            if (accelx < 0) {
                player.facingLeft = true;

            } else {
                player.facingLeft = false;

            }
        }

        //playerJump
        if (MyInput.isPressed(MyInput.BUTTON1)) {
            //System.out.println("pressed Z");
            if (cl.isPlayerOnGround()) {
                //force is in newtons
                    player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, 4f);
                    //player.getBody().applyForceToCenter(0, 175, true);
                    MyInput.setKey(MyInput.BUTTON1, false);

            }
        }
        //switch block color
        if (MyInput.isPressed(MyInput.BUTTON2)) {
            //System.out.println("hold X");
            switchBlocks();
            MyInput.setKey(MyInput.BUTTON2, false);

        }

        if (MyInput.isDown(MyInput.BUTTON3)) {
            if(player.getBody().getLinearVelocity().x < Player.PLAYER_MAX_SPEED) {
                player.getBody().applyForceToCenter(12, 0, true);

            }
            player.facingLeft = false;

        }

        if (MyInput.isDown(MyInput.BUTTON4)) {
            if(player.getBody().getLinearVelocity().x > -Player.PLAYER_MAX_SPEED) {
                player.getBody().applyForceToCenter(-12, 0, true);

            }
            player.facingLeft = true;
        }

        //if player is on the ground and moving left or right, then set walking to true
        if((cl.isPlayerOnGround()) && Math.abs(player.getBody().getLinearVelocity().x)>0){
            player.isWalking = true;
        }
        else{
            player.isWalking = false;
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
        bdef.position.set((game.SCREEN_WIDTH / Box2DVars.PPM)/2, 120 / Box2DVars.PPM);
        bdef.type = BodyDef.BodyType.StaticBody;

        //create body
        Body body = world.createBody(bdef);

        //define Fixture
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((game.SCREEN_WIDTH / Box2DVars.PPM) /2, 5 / Box2DVars.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;

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
        shape.setAsBox((Player.WIDTH / Box2DVars.PPM)/2, (Player.HEIGHT / Box2DVars.PPM)/2);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        //fdef.restitution = 0.7f;//1= perfectly bouncy 0 = not at all bouncy
        fdef.filter.categoryBits = Box2DVars.BIT_PLAYER;
        fdef.filter.maskBits = Box2DVars.BIT_BLUE| Box2DVars.BIT_CRYSTAL;//what it can collide with (bitwise operators)

        //create fixture
        body.createFixture(fdef).setUserData("box");

        //create player
        player = new Player(body);
        body.setUserData(player);//used to provide reference later

        //create foot sensor
        shape.setAsBox(13 / Box2DVars.PPM, 13 / Box2DVars.PPM, new Vector2(0, ((-Player.HEIGHT/2 +12) / Box2DVars.PPM)), 0);//set the box down
        fdef.shape = shape;
        //fdef.filter.categoryBits = BIT_PLAYER;
        //fdef.filter.maskBits = BIT_BLUE;
        fdef.isSensor = true;//make the foot go through ground for easier contact determining
        body.createFixture(fdef).setUserData("foot");

        //create a box around the player to wake sleeping box2d objects
        shape.setAsBox(BlockBunnyGame.SCREEN_WIDTH/Box2DVars.PPM, BlockBunnyGame.SCREEN_HEIGHT /Box2DVars.PPM);
        fdef.shape = shape;
        fdef.isSensor = true;
        body.createFixture(fdef).setUserData("awake");


        shape.dispose();

    }

    public void createTiles(){
        /////////////////////////////////////////////////////////////////////
        // load tile map
        tileMap = new TmxMapLoader().load(tileMapLocation);
        tmr = new OrthogonalTiledMapRenderer(tileMap);

        tileSize = tileMap.getProperties().get("tilewidth", Integer.class);

        TiledMapTileLayer layer;
        layer = (TiledMapTileLayer) tileMap.getLayers().get("blue");
        if(layer!=null) {
            createLayer(layer, Box2DVars.BIT_BLUE);
        }

        layer = (TiledMapTileLayer) tileMap.getLayers().get("green");
        if(layer!=null) {
            createLayer(layer, Box2DVars.BIT_GREEN);
        }

        layer = (TiledMapTileLayer) tileMap.getLayers().get("red");
        if(layer!=null) {
            createLayer(layer, Box2DVars.BIT_RED);
        }

    }
    public void createLayer(TiledMapTileLayer layer, short bits){
        //for some reason, the guy has rows and columns reversed
        //go through all cells in layer
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();

        for (int row = 0; row < layer.getHeight(); row++) {
            for (int col = 0; col < layer.getWidth(); col++) {
                //get cell
                TiledMapTileLayer.Cell cell = layer.getCell(col, row);

                //check if cell exists
                if (cell == null) {
                    continue;
                }
                if (cell.getTile() == null) {
                    continue;
                }

                //create a body and fixture from cell
                bdef.type = BodyDef.BodyType.StaticBody;

                //box2d uses center, not corner positioning, so we add 0.5
                bdef.position.set(
                        (col + 0.5f) * tileSize / Box2DVars.PPM,
                        (row + 0.5f) * tileSize / Box2DVars.PPM
                );

                //use chainShape to prevent getting stuck between boxes
                ChainShape cs = new ChainShape();

                //chainshapes select point and draw to next point. so need 5 points for 4 edges
                //(first point simply is a point, but also the end of our box shape.)
                Vector2[] v = new Vector2[5];
                v[0] = new Vector2(
                        -tileSize / 2 / Box2DVars.PPM, -tileSize / 2 / Box2DVars.PPM);
                v[1] = new Vector2(
                        -tileSize / 2 / Box2DVars.PPM, tileSize / 2 / Box2DVars.PPM);
                v[2] = new Vector2(
                        tileSize / 2 / Box2DVars.PPM, tileSize / 2 / Box2DVars.PPM);
                v[3] = new Vector2(
                        tileSize / 2 / Box2DVars.PPM, -tileSize / 2 / Box2DVars.PPM);
                v[4] = v[0];

                cs.createChain(v);
                fdef.shape = cs;
                fdef.filter.categoryBits = bits;
                fdef.filter.maskBits = Box2DVars.BIT_PLAYER;//default
                fdef.isSensor = false;
                fdef.friction = 0.5f;
                world.createBody(bdef).createFixture(fdef);
                cs.dispose();
            }
        }
    }
    private void switchBlocks(){
        Filter filter = player.getBody().getFixtureList().first().getFilterData();
        short bits = filter.maskBits;

        //switch to next color blue->green->red
        if((bits & Box2DVars.BIT_BLUE) !=0){
            //unset the blue bit
            bits &= ~Box2DVars.BIT_BLUE;
            bits |= Box2DVars.BIT_GREEN;

        }
        else if((bits & Box2DVars.BIT_GREEN) !=0){
            //unset the blue bit
            bits &= ~Box2DVars.BIT_GREEN;
            bits |= Box2DVars.BIT_RED;

        }
        else if((bits & Box2DVars.BIT_RED) !=0){
            //unset the blue bit
            bits &= ~Box2DVars.BIT_RED;
            bits |= Box2DVars.BIT_BLUE;

        }
        filter.maskBits = bits;
        player.getBody().getFixtureList().first().setFilterData(filter);

        //set new mask bits for foot
        filter = player.getBody().getFixtureList().get(1).getFilterData();
        bits &= ~Box2DVars.BIT_CRYSTAL;
        filter.maskBits = bits;
        player.getBody().getFixtureList().get(1).setFilterData(filter);

    }
    private void createCrystals(){
        crystals = new Array<Crystal>();
        testCrystals = new Array<Body>();
        MapLayer layer = tileMap.getLayers().get("crystals");
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        float x=0;
        float y=0;

//        MapProperties   prop = tileMap.getProperties();
//        float mapWidth = prop.get("width", Integer.class);//gives the width in blocks
//        mapWidth = mapWidth * prop.get("tilewidth", Integer.class); //multiply by width of blocks


        for (MapObject mo: layer.getObjects()){

                if (mo instanceof RectangleMapObject) {
                    Rectangle rect = ((RectangleMapObject) mo).getRectangle();
                    x = rect.x / Box2DVars.PPM;
                    y = rect.y / Box2DVars.PPM;
                } else if (mo instanceof PolygonMapObject) {
                    Polygon polygon = ((PolygonMapObject) mo).getPolygon();
                    //these are not right for this shape, but just a starter for if I need it
//                x = polygon.x / Box2DVars.PPM;
//                y = polygon.y / Box2DVars.PPM;
                } else if (mo instanceof PolylineMapObject) {
                    Polyline chain = ((PolylineMapObject) mo).getPolyline();
//                x = chain.x / Box2DVars.PPM;
//                y = chain.y / Box2DVars.PPM;
                } else if (mo instanceof CircleMapObject) {
                    Circle circle = ((CircleMapObject) mo).getCircle();
                    x = circle.x / Box2DVars.PPM;
                    y = circle.y / Box2DVars.PPM;
                } else if (mo instanceof EllipseMapObject) {
                    Ellipse ellipse = ((EllipseMapObject) mo).getEllipse();
                    x = ellipse.x / Box2DVars.PPM;
                    y = ellipse.y / Box2DVars.PPM;
                }


                bdef.type = BodyDef.BodyType.DynamicBody;
                bdef.allowSleep = true;
                bdef.awake = false;
                bdef.position.set(x, y);

                CircleShape cshape = new CircleShape();
                cshape.setRadius(8 / Box2DVars.PPM);

                fdef.shape = cshape;
                fdef.isSensor = true;
                fdef.filter.categoryBits = Box2DVars.BIT_CRYSTAL;
                fdef.filter.maskBits = Box2DVars.BIT_PLAYER | Box2DVars.BIT_AWAKE;

                Body body = world.createBody(bdef);
                body.createFixture(fdef).setUserData("crystal");

                //test area.... using actors and stage instead
                //////////////////////////////////////////////////////////////////////////////////
                //create crystal, add to stage

                Crystal crystal = new Crystal(body, this);
                body.setUserData(crystal);//used to find it again given just the body later
                crystal.setPosition(x * Box2DVars.PPM, y * Box2DVars.PPM);
                crystal.addAction(
                        forever(
                                sequence(
                                        moveTo(x * Box2DVars.PPM + 50, y * Box2DVars.PPM, 1),
                                        moveTo(x * Box2DVars.PPM, y * Box2DVars.PPM, 1)
                                )
                        )
                );
                testCrystals.add(body);
                stage.addActor(crystal);
                //////////////////////////////////////////////////////////////////////////////////
                cshape.dispose();
            }


    }

    private void createBatEnemies(){
        crystals = new Array<Crystal>();
        testCrystals = new Array<Body>();
        MapLayer layer = tileMap.getLayers().get("enemies");
        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        float x=0;
        float y=0;

//        MapProperties   prop = tileMap.getProperties();
//        float mapWidth = prop.get("width", Integer.class);//gives the width in blocks
//        mapWidth = mapWidth * prop.get("tilewidth", Integer.class); //multiply by width of blocks


        for (MapObject mo: layer.getObjects()){

            if (mo instanceof RectangleMapObject) {
                Rectangle rect = ((RectangleMapObject) mo).getRectangle();
                x = rect.x / Box2DVars.PPM;
                y = rect.y / Box2DVars.PPM;
            } else if (mo instanceof PolygonMapObject) {
                Polygon polygon = ((PolygonMapObject) mo).getPolygon();
                //these are not right for this shape, but just a starter for if I need it
//                x = polygon.x / Box2DVars.PPM;
//                y = polygon.y / Box2DVars.PPM;
            } else if (mo instanceof PolylineMapObject) {
                Polyline chain = ((PolylineMapObject) mo).getPolyline();
//                x = chain.x / Box2DVars.PPM;
//                y = chain.y / Box2DVars.PPM;
            } else if (mo instanceof CircleMapObject) {
                Circle circle = ((CircleMapObject) mo).getCircle();
                x = circle.x / Box2DVars.PPM;
                y = circle.y / Box2DVars.PPM;
            } else if (mo instanceof EllipseMapObject) {
                Ellipse ellipse = ((EllipseMapObject) mo).getEllipse();
                x = ellipse.x / Box2DVars.PPM;
                y = ellipse.y / Box2DVars.PPM;
            }


            bdef.type = BodyDef.BodyType.DynamicBody;
            bdef.allowSleep = true;
            bdef.awake = false;
            bdef.position.set(x, y);

            CircleShape cshape = new CircleShape();
            cshape.setRadius(8 / Box2DVars.PPM);

            fdef.shape = cshape;
            fdef.isSensor = true;
            fdef.filter.categoryBits = Box2DVars.BIT_CRYSTAL;
            fdef.filter.maskBits = Box2DVars.BIT_PLAYER | Box2DVars.BIT_AWAKE;

            Body body = world.createBody(bdef);
            body.createFixture(fdef).setUserData("bat");

            //test area.... using actors and stage instead
            //////////////////////////////////////////////////////////////////////////////////
            //create crystal, add to stage

            BatEnemy enemy = new BatEnemy(body, this);
            body.setUserData(enemy);//used to find it again given just the body later
            enemy.setPosition(x * Box2DVars.PPM, y * Box2DVars.PPM);
            enemy.addAction(
                    forever(
                            sequence(
                                    moveTo(x * Box2DVars.PPM + 50, y * Box2DVars.PPM, 1),
                                    moveTo(x * Box2DVars.PPM, y * Box2DVars.PPM, 1)
                            )
                    )
            );
            //testCrystals.add(body);
            stage.addActor(enemy);
            //////////////////////////////////////////////////////////////////////////////////
            cshape.dispose();
        }


    }


}
