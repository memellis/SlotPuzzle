package com.ellzone.slotpuzzle2d.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.effects.ReelSpriteAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.graphs.DrawTweenGraphs;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTileScroll;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import java.util.Random;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Sine;

public class TweenGraphsScreen implements Screen {
    private static final float SIXTY_FPS = 1/60f;

    private DrawTweenGraphs game;
    private final OrthographicCamera camera = new OrthographicCamera();
    private Viewport viewport;
    private Stage stage;
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
    private boolean isLoaded;
    private Random random;
    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private int touchX, touchY;
    private final TweenManager tweenManager = new TweenManager();
    private SlotPuzzleTween tween;
    private float returnValues[] = new float[2];
    private boolean tweenClicked = false;

    public TweenGraphsScreen(DrawTweenGraphs game) {
        this.game = game;
        defineSplashScreen();
    }

    @Override
    public void show() {
    }

    private void defineSplashScreen() {
        random = new Random();
        Tween.setWaypointsLimit(10);
        Tween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(ReelSlotTileScroll.class, new ReelSpriteAccessor());

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

        sprites = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
        for (Sprite sprite : sprites) {
            sprite.setOrigin(0, 0);
        }

        viewport = new FitViewport(800, 480, camera);
        stage = new Stage(viewport, game.batch);

        slotReelScrollPixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        reelSlot = new ReelSlotTileScroll(slotReelScrollTexture, slotReelScrollTexture.getWidth(), slotReelScrollTexture.getHeight(), 0, 32, 0, TweenGraphsScreen.SIXTY_FPS);
        reelSlot.setX(0);
        reelSlot.setY(32);
        reelSlot.setEndReel(random.nextInt(sprites.length));

        tween = SlotPuzzleTween.to(reelSlot, ReelSpriteAccessor.SCROLL_XY, 20.0f) .target(0,  2560 + reelSlot.getEndReel() * 32) .ease(Sine.OUT).setCallback(new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                delegateTweenOnEvent(type, source);
            }
        }) .setCallbackTriggers(TweenCallback.STEP + TweenCallback.END)
                .start(tweenManager);

    }

    private void delegateTweenOnEvent(int type, BaseTween<?> source) {
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
    public void handleInput(float dt) {
        if (Gdx.input.justTouched()) {
            touchX = Gdx.input.getX();
            touchY = Gdx.input.getY();
            Vector2 newPoints1 = new Vector2(touchX, touchY);
            newPoints1 = viewport.unproject(newPoints1);
            if ((newPoints1.x >= 0) & (newPoints1.x <= 32) & (newPoints1.y >= 32) & (newPoints1.y <= 64)) {
                if (tween.getCurrentTime() == 0) {
                    tweenClicked = false;
                    reelSlot.setEndReel(random.nextInt(sprites.length));
                    tween = SlotPuzzleTween.to(reelSlot, ReelSpriteAccessor.SCROLL_XY, 20.0f).target(0, returnValues[1] + (2560 - returnValues[1] % 2560) + reelSlot.getEndReel() * 32).ease(Sine.OUT).setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            delegateTweenOnEvent(type, source);
                        }
                    }).setCallbackTriggers(TweenCallback.END)
                            .start(tweenManager);
                } else {
                    if ((tween.getCurrentTime() < tween.getDuration() / 2.0f) & (!tweenClicked)) {
                        tweenClicked = true;
                        reelSlot.setEndReel();
                        tween = tween.target(0, returnValues[1] + (1280 - (returnValues[1] % 1280)) + reelSlot.getEndReel() * 32);
                        tween = tween.setDuration((tween.getDuration() - tween.getCurrentTime()));
                        tween.start();
                    }
                }
            }
        }
    }

    private void update(float delta) {
        tweenManager.update(delta);
        reelSlot.update(delta);
    }

    @Override
    public void render(float delta) {
            update(delta);
            handleInput(delta);
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            if(isLoaded) {
                game.batch.begin();
                reelSlot.draw(game.batch);
                 game.batch.end();
            } else {
                if (Assets.inst().getProgress() < 1) {
                    Assets.inst().update();
                } else {
                    isLoaded = true;
                }
            }
            stage.draw();

    }

    @Override
    public void resize(int width, int height) {

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
