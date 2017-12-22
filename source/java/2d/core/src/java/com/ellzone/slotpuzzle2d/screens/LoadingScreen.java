/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ellzone.slotpuzzle2d.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class LoadingScreen implements Screen{
    private SlotPuzzle game;
	private Viewport viewport;
	private Stage stage;
	private OrthographicCamera camera;
	private Texture progressBarImg, progressBarBaseImg;
	private Vector2 pbPos;
	private boolean show = false;

    public LoadingScreen(SlotPuzzle game) {
    	this.game = game;
    	defineLoadingScreen();
    }
    
    private void defineLoadingScreen() {
    	initialiseLoadingScreen();
     	getAssets(game.annotationAssetManager);
    	initialiseScreenPositions();
    }
    
    private void initialiseLoadingScreen() {
    	camera = new OrthographicCamera();
		viewport = new FitViewport(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
    }

    private void getAssets(AnnotationAssetManager annotationAssetManager) {
		progressBarImg = annotationAssetManager.get(AssetsAnnotation.PROGRESS_BAR);
		progressBarBaseImg = annotationAssetManager.get(AssetsAnnotation.PROGRESS_BAR_BASE);
    }
    
    private void initialiseScreenPositions() {
		pbPos = new Vector2();
		pbPos.set((SlotPuzzleConstants.V_WIDTH - progressBarBaseImg.getWidth()) >> 1, SlotPuzzleConstants.V_HEIGHT >> 1);
    }

	@Override
	public void show() {
		this.show = false;
		Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "show() called.");
	}

	@Override
	public void render(float delta) {	
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.batch.begin();
		game.batch.draw(progressBarBaseImg, pbPos.x, pbPos.y);
		game.batch.draw(progressBarImg, pbPos.x, pbPos.y, progressBarImg.getWidth() * game.annotationAssetManager.getProgress(), progressBarImg.getHeight());
		game.batch.end();
        stage.draw();
        
		if (game.annotationAssetManager.update()) {
			game.setScreen(new IntroScreen(game));
		}
	}

	@Override
	public void resize(int width, int height) {		
	}

	@Override
	public void pause() {
		this.show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "pause() called.");
    }

	@Override
	public void resume() {
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "resume() called.");
	}

	@Override
	public void hide() {
        this.show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "hide() called.");
	}

	@Override
	public void dispose() {
	}
}
