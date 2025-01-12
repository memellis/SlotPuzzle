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

package com.ellzone.slotpuzzle.sprites.reel;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ellzone.slotpuzzle.physics.DampenedSineParticle;

public interface AnimatedReelInterface {
    void setupSpinning();

    void setX(float x);

    void setY(float y);

    void setSx(float sx);

    void setSy(float sy);

    void setEndReel(int endReel);

    Texture getTexture();

    float getX();

    float getY();

    float getSx();

    float getSy();

    float getTileWidth();

    float getTileHeight();

    float getReelDisplayWidth();

    float getReelDisplayHeight();

    int getEndReel();

    ReelTile getReel();

    DampenedSineParticle.DSState getDampenedSineState();

    void update(float delta);

    void draw(SpriteBatch spriteBatch);

    void draw(ShapeRenderer shapeRenderer);

    void reinitialise();
}
