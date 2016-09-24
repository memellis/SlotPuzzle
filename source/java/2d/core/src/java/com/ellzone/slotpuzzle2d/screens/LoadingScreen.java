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
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;

public class LoadingScreen implements Screen{
	private static final String TAG = "SlotPuzzleLoadingScreen";
	private static final int VIEWPORT_WIDTH = 800;
	private static final int VIEWPORT_HEIGHT = 480;

    private SlotPuzzle game;
	private Viewport viewport;
	private Stage stage;
	private OrthographicCamera camera;
	private TiledMap level1;
	private Texture progressBarImg, progressBarBaseImg;
	private TextureAtlas reelAtlas;
	private Vector2 pbPos;

    public LoadingScreen(SlotPuzzle game) {
    	this.game = game;
    	defineLoadingScreen();
    }
    
    private void defineLoadingScreen() {
    	initialiseLoadingScreen();
    	loadAssets();
    	getAssets();
    	initialiseScreenPositions();
    	loadSplashScreenAssets();
    }
    
    private void initialiseLoadingScreen() {
    	camera = new OrthographicCamera();
		viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
    }
    
    private void loadAssets() {
		game.assetManager = new AssetManager();
		game.assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		game.assetManager.load("levels/level 1.tmx", TiledMap.class);
		game.assetManager.load("loading_screen/progress_bar.png", Texture.class);
		game.assetManager.load("loading_screen/progress_bar_base.png", Texture.class);
		game.assetManager.load("reel/reels.pack.atlas", TextureAtlas.class);
 		game.assetManager.finishLoading();
		Gdx.app.log(TAG, "Assets loaded");
    }
    
    private void getAssets() {
		level1 = game.assetManager.get("levels/level 1.tmx");
		progressBarImg = game.assetManager.get("loading_screen/progress_bar.png");
		progressBarBaseImg = game.assetManager.get("loading_screen/progress_bar_base.png");
		reelAtlas = game.assetManager.get("reel/reels.pack.atlas", TextureAtlas.class);
    }
    
    private void initialiseScreenPositions() {
		pbPos = new Vector2();
		pbPos.set((Gdx.graphics.getWidth() - progressBarBaseImg.getWidth()) >> 1, Gdx.graphics.getHeight() >> 1);
    }
    
    private void loadSplashScreenAssets() {
    	game.assetManager.load("splash/pack.atlas", TextureAtlas.class);
    	game.assetManager.load("splash/splash3.pack.atlas", TextureAtlas.class);
    }
	
	@Override
	public void show() {		
	}

	@Override
	public void render(float delta) {	
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		game.batch.begin();
		game.batch.draw(progressBarBaseImg, pbPos.x, pbPos.y);
		game.batch.draw(progressBarImg, pbPos.x, pbPos.y, progressBarImg.getWidth() * game.assetManager.getProgress(), progressBarImg.getHeight());
		game.batch.end();
        stage.draw();
        
		if (game.assetManager.update()) {
			game.setScreen(new SplashScreen(game));
		}
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
