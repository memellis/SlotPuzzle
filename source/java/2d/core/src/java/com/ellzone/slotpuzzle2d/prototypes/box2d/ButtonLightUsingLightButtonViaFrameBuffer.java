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

package com.ellzone.slotpuzzle2d.prototypes.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.sprites.LightButton;
import box2dLight.RayHandler;

public class ButtonLightUsingLightButtonViaFrameBuffer extends SPPrototype {
    private static final String TAG = "ButtonLight";
    private static final float SCENE_WIDTH = 12.80f; // 12.8 metres wide
    private static final float SCENE_HEIGHT = 7.20f; // 7.2 metres high
    private static final float PIXELS_PER_METER = 100;
    private Viewport viewport, hudViewport;
    private Vector3 point = new Vector3();
    private SpriteBatch batch;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private RayHandler rayHandler;
    private Array<LightButton> lightButtons;

    @Override
    public void create () {
        super.create();
        viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT);
        viewport.getCamera().position.set(viewport.getCamera().position.x + SCENE_WIDTH*0.5f,
                viewport.getCamera().position.y + SCENE_HEIGHT*0.5f,
                0);
        viewport.getCamera().update();
        hudViewport = new FitViewport(SCENE_WIDTH * PIXELS_PER_METER, SCENE_HEIGHT * PIXELS_PER_METER, new OrthographicCamera());
        batch = new SpriteBatch();
        Gdx.input.setInputProcessor(this);

        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        rayHandler = new RayHandler(world);
        rayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.5f, 0.5f, 0.5f, 0.1f);

        lightButtons = new Array<LightButton>();
        for (int i = 0; i < 3; i++) {
            LightButton lightButton = new LightButton(world, rayHandler, i * 80 / PIXELS_PER_METER  + SCENE_WIDTH / 2 - (3 * 80 / PIXELS_PER_METER) / 2, (int) SCENE_HEIGHT / 2, 80, 80, new BitmapFont(), "","HOLD");
            lightButton.getSprite().setSize(80 / PIXELS_PER_METER, 80 / PIXELS_PER_METER);
            lightButtons.add(lightButton);
        }
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            point.set(screenX, screenY, 0);
            viewport.getCamera().unproject(point);
            for (LightButton lightButton : lightButtons) {
                if (lightButton.getSprite().getBoundingRectangle().contains(point.x, point.y)) {
                    if (lightButton.getLight().isActive()) {
                        lightButton.getLight().setActive(false);
                    } else {
                        lightButton.getLight().setActive(true);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hudViewport.update(width, height);
    }

    @Override
    public void dispose() {
        debugRenderer.dispose();
        batch.dispose();
        rayHandler.dispose();
        world.dispose();
    }

    @Override
    public void render () {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        world.step(1/60f, 6, 2);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        for (LightButton lightButton : lightButtons) {
            lightButton.getSprite().draw(batch);
        }
        batch.end();
        rayHandler.setCombinedMatrix(viewport.getCamera().combined);
        rayHandler.updateAndRender();
        debugRenderer.render(world, viewport.getCamera().combined);
    }
}
