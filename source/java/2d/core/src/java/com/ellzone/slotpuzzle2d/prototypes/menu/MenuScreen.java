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

package com.ellzone.slotpuzzle2d.prototypes.menu;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypesGame;
import com.ellzone.slotpuzzle2d.prototypes.map.WorldScreenPrototype;
import com.ellzone.slotpuzzle2d.utils.UiUtils;

public class MenuScreen implements Screen {
    SlotPuzzleGame game;
    Skin skin;
    Stage stage;
    FitViewport viewport;
    BitmapFont font;
    String gdxVersion = "";
    Integer fps;
    boolean enteredSubScreen = false;
    boolean worldScreenPrototype = false;

    public MenuScreen(SlotPuzzleGame game) {
        this.game = game;
	    defineMenuScreen();
    }

    private void defineMenuScreen() {
        initialiseScreen();
        font = new BitmapFont(); 
	    Gdx.input.setInputProcessor(stage);

	    skin = new Skin();
        UiUtils.createBasicSkin(skin);
        createButtons();
    }

    private void initialiseScreen(){
	    viewport = new FitViewport(SPPrototypesGame.V_WIDTH, SPPrototypesGame.V_HEIGHT, game.camera);
	    stage = new Stage(viewport, game.batch);
    }

    private void createButtons() {
        TextButton gdxButton = new TextButton("Get libGDX version", skin);
        gdxButton.setPosition(SPPrototypesGame.V_WIDTH / 2 - SPPrototypesGame.V_WIDTH / 8 , SPPrototypesGame.V_HEIGHT/2 - gdxButton.getHeight());
        stage.addActor(gdxButton);
        gdxButton.addListener(new ChangeListener() {
            @Override
	        public void changed(ChangeEvent event, Actor actor) {
                gdxVersion = SPPrototypesGame.gdxVersion.VERSION;
	        }
	    });

        TextButton worldMapButton = new TextButton("WorldMap Prototype", skin);
        worldMapButton.setPosition(SPPrototypesGame.V_WIDTH / 2 - SPPrototypesGame.V_WIDTH / 8, SPPrototypesGame.V_HEIGHT / 2 - 2 * worldMapButton.getHeight());
        stage.addActor(worldMapButton);
        worldMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                worldScreenPrototype = true;
            }
        });

    }

    @Override
    public void show() {
    }

    private void update(float delta) {
        if (enteredSubScreen) {
	        Gdx.input.setInputProcessor(stage);
	    }
	    if (worldScreenPrototype) {
            worldScreenPrototype = false;
            game.setScreen(new WorldScreenPrototype(game));
        }
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    update(delta);
	    game.batch.begin();
	    fps = Gdx.graphics.getFramesPerSecond();
        font.setColor(Color.YELLOW);
	    font.draw(game.batch,"fps:" + fps, 10, 15);
	    font.draw(game.batch, gdxVersion, SPPrototypesGame.V_WIDTH - 40, SPPrototypesGame.V_HEIGHT - 10);
	    game.batch.end();

        stage.act();
        stage.draw();
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
	    stage.dispose();
    }
}
