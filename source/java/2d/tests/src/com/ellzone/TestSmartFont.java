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
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.utils.FileUtils;

import de.tomgrill.gdxtesting.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class TestSmartFont {
	
	/**************************************************************************
	 * TestSmartFont 
	 * No errors when loading means test passed.
	 * 
	 * @throws Exception
	 */

	private SmartFontGenerator fontGen;
	private FileHandle exoFileInternal;
	private FileHandle generatedFontDir;
	private FileHandle exoFile;
	private BitmapFont fontSmall;
	private BitmapFont fontMedium;
	private BitmapFont fontLarge;
	
	@Before
	public void setUp() throws Exception {
		fontGen = new SmartFontGenerator();
		exoFileInternal = Gdx.files.internal("LiberationMono-Regular.ttf");
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
		fontMedium = fontGen.createFont(exoFile, "exo-medium", 48);
		fontLarge = fontGen.createFont(exoFile, "exo-large", 64);
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
}