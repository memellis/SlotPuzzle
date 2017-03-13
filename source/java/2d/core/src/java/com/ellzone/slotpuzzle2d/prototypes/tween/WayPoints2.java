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
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import aurelienribon.tweenengine.TweenPaths;

public class WayPoints2 extends SPPrototype {

    private static final float MINIMUM_VIEWPORT_SIZE = 15.0f;
    private PerspectiveCamera cam;
    private Sprite cheesecake;
    private Sprite cherry;
    private Sprite grapes;
    private Sprite jelly;
    private Sprite lemon;
    private Sprite peach;
    private Sprite pear;
    private Sprite tomato;
    private Sprite[] sprites;
    private int spriteWidth;
    private int spriteHeight;
    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private Random random;
    private ReelTile reelTile;
    private Array<ReelTile> reelTiles;
    private TweenManager tweenManager;
    private SpriteBatch batch;
    private float tweenDuration;

    @Override
    public void create() {
        initialiseUniversalTweenEngine();
        loadAssets();
        initialiseReelSlots();
        initialiseTweens();
        initialiseCamera();
        batch = new SpriteBatch();
    }

    private void initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        tweenManager = new TweenManager();
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

    private void initialiseReelSlots() {
        random = new Random();
        reelTiles = new Array<ReelTile>();
        slotReelScrollPixmap = new Pixmap(spriteWidth, spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        reelTile = new ReelTile(slotReelScrollTexture, 0, 32, spriteWidth, spriteHeight, 0);
        reelTile.setX(0);
        reelTile.setY(0);
        reelTile.setEndReel(random.nextInt(sprites.length));
        reelTiles.add(reelTile);
    }

    private void initialiseTweens() {
        tweenDuration = 10.0f;
        SlotPuzzleTween.set(reelTiles.get(0), ReelAccessor.SCROLL_XY)
                .setDuration(tweenDuration)
                .target(0, reelTile.getEndReel() * 32)
                .waypoint(0,  slotReelScrollTexture.getHeight()*4)
                .waypoint(0, -slotReelScrollTexture.getHeight()*8)
                .waypoint(0,  slotReelScrollTexture.getHeight()*10)
                .waypoint(0, -slotReelScrollTexture.getHeight()*12 + reelTile.getEndReel() * 32)
                .waypoint(0,  reelTile.getEndReel() * 32 + 16)
                .waypoint(0,  reelTile.getEndReel() * 32 - 15)
                .waypoint(0,  reelTile.getEndReel() * 32 + 8)
                .waypoint(0,  reelTile.getEndReel() * 32 - 8)
                .waypoint(0,  reelTile.getEndReel() * 32)
                .path(TweenPaths.catmullRom)
                .start(tweenManager);
    }

    private void initialiseCamera() {
        cam = new PerspectiveCamera();
        cam.position.set(0, 0, 10);
        cam.lookAt(0, 0, 0);
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
        for(ReelTile reelTile : reelTiles) {
            reelTile.update(delta);
        }
    }

    @Override
    public void render() {
        final float delta = Math.min(1/60f, Gdx.graphics.getDeltaTime());
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin();
        for (ReelTile reelTile : reelTiles) {
            reelTile.draw(batch);
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