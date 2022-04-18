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
