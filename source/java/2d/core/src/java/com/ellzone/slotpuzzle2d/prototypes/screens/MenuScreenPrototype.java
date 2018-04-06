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

package com.ellzone.slotpuzzle2d.prototypes.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypesGame;
import com.ellzone.slotpuzzle2d.prototypes.map.WorldScreenPrototype;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.utils.UiUtils;

import java.util.Random;

public class MenuScreenPrototype implements Screen {
    private SlotPuzzle game;
    private LevelDoor levelDoor;
    private MapTile mapTile;
    private Viewport viewport;
    private Stage stage;
    private BitmapFont font;
    private Skin skin;
    private String gdxVersion = "";
    private Integer fps;
    private boolean level1Won = false;
    private Random random;

    public MenuScreenPrototype(SlotPuzzle game, LevelDoor levelDoor, MapTile mapTile) {
        this.game = game;
        this.levelDoor = levelDoor;
        this.mapTile = mapTile;
        random = new Random();
        definePlayScreen();
    }

    private void definePlayScreen() {
        initialiseScreen();
        font = new BitmapFont();
        Gdx.input.setInputProcessor(stage);
        skin = new Skin();
        UiUtils.createBasicSkin(skin);
        createButtons();
    }

    private void initialiseScreen(){
        viewport = new FitViewport(SPPrototypesGame.V_WIDTH, SPPrototypesGame.V_HEIGHT, new OrthographicCamera());
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

        TextButton worldMapButton = new TextButton("I've won Level 1 ", skin);
        worldMapButton.setPosition(SPPrototypesGame.V_WIDTH / 2 - SPPrototypesGame.V_WIDTH / 8, SPPrototypesGame.V_HEIGHT / 2 - 2 * worldMapButton.getHeight());
        stage.addActor(worldMapButton);
        worldMapButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                level1Won = true;
            }
        });
    }

    @Override
    public void show() {
    }

    private void update(float delta) {
        if (level1Won) {
            mapTile.getLevel().setLevelCompleted();
            mapTile.getLevel().setScore(random.nextInt(1000));
            ((WorldScreenPrototype)game.getWorldScreen()).worldScreenCallBack();
            game.setScreen(game.getWorldScreen());
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
    }
}
