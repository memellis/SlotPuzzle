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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.level.LevelPopUp;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;

public class LevelPopUpUsingLevelPopUp extends SPPrototypeTemplate {

    public static final String LEVEL_DESC =  "Reveal the hidden pattern to complete the level.";
    public static final String CURRENT_LEVEL = "1-1";
    private LevelPopUp levelPopUp;
    private Array<Sprite> sprites;
    private BitmapFont currentLevelFont;

    @Override
    protected void initialiseOverride() {
        currentLevelFont = new BitmapFont();
        currentLevelFont.getData().scale(1.5f);
        levelPopUp = new LevelPopUp(batch, tweenManager, sprites, currentLevelFont, CURRENT_LEVEL, LEVEL_DESC);
        Gdx.input.setInputProcessor(inputProcessor);
        levelPopUp.showLevelPopUp(null);
    }

    @Override
    protected void initialiseScreenOverride() {

    }

    @Override
    protected void loadAssetsOverride() {
        Assets.inst().load("tiles/tiles.pack.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();
        TextureAtlas tilesAtlas = Assets.inst().get("tiles/tiles.pack.atlas", TextureAtlas.class);

        sprites = new Array<Sprite>();
        sprites.add(tilesAtlas.createSprite("GamePopUp"));
        sprites.add(tilesAtlas.createSprite("level"));

        sprites.get(0).setPosition(displayWindowWidth / 2 - sprites.get(0).getWidth() / 2, displayWindowHeight / 2 - sprites.get(0).getHeight() /2);
        sprites.get(1).setPosition(-200, displayWindowHeight / 2 - sprites.get(1).getHeight() /2);
    }

    @Override
    protected void disposeOverride() {
        Assets.inst().dispose();
    }

    @Override
    protected void updateOverride(float dt) {
    }

    @Override
    protected void renderOverride(float dt) {
        levelPopUp.draw(batch);
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {
		SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
	}

    private final InputProcessor inputProcessor = new InputAdapter() {
        @Override
        public boolean touchUp(int x, int y, int pointer, int button) {
            tweenManager.killAll();
            levelPopUp.hideLevelPopUp(null);
            return true;
        }
    };
}
