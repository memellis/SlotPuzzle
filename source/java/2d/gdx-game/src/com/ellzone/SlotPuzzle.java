package com.ellzone;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.files.*;
import java.text.*;
import com.ellzone.utils.ScreenshotFactory;
import com.ellzone.org.jrenner.smartfont.SmartFontGenerator;

public class SlotPuzzle implements ApplicationListener
{
	private final static String SLOT_PUZZLE = "Slot Puzzle";
	private final static int SLOT_PUZZLE_FONT_SCALE = 5;
	private int screenWidth;
	private int screenHeight;
	private Texture texture;
	private Texture reelTexture;
	private Texture fontTexture;
	private SpriteBatch batch;
	private BitmapFont font;
	private FrameBuffer frameBuffer;
	private TextureRegion fboRegion;
	private TextureRegion reelRegion;
	private Sprite sprite;
	private Sprite reelSprite;
	private Sprite reelSprite2;
	private Float scrollTimer = 0.0f;
    private Animation scrollAnimation;
	private float stateTime = 0;
	private TextureRegion scrollFrame;
	private boolean takeScreenShot = false;
	private int scrollCount = 0;
	private FileHandle exoFile;
	private FileHandle exoFileInternal;
	private FileHandle generatedFontDir;
	
	
	@Override
	public void create()
	{
		SmartFontGenerator fontGen = new SmartFontGenerator();
		exoFileInternal = Gdx.files.internal("LiberationMono-Regular.ttf");
		generatedFontDir = Gdx.files.local("generated-fonts/");
		generatedFontDir.mkdirs();
		exoFileInternal.copyTo(Gdx.files.local("LiberationMono-Regular.ttf"));
		exoFile = Gdx.files.local("LiberationMono-Regular.ttf");
		
		BitmapFont fontSmall = fontGen.createFont(exoFile, "exo-small", 24);
		//BitmapFont fontMedium = fontGen.createFont(exoFile, "exo-medium", 48);
		//BitmapFont fontLarge = fontGen.createFont(exoFile, "exo-large", 64);
		
		texture = new Texture(Gdx.files.internal("android.jpg"));
		batch = new SpriteBatch();
		font = new BitmapFont();
		texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
	   
		font.setScale(SLOT_PUZZLE_FONT_SCALE);
		screenWidth = Gdx.graphics.getWidth();
		screenHeight = Gdx.graphics.getHeight();
		
		frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, screenWidth, screenHeight,false);
		fboRegion = new TextureRegion(frameBuffer.getColorBufferTexture(), 0, 0, 64, 704);
		fboRegion.flip(false, true);
		
		frameBuffer.begin();
		
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		font.draw(batch, "S", 0, 64);
		font.draw(batch, "l", 10, 128);
		font.draw(batch, "o", 0, 192);
		font.draw(batch, "t", 10, 256);
		font.draw(batch, "P", 0, 320);
		font.draw(batch, "u", 0, 384);
		font.draw(batch, "z", 0, 448);
		font.draw(batch, "z", 0, 512);
		font.draw(batch, "l", 10, 576);
		font.draw(batch, "e", 0, 640);
		batch.end();
		frameBuffer.end();
		
		reelTexture = fboRegion.getTexture();
		
		//reelTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		
		fontTexture = dynamicFont();
		fontTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		reelSprite = new Sprite(fontTexture);
		reelSprite.setPosition(256, 0);
		//reelSprite.flip(false, true);
		
		scrollAnimation = createScollingAnimation(texture);
		
		reelSprite2 = new Sprite(texture);
		reelSprite2.setPosition(640,640);
	}

	@Override
	public void render()
	{        
	    
	    Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		font.draw(batch, SLOT_PUZZLE, (screenWidth / 3) + 40, screenHeight - screenHeight / 4);
		font.draw(batch, "File="+exoFile.exists(),0,500);
		batch.draw(fboRegion, 0, 0, 64, 704);
		batch.draw(reelTexture, 64, screenHeight / 3, 0, scrollCount, 64, 704);
		scrollCount++;
		if (takeScreenShot) {
			ScreenshotFactory.saveScreenshot();
			takeScreenShot = false;
			Gdx.app.log("SlotPuzzle","screenshot location" + Gdx.files.getLocalStoragePath());
		}
		//if (scrollCount > reelTexture.getHeight()) {
			//scrollCount = 0;
		//}
		batch.end();
		
	}

	@Override
	public void dispose()
	{
	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}
	
	private Texture dynamicFont() {
		
		// Has issue with not scrolling when height of texture region reached
		Pixmap tile = new Pixmap(64, 702, Pixmap.Format.RGBA8888);
		
		BitmapFont font = new BitmapFont(Gdx.files.internal("arial-32.fnt"));
		BitmapFont.BitmapFontData data = font.getData();
		Pixmap fontPixmap = new Pixmap(Gdx.files.internal(data.getImagePath()));
		BitmapFont.Glyph glyph = data.getGlyph('S');
        tile.setColor(Color.BLACK);
		tile.fillRectangle(0,0,tile.getWidth(),tile.getHeight());
		tile.drawPixmap(fontPixmap, (tile.getWidth() - glyph.width) / 2, (tile.getHeight() - glyph.height) / 2 + 288,
						glyph.srcX, glyph.srcY, glyph.width, glyph.height);
		glyph = data.getGlyph('l');
		tile.drawPixmap(fontPixmap, (tile.getWidth() - glyph.width) / 2, (tile.getHeight() - glyph.height) / 2 + 256,
						glyph.srcX, glyph.srcY, glyph.width, glyph.height);
		glyph = data.getGlyph('o');
		tile.drawPixmap(fontPixmap, (tile.getWidth() - glyph.width) / 2, (tile.getHeight() - glyph.height) / 2 + 234,
						glyph.srcX, glyph.srcY, glyph.width, glyph.height);
		glyph = data.getGlyph('t');
		tile.drawPixmap(fontPixmap, (tile.getWidth() - glyph.width) / 2, (tile.getHeight() - glyph.height) / 2 + 192,
						glyph.srcX, glyph.srcY, glyph.width, glyph.height);
		glyph = data.getGlyph('P');
		tile.drawPixmap(fontPixmap, (tile.getWidth() - glyph.width) / 2, (tile.getHeight() - glyph.height) / 2 + 160,
						glyph.srcX, glyph.srcY, glyph.width, glyph.height);
		glyph = data.getGlyph('u');
		tile.drawPixmap(fontPixmap, (tile.getWidth() - glyph.width) / 2, (tile.getHeight() - glyph.height) / 2 + 128,
						glyph.srcX, glyph.srcY, glyph.width, glyph.height);
		glyph = data.getGlyph('z');
		tile.drawPixmap(fontPixmap, (tile.getWidth() - glyph.width) / 2, (tile.getHeight() - glyph.height) / 2 + 96, 
						glyph.srcX, glyph.srcY, glyph.width, glyph.height);
		glyph = data.getGlyph('z');
		tile.drawPixmap(fontPixmap, (tile.getWidth() - glyph.width) / 2, (tile.getHeight() - glyph.height) / 2 + 64,
						glyph.srcX, glyph.srcY, glyph.width, glyph.height);
		glyph = data.getGlyph('l');
		tile.drawPixmap(fontPixmap, (tile.getWidth() - glyph.width) / 2, (tile.getHeight() - glyph.height) / 2 + 32,
						glyph.srcX, glyph.srcY, glyph.width, glyph.height);
		glyph = data.getGlyph('e');
		tile.drawPixmap(fontPixmap, (tile.getWidth() - glyph.width) / 2, (tile.getHeight() - glyph.height) / 2 + 0,
						glyph.srcX, glyph.srcY, glyph.width, glyph.height);
		
		return new Texture(tile);
	}
	
	private Animation createScollingAnimation(Texture textureToScroll) {
		int scrollWidth = 128;
		int scrollHeight = 128;
		
		int scrollOffset = 0;
		TextureRegion[] scrollFrames;
		
		scrollFrames = new TextureRegion[textureToScroll.getHeight()/2]; 
		
		while (scrollOffset < (textureToScroll.getHeight()/2 - scrollHeight - 1)) {
			scrollFrames[scrollOffset] = new TextureRegion(textureToScroll, 0, scrollOffset, scrollWidth, scrollHeight);
			scrollOffset++;
		}
		return new Animation(0.025f, scrollFrames);
	}
}
