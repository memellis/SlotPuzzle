package com.ellzone.slotpuzzle2d.screens;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.jrenner.smartfont.SmartFontGenerator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.sprites.ReelLetter;
import com.ellzone.utils.PixmapProcessors;

public class IntroScreen implements Screen {
	private static final int FRAME_COLS = 54;
	private static final int FRAME_ROWS = 11;
	private static final int TEXT_SPACING_SIZE = 30;
	private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz. ";
	private static final String SLOT_PUZZLE_REEL_TEXT = "Slot Puzzle";
	private static final String BY_TEXT = "by";
	private static final String AUTHOR_TEXT = "Mark Ellis";
	
	private SlotPuzzle game;
	private Texture texture;
	private Texture reelSheet;
	private TextureRegion[] reelFrames;
	private TextureRegion currentFrame;
	private Animation reelAnimation;
	private float stateTime;
	private Pixmap slotReelPixmap;
	private Texture slotReelTexture;
	private OrthographicCamera gameCamera;
	private Viewport gameViewport;
	private BitmapFont fontSmall;
	private BitmapFont fontMedium;
	private BitmapFont fontLarge;
	private Array<ReelLetter> introScreenLetters;
	
	public IntroScreen(SlotPuzzle game) {
		this.game = game;
		
		slotReelPixmap = new Pixmap(24, 216, Pixmap.Format.RGBA8888);		
		
		gameCamera = new OrthographicCamera();
		gameViewport = new FitViewport(800, 480, gameCamera);
		
		defineIntroScreen();

	}
	
	void defineIntroScreen() {
		SmartFontGenerator fontGen = new SmartFontGenerator();
		FileHandle exoFileInternal = Gdx.files.internal("LiberationMono-Regular.ttf");
		FileHandle generatedFontDir = Gdx.files.local("generated-fonts/");
		generatedFontDir.mkdirs();
		
		FileHandle exoFile = Gdx.files.local("generated-fonts/LiberationMono-Regular.ttf");
		
		try {
	        copyFile(exoFileInternal.file(), exoFile.file());
		} catch (IOException ex) {
			System.out.println("Could not copy " + exoFileInternal.file().getAbsolutePath() + " to file " + exoFile.file().getAbsolutePath());
			System.out.println("Error=" + ex.getMessage());
		}
		
		fontSmall = fontGen.createFont(exoFile, "exo-small", 24);
		fontMedium = fontGen.createFont(exoFile, "exo-medium", 48);
		fontLarge = fontGen.createFont(exoFile, "exo-large", 64);
		
		if (Gdx.files.local("SlotPuzzleTextFontTile.png").exists()) {
			texture = new Texture(Gdx.files.local("SlotPuzzleTextFontTile.png"));
			Gdx.app.log(SlotPuzzle.SLOT_PUZZLE, "Loaded cached SlotPuzzleTextFontTile.png file.");
				
		} else {
				
			introScreenLetters = new Array<ReelLetter>();

			slotReelPixmap = createDynamicVerticalFontText(fontSmall, IntroScreen.SLOT_PUZZLE_REEL_TEXT, slotReelPixmap);
			slotReelPixmap = createDynamicScrollAnimatedVerticalText(slotReelPixmap, 20);
			slotReelTexture = new Texture(slotReelPixmap);

			for (int i = 0; i < IntroScreen.SLOT_PUZZLE_REEL_TEXT.length(); i++) {
				introScreenLetters.add(new ReelLetter(this, slotReelTexture, 11, 54, 0.050f, (i * IntroScreen.TEXT_SPACING_SIZE) + gameViewport.getWorldWidth() / 5.0f, gameViewport.getWorldHeight() / 2.0f + IntroScreen.TEXT_SPACING_SIZE + 10, i));
			}

			// FIXME Weird disappearing columns when scrolling
			
			slotReelPixmap = new Pixmap(24, 40, Pixmap.Format.RGBA8888);;
			slotReelPixmap = createDynamicVerticalFontText(fontSmall, IntroScreen.BY_TEXT, slotReelPixmap);
			slotReelPixmap = createDynamicScrollAnimatedVerticalText(slotReelPixmap, 20);
			slotReelTexture = new Texture(slotReelPixmap);

			for (int i = 0; i < IntroScreen.BY_TEXT.length(); i++) {
				introScreenLetters.add(new ReelLetter(this, slotReelTexture, 11, 54, 0.0166f, (i * IntroScreen.TEXT_SPACING_SIZE) + gameViewport.getWorldWidth() / 2.8f, gameViewport.getWorldHeight() / 2.0f, i));	
			}

			slotReelPixmap = new Pixmap(24, 200, Pixmap.Format.RGBA8888);;
			slotReelPixmap = createDynamicVerticalFontText(fontSmall, IntroScreen.AUTHOR_TEXT, slotReelPixmap);
			slotReelPixmap = createDynamicScrollAnimatedVerticalText(slotReelPixmap, 20);
			slotReelTexture = new Texture(slotReelPixmap);

			for (int i = 0; i < IntroScreen.AUTHOR_TEXT.length(); i++) {
				introScreenLetters.add(new ReelLetter(this, slotReelTexture, 11, 54, 0.0166f, (i * IntroScreen.TEXT_SPACING_SIZE) + gameViewport.getWorldWidth() / 4.5f, gameViewport.getWorldHeight() / 2.0f - IntroScreen.TEXT_SPACING_SIZE - 10, i));	
			}
		}
		
	}

	private static void copyFile(File source, File dest) throws IOException {
	    Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	private Pixmap createDynamicVerticalFontText(BitmapFont font, String text, Pixmap src) {
		final int width = src.getWidth();
	    final int height = src.getHeight();
	    
	    Pixmap verticalFontText = new Pixmap(width, height, src.getFormat());
	    BitmapFont.BitmapFontData fontData = font.getData();
	    Pixmap fontPixmap = new Pixmap(Gdx.files.internal(fontData.getImagePath(0)));
	    BitmapFont.Glyph glyph;
	    
        verticalFontText.setColor(Color.BLACK);
		verticalFontText.fillRectangle(0, 0, width, height);
		verticalFontText.setColor(Color.WHITE);
		
	    for(int i = 0; i < text.length(); i++) {
	    	glyph = fontData.getGlyph(text.charAt(i));
	    	verticalFontText.drawPixmap(fontPixmap, 
	    							    (verticalFontText.getWidth() - glyph.width) / 2, 
	    							    (i * (int) (font.getLineHeight() - 7)),
	    							    glyph.srcX, glyph.srcY, glyph.width, glyph.height);
	    }   
		return verticalFontText;
	}
	
	private Pixmap createDynamicScrollAnimatedVerticalText(Pixmap textToAnimate, int textHeight) {
		Pixmap scrollAnimatedVerticalText = new Pixmap(24 * 54, 220, textToAnimate.getFormat());
		
		PixmapProcessors.copyPixmapVertically(textToAnimate, scrollAnimatedVerticalText, 0);

		Pixmap scrolledText = new Pixmap(textToAnimate.getWidth(), textToAnimate.getHeight(), textToAnimate.getFormat());
		PixmapProcessors.copyPixmapVertically(textToAnimate, scrolledText, 0);
		
		for (int i = 0; i < 53; i++) {
			scrolledText = PixmapProcessors.scrollPixmapWrap(scrolledText, 4);
			PixmapProcessors.copyPixmapVertically(scrolledText, scrollAnimatedVerticalText, scrolledText.getWidth() * (i + 1));
		}
		PixmapIO.writePNG(Gdx.files.local("SlotPuzzleAnimatedVerticalText.png"), scrollAnimatedVerticalText);
		
		return scrollAnimatedVerticalText;
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}
	
	public void update(float dt) {
		for(ReelLetter reel : introScreenLetters) {
			reel.update(dt);
		}
	}

	@Override
	public void render(float delta) {
		update(delta);
		
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
 		game.batch.begin();
		for (ReelLetter reel : introScreenLetters) {
			reel.draw(game.batch);
		}
		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		//gameViewport.update(width,  height);
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		//pixmap.dispose();
		//texture.dispose();
		fontSmall.dispose();
		fontMedium.dispose();
		fontLarge.dispose();
	}
	
	public SlotPuzzle getGame() {
		return this.game;
	}
}
