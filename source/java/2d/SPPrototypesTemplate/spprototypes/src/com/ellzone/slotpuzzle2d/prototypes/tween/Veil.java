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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import aurelienribon.tweenengine.equations.Back;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;

public class Veil extends SPPrototypeTemplate {

	private Sprite strip, veil;
	
	@Override
	protected void initialiseOverride() {
        Gdx.input.setInputProcessor(inputProcessor);
        createSequence();
	}

	@Override
	protected void loadAssetsOverride() {
		Assets.inst().load("splash/pack.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();
        TextureAtlas splashAtlas = Assets.inst().get("splash/pack.atlas", TextureAtlas.class);
        strip = splashAtlas.createSprite("white");
        veil = splashAtlas.createSprite("white");

        strip.setOrigin(displayWindowWidth / 2, displayWindowHeight / 2);
        strip.setPosition(0, 0);
        strip.setSize(displayWindowWidth, displayWindowHeight);

        veil.setOrigin(displayWindowHeight / 2, displayWindowHeight / 2);
        veil.setPosition(0, 0);
        veil.setSize(displayWindowWidth, displayWindowHeight);
        veil.setColor(1, 1, 1, 0);
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
        strip.draw(batch);
        if (veil.getColor().a > 0.1f) {
            veil.draw(batch);
        }
		for (Sprite sprite : sprites) {
			sprite.draw(batch);
		}
        batch.end();
 	}

	@Override
	protected void initialiseUniversalTweenEngineOverride() {
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
	}

    private void createSequence() {
        Timeline.createSequence()
                .push(SlotPuzzleTween.set(strip, SpriteAccessor.SCALE_XY).target(1, 0))
                .pushPause(0.5f)
                .push(SlotPuzzleTween.to(strip, SpriteAccessor.SCALE_XY, 0.8f).target(1, 0.6f).ease(Back.OUT))
                .pushPause(-0.3f)
                .pushPause(0.3f)
                .pushPause(0.3f)
                .push(SlotPuzzleTween.to(strip, SpriteAccessor.SCALE_XY, 0.5f).target(1, 1).ease(Back.IN))
                .pushPause(0.3f)
                .pushPause(-0.3f)
                .pushPause(0.5f)
                .start(tweenManager);
    }
 
    private final InputProcessor inputProcessor = new InputAdapter() {
        @Override
        public boolean touchUp(int x, int y, int pointer, int button) {
            SlotPuzzleTween.to(veil, SpriteAccessor.OPACITY, 0.7f)
                    .target(1)
                    .start(tweenManager);
            return true;
        }
    };
}
