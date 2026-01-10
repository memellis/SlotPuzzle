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

package com.ellzone.slotpuzzle.sprites.lights;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.sprites.lights.LightButton;


import box2dLight.RayHandler;

public class HoldLightButton extends LightButton {
    private Array<Integer> entityIds;

    public HoldLightButton(
        World world,
        RayHandler rayHandler,
        float positionX,
        float positionY,
        int buttonWidth,
        int buttonHeight) {
        super(
            world,
            rayHandler,
            positionX,
            positionY,
            buttonWidth,
            buttonHeight,
            new BitmapFont(),
            "",
            "Hold",
            true);
    }

    public void setEntityIds(Array<Integer> entityIds) {
        this.entityIds = entityIds;
    }
}
