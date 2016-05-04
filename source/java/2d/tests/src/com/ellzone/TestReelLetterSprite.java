package com.ellzone;

import org.junit.runner.RunWith;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.screens.IntroScreen;
import com.ellzone.slotpuzzle2d.sprites.ReelLetter;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import static org.junit.Assert.*;
import java.io.IOException;
import org.jrenner.smartfont.SmartFontGenerator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import de.tomgrill.gdxtesting.GdxTestRunner;

@RunWith(GdxTestRunner.class)
public class TestReelLetterSprite {

	private static final String SLOT_PUZZLE_REEL_TEXT = "Slot Puzzle";
	private static final int EXO_FONT_SMALL_SIZE = 24;
	private static final int REEL_SIZE_WIDTH = 40;
	private static final int REEL_SIZE_HEIGHT = 40;
	private static final int SCROLL_HEIGHT = 20;
	private static final int SCROLL_STEP = 4;
	private static final float SIXTY_FPS = 1.0f / 60.0f;
	private static final String BY_TEXT = "by";
	private SmartFontGenerator fontGen;
	private BitmapFont fontSmall;
	private FileHandle generatedFontDir;
	private FileHandle exoFileInternal;
	private FileHandle exoFile;

	@Before
	public void setUp() throws Exception {
		SmartFontGenerator fontGen = new SmartFontGenerator();
		exoFileInternal = Gdx.files.internal("LiberationMono-Regular.ttf");
		generatedFontDir = Gdx.files.local("generated-fonts/");
		generatedFontDir.mkdirs();
		
		exoFile = Gdx.files.local("generated-fonts/LiberationMono-Regular.ttf");
		try {
	        FileUtils.copyFile(exoFileInternal, exoFile);
		} catch (IOException ex) {
			Gdx.app.error(SlotPuzzle.SLOT_PUZZLE, "Could not copy " + exoFileInternal.file().getPath() + " to file " + exoFile.file().getAbsolutePath() + " " + ex.getMessage());
		}
		fontSmall = fontGen.createFont(exoFile, "exo-small", TestReelLetterSprite.EXO_FONT_SMALL_SIZE);
	}
	
	@After
	public void tearDown() throws Exception {
		generatedFontDir.delete();
	}
	
	@Test
	public void TestReelLetterSpriteCreation() {
		Pixmap textPixmap = new Pixmap(TestReelLetterSprite.REEL_SIZE_WIDTH, TestReelLetterSprite.BY_TEXT.length() * TestReelLetterSprite.REEL_SIZE_HEIGHT, Pixmap.Format.RGBA8888);
		textPixmap = PixmapProcessors.createDynamicVerticalFontText(fontSmall, TestReelLetterSprite.BY_TEXT, textPixmap);
		FileHandle pixmapFile = Gdx.files.local("reelLetterSpriteVertical.png");
		if (pixmapFile.exists()) {
			pixmapFile.delete();
		}
		PixmapProcessors.savePixmap(textPixmap, pixmapFile.file());
		textPixmap = PixmapProcessors.createDynamicScrollAnimatedVerticalText(textPixmap, TestReelLetterSprite.SCROLL_HEIGHT, TestReelLetterSprite.BY_TEXT, TestReelLetterSprite.EXO_FONT_SMALL_SIZE, TestReelLetterSprite.SCROLL_STEP);
		Texture textTexture = new Texture(textPixmap);
		pixmapFile = Gdx.files.local("reelLetterSpriteScrolling.png");
		if (pixmapFile.exists()) {
			pixmapFile.delete();
		}
		PixmapProcessors.savePixmap(textPixmap, pixmapFile.file());
		assertTrue(textTexture != null);
		assertTrue(pixmapFile.exists());
		Screen screen = null;
		Array<ReelLetter> reelLetters = new Array<ReelLetter>();
		for (int i = 0; i < TestReelLetterSprite.BY_TEXT.length(); i++) {			
			reelLetters.add(new ReelLetter(screen, textTexture, TestReelLetterSprite.BY_TEXT.length(), textPixmap.getWidth() / TestReelLetterSprite.REEL_SIZE_WIDTH, TestReelLetterSprite.SIXTY_FPS, (i * TestReelLetterSprite.REEL_SIZE_WIDTH) + 800 / 3.2f, 480 / 2.0f + TestReelLetterSprite.REEL_SIZE_HEIGHT + 10, i));
		}

		Array<Pixmap> startPixmaps = new Array<Pixmap>();
		
		for (int letter = 0; letter < TestReelLetterSprite.BY_TEXT.length(); letter++) {
			TextureRegion startReelLetter = new TextureRegion(textTexture, 0, letter * TestReelLetterSprite.REEL_SIZE_HEIGHT, TestReelLetterSprite.REEL_SIZE_WIDTH, TestReelLetterSprite.REEL_SIZE_HEIGHT);
			startPixmaps.add(PixmapProcessors.getPixmapFromtextureRegion(startReelLetter));
			FileHandle reelLetterPixmapFile = Gdx.files.local("startReelLetterPixmap" + letter + ".png");
			if (reelLetterPixmapFile.exists()) {
				reelLetterPixmapFile.delete();
			}
			PixmapProcessors.savePixmap(startPixmaps.get(letter), reelLetterPixmapFile.file());			
		}
		
		float dt = 0.0f;
		int spinCount = TestReelLetterSprite.BY_TEXT.length();
		while ((dt < 10) & (spinCount > 0)) { 
			for(ReelLetter reel : reelLetters) {
				reel.update(dt);
				if (!reel.isSpinning()) {
					spinCount--;
				}
			}
			dt = dt + TestReelLetterSprite.SIXTY_FPS;
		}

		int endReel = 0;
		Pixmap endPixmap;
		for(ReelLetter reel : reelLetters) {
			FileHandle reelLetterPixmapFile = Gdx.files.local("reelLetterPixmap" + endReel + ".png");
			if (reelLetterPixmapFile.exists()) {
				reelLetterPixmapFile.delete();
			}		
			endPixmap = PixmapProcessors.getPixmapFromSprite(reel);
			endPixmap = PixmapProcessors.scrollPixmapWrap(endPixmap, TestReelLetterSprite.SCROLL_STEP - 1);
			PixmapProcessors.savePixmap(endPixmap, reelLetterPixmapFile.file());

			assertTrue(endReel == reel.getEndReel());
			assertTrue(PixmapProcessors.arePixmapsEqual(startPixmaps.get(endReel), endPixmap));
			endReel++;
		}
		
	}
}
