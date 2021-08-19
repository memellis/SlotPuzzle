package com.ellzone.slotpuzzle2d.component.artemis;

import com.artemis.Component;
import com.artemis.annotations.DelayedComponentRemoval;
import com.badlogic.gdx.physics.box2d.Body;

@DelayedComponentRemoval
public class Boxed extends Component {
    public Body body;
}