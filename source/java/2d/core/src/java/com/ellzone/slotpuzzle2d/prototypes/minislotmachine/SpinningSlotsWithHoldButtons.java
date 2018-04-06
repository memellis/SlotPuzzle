/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes.minislotmachine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.LightButton;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.SlotHandleSprite;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import java.util.Random;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import box2dLight.PointLight;
import box2dLight.RayHandler;

public class SpinningSlotsWithHoldButtons extends SPPrototypeTemplate {

    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private Random random;
    private Array<AnimatedReel> reels;
    private Timeline introSequence;
    private Sound pullLeverSound, reelSpinningSound, reelStoppingSound;
    private Vector2 touch;
    private Vector3 point = new Vector3();
    private TextureAtlas slotHandleAtlas;
    private int reelSpriteHelp;
    private SlotHandleSprite slotHandleSprite;
    private static final float PIXELS_PER_METER = 100;
    private Viewport lightViewport, hudViewport;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private RayHandler rayHandler;
    private PointLight reelLight, handleLight, reelHelperLight;
    private Array<LightButton> lightButtons;

    @Override
    protected void initialiseOverride() {
        touch = new Vector2();
        createHoldButtons();
    }

    @Override
    protected void initialiseScreenOverride() {

    }

    private void createHoldButtons() {
        lightViewport = new FitViewport(SlotPuzzleConstants.V_WIDTH / PIXELS_PER_METER, SlotPuzzleConstants.V_HEIGHT / PIXELS_PER_METER);
        lightViewport.getCamera().position.set(lightViewport.getCamera().position.x + SlotPuzzleConstants.V_WIDTH / PIXELS_PER_METER * 0.5f,
                lightViewport.getCamera().position.y + SlotPuzzleConstants.V_HEIGHT / PIXELS_PER_METER * 0.5f,
                0);
        lightViewport.getCamera().update();
        hudViewport = new FitViewport(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT, new OrthographicCamera());

        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        rayHandler = new RayHandler(world);
        rayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.2f, 0.2f, 0.2f, 0.25f);

        reelLight = new PointLight(rayHandler, 32);
        reelLight.setActive(true);
        reelLight.setColor(Color.WHITE);
        reelLight.setDistance(2.0f);
        reelLight.setPosition(SlotPuzzleConstants.V_WIDTH / ( PIXELS_PER_METER * 2), (SlotPuzzleConstants.V_HEIGHT + 96) / (PIXELS_PER_METER * 2));

        handleLight = new PointLight(rayHandler, 32);
        handleLight.setActive(true);
        handleLight.setColor(Color.WHITE);
        handleLight.setDistance(1.5f);
        Rectangle slotHandleSprintBoundingRectangle = slotHandleSprite.getBoundingRectangle();
        float slotHandleSpriteCenterX = slotHandleSprintBoundingRectangle.getX() + slotHandleSprintBoundingRectangle.getWidth() / 2;
        float slotHandleSpriteCenterY = slotHandleSprintBoundingRectangle.getY() + slotHandleSprintBoundingRectangle.getHeight() / 2;
        handleLight.setPosition(slotHandleSpriteCenterX / PIXELS_PER_METER, slotHandleSpriteCenterY / PIXELS_PER_METER);

        reelHelperLight = new PointLight(rayHandler, 32);
        reelHelperLight.setActive(true);
        reelHelperLight.setColor(Color.RED);
        reelHelperLight.setDistance(1.0f);
        reelHelperLight.setPosition(48 / PIXELS_PER_METER,  (sprites[0].getY() + 16) / PIXELS_PER_METER);

        lightButtons = new Array<LightButton>();
        for (int i = 0; i < 3; i++) {
            LightButton lightButton = new LightButton(world, rayHandler, i * 32 / PIXELS_PER_METER + SlotPuzzleConstants.V_WIDTH / (PIXELS_PER_METER * 2) - (3 * 32 / PIXELS_PER_METER) / 2, SlotPuzzleConstants.V_HEIGHT / (PIXELS_PER_METER * 4), 32, 32, new BitmapFont(), "", "Hold");
            lightButton.getSprite().setSize(32 / PIXELS_PER_METER, 32 / PIXELS_PER_METER);
            lightButtons.add(lightButton);
        }
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
    public void resize(int width, int height) {
        super.resize(width, height);
        lightViewport.update(width, height);
        hudViewport.update(width, height);
    }

    @Override
    protected void disposeOverride() {
        debugRenderer.dispose();
        rayHandler.dispose();
        world.dispose();
    }

    @Override
    protected void updateOverride(float dt) {
        handleInput();
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
        batch.setProjectionMatrix(lightViewport.getCamera().combined);
        batch.begin();
        for (LightButton lightButton : lightButtons) {
            lightButton.getSprite().draw(batch);
        }
        batch.end();
        rayHandler.setCombinedMatrix(lightViewport.getCamera().combined);
        rayHandler.updateAndRender();
        debugRenderer.render(world, lightViewport.getCamera().combined);
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
            AnimatedReel animatedReel = new AnimatedReel(slotReelScrollTexture, 0, 0, spriteWidth, spriteHeight, spriteWidth, spriteHeight * 3, 0, reelSpinningSound, reelStoppingSound, tweenManager);
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

    public void handleInput() {
        if (Gdx.input.justTouched()) {
            touch = touch.set(Gdx.input.getX(), Gdx.input.getY());
            touch = viewport.unproject(touch);
            for (AnimatedReel animatedReel : reels) {
                if (animatedReel.getReel().getBoundingRectangle().contains(touch)) {
                    if (animatedReel.getReel().isSpinning()) {
                        if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                            reelSpriteHelp = animatedReel.getReel().getCurrentReel();
                            animatedReel.getReel().setEndReel(reelSpriteHelp - 1 < 0 ? 0 : reelSpriteHelp - 1);
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
                for (AnimatedReel animatedReel : reels) {
                    if (animatedReel.getReel().isSpinning()) {
                        reelsNotSpinning = false;
                    }
                }
                if (reelsNotSpinning) {
                    slotHandleSprite.pullSlotHandle();
                    pullLeverSound.play();
                    int i = 0;
                    for (AnimatedReel animatedReel : reels) {
                        if (!lightButtons.get(i).getLight().isActive()) {
                            animatedReel.setEndReel(random.nextInt(sprites.length - 1));
                            animatedReel.reinitialise();
                            animatedReel.getReel().startSpinning();
                        }
                        i++;
                    }
                } else {
                    reelStoppingSound.play();
                }
            }
            touch = touch.set(Gdx.input.getX(), Gdx.input.getY());
            touch = lightViewport.unproject(touch);
            for (LightButton lightButton : lightButtons) {
                if (lightButton.getSprite().getBoundingRectangle().contains(touch.x, touch.y)) {
                    if (lightButton.getLight().isActive()) {
                        lightButton.getLight().setActive(false);
                    } else {
                        lightButton.getLight().setActive(true);
                    }
                }
            }
        }
    }
}
