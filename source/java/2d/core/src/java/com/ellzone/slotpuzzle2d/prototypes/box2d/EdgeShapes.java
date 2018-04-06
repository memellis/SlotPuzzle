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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.ellzone.slotpuzzle2d.camera.CameraHelper;
import com.ellzone.slotpuzzle2d.camera.CameraSettings;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;

public class EdgeShapes extends SPPrototype {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    PhysicsManagerCustomBodies physics;

    @Override
    public void create() {
        this.camera = CameraHelper.GetCamera(CameraSettings.VIRTUAL_WIDTH, CameraSettings.VIRTUAL_HEIGHT);
        this.batch = new SpriteBatch();

        this.physics = new PhysicsManagerCustomBodies(camera);
        this.physics.createCircleBody(BodyDef.BodyType.DynamicBody, 120, 300,4);
        this.physics.createEdgeBody(BodyDef.BodyType.StaticBody,100,290,260,280);
        this.physics.createEdgeBody(BodyDef.BodyType.StaticBody,330,280,180,250);
        this.physics.createEdgeBody(BodyDef.BodyType.StaticBody,65,255,260,215);
        this.physics.createEdgeBody(BodyDef.BodyType.StaticBody,400,240,170,170);
        this.physics.createEdgeBody(BodyDef.BodyType.StaticBody,5,190,290,95);
        this.physics.createEdgeBody(BodyDef.BodyType.StaticBody,290,95,290,70);
        this.physics.createEdgeBody(BodyDef.BodyType.StaticBody,315,95,315,70);
        this.physics.createEdgeBody(BodyDef.BodyType.StaticBody,290,70,315,70);
    }

    @Override
    public void dispose() {
        if (this.physics != null) {
            this.physics.dispose();
        }
        if (this.batch != null) {
            this.batch.dispose();
        }
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.update(Gdx.graphics.getDeltaTime());
        this.physics.draw(batch);
    }

    private void update(float dt) {
        this.physics.update(dt);
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
