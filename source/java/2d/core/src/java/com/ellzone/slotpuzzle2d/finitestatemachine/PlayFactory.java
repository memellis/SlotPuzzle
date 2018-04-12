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

package com.ellzone.slotpuzzle2d.finitestatemachine;

/*
@startuml

TestPlay --> PlayFactory : asks
Play --> PlayFactory : asks
PlayFactory : +getPlay(playType : String, play : Play) : PlayInterface

PlayInterface : +getNumberOfReelsFalling() : int
PlayInterface : +getNumberOfReelsSpinning() : int
PlayInterface : +getNumberOfReelsDeleted() : int
PlayInterface : +getNumberOfReelsMatched() : int
PlayInterface : +getNumberOfReelsFlashing() : int
PlayInterface : +areReelsFalling() : boolean
PlayInterface : +areReelsSpinning() : boolean
PlayInterface : +areReelsFlashing() : boolean
PlayInterface : +update(delta : float) : void
PlayInterface : +start() : void
PlayInterface : +isStopped : boolean

RealPlay --> PlayInterface : implements
PlaySimulator --> PlayInterface : implements

@enduml
 */

public class PlayFactory {

    public PlayInterface getPlay(String playType, Play play) {
        if (playType == null) {
            return null;
        }
        if (playType.equalsIgnoreCase(PlaySimulator.class.getSimpleName())) {
            return new PlaySimulator(play);
        }
        if (playType.equalsIgnoreCase(RealPlay.class.getSimpleName())) {
            return new RealPlay();
        }
        return null;
    }
}
