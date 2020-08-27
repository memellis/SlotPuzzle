package com.ellzone.slotpuzzle2d.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;

public class ColorComponent extends Component {
    public float red, green, blue, alpha;
    public ColorComponent() {}
    public ColorComponent(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
}
