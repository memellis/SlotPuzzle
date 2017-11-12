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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.camera.CameraHelper;
import com.ellzone.slotpuzzle2d.camera.CameraSettings;
import com.ellzone.slotpuzzle2d.physics.BoxBodyBuilder;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;

public class Box2dFallingReelsWithCatchBox extends SPPrototype {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    PhysicsManagerCustomBodies physics;
    BoxBodyBuilder bodyFactory;
    Array<Body> reelBoxes;

    @Override
    public void create() {
        camera = CameraHelper.GetCamera(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT);
        batch = new SpriteBatch();

        physics = new PhysicsManagerCustomBodies(camera);
        bodyFactory = physics.getBodyFactory();

        physics.createEdgeBody(BodyDef.BodyType.StaticBody,120,160,120,70);
        physics.createEdgeBody(BodyDef.BodyType.StaticBody,330,160,330,70);
        physics.createEdgeBody(BodyDef.BodyType.StaticBody,120,70,330,70);
        reelBoxes = new Array<Body>();
        reelBoxes = createReelBoxes();

    }

    private Array<Body> createReelBoxes() {
        Array<Body> reelBoxes = new Array<Body>();
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 7; column++) {
                reelBoxes.add(physics.createBoxBody(BodyDef.BodyType.DynamicBody, 131.5f + (row * 10) + (column * 10), 320, 10, 10));
            }
        }
        return reelBoxes;
    }

    @Override
    public void dispose() {
        physics.dispose();
        batch.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        update(Gdx.graphics.getDeltaTime());

        physics.draw(batch);
    }

    private void update(float dt) {
        physics.update(dt);
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
}