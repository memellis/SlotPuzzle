/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ellzone.slotpuzzle2d.prototypes.minislotmachine;

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
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
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.sprites.AnimatedHandle;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Sine;
import com.ellzone.slotpuzzle2d.sprites.SlotHandleSprite;

public class SpinningSlotsWithThreeTilesDisplayed extends SPPrototypeTemplate {
	
    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private Random random;
    private Array<AnimatedReel> reels;
    private Timeline introSequence;
    private Sound pullLeverSound, reelSpinningSound, reelStoppingSound;
	private Vector2 touch;
	private TextureAtlas slotHandleAtlas;
	private int reelSpriteHelp;
	private SlotHandleSprite slotHandleSprite;
	
	@Override
	protected void initialiseOverride() {
		touch = new Vector2();
	}

	@Override
	protected void loadAssetsOverride() {
		Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
		Assets.inst().load("slot_handle/slot_handle.pack.atlas", TextureAtlas.class);
		Assets.inst().load("sounds/pull-lever1.wav", Sound.class);
		Assets.inst().load("sounds/click2.wav", Sound.class);
		Assets.inst().load("sounds/reel-stopped.wav", Sound.class);
		Assets.inst().update();
		Assets.inst().finishLoading();

		slotHandleAtlas = Assets.inst().get("slot_handle/slot_handle.pack.atlas", TextureAtlas.class);
		pullLeverSound = Assets.inst().get("sounds/pull-lever1.wav");
		reelSpinningSound = Assets.inst().get("sounds/click2.wav");
		reelStoppingSound = Assets.inst().get("sounds/reel-stopped.wav");
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
	}
	
	@Override
	protected void renderOverride(float dt) {
		batch.begin();
        for (AnimatedReel reel : reels) {
            reel.draw(batch);
			
            sprites[reelSpriteHelp].setX(32);
            sprites[reelSpriteHelp].draw(batch);
        }
		slotHandleSprite.draw(batch);
        batch.end();
	}

	@Override
	protected void initialiseUniversalTweenEngineOverride() {
		SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
		SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
		initialiseReelSlots();
		createIntroSequence();
		slotHandleSprite = new SlotHandleSprite(slotHandleAtlas, tweenManager);
	}

    private void initialiseReelSlots() {
        random = new Random();
        reels = new Array<AnimatedReel>();
        slotReelScrollPixmap = new Pixmap(spriteWidth, spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        for (int i = 0; i < 3; i++) {
            AnimatedReel animatedReel = new AnimatedReel(slotReelScrollTexture, 0, 0, spriteWidth, spriteHeight * 3, 0, reelSpinningSound, reelStoppingSound, tweenManager);
            animatedReel.setX(i * spriteWidth + displayWindowWidth / 2);
            animatedReel.setY((displayWindowHeight + 3 * spriteHeight) / 2);
            animatedReel.setSx(0);
            animatedReel.setEndReel(random.nextInt(sprites.length - 1));
            reels.add(animatedReel);
        }
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
                return new Vector2(displayWindowWidth + random.nextFloat(), -1 * random.nextFloat());
           case 3:
                return new Vector2(displayWindowWidth + random.nextFloat(), displayWindowWidth + random.nextFloat());
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
            if (slotHandleSprite.getBoundingRectangle().contains(touch)) {
            	boolean reelsNotSpinning = true;
                for  (AnimatedReel animatedReel : reels) {
                    if (animatedReel.getReel().isSpinning()) {
                    	reelsNotSpinning = false;
                    }
                }
                if (reelsNotSpinning) {
			        slotHandleSprite.pullSlotHandle();
                    pullLeverSound.play();
                    for (AnimatedReel animatedReel : reels) {
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
