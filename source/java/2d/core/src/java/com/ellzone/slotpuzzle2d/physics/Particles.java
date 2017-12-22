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

package com.ellzone.slotpuzzle2d.physics;

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTiles;
import com.ellzone.slotpuzzle2d.sprites.Reels;

public class Particles {
	private Vector accelerator;
	private Array<Particle> reelParticles;
	private ReelTiles reelTiles;
	private Reels reels;
	private Particle reelParticle;
	private int dampPoint;
	
	public Particles(Reels reels, ReelTiles reelTiles) {
		this.reels = reels;
		this.reelTiles = reelTiles;
	    initialiseParticles();	
	}
	
	private void initialiseParticles() {
		ReelTile reelTile0 = reelTiles.getReelTiles().get(0);
		accelerator = new Vector(0, 3f);
        reelParticles = new Array<Particle>();
        reelParticle = new Particle(0, reelTile0.getSy(), 0.0001f , 0, 0);
        reelParticle.velocity.setX(0);
        reelParticle.velocity.setY(4);
        reelParticle.accelerate(new Vector(0, 2f));
        reelParticles.add(reelParticle);
        dampPoint = reelTiles.getReelTileTextureHeight() * 20 + reelTile0.getEndReel() * reels.getReelHeight();
	}
	
	public Array<Particle> getParticles() {
		return reelParticles;
	}
	
	public int getDampoint() {
		return dampPoint;
	}
	
	public Vector getAccelerator() {
		return this.accelerator;
	}

	public void setAccelerator(Vector accelerator) {
		this.accelerator = accelerator;
	}	
}
