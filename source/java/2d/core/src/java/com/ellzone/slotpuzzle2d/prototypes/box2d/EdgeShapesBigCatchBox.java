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
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.ellzone.slotpuzzle2d.camera.CameraHelper;
import com.ellzone.slotpuzzle2d.camera.CameraSettings;
import com.ellzone.slotpuzzle2d.physics.BoxBodyBuilder;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;

public class EdgeShapesBigCatchBox extends SPPrototype {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    PhysicsManagerCustomBodies physics;
    BoxBodyBuilder bodyFactory;

    @Override
    public void create() {
        camera = CameraHelper.GetCamera(CameraSettings.VIRTUAL_WIDTH, CameraSettings.VIRTUAL_HEIGHT);
        batch = new SpriteBatch();

        physics = new PhysicsManagerCustomBodies(camera);
        bodyFactory = physics.getBodyFactory();

        physics.createCircleBody(BodyDef.BodyType.DynamicBody, 120, 300,4);
        physics.createEdgeBody(BodyDef.BodyType.StaticBody,100,290,260,280);
        physics.createEdgeBody(BodyDef.BodyType.StaticBody,330,280,180,250);
        physics.createEdgeBody(BodyDef.BodyType.StaticBody,65,255,260,215);
        physics.createEdgeBody(BodyDef.BodyType.StaticBody,400,240,170,170);
        physics.createEdgeBody(BodyDef.BodyType.StaticBody,5,190,290,95);
        physics.createEdgeBody(BodyDef.BodyType.StaticBody,290,95,290,70);
        physics.createEdgeBody(BodyDef.BodyType.StaticBody,330,95,330,70);
        physics.createEdgeBody(BodyDef.BodyType.StaticBody,290,70,330,70);
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
