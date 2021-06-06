package com.ellzone.slotpuzzle2d.component.artemisosb;

import com.artemis.Component;

import net.mostlyoriginal.api.component.common.ExtendedComponent;

public class Position extends ExtendedComponent {
    public float x, y;
    public Position() {}

    @Override
    protected void reset() {

    }

    public Position(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void set(Component component) {

    }
}
