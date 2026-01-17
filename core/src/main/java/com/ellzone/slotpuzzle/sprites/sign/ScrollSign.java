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

package com.ellzone.slotpuzzle.sprites.sign;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

import lombok.Getter;
import lombok.Setter;

public class ScrollSign extends Sprite {
    private Texture signTexture;
    private Array<Texture> signTextures;
    @Getter
    private TextureRegion region;
    private final float x;
    private final float y;
    private final float signWidth;
    private final float signHeight;
    @Setter
    @Getter
    private float sx;
    @Setter
    @Getter
    private float sy;
    public static enum SignDirection {LEFT, RIGHT};
    private final SignDirection signDirection;
    @Getter
    private int currentSign, newSign;
    private boolean switchSign = false;

    public ScrollSign(Texture signTexture, float x, float y, float signWidth, float signHeight, SignDirection signDirection) {
        this.signTexture = signTexture;
        this.x = x;
        this.y = y;
        this.signWidth = signWidth;
        this.signHeight = signHeight;
        this.signDirection = signDirection;
        defineScrollSign();
    }

    public ScrollSign(Array<Texture> signTextures, float x, float y, float signWidth, float signHeight, SignDirection signDirection) {
        this.signTextures = signTextures;
        this.x = x;
        this.y = y;
        this.signWidth = signWidth;
        this.signHeight = signHeight;
        this.signDirection = signDirection;
        defineScrollSign();
    }

    void defineScrollSign() {
        sx = 0;
        sy = 0;
        setPosition((int)x, (int)y);
        setOrigin((int)x, (int)y);
        if (signTexture != null) {
            signTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
            region = new TextureRegion(signTexture);
            region.setRegion(0, 0, (int) signWidth, (int) signHeight);
            setBounds((int) x, (int) y, (int) signWidth, (int) signHeight);
            setRegion(region);
        } else {
            if (signTextures != null) {
                for (Texture signTexture : new Array.ArrayIterator<>(signTextures)) {
                    signTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
                }
                currentSign = 0;
                region = new TextureRegion(signTextures.get(currentSign));
                region.setRegion(0, 0, (int) signWidth, (int) signHeight);
                setBounds((int) x, (int) y, (int) signWidth, (int) signHeight);
                setRegion(region);
            }
        }
    }

    public void update(float dt) {
        float sxModulus;
        if (signTexture != null) {
            sxModulus = sx % signTexture.getWidth();
        } else {
            sxModulus = sx % signTextures.get(currentSign).getWidth();
            if (switchSign) {
                if (sxModulus == 0) {
                    sx = 0;
                    switchSign = false;
                    currentSign = newSign;
                    signTextures.get(getCurrentSign()).setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
                    region = new TextureRegion(signTextures.get(currentSign));
                    sxModulus = sx % signTextures.get(currentSign).getWidth();
                }
            }
        }

        region.setRegion((int) sxModulus, (int) sy, (int) signWidth, (int) signHeight);
        setRegion(region);
    }

    public int getSignWidth() {
        return (int) this.signWidth;
    }

    public int getSignHeight() {
        return (int) this.signHeight;
    }

    public int getTextureWidth() {
        if (signTexture != null) {
            return signTexture.getWidth();
        }
        return signTextures.get(currentSign).getWidth();
    }

    public void switchSign(int newSign) {
        if (signTextures == null) {
            throw new IllegalArgumentException("The sign textures haven't been initialised");
        }
        if (newSign < 0) {
            throw new IllegalArgumentException("Switching to a new sign value can't be a negative value");
        }
        if (newSign > this.signTextures.size) {
            throw new IllegalArgumentException("Switching to a new sign value can't be greater than the number of sign textures which is: " + this.signTextures.size);
        }
        this.newSign = newSign;
        switchSign = true;
    }

    public Array<Texture> getSignTextures() {
        if (signTexture != null) {
            return null;
        }
        return signTextures;
    }

    public void dispose() {
        if (region != null) {
            region.getTexture().dispose();
        }
    }
}
