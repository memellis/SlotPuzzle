/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ellzone.slotpuzzle2d.prototypes.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;

public class CollidingFallingReels extends SPPrototypeTemplate {
    Texture img;
    World world;
    Body body,body2;
    Body bodyEdgeScreen;
    Sprite[] sprites;
    Matrix4 debugMatrix;

    final float PIXELS_TO_METERS = 100f;

    final short PHYSICS_ENTITY = 0x1;    // 0001
    final short WORLD_ENTITY = 0x1 << 1; // 0010 or 0x2 in hex


    @Override
    protected void initialiseOverride() {
        sprites = reels.getReels();
        sprites[0].setPosition(-sprites[0].getWidth() / 2, -sprites[0].getHeight()/2 + 200);
        sprites[1].setPosition(-sprites[0].getWidth() / 2 + 20, -sprites[0].getHeight()/2 + 400);

        world = new World(new Vector2(0, -1f),true);

        // Sprite1's Physics body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((sprites[0].getX() + sprites[0].getWidth()/2) / PIXELS_TO_METERS,
                             (sprites[0].getY() + sprites[0].getHeight()/2) / PIXELS_TO_METERS);

        body = world.createBody(bodyDef);

        // Sprite2's physics body
        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.type = BodyDef.BodyType.DynamicBody;
        bodyDef2.position.set((sprites[1].getX() + sprites[1].getWidth()/2) / PIXELS_TO_METERS,
                              (sprites[1].getY() + sprites[1].getHeight()/2) / PIXELS_TO_METERS);

        body2 = world.createBody(bodyDef2);

        // Both bodies have identical shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(sprites[0].getWidth() / 2 / PIXELS_TO_METERS, sprites[0].getHeight() / 2 / PIXELS_TO_METERS);

        // Sprite1
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        fixtureDef.restitution = 0.5f;
        fixtureDef.filter.categoryBits = PHYSICS_ENTITY;
        fixtureDef.filter.maskBits = WORLD_ENTITY;


        // Sprite2
        FixtureDef fixtureDef2 = new FixtureDef();
        fixtureDef2.shape = shape;
        fixtureDef2.density = 0.1f;
        fixtureDef2.restitution = 0.5f;
        fixtureDef2.filter.categoryBits = PHYSICS_ENTITY;
        fixtureDef2.filter.maskBits = WORLD_ENTITY;

        body.createFixture(fixtureDef);
        body2.createFixture(fixtureDef2);

        shape.dispose();

        // Now the physics body of the bottom edge of the screen
        BodyDef bodyDef3 = new BodyDef();
        bodyDef3.type = BodyDef.BodyType.StaticBody;

        float w = Gdx.graphics.getWidth()/PIXELS_TO_METERS;
        float h = Gdx.graphics.getHeight()/PIXELS_TO_METERS;

        bodyDef3.position.set(0,0);
        FixtureDef fixtureDef3 = new FixtureDef();
        fixtureDef3.filter.categoryBits = WORLD_ENTITY;
        fixtureDef3.filter.maskBits = PHYSICS_ENTITY;

        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(-w/2,-h/2,w/2,-h/2);
        fixtureDef3.shape = edgeShape;

        bodyEdgeScreen = world.createBody(bodyDef3);
        bodyEdgeScreen.createFixture(fixtureDef3);
        edgeShape.dispose();
    }

    @Override
    protected void initialiseScreenOverride() {
    }

    @Override
    protected void loadAssetsOverride() {
    }

    @Override
    protected void disposeOverride() {
        img.dispose();
        world.dispose();
    }

    @Override
    protected void updateOverride(float dt) {
    }

    @Override
    protected void renderOverride(float dt) {
        world.step(1f/60f, 6, 2);

        sprites[0].setPosition((body.getPosition().x * PIXELS_TO_METERS) - sprites[0].getWidth()/2 ,
                               (body.getPosition().y * PIXELS_TO_METERS) - sprites[0].getHeight()/2 );


        sprites[0].setRotation((float)Math.toDegrees(body2.getAngle()));
        sprites[1].setPosition((body2.getPosition().x * PIXELS_TO_METERS) - sprites[1].getWidth()/2 ,
                               (body2.getPosition().y * PIXELS_TO_METERS) - sprites[1].getHeight()/2 );
        sprites[1].setRotation((float)Math.toDegrees(body.getAngle()));

        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(orthographicCamera.combined);
        batch.begin();

        batch.draw(sprites[0],
                   sprites[0].getX(),
                   sprites[0].getY(),
                   sprites[0].getOriginX(),
                   sprites[0].getOriginY(),
                   sprites[0].getWidth(),
                   sprites[0].getHeight(),
                   sprites[0].getScaleX(),
                   sprites[0].getScaleY(),
                   sprites[0].getRotation());

        batch.draw(sprites[1],
                   sprites[1].getX(),
                   sprites[1].getY(),
                   sprites[1].getOriginX(),
                   sprites[1].getOriginY(),
                   sprites[1].getWidth(),
                   sprites[1].getHeight(),
                   sprites[1].getScaleX(),
                   sprites[1].getScaleY(),
                   sprites[1].getRotation());

        batch.end();
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {
    }
}
