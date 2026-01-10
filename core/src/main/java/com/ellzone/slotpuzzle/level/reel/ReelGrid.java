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

package com.ellzone.slotpuzzle.level.reel;

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedReelECS;

public class ReelGrid {
    private float x;
    private float y;
    private float width;
    private float height;
    private Array<AnimatedReelECS> animatedReelsWithinReelGrid = new Array<>();

    public ReelGrid(float x, float y, float width, float height) {
        this.setX(x);
        this.setY(y);
        this.setWidth(width);
        this.setHeight(height);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void addAnimatedReel(AnimatedReelECS animatedReel) {
        animatedReelsWithinReelGrid.add(animatedReel);
    }

    public Array<AnimatedReelECS> getAnimatedReelsWithinReelGrid() {
        return animatedReelsWithinReelGrid;
    }
}
