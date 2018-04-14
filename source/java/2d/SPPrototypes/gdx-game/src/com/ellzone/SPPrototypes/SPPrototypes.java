package com.ellzone.SPPrototypes;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.ellzone.SPPrototypes.screens.SPPrototypeMenuScreen;
import com.badlogic.gdx.graphics.*;

public class SPPrototypes extends Game {
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public final static Version gdxVersion = new Version();
    public final static String SLOT_PUZZLE = "Slot Puzzle Prototypes";
    public final static float V_WIDTH = 800;
    public final static float V_HEIGHT = 480;

    private Screen previousScreen;

    @Override
    public void create() {
        setLogLevel();
	batch = new SpriteBatch();
	camera = new OrthographicCamera();
	previousScreen = new SPPrototypeMenuScreen(this);
	setScreen(previousScreen);
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

    public Screen getPreviousScreen() {
	return previousScreen;
    }

    public void setPreviousScreen(Screen previousScreen) {
        this.previousScreen = previousScreen;
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
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
