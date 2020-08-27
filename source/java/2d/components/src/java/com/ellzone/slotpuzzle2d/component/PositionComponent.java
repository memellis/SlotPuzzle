package com.ellzone.slotpuzzle2d.component;

import com.artemis.Component;

public class PositionComponent extends Component {
    public float x, y;
    public PositionComponent() {}
    public PositionComponent (float x, float y) {
        this.x = x;
        this.y = y;
    }
}
