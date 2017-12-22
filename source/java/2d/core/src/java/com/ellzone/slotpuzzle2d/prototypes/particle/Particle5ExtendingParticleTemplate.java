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

import com.ellzone.slotpuzzle2d.physics.DampenedSine;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsEvent;
import com.ellzone.slotpuzzle2d.sprites.Reels;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import java.util.Random;
import com.ellzone.slotpuzzle2d.sprites.ReelTiles;
import com.ellzone.slotpuzzle2d.physics.Particles;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.badlogic.gdx.math.Vector2;
import com.ellzone.slotpuzzle2d.physics.Particle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class Particle5ExtendingParticleTemplate extends ParticleTemplate {
	private Array<DampenedSine> dampenedSines;
	private DampenedSine dampenedSine;
	private float graphStep;
	private Reels reels;
	private Sprite[] reelSprites;
	private ShapeRenderer shapeRenderer;
	private Random random;
	private ReelTiles reelTiles;
	private Array<ReelTile> reelTilesArray;
	private int slotReelScrollheight;
	private Particles particles;
	private Array<Particle> reelParticles;
	private Array<Vector2> points = new Array<Vector2>();
	private Vector accelerator;
	private int dampPoint;
	private Vector2 touch;

	@Override
	protected void initialiseOverride() {
		initialiseReelTiles(annotationAssetManager);
		initialiseParticles();
        initialiseDampenedSine();
		shapeRenderer = new ShapeRenderer();
		random = new Random();
		touch = new Vector2();
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
	
	private void initialiseDampenedSine() {
        dampenedSines = new Array<DampenedSine>();
        for (ReelTile reel : reelTilesArray) {
            dampenedSine = new DampenedSine(0, reel.getSy(), 0, 0, 0, slotReelScrollheight * 20, slotReelScrollheight, reel.getEndReel());
            dampenedSine.setCallback(dsCallback);
            dampenedSine.setCallbackTriggers(SPPhysicsCallback.PARTICLE_UPDATE + SPPhysicsCallback.END);
            dampenedSine.setUserData(reel);
            dampenedSines.add(dampenedSine);
        }
        graphStep = 0f;
    }

    private SPPhysicsCallback dsCallback = new SPPhysicsCallback() {
        @Override
        public void onEvent(int type, SPPhysicsEvent source) {
            delegateDSCallback(type, source);
        }
    };

    private void delegateDSCallback(int type, SPPhysicsEvent source) {
        if (type == SPPhysicsCallback.PARTICLE_UPDATE) {
            addGraphPoint(new Vector2(graphStep++ % displayWindowWidth, (displayWindowHeight / 2 + dampenedSines.get(0).dsEndReel)));
        } else {
            if (type == SPPhysicsCallback.END) {
                DampenedSine ds = (DampenedSine)source.getSource();
                ReelTile reel = (ReelTile)ds.getUserData();
                reel.setEndReel(random.nextInt(reelSprites.length - 1));
                ds.position.y = 0;
                ds.initialiseDampenedSine();
                ds.setEndReel(reel.getEndReel());
            }
        }
    }

    private void addGraphPoint(Vector2 newPoint) {
        points.add(newPoint);
    }
	
	private void drawGraphPoint(ShapeRenderer shapeRenderer) {
        if (points.size >= 2) {
            shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.YELLOW);
            for (int i = 0; i < points.size - 1; i++) {
                shapeRenderer.line(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            }
            shapeRenderer.end();
        }
    }

	public void handleInput(float delta) {
        int dsIndex = 0;
        for (ReelTile reel : reelTilesArray) {
            if (Gdx.input.justTouched()) {
                touch = touch.set(Gdx.input.getX(), cam.viewportHeight - Gdx.input.getY());
                if(reel.getBoundingRectangle().contains(touch)) {
                    if (dampenedSines.get(dsIndex).getDSState() == DampenedSine.DSState.UPDATING_DAMPENED_SINE) {
                        reel.setEndReel(reel.getCurrentReel());
                        dampenedSines.get(dsIndex).setEndReel(reel.getCurrentReel());
                    }
                }
            }
            dsIndex++;
        }
    }
	
	@Override
	protected void disposeOverride() {
	}

	@Override
	protected void updateOverride(float delta) {
		int dsIndex = 0;
        for (ReelTile reelTile : reelTilesArray) {
            dampenedSines.get(dsIndex).update();
            reelTile.setSy(dampenedSines.get(dsIndex).position.y);
            reelTile.update(delta);
            dsIndex++;
        }
		handleInput(delta);
	}

	@Override
	protected void renderOverride(float delta) {
		batch.begin();
        for (ReelTile reelTile : reelTilesArray) {
            reelTile.draw(batch);
            reelSprites[reelTile.getEndReel()].setX(32);
			reelSprites[reelTile.getEndReel()].draw(batch);
		}
        batch.end();
        drawGraphPoint(shapeRenderer);
	}

	@Override
	protected void initialiseUniversalTweenEngineOverride() {
	}	
}
