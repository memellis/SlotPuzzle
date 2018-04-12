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

package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.graphics.Color;
import com.ellzone.slotpuzzle2d.physics.Point;

class Star {
    private Point position;
    private boolean flashState;
    private float flashTimer;
    private Color color;

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public boolean isFlashState() {
        return flashState;
    }

    public void setFlashState(boolean flashState) {
        this.flashState = flashState;
    }

    public boolean getFlashState() {
        return this.flashState;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public float getFlashTimer() {
        return flashTimer;
    }

    public void setFlashTimer(float flashTimer) {
        this.flashTimer = flashTimer;
    }
}