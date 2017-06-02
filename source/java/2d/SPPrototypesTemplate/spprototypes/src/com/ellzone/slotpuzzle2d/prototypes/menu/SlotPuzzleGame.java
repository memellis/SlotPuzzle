package com.ellzone.slotpuzzle2d.prototypes.menu;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.*;
import com.ellzone.slotpuzzle2d.screens.IntroScreen;
import com.ellzone.slotpuzzle2d.screens.SplashScreen;
import com.ellzone.slotpuzzle2d.screens.WorldScreen;
import com.ellzone.slotpuzzle2d.prototypes.menu.MenuScreen;
import com.ellzone.slotpuzzle2d.prototypes.*;
import com.ellzone.slotpuzzle2d.*;
import com.ellzone.slotpuzzle2d.screens.*;

public class SlotPuzzleGame extends SPPrototype {
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public final static String SLOT_PUZZLE = "Slot Puzzle Prototypes";
    public final static float V_WIDTH = 800;
    public final static float V_HEIGHT = 480;

    private Screen previousScreen;
	private Screen screen;
	private SlotPuzzle slotPuzzle;

    @Override
    public void create() {
        setLogLevel();
	    batch = new SpriteBatch();
	    camera = new OrthographicCamera();
		slotPuzzle = new SlotPuzzle();
		slotPuzzle.create();
	    previousScreen = new WorldScreen(slotPuzzle);
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
		if (screen != null) screen.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
		if (screen != null) screen.hide();
    }

    @Override
    public void resize(int width, int height) {
		if (screen != null) screen.render(Gdx.graphics.getDeltaTime());
	}

    @Override
    public void pause() {
		if (screen != null) screen.pause();
    }

    @Override
    public void resume() {
        if (screen != null) screen.resume();		
    }

	public void setScreen (Screen screen) {
		if (this.screen != null) this.screen.hide();
		this.screen = screen;
		if (this.screen != null) {
			this.screen.show();
			this.screen.resize((int) V_WIDTH, (int) V_HEIGHT);
		}
	}
}
