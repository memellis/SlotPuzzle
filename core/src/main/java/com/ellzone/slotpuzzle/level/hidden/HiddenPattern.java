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

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ellzone.slotpuzzle.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;

public abstract class HiddenPattern {
    public static final String HIDDEN_PATTERN_LAYER_NAME = "Hidden Pattern Object";

    protected TiledMap level;
    public HiddenPattern(TiledMap level) {
        this.level = level;
    }

    public abstract boolean isHiddenPatternRevealed(TupleValueIndex[][] grid, Array<ReelTile> reelTiles, int levelWidth, int levelHeight);

    public static class HiddenPatternPuzzleGridException extends GdxRuntimeException {
        public HiddenPatternPuzzleGridException(String message) {
            super(message);
        }
    }
}
