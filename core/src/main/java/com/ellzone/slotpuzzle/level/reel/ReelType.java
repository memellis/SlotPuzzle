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

package com.ellzone.slotpuzzle.level.reel;

public enum ReelType {
    Cherry("Cherry", 0),
    Cheesecake("Cheesecake", 1),
    Grapes("Grapes", 2),
    Jelly("Jelly", 3),
    Lemon("Lemon", 4),
    Peach("Peach", 5),
    Pear("Peach", 6),
    Tomato("Tomato", 7),
    Bomb("Bomb", 8);

    public final String name;
    public final int index;

    private ReelType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static int getNumberOfReelTypes() {
        return ReelType.values().length;
    }
}
