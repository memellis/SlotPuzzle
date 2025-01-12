/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle.messaging;

public enum MessageType {
    AddedComponent("AddComponent", 0),
    RemovedComponent("RemoveComponent", 1),
    AddedEntity("AddedEntity", 2),
    RemovedEntity("RemovedEntity",3),
    PlayMusic("PlayMusic", 4),
    StopMusic("StopMusic", 5),
    PauseMusic("PauseMusic", 6),
    GetCurrentMusicTrack("GetCurrentMusicTrack", 7),
    PlayAudio("PlayAudio", 8),
    StopAudio("StopAudio", 9),
    PauseAudio("PauseAudio", 10),
    SwapReelsAboveMe("SwapReelsAboveMe", 11),
    ReelsLeftToFall("ReelsLeftToFall", 12),
    ReelSinkReelsLeftToFall("ReelSinkReelsLeftToFall", 13);

    public final String name;
    public final int index;

    private MessageType(String name, int index) {
        this.name = name;
        this.index = index;
    }

    public static int getNumberOfMessageTypes() {
        return MessageType.values().length;
    }
}
