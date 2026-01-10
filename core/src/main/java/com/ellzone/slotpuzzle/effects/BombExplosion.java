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

package com.ellzone.slotpuzzle.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.graphics.ParticleEmitterBox2D;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle.SlotPuzzleConstants;
import com.ellzone.slotpuzzle.level.creator.LevelCallback;
import com.ellzone.slotpuzzle.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle.utils.assets.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class BombExplosion {
    private final int MAX_NBR = 30;
    public static final int BOMB_REEL = 8;

    private AnnotationAssetManager annotationAssetManager;
    private final World world;
    private TextureAtlas bombAtlas;
    private Array<ParticleEffect> explosionEffects;
    private ParticleEffectPool bombExplosionPool;
    private Array<Animation<TextureRegion>> bombAnimations;
    private Array<ReelTileGridValue> bombFuses;
    private float stateTime;
    private LevelCallback deleteReelCallback;
    private Array<AnimatedReel> animatedReels;

    public BombExplosion(
        AnnotationAssetManager annotationAssetManager,
        World world) {
        this.annotationAssetManager = annotationAssetManager;
        this.world = world;
    }

    public void initialise() {
        getAssets();
        explosionGenerator();
    }

    private void getAssets() {
        bombAtlas = annotationAssetManager.get(AssetsAnnotation.BOMB_ANIMATION);
    }

    private void explosionGenerator() {
        explosionEffects = new Array<>();
        TextureAtlas textureAtlas = new TextureAtlas();
        textureAtlas.addRegion(
            "particle",
            new TextureRegion(new Texture("box2d_particle_effects/particle.png")));
        ParticleEffect explosionEffect = new ParticleEffect();
        explosionEffect.load(Gdx.files.internal("bomb/particle_explosion.p"), textureAtlas);
        bombExplosionPool = new ParticleEffectPool(explosionEffect,MAX_NBR*2,  MAX_NBR*2);
        addBombAnimation();
    }

    private void addBombAnimation() {
        bombAnimations = new Array<Animation<TextureRegion>>();
        bombAnimations.add(
            new Animation<TextureRegion>(
                0.1f,
                bombAtlas.findRegions("bomb"),
                Animation.PlayMode.LOOP));
        bombFuses = new Array<>();
    }

    public void addDeleteReelCallback(LevelCallback deleteReelCallback) {
        this.deleteReelCallback = deleteReelCallback;
    }

    public void updateBombExplosion(float dt) {
        stateTime += dt;
        for (int i = explosionEffects.size - 1; i >= 0; i--) {
            ParticleEffect explosionEffect = explosionEffects.get(i);
            explosionEffect.update(dt);

            if (explosionEffect.isComplete())
                explosionEffects.removeIndex(i);
        }
    }

    public void renderBombFuseAnimations(SpriteBatch batch) {
        batch.begin();
        for (int i = bombFuses.size - 1; i >= 0; i--) {
            drawAnimationCurrentFrame(
                batch,
                (TextureRegion) bombAnimations.get(0).getKeyFrame(stateTime, false),
                bombFuses.get(i).getReelTile().getX(),
                bombFuses.get(i).getReelTile().getY()
            );
            if (bombAnimations.get(0).isAnimationFinished(stateTime)) {
                if (i == 0) {
                    processExplosions(bombFuses);
                    bombFuses.removeRange(0, bombFuses.size - 1);
                }
            }
        }
        batch.end();
    }

    private void drawAnimationCurrentFrame(
        SpriteBatch spriteBatch, TextureRegion currentFrame, float x, float y) {
        spriteBatch.draw(currentFrame, x, y);
    }

    public void renderBombExplosions(SpriteBatch batch, Viewport viewport) {
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        for (int i = explosionEffects.size - 1; i >= 0; i--) {
            ParticleEffect explosionEffect = explosionEffects.get(i);
            explosionEffect.draw(batch);
        }
        batch.end();
    }

    private void processExplosions(Array<ReelTileGridValue> matchedSlots) {
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        Array<ReelTile> reelTiles = PuzzleGridTypeReelTile.getReelTilesFromAnimatedReels(animatedReels);
        ReelTileGridValue[][] matchGrid =
            puzzleGridTypeReelTile.populateMatchGrid(
                reelTiles,
                new GridSize(
                    SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                    SlotPuzzleConstants.GAME_LEVEL_HEIGHT)
            );
        ReelTileGridValue[][] linkGrid = puzzleGridTypeReelTile.createGridLinksWithoutMatch(matchGrid);
        Array<ReelTileGridValue> surroundingReelTiles =
            PuzzleGridTypeReelTile.getSurroundingReelTiles(matchedSlots, linkGrid);
        if (matchedSlots.size > 0)
            explodeReelsTiles(matchedSlots);
        if (surroundingReelTiles.size > 0)
            explodeReelsTiles(surroundingReelTiles);
    }

    private void explodeReelsTiles(Array<ReelTileGridValue> reelTiles) {
        for (int i = reelTiles.size - 1; i >= 0; i--) {
            ParticleEffect explosionEffect = bombExplosionPool.obtain();
            explosionEffect.getEmitters().add(
                new ParticleEmitterBox2D(world, explosionEffect.getEmitters().first()));
            explosionEffect.getEmitters().removeIndex(0);
            explosionEffect.setPosition(
                reelTiles.get(i).reelTile.getX() + reelTiles.get(i).reelTile.getWidth() / 2,
                reelTiles.get(i).reelTile.getY() + reelTiles.get(i).reelTile.getHeight() / 2);
            explosionEffect.start();
            explosionEffects.add(explosionEffect);
            if (deleteReelCallback != null)
                deleteReelCallback.onEvent(reelTiles.get(i).reelTile);
        }
    }

    public void addToBombFuseAnimation(
        Array<ReelTileGridValue> matchedSlots,
        Array<AnimatedReel> animatedReels) {
        this.animatedReels = animatedReels;
        for (ReelTileGridValue reelTileGridValue : matchedSlots) {
            if (reelTileGridValue.reelTile.getEndReel() == BOMB_REEL) {
                bombFuses.add(reelTileGridValue);
                stateTime = 0;
            }
        }
    }
}
