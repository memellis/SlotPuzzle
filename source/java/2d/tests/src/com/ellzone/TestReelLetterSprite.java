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
	private static final String BY_TEXT = "by";
	private static final int EXO_FONT_SMALL_SIZE = 24;
	private static final int REEL_SIZE_WIDTH = 40;
	private static final int REEL_SIZE_HEIGHT = 40;
	private static final int SCROLL_HEIGHT = 20;
	private static final int SCROLL_STEP = 4;
	private static final float SIXTY_FPS = 1.0f / 60.0f;
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
	public void TestReelLetterSpriteByString() {
        for (int numberOfTests = 0; numberOfTests < 100; numberOfTests++) {
            testReelLetter(TestReelLetterSprite.BY_TEXT);
        }
	}
	
	@Test
	public void TestReelLetterSpriteSlotPuzzleString() {
		testReelLetter(TestReelLetterSprite.SLOT_PUZZLE_REEL_TEXT);
	}

	private void testReelLetter(String reelText) {
		Pixmap textPixmap = new Pixmap(TestReelLetterSprite.REEL_SIZE_WIDTH, reelText.length() * TestReelLetterSprite.REEL_SIZE_HEIGHT, Pixmap.Format.RGBA8888);
		textPixmap = PixmapProcessors.createDynamicVerticalFontText(fontSmall, reelText, textPixmap);
		savePixmap(textPixmap, "reelLetterSpriteVertical.png");
		textPixmap = PixmapProcessors.createDynamicScrollAnimatedVerticalText(textPixmap, TestReelLetterSprite.SCROLL_HEIGHT, reelText, TestReelLetterSprite.EXO_FONT_SMALL_SIZE, TestReelLetterSprite.SCROLL_STEP);
		Texture textTexture = new Texture(textPixmap);
		savePixmap(textPixmap, "reelLetterSpriteScrolling.png");
		assertTrue(textTexture != null);
		Screen screen = null;
		Array<ReelLetter> reelLetters = new Array<ReelLetter>();
		for (int i = 0; i < reelText.length(); i++) {			
			reelLetters.add(new ReelLetter(screen, textTexture, reelText.length(), textPixmap.getWidth() / TestReelLetterSprite.REEL_SIZE_WIDTH, TestReelLetterSprite.SIXTY_FPS, (i * TestReelLetterSprite.REEL_SIZE_WIDTH) + 800 / 3.2f, 480 / 2.0f + TestReelLetterSprite.REEL_SIZE_HEIGHT + 10, i));
		}

		Array<Pixmap> startPixmaps = new Array<Pixmap>();
		for (int letter = 0; letter < reelText.length(); letter++) {
			TextureRegion startReelLetter = new TextureRegion(textTexture, 0, letter * TestReelLetterSprite.REEL_SIZE_HEIGHT, TestReelLetterSprite.REEL_SIZE_WIDTH, TestReelLetterSprite.REEL_SIZE_HEIGHT);
			startPixmaps.add(PixmapProcessors.getPixmapFromTextureRegion(startReelLetter));
			savePixmap(startPixmaps.get(letter), "startReelLetterPixmap" + letter + ".png");
		}
	
		float dt = 0.0f;
		int spinCount = reelText.length();
		boolean[] spin = new boolean[spinCount];
		for (int i=0; i<spin.length;i++) {
			spin[i] = true;
		}
		boolean reelsStoppedSpinning = false;
		while (!reelsStoppedSpinning) {
			int index = 0;
			for(ReelLetter reel : reelLetters) {
				reel.update(dt);
				if (!reel.isSpinning()) {
					spin[index] = false;
					index++;
				}
			}
			for (int i=0; i<spin.length;i++) {
				if (!spin[i]) {
					reelsStoppedSpinning = true;
				} else {
					reelsStoppedSpinning = false;
				}
			}
			dt = dt + TestReelLetterSprite.SIXTY_FPS;
		}

		int endReel = 0;
		Pixmap endPixmap;
		for(ReelLetter reel : reelLetters) {
			endPixmap = PixmapProcessors.getPixmapFromSprite(reel);
			endPixmap = PixmapProcessors.scrollPixmapWrap(endPixmap, 3);
            savePixmap(endPixmap, "reelLetterPixmap" + endReel + ".png");
            assertTrue(endReel == reel.getEndReel());
			assertTrue(PixmapProcessors.arePixmapsEqual(startPixmaps.get(endReel), endPixmap));
			endReel++;
		}
	}

	private void savePixmap(Pixmap pixmap, String pixmapFileName) {
		FileHandle pixmapFile = Gdx.files.local(pixmapFileName);
		if (pixmapFile.exists()) {
			pixmapFile.delete();
		}
		PixmapProcessors.savePixmap(pixmap, pixmapFile.file());
	}
}

