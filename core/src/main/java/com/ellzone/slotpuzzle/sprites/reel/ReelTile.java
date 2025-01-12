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

package com.ellzone.slotpuzzle.sprites.reel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.utils.PixmapProcessors;
import com.ellzone.slotpuzzle.utils.Random;

public class ReelTile extends ReelSprite implements ReelTileInterface {
    private final Texture scrollTexture;
    private int numberOfReelsInTexture = 0;
    private TextureRegion region, flashReel;
    private final float tileWidth;
    private final float tileHeight;
    private float reelDisplayWidth = 0, reelDisplayHeight = 0;
    private float screenDisplayWidth = 0, screenDisplayHeight = 0;
    private float destinationX;
    private float destinationY;
    private float sx = 0;
    private float sy = 0;
    private int index = -1;
    private boolean tileDeleted;
    private boolean reelFlash;
    private boolean reelFlashTween;
    private boolean isFallen;
    private boolean isStoppedFalling;
    private Array<Integer> entityIds;

    public enum FlashState {FLASH_OFF, FLASH_ON};
    private FlashState reelFlashState;
    private Color flashColor;
    private final Array<Vector2> reelFlashSegments = new Array<>();
    private int score;
    private Pixmap flashOnReelPixmap;
    private boolean isReelSpinDirectionClockwise = false;

    public ReelTile(
        Texture scrollTexture,
        float x,
        float y,
        float tileWidth,
        float tileHeight,
        int endReel) {
        this.scrollTexture = scrollTexture;
        super.setX(x);
        super.setY(y);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.reelDisplayWidth = tileWidth;
        this.reelDisplayHeight = tileHeight;
        super.setEndReel(endReel);
        defineReelSlotTileScroll();
    }

    public ReelTile(
        Texture texture,
        int numberOfReelsInTexture,
        float x,
        float y,
        float tileWidth,
        float tileHeight,
        float reelDisplayWidth,
        float reelDisplayHeight,
        int endReel) {
        this.scrollTexture = texture;
        this.numberOfReelsInTexture = numberOfReelsInTexture;
        super.setX(x);
        super.setY(y);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.reelDisplayWidth = reelDisplayWidth;
        this.reelDisplayHeight = reelDisplayHeight;
        super.setEndReel(endReel);
        defineReelSlotTileScroll();
    }

    public ReelTile(
        Texture texture,
        int numberOfReelsInTexture,
        float x,
        float y,
        float tileWidth,
        float tileHeight,
        float reelDisplayWidth,
        float reelDisplayHeight,
        int endReel,
        boolean isReelSpinDirectionClockwise) {
        this.scrollTexture = texture;
        this.numberOfReelsInTexture = numberOfReelsInTexture;
        super.setX(x);
        super.setY(y);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.reelDisplayWidth = reelDisplayWidth;
        this.reelDisplayHeight = reelDisplayHeight;
        super.setEndReel(endReel);
        this.isReelSpinDirectionClockwise = isReelSpinDirectionClockwise;
        defineReelSlotTileScroll();
    }


    private void defineReelSlotTileScroll() {
        setPosition((int) super.getX(), (int) super.getY());
        if (scrollTexture != null) {
            scrollTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            region = new TextureRegion(scrollTexture);
            int randomSy = 0;
            if (numberOfReelsInTexture > 0) {
                randomSy = Random.getInstance().nextInt(numberOfReelsInTexture) * (int) tileHeight;
                randomSy = isReelSpinDirectionClockwise ? randomSy : randomSy * -1;
                sy = randomSy;
            }
            if ((reelDisplayWidth == 0) && (reelDisplayHeight == 0)) {
                region.setRegion(0, randomSy, (int) tileWidth, (int) tileHeight);
                setBounds((int) super.getX(), (int) super.getY(), (int) tileWidth, (int) tileHeight);
            } else {
                region.setRegion(0, randomSy, (int) reelDisplayWidth, (int) reelDisplayHeight);
                setBounds((int) super.getX(), (int) super.getY(), (int) reelDisplayWidth, (int) reelDisplayHeight);
            }
            setRegion(region);
        }
        reelFlash = false;
        reelFlashTween = false;
        reelFlashState = FlashState.FLASH_OFF;
        flashColor = Color.RED;
        tileDeleted = false;
        isFallen = true;
        isStoppedFalling = true;
    }

    @Override
    public void update(float delta) {
        if (super.isSpinning())
            processSpinningState();
        if (reelFlashTween)
            processFlashTweenState(delta);
    }

    @Override
    public float getSnapX() {
        int quotient = (int) ((super.getX() + (tileWidth / 2)) / tileWidth);
        return quotient * tileWidth;
    }

    @Override
    public float getSnapY() {
        int quotient = (int) ((super.getY() + (tileHeight / 2)) / tileHeight);
        return quotient * tileHeight;
    }

    public void processSpinningState() {
        float syModulus = sy % scrollTexture.getHeight();
        region.setRegion((int) sx, (int) syModulus, (int)reelDisplayWidth, (int)reelDisplayHeight);
        setRegion(region);
    }

    private void processFlashTweenState(float delta) {
        setFlashOn();
    }

    @Override
    public float getDestinationX() {
        return this.destinationX;
    }

    @Override
    public float getDestinationY() {
        return this.destinationY;
    }

    @Override
    public void setDestinationX(float destinationX) {
        this.destinationX = destinationX;
    }

    @Override
    public void setDestinationY(float destinationY) {
        this.destinationY = destinationY;
    }

    @Override
    public float getSx() {
        return this.sx;
    }

    @Override
    public float getSy() {
        return this.sy;
    }

    @Override
    public void setSx(float sx) {
        this.sx = sx;
    }

    @Override
    public void setSy(float sy) {
        this.sy = sy;
    }

    @Override
    public float getTileWidth() {
        return tileWidth;
    }

    @Override
    public float getTileHeight() {
        return tileHeight;
    }

    @Override
    public void setEndReel() {
        float syModulus = sy % scrollTexture.getHeight();
        super.setEndReel(
            (int) ((int) ((syModulus + (tileHeight / 2)) % scrollTexture.getHeight()) / tileHeight));
    }

    @Override
    public int getCurrentReel() {
        float syModulus = sy % scrollTexture.getHeight();
        return calculateCurrentReel(syModulus);
    }

    @Override
    public boolean isReelTileDeleted() {
        return this.tileDeleted;
    }

    @Override
    public void deleteReelTile() {
        this.tileDeleted = true;
    }

    @Override
    public void unDeleteReelTile() { this.tileDeleted = false; }

    @Override
    public void setIsFallen(boolean isFallen) {
        this.isFallen = isFallen;
    }

    @Override
    public boolean isFallen() {
        return isFallen;
    }

    @Override
    public boolean isStoppedFalling() { return isStoppedFalling; }

    @Override
    public void setIsStoppedFalling(boolean isStoppedFalling) {
        this.isStoppedFalling = isStoppedFalling;
    }

    @Override
    public void startSpinning() {
        super.setSpinning(true);
    }

    @Override
    public void stopSpinning() {
        super.setSpinning(false);
    }

    @Override
    public void stopSpinningSound() {
    }

    @Override
    public FlashState getFlashState() {
        return reelFlashState;
    }

    @Override
    public boolean isFlashing() {
        return reelFlash;
    }

    @Override
    public void setFlashMode(boolean reelFlash) {
        this.reelFlash = reelFlash;
    }

    @Override
    public Color getFlashColor() {
        return this.flashColor;
    }

    @Override
    public void setFlashColor(Color flashColor) {
        this.flashColor = flashColor;
    }

    @Override
    public void setFlashTween(boolean reelFlashTween) {
        this.reelFlashTween = reelFlashTween;
    }

    @Override
    public boolean getFlashTween() {
        return this.reelFlashTween;
    }

    @Override
    public void setFlashOn() {
        reelFlashState = FlashState.FLASH_ON;
    }

    @Override
    public void setFlashOff() {
        reelFlashState = FlashState.FLASH_OFF;
    }

    @Override
    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int getScore() {
        return this.score;
    }

    @Override
    public TextureRegion getRegion() {
        return this.region;
    }

    @Override
    public void dispose() {
        if (flashOnReelPixmap != null)
            flashOnReelPixmap.dispose();
    }

    @Override
    public void setIndex(int index) { this.index = index; }

    @Override
    public int getIndex() { return this.index; }

    @Override
    public void saveRegion() {
        if (region != null)
            PixmapProcessors.saveTextureRegion(region);
    }

    @Override
    public void saveFlashRegion() {
        if(flashReel != null)
            PixmapProcessors.saveTextureRegion(flashReel);
    }

    @Override
    public void resetReel() {
        flashOnReelPixmap = null;
        float syModulus = sy % scrollTexture.getHeight();
        region.setRegion((int) sx, (int) syModulus, (int)reelDisplayWidth, (int)reelDisplayHeight);
        setRegion(region);
    }

    public void resetSy() {
        sy = sy % scrollTexture.getHeight();
    }

    @Override
    public int getScrollTextureHeight() {
        return scrollTexture.getHeight();
    }

    @Override
    public int getNumberOfReelsInTexture() {
        return numberOfReelsInTexture;
    }

    @Override
    public void drawFlashSegments(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(flashColor);
        for (Vector2 reelFlashSegment : reelFlashSegments)
            drawFlashSegment(shapeRenderer, reelFlashSegment);
        shapeRenderer.end();
    }

    private void drawFlashSegment(ShapeRenderer shapeRenderer, Vector2 reelFlashSegment) {
        shapeRenderer.rect(reelFlashSegment.x + 0, reelFlashSegment.y + 0, tileWidth - 0, tileHeight - 0);
        shapeRenderer.rect(reelFlashSegment.x + 1, reelFlashSegment.y + 1, tileWidth - 2, tileHeight - 2);
        shapeRenderer.rect(reelFlashSegment.x + 2, reelFlashSegment.y + 2, tileWidth - 4, tileHeight - 4);
    }

    @Override
    public void addReelFlashSegment(float x, float y) {
        reelFlashSegments.add(new Vector2(x, y));
    }

    @Override
    public Array<Vector2> getReelFlashSegments() {
        return reelFlashSegments;
    }

    @Override
    public void clearReelFlashSegments() {
        reelFlashSegments.clear();
    }

    @Override
    public void updateReelFlashSegments(float x, float y) {
        for (Vector2 reelFlashSegment : reelFlashSegments)
            reelFlashSegment.set(new Vector2(x, y));
    }

    public void setReelDisplaySize(float width, float height) {
        screenDisplayWidth = width;
        screenDisplayHeight = height;
    }

    public void drawOverride(SpriteBatch spritebatch) {
        spritebatch.draw(
            region,
            super.getX(),
            super.getY(),
            screenDisplayWidth == 0 ? region.getRegionWidth() : screenDisplayWidth,
            screenDisplayHeight == 0 ? region.getRegionHeight() : screenDisplayHeight);
    }

    public Array<Integer> getEntityIds() {
        return entityIds;
    }

    public void setEntityIds(Array<Integer> entityIds) {
        this.entityIds = entityIds;
    }

    public boolean isReelSpinDirectionClockwise() {
        return isReelSpinDirectionClockwise;
    }

    public void setReelSpinDirectionClockwise(boolean isReelSpinDirectionClockwise) {
        this.isReelSpinDirectionClockwise = isReelSpinDirectionClockwise;
    }

    private int calculateCurrentReel(float syModulus) {
        return (int) ((isReelSpinDirectionClockwise ?
            ((int) ((syModulus + (tileHeight / 2)) % scrollTexture.getHeight()) / tileHeight) :
            ((int) ((syModulus - (tileHeight / 2)) % scrollTexture.getHeight()) / tileHeight)));
    }
}
