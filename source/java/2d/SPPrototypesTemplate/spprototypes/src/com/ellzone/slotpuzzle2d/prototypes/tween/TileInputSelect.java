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
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.badlogic.gdx.math.Vector2;

public class TileInputSelect extends SPPrototypeTemplate {

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

    private BitmapFont font;
    private Tile tile, selectedTile;
    private final List<Tile> tiles = new ArrayList<Tile>();
	private TextureAtlas tilesAtlas;
	
	@Override
	protected void initialiseOverride() {
        font = new BitmapFont();
        Gdx.input.setInputProcessor(launcherInputProcessor);
        createPopUps();
	}

	@Override
	protected void loadAssetsOverride() {
        Assets.inst().load("tiles/tiles.pack.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();
        tilesAtlas = Assets.inst().get("tiles/tiles.pack.atlas", TextureAtlas.class);
	}

	@Override
	protected void disposeOverride() {
	}

	@Override
	protected void updateOverride(float dt) {
	}

	@Override
	protected void renderOverride(float dt) {
        batch.begin();
        for (int i=0; i<tiles.size(); i++) {
            tiles.get(i).draw(batch);
        }
        batch.end();
	}

	@Override
	protected void initialiseUniversalTweenEngineOverride() {
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
	}
	
    private void createPopUps() {
        Level level1 = new TestLevel1();
        Level level2 = new TestLevel2();
        tile = new Tile(20, 20, 200, 200, level1, tilesAtlas, cam, font, tweenManager);
        tiles.add(tile);
        tile = new Tile(20, 240, 200, 200, level2, tilesAtlas, cam, font, tweenManager);
        tiles.add(tile);
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

    private final InputProcessor launcherInputProcessor = new InputAdapter() {
        private boolean isDragged;

        @Override
        public boolean touchDown(int x, int y, int pointer, int button) {
            isDragged = false;
            return true;
        }

        @Override
        public boolean touchUp(int x, int y, int pointer, int button) {
	        Vector2 point = new Vector2(x , y);	
			point = viewport.unproject(point);
            if (!isDragged) {
				Gdx.app.log("tileinputselect", "point.x"+point.x+ " point.y=" + point.y);
                Tile tile = getOverTile(point.x, point.y);

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
            for (int i=0; i<tiles.size(); i++) {
                if (tiles.get(i).isOver(x, y)) {
					return tiles.get(i);
				}
			}
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
