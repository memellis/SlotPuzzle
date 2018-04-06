/*
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes.scrollingsign;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.sprites.ScrollSign;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import org.jrenner.smartfont.SmartFontGenerator;
import java.io.IOException;
import java.util.Random;

public class ScrollingSignDynamicSignChange extends SPPrototypeTemplate {
    public static final String SLOT_PUZZLE = "Slot Puzzle ";
    public static final String LIBERATION_MONO_REGULAR_FONT_NAME = "LiberationMono-Regular.ttf";
    public static final String GENERATED_FONTS_DIR = "generated-fonts/";
    public static final String FONT_SMALL = "exo-small";
    public static final int FONT_SMALL_SIZE = 24;
    public static final int SIGN_WIDTH = 32;
    public static final int SIGN_HEIGHT = 32;
    public static final int SCROLL_SIGN_TIME = 15;
    private BitmapFont fontSmall;
    private Texture textTexture;
    private Array<Texture> textTextures;
    private Vector2 touch;
    private ScrollSign scrollSign;
    private Array<String> scrollSignMessages;
    private int sx;
    private boolean startScrollSignTimer = true;
    private int scrollSignTimer = SCROLL_SIGN_TIME;
    private float timeCount = 0;
    private int useScoreSignCount = 2;
    private Random random;

    @Override
    protected void initialiseOverride() {
        initialiseFonts();
        scrollSignMessages = initialiseScrollSignMessages(scrollSignMessages);
        textTextures = initialiseFontTextures(scrollSignMessages, textTextures);
        scrollSign = new ScrollSign(textTextures, (displayWindowWidth - textTextures.get(0).getWidth()) / 2, (displayWindowHeight - textTextures.get(0).getHeight()) / 2 , SIGN_WIDTH * 4, SIGN_HEIGHT, ScrollSign.SignDirection.RIGHT);
        sx = 0;
        touch = new Vector2(0, 0);
        random = new Random();
    }

    @Override
    protected void initialiseScreenOverride() {
    }

    private void initialiseFonts() {
        SmartFontGenerator fontGen = new SmartFontGenerator();
        FileHandle internalFontFile = Gdx.files.internal(LIBERATION_MONO_REGULAR_FONT_NAME);
        FileHandle generatedFontDir = Gdx.files.local(GENERATED_FONTS_DIR);
        generatedFontDir.mkdirs();

        FileHandle generatedFontFile = Gdx.files.local("generated-fonts/LiberationMono-Regular.ttf");
        try {
            FileUtils.copyFile(internalFontFile, generatedFontFile);
        } catch (IOException ex) {
            Gdx.app.error(SlotPuzzleConstants.SLOT_PUZZLE, "Could not copy " + internalFontFile.file().getPath() + " to file " + generatedFontFile.file().getAbsolutePath() + " " + ex.getMessage());
        }
        fontSmall = fontGen.createFont(generatedFontFile, FONT_SMALL, FONT_SMALL_SIZE);
    }

    private Array<String> initialiseScrollSignMessages(Array scrollSignMessages) {
        scrollSignMessages = new Array<String>();
        scrollSignMessages.add(SLOT_PUZZLE);
        scrollSignMessages.add(SLOT_PUZZLE + "level completed ");
        return scrollSignMessages;
    }

    private Array<Texture> initialiseFontTextures(Array<String> textureTexts, Array<Texture> textTextures) {
        textTextures = new Array<Texture>();
        for (String textureText : textureTexts) {
            Pixmap textPixmap = new Pixmap(textureText.length() * SIGN_WIDTH / 2, SIGN_HEIGHT, Pixmap.Format.RGBA8888);
            textPixmap = PixmapProcessors.createDynamicHorizontalFontTextViaFrameBuffer(fontSmall, Color.WHITE, textureText, textPixmap, 3, 20);
            textTexture = new Texture(textPixmap);
            textTextures.add(textTexture);
        }
        return  textTextures;
    }

    @Override
    protected void loadAssetsOverride() {
    }

    @Override
    protected void disposeOverride() {
    }

    @Override
    protected void updateOverride(float dt) {
        if (startScrollSignTimer) {
            timeCount += dt;
            if(timeCount >= 1){
                if (scrollSignTimer > 0) {
                    scrollSignTimer--;
                } else {
                    scrollSign.switchSign(scrollSign.getCurrentSign() == 0 ? 1 : 0);
                    scrollSignTimer = SCROLL_SIGN_TIME;
                    if (useScoreSignCount > 0) {
                        useScoreSignCount--;
                    } else {
                        dynamicallyChangeSign();
                    }
                }
                timeCount = 0;
            }
        }

        scrollSign.update(dt);
        scrollSign.setSx((scrollSign.getSx() + 1));
    }

    @Override
    protected void renderOverride(float dt) {
        batch.begin();
        scrollSign.draw(batch);
        batch.end();
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {
    }

    private void dynamicallyChangeSign() {
        Array<Texture> signTextures = scrollSign.getSignTextures();
        int score = random.nextInt();
        String textureText = SLOT_PUZZLE + "level completed with Score: " + score;
        Pixmap textPixmap = new Pixmap(textureText.length() * SIGN_WIDTH / 2, SIGN_HEIGHT, Pixmap.Format.RGBA8888);
        textPixmap = PixmapProcessors.createDynamicHorizontalFontTextViaFrameBuffer(fontSmall, Color.WHITE, textureText, textPixmap, 3, 20);
        textTexture = new Texture(textPixmap);
        signTextures.set(1, textTexture);
    }
}
