package com.ellzone.slotpuzzle2d.screens;

import java.io.IOException;
import org.jrenner.smartfont.SmartFontGenerator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.sprites.ReelLetter;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

public class IntroScreen implements Screen {
	private static final int TEXT_SPACING_SIZE = 30;
	private static final float SIXTY_FPS = 1/60f;
	private static final int EXO_FONT_SMALL_SIZE = 24;
	private static final int SCROLL_STEP = 4;
	private static final int SCROLL_HEIGHT = 20;
	private static final String SLOT_PUZZLE_REEL_TEXT = "Slot Puzzle";
	private static final String BY_TEXT = "by";
	private static final String AUTHOR_TEXT = "Mark Ellis";
	private static final String COPYRIGHT_YEAR_AUTHOR_TEXT = "©2015 Mark Ellis";
	
	private SlotPuzzle game;
	private Texture texture;
	private Pixmap slotReelPixmap;
	private Texture slotReelTexture;
	private Viewport viewport;
	private Stage stage;
	private BitmapFont fontSmall;
	private BitmapFont fontMedium;
	private BitmapFont fontLarge;
	private Array<ReelLetter> introScreenLetters;
	private boolean endOfIntroScreen;
	private TextButton button;
    private TextButtonStyle textButtonStyle;
    private Skin skin;
    private TextureAtlas buttonAtlas;
	private TweenManager tweenManager = new TweenManager();
	
	public IntroScreen(SlotPuzzle game) {
		this.game = game;			
		defineIntroScreen();
	}
	
	void defineIntroScreen() {
		viewport = new FillViewport(800, 480, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);
               
        // FIXME 1: Resizing window needs to generate resized Smartfont
        
		Tween.setWaypointsLimit(10);
		Tween.setCombinedAttributesLimit(3);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
        
		SmartFontGenerator fontGen = new SmartFontGenerator();
		FileHandle exoFileInternal = Gdx.files.internal("LiberationMono-Regular.ttf");
		FileHandle generatedFontDir = Gdx.files.local("generated-fonts/");
		generatedFontDir.mkdirs();
		
		FileHandle exoFile = Gdx.files.local("generated-fonts/LiberationMono-Regular.ttf");
		
		try {
	        FileUtils.copyFile(exoFileInternal, exoFile);
		} catch (IOException ex) {
			Gdx.app.error(SlotPuzzle.SLOT_PUZZLE, "Could not copy " + exoFileInternal.file().getPath() + " to file " + exoFile.file().getAbsolutePath() + " " + ex.getMessage());
		}
		
		fontSmall = fontGen.createFont(exoFile, "exo-small", 24);
		fontMedium = fontGen.createFont(exoFile, "exo-medium", 48);
		fontLarge = fontGen.createFont(exoFile, "exo-large", 64);
		
		ReelLetter.instanceCount = 0;
		
		if (Gdx.files.local("SlotPuzzleTextFontTile.png").exists()) {
			texture = new Texture(Gdx.files.local("SlotPuzzleTextFontTile.png"));
			Gdx.app.log(SlotPuzzle.SLOT_PUZZLE, "Loaded cached SlotPuzzleTextFontTile.png file.");
				
		} else {
				
			introScreenLetters = new Array<ReelLetter>();

			slotReelPixmap = new Pixmap(IntroScreen.EXO_FONT_SMALL_SIZE, IntroScreen.SLOT_PUZZLE_REEL_TEXT.length() * IntroScreen.SCROLL_HEIGHT, Pixmap.Format.RGBA8888);		
			slotReelPixmap = PixmapProcessors.createDynamicVerticalFontText(fontSmall, IntroScreen.SLOT_PUZZLE_REEL_TEXT, slotReelPixmap);
			slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedVerticalText(slotReelPixmap, IntroScreen.SCROLL_HEIGHT, IntroScreen.SLOT_PUZZLE_REEL_TEXT, IntroScreen.EXO_FONT_SMALL_SIZE, IntroScreen.SCROLL_STEP);
			slotReelTexture = new Texture(slotReelPixmap);

			for (int i = 0; i < IntroScreen.SLOT_PUZZLE_REEL_TEXT.length(); i++) {
				introScreenLetters.add(new ReelLetter(this, slotReelTexture, IntroScreen.SLOT_PUZZLE_REEL_TEXT.length(), IntroScreen.SLOT_PUZZLE_REEL_TEXT.length() * 5 - 1, SIXTY_FPS, (i * IntroScreen.TEXT_SPACING_SIZE) + viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + IntroScreen.TEXT_SPACING_SIZE + 10, i));
			}
			
			slotReelPixmap = new Pixmap(IntroScreen.EXO_FONT_SMALL_SIZE, IntroScreen.BY_TEXT.length() * IntroScreen.SCROLL_HEIGHT, Pixmap.Format.RGBA8888);;
			slotReelPixmap = PixmapProcessors.createDynamicVerticalFontText(fontSmall, IntroScreen.BY_TEXT, slotReelPixmap);
			slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedVerticalText(slotReelPixmap, IntroScreen.SCROLL_HEIGHT, IntroScreen.BY_TEXT, IntroScreen.EXO_FONT_SMALL_SIZE, IntroScreen.SCROLL_STEP);
			slotReelTexture = new Texture(slotReelPixmap);

			for (int i = 0; i < IntroScreen.BY_TEXT.length(); i++) {
				introScreenLetters.add(new ReelLetter(this, slotReelTexture, IntroScreen.BY_TEXT.length(), IntroScreen.BY_TEXT.length() * 5 - 1, SIXTY_FPS, (i * IntroScreen.TEXT_SPACING_SIZE) + viewport.getWorldWidth() / 2.2f, viewport.getWorldHeight() / 2.0f, i));	
			}

			slotReelPixmap = new Pixmap(IntroScreen.EXO_FONT_SMALL_SIZE, IntroScreen.AUTHOR_TEXT.length() * IntroScreen.SCROLL_HEIGHT, Pixmap.Format.RGBA8888);;
			slotReelPixmap = PixmapProcessors.createDynamicVerticalFontText(fontSmall, IntroScreen.AUTHOR_TEXT, slotReelPixmap);
			slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedVerticalText(slotReelPixmap, IntroScreen.SCROLL_HEIGHT, IntroScreen.AUTHOR_TEXT, IntroScreen.EXO_FONT_SMALL_SIZE, IntroScreen.SCROLL_STEP);
			slotReelTexture = new Texture(slotReelPixmap);

			for (int i = 0; i < IntroScreen.AUTHOR_TEXT.length(); i++) {
				introScreenLetters.add(new ReelLetter(this, slotReelTexture, IntroScreen.AUTHOR_TEXT.length(), IntroScreen.AUTHOR_TEXT.length() * 5 - 1, SIXTY_FPS, (i * IntroScreen.TEXT_SPACING_SIZE) + viewport.getWorldWidth() / 3.0f, viewport.getWorldHeight() / 2.0f - IntroScreen.TEXT_SPACING_SIZE - 10, i));	
			}
			
			slotReelPixmap = new Pixmap(IntroScreen.EXO_FONT_SMALL_SIZE, IntroScreen.COPYRIGHT_YEAR_AUTHOR_TEXT.length() * IntroScreen.SCROLL_HEIGHT, Pixmap.Format.RGBA8888);;
			slotReelPixmap = PixmapProcessors.createDynamicVerticalFontText(fontSmall, IntroScreen.COPYRIGHT_YEAR_AUTHOR_TEXT, slotReelPixmap);
			slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedVerticalText(slotReelPixmap, IntroScreen.SCROLL_HEIGHT, IntroScreen.COPYRIGHT_YEAR_AUTHOR_TEXT, IntroScreen.EXO_FONT_SMALL_SIZE, IntroScreen.SCROLL_STEP);
			slotReelTexture = new Texture(slotReelPixmap);

			for (int i = 0; i < IntroScreen.COPYRIGHT_YEAR_AUTHOR_TEXT.length(); i++) {
				introScreenLetters.add(new ReelLetter(this, slotReelTexture, IntroScreen.COPYRIGHT_YEAR_AUTHOR_TEXT.length(), IntroScreen.COPYRIGHT_YEAR_AUTHOR_TEXT.length() * 5 - 1 , SIXTY_FPS, (i * IntroScreen.TEXT_SPACING_SIZE) + viewport.getWorldWidth() / 4.5f, viewport.getWorldHeight() / 2.0f - 5 * IntroScreen.TEXT_SPACING_SIZE - 10, i));	
			}
		}
		
		endOfIntroScreen = false;
		
		Gdx.input.setInputProcessor(stage);
		
		skin = new Skin();
        buttonAtlas = new TextureAtlas(Gdx.files.internal("ui/ui-blue.atlas"));
        skin.addRegions(buttonAtlas);
        textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = fontSmall;
        textButtonStyle.up = skin.getDrawable("icon_arrow_up");
        textButtonStyle.down = skin.getDrawable("icon_arrow_down");
        textButtonStyle.checked = skin.getDrawable("checkbox_on");
        button = new TextButton("                  ", textButtonStyle);
        button.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
    			endOfIntroScreen = true;
            }
        });

        Label.LabelStyle font = new Label.LabelStyle(fontSmall, Color.WHITE);
        Label buttonPressLabel = new Label("LAUNCH", font);
        
        Table table = new Table();
        table.bottom();
        table.setFillParent(true);
        table.add(button).expandX().padTop(50f);
        table.row();
        table.add(buttonPressLabel).expandX();

        stage.addActor(table);
        viewport.update(800, 480);
        
        Timeline introSeq = Timeline.createSequence();
        
        for (int i = 0; i < introScreenLetters.size; i++) {
        	introSeq = introSeq.push(Tween.set(introScreenLetters.get(i), SpriteAccessor.POS_XY).target(-20f, -20f + i *20f));
        }
        
		introSeq = introSeq.pushPause(1.0f);

        for (int i = 0; i < 11; i++) {
        	introSeq = introSeq.push(Tween.to(introScreenLetters.get(i), SpriteAccessor.POS_XY, 0.8f).target(250f + i * 30f, 280f));
        }

        for (int i = 11; i < 13; i++) {
        	introSeq = introSeq.push(Tween.to(introScreenLetters.get(i), SpriteAccessor.POS_XY, 0.8f).target(60f + i * 30f, 240f));
        }

        for (int i = 13; i < 23; i++) {
        	introSeq = introSeq.push(Tween.to(introScreenLetters.get(i), SpriteAccessor.POS_XY, 0.8f).target(-120f + i * 30f, 200f));
        }
        
        for (int i = 23; i < 39; i++) {
        	introSeq = introSeq.push(Tween.to(introScreenLetters.get(i), SpriteAccessor.POS_XY, 0.8f).target(-520f + i * 30f, 90f));
        }

        introSeq = introSeq
			.pushPause(0.3f)
			.start(tweenManager);
	}
		
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}
	
	public void update(float dt) {
		tweenManager.update(dt);
		for(ReelLetter reel : introScreenLetters) {
			reel.update(dt);
		}
		if (ReelLetter.instanceCount == 0 | endOfIntroScreen) {
			game.setScreen(new PlayScreen(game));
			dispose();
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
		stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,  height);
		fontSmall.newFontCache();
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
		if (tweenManager != null) tweenManager.killAll();
		if (slotReelPixmap != null) slotReelPixmap.dispose();
		if (slotReelTexture != null) slotReelTexture.dispose();
		if (texture != null) texture.dispose();
		if (fontSmall != null) fontSmall.dispose();
		if (fontMedium != null) fontMedium.dispose();
		if (fontLarge != null) fontLarge.dispose();
	}
	
	public SlotPuzzle getGame() {
		return this.game;
	}
}
