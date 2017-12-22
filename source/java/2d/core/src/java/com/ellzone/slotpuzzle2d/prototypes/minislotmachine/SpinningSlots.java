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

package com.ellzone.slotpuzzle2d.prototypes.minislotmachine;

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.sprites.AnimatedHandle;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class SpinningSlots extends SPPrototypeTemplate {
	
    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private Random random;
    private Array<AnimatedReel> reels;
    private Timeline introSequence;
    private Sound pullLeverSound, reelSpinningSound, reelStoppingSound;
	private Vector2 touch;
	private AnimatedHandle animatedHandle;
	private TextureAtlas handleAtlas;
	private int reelSpriteHelp;
	private Sprite[] sprites;
	 
	@Override
	protected void initialiseOverride() {
		touch = new Vector2();
	}

    @Override
    protected void initialiseScreenOverride() {
    }

    @Override
	protected void loadAssetsOverride() {
		pullLeverSound = annotationAssetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
		reelSpinningSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
		reelStoppingSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
	}

	@Override
	protected void disposeOverride() {
	}

	@Override
	protected void updateOverride(float dt) {
		handleInput(dt);
		tweenManager.update(dt);
        for (AnimatedReel reel : reels) {
            reel.update(dt);
        }
        animatedHandle.update(dt);
		
	}

	@Override
	protected void renderOverride(float dt) {
		batch.begin();
        for (AnimatedReel reel : reels) {
            reel.draw(batch);
            sprites[reelSpriteHelp].setX(32);
            sprites[reelSpriteHelp].draw(batch);
        }
        animatedHandle.draw(batch);
        batch.end();
	}

	@Override
	protected void initialiseUniversalTweenEngineOverride() {
		SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
		initialiseReelSlots();
		createIntroSequence();
	}

    private void initialiseReelSlots() {
        random = new Random();
        reels = new Array<AnimatedReel>();
        slotReelScrollPixmap = new Pixmap(spriteWidth, spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        for (int i=0; i<3; i++) {
            AnimatedReel reel = new AnimatedReel(slotReelScrollTexture, 0, 0, spriteWidth, spriteHeight, spriteWidth, spriteHeight, 0, reelSpinningSound, reelStoppingSound, tweenManager);
            reel.setX(i*spriteWidth + cam.viewportWidth / 2);
            reel.setY(cam.viewportHeight / 2);
            reel.setSx(0);
            reel.setSy(0);
            reel.setEndReel(random.nextInt(sprites.length - 1));
            reels.add(reel);
        }
        animatedHandle = new AnimatedHandle(handleAtlas, displayWindowWidth - 250, 50);
    }

    private void createIntroSequence() {
        introSequence = Timeline.createParallel();
        for(int i=0; i < reels.size; i++) {
            introSequence = introSequence
                   .push(buildSequence(reels.get(i).getReel(), i, random.nextFloat() * 5.0f, random.nextFloat() * 5.0f, reels.size));
        }

        introSequence = introSequence
               .pushPause(0.3f)
               .start(tweenManager);
    }

    private Timeline buildSequence(Sprite target, int id, float delay1, float delay2, int numberOfSprites) {
        Vector2 targetXY = getRandomCorner();
        int targetPositionX = (id * spriteWidth) + (displayWindowWidth - (((spriteWidth * numberOfSprites) +  displayWindowWidth) / 2));
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
               .push(SlotPuzzleTween.to(target, SpriteAccessor.POS_XY, 1.0f).target(targetPositionX, displayWindowHeight / 2                                                                                         ).ease(Back.OUT))
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
                return new Vector2(-1 * random.nextFloat(), displayWindowWidth + random.nextFloat());
            case 2:
                return new Vector2(displayWindowHeight + random.nextFloat(), -1 * random.nextFloat());
           case 3:
                return new Vector2(displayWindowHeight + random.nextFloat(), displayWindowWidth + random.nextFloat());
           default:
                return new Vector2(-0.5f, -0.5f);
        }
    }

    public void handleInput(float delta) {
        if (Gdx.input.justTouched()) {
            touch = touch.set(Gdx.input.getX(), Gdx.input.getY());
			touch = viewport.unproject(touch);
            for (AnimatedReel animatedReel : reels) {
                if(animatedReel.getReel().getBoundingRectangle().contains(touch)) {
                	if (animatedReel.getReel().isSpinning()) {
                        if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                            reelSpriteHelp = animatedReel.getReel().getCurrentReel();
                        	animatedReel.getReel().setEndReel(reelSpriteHelp);
                        }
                    } else {
                        animatedReel.setEndReel(random.nextInt(sprites.length - 1));
                        animatedReel.reinitialise();
                        animatedReel.getReel().startSpinning();
                    }
                }
            }
            if(animatedHandle.getBoundingRectangle().contains(touch)) {
            	boolean reelsNotSpinning = true;
                for  (AnimatedReel animatedReel : reels) {
                    if (animatedReel.getReel().isSpinning()) {
                    	reelsNotSpinning = false;
                    }
                }
                if (reelsNotSpinning) {
                    animatedHandle.setAnimated(true);
                    pullLeverSound.play();
                    for  (AnimatedReel animatedReel : reels) {
                        animatedReel.setEndReel(random.nextInt(sprites.length - 1));
                        animatedReel.reinitialise();
                        animatedReel.getReel().startSpinning();
                    }
                } else {
                	reelStoppingSound.play();
                }
 
            }
        }
    }
}
