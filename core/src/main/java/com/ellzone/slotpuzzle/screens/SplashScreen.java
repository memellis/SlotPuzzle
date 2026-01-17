/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ellzone.slotpuzzle.SlotPuzzleGame;
import com.ellzone.slotpuzzle.effects.SpriteAccessor;
import com.ellzone.slotpuzzle.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle.utils.assets.Assets;
import com.ellzone.slotpuzzle.utils.assets.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import com.ellzone.slotpuzzle.tweenengine.BaseTween;
import com.ellzone.slotpuzzle.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Quint;

public class SplashScreen implements Screen {
    private static final int PX_PER_METER = 400;
    private final SlotPuzzleGame game;
    private final OrthographicCamera camera = new OrthographicCamera();
    private float wpw;
    private float wph;
    boolean isLoaded;
    private Sprite slot;
    private Sprite puzzle;
    private Sprite universal;
    private Sprite tween;
    private Sprite engine;
    private Sprite logo;
    private Sprite strip;
    private Sprite powered;
    private Sprite gdx;
    private Sprite veil;
    private TextureRegion gdxTex;
    private boolean endOfSplashScreen = false;
    private enum NextScreen {
        LOADING_SCREEN,
        SPLASH_SCREEN,
        INTRO_SCREEN,
        PLAY_SCREEN,
        END_OF_GAME_SCREEN,
        CREDITS_SCREEN
    }

    private NextScreen nextScreen;

    public SplashScreen(SlotPuzzleGame game) {
        this.game = game;
        defineSplashScreen();
    }

    private void defineSplashScreen() {
        initialiseSplashScreen();
        initialiseTweenEngine();
        initialiseCamera();
        createSprites(game.annotationAssetManager);
        createSplashScreenSequence();
    }

    private void initialiseSplashScreen() {
        endOfSplashScreen = false;
        nextScreen = NextScreen.INTRO_SCREEN;
        Gdx.input.setInputProcessor(splashScreenInputProcessor);
    }

    private void initialiseTweenEngine() {
        Tween.setWaypointsLimit(10);
        Tween.setCombinedAttributesLimit(3);
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
    }

    private void initialiseCamera() {
        wpw = 1f;
        wph = wpw * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();
        camera.viewportWidth = wpw;
        camera.viewportHeight = wph;
        camera.update();
    }


    private void createSprites(AnnotationAssetManager annotationAssetManager) {
        TextureAtlas atlas = annotationAssetManager.get(AssetsAnnotation.SPLASH);
        universal = atlas.createSprite(AssetsAnnotation.UNIVERSAL);
        tween = atlas.createSprite(AssetsAnnotation.TWEEN);
        engine = atlas.createSprite(AssetsAnnotation.ENGINE);
        logo = atlas.createSprite(AssetsAnnotation.LOGO);
        strip = atlas.createSprite(AssetsAnnotation.WHITE);
        powered = atlas.createSprite(AssetsAnnotation.POWERED);
        gdx = atlas.createSprite(AssetsAnnotation.GDXBLUR);
        veil = atlas.createSprite(AssetsAnnotation.WHITE);
        gdxTex = atlas.findRegion(AssetsAnnotation.GDX);

        TextureAtlas atlas1 = annotationAssetManager.get(AssetsAnnotation.SPLASH3);
        slot = atlas1.createSprite(AssetsAnnotation.SLOT);
        puzzle = atlas1.createSprite(AssetsAnnotation.PUZZLE);

        Sprite[] sprites = new Sprite[] {slot, puzzle, universal, tween, engine, logo, powered, gdx};
        for (Sprite sprite : sprites) {
            System.out.println("sprite="+sprite);
            sprite.setSize(sprite.getWidth()/PX_PER_METER, sprite.getHeight()/PX_PER_METER);
            sprite.setOrigin(sprite.getWidth()/2, sprite.getHeight()/2);
        }

        slot.setPosition(-0.325f,  0.110f);
        puzzle.setPosition(0.020f, 0.110f);
        universal.setPosition(-0.325f, 0.028f);
        tween.setPosition(-0.320f, -0.066f);
        engine.setPosition(0.020f, -0.087f);
        logo.setPosition(0.238f, 0.022f);

        strip.setSize(wpw, wph);
        strip.setOrigin(wpw/2, wph/2);
        strip.setPosition(-wpw/2, -wph/2);

        powered.setPosition(-0.278f, -0.025f);
        gdx.setPosition(0.068f, -0.077f);

        veil.setSize(wpw, wph);
        veil.setPosition(-wpw/2, -wph/2);
        veil.setColor(1, 1, 1, 0);
    }

    private void createSplashScreenSequence() {
        Timeline.createSequence()
            .push(SlotPuzzleTween.set(slot, SpriteAccessor.POS_XY).targetRelative(-1,0))
            .push(SlotPuzzleTween.set(puzzle, SpriteAccessor.POS_XY).targetRelative(1,0))
            .push(SlotPuzzleTween.set(tween, SpriteAccessor.POS_XY).targetRelative(-1, 0))
            .push(SlotPuzzleTween.set(engine, SpriteAccessor.POS_XY).targetRelative(1, 0))
            .push(SlotPuzzleTween.set(universal, SpriteAccessor.POS_XY).targetRelative(0, 0.5f))
            .push(SlotPuzzleTween.set(logo, SpriteAccessor.SCALE_XY).target(7, 7))
            .push(SlotPuzzleTween.set(logo, SpriteAccessor.OPACITY).target(0))
            .push(SlotPuzzleTween.set(strip, SpriteAccessor.SCALE_XY).target(1, 0))
            .push(SlotPuzzleTween.set(powered, SpriteAccessor.OPACITY).target(0))
            .push(SlotPuzzleTween.set(gdx, SpriteAccessor.OPACITY).target(0))

            .pushPause(0.5f)
            .push(SlotPuzzleTween.to(strip, SpriteAccessor.SCALE_XY, 0.8f).target(1, 0.6f).ease(Back.OUT))
            .push(SlotPuzzleTween.to(slot, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Quart.OUT))
            .push(SlotPuzzleTween.to(puzzle, SpriteAccessor.POS_XY, 0.5f).targetRelative(-1, 0).ease(Quart.OUT))
            .push(SlotPuzzleTween.to(tween, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Quart.OUT))
            .push(SlotPuzzleTween.to(engine, SpriteAccessor.POS_XY, 0.5f).targetRelative(-1, 0).ease(Quart.OUT))
            .push(SlotPuzzleTween.to(universal, SpriteAccessor.POS_XY, 0.6f).targetRelative(0, -0.5f).ease(Quint.OUT))
            .pushPause(-0.3f)
            .beginParallel()
            .push(SlotPuzzleTween.set(logo, SpriteAccessor.OPACITY).target(1))
            .push(SlotPuzzleTween.to(logo, SpriteAccessor.SCALE_XY, 0.5f).target(1, 1).ease(Back.OUT))
            .end()
            .pushPause(0.3f)
            .push(SlotPuzzleTween.to(slot, SpriteAccessor.SCALE_XY, 0.6f).waypoint(1.6f, 0.4f).target(1.2f, 1.2f).ease(Cubic.OUT))
            .push(SlotPuzzleTween.to(puzzle, SpriteAccessor.SCALE_XY, 0.6f).waypoint(1.6f, 0.4f).target(1.2f, 1.2f).ease(Cubic.OUT))
            .pushPause(0.3f)
            .push(SlotPuzzleTween.to(strip, SpriteAccessor.SCALE_XY, 0.5f).target(1, 1).ease(Back.IN))
            .pushPause(0.3f)
            .beginParallel()
            .push(SlotPuzzleTween.to(slot, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
            .push(SlotPuzzleTween.to(puzzle, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
            .push(SlotPuzzleTween.to(tween, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
            .push(SlotPuzzleTween.to(engine, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
            .push(SlotPuzzleTween.to(universal, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
            .push(SlotPuzzleTween.to(logo, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
            .end()

            .pushPause(-0.3f)
            .push(SlotPuzzleTween.set(slot, SpriteAccessor.POS_XY).targetRelative(-1,0))
            .push(SlotPuzzleTween.set(puzzle, SpriteAccessor.POS_XY).targetRelative(1,0))
            .pushPause(0.5f)
            .push(SlotPuzzleTween.to(slot, SpriteAccessor.POS_XY, 0.5f).targetRelative(-1, 0).ease(Quart.OUT))
            .push(SlotPuzzleTween.to(puzzle, SpriteAccessor.POS_XY, 0.5f).targetRelative(-1, 0).ease(Quart.OUT))
            .pushPause(0.3f)
            .push(SlotPuzzleTween.to(slot, SpriteAccessor.SCALE_XY, 0.6f).waypoint(1.6f, 0.4f).target(1.2f, 1.2f).ease(Cubic.OUT))
            .push(SlotPuzzleTween.to(puzzle, SpriteAccessor.SCALE_XY, 0.6f).waypoint(1.6f, 0.4f).target(1.2f, 1.2f).ease(Cubic.OUT))
            .pushPause(-0.3f)
            .push(SlotPuzzleTween.to(powered, SpriteAccessor.OPACITY, 0.3f).target(1))
            .beginParallel()
            .push(SlotPuzzleTween.to(gdx, SpriteAccessor.OPACITY, 1.5f).target(1).ease(Cubic.IN))
            .push(SlotPuzzleTween.to(gdx, SpriteAccessor.ROTATION, 2.0f).target(360*15).ease(Quad.OUT))
            .end()
            .pushPause(0.3f)
            .push(SlotPuzzleTween.to(gdx, SpriteAccessor.SCALE_XY, 0.6f).waypoint(1.6f, 0.4f).target(1.2f, 1.2f).ease(Cubic.OUT))
            .pushPause(0.3f)
            .beginParallel()
            .push(SlotPuzzleTween.to(powered, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
            .push(SlotPuzzleTween.to(gdx, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
            .end()
            .pushPause(0.3f)
            .setCallback(new TweenCallback() {
                @Override
                public void onEvent(int arg0, BaseTween<?> arg1) {
                    game.setScreen(new IntroScreen(game));
                }})
            .start(game.getTweenManager());
    }

    @Override
    public void show() {
    }

    private void update(float dt) {
        game.getTweenManager().update(dt);
        if (gdx.getRotation() > 360*15-20) gdx.setRegion(gdxTex);
        if (endOfSplashScreen) {
            switch (nextScreen) {
                case LOADING_SCREEN:
                    game.setScreen(new LoadingScreen(game));
                    break;
                case SPLASH_SCREEN:
                    game.setScreen(new SplashScreen(game));
                    break;
                case PLAY_SCREEN:
                    game.setScreen(new PlayScreen(game, null, null));
                    break;
                case END_OF_GAME_SCREEN:
                    game.setScreen(new EndOfGameScreen(game));
                    break;
                case CREDITS_SCREEN:
                    game.setScreen(new CreditsScreen(game));
                    break;
                default:
                    game.setScreen(new IntroScreen(game));
                    break;
            }
            dispose();
        }
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(isLoaded) {
            game.batch.setProjectionMatrix(camera.combined);
            game.batch.begin();
            strip.draw(game.batch);
            slot.draw(game.batch);
            puzzle.draw(game.batch);
            universal.draw(game.batch);
            tween.draw(game.batch);
            engine.draw(game.batch);
            logo.draw(game.batch);
            powered.draw(game.batch);
            gdx.draw(game.batch);
            if (veil.getColor().a > 0.1f) veil.draw(game.batch);
            game.batch.end();
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
        game.getTweenManager().killAll();
    }

    private final InputProcessor splashScreenInputProcessor = new InputAdapter() {
        @Override
        public boolean touchDown (int x, int y, int pointer, int button) {
            endOfSplashScreen = true;
            return true;
        }
        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Keys.L:
                    nextScreen = NextScreen.LOADING_SCREEN;
                    break;
                case Keys.S:
                    nextScreen = NextScreen.SPLASH_SCREEN;
                    break;
                case Keys.P:
                    nextScreen = NextScreen.PLAY_SCREEN;
                    break;
                case Keys.C:
                    nextScreen = NextScreen.CREDITS_SCREEN;
                    break;
                case Keys.E:
                    nextScreen = NextScreen.END_OF_GAME_SCREEN;
                    break;
                default:
                    nextScreen = NextScreen.INTRO_SCREEN;
                    break;
            }
            endOfSplashScreen = true;
            return true;
        }
    };
}
