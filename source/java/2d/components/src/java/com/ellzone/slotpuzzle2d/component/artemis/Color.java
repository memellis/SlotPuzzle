package com.ellzone.slotpuzzle2d.component.artemis;

import com.artemis.Component;

public class Color extends Component {
    public float red, green, blue, alpha;
    public Color() {}
    public Color(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
}
