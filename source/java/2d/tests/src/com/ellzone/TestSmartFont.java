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
		System.out.println("generatedFontDir="+generatedFontDir.file().getAbsolutePath());
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
		//generatedFontDir.deleteDirectory();
	}

	@Test
	public void testDyanmicFontFileExists() {
		fontSmall = fontGen.createFont(exoFile, "exo-small", 24);
		fontMedium = fontGen.createFont(exoFile, "exo-medium", 48);
		fontLarge = fontGen.createFont(exoFile, "exo-large", 64);
		assertTrue(fontSmall != null);
	}
}