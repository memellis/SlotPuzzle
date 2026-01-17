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

package com.ellzone.slotpuzzle.screens;

import com.ellzone.slotpuzzle.SlotPuzzleGame;
import com.ellzone.slotpuzzle.level.LevelDoor;
import com.ellzone.slotpuzzle.scene.MapTile;

public class PlayScreenHiddenPattern extends PlayScreen {
    public PlayScreenHiddenPattern(SlotPuzzleGame game, LevelDoor levelDoor, MapTile mapTile) {
        super(game, levelDoor, mapTile);
    }
}
