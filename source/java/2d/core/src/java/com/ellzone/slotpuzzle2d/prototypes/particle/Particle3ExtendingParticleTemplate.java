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

package com.ellzone.slotpuzzle2d.prototypes.particle;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ellzone.slotpuzzle2d.sprites.Reels;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTiles;
import com.ellzone.slotpuzzle2d.physics.Particles;
import com.ellzone.slotpuzzle2d.physics.Particle;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.badlogic.gdx.Gdx;
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class Particle3ExtendingParticleTemplate extends ParticleTemplate {
	private static final float VELOCITY_MIN = 2.0f;
	private Reels reels;
	private Sprite[] reelSprites;
	private ReelTiles reelTiles;
	private Array<ReelTile> reelTilesArray;
	private Particles particles;
	private Array<Particle> reelParticles;
	private int dampPoint;
	private Array<Vector2> points = new Array<Vector2>();
	private Particle reelParticle;
	private Vector accelerator;
    private float graphStep;
    private float savedAmplitude;
    private float savedSy;
    private boolean saveAmplitude;
	private float plotTime;
	private ShapeRenderer shapeRenderer;
	private float slotReelScrollheight;
	
	@Override
	protected void initialiseOverride() {
		initialiseReelTiles(annotationAssetManager);
		initialiseParticles();
        initialiseDampenedSine();
		shapeRenderer = new ShapeRenderer();

		Gdx.app.log("Slot_Puzzle", "displayWindowWidth"+displayWindowWidth);
	}
	
	@Override
    protected void loadAssetsOverride(AnnotationAssetManager annotationAssetManager) {
	}
	
	private void initialiseReelTiles(AnnotationAssetManager annotationAssetManager) {
        reels = new Reels(annotationAssetManager);
        reelSprites = reels.getReels();
        reelTiles = new ReelTiles(reels);
		reelTilesArray = reelTiles.getReelTiles();
		slotReelScrollheight = reelTiles.getReelTileTextureHeight();
    }
	
	private void initialiseParticles() {
		particles = new Particles(reels, reelTiles);
		reelParticles = particles.getParticles();
		accelerator = particles.getAccelerator();
		dampPoint = particles.getDampoint();
	}
	
	private void reinitialiseParticle(int index) {
        reelTilesArray.get(0).setSy(0);
        accelerator = new Vector(0, 3f);
        reelParticles.removeIndex(0);
        reelParticle = new Particle(0, reelTilesArray.get(0).getSy(), 0.0001f , 0, 0);
        reelParticle.velocity.setX(0);
        reelParticle.velocity.setY(4);
        reelParticle.accelerate(new Vector(0, 2f));
        reelParticles.add(reelParticle);
        dampPoint = reelTiles.getReelTileTextureHeight() * 20;
        saveAmplitude = true;
        savedAmplitude = 0;
        plotTime = 132;
    }
	
	private float dampenedSine(float initialAmplitude, float lambda, float angularFrequency, float time, float phaseAngle) {
        return (float) (initialAmplitude * Math.exp(-lambda * time) *  Math.cos(angularFrequency * time + phaseAngle));
    }
	
	private void initialiseDampenedSine() {
        graphStep = 0;
        savedAmplitude = 0;
        saveAmplitude = true;
        plotTime = 132;
    }
	
	@Override
	protected void disposeOverride() {
	}

	@Override
	protected void updateOverride(float delta) {
        for (Particle reelParticle : reelParticles) {
            reelParticle.update();
        }
        for(ReelTile reelSlot : reelTilesArray) {
            if (reelParticles.get(0).velocity.getY() > VELOCITY_MIN) {
                // Particle3.VELOCITY.MIN has a major influence on the endReel
                // Current setting means the natural endReel is 4 (lemon)
                reelParticles.get(0).velocity.mulitplyBy(0.97f);
                reelParticles.get(0).accelerate(accelerator);
                accelerator.mulitplyBy(0.97f);
                reelSlot.setSy(reelParticles.get(0).position.getY());
            } else {
                if (reelSlot.getSy() < dampPoint) {
                    if (saveAmplitude) {
                        saveAmplitude = false;
                        savedSy = reelSlot.getSy() + reelTiles.getReelTileTextureHeight() - (reelSlot.getSy() % slotReelScrollheight);
                        savedAmplitude = (dampPoint - savedSy);
                    }
                    float ds = dampenedSine(savedAmplitude, 1.0f, (float) (3 * Math.PI), plotTime++ / 32, 0);
                    float dsEndReel = ds + reelSlot.getEndReel() * 32;
                    addGraphPoint(new Vector2(graphStep++ % displayWindowWidth, (displayWindowHeight / 2 + dsEndReel)));
                    reelSlot.setSy(savedSy + dsEndReel);
                    if(Math.abs(ds)<0.0000001f) {
                        reinitialiseParticle(0);
                        reelSlot.setEndReel(com.ellzone.slotpuzzle2d.utils.Random.getInstance().nextInt(reels.getReels().length - 1));
                    }
                }
            }
            reelSlot.update(delta);
        }
	}
	
	private void addGraphPoint(Vector2 newPoint) {
        points.add(newPoint);
    }

    private void drawGraphPoint(ShapeRenderer shapeRenderer) {
        if (points.size >= 2) {
			shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            int rr = Random.getInstance().nextInt(255);
            int rg = Random.getInstance().nextInt(255);
            int rb = Random.getInstance().nextInt(255);
            shapeRenderer.setColor(rr, rg, rb, 255);
            for (int i = 0; i < points.size - 1; i++) {
                shapeRenderer.line(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            }
            shapeRenderer.end();
        }
    }

	@Override
	protected void renderOverride(float delta) {
		batch.begin();
		for (ReelTile reelSlot : reelTilesArray) {
			reelSlot.draw(batch);
			reelSprites[reelSlot.getEndReel()].setX(32);
			reelSprites[reelSlot.getEndReel()].draw(batch);
		}
		batch.end();
		drawGraphPoint(shapeRenderer);
	}

	@Override
	protected void initialiseUniversalTweenEngineOverride() {
	}
}
