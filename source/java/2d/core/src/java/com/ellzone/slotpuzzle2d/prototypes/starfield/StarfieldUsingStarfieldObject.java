/*
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes.starfield;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.sprites.StarField;
import com.ellzone.slotpuzzle2d.utils.Random;

public class StarfieldUsingStarfieldObject extends SPPrototypeTemplate {
    public static float SCALE = 0.5f;
    public static int NUM_STARS = 255;
    private ShapeRenderer shapeRenderer;
    private StarField starField;

    @Override
    protected void initialiseOverride() {
        shapeRenderer = new ShapeRenderer();
        starField = new StarField(shapeRenderer,
                                  NUM_STARS,
                                  SCALE,
                                  displayWindowWidth,
                                  displayWindowHeight,
                                  Random.getInstance(),
                                  viewport);
    }

    @Override
    protected void initialiseScreenOverride() {

    }

    @Override
    protected void loadAssetsOverride() {

    }

    @Override
    protected void disposeOverride() {

    }

    @Override
    protected void updateOverride(float dt) {

    }

    @Override
    protected void renderOverride(float dt) {
         starField.updateStarfield(dt, shapeRenderer);
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {

    }
}
