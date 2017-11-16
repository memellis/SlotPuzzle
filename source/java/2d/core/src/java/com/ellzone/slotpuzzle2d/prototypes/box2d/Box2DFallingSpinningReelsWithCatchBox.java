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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.camera.CameraHelper;
import com.ellzone.slotpuzzle2d.camera.CameraSettings;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.physics.BoxBodyBuilder;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReelHelper;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;

public class Box2DFallingSpinningReelsWithCatchBox extends SPPrototype {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private PhysicsManagerCustomBodies physics;
    private BoxBodyBuilder bodyFactory;
    private AnimatedReelHelper animatedReelHelper;
    private TweenManager tweenManager;
    private Array<Body> reelBoxes;
    private float centreX = SlotPuzzleConstants.V_WIDTH / 2;
    private float centreY = SlotPuzzleConstants.V_HEIGHT / 2;

    @Override
    public void create() {
        camera = CameraHelper.GetCamera(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT);
        batch = new SpriteBatch();

        initialiseUniversalTweenEngine();
        animatedReelHelper = new AnimatedReelHelper(tweenManager);

        physics = new PhysicsManagerCustomBodies(camera);
        bodyFactory = physics.getBodyFactory();

        physics.createEdgeBody(BodyDef.BodyType.StaticBody,
                centreX - 7 * 42 / 2,
                centreY - 4 * 42 / 2,
                centreX + 7 * 42 / 2,
                centreY - 4 * 42 / 2);
        physics.createEdgeBody(BodyDef.BodyType.StaticBody,
                centreX - 7 * 42 / 2,
                centreY - 4 * 42 / 2,
                centreX - 7 * 42 / 2,
                centreY + 4 * 42 / 2);
        physics.createEdgeBody(BodyDef.BodyType.StaticBody,
                centreX + 7 * 42 / 2,
                centreY - 4 * 42 / 2,
                centreX + 7 * 42 / 2,
                centreY + 4 * 42 / 2);

        reelBoxes = new Array<Body>();
        reelBoxes = createReelBoxes();
    }

    private Array<Body> createReelBoxes() {
        Array<Body> reelBoxes = new Array<Body>();
        for (int row = 0; row < 4; row++) {
            for (int column = 0; column < 7; column++) {
                reelBoxes.add(physics.createBoxBody(BodyDef.BodyType.DynamicBody,
                        centreX - 7 * 40 / 2 + 20 + (column * 40),
                        SlotPuzzleConstants.V_HEIGHT + (row * 40) / 2,
                        20,
                        20));
            }
        }
        return reelBoxes;
    }

    private void initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        tweenManager = new TweenManager();
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
