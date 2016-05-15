package com.ellzone.slotpuzzle2d.screens;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.utils.FileUtils;

import aurelienribon.tweenengine.TweenManager;

public class MenuScreen implements Screen {
	private SlotPuzzle game;
	private Viewport viewport;
	private Stage stage;
 	private final TweenManager tweenManager = new TweenManager();
	private boolean isLoaded = false;

	
	public MenuScreen(SlotPuzzle game) {
		this.game = game;
		defineMenuScreen();
		testFileCopy();
	}
	
	private void defineMenuScreen() {
		viewport = new FillViewport(800, 480, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);		
	}

	private void testFileCopy() {		
		FileHandle internalFile = Gdx.files.internal("SlotPuzzleVersion.txt");
		FileHandle localFile = Gdx.files.local("SlotPuzle");
		
		try {
			FileUtils.copyFile(internalFile, localFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (localFile.exists()) {
			Gdx.app.log(SlotPuzzle.SLOT_PUZZLE, localFile.file().getAbsolutePath() + " exists");
		} else {
			Gdx.app.log(SlotPuzzle.SLOT_PUZZLE, localFile.path() + " does not exist");			
		}

	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		
	}
	
	public void update(float delta) {
		
	}

	@Override
	public void render(float delta) {
		update(delta);
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.batch.begin();
		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

}
