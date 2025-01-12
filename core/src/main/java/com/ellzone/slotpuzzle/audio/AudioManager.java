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

package com.ellzone.slotpuzzle.audio;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.audio.Sound;
import com.ellzone.slotpuzzle.messaging.MessageType;
import com.ellzone.slotpuzzle.utils.assets.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import java.util.HashMap;
import java.util.Map;

public class AudioManager implements Telegraph {

    private AnnotationAssetManager annotationAssetManager;
    private Map<String, Sound> audioLibrary;
    private Map<Sound, String> audioLibraryToName;
    private Map<Long, Long> soundsPlayed = new HashMap<Long, Long>();
    private int maxMessages = 20;
    private long timeUnit = 1000L;

    public AudioManager(AnnotationAssetManager annotationAssetManager) {
        this.annotationAssetManager = annotationAssetManager;
        initialise();
    }

    private void initialise() {
        initialiseAudioLibrary();
        createAudoLibrary();
    }

    private void initialiseAudioLibrary() {
        audioLibrary = new HashMap<>();
        audioLibraryToName = new HashMap<>();
    }

    private void createAudoLibrary() {
        addSoundToAudioLibrary(AssetsAnnotation.SOUND_REEL_SPINNING);
        addSoundToAudioLibrary(AssetsAnnotation.SOUND_REEL_STOPPED);
        addSoundToAudioLibrary(AssetsAnnotation.SOUND_PULL_LEVER);
        addSoundToAudioLibrary(AssetsAnnotation.SOUND_CHA_CHING);
    }

    private void addSoundToAudioLibrary(String sound) {
        audioLibrary.put(
            sound,
            (Sound) annotationAssetManager.get(sound)
        );
        audioLibraryToName.put(
            (Sound) annotationAssetManager.get(sound),
            sound
        );
    }

    @Override
    public boolean handleMessage(Telegram message) {
        if (message.message == MessageType.PlayAudio.index) {
            if (maxMessages > 0) {
                int numberOfSoundsPlayingSinceTimeInMilliSeconds = getNumberSoundsPlayingSinceTimeInMilliSeconds(System.currentTimeMillis() - timeUnit);
                if (numberOfSoundsPlayingSinceTimeInMilliSeconds > maxMessages)
                    return true;
            }

            String soundToPlay = (String) message.extraInfo;
            Sound sound = audioLibrary.get(soundToPlay);
            Long soundId = new Long(sound.play());
            soundsPlayed.put(soundId, System.currentTimeMillis());
            return true;
        }
        return false;
    }

    public int getNumberSoundsPlayingSinceTimeInMilliSeconds(Long sinceTime) {
        int numberOfSoundsPlayedSince = 0;
        for (Map.Entry<Long, Long> soundEntry : soundsPlayed.entrySet()) {
            Long timeSoundPlayed = soundEntry.getValue();
            if (timeSoundPlayed > sinceTime)
                numberOfSoundsPlayedSince++;
        }
        return numberOfSoundsPlayedSince;
    }

    public void setMaxMessagesPerTimeUnit(int maxMessages, Long timeUnit) {

    }

    public String[] getAudioTracksNames() {
        return (String[]) audioLibrary.keySet().toArray(new String[audioLibrary.size()]);
    }
}
