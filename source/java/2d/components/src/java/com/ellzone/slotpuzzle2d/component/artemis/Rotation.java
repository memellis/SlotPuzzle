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

public class Rotation extends ExtendedComponent<Rotation> implements Tweenable<Rotation> {
    public float angle;

    @Override
    protected void reset() {
        angle = 0;
    }

    @Override
    public void set(Rotation rotate) {
        angle = rotate.angle;
    }

    public void set(float angle) {
        this.angle = angle;
    }

    @Override
    public void tween(Rotation a, Rotation b, float value) {
        angle = Interpolation.linear.apply(a.angle, b.angle, value);
    }
}
