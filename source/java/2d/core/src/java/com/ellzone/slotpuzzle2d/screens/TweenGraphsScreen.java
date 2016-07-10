package com.ellzone.slotpuzzle2d.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.effects.ReelSpriteAccessor;
import com.ellzone.slotpuzzle2d.graphs.DrawTweenGraphs;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTileScroll;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTileScroll3D;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import java.util.Random;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Quint;
import aurelienribon.tweenengine.equations.Sine;

public class TweenGraphsScreen implements Screen {
    public final static float SIXTY_FPS = 1/60f;
    public final static float MINIMUM_VIEWPORT_SIZE = 15f;

    public static class TileBatch extends ObjectSet<ReelSlotTileScroll3D> implements RenderableProvider, Disposable {
        Renderable renderable;
        Mesh mesh;
        MeshBuilder meshBuilder;

        public TileBatch(Material material) {
            final int maxNumberOfTiles = 52;
            final int maxNumberOfVertices = maxNumberOfTiles * 8;
            final int maxNumberOfIndices = maxNumberOfTiles * 12;
            mesh = new Mesh(false, maxNumberOfVertices, maxNumberOfIndices,
                    VertexAttribute.Position(), VertexAttribute.Normal(), VertexAttribute.TexCoords(0));
            meshBuilder = new MeshBuilder();

            renderable = new Renderable();
            renderable.material = material;
        }

        @Override
        public void getRenderables(Array<Renderable> renderables, Pool<Renderable> pool) {
            meshBuilder.begin(mesh.getVertexAttributes());
            meshBuilder.part("tiles", GL20.GL_TRIANGLES, renderable.meshPart);
            for (ReelSlotTileScroll3D reel3D : this) {
                meshBuilder.setVertexTransform(reel3D.transform);
                meshBuilder.addMesh(reel3D.vertices, reel3D.indices);
            }
            meshBuilder.end(mesh);

            renderables.add(renderable);
        }

        @Override
        public void dispose() {
            mesh.dispose();
        }
    }


    private DrawTweenGraphs game;
    PerspectiveCamera cam;
    private Sprite cheesecake;
    private Sprite cherry;
    private Sprite grapes;
    private Sprite jelly;
    private Sprite lemon;
    private Sprite peach;
    private Sprite pear;
    private Sprite tomato;
    private Sprite[] sprites;
    private ReelSlotTileScroll reelSlot;
    private Array<ReelSlotTileScroll> reelSlots = new Array<ReelSlotTileScroll>();
    private boolean isLoaded;
    private Random random;
    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private int touchX, touchY;
    private final TweenManager tweenManager = new TweenManager();
    private SlotPuzzleTween tween;
    private Array<SlotPuzzleTween> tweens = new Array<SlotPuzzleTween>();
    private float returnValues[] = new float[2];
    private boolean tweenClicked = false;
    private ShapeRenderer shapeRenderer;
    private int graphStep = 0;
    private Array<Vector2> points = new Array<Vector2>();
    private TweenEquation easeEquation;
    private int toValue;
    private int oldToValue;
    private float savedTime;
    private boolean endCallBack = false;
    CameraInputController camController;
    ModelBatch modelBatch;
    Environment environment;
    TileBatch tiles;


    public TweenGraphsScreen(DrawTweenGraphs game) {
        this.game = game;
        defineTweenScreen();
    }

    @Override
    public void show() {
    }

    private void defineTweenScreen() {
        random = new Random();
        Tween.setWaypointsLimit(10);
        Tween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(ReelSlotTileScroll.class, new ReelSpriteAccessor());

        modelBatch = new ModelBatch();

        Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();
        isLoaded = true;

        TextureAtlas atlas = Assets.inst().get("reel/reels.pack.atlas", TextureAtlas.class);
        cherry = atlas.createSprite("cherry");
        cheesecake = atlas.createSprite("cheesecake");
        grapes = atlas.createSprite("grapes");
        jelly = atlas.createSprite("jelly");
        lemon = atlas.createSprite("lemon");
        peach = atlas.createSprite("peach");
        pear = atlas.createSprite("pear");
        tomato = atlas.createSprite("tomato");

        Material material = new Material(
                TextureAttribute.createDiffuse(atlas.getTextures().first()),
                new BlendingAttribute(false, 1f),
                FloatAttribute.createAlphaTest(0.5f));
        tiles = new TileBatch(material);

        sprites = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
        for (Sprite sprite : sprites) {
            sprite.setOrigin(0, 0);
        }

        slotReelScrollPixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        reelSlot = new ReelSlotTileScroll(slotReelScrollTexture, slotReelScrollTexture.getWidth(), slotReelScrollTexture.getHeight(), 0, 32, 0, TweenGraphsScreen.SIXTY_FPS);
        reelSlot.setX(0);
        reelSlot.setY(32);
        reelSlot.setEndReel(random.nextInt(sprites.length));
        reelSlots.add(reelSlot);

        easeEquation =  Quart.OUT;
        tween = SlotPuzzleTween.to(reelSlot, ReelSpriteAccessor.SCROLL_XY, 20.0f) .target(0,  2560 + reelSlot.getEndReel() * 32) .ease(easeEquation).setCallback(new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                delegateTweenOnEvent(type, source, tween, reelSlot);
            }
        }) .setCallbackTriggers(TweenCallback.STEP + TweenCallback.END)
                .start(tweenManager);
        tweens.add(tween);

        reelSlot = new ReelSlotTileScroll(slotReelScrollTexture, slotReelScrollTexture.getWidth(), slotReelScrollTexture.getHeight(), 32, 32, 0, TweenGraphsScreen.SIXTY_FPS);
        reelSlot.setX(32);
        reelSlot.setY(32);
        reelSlot.setEndReel(random.nextInt(sprites.length));
        reelSlots.add(reelSlot);

        easeEquation =  Elastic.OUT;
        tween = SlotPuzzleTween.to(reelSlot, ReelSpriteAccessor.SCROLL_XY, 10.0f) .target(0, reelSlot.getEndReel() * 32) .ease(easeEquation);
        tween = tween.setCallback(new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                delegateTweenOnEvent(type, source, tween, reelSlot);
            }
        }) .setCallbackTriggers(TweenCallback.STEP + TweenCallback.END)
                .start(tweenManager);
        tweens.add(tween);

        reelSlot = new ReelSlotTileScroll(slotReelScrollTexture, slotReelScrollTexture.getWidth(), slotReelScrollTexture.getHeight(), 64, 32, 0, TweenGraphsScreen.SIXTY_FPS);
        reelSlot.setX(64);
        reelSlot.setY(32);
        reelSlot.setEndReel(random.nextInt(sprites.length));
        reelSlots.add(reelSlot);

        easeEquation =  Quint.OUT;
        toValue = 2048;
        tween = SlotPuzzleTween.to(reelSlot, ReelSpriteAccessor.SCROLL_XY, 10.0f) .target(0, toValue).ease(easeEquation);
        tween = tween.setCallback(new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                delegateTweenOnEventToAdjustTarget(type, source, tween, reelSlot);
            }
        }) .setCallbackTriggers(TweenCallback.STEP + TweenCallback.END + TweenCallback.COMPLETE)
                .start(tweenManager);
        tweens.add(tween);

        shapeRenderer = new ShapeRenderer();

        cam = new PerspectiveCamera();
        cam.position.set(0, 0, 10);
        cam.lookAt(0, 0, 0);
        //camController = new CameraInputController(cam);
        Gdx.input.setInputProcessor(camController);

        ReelSlotTileScroll3D tile1 = new ReelSlotTileScroll3D(slotReelScrollTexture,
                slotReelScrollTexture.getWidth(), slotReelScrollTexture.getHeight(), -1, 0, 0, TweenGraphsScreen.SIXTY_FPS, lemon, cherry);
        tile1.position.set(-1, 0, 0.01f);
        tile1.update();
        tiles.add(tile1);

        ReelSlotTileScroll3D tile2 = new ReelSlotTileScroll3D(slotReelScrollTexture,
                slotReelScrollTexture.getWidth(), slotReelScrollTexture.getHeight(), 0, 0, 0, TweenGraphsScreen.SIXTY_FPS, jelly, grapes);
        tile2.position.set(0, 0, 0.01f);
        tile2.update();
        tiles.add(tile2);

        ReelSlotTileScroll3D tile3 = new ReelSlotTileScroll3D(slotReelScrollTexture,
                slotReelScrollTexture.getWidth(), slotReelScrollTexture.getHeight(), 1, 0, 0, TweenGraphsScreen.SIXTY_FPS, peach, pear);
        tile3.position.set(1, 0, 0.01f);
        tile3.update();
        tiles.add(tile3);

        ReelSlotTileScroll3D tile4 = new ReelSlotTileScroll3D(slotReelScrollTexture,
                slotReelScrollTexture.getWidth(), slotReelScrollTexture.getHeight(), -5, -5, 0, TweenGraphsScreen.SIXTY_FPS, peach, pear);
        tile4.position.set(-12, -7, 0.0f);
        tile4.update();
        tiles.add(tile4);


        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1.f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -.4f, -.4f, -.4f));
    }

    private void delegateTweenOnEvent(int type, BaseTween<?> source, SlotPuzzleTween tween, ReelSlotTileScroll reelSlot) {
        switch (type){
            case TweenCallback.ANY: System.out.println("ANY"); break;
            case TweenCallback.ANY_BACKWARD: System.out.println("ANY_BACKWARD"); break;
            case TweenCallback.ANY_FORWARD: System.out.println("ANY_FORWARD"); break;
            case TweenCallback.BACK_BEGIN: System.out.println("BACK_BEGIN"); break;
            case TweenCallback.BACK_COMPLETE: System.out.println("BACK_COMPLETE"); break;
            case TweenCallback.BACK_START: System.out.println("BACK_START"); break;
            case TweenCallback.COMPLETE: System.out.println("COMPLETE"); break;
            case TweenCallback.END:
                ReelSpriteAccessor accessor = (ReelSpriteAccessor) tween.getAccessor();
                if (accessor != null) {
                    int size = accessor.getValues(reelSlot, ReelSpriteAccessor.SCROLL_XY, returnValues);
                } else {
                    System.out.println("null!");
                }
                break;
            case TweenCallback.START: System.out.println("START"); break;
            case TweenCallback.STEP: break;
        }
    }

    private void delegateTweenOnEventToAdjustTarget(int type, BaseTween<?> source, SlotPuzzleTween tween1, ReelSlotTileScroll reelSlot) {
        if (type == TweenCallback.STEP) {
            if (toValue != 0) {
                System.out.println("tv=0");
                toValue += 4;
                tween1.target(0, toValue);
                tween1.setDuration(tween1.getDuration() + 0.2f);
            }
            else if (!endCallBack) {
                System.out.println("!endCallBack");
                System.out.println("duration="+tween1.getDuration());
                System.out.println("currentTime="+tween1.getCurrentTime());
                float myReturnValues[] = tween1.getTargetValues();

                tween1.target(0, 1024 - (oldToValue % 1024) + 2024);
                tween1.setDuration(savedTime + 5.0f);
                endCallBack = true;
            } else {
                System.out.println("endCallBack");
                System.out.println(tween1.getDuration());
                System.out.println(tween1.getCurrentTime());
                tween1.setDuration(0);
            }
        } else if (type == TweenCallback.COMPLETE) {
            final SlotPuzzleTween tweenReference = tween1;
            final ReelSlotTileScroll reelSlotReference = reelSlot;
            toValue = 2048;
            endCallBack = false;
            tween1 = tween1.to(reelSlot, ReelSpriteAccessor.SCROLL_XY, 10.0f) .target(0, toValue).ease(Quint.OUT);
            tween1 = tween1.setCallback(new TweenCallback() {
                @Override
                public void onEvent(int type, BaseTween<?> source) {
                        delegateTweenOnEventToAdjustTarget(type, source, tweenReference, reelSlotReference);
                    }
                }) .setCallbackTriggers(TweenCallback.STEP + TweenCallback.END + TweenCallback.COMPLETE)
                    .start(tweenManager);
        }
    }

    public void handleInput(float dt) {
        if (Gdx.input.justTouched()) {
            touchX = Gdx.input.getX();
            touchY = Gdx.input.getY();
            System.out.println("x="+touchX+ " y="+touchY);
            Vector3 newPoints = new Vector3(touchX, touchY, 0);
            cam.unproject(newPoints);
            newPoints.x = newPoints.x * 800;
            newPoints.y = newPoints.y * 480;
            System.out.println("x="+newPoints.x+ " y="+newPoints.y);
            if ((newPoints.x >= -880) & (newPoints.x <= -670) & (newPoints.y >= -276) & (newPoints.y <= -234)) {

                int slotIndex = (int) (Math.abs(newPoints.x + 880))  / 69;
                System.out.println("Touched region"+Math.abs(newPoints.x + 880));
                System.out.println(slotIndex);
                if (slotIndex == 0) {
                    System.out.println("slotIndex=0 clicked");
                    if (tweens.get(0).getCurrentTime() == 0) {
                        tweenClicked = false;
                        reelSlots.get(0).setEndReel(random.nextInt(sprites.length));
                        tweens.set(0, SlotPuzzleTween.to(reelSlots.get(0), ReelSpriteAccessor.SCROLL_XY, 20.0f).target(0, returnValues[1] + (2560 - returnValues[1] % 2560) + reelSlots.get(0).getEndReel() * 32).ease(Sine.OUT).setCallback(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                delegateTweenOnEvent(type, source, tweens.get(0), reelSlots.get(0));
                            }
                        }).setCallbackTriggers(TweenCallback.END)
                                .start(tweenManager));
                    } else {
                        if ((tweens.get(0).getCurrentTime() < tweens.get(0).getDuration() / 2.0f) & (!tweenClicked)) {
                            tweenClicked = true;
                            reelSlots.get(0).setEndReel();
                            tweens.set(0, tweens.get(0).target(0, returnValues[1] + (1280 - (returnValues[1] % 1280)) + reelSlots.get(0).getEndReel() * 32));
                            tweens.set(0, tweens.get(0).setDuration((tweens.get(0).getDuration() - tweens.get(0).getCurrentTime())));
                            System.out.println("new target="+returnValues[1] + (1280 - (returnValues[1] % 1280)) + reelSlots.get(0).getEndReel() * 32);
                            System.out.println("new duration="+(tweens.get(0).getDuration() - tweens.get(0).getCurrentTime()));
                            tweens.get(0).start();
                        }
                    }
                } else if (slotIndex == 2) {
                    System.out.println("reel 2");
                    oldToValue = toValue;
                    toValue = 0;
                    savedTime = tweens.get(2).getCurrentTime();
                    System.out.println("oldToValue="+oldToValue);

                }
            }
        }
    }

    private void drawGraphPoint(ShapeRenderer shapeRenderer, Vector2 newPoint) {
        if (points.size >= 2) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(255, 255, 255, 255);
            for (int i = 0; i < points.size - 1; i++) {
                shapeRenderer.line(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            }
            shapeRenderer.end();
        }
        points.add(newPoint);
    }

    private void update(float delta) {
        tweenManager.update(delta);
        for(ReelSlotTileScroll reelSlot : reelSlots) {
            reelSlot.update(delta);
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        handleInput(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if(isLoaded) {
                game.batch.begin();
                for (ReelSlotTileScroll reelSlot : reelSlots) {
                    reelSlot.draw(game.batch);
                }
                game.batch.end();

                //camController.update();

                ReelSlotTileScroll3D tile = tiles.first();
                tile.angle = (tile.angle + 90 * delta) % 360;
                tile.update();

                modelBatch.begin(cam);
                modelBatch.render(tiles, environment);
                modelBatch.end();
                drawGraphPoint(shapeRenderer, new Vector2(graphStep++   % Gdx.graphics.getWidth(), reelSlots.get(0).getSy() % slotReelScrollTexture.getHeight()));
                drawGraphPoint(shapeRenderer, new Vector2(graphStep++ % Gdx.graphics.getWidth(), reelSlots.get(1).getSy() % slotReelScrollTexture.getHeight()));

            } else {
                if (Assets.inst().getProgress() < 1) {
                    Assets.inst().update();
                } else {
                    isLoaded = true;
                }
            }
    }

    @Override
    public void resize(int width, int height) {
        System.out.println("w="+width+ " h="+height);
        float halfHeight = MINIMUM_VIEWPORT_SIZE * 0.5f;
        if (height > width)
            halfHeight *= (float)height / (float)width;
        float halfFovRadians = MathUtils.degreesToRadians * cam.fieldOfView * 0.5f;
        float distance = halfHeight / (float)Math.tan(halfFovRadians);
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.position.set(0, 0, distance);
        cam.lookAt(0, 0, 0);
        cam.update();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
