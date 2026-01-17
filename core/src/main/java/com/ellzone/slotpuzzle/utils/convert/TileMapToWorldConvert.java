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

package com.ellzone.slotpuzzle.utils.convert;

import com.ellzone.slotpuzzle.physics.Point;

public class TileMapToWorldConvert implements WorldConvertInterface {
    public static final int MAP_WIDTH = 4000;
    public static final int MAP_HEIGHT = 16000;
    public static final int WORLD_WIDTH = 10000;
    public static final int WORLD_HEIGHT = 40000;

    private int mapWidth;
    private int mapHeight;
    private int worldWidth;
    private int worldHeight;

    public TileMapToWorldConvert(
        int mapWidth,
        int mapHeight,
        int worldWidth,
        int worldHeight) {
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
    }

    public static float convertTileMapXToWorldX(float x) {
        return x / MAP_WIDTH * WORLD_WIDTH;
    }

    public static float convertTileMapYToWorldY(float y) {
        return y / MAP_HEIGHT * WORLD_HEIGHT;
    }

    public Point convertToWorldPosition(Point point) {
        return new Point(
            convertToWorldX(point.getX()),
            convertToWorldY(point.getY()));
    }

    public float convertToWorldX(float x) {
        return x / mapWidth * worldWidth;
    }

    public float convertToWorldY(float y) {
        return y / mapHeight * worldHeight;
    }

    public int getMapWidth() { return mapWidth; }

    public int getMapHeight() { return mapHeight; }

    public int getWorldWidth() { return worldWidth; }

    public int getWorldHeight() { return worldHeight; }
}
