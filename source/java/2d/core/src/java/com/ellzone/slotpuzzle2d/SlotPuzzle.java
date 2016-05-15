package com.ellzone.slotpuzzle2d;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.ellzone.slotpuzzle2d.screens.SplashScreen;


public class SlotPuzzle extends Game
{
	public SpriteBatch batch;
	public final static String SLOT_PUZZLE = "Slot Puzzle";

	@Override
	public void create() {
		setLogLevel();
		batch = new SpriteBatch();
		setScreen(new SplashScreen(this));
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
