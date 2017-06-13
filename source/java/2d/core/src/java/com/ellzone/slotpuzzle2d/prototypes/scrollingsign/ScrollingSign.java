/*******************************************************************************
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
 ******************************************************************************/


package com.ellzone.slotpuzzle2d.prototypes.scrollingsign;

import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.sprites.ScrollSign;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Texture;
import org.jrenner.smartfont.SmartFontGenerator;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Gdx;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import java.io.IOException;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.badlogic.gdx.graphics.Pixmap;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.badlogic.gdx.math.Vector2;

public class ScrollingSign extends SPPrototypeTemplate {
	public static final String SLOT_PUZZLE = "Slot Puzzle";
    public static final String LIBERATION_MONO_REGULAR_FONT_NAME = "LiberationMono-Regular.ttf";
    public static final String GENERATED_FONTS_DIR = "generated-fonts/";
    public static final String FONT_SMALL = "exo-small";
    public static final int FONT_SMALL_SIZE = 24;
	public static final int SIGN_WIDTH = 32;
	public static final int SIGN_HEIGHT = 32;
	private BitmapFont fontSmall;
    private Texture textTexture;
    private Vector2 touch;
	private ScrollSign scrollSign;
	
	@Override
	protected void initialiseOverride() {
		initialiseFonts();
        initialiseFontTexture(SLOT_PUZZLE);
		scrollSign = new ScrollSign(textTexture, 400, 400, SIGN_WIDTH * SLOT_PUZZLE.length(), SIGN_HEIGHT, ScrollSign.SignDirection.RIGHT);
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
	
	private void initialiseFontTexture(String text) {
        Pixmap textPixmap = new Pixmap(text.length() * SIGN_WIDTH, SIGN_WIDTH, Pixmap.Format.RGBA8888);
        textPixmap = PixmapProcessors.createDynamicHorizontalFontText(fontSmall, text, textPixmap);
        textTexture = new Texture(textPixmap);
        touch = new Vector2(0, 0);
	}
	
	@Override
	protected void loadAssetsOverride() {
	}

	@Override
	protected void disposeOverride() {
	}

	@Override
	protected void updateOverride(float dt) {
		scrollSign.update(dt);
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
}
 
