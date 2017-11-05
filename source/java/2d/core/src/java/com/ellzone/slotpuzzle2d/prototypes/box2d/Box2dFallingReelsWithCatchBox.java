/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ellzone.slotpuzzle2d.prototypes.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.prototypes.Reels;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import java.util.ArrayList;
import java.util.Random;

public class Box2dFallingReelsWithCatchBox extends SPPrototype implements InputProcessor {
    private OrthographicCamera camera;
    private ShapeRenderer renderer;
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private BitmapFont font;
    private Random random;
    private World world;
    private ArrayList<Body> boxes = new ArrayList<Body>();
    private Body groundBody, miniSlotMachineBody;
    private MouseJoint mouseJoint = null;
    private Body hitBody = null;
    private TweenManager tweenManager = new TweenManager();
    Array<AnimatedReel> animatedReels;
    private Reels reels;
    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private int spriteWidth, spriteHeight;
    private int displayWindowWidth, displayWindowHeight;
    private TextureAtlas slotHandleAtlas;
    private Sound pullLeverSound, reelSpinningSound, reelStoppingSound;

    @Override
    public void create() {
        camera = new OrthographicCamera(48, 32);
        camera.position.set(0, 16, 0);
        renderer = new ShapeRenderer();
        debugRenderer = new Box2DDebugRenderer();
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.RED);
        loadAssets();
        reels = new Reels();
        spriteWidth = reels.getReelWidth();
        spriteHeight = reels.getReelHeight();
        this.displayWindowWidth = SlotPuzzleConstants.V_WIDTH;
        this.displayWindowHeight = SlotPuzzleConstants.V_HEIGHT;
        initialiseUniversalTweenEngine();
        createPhysicsWorld();
        Gdx.input.setInputProcessor(this);
    }

    private void loadAssets() {
        Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
        Assets.inst().load("slot_handle/slot_handle.pack.atlas", TextureAtlas.class);
        Assets.inst().load("sounds/pull-lever1.wav", Sound.class);
        Assets.inst().load("sounds/click2.wav", Sound.class);
        Assets.inst().load("sounds/reel-stopped.wav", Sound.class);
        Assets.inst().update();
        Assets.inst().finishLoading();

        slotHandleAtlas = Assets.inst().get("slot_handle/slot_handle.pack.atlas", TextureAtlas.class);
        pullLeverSound = Assets.inst().get("sounds/pull-lever1.wav");
        reelSpinningSound = Assets.inst().get("sounds/click2.wav");
        reelStoppingSound = Assets.inst().get("sounds/reel-stopped.wav");
    }

    private void initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        initialiseReelSlots();
    }

    private void initialiseReelSlots() {
        random = new Random();
        animatedReels = new Array<AnimatedReel>();
        slotReelScrollPixmap = new Pixmap(spriteWidth, spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(reels.getReels());
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        for (int i = 0; i < 20; i++) {
            AnimatedReel animatedReel = new AnimatedReel(slotReelScrollTexture, 0, 0, spriteWidth, spriteHeight, spriteWidth, spriteHeight, 0, reelSpinningSound, reelStoppingSound, tweenManager);
            animatedReel.setSx(0);
            animatedReel.setEndReel(random.nextInt(reels.getReels().length - 1));
            animatedReel.getReel().startSpinning();
            animatedReels.add(animatedReel);
        }
    }

    private void createPhysicsWorld() {
        world = new World(new Vector2(0, -10), true);

        PolygonShape groundPoly = new PolygonShape();
        groundPoly.setAsBox(50, 1);

        BodyDef groundBodyDef = new BodyDef();
        groundBodyDef.type = BodyDef.BodyType.StaticBody;
        groundBody = world.createBody(groundBodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = groundPoly;
        fixtureDef.filter.groupIndex = 0;
        groundBody.createFixture(fixtureDef);
        groundPoly.dispose();

        createBoxes();
    }

    private void createBoxes() {
        PolygonShape boxPoly = new PolygonShape();
        boxPoly.setAsBox(1, 1);
        for (int i = 0; i < 20; i++) {
            BodyDef boxBodyDef = new BodyDef();
            boxBodyDef.type = BodyDef.BodyType.DynamicBody;
            boxBodyDef.position.x = -24 + (float) (Math.random() * 48);
            boxBodyDef.position.y = 10 + (float) (Math.random() * 100);
            Body boxBody = world.createBody(boxBodyDef);

            boxBody.createFixture(boxPoly, 1);
            boxes.add(boxBody);

        }
        boxPoly.dispose();
    }

    private void update(float dt) {
        tweenManager.update(dt);
        for (AnimatedReel reel : animatedReels) {
            reel.update(dt);
        }
    }

    @Override
    public void render() {
        long start = TimeUtils.nanoTime();
        float dt = Gdx.graphics.getDeltaTime();
        world.step(dt, 8, 3);
        float updateTime = (TimeUtils.nanoTime() - start) / 1000000000.0f;

        update(dt);

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        renderBox(groundBody, 50, 1);
        batch.getProjectionMatrix().set(camera.combined);
        batch.begin();
        for (int i = 0; i < boxes.size(); i++) {
            Body box = boxes.get(i);
            Vector2 position = box.getPosition();
            float angle = MathUtils.radiansToDegrees * box.getAngle();
            animatedReels.get(i).getReel().setPosition(position.x - 1, position.y - 1);
            animatedReels.get(i).getReel().setSize(2, 2);
            animatedReels.get(i).getReel().setOrigin(1, 1);
            animatedReels.get(i).getReel().setRotation(angle);
            animatedReels.get(i).draw(batch);
        }
        batch.end();

        debugRenderer.render(world, camera.combined);
        renderer.setProjectionMatrix(camera.combined);
        renderer.begin(ShapeRenderer.ShapeType.Point);
        renderer.setColor(0, 1, 0, 1);
        for (int i = 0; i < world.getContactCount(); i++) {
            Contact contact = world.getContactList().get(i);
            if (contact.isTouching()) {
                WorldManifold manifold = contact.getWorldManifold();
                int numContactPoints = manifold.getNumberOfContactPoints();
                for (int j = 0; j < numContactPoints; j++) {
                    Vector2 point = manifold.getPoints()[j];
                    renderer.point(point.x, point.y, 0);
                }
            }
        }
        renderer.end();
        batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.begin();
        font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond() + " update time: " + updateTime, 0, 20);
        batch.end();
    }

    Matrix4 transform = new Matrix4();

    private void renderBox(Body body, float halfWidth, float halfHeight) {
        Vector2 pos = body.getWorldCenter();
        float angle = body.getAngle();

        transform.setToTranslation(pos.x, pos.y, 0);
        transform.rotate(0, 0, 1, (float) Math.toDegrees(angle));

        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setTransformMatrix(transform);
        renderer.setColor(1, 1, 1, 1);
        renderer.rect(-halfWidth, -halfHeight, halfWidth * 2, halfHeight * 2);
        renderer.end();
    }

    Vector3 testPoint = new Vector3();
    QueryCallback callback = new QueryCallback() {
        @Override
        public boolean reportFixture(Fixture fixture) {
            if (fixture.getBody() == groundBody) return true;

            if (fixture.testPoint(testPoint.x, testPoint.y)) {
                hitBody = fixture.getBody();
                return false;
            } else
                return true;
        }
    };

    @Override
    public boolean touchDown(int x, int y, int pointer, int newParam) {
        testPoint.set(x, y, 0);
        camera.unproject(testPoint);
        hitBody = null;
        world.QueryAABB(callback, testPoint.x - 0.1f, testPoint.y - 0.1f, testPoint.x + 0.1f, testPoint.y + 0.1f);

        if (hitBody != null) {
            MouseJointDef def = new MouseJointDef();
            def.bodyA = groundBody;
            def.bodyB = hitBody;
            def.collideConnected = true;
            def.target.set(testPoint.x, testPoint.y);
            def.maxForce = 1000.0f * hitBody.getMass();

            mouseJoint = (MouseJoint) world.createJoint(def);
            hitBody.setAwake(true);
        } else {
            for (Body box : boxes)
                world.destroyBody(box);
            boxes.clear();
            createBoxes();
            for (AnimatedReel animatedReel : animatedReels) {
                animatedReel.reinitialise();
                animatedReel.getReel().startSpinning();
            }
        }

        return false;
    }

    Vector2 target = new Vector2();

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        if (mouseJoint != null) {
            camera.unproject(testPoint.set(x, y, 0));
            mouseJoint.setTarget(target.set(testPoint.x, testPoint.y));
        }
        return false;
    }

    @Override
    public boolean touchUp(int x, int y, int pointer, int button) {
        if (mouseJoint != null) {
            world.destroyJoint(mouseJoint);
            mouseJoint = null;
        }
        return false;
    }

    @Override
    public void dispose() {
        world.dispose();
        renderer.dispose();
        debugRenderer.dispose();
        font.dispose();
    }
}