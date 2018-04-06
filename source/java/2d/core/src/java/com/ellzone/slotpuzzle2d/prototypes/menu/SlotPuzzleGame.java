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

package com.ellzone.slotpuzzle2d.prototypes.menu;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;

public class SlotPuzzleGame extends SPPrototype implements Screen {
    public SpriteBatch batch;
    public OrthographicCamera camera;
    public final static String SLOT_PUZZLE = "Slot Puzzle Prototypes";
    public final static float V_WIDTH = 800;
    public final static float V_HEIGHT = 480;

    private SlotPuzzle game;
    private Screen previousScreen;
	private Screen screen;
	private Screen worldScreen;

    @Override
    public void create() {
        this.game = new SlotPuzzle();
        game.create();
        setLogLevel();
	    batch = game.batch;
	    previousScreen = new MenuScreen(game);
        this.game.setScreen(previousScreen);
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
		if (game.getScreen() != null) game.getScreen().render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void dispose() {
		super.dispose();
	}

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        super.render();
        if (game.getScreen() != null) game.getScreen().render(delta);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        System.out.println("resize");
        if (screen != null) {
            screen.resize(width, height);
        }
	}

    @Override
    public void pause() {
		if (screen != null) screen.pause();
    }

    @Override
    public void resume() {
        if (screen != null) screen.resume();		
    }

    @Override
    public void hide() {

    }

    public void setScreen (Screen screen) {
 		if (game.getScreen() != null) game.getScreen().hide();
		game.setScreen(screen);
		if (game.getScreen() != null) {
			game.getScreen().show();
			game.getScreen().resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
	}

    public void setWorldScreen(Screen worldScreen) {
        this.worldScreen = worldScreen;
    }

    public Screen getWorldScreen() {
        return this.worldScreen;
    }
}
