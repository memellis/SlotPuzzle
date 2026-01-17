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

package com.ellzone.slotpuzzle.level.hidden;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;

public class HiddenShape extends HiddenPattern {
    public HiddenShape(TiledMap level) {
        super(level);
    }

    @Override
    public boolean isHiddenPatternRevealed(TupleValueIndex[][] grid, Array<ReelTile> reelTiles, int levelWidth, int levelHeight) {
        for (MapObject mapObject : level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            int c = PuzzleGridTypeReelTile.getColumnFromLevel(mapRectangle.getX());
            int r = PuzzleGridTypeReelTile.getRowFromLevel(mapRectangle.getY(), levelHeight);
            if ((r >= 0) & (r <= levelHeight) & (c >= 0) & (c <= levelWidth)) {
                if (grid[r][c] != null) {
                    if (!reelTiles.get(grid[r][c].getIndex()).isReelTileDeleted())
                        return false;
                } else
                    throw new HiddenPattern.HiddenPatternPuzzleGridException(String.format("Grid cell r=%d, c=%d is null", r, c));
            } else
                throw new HiddenPattern.HiddenPatternPuzzleGridException(String.format("Grid cell r=%d, c=%d has exceeded the grid limits: width=%d, height=%d", r, c, levelWidth, levelHeight));
        }
        return true;
    }
}
