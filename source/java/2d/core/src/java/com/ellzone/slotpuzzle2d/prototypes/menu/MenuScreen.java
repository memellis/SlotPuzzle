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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.MapLevel1;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypesGame;
import com.ellzone.slotpuzzle2d.prototypes.map.WorldScreenPrototype;
import com.ellzone.slotpuzzle2d.prototypes.screens.PlayScreenPrototype;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.UiUtils;
import com.ellzone.slotpuzzle2d.prototypes.screens.IntroScreenPrototype;
import com.ellzone.slotpuzzle2d.prototypes.typewriter.TypewriterScreen;

public class MenuScreen implements Screen {
    private static final String TILE_PACK_ATLAS = "tiles/tiles.pack.atlas";
    SlotPuzzle game;
    Skin skin;
    Stage stage;
    FitViewport viewport;
    BitmapFont font;
    String gdxVersion = "";
    Integer fps;
    boolean enteredSubScreen = false;
	boolean introScreenPrototype = false;
    boolean worldScreenPrototype = false;
    boolean playScreenPrototype = false;
	boolean typeWriterPrototype = false;
    LevelDoor levelDoor;
    MapTile mapTile;
    TweenManager tweenManager = new TweenManager();
    private TextureAtlas tilesAtlas;
    float w, h;

    public MenuScreen(SlotPuzzle game) {
        this.game = game;
	    defineMenuScreen();
    }

    private void defineMenuScreen() {
        initialiseScreen();
        this.font = new BitmapFont(); 
	    Gdx.input.setInputProcessor(stage);

	    this.skin = new Skin();
        UiUtils.createBasicSkin(skin);
        createButtons();
        setLevel();
        Assets.inst().load(TILE_PACK_ATLAS, TextureAtlas.class);
        Assets.inst().finishLoading();
        tilesAtlas = Assets.inst().get(TILE_PACK_ATLAS, TextureAtlas.class);
        createTile();
    }

    private void initialiseScreen(){
	    this.viewport = new FitViewport(SPPrototypesGame.V_WIDTH, SPPrototypesGame.V_HEIGHT, new OrthographicCamera());
	    this.stage = new Stage(viewport, game.batch);
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
		
		TextButton introScreenButton = new TextButton("IntroScreen Prototype", skin);
        introScreenButton.setPosition(SPPrototypesGame.V_WIDTH / 2 - SPPrototypesGame.V_WIDTH / 8, SPPrototypesGame.V_HEIGHT / 2 - 2 * introScreenButton.getHeight());
        this.stage.addActor(introScreenButton);
        introScreenButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					introScreenPrototype = true;
				}
			});

        TextButton worldScreenButton = new TextButton("WorldMap Prototype", skin);
        worldScreenButton.setPosition(SPPrototypesGame.V_WIDTH / 2 - SPPrototypesGame.V_WIDTH / 8, SPPrototypesGame.V_HEIGHT / 2 - 3 * worldScreenButton.getHeight());
        this.stage.addActor(worldScreenButton);
        worldScreenButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                worldScreenPrototype = true;
            }
        });

        TextButton playScreenButton = new TextButton("PlayScreen Prototype", skin);
        playScreenButton.setPosition(SPPrototypesGame.V_WIDTH / 2 - SPPrototypesGame.V_WIDTH / 8, SPPrototypesGame.V_HEIGHT / 2 - 4 * playScreenButton.getHeight());
        this.stage.addActor(playScreenButton);
        playScreenButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                playScreenPrototype = true;
                setUpPlayScreenPrototye();
            }
        });

		TextButton typeWriterButton = new TextButton("TypeWriter Prototype", skin);
        typeWriterButton.setPosition(SPPrototypesGame.V_WIDTH / 2 - SPPrototypesGame.V_WIDTH / 8, SPPrototypesGame.V_HEIGHT / 2 - 5 * typeWriterButton.getHeight());
        this.stage.addActor(typeWriterButton);
        typeWriterButton.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					typeWriterPrototype = true;
				}
        });
    }

    private void setUpPlayScreenPrototye() {

    }

    private void setLevel() {
        this.levelDoor = new LevelDoor();
        this.levelDoor.levelName = "Level 1";
        this.levelDoor.levelType = "HiddenPattern";
    }

    private void createTile() {
        mapTile = new MapTile(20, 20, 200, 200, w, h, new MapLevel1(), tilesAtlas, null, font, tweenManager, new Sprite());
    }

    @Override
    public void show() {
    }

    private void update(float delta) {
        if (this.enteredSubScreen) {
	        Gdx.input.setInputProcessor(stage);
	    }
		if (this.introScreenPrototype) {
			this.introScreenPrototype = false;
			this.game.setScreen(new IntroScreenPrototype(this.game));
		}
	    if (this.worldScreenPrototype) {
            this.worldScreenPrototype = false;
            this.game.setScreen(new WorldScreenPrototype(this.game));
        }
        if (this.playScreenPrototype) {
            this.playScreenPrototype = false;
            this.game.setScreen(new PlayScreenPrototype(this.game, levelDoor, null));
        }
		if (this.typeWriterPrototype) {
			this.typeWriterPrototype = false;
			this.game.setScreen(new TypewriterScreen(this.game));
		}
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    update(delta);
	    game.batch.begin();
	    this.fps = Gdx.graphics.getFramesPerSecond();
        this.font.setColor(Color.YELLOW);
	    this.font.draw(game.batch,"fps:" + fps, 10, 15);
	    this.font.draw(game.batch, gdxVersion, SPPrototypesGame.V_WIDTH - 40, SPPrototypesGame.V_HEIGHT - 10);
	    this.game.batch.end();

        this.stage.act();
        this.stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        this.w = width;
        this.h = height;
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
	    this.stage.dispose();
    }
}
