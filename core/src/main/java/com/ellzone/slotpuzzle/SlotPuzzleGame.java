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

package com.ellzone.slotpuzzle;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;
import com.ellzone.slotpuzzle.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle.utils.assets.AssetsLoader;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class SlotPuzzleGame extends Game implements SlotPuzzleGameInterface {
    public SpriteBatch batch;
    public AnnotationAssetManager annotationAssetManager;
    private Screen worldScreen;
    private final TweenManager tweenManager = new TweenManager();

    @Override
    public void create() {
        setLogLevel();
        batch = LibGdxFactory.getInstance().newSpriteBatch();
        annotationAssetManager = loadAssets();
        setScreen(LibGdxFactory.getInstance().newLoadScreen(this));
    }

    private void setLogLevel() {
        String logLevel = System.getProperty(SlotPuzzleConstants.LIBGDX_LOGLEVEL_PROPERTY);
        if (logLevel != null)
            setApplicationLogLevel(logLevel);
        else {
            logLevel= System.getenv(SlotPuzzleConstants.LIBGDX_LOGLEVEL);
            if ((logLevel != null) && (!logLevel.equals("")))
                setApplicationLogLevel(logLevel);
        }
    }

    private void setApplicationLogLevel(String logLevel) {
        switch (logLevel) {
            case SlotPuzzleConstants.DEBUG:
                Gdx.app.setLogLevel(Application.LOG_DEBUG);
                break;
            case SlotPuzzleConstants.INFO:
                Gdx.app.setLogLevel(Application.LOG_INFO);
                break;
            case SlotPuzzleConstants.ERROR:
                Gdx.app.setLogLevel(Application.LOG_ERROR);
                break;
        }
    }

    private AnnotationAssetManager loadAssets() {
        AssetsLoader assetsLoader = new AssetsLoader();
        return assetsLoader.getAnnotationAssetManager();
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void dispose() {
        super.dispose();
        if (batch != null) {
            batch.dispose();
        }
        if (annotationAssetManager != null) {
            annotationAssetManager.dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE, "resize");
    }

    @Override
    public void pause() {
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE, "pause");
    }

    @Override
    public void resume() {
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE, "resume");
    }

    public void setWorldScreen(Screen worldScreen) {
        this.worldScreen = worldScreen;
    }

    @Override
    public Screen getWorldScreen() {
        return worldScreen;
    }

    @Override
    public void setScreen(Screen screen) {
        this.screen = screen;
        super.setScreen(screen);
    }

    @Override
    public Screen getScreen() { return screen; }

    @Override
    public SpriteBatch getBatch() { return batch; }

    @Override
    public AnnotationAssetManager getAnnotationAssetManager() { return annotationAssetManager; }

    @Override
    public TweenManager getTweenManager() { return tweenManager; }
}
