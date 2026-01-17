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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.sprites.SpriteRenderInterface;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;
import com.ellzone.slotpuzzle.utils.convert.TileMapToWorldConvert;

import lombok.Getter;

public class SlotHandleTileMap implements SlotHandle, SpriteRenderInterface {
    private final SlotHandleSprite slotHandle;
    @Getter
    private float worldPositionX;
    @Getter
    private float worldPositionY;
    private Array<TextureRegion> textureRegions;
    private Array<Integer> entityIds;

    public SlotHandleTileMap(TextureAtlas slotHandleAtlas,
                             TweenManager tweenManager,
                             float xPosition,
                             float yPosition) {
        slotHandle = new SlotHandleSprite(
            slotHandleAtlas,
            tweenManager,
            convertTileMapXToWorldPositionX(xPosition) / 100,
            convertTileMapYToWorldPositionY(yPosition) / 100);
        changeSpriteSize(slotHandle.getSlotHandleSprite());
        changeSpriteSize(slotHandle.getSlotHandleBaseSprite());
        setSlotHandleOriginSize();
        textureRegions.add((TextureRegion) slotHandle.getSlotHandleSprite());
        textureRegions.add((TextureRegion) slotHandle.getSlotHandleBaseSprite());
    }

    private void setSlotHandleOriginSize() {
        slotHandle.getSlotHandleSprite().setOrigin(
            (float) 22.0 / (float) 50,
            (float) 10.0 / (float) 50);
    }

    private void changeSpriteSize(Sprite sprite) {
        sprite.setBounds(
            sprite.getX(),
            sprite.getY(),
            sprite.getWidth() / (float) 50,
            sprite.getHeight() / (float) 50);
    }

    private float convertTileMapXToWorldPositionX(float x) {
        worldPositionX = TileMapToWorldConvert.convertTileMapXToWorldX(x);
        return worldPositionX;
    }

    private float convertTileMapYToWorldPositionY(float y) {
        worldPositionY = TileMapToWorldConvert.convertTileMapYToWorldY(y);
        return worldPositionY;
    }

    @Override
    public Sprite getSlotHandleSprite() {
        return slotHandle.getSlotHandleSprite();
    }

    @Override
    public Sprite getSlotHandleBaseSprite() {
        return slotHandle.getSlotHandleBaseSprite();
    }

    @Override
    public void setSlotHandleSprite(Sprite sprite) {
        slotHandle.setSlotHandleSprite(sprite);
    }

    @Override
    public void setSlotHandleBaseSprite(Sprite sprite) {
        slotHandle.setSlotHandleBaseSprite(sprite);
    }

    @Override
    public void pullSlotHandle() {
        slotHandle.pullSlotHandle();
    }

    @Override
    public Rectangle getBoundingRectangle() {
        return slotHandle.getBoundingRectangle();
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        slotHandle.draw(spriteBatch);
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
