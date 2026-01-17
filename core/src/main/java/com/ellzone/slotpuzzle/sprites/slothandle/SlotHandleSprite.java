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

package com.ellzone.slotpuzzle.sprites.slothandle;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.sprites.SpriteRenderInterface;
import com.ellzone.slotpuzzle.utils.PixmapProcessors;
import com.badlogic.gdx.graphics.Texture;
import com.ellzone.slotpuzzle.tweenengine.BaseTween;
import com.ellzone.slotpuzzle.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle.tweenengine.Timeline;
import com.ellzone.slotpuzzle.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Sine;
import com.ellzone.slotpuzzle.effects.SpriteAccessor;
import com.badlogic.gdx.math.*;

public class SlotHandleSprite implements SlotHandle, SpriteRenderInterface {
    public static final String SLOT_HANDLE = "slot_handle";
    public static final String SLOT_HANDLE_BASE = "slot_handle_base";
    private final TextureAtlas slotHandleAtlas;
    private Sprite slotHandle, slotHandleBase;
    private final TweenManager tweenManager;
    private Timeline slotHandleSequence;
    private final Array<TextureRegion> textureRegions = new Array<>();
    private Array<Integer> entityIds;

    public SlotHandleSprite(TextureAtlas slotHandleAtlas, TweenManager tweenManager) {
        this.slotHandleAtlas = slotHandleAtlas;
        this.tweenManager = tweenManager;
        defineSlotHandleSprite();
    }

    public SlotHandleSprite(TextureAtlas slotHandleAtlas,
                            TweenManager tweenManager,
                            float xPosition,
                            float yPosition) {
        this(slotHandleAtlas, tweenManager);
        slotHandle.setPosition(xPosition, yPosition);
        slotHandleBase.setPosition(xPosition, yPosition - 20);
        textureRegions.add(getSlotHandleSprite());
        textureRegions.add(getSlotHandleBaseSprite());
    }

    @Override
    public Sprite getSlotHandleSprite() {
        return slotHandle;
    }

    @Override
    public Sprite getSlotHandleBaseSprite() {
        return slotHandleBase;
    }

    @Override
    public void setSlotHandleSprite(Sprite sprite) {
        slotHandle = sprite;
    }

    @Override
    public void setSlotHandleBaseSprite(Sprite sprite) {
        slotHandleBase = sprite;
    }

    @Override
    public void pullSlotHandle() {
        slotHandleSequence.start(tweenManager);
    }

    @Override
    public Rectangle getBoundingRectangle() {
        return slotHandle.getBoundingRectangle();
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        slotHandleBase.draw(spriteBatch);
        slotHandle.draw(spriteBatch);
    }

    private void defineSlotHandleSprite() {
        slotHandle = slotHandleAtlas.createSprite(SLOT_HANDLE);
        slotHandleBase = slotHandleAtlas.createSprite(SLOT_HANDLE_BASE);
        assert slotHandle != null;
        Pixmap slotHandlePixmap = PixmapProcessors.getPixmapFromTextureRegion(slotHandle);
        slotHandlePixmap.drawCircle(22, slotHandlePixmap.getHeight() - 10, 5);
        slotHandle = new Sprite(new Texture(slotHandlePixmap));
        slotHandle.setOrigin(22.0f, 10.0f);
        slotHandle.setPosition(510, 95);
        assert slotHandleBase != null;
        slotHandleBase.setPosition(500, 75);
        createSlotHandleTween();
    }

    private void createSlotHandleTween() {
        slotHandleSequence = Timeline.createSequence();
        slotHandleSequence.push(SlotPuzzleTween.set(slotHandle, SpriteAccessor.ROTATION)
            .target(0)
            .ease(Sine.IN));

        slotHandleSequence.push(SlotPuzzleTween.to(slotHandle, SpriteAccessor.ROTATION, 0.75f)
            .target(-60.0f)
            .ease(Sine.OUT));

        slotHandleSequence.push(SlotPuzzleTween.to(slotHandle, SpriteAccessor.ROTATION, 0.75f)
            .target(0.0f)
            .ease(Sine.OUT)
            .setCallback(slotHandleCallback)
            .setCallbackTriggers(TweenCallback.END));
    }

    private final TweenCallback slotHandleCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            delegateSlotHandleCallback(type, source);
        }
    };

    private void delegateSlotHandleCallback(int type, BaseTween<?> source) {
        createSlotHandleTween();
    }

    @Override
    public Array<TextureRegion> getTextureRegions() {
        return textureRegions;
    }

    @Override
    public Array<Integer> getEntityIds() {
        return entityIds;
    }

    @Override
    public void setEntityIds(Array<Integer> entityIds) {
        this.entityIds = entityIds;
    }
}
