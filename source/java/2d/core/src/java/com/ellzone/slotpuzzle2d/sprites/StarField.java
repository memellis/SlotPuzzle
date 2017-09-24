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

package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.physics.Point;

import java.util.Random;

public class StarField {
    public static final float STAR_BASE_FLASH_TIME = 0.3f;
    private Star[] stars;
    private ShapeRenderer shapeRenderer;
    private int numberOfStars;
    private float scale;
    private int starfieldWidth;
    private int starfieldHeight;
    private Random random;
    private Viewport viewport;

    public StarField(ShapeRenderer shapeRenderer, int numberOfStars, float scale, int starfieldWidth, int starfieldHeight, Random random, Viewport viewport) {
        this.shapeRenderer = shapeRenderer;
        this.numberOfStars = numberOfStars;
        this.scale = scale;
        this.starfieldWidth = starfieldWidth;
        this.starfieldHeight = starfieldHeight;
        this.random = random;
        this.viewport = viewport;
        initialiseStarfield();
    }

    private void initialiseStarfield() {
        stars = new Star[numberOfStars];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star();
            stars[i].setPosition(new Point((int) (Math.random() * this.starfieldWidth), (int) (Math.random() * this.starfieldHeight)));
            stars[i].setFlashState(this.random.nextBoolean());
            stars[i].setFlashTimer(random.nextFloat() * STAR_BASE_FLASH_TIME);
            stars[i].setColor(new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat()));
        }
    }

    public void updateStarfield(float delta, ShapeRenderer shapeRenderer) {
        int i, x, y;

        for (i=0; i<stars.length; i++) {
            x = (int)stars[i].getPosition().getX();
            y = (int)stars[i].getPosition().getY();
            if (i%3==0) {
                y-=2;
            }
            else {
                y--;
            }
            if (y < 0) {
                y = starfieldHeight;
            }
            stars[i].getPosition().setLocation(x, y);
            shapeRenderer.setProjectionMatrix(this.viewport.getCamera().combined);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(stars[i].getColor());
            if(stars[i].getFlashState()) {
                shapeRenderer.rect(x, y, (int) (2.0 * this.scale), (int) (2.0 * this.scale));
            }
            shapeRenderer.end();
            stars[i].setFlashTimer((stars[i].getFlashTimer() + delta));
            if (stars[i].getFlashTimer() > STAR_BASE_FLASH_TIME) {
                stars[i].setFlashState(!stars[i].getFlashState());
                stars[i].setFlashTimer(0.0f);
            }
        }
    }
}
