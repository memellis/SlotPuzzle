package com.ellzone.slotpuzzle2d.component;

import com.artemis.Component;
import com.badlogic.gdx.graphics.Color;

public class ColorComponent extends Component {
    public Color color;
    public ColorComponent(float red, float green, float blue, float alpha) {
        this.color = new Color(red, green, blue, alpha);
    }
}
