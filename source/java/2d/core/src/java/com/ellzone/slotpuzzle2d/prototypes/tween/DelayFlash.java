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

package com.ellzone.slotpuzzle2d.prototypes.tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import java.util.Random;

import aurelienribon.tweenengine.equations.Sine;

public class DelayFlash extends SPPrototypeTemplate {

    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private Random random;
    private Array<ReelTile> reelTiles;
    private Timeline flashSeq;

    @Override
    protected void initialiseOverride() {
        Gdx.input.setInputProcessor(inputProcessor);
    }

    @Override
    protected void initialiseScreenOverride() {

    }

    @Override
    protected void loadAssetsOverride() {

    }

    @Override
    protected void disposeOverride() {
        Gdx.app.log(SLOTPUZZLE_PLAY, "dispose override called!");
    }

    @Override
    protected void updateOverride(float dt) {
        tweenManager.update(dt);
        for (ReelTile reel : reelTiles) {
            reel.update(dt);
        }
    }

    @Override
    protected void renderOverride(float dt) {
        batch.begin();
        for (Sprite sprite : sprites) {
            sprite.draw(batch);
        }
        for (ReelTile reel : reelTiles) {
            reel.draw(batch);
        }
        batch.end();
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        initialiseReelSlots();
    }

    private void initialiseReelSlots() {
        random = new Random();
        reelTiles = new Array<ReelTile>();
        slotReelScrollPixmap = new Pixmap(spriteWidth, spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        ReelTile reel = new ReelTile(slotReelScrollTexture,  slotReelScrollTexture.getHeight() / spriteHeight, 0, 32, spriteWidth, spriteHeight,  spriteWidth, spriteHeight, 0, null);
        reel.setX(0);
        reel.setY(0);
        reel.setSx(0);
        reel.setSy(0);
        reel.setEndReel(random.nextInt(sprites.length - 1));
        initialiseReelFlash(reel);
        reelTiles.add(reel);
    }

    private void initialiseReelFlash(ReelTile reel) {
        reel.setFlashTween(true);
        flashSeq = Timeline.createSequence();
        Color myRed = new Color(Color.RED);
        myRed.r = 1.0f;
        myRed.g = 0.0f;
        myRed.b = 0.0f;
        myRed.a = 1.0f;
        Color flashColor = new Color(Color.WHITE);
        reel.setFlashColor(flashColor);
        flashSeq = flashSeq.pushPause(5.0f);

        flashSeq = flashSeq.push(SlotPuzzleTween.set(reel, ReelAccessor.FLASH_TINT)
                .target(myRed.r, myRed.g, myRed.b)
                .ease(Sine.IN));

        flashSeq = flashSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.FLASH_TINT, 0.5f)
                .target(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b)
                .ease(Sine.OUT)
                .repeatYoyo(17, 0));

        flashSeq = flashSeq.push(SlotPuzzleTween.set(reel, ReelAccessor.FLASH_TINT)
                .target(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b)
                .ease(Sine.IN));
        flashSeq = flashSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.FLASH_TINT, 0.2f)
                .target(myRed.r, myRed.g, myRed.b)
                .ease(Sine.OUT)
                .repeatYoyo(25, 0));

        flashSeq = flashSeq.push(SlotPuzzleTween.set(reel, ReelAccessor.FLASH_TINT)
                .target(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b)
                .ease(Sine.IN));
        flashSeq = flashSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.FLASH_TINT, 0.05f)
                .target(myRed.r, myRed.g, myRed.b)
                .ease(Sine.OUT)
                .repeatYoyo(33, 0))
                .setCallback(reelFlashCallback)
                .setCallbackTriggers(TweenCallback.END)
                .setUserData(reel)
                .start(tweenManager);
    }

    private TweenCallback reelFlashCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
        delegateReelFlashCallback(type, source);
    }
    };

    private void delegateReelFlashCallback(int type, BaseTween<?> source) {
        ReelTile reel = (ReelTile)source.getUserData();
        reel.setFlashTween(false);
        reel.setFlashOff();
    }

    private final InputProcessor inputProcessor = new InputAdapter() {
        @Override
        public boolean touchUp(int x, int y, int pointer, int button) {
        tweenManager.killAll();
        for (ReelTile reel : reelTiles) {
            if (!reel.getFlashTween()) {
                Color flashColor = new Color(Color.RED);
                reel.setFlashColor(flashColor);
                initialiseReelFlash(reel);
            }
        }
        return true;
    }
    };
}
