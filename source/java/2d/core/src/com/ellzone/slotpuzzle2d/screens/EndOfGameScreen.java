package com.ellzone.slotpuzzle2d.screens;

import java.io.FileInputStream;
import java.io.FileOutputStream;
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.sprites.ReelLetter;
import com.ellzone.utils.FileUtils;
import com.ellzone.utils.PixmapProcessors;

public class EndOfGameScreen implements Screen {
	private static final int TEXT_SPACING_SIZE = 30;
	private static final float SIXTY_FPS = 0.0166f;
	private static final int EXO_FONT_SMALL_SIZE = 24;
	private static final int EXO_FONT_MEDIUM_SIZE = 48;
	private static final int EXO_FONT_LARGE_SIZE = 64;
	private static final int SCROLL_STEP = 4;
	private static final int SCROLL_HEIGHT = 20;
	private static final String GAME_OVER_TEXT="Game Over";
	private BitmapFont fontSmall;
	private BitmapFont fontMedium;
	private BitmapFont fontLarge;
	private Array<ReelLetter> gameOverScreenLetters;
	private Pixmap slotReelPixmap;
	private Texture slotReelTexture;
	private Texture texture;
	private SlotPuzzle game;
	private Viewport viewport;
	private Stage stage;
	
	public EndOfGameScreen(SlotPuzzle game) {
		this.game = game;
		createEndOfGameScreen();
	}

	private void createEndOfGameScreen() {
		viewport = new FillViewport(800, 480, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameOverLabel = new Label("GAME OVER", font);
        Label playAgainLabel = new Label("Click to Play Again", font);

        table.add(gameOverLabel).expandX();
        table.row();
        table.add(playAgainLabel).expandX().padTop(10f);

        stage.addActor(table);		
        
		SmartFontGenerator fontGen = new SmartFontGenerator();
		FileHandle exoFileInternal = Gdx.files.internal("LiberationMono-Regular.ttf");
		FileHandle generatedFontDir = Gdx.files.local("generated-fonts/");
		generatedFontDir.mkdirs();
		
		FileHandle exoFile = Gdx.files.local("generated-fonts/LiberationMono-Regular.ttf");
		
		try {
	        FileUtils.copyFile(exoFileInternal, exoFile);
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
			gameOverScreenLetters = new Array<ReelLetter>();

			slotReelPixmap = new Pixmap(EndOfGameScreen.EXO_FONT_SMALL_SIZE, EndOfGameScreen.GAME_OVER_TEXT.length() * EndOfGameScreen.SCROLL_HEIGHT, Pixmap.Format.RGBA8888);		
			slotReelPixmap = PixmapProcessors.createDynamicVerticalFontText(fontSmall, EndOfGameScreen.GAME_OVER_TEXT, slotReelPixmap);
			slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedVerticalText(slotReelPixmap, 20, EndOfGameScreen.GAME_OVER_TEXT, EndOfGameScreen.EXO_FONT_SMALL_SIZE, EndOfGameScreen.SCROLL_STEP);
			slotReelTexture = new Texture(slotReelPixmap);

			for (int i = 0; i < EndOfGameScreen.GAME_OVER_TEXT.length(); i++) {
				gameOverScreenLetters.add(new ReelLetter(this, slotReelTexture, EndOfGameScreen.GAME_OVER_TEXT.length(), EndOfGameScreen.GAME_OVER_TEXT.length() * 5 - 1 , SIXTY_FPS, (i * EndOfGameScreen.TEXT_SPACING_SIZE) + viewport.getWorldWidth() / 3, viewport.getWorldHeight() / 2 +  2 * EndOfGameScreen.TEXT_SPACING_SIZE, i));	
			}
		}
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}

	private void update(float dt) {
		for(ReelLetter reel : gameOverScreenLetters) {
			reel.update(dt);
		}
	}
	
	@Override
	public void render(float delta) {
		update(delta);
		if(Gdx.input.justTouched()) {
			game.setScreen(new SplashScreen(game));
	        dispose();
	    }
	    Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
 		game.batch.begin();
		for (ReelLetter reel : gameOverScreenLetters) {
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
		stage.dispose();
	}
}
