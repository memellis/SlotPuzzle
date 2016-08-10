package com.ellzone.slotpuzzle2d;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.*;
import com.ellzone.slotpuzzle2d.screens.LoadingScreen;

public class SlotPuzzle extends Game
{
	public final static String SLOT_PUZZLE = "Slot Puzzle";
	public final static int V_WIDTH = 800; 
	public final static int V_HEIGHT = 480;
	public SpriteBatch batch;
	public AssetManager assetManager;

	@Override
	public void create() {
		setLogLevel();
		batch = new SpriteBatch();
		assetManager = new AssetManager();
		setScreen(new LoadingScreen(this));
	}
	
	private void setLogLevel() {
		String logLevel = System.getProperty("libgdx.logLevel");
		if (logLevel != null) {
			if (logLevel.equals("DEBUG")) {
				Gdx.app.setLogLevel(Application.LOG_DEBUG);	
			} else if (logLevel.equals("INFO")) {
				Gdx.app.setLogLevel(Application.LOG_INFO);				
			} else if (logLevel.equals("ERROR") ) {
				Gdx.app.setLogLevel(Application.LOG_ERROR);								
			}
		} else {
			logLevel= System.getenv("LIBGDX_LOGLEVEL");
			if ((logLevel != null) && (logLevel != "")) {
				if (logLevel.equals("DEBUG")) {
					Gdx.app.setLogLevel(Application.LOG_DEBUG);	
				} else if (logLevel.equals("INFO")) {
					Gdx.app.setLogLevel(Application.LOG_INFO);				
				} else if (logLevel.equals("ERROR") ) {
					Gdx.app.setLogLevel(Application.LOG_ERROR);								
				}
			}
		}
	}

	@Override
	public void render() {        
	    super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
		assetManager.dispose();
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
}
