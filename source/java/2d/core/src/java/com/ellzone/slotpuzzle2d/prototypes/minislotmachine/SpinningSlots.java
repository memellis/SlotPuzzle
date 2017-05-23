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

import aurelienribon.tweenengine.equations.*;
import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.viewport.*;
import com.ellzone.slotpuzzle2d.*;
import com.ellzone.slotpuzzle2d.effects.*;
import com.ellzone.slotpuzzle2d.physics.*;
import com.ellzone.slotpuzzle2d.prototypes.*;
import com.ellzone.slotpuzzle2d.sprites.*;
import com.ellzone.slotpuzzle2d.tweenengine.*;
import com.ellzone.slotpuzzle2d.utils.*;
import java.util.*;

public class SpinningSlots extends SPPrototype {
    private static final float MINIMUM_VIEWPORT_SIZE = 15.0f;
    private PerspectiveCamera cam;
	private FitViewport viewport;
    private SpriteBatch batch;
	private Stage stage;
    private Sprite cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato;
    private Sprite[] sprites;
    private int spriteWidth;
    private int spriteHeight;
    private int displayWindowWidth;
    private int displayWindowHeight;
    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private Random random;
    private Array<AnimatedReel> reels;
    private Timeline introSequence;
    private TweenManager tweenManager;
 	private Sound pullLeverSound, reelSpinningSound, reelStoppingSound;
	private Vector2 touch;
	private int reelSpriteHelp;

    @Override
     public void create() {
         loadAssets();
         initialiseLibGdx();
		 initialiseScreen();
         initialiseCamera();
         initialiseUniversalTweenEngine();
         initialiseReelSlots();
         createIntroSequence();
     }

     private void loadAssets() {
         Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
         Assets.inst().load("sounds/pull-lever1.wav", Sound.class);
         Assets.inst().load("sounds/click2.wav", Sound.class);
         Assets.inst().load("sounds/reel-stopped.wav", Sound.class);
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
         
         pullLeverSound = Assets.inst().get("sounds/pull-lever1.wav");
         reelSpinningSound = Assets.inst().get("sounds/click2.wav");
         reelStoppingSound = Assets.inst().get("sounds/reel-stopped.wav");
    }

	private void initialiseScreen() {
		viewport = new FitViewport(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT);
		stage = new Stage(viewport, batch); 
	
	}
	
    private void initialiseCamera() {
        cam = new PerspectiveCamera();
        cam.position.set(0, 0, 10);
        cam.lookAt(0, 0, 0);
        displayWindowWidth = SlotPuzzleConstants.V_WIDTH;
        displayWindowHeight = SlotPuzzleConstants.V_HEIGHT;
    }

    private void initialiseLibGdx() {
        batch = new SpriteBatch();
        touch = new Vector2();
    }

    private void initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        tweenManager = new TweenManager();
    }

    private void initialiseReelSlots() {
        random = new Random();
        reels = new Array<AnimatedReel>();
        slotReelScrollPixmap = new Pixmap(spriteWidth, spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        for (int i=0; i<3; i++) {
            AnimatedReel reel = new AnimatedReel(slotReelScrollTexture, 0, 0, spriteWidth, spriteHeight, 0, reelSpinningSound, reelStoppingSound, tweenManager);
            reel.setX(i*spriteWidth + cam.viewportWidth / 2);
            reel.setY(cam.viewportHeight / 2);
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
		viewport.update(width, height);
    }

    public void handleInput(float delta) {
        if (Gdx.input.justTouched()) {
            touch = touch.set(Gdx.input.getX(), Gdx.input.getY());
		    touch = viewport.unproject(touch);
			for (AnimatedReel animatedReel : reels) {	
                if(animatedReel.getReel().getBoundingRectangle().contains(touch)) {
                	if (animatedReel.getReel().isSpinning()) {
                        if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
							Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE, "animatedReel.getReel().getEndReel()="+animatedReel.getReel().getEndReel());
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
        }
    }

    private void update(float delta) {
        tweenManager.update(delta);
        for (AnimatedReel reel : reels) {
            reel.update(delta);
        }
    }

    @Override
    public void render() {
        final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        update(delta);
        handleInput(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin();
        for (AnimatedReel reel : reels) {
            reel.draw(batch);
            sprites[reelSpriteHelp].setX(32);
            sprites[reelSpriteHelp].draw(batch);
        }
        batch.end();
		stage.draw();
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
		stage.dispose();
        Assets.inst().dispose();
    }
}
