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

package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class LightButton {
	private Sprite lightButtonSprite;
 	private World world;
	private RayHandler rayHandler;
	private PointLight light;
	private float positionX, positionY;
	private int buttonWidth, buttonHeight;
	private BitmapFont buttonFont;
	private String buttonText;
	
	public LightButton(World world, RayHandler rayHandler, float positionX, float positionY, int buttonWidth, int buttonHeight) {
		this.world = world;
		this.rayHandler = rayHandler;
		this.positionX = positionX;
		this.positionY = positionY;
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
		initialiseLightButton();
	}
	
	public LightButton(World world, RayHandler rayHandler, float positionX, float positionY, int buttonWidth, int buttonHeight, BitmapFont buttonFont, String buttonText) {
		this.world = world;
		this.rayHandler = rayHandler;
		this.positionX = positionX;
		this.positionY = positionY;
		this.buttonWidth = buttonWidth;
		this.buttonHeight = buttonHeight;
		this.buttonFont = buttonFont;
		this.buttonText = buttonText;
		initialiseLightButton();
	}
	
	private void initialiseLightButton() {
		light = new PointLight(rayHandler, 32);
		light.setActive(false);
		light.setColor(Color.PURPLE);
		light.setDistance(1.5f);
		float lightButtonCentreX = positionX + (float)buttonWidth / 200.0f;
		float lightButtonCentreY = positionY + (float)buttonHeight / 200.0f;
		light.setPosition(lightButtonCentreX, lightButtonCentreY);
		lightButtonSprite = new Sprite(createButton());
		lightButtonSprite.setPosition(positionX, positionY);
		lightButtonSprite.setSize(buttonWidth, buttonHeight);
	}
	
	private Texture createButton() {
		Pixmap button = new Pixmap(buttonWidth, buttonHeight, Pixmap.Format.RGBA8888);
		button.setColor(0, 200, 0, 127);
		button.fillRectangle(0, 0, buttonWidth, buttonHeight);
		button.setColor(225, 0, 0 , 164);
		button.fillRectangle(6, 6, buttonWidth - 12, buttonHeight - 12);
		button.setColor(Color.BROWN);
		button.drawRectangle(0, 0, buttonWidth, buttonHeight);
		button.setColor(Color.YELLOW);
		if (buttonText != null) {
		    button = createText(buttonFont, buttonText, button);
		}
		button.setColor(0, 200, 200, 0);
		button.fillRectangle(0, 0, buttonWidth, buttonHeight);
		return new Texture(button);
	}
	
	private Pixmap createText(BitmapFont font, String text, Pixmap pixmap) {
		return PixmapProcessors.createDynamicHorizontalFontTextCustom(font, Color.YELLOW, text, pixmap, 3, 45);
	}
	
	public PointLight getLight() {
		return this.light;
	}
	
	public Sprite getSprite() {
		return lightButtonSprite;
	}
}
