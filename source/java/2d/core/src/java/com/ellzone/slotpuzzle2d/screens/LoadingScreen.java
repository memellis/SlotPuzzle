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
	private AssetManager assetManager;
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
		assetManager = new AssetManager();
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		assetManager.load("levels/level 1.tmx", TiledMap.class);
		assetManager.load("loading_screen/progress_bar.png", Texture.class);
		assetManager.load("loading_screen/progress_bar_base.png", Texture.class);
		assetManager.load("reel/reels.pack.atlas", TextureAtlas.class);
		assetManager.finishLoading();
		Gdx.app.log(TAG, "Assets loaded");
    }
    
    private void getAssets() {
		level1 = assetManager.get("levels/level 1.tmx");
		progressBarImg = assetManager.get("loading_screen/progress_bar.png");
		progressBarBaseImg = assetManager.get("loading_screen/progress_bar_base.png");
		reelAtlas = assetManager.get("reel/reels.pack.atlas", TextureAtlas.class);
    }
    
    private void initialiseScreenPositions() {
		pbPos = new Vector2();
		pbPos.set((Gdx.graphics.getWidth() - progressBarBaseImg.getWidth()) >> 1, Gdx.graphics.getHeight() >> 1);
    }
    
    private void loadSplashScreenAssets() {
    	assetManager.load("splash/pack.atlas", TextureAtlas.class);
    	assetManager.load("splash/splash3.pack.atlas", TextureAtlas.class);
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
		game.batch.draw(progressBarImg, pbPos.x, pbPos.y, progressBarImg.getWidth() * assetManager.getProgress(), progressBarImg.getHeight());
		game.batch.end();
        stage.draw();
        
		if (assetManager.update()) {
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
		assetManager.dispose();
	}
}
