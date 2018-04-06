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

import box2dLight.Light;
import box2dLight.PointLight;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.*;

public class ButtonLight extends SPPrototype {

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
	private Light light;
	private Sprite lightButton;
	private enum ButtonState {LIGHT_ON, LIGHT_OFF};
	private ButtonState buttonLight;
	private BitmapFont font;
	
	@Override
	public void create () {
	    super.create();
		font = new BitmapFont();
	    viewport = new FitViewport(SCENE_WIDTH, SCENE_HEIGHT);
	    // Center camera
	    viewport.getCamera().position.set(viewport.getCamera().position.x + SCENE_WIDTH*0.5f,
	                                      viewport.getCamera().position.y + SCENE_HEIGHT*0.5f,
										  0);
	    viewport.getCamera().update();		
		hudViewport = new FitViewport(SCENE_WIDTH * PIXELS_PER_METER, SCENE_HEIGHT * PIXELS_PER_METER, new OrthographicCamera());    
		batch = new SpriteBatch();
	    Gdx.input.setInputProcessor(this);
	    // Create Physics World
	    world = new World(new Vector2(0,-9.8f), true);
	    // Instantiate the class in charge of drawing physics shapes
	    debugRenderer = new Box2DDebugRenderer();
		
		rayHandler = new RayHandler(world);
		rayHandler.setAmbientLight(0.2f, 0.2f, 0.2f, 0.25f);
		light = new PointLight(rayHandler, 32);
		light.setActive(false);
		light.setColor(Color.PURPLE);
		light.setDistance(1.5f);
		lightButton = createButton((int) SCENE_WIDTH / 2, (int) SCENE_HEIGHT / 2, 80, 80);
	}
	
	private Sprite createButton(int xPos, int yPos, int buttonWidth, int buttonHeight) {
		Pixmap button = new Pixmap(buttonWidth, buttonHeight,Pixmap.Format.RGBA8888);
		button.setColor(0, 200, 0, 127);
		button.fillRectangle(0, 0, buttonWidth, buttonHeight);
		button.setColor(225, 0, 0 , 164);
		button.fillRectangle(6, 6, buttonWidth - 12, buttonHeight - 12);
		button.setColor(Color.BROWN);
		button.drawRectangle(0, 0, buttonWidth, buttonHeight);
		Sprite buttonSprite = new Sprite(new Texture(button));
		buttonSprite.setPosition(xPos, yPos);
		buttonSprite.setSize(buttonWidth / PIXELS_PER_METER, buttonHeight / PIXELS_PER_METER);
		buttonLight = ButtonState.LIGHT_OFF;
		return buttonSprite;
	}
		
	@Override
	public boolean touchDown (int screenX, int screenY, int pointer, int button) {
		if (button == Input.Buttons.LEFT) {
			point.set(screenX, screenY, 0);
			viewport.getCamera().unproject(point);
			if (lightButton.getBoundingRectangle().contains(point.x, point.y)) {
 			    light.setPosition(SCENE_WIDTH / 2, SCENE_HEIGHT / 2 - 0.2f);
				if (light.isActive()) { 
					light.setActive(false);
				} else {
					light.setActive(true);
				}
			    return true;
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
 		rayHandler.setCombinedMatrix(viewport.getCamera().combined);
		rayHandler.updateAndRender();
		debugRenderer.render(world, viewport.getCamera().combined);	
		String message = "lightButton.getBoundingRectangle()=" + lightButton.getBoundingRectangle();
		batch.setProjectionMatrix(viewport.getCamera().combined);
		batch.begin();
		lightButton.draw(batch); 
		batch.end();
		batch.setProjectionMatrix(hudViewport.getCamera().combined);
	    batch.begin();
		font.draw(batch, message, 10, 100);
		message = "point="+point;
		font.draw(batch, message, 10, 180);
		batch.end();
	}
}
