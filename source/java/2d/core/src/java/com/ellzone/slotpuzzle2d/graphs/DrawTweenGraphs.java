package com.ellzone.slotpuzzle2d.graphs;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ellzone.slotpuzzle2d.screens.SplashScreen;
import com.ellzone.slotpuzzle2d.screens.TweenGraphsScreen;

public class DrawTweenGraphs extends Game {
    public SpriteBatch batch;
    public final static String SLOT_PUZZLE = "Slot Puzzle";

    @Override
    public void create() {
        setLogLevel();
        batch = new SpriteBatch();
        setScreen(new TweenGraphsScreen(this));
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
