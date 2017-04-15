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

package com.ellzone.slotpuzzle2d.prototypes.tween;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.Level;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.scene.Tile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;

public class TileInputSelect extends SPPrototype {

    public class TestLevel1 extends Level {
        @Override
        public void initialise() {
        }

        @Override
        public String getImageName() {
            return "GamePopUp";
        }

        @Override
        public InputProcessor getInput() {
            return null;
        }

        @Override
        public String getTitle() {
            String title = "This is Test Level 1";
            return title;
        }

        @Override
        public void dispose() {
        }

		@Override
		public int getLevelNumber() {
			return 1;
		}
    }

    public class TestLevel2 extends Level {
        @Override
        public void initialise() {
        }

        @Override
        public String getImageName() {
            return "GamePopUp";
        }

        @Override
        public InputProcessor getInput() {
            return inputProcessor;
        }

        @Override
        public String getTitle() {
            String title = "This is Test Level 2";
            return title;
        }

        @Override
        public void dispose() {
        }

        private final InputProcessor inputProcessor = new InputAdapter() {
            @Override
            public boolean touchDown(int x, int y, int pointer, int button) {
                return true;
            }
        };

		@Override
		public int getLevelNumber() {
			return 2;
		}

    }

    private static final float MINIMUM_VIEWPORT_SIZE = 15.0f;
    private PerspectiveCamera cam;
    private SpriteBatch batch;
    private Sprite cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato;
    private Sprite[] sprites;
    private TweenManager tweenManager;
    private BitmapFont font;
    private TextureAtlas reelAtlas, tilesAtlas;
    private Tile tile, selectedTile;
    private final List<Tile> tiles = new ArrayList<Tile>();

    @Override
    public void create() {
        loadAssets();
        initialiseCamera();
        initialiseLibGdx();
        initialiseUniversalTweenEngine();
        createPopUps();
    }

    private void loadAssets() {
        Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
        Assets.inst().load("tiles/tiles.pack.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();

        reelAtlas = Assets.inst().get("reel/reels.pack.atlas", TextureAtlas.class);
        cherry = reelAtlas.createSprite("cherry");
        cheesecake = reelAtlas.createSprite("cheesecake");
        grapes = reelAtlas.createSprite("grapes");
        jelly = reelAtlas.createSprite("jelly");
        lemon = reelAtlas.createSprite("lemon");
        peach = reelAtlas.createSprite("peach");
        pear = reelAtlas.createSprite("pear");
        tomato = reelAtlas.createSprite("tomato");

        tilesAtlas = Assets.inst().get("tiles/tiles.pack.atlas", TextureAtlas.class);

        int i = 0;
        sprites = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
        for (Sprite sprite : sprites) {
            sprite.setOrigin(0, 0);
            sprite.setPosition(192 + i * sprite.getWidth(), Gdx.graphics.getHeight() / 2 - sprite.getHeight() / 2);
            i++;
        }
    }

    private void initialiseCamera() {
        cam = new PerspectiveCamera();
        cam.position.set(0, 0, 10);
        cam.lookAt(0, 0, 0);
        cam.update();
    }
    private void initialiseLibGdx() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        Gdx.input.setInputProcessor(launcherInputProcessor);
    }

    private void createPopUps() {
        Level level1 = new TestLevel1();
        Level level2 = new TestLevel2();
        tile = new Tile(20, 20, 200, 200, level1, tilesAtlas, cam, font, tweenManager);
        tiles.add(tile);
        tile = new Tile(20, 240, 200, 200, level2, tilesAtlas, cam, font, tweenManager);
        tiles.add(tile);
    }

    private void initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        tweenManager = new TweenManager();
    }

    private void closeSelectedTile() {
        selectedTile.minimize(minimizeCallback);
        selectedTile = null;
        Gdx.input.setInputProcessor(null);
    }

    private final TweenCallback minimizeCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            Tile tile = (Tile) source.getUserData();
            tile.getLevel().dispose();
            Gdx.input.setInputProcessor(launcherInputProcessor);
            Gdx.input.setCatchBackKey(false);
        }
    };

    private final TweenCallback maximizeCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            selectedTile = (Tile) source.getUserData();
            selectedTile.getLevel().initialise();
            Gdx.input.setInputProcessor(testInputMultiplexer);
            Gdx.input.setCatchBackKey(true);

            testInputMultiplexer.clear();
            testInputMultiplexer.addProcessor(testInputProcessor);
            if (selectedTile.getLevel().getInput() != null) {
                testInputMultiplexer.addProcessor(selectedTile.getLevel().getInput());
            }
        }
    };

    @Override
    public void resize(int width, int height) {
        float halfHeight = MINIMUM_VIEWPORT_SIZE * 0.5f;
        if (height > width)
            halfHeight *= (float)height / (float)width;
        float halfFovRadians = MathUtils.degreesToRadians * cam.fieldOfView * 0.5f;
        float distance = halfHeight / (float)Math.tan(halfFovRadians);
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.position.set(0, 0, distance);
        cam.lookAt(0, 0, 0);
        cam.update();
    }

    private void update(float delta) {
        tweenManager.update(delta);
    }

    @Override
    public void render() {
        final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        update(delta);
        batch.begin();
        for (int i=0; i<tiles.size(); i++) {
            tiles.get(i).draw(batch);
        }
        batch.end();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        if(batch != null) {
            batch.dispose();
        }
        Assets.inst().dispose();
    }

    private final InputProcessor launcherInputProcessor = new InputAdapter() {
        private boolean isDragged;

        @Override
        public boolean touchDown(int x, int y, int pointer, int button) {
            isDragged = false;
            return true;
        }

        @Override
        public boolean touchUp(int x, int y, int pointer, int button) {
            if (!isDragged) {
                Tile tile = getOverTile(x, cam.viewportHeight - y);

                if (tile != null) {
                    tiles.remove(tile);
                    tiles.add(tile);

                    tile.maximize(maximizeCallback);
                    Gdx.input.setInputProcessor(null);
                }
            }

            return true;
        }

        private Tile getOverTile(float x, float y) {
            for (int i=0; i<tiles.size(); i++)
                if (tiles.get(i).isOver(x, y)) return tiles.get(i);
            return null;
        }
    };

    private final InputMultiplexer testInputMultiplexer = new InputMultiplexer();
    private final InputProcessor testInputProcessor = new InputAdapter() {
        @Override
        public boolean keyDown(int keycode) {
            if ((keycode == Keys.BACK || keycode == Keys.ESCAPE) && selectedTile != null) {
                closeSelectedTile();
            }

            return false;
        }
    };
}