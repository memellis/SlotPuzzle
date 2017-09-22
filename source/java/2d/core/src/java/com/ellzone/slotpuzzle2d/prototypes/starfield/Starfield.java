/*******************************************************************************
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
 ******************************************************************************/

package com.ellzone.slotpuzzle2d.prototypes.starfield;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;

import java.awt.Point;

public class Starfield extends SPPrototypeTemplate {
    class Star {
        public Point position;
        public boolean flashState;
        public float flashTimer;
        public Color color;
    }
    public static float SCALE = 0.5f;
    public static int NUM_STARS = 1024;
    private Star[] stars;
    private ShapeRenderer shapeRenderer;

    @Override
    protected void initialiseOverride() {
        stars = new Star[NUM_STARS];
        for (int i=0; i<stars.length; i++) {
            stars[i] = new Star();
            stars[i].position = new Point((int)(Math.random()*displayWindowWidth), (int)(Math.random()*displayWindowHeight));
            stars[i].flashState = random.nextBoolean();
            stars[i].flashTimer = random.nextFloat()*0.5f;
            stars[i].color = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0);
        }
        shapeRenderer = new ShapeRenderer();
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
    protected void renderOverride(float delta) {
        updateBackground(delta, shapeRenderer);
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {

    }

    private void updateBackground(float delta, ShapeRenderer shapeRenderer) {
        int i, x, y;

        for (i=0; i<stars.length; i++) {

            x = (int)stars[i].position.getX();
            y = (int)stars[i].position.getY();
            if (i%3==0) {
                y-=2;
            }
            else {
                y--;
            }

            if (y < 0) {
                y = displayWindowHeight;
            }
            stars[i].position.setLocation(x, y);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(stars[i].color);
            if(stars[i].flashState) {
                shapeRenderer.rect(x, y, (int) (2.0 * SCALE), (int) (2.0 * SCALE));
            }
            shapeRenderer.end();
            stars[i].flashTimer += delta;
            if (stars[i].flashTimer > 0.25f) {
                stars[i].flashState = ! stars[i].flashState;
                stars[i].flashTimer = 0;
            }
        }
    }
 }
