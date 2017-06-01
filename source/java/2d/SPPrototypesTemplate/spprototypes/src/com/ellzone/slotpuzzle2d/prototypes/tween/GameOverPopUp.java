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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Quad;

public class GameOverPopUp extends SPPrototypeTemplate {

	private Sprite gameOverPopUp, game, over;
    private BitmapFont font;
	
	@Override
	protected void initialiseOverride() {
		Gdx.input.setInputProcessor(inputProcessor);
		createGameOverPopUp();
		font = new BitmapFont();
	}

	@Override
	protected void loadAssetsOverride() {
		Assets.inst().load("tiles/tiles.pack.atlas", TextureAtlas.class);
		Assets.inst().update();
		Assets.inst().finishLoading();
		
		TextureAtlas tilesAtlas = Assets.inst().get("tiles/tiles.pack.atlas", TextureAtlas.class);
        gameOverPopUp = tilesAtlas.createSprite("GamePopUp");
        game = tilesAtlas.createSprite("game");
        over = tilesAtlas.createSprite("over");

        gameOverPopUp.setPosition(displayWindowWidth / 2 - gameOverPopUp.getWidth() / 2, displayWindowHeight/ 2 - gameOverPopUp.getHeight() /2);
        game.setPosition(-200, displayWindowHeight / 2 - game.getHeight() /2);
        over.setPosition(200 + displayWindowWidth, displayWindowHeight / 2 - over.getHeight() /2);
	}

	@Override
	protected void disposeOverride() {
        font.dispose();
	}

	@Override
	protected void updateOverride(float dt) {
	}

	@Override
	protected void renderOverride(float dt) {
        batch.begin();
        gameOverPopUp.draw(batch);
        game.draw(batch);
        over.draw(batch);
        font.draw(batch, "Touch/Click to try again", game.getX() + 32, game.getY() - 32);
	    batch.end();
	}

	@Override
	protected void initialiseUniversalTweenEngineOverride() {
		SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());        
    }

    private void createGameOverPopUp() {
        Timeline.createSequence()
                .push(SlotPuzzleTween.set(gameOverPopUp, SpriteAccessor.SCALE_XY).target(0.1f, 0))
                .push(SlotPuzzleTween.set(game, SpriteAccessor.POS_XY). target(-200, displayWindowWidth / 2 - game.getHeight() /2))
                .push(SlotPuzzleTween.set(over, SpriteAccessor.POS_XY). target(200 + displayWindowWidth, displayWindowHeight / 2 - over.getHeight() /2))
                .pushPause(0.5f)
                .push(SlotPuzzleTween.to(gameOverPopUp, SpriteAccessor.SCALE_XY, 0.8f).target(1, 0.6f).ease(Back.OUT))
                .pushPause(-0.3f)
                .pushPause(0.3f)
                .pushPause(0.3f)
                .beginParallel()
                .push(SlotPuzzleTween.to(gameOverPopUp, SpriteAccessor.SCALE_XY, 0.5f).target(1.1f, 1.1f).ease(Back.IN))
                .push(SlotPuzzleTween.to(gameOverPopUp, SpriteAccessor.OPACITY, 0.5f).target(0.7f).ease(Back.IN))
                .end()
                .pushPause(0.3f)
                .pushPause(-0.3f)
                .beginParallel()
                .push(SlotPuzzleTween.to(game, SpriteAccessor.POS_XY, 1.0f).target(displayWindowWidth / 2 - game.getWidth(), displayWindowHeight / 2 - game.getHeight() /2).ease(Back.INOUT))
                .push(SlotPuzzleTween.to(over, SpriteAccessor.POS_XY, 1.0f).target(displayWindowWidth / 2, displayWindowHeight / 2 - over.getHeight() / 2).ease(Back.INOUT))
                .end()
                .pushPause(0.5f)
                .start(tweenManager);
    }

    private void hideGameOverPopUp() {
        Timeline.createSequence()
                .pushPause(0.25f)
                .beginParallel()
                .push((SlotPuzzleTween.to(gameOverPopUp, SpriteAccessor.POS_XY, 1.5f)
                        .waypoint(displayWindowWidth / 2 - gameOverPopUp.getWidth() / 2, displayWindowHeight / 2 - gameOverPopUp.getHeight() / 2 + 64)
                        .target(displayWindowWidth / 2 - gameOverPopUp.getWidth() / 2, -300).ease(Quad.OUT)))
                .push((SlotPuzzleTween.to(game, SpriteAccessor.POS_XY, 1.5f)
                        .waypoint(displayWindowWidth / 2 - game.getWidth(), displayWindowHeight / 2 - game.getHeight() /2 + 64)
                        .target(displayWindowWidth / 2 - game.getWidth(), -300).ease(Quad.OUT)))
                .push((SlotPuzzleTween.to(over, SpriteAccessor.POS_XY, 1.5f)
                        .waypoint(displayWindowWidth / 2, displayWindowHeight / 2 - over.getHeight() / 2 + 64)
                        .target(displayWindowWidth / 2, -300).ease(Quad.OUT)))
                .end()
                .pushPause(0.5f)
                .start(tweenManager);
    }

    private final InputProcessor inputProcessor = new InputAdapter() {
        @Override
        public boolean touchUp(int x, int y, int pointer, int button) {
            tweenManager.killAll();
            hideGameOverPopUp();
            return true;
        }
    };
}
