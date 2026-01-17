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

package com.ellzone.slotpuzzle.level.popups;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;

import net.dermetfan.gdx.Typewriter;

public class LevelPopUpTypeWriterText  extends LevelPopUp{
    private Typewriter typewriter = new Typewriter();
    private boolean finishedTyping = false;

    public LevelPopUpTypeWriterText(SpriteBatch batch,
                                    TweenManager tweenManager,
                                    Array<Sprite> sprites,
                                    BitmapFont levelFont,
                                    String currentLevel,
                                    String levelDescription) {
        super(batch,
            tweenManager,
            sprites,
            levelFont,
            currentLevel,
            levelDescription);
        iniialiseTypewriter();
    }

    public void hideLevelPopUp(TweenCallback callback) {
        super.hideLevelPopUp(callback);
        typewriter.setTime(0);
    }

    private void iniialiseTypewriter() {
        typewriter.getInterpolator().setInterpolation(Interpolation.linear);
        setTypewriterCustomCusors();
    }

    private void setTypewriterCustomCusors() {
        typewriter.getAppender().set(new CharSequence[] {"", ".", "..", "..."}, 1.5f / 4f);
        typewriter.getAppender().set(new CharSequence[] {" ", "_"}, 1.5f / 4f);
    }

    public void draw(SpriteBatch batch) {
        batch.begin();
        for (Sprite sprite : sprites) {
            sprite.draw(batch);
        }
        levelFont.draw(batch, currentLevel, sprites.get(1).getX() + 100, sprites.get(1).getY() + 32);
        batch.end();
    }

    public void drawSpeechBubble(SpriteBatch batch, float delta) {
        if (state == STATE.SHOW_POPUP) {
            CharSequence typeWriterText = typewriter.updateAndType(levelDescription, delta);
            batch.begin();
            font.draw(batch,
                typeWriterText,
                sW / 2 - 110,
                sH / 2,
                200,
                Align.right,
                true);
            batch.end();
            if (!finishedTyping && typeWriterText.length() >= levelDescription.length())
                finishedTyping = true;
        }
    }
}
