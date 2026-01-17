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

package com.ellzone.slotpuzzle.level.creator.utils;

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.utils.Random;

public class FilterReelBoxes {
    public static Array<Integer> filterReelBoxesByDifficultyLevel(Array<Integer> deletedReelBoxes, float difficultyLevelFactor) {
        Array<Integer> filterReplacementReelBoxes = new Array<>();
        if (deletedReelBoxes.size == 0)
            return filterReplacementReelBoxes;
        int numberOfReelBoxesToBeSelected = (int) Math.ceil(deletedReelBoxes.size * difficultyLevelFactor);

        do {
            int next = Random.getInstance().nextInt(deletedReelBoxes.size);
            int nextIndex = deletedReelBoxes.get(next);
            if (!filterReplacementReelBoxes.contains(nextIndex, true))
                filterReplacementReelBoxes.add(nextIndex);
        } while (filterReplacementReelBoxes.size < numberOfReelBoxesToBeSelected);
        return filterReplacementReelBoxes;
    }
}
