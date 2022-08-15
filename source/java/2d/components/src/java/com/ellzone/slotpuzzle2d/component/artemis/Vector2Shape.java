package com.ellzone.slotpuzzle2d.component.artemis;

import com.artemis.Component;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class Vector2Shape extends Component {
    private Array<Vector2> vectors;

    public Vector2Shape() {
    }

    public Vector2Shape(Array<Vector2> vectors) {
        this.setVectors(vectors);
    }

    public Array<Vector2> getVectors() {
        return vectors;
    }

    public void setVectors(Array<Vector2> vectors) {
        this.vectors = vectors;
    }
}
