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

package com.ellzone.slotpuzzle2d.prototypes.tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Quad;

public class GameOverPopUp extends SPPrototype {

    private static final float MINIMUM_VIEWPORT_SIZE = 15.0f;
    private PerspectiveCamera cam;
    private SpriteBatch batch;
    private Sprite cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato;
    private Sprite gameOverPopUp, game, over;
    private Sprite[] sprites;
    private TweenManager tweenManager;
    private BitmapFont font;

    @Override
    public void create() {
        loadAssets();
        initialiseCamera();
        initialiseLibGdx();
        initialiseUniversalTweenEngine();
        createGameOverPopUp();
    }

    private void loadAssets() {
        Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
        Assets.inst().load("tiles/tiles.pack.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();

        TextureAtlas reelAtlas = Assets.inst().get("reel/reels.pack.atlas", TextureAtlas.class);
        cherry = reelAtlas.createSprite("cherry");
        cheesecake = reelAtlas.createSprite("cheesecake");
        grapes = reelAtlas.createSprite("grapes");
        jelly = reelAtlas.createSprite("jelly");
        lemon = reelAtlas.createSprite("lemon");
        peach = reelAtlas.createSprite("peach");
        pear = reelAtlas.createSprite("pear");
        tomato = reelAtlas.createSprite("tomato");

        TextureAtlas tilesAtlas = Assets.inst().get("tiles/tiles.pack.atlas", TextureAtlas.class);
        gameOverPopUp = tilesAtlas.createSprite("GamePopUp");
        game = tilesAtlas.createSprite("game");
        over = tilesAtlas.createSprite("over");

        gameOverPopUp.setPosition(Gdx.graphics.getWidth() / 2 - gameOverPopUp.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOverPopUp.getHeight() /2);
        game.setPosition(-200, Gdx.graphics.getHeight() / 2 - game.getHeight() /2);
        over.setPosition(200 + Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2 - over.getHeight() /2);

        int i = 0;
        sprites = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
        for (Sprite sprite : sprites) {
            sprite.setOrigin(0, 0);
            sprite.setPosition(192 + i * sprite.getWidth(), Gdx.graphics.getHeight() / 2 - sprite.getHeight() / 2);
            i++;
        }
    }

    private void initialiseCamera() {
        cam = new PerspectiveCamera();
        cam.position.set(0, 0, 10);
        cam.lookAt(0, 0, 0);
        cam.update();
    }

    private void initialiseLibGdx() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        Gdx.input.setInputProcessor(inputProcessor);
    }

    private void initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        tweenManager = new TweenManager();
    }

    private void createGameOverPopUp() {
        Timeline.createSequence()
                .push(SlotPuzzleTween.set(gameOverPopUp, SpriteAccessor.SCALE_XY).target(0.1f, 0))
                .push(SlotPuzzleTween.set(game, SpriteAccessor.POS_XY). target(-200, Gdx.graphics.getHeight() / 2 - game.getHeight() /2))
                .push(SlotPuzzleTween.set(over, SpriteAccessor.POS_XY). target(200 + Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2 - over.getHeight() /2))
                .pushPause(0.5f)
                .push(SlotPuzzleTween.to(gameOverPopUp, SpriteAccessor.SCALE_XY, 0.8f).target(1, 0.6f).ease(Back.OUT))
                .pushPause(-0.3f)
                .pushPause(0.3f)
                .pushPause(0.3f)
                .beginParallel()
                .push(SlotPuzzleTween.to(gameOverPopUp, SpriteAccessor.SCALE_XY, 0.5f).target(1.1f, 1.1f).ease(Back.IN))
                .push(SlotPuzzleTween.to(gameOverPopUp, SpriteAccessor.OPACITY, 0.5f).target(0.7f).ease(Back.IN))
                .end()
                .pushPause(0.3f)
                .pushPause(-0.3f)
                .beginParallel()
                .push(SlotPuzzleTween.to(game, SpriteAccessor.POS_XY, 1.0f).target(Gdx.graphics.getWidth() / 2 - game.getWidth(), Gdx.graphics.getHeight() / 2 - game.getHeight() /2).ease(Back.INOUT))
                .push(SlotPuzzleTween.to(over, SpriteAccessor.POS_XY, 1.0f).target(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - over.getHeight() /2).ease(Back.INOUT))
                .end()
                .pushPause(0.5f)
                .start(tweenManager);
    }

    private void hideGameOverPopUp() {
        Timeline.createSequence()
                .pushPause(0.25f)
                .beginParallel()
                .push((SlotPuzzleTween.to(gameOverPopUp, SpriteAccessor.POS_XY, 1.5f)
                        .waypoint(Gdx.graphics.getWidth() / 2 - gameOverPopUp.getWidth() / 2, Gdx.graphics.getHeight() / 2 - gameOverPopUp.getHeight() / 2 + 64)
                        .target(Gdx.graphics.getWidth() / 2 - gameOverPopUp.getWidth() / 2, -300).ease(Quad.OUT)))
                .push((SlotPuzzleTween.to(game, SpriteAccessor.POS_XY, 1.5f)
                        .waypoint(Gdx.graphics.getWidth() / 2 - game.getWidth(), Gdx.graphics.getHeight() / 2 - game.getHeight() /2 + 64)
                        .target(Gdx.graphics.getWidth() / 2 - game.getWidth(), -300).ease(Quad.OUT)))
                .push((SlotPuzzleTween.to(over, SpriteAccessor.POS_XY, 1.5f)
                        .waypoint(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2 - over.getHeight() / 2 + 64)
                        .target(Gdx.graphics.getWidth() / 2, -300).ease(Quad.OUT)))
                .end()
                .pushPause(0.5f)
                .start(tweenManager);
    }

    @Override
    public void resize(int width, int height) {
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

    private void update(float delta) {
        tweenManager.update(delta);
    }

    @Override
    public void render() {
        final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        update(delta);
        batch.begin();
        for (Sprite sprite : sprites) {
            sprite.draw(batch);
        }
        gameOverPopUp.draw(batch);
        game.draw(batch);
        over.draw(batch);
        font.draw(batch, "Touch/Click to try again", game.getX() + 32, game.getY() - 32);
        batch.end();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        for (Sprite sprite : sprites) {
            sprite.getTexture().dispose();
        }
        font.dispose();
        Assets.inst().dispose();
    }

    private final InputProcessor inputProcessor = new InputAdapter() {
        @Override
        public boolean touchUp(int x, int y, int pointer, int button) {
            tweenManager.killAll();
            hideGameOverPopUp();
            return true;
        }
    };
}