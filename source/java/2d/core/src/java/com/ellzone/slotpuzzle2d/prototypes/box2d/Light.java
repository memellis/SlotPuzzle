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

package com.ellzone.slotpuzzle2d.prototypes.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class Light extends SPPrototypeTemplate {
    Viewport lightViewport;
    World world;
    Box2DDebugRenderer debugRenderer;
    RayHandler rayHandler;
    PointLight light;
    Vector3 point = new Vector3();
    float sceneWidth = SlotPuzzleConstants.V_WIDTH / SlotPuzzleConstants.PIXELS_PER_METER;
    float sceneHeight = SlotPuzzleConstants.V_HEIGHT / SlotPuzzleConstants.PIXELS_PER_METER;

    @Override
    protected void initialiseOverride() {
        initialiseBox2d();
        initialiseLights();
        Gdx.input.setInputProcessor(this);
    }

    @Override
    protected void initialiseScreenOverride() {
        lightViewport = new FitViewport(sceneWidth, sceneHeight);
        lightViewport.getCamera().position.set(lightViewport.getCamera().position.x + sceneWidth * 0.5f,
                lightViewport.getCamera().position.y + sceneHeight * 0.5f,
                0);
        lightViewport.getCamera().update();
    }

    private void initialiseBox2d() {
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        rayHandler = new RayHandler(world);
        rayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.5f, 0.5f, 0.5f, 0.1f);
    }

    private void initialiseLights() {
        light = new PointLight(rayHandler, 32);
        light.setActive(false);
        light.setColor(Color.RED);
        light.setDistance(1.5f);
    }

    @Override
    protected void loadAssetsOverride() {

    }

    @Override
    protected void disposeOverride() {
        debugRenderer.dispose();
        world.dispose();
        rayHandler.dispose();
    }

    @Override
    protected void updateOverride(float dt) {

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            lightViewport.getCamera().unproject(point.set(screenX, screenY, 0));
            light.setPosition(point.x, point.y);
            light.setActive(true);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            light.setActive(false);
            return true;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int x, int y, int pointer) {
        lightViewport.getCamera().unproject(point.set(x, y, 0));
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            light.setPosition(point.x, point.y);
        }
        return false;
    }

    @Override
    protected void renderOverride(float dt) {
        batch.begin();
        for (Sprite sprite : reels.getReels()) {
            sprite.draw(batch);
        }
        batch.end();
        rayHandler.setCombinedMatrix(lightViewport.getCamera().combined);
        rayHandler.updateAndRender();
        debugRenderer.render(world, lightViewport.getCamera().combined);
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {

    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);
        lightViewport.update(width, height);
    }
}