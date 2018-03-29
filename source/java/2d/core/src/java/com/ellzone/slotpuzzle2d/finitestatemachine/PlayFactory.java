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
