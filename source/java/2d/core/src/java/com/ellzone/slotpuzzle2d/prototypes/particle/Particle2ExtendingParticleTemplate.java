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
import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.prototypes.Reels;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.physics.Particle;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ellzone.slotpuzzle2d.prototypes.*;

public class Particle2ExtendingParticleTemplate extends ParticleTemplate {
    private static final float VELOCITY_MIN = 2.0f;
	private Reels reels;
	private Sprite[] reelSprites;
    private ReelTiles reelTiles;
	private Array<ReelTile> reelTilesArray;
	private Vector accelerator;
	private Array<Particle> reelParticles;
	private Particles particles;
	private Array<Vector2> points = new Array<Vector2>();
    private float graphStep;
    private float savedAmplitude;
    private float savedSy;
    private boolean saveAmplitude;
    private float plotTime;
	private int dampPoint;
	private ShapeRenderer shapeRenderer;
	
	@Override
	protected void initialiseOverride() {
		initialiseReelTiles();
		intialiseParticles();
		shapeRenderer = new ShapeRenderer();
        graphStep = 0;
        savedAmplitude = 0;
        saveAmplitude = true;
        plotTime = 132;
	}

	@Override
	protected void loadAssetsOverride() {
		reels = new Reels();
        reelSprites = reels.getReels();
	}

	private void initialiseReelTiles() {
        reelTiles = new ReelTiles(reels);
		reelTilesArray = reelTiles.getReelTiles();
    }

	private void intialiseParticles() {
		particles = new Particles(reels, reelTiles);
		reelParticles = particles.getParticles();
		accelerator = particles.getAccelerator();
		dampPoint = particles.getDampoint();
	}
	
	private float dampenedSine(float initialAmplitude, float lambda, float angularFrequency, float time, float phaseAngle) {
        return (float) (initialAmplitude * Math.exp(-lambda * time) *  Math.cos(angularFrequency * time + phaseAngle));
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
                reelParticles.get(0).velocity.mulitplyBy(0.97f);
                reelParticles.get(0).accelerate(accelerator);
                accelerator.mulitplyBy(0.97f);
                reelSlot.setSy(reelParticles.get(0).position.getY());
            } else {
                if (reelSlot.getSy() < dampPoint) {
                    if (saveAmplitude) {
                        saveAmplitude = false;
                        savedSy = reelSlot.getSy();
                        savedAmplitude = (dampPoint - savedSy);
                    }
                    float ds = dampenedSine(savedAmplitude, 1.0f, (float) (2 * Math.PI), plotTime++/30, 0);
                    reelSlot.setSy(savedSy+ds);
                }
            }
            reelSlot.update(delta);
        }
	}

	private void drawGraphPoint(ShapeRenderer shapeRenderer, Vector2 newPoint) {
        if (points.size >= 2) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0, 255, 255, 255);
            for (int i = 0; i < points.size - 1; i++) {
                shapeRenderer.line(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            }
            shapeRenderer.end();
        }
        points.add(newPoint);
    }
	
	@Override
	protected void renderOverride(float delta) {
		batch.begin();
        for (ReelTile reelSlot : reelTilesArray) {
            reelSlot.draw(batch);
        }
        batch.end();
        float dsine = dampenedSine(savedAmplitude, 1.0f, (float) (2 * Math.PI), graphStep / 75, (float) Math.PI/2);
        drawGraphPoint(shapeRenderer, new Vector2(graphStep % Gdx.graphics.getWidth(), (200 + dsine) % Gdx.graphics.getHeight()));
        graphStep++;
	}

	@Override
	protected void initialiseUniversalTweenEngineOverride() {
	}
}
