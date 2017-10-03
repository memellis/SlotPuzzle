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

package com.ellzone.slotpuzzle2d.prototypes;

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class SPPrototypeTemplate extends SPPrototype {

    public static final float MINIMUM_VIEWPORT_SIZE = 15.0f;
    public static final String SLOTPUZZLE_PLAY = "SlotPuzzlePlay";

    protected final TweenManager tweenManager = new TweenManager();
    protected Sprite cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato;
    protected Sprite[] sprites;
	protected int spriteWidth, spriteHeight;
    protected PerspectiveCamera cam;
    protected SpriteBatch batch;
    protected final Random random = new Random();
    protected BitmapFont font;
	protected FitViewport viewport;
	protected Stage stage;
	protected int displayWindowHeight;
	protected int displayWindowWidth;

    protected abstract void initialiseOverride();
    protected abstract void initialiseScreenOverride();
    protected abstract void loadAssetsOverride();
    protected abstract void disposeOverride();
    protected abstract void updateOverride(float dt);
    protected abstract void renderOverride(float dt);
    protected abstract void initialiseUniversalTweenEngineOverride();

    @Override
    public void create() {
        if (isCustomDisplay()) {
            initialiseOverride();
            return;
        }

        initialiseLibGdx();
		initialiseScreen();
		initialiseCamera();
        loadAssets();
        initialiseUniversalTweenEngine();
        initialiseOverride();
    }

    protected void loadAssets() {
        if (isCustomDisplay()) {
            loadAssetsOverride();
            return;
        }
        Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();

        TextureAtlas reelAtlas = Assets.inst().get("reel/reels.pack.atlas", TextureAtlas.class);
        cherry = reelAtlas.createSprite("cherry");
        cheesecake = reelAtlas.createSprite("cheesecake");
        grapes = reelAtlas.createSprite("grapes");
        jelly = reelAtlas.createSprite("jelly");
        lemon = reelAtlas.createSprite("lemon");
        peach = reelAtlas.createSprite("peach");
        pear = reelAtlas.createSprite("pear");
        tomato = reelAtlas.createSprite("tomato");

        int i = 0;
        sprites = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
		spriteWidth = (int) cherry.getWidth();
		spriteHeight = (int) cherry.getWidth();
        float startPosition = (displayWindowWidth - sprites.length * cherry.getWidth()) / 2;
        for (Sprite sprite : sprites) {
            sprite.setOrigin(0, 0);
            sprite.setX(startPosition + i * sprite.getWidth());
			sprite.setY((float) displayWindowHeight / 2 - sprite.getHeight() / 2);
            i++;
        }

        loadAssetsOverride();
    }

	
    protected void initialiseCamera() {
        cam = new PerspectiveCamera();
        cam.position.set(0, 0, 10);
        cam.lookAt(0, 0, 0);
        cam.update();
		displayWindowWidth = SlotPuzzleConstants.V_WIDTH;
        displayWindowHeight = SlotPuzzleConstants.V_HEIGHT;
    }

    protected void initialiseLibGdx() {
        batch = new SpriteBatch();
        font = new BitmapFont();
    }

	protected void initialiseScreen() {
		viewport = new FitViewport(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT);
		stage = new Stage(viewport, batch);

        initialiseScreenOverride();
	}
	
    protected void initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        initialiseUniversalTweenEngineOverride();
    }

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
		viewport.update(width, height);
    }

    private void update(float delta) {
        tweenManager.update(delta);
        updateOverride(delta);
    }

    @Override
    public void render() {
        final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        if (isCustomDisplay()) {
            renderOverride(delta);
            return;
        }

        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        update(delta);
        renderOverride(delta);
		stage.draw();
    }

    @Override
    public void pause() {
        Gdx.app.log(SLOTPUZZLE_PLAY, "pause");
    }

    @Override
    public void resume() {
        Gdx.app.log(SLOTPUZZLE_PLAY, "resume");
    }

    public void dispose() {
        if (tweenManager != null) {
            tweenManager.killAll();
        }
        if (batch != null) {
            batch.dispose();
        }
		if (stage != null) {
			stage.dispose();
		}
        for (Sprite sprite : sprites) {
            sprite.getTexture().dispose();
        }
        Assets.inst().dispose();

        disposeOverride();
    }

    protected boolean isCustomDisplay() {
        return false;
    }
}
