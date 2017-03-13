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

package com.ellzone.slotpuzzle2d.prototypes.assets;

import java.io.IOException;
import org.jrenner.smartfont.SmartFontGenerator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.effects.ReelLetterAccessor;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsEvent;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.sprites.ReelLetterTile;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import aurelienribon.tweenengine.equations.Elastic;

public class CreateLevelReels extends SPPrototypeTemplate {

    public static final String SLOT_PUZZLE = "Slot Puzzle";
    public static final String WORLD = "World";
    public static final String LEVEL = "Level";
    public static final String LIBERATION_MONO_REGULAR_FONT_NAME = "LiberationMono-Regular.ttf";
    public static final String GENERATED_FONTS_DIR = "generated-fonts/";
    public static final String FONT_SMALL = "exo-small";
    public static final int FONT_SMALL_SIZE = 24;
    public static final int REEL_WIDTH = 40;
    public static final int REEL_HEIGHT = 40;

    private BitmapFont fontSmall;
    private Texture textTexture;
    private Array<ReelLetterTile> reelLetterTiles;
    private Array<DampenedSineParticle> dampenedSines;
    private Timeline endReelSeq;
    private Vector accelerator, accelerate, velocity, velocityMin;
    private float acceleratorY, accelerateY, acceleratorFriction, velocityFriction, velocityY, velocityYMin;
    private int reelTextureHeight;
    private Vector2 touch;
    private int numReelLettersSpinning;

    @Override
    protected void initialiseOverride() {
        initialiseFonts();
        String genText = WORLD + " 1234567890ABCDEFGHIJLMNOPQRSTUVWXYZabcdefghijlmnopqrstuvwxyz";
        initialiseFontTexture(SLOT_PUZZLE);
        initialiseFontReel(SLOT_PUZZLE);
        initialiseFontTexture(genText);
        initialiseFontReel(genText);
        initialiseDampenedSine();
        touch = new Vector2(0, 0);
    }

    private void initialiseFonts() {
        SmartFontGenerator fontGen = new SmartFontGenerator();
        FileHandle internalFontFile = Gdx.files.internal(LIBERATION_MONO_REGULAR_FONT_NAME);
        FileHandle generatedFontDir = Gdx.files.local(GENERATED_FONTS_DIR);
        generatedFontDir.mkdirs();

        FileHandle generatedFontFile = Gdx.files.local("generated-fonts/LiberationMono-Regular.ttf");
        try {
            FileUtils.copyFile(internalFontFile, generatedFontFile);
        } catch (IOException ex) {
            Gdx.app.error(SlotPuzzle.SLOT_PUZZLE, "Could not copy " + internalFontFile.file().getPath() + " to file " + generatedFontFile.file().getAbsolutePath() + " " + ex.getMessage());
        }
        fontSmall = fontGen.createFont(generatedFontFile, FONT_SMALL, FONT_SMALL_SIZE);
    }

    private void initialiseFontTexture(String reelText) {
        Pixmap textPixmap = new Pixmap(REEL_WIDTH, reelText.length() * REEL_HEIGHT, Pixmap.Format.RGBA8888);
        textPixmap = PixmapProcessors.createDynamicVerticalFontText(fontSmall, reelText, textPixmap);
        PixmapProcessors.savePixmap(textPixmap, "../android/assets/reel/reelfont.png");
        textTexture = new Texture(textPixmap);
        reelTextureHeight = textTexture.getHeight();
    }

    private void initialiseFontReel(String reelText) {
        reelLetterTiles = new Array<ReelLetterTile>();
        float r = 0;
        float c = 0;
        for (int i = 0; i < reelText.length(); i++) {
            c = (float)(20 + (i % 10) * REEL_WIDTH);
            r = 350f -  (float)Math.floor(i / 10.0f) * (REEL_HEIGHT + 5);
            ReelLetterTile reelLetter = new ReelLetterTile(textTexture, c, r, (float)REEL_WIDTH, (float)REEL_HEIGHT, i);
            reelLetter.setSy(random.nextInt(reelText.length() - 1) * REEL_HEIGHT);
            reelLetter.setSpinning();
            reelLetterTiles.add(reelLetter);
        }
        numReelLettersSpinning = reelText.length();
    }

    private void initialiseDampenedSine() {
        velocityY = 4.0f;
        velocityYMin = 2.0f;
        acceleratorY = 3.0f;
        accelerateY = 2.0f;
        acceleratorFriction = 0.97f;
        velocityFriction = 0.97f;
        DampenedSineParticle dampenedSine;
        dampenedSines = new Array<DampenedSineParticle>();
        for (ReelLetterTile reel : reelLetterTiles) {
            velocity = new Vector(0, velocityY);
            velocityMin = new Vector(0, velocityYMin);
            accelerator = new Vector(0, acceleratorY);
            accelerate = new Vector(0, accelerateY);
            dampenedSine = new DampenedSineParticle(0, reel.getSy(), 0, 0, 0, velocity, velocityMin, accelerator, accelerate, velocityFriction, acceleratorFriction);
            dampenedSine.setCallback(dsCallback);
            dampenedSine.setCallbackTriggers(SPPhysicsCallback.PARTICLE_UPDATE);
            dampenedSine.setUserData(reel);
            dampenedSines.add(dampenedSine);
        }
    }

    private SPPhysicsCallback dsCallback = new SPPhysicsCallback() {
        @Override
        public void onEvent(int type, SPPhysicsEvent source) {
            delegateDSCallback(type, source);
        }
    };

    private void delegateDSCallback(int type, SPPhysicsEvent source) {
        if (type == SPPhysicsCallback.PARTICLE_UPDATE) {
            DampenedSineParticle ds = (DampenedSineParticle)source.getSource();
            ReelLetterTile reel = (ReelLetterTile)ds.getUserData();

            endReelSeq = Timeline.createSequence();
            float endSy = (reel.getEndReel() * REEL_HEIGHT) % reelTextureHeight;
            reel.setSy(reel.getSy() % (reelTextureHeight));
            endReelSeq = endReelSeq.push(SlotPuzzleTween.to(reel, ReelLetterAccessor.SCROLL_XY, 5.0f)
                    .target(0f, endSy)
                    .ease(Elastic.OUT)
                    .setUserData(reel)
                    .setCallbackTriggers(TweenCallback.END)
                    .setCallback(slowingSpinningCallback));
            endReelSeq = endReelSeq
                    .start(tweenManager);
        }
    }

    private TweenCallback slowingSpinningCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            delegateSlowingSpinning(type, source);
        }
    };

    private void delegateSlowingSpinning(int type, BaseTween<?> source) {
        ReelLetterTile reel = (ReelLetterTile)source.getUserData();
        if (type == TweenCallback.END) {
            reel.setSpinning(false);
            numReelLettersSpinning--;
            if (numReelLettersSpinning == 0) {
                restartReelLettersSpinning();
                numReelLettersSpinning = reelLetterTiles.size;
            }
        }
    }

    private void restartReelLettersSpinning() {
        int dsIndex = 0;
        int nextSy = 0;
        for (ReelLetterTile reel : reelLetterTiles) {
            reel.setEndReel(dsIndex);
            reel.setSpinning(true);
            nextSy = random.nextInt(reelLetterTiles.size - 1) * REEL_HEIGHT;
            reel.setSy(nextSy);
            dampenedSines.get(dsIndex).initialiseDampenedSine();
            dampenedSines.get(dsIndex).position.y = nextSy;
            dampenedSines.get(dsIndex).velocity = new Vector(0, velocityY);
            accelerator = new Vector(0, acceleratorY);
            dampenedSines.get(dsIndex).accelerator = accelerator;
            accelerate = new Vector(0, accelerateY);
            dampenedSines.get(dsIndex).accelerate(accelerate);
            dampenedSines.get(dsIndex).velocityMin.y = velocityMin.y;
            dsIndex++;
        }
    }


    @Override
    protected void loadAssetsOverride() {
    }

    @Override
    protected void updateOverride(float delta) {
        int dsIndex = 0;
        for (ReelLetterTile reel : reelLetterTiles) {
            dampenedSines.get(dsIndex).update();
            if (dampenedSines.get(dsIndex).getDSState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                reel.setSy(dampenedSines.get(dsIndex).position.y);
            }
            reel.update(delta);
            dsIndex++;
        }
        handleInput(delta);
    }

    @Override
    protected void renderOverride(float delta) {
        batch.begin();
        for (ReelLetterTile reel : reelLetterTiles) {
            reel.draw(batch);
        }
        batch.end();
    }

    public void handleInput(float delta) {
        int dsIndex = 0;
        for (ReelLetterTile reel : reelLetterTiles) {
            if (Gdx.input.justTouched()) {
                touch = touch.set(Gdx.input.getX(), cam.viewportHeight - Gdx.input.getY());
                if(reel.getBoundingRectangle().contains(touch)) {
                    if (reel.isSpinning()) {
                        if (dampenedSines.get(dsIndex).getDSState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                            reel.setEndReel(reel.getCurrentReel());
                        }
                    } else {
                        reel.setEndReel(random.nextInt(sprites.length - 1));
                        reel.setSpinning(true);
                        reel.setSy(0);
                        dampenedSines.get(dsIndex).initialiseDampenedSine();
                        dampenedSines.get(dsIndex).position.y = 0;
                        dampenedSines.get(dsIndex).velocity = new Vector(0, velocityY);
                        accelerator = new Vector(0, acceleratorY);
                        dampenedSines.get(dsIndex).accelerator = accelerator;
                        accelerate = new Vector(0, accelerateY);
                        dampenedSines.get(dsIndex).accelerate(accelerate);
                        dampenedSines.get(dsIndex).velocityMin.y = velocityMin.y;
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
    protected void initialiseUniversalTweenEngineOverride() {
        SlotPuzzleTween.registerAccessor(ReelLetterTile.class, new ReelLetterAccessor());

    }
}