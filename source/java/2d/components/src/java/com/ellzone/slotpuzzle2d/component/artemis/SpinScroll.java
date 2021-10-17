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

package com.ellzone.slotpuzzle2d.component.artemis;

import com.badlogic.gdx.math.Interpolation;

import net.mostlyoriginal.api.component.common.ExtendedComponent;
import net.mostlyoriginal.api.component.common.Tweenable;

public class SpinScroll extends ExtendedComponent<SpinScroll> implements Tweenable<SpinScroll> {
    public float sX;
    public float sY;

    public SpinScroll() {}

    public SpinScroll(float sX, float sY) {
        this.sX = sX;
        this.sY = sY;
    }

    @Override
    protected void reset() {
        sX = 0;
        sY = 0;
    }

    @Override
    public void set(SpinScroll spinScroll) {
        sX = spinScroll.sX;
        sY = spinScroll.sY;
    }

    public void set(float sX, float sY) {
        this.sX = sX;
        this.sY = sY;
    }

    @Override
    public void tween(SpinScroll a, SpinScroll b, float value) {
        sY = Interpolation.linear.apply(a.sY, b.sY, value);
    }
}
