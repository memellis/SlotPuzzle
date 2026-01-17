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

package com.ellzone.slotpuzzle.finitestatemachine;

public enum PlayStates {
    INITIALISING,
    INTRO_SEQUENCE,
    INTRO_POPUP,
    INTRO_SPINNING,
    HIT_SINK_BOTTOM,
    INTRO_FLASHING,
    CREATED_REELS_HAVE_FALLEN,
    PLAYING,
    LEVEL_TIMED_OUT,
    LEVEL_LOST,
    WON_LEVEL,
    RESTARTING_LEVEL,
    REELS_SPINNING,
    REELS_FLASHING,
    BONUS_LEVEL_ENDED;
}
