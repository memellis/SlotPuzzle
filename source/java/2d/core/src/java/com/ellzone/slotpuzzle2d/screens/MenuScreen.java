/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

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
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.utils.FileUtils;

import aurelienribon.tweenengine.TweenManager;

public class MenuScreen implements Screen {
	private SlotPuzzle game;
	private Viewport viewport;
	private Stage stage;
 	private TweenManager tweenManager = new TweenManager();
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
			e.printStackTrace();
		}
		
		if (localFile.exists()) {
			Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE, localFile.file().getAbsolutePath() + " exists");
		} else {
			Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE, localFile.path() + " does not exist");			
		}

	}
	
	@Override
	public void show() {
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
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}
}
