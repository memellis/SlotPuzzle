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

package com.ellzone;

import static org.junit.Assert.*;
import java.io.IOException;
import org.jrenner.smartfont.SmartFontGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import de.tomgrill.gdxtesting.GdxTestRunnerGetAllTestClasses;

@RunWith(GdxTestRunnerGetAllTestClasses.class)
public class TestSmartFont {

	private static final int EXO_FONT_SMALL_SIZE = 24;
	private static final String SLOT_PUZZLE_REEL_TEXT = "Slot Puzzle";
	private static final int SCROLL_HEIGHT = 20;

	private SmartFontGenerator fontGen;
	private FileHandle exoFileInternal;
	private FileHandle generatedFontDir;
	private FileHandle exoFile;
	private BitmapFont fontSmall;
	
	@Before
	public void setUp() throws Exception {
		fontGen = new SmartFontGenerator();
		exoFileInternal = Gdx.files.internal("LiberationMono-Regular.ttf");
		System.out.println(exoFileInternal.list());
		generatedFontDir = Gdx.files.local("generated-fonts/");
		generatedFontDir.mkdirs();
		exoFile = Gdx.files.local("generated-fonts/LiberationMono-Regular.ttf");
		
		try {
	        FileUtils.copyFile(exoFileInternal, exoFile);
		} catch (IOException ex) {
			Gdx.app.error(SlotPuzzle.SLOT_PUZZLE, "Could not copy " + exoFileInternal.file().getPath() + " to file " + exoFile.file().getAbsolutePath() + " " + ex.getMessage());
		}
	}

	@After
	public void tearDown() throws Exception {
		generatedFontDir.deleteDirectory();
	}

	@Test
	public void testDyanmicFontFileExists() {
		fontSmall = fontGen.createFont(exoFile, "exo-small", 24);
		assertTrue(fontSmall != null);
		FileHandle exoSmallFontFile = Gdx.files.local("generated-fonts/exo-small.fnt");
		FileHandle exoSmallFontPngFile = Gdx.files.local("generated-fonts/exo-small/exo-small.png");
		FileHandle exoMediumFontFile = Gdx.files.local("generated-fonts/exo-medium.fnt");
		FileHandle exoMediumFontPngFile = Gdx.files.local("generated-fonts/exo-medium/exo-medium.png");
		FileHandle exoLargeFontFile = Gdx.files.local("generated-fonts/exo-large.fnt");
		FileHandle exoLargeFontPngFile0 = Gdx.files.local("generated-fonts/exo-large/exo-large_0.png");
		FileHandle exoLargeFontPngFile1 = Gdx.files.local("generated-fonts/exo-large/exo-large_1.png");
		assertTrue(exoSmallFontFile.exists());
		assertTrue(exoSmallFontPngFile.exists());
		assertTrue(exoMediumFontFile.exists());
		assertTrue(exoMediumFontPngFile.exists());
		assertTrue(exoLargeFontFile.exists());
		assertTrue(exoLargeFontPngFile0.exists());
		assertTrue(exoLargeFontPngFile1.exists());
	}
	
	@Test
	/* Test for bug when first creating a font that's not been cached
	 * Bug caused nullPointer exception in PixmapProcessors.createDynamicVerticalFontText when retrieving imagepaths from fontData
	 * Added fix for imagepaths not being populated before font saved
	 */
	public void testCreateDynamicVerticalFontText() throws Exception {
		fontSmall = fontGen.createFont(exoFile, "exo-small", 24);
		Pixmap textPixmap = new Pixmap(TestSmartFont.EXO_FONT_SMALL_SIZE, TestSmartFont.SLOT_PUZZLE_REEL_TEXT.length() * TestSmartFont.SCROLL_HEIGHT, Pixmap.Format.RGBA8888);
		textPixmap = PixmapProcessors.createDynamicVerticalFontText(fontSmall, TestSmartFont.SLOT_PUZZLE_REEL_TEXT, textPixmap);
		assertTrue(textPixmap != null);
	}
}