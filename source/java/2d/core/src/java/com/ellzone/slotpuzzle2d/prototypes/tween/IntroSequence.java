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

import java.util.Random;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;

public class IntroSequence extends SPPrototype {

    private static final float MINIMUM_VIEWPORT_SIZE = 15.0f;
    private PerspectiveCamera cam;
    private SpriteBatch batch;
    private Sprite cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato;
    private Sprite[] sprites;
    private int spriteWidth;
    private int spriteHeight;
    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private Random random;
    private Array<ReelTile> reels;
    private Timeline introSequence;
    private TweenManager tweenManager;

    @Override
    public void create() {
        loadAssets();
        initialiseCamera();
        initialiseLibGdx();
        initialiseUniversalTweenEngine();
        initialiseReelSlots();
        createIntroSequence();
    }

    private void loadAssets() {
        Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();

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
        spriteWidth = (int) sprites[0].getWidth();
        spriteHeight = (int) sprites[0].getHeight();
    }

    private void initialiseCamera() {
        cam = new PerspectiveCamera();
        cam.position.set(0, 0, 10);
        cam.lookAt(0, 0, 0);
    }

    private void initialiseLibGdx() {
        batch = new SpriteBatch();
    }

    private void initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        tweenManager = new TweenManager();
    }

    private void initialiseReelSlots() {
        random = new Random();
        reels = new Array<ReelTile>();
        slotReelScrollPixmap = new Pixmap(spriteWidth, spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        for (int i=0; i<15; i++) {
            ReelTile reel = new ReelTile(slotReelScrollTexture, 0, 0, spriteWidth, spriteHeight, 0);
            reel.setX(i*spriteHeight);
            reel.setY(i*spriteWidth);
            reel.setSx(0);
            reel.setSy(0);
            reel.setEndReel(random.nextInt(sprites.length - 1));
            reels.add(reel);
        }
    }

    private void createIntroSequence() {
        introSequence = Timeline.createParallel();
        for(int i=0; i < reels.size; i++) {
            introSequence = introSequence
                    .push(buildSequence(reels.get(i), i, random.nextFloat() * 5.0f, random.nextFloat() * 5.0f));
        }

        introSequence = introSequence
                .pushPause(0.3f)
                .start(tweenManager);
    }

    private Timeline buildSequence(Sprite target, int id, float delay1, float delay2) {
        Vector2 targetXY = getRandomCorner();
        return Timeline.createSequence()
                .push(SlotPuzzleTween.set(target, SpriteAccessor.POS_XY).target(targetXY.x, targetXY.y))
                .push(SlotPuzzleTween.set(target, SpriteAccessor.SCALE_XY).target(30, 30))
                .push(SlotPuzzleTween.set(target, SpriteAccessor.ROTATION).target(0))
                .push(SlotPuzzleTween.set(target, SpriteAccessor.OPACITY).target(0))
                .pushPause(delay1)
                .beginParallel()
                .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
                .end()
                .pushPause(-0.5f)
                .push(SlotPuzzleTween.to(target, SpriteAccessor.POS_XY, 1.0f).target(id * spriteWidth, id * spriteHeight).ease(Back.OUT))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.ROTATION, 0.8f).target(360).ease(Cubic.INOUT))
                .pushPause(delay2)
                .beginParallel()
                .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 0.3f).target(3, 3).ease(Quad.IN))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 0.3f).target(0).ease(Quad.IN))
                .end()
                .pushPause(-0.5f)
                .beginParallel()
                .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
                .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
                .end();
    }

    private Vector2 getRandomCorner() {
        int randomCorner = random.nextInt(4);
        switch (randomCorner) {
            case 0:
                return new Vector2(-1 * random.nextFloat(), -1 * random.nextFloat());
            case 1:
                return new Vector2(-1 * random.nextFloat(), 800 + random.nextFloat());
            case 2:
                return new Vector2(480 + random.nextFloat(), -1 * random.nextFloat());
            case 3:
                return new Vector2(480 + random.nextFloat(), 800 + random.nextFloat());
            default:
                return new Vector2(-0.5f, -0.5f);
        }
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
        for (ReelTile reel : reels) {
            reel.update(delta);
        }
    }

    @Override
    public void render() {
        final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin();
        for (ReelTile reel : reels) {
            reel.draw(batch);
            sprites[reel.getEndReel()].setX(32);
            sprites[reel.getEndReel()].draw(batch);
        }
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
        Assets.inst().dispose();
    }
}
