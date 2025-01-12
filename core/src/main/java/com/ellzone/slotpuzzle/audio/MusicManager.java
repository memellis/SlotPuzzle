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

import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.audio.Music;
import com.ellzone.slotpuzzle.messaging.MessageType;
import com.ellzone.slotpuzzle.utils.assets.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import java.util.HashMap;

public class MusicManager implements Telegraph {
    private AnnotationAssetManager annotationAssetManager;
    private HashMap<String, Music> musicLibrary;
    private HashMap<Music, String> musicLibraryToName;
    private Music currentlyPlaying;

    public MusicManager(AnnotationAssetManager annotationAssetManager) {
        this.annotationAssetManager = annotationAssetManager;
        initialise();
    }

    private void initialise() {
        musicLibrary = new HashMap<>();
        musicLibrary.put(
            AssetsAnnotation.MUSIC_INTRO_SCREEN,
            (Music) annotationAssetManager.get(AssetsAnnotation.MUSIC_INTRO_SCREEN));
        musicLibraryToName = new HashMap<>();
        musicLibraryToName.put(
            (Music) annotationAssetManager.get(AssetsAnnotation.MUSIC_INTRO_SCREEN),
            AssetsAnnotation.MUSIC_INTRO_SCREEN);
    }

    @Override
    public boolean handleMessage(Telegram message) {
        if (message.message == MessageType.PlayMusic.index) {
            currentlyPlaying = musicLibrary.get(AssetsAnnotation.MUSIC_INTRO_SCREEN);
            currentlyPlaying.play();
            MessageManager.getInstance().dispatchMessage(MessageType.GetCurrentMusicTrack.index, currentlyPlaying);
            return true;
        }
        if (message.message == MessageType.StopMusic.index) {
            if (currentlyPlaying != null)
                currentlyPlaying.stop();
            return true;
        }
        if (message.message == MessageType.PauseMusic.index) {
            if (currentlyPlaying != null)
                currentlyPlaying.pause();
            return true;
        }
        return false;
    }

    public String getCurrentlyPlaying() {
        return currentlyPlaying == null ? null : musicLibraryToName.get(currentlyPlaying);
    }
}
