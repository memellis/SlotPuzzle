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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class LevelEntrance {
    private Texture levelEntrance;
    private int width, height;

    public LevelEntrance(int width, int height) {
        this.width = width;
        this.height = height;
        initialiseLevelEntrance();
    }

    private void initialiseLevelEntrance() {
        levelEntrance = createLevelEntrance(width, height);
    }

    private Texture createLevelEntrance(int width, int height) {
        Pixmap levelEntrance = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        levelEntrance.setColor(Color.RED);
        levelEntrance.fillRectangle(0, 0, width, height);
        levelEntrance.setColor(Color.BLACK);
        levelEntrance.fillRectangle(4, 4, width - 8, height - 9);
        levelEntrance.setColor(Color.RED);
        levelEntrance.drawLine(width / 2 - 1, 40, width / 2 - 1, height);
        levelEntrance.drawLine(width / 2 + 1, 40, width / 2 + 1, height);
        levelEntrance.setColor(Color.CORAL);
        levelEntrance.fillRectangle(40 ,40 , 80, 40);
        return new Texture(levelEntrance);
    }

    public Texture getLevelEntrance() {
        return levelEntrance;
    }

    public void setLevelEntrance(Texture levelEntrance) {
        this.levelEntrance = levelEntrance;
    }
}
