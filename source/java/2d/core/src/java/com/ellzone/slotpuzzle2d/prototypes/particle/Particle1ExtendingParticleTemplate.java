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

package com.ellzone.slotpuzzle2d.prototypes.particle;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.ellzone.slotpuzzle2d.physics.Particle;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.physics.Particles;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTiles;
import com.ellzone.slotpuzzle2d.sprites.Reels;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class Particle1ExtendingParticleTemplate extends ParticleTemplate {
	private static final float VELOCITY_MIN = 2.0f;
	protected AnnotationAssetManager annotationAssetManager;
	private Reels reels;
	private Sprite[] reelSprites;
	private Array<Particle> reelParticles;
    private Vector accelerator;
    private int dampPoint;
	private ReelTiles reelTiles;
	private Array<ReelTile> reelTilesArray;
	private Particles particles;
	
	@Override
	protected void initialiseOverride() {
	    initialiseReels(annotationAssetManager);
		initialiseReelSlots();
		intialiseParticles();
	}

	@Override
	protected void loadAssetsOverride(AnnotationAssetManager annotationAssetManager) {
	}
	
	private void initialiseReels(AnnotationAssetManager annotationAssetManager) {
        reels = new Reels(annotationAssetManager);
        reelSprites = reels.getReels();				
	}
	
	private void initialiseReelSlots() {
        reelTiles = new ReelTiles(reels);
		reelTilesArray = reelTiles.getReelTiles();
	}

    private void intialiseParticles() {
		particles = new Particles(reels, reelTiles);
		reelParticles = particles.getParticles();
		accelerator = particles.getAccelerator();
		dampPoint = particles.getDampoint();
    }

	@Override
	protected void disposeOverride() {
	}

	@Override
	protected void updateOverride(float delta) {
		for (Particle reelParticle : reelParticles) {
            reelParticle.update();
        }
        for(ReelTile reelTile : reelTilesArray) {
            if (reelParticles.get(0).velocity.getY()  > VELOCITY_MIN) {
                reelParticles.get(0).velocity.mulitplyBy(0.97f);
                reelParticles.get(0).accelerate(particles.getAccelerator());
                accelerator.mulitplyBy(0.97f);
                reelTile.setSy(reelParticles.get(0).position.getY());
            } else {
                if (reelTile.getSy() < dampPoint) {
                    reelTile.setSy(reelTile.getSy() + reelParticles.get(0).velocity.getY());
                }
            }
            reelTile.update(delta);
        }
	}

	@Override
	protected void renderOverride(float delta) {
	    batch.begin();
        for (ReelTile reelTile : reelTilesArray) {
            reelTile.draw(batch);
        }
        batch.end();
	}

	@Override
	protected void initialiseUniversalTweenEngineOverride() {
	}
}
