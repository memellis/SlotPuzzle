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
import com.ellzone.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.sprites.ReelLetter;
import com.ellzone.utils.FileUtils;
import com.ellzone.utils.PixmapProcessors;
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
        
		Timeline.createSequence()
			.push(Tween.set(introScreenLetters.get(0), SpriteAccessor.POS_XY).target(-20f, -20f))
			.push(Tween.set(introScreenLetters.get(1), SpriteAccessor.POS_XY).target(-20f,  00f))
			.push(Tween.set(introScreenLetters.get(2), SpriteAccessor.POS_XY).target(-20f,  20f))
			.push(Tween.set(introScreenLetters.get(3), SpriteAccessor.POS_XY).target(-20f,  40f))
			.push(Tween.set(introScreenLetters.get(4), SpriteAccessor.POS_XY).target(-20f,  60f))
			.push(Tween.set(introScreenLetters.get(5), SpriteAccessor.POS_XY).target(-20f,  80f))
			.push(Tween.set(introScreenLetters.get(6), SpriteAccessor.POS_XY).target(-20f,  100f))
			.push(Tween.set(introScreenLetters.get(7), SpriteAccessor.POS_XY).target(-20f,  120f))
			.push(Tween.set(introScreenLetters.get(8), SpriteAccessor.POS_XY).target(-20f,  140f))
			.push(Tween.set(introScreenLetters.get(9), SpriteAccessor.POS_XY).target(-20f,  160f))
			.push(Tween.set(introScreenLetters.get(10), SpriteAccessor.POS_XY).target(-20f,  180f))
			.push(Tween.set(introScreenLetters.get(11), SpriteAccessor.POS_XY).target(-20f,  200f))
			.push(Tween.set(introScreenLetters.get(12), SpriteAccessor.POS_XY).target(-20f,  220f))
			.push(Tween.set(introScreenLetters.get(13), SpriteAccessor.POS_XY).target(-20f,  240f))
			.push(Tween.set(introScreenLetters.get(14), SpriteAccessor.POS_XY).target(-20f,  260f))
			.push(Tween.set(introScreenLetters.get(15), SpriteAccessor.POS_XY).target(-20f,  280f))
			.push(Tween.set(introScreenLetters.get(16), SpriteAccessor.POS_XY).target(-20f,  300f))
			.push(Tween.set(introScreenLetters.get(17), SpriteAccessor.POS_XY).target(-20f,  320f))
			.push(Tween.set(introScreenLetters.get(18), SpriteAccessor.POS_XY).target(-20f,  340f))
			.push(Tween.set(introScreenLetters.get(19), SpriteAccessor.POS_XY).target(-20f,  360f))
			.push(Tween.set(introScreenLetters.get(20), SpriteAccessor.POS_XY).target(-20f,  400f))
			.push(Tween.set(introScreenLetters.get(21), SpriteAccessor.POS_XY).target(-20f,  420f))
			.push(Tween.set(introScreenLetters.get(22), SpriteAccessor.POS_XY).target(-20f,  440f))
			.push(Tween.set(introScreenLetters.get(23), SpriteAccessor.POS_XY).target(-20f,  460f))			
			.push(Tween.set(introScreenLetters.get(24), SpriteAccessor.POS_XY).target(-20f,  480f))			
			.push(Tween.set(introScreenLetters.get(25), SpriteAccessor.POS_XY).target(-20f,  500f))			
			.push(Tween.set(introScreenLetters.get(26), SpriteAccessor.POS_XY).target(-20f,  520f))			
			.push(Tween.set(introScreenLetters.get(27), SpriteAccessor.POS_XY).target(-20f,  540f))			
			.push(Tween.set(introScreenLetters.get(28), SpriteAccessor.POS_XY).target(-20f,  560f))			
			.push(Tween.set(introScreenLetters.get(29), SpriteAccessor.POS_XY).target(-20f,  580f))			
			.push(Tween.set(introScreenLetters.get(30), SpriteAccessor.POS_XY).target(-20f,  600f))			
			.push(Tween.set(introScreenLetters.get(31), SpriteAccessor.POS_XY).target(-20f,  620f))			
			.push(Tween.set(introScreenLetters.get(32), SpriteAccessor.POS_XY).target(-20f,  640f))			
			.push(Tween.set(introScreenLetters.get(33), SpriteAccessor.POS_XY).target(-20f,  660f))			
			.push(Tween.set(introScreenLetters.get(34), SpriteAccessor.POS_XY).target(-20f,  680f))			
			.push(Tween.set(introScreenLetters.get(35), SpriteAccessor.POS_XY).target(-20f,  700f))			
			.push(Tween.set(introScreenLetters.get(36), SpriteAccessor.POS_XY).target(-20f,  720f))			
			.push(Tween.set(introScreenLetters.get(37), SpriteAccessor.POS_XY).target(-20f,  740f))			
			.push(Tween.set(introScreenLetters.get(38), SpriteAccessor.POS_XY).target(-20f,  760f))			
			.pushPause(1.0f)
			.push(Tween.to(introScreenLetters.get(0), SpriteAccessor.POS_XY, 0.8f).target(250f, 280f))
			.push(Tween.to(introScreenLetters.get(1), SpriteAccessor.POS_XY, 0.8f).target(280f, 280f))
			.push(Tween.to(introScreenLetters.get(2), SpriteAccessor.POS_XY, 0.8f).target(310f, 280f))
			.push(Tween.to(introScreenLetters.get(3), SpriteAccessor.POS_XY, 0.8f).target(340f, 280f))
			.push(Tween.to(introScreenLetters.get(4), SpriteAccessor.POS_XY, 0.8f).target(370f, 280f))
			.push(Tween.to(introScreenLetters.get(5), SpriteAccessor.POS_XY, 0.8f).target(400f, 280f))
			.push(Tween.to(introScreenLetters.get(6), SpriteAccessor.POS_XY, 0.8f).target(430f, 280f))
			.push(Tween.to(introScreenLetters.get(7), SpriteAccessor.POS_XY, 0.8f).target(460f, 280f))
			.push(Tween.to(introScreenLetters.get(8), SpriteAccessor.POS_XY, 0.8f).target(490f, 280f))
			.push(Tween.to(introScreenLetters.get(9), SpriteAccessor.POS_XY, 0.8f).target(520f, 280f))
			.push(Tween.to(introScreenLetters.get(10), SpriteAccessor.POS_XY, 0.8f).target(550f, 280f))
			.push(Tween.to(introScreenLetters.get(11), SpriteAccessor.POS_XY, 0.8f).target(375f, 240f))
			.push(Tween.to(introScreenLetters.get(12), SpriteAccessor.POS_XY, 0.8f).target(400f, 240f))
			.push(Tween.to(introScreenLetters.get(13), SpriteAccessor.POS_XY, 0.8f).target(270f, 200f))
			.push(Tween.to(introScreenLetters.get(14), SpriteAccessor.POS_XY, 0.8f).target(300f, 200f))
			.push(Tween.to(introScreenLetters.get(15), SpriteAccessor.POS_XY, 0.8f).target(330f, 200f))
			.push(Tween.to(introScreenLetters.get(16), SpriteAccessor.POS_XY, 0.8f).target(360f, 200f))
			.push(Tween.to(introScreenLetters.get(17), SpriteAccessor.POS_XY, 0.8f).target(390f, 200f))
			.push(Tween.to(introScreenLetters.get(18), SpriteAccessor.POS_XY, 0.8f).target(420f, 200f))
			.push(Tween.to(introScreenLetters.get(19), SpriteAccessor.POS_XY, 0.8f).target(450f, 200f))
			.push(Tween.to(introScreenLetters.get(20), SpriteAccessor.POS_XY, 0.8f).target(480f, 200f))
			.push(Tween.to(introScreenLetters.get(21), SpriteAccessor.POS_XY, 0.8f).target(510f, 200f))
			.push(Tween.to(introScreenLetters.get(22), SpriteAccessor.POS_XY, 0.8f).target(540f, 200f))
			.push(Tween.to(introScreenLetters.get(23), SpriteAccessor.POS_XY, 0.8f).target(180f,  90f))			
			.push(Tween.to(introScreenLetters.get(24), SpriteAccessor.POS_XY, 0.8f).target(210f,  90f))			
			.push(Tween.to(introScreenLetters.get(25), SpriteAccessor.POS_XY, 0.8f).target(240f,  90f))			
			.push(Tween.to(introScreenLetters.get(26), SpriteAccessor.POS_XY, 0.8f).target(270f,  90f))			
			.push(Tween.to(introScreenLetters.get(27), SpriteAccessor.POS_XY, 0.8f).target(300f,  90f))			
			.push(Tween.to(introScreenLetters.get(28), SpriteAccessor.POS_XY, 0.8f).target(330f,  90f))			
			.push(Tween.to(introScreenLetters.get(29), SpriteAccessor.POS_XY, 0.8f).target(360f,  90f))			
			.push(Tween.to(introScreenLetters.get(30), SpriteAccessor.POS_XY, 0.8f).target(390f,  90f))			
			.push(Tween.to(introScreenLetters.get(31), SpriteAccessor.POS_XY, 0.8f).target(420f,  90f))			
			.push(Tween.to(introScreenLetters.get(32), SpriteAccessor.POS_XY, 0.8f).target(450f,  90f))			
			.push(Tween.to(introScreenLetters.get(33), SpriteAccessor.POS_XY, 0.8f).target(480f,  90f))			
			.push(Tween.to(introScreenLetters.get(34), SpriteAccessor.POS_XY, 0.8f).target(510f,  90f))			
			.push(Tween.to(introScreenLetters.get(35), SpriteAccessor.POS_XY, 0.8f).target(540f,  90f))			
			.push(Tween.to(introScreenLetters.get(36), SpriteAccessor.POS_XY, 0.8f).target(570f,  90f))			
			.push(Tween.to(introScreenLetters.get(37), SpriteAccessor.POS_XY, 0.8f).target(600f,  90f))			
			.push(Tween.to(introScreenLetters.get(38), SpriteAccessor.POS_XY, 0.8f).target(630f,  90f))			
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
