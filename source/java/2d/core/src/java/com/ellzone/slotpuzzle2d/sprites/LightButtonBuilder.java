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

package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.World;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import box2dLight.PointLight;
import box2dLight.RayHandler;

public class LightButtonBuilder {
    private World world;
    private RayHandler rayHandler;
    private BitmapFont buttonFont;
    private Color buttonFontColor;
    private Color buttonEdgeColor;
    private Color buttonBackgroundColor;
    private Color buttonForegroundColor;
    private Color buttonTransparentColor;
    private float buttonPositionX;
    private float buttonPositionY;
    private int buttonWidth;
    private int buttonHeight;
    private String buttonText;
    private int startButtonTextX;
    private int startButtonTextY;
    private Color buttonLightColor;
    private PointLight light;
    private float buttonLightDistance;
    private Sprite lightButtonSprite;

    public static class Builder {
        private World world;
        private RayHandler rayHandler;
        private BitmapFont buttonFont;
        private Color buttonFontColor;
        private Color buttonEdgeColor;
        private Color buttonBackgroundColor;
        private Color buttonForegroundColor;
        private Color buttonTransparentColor;
        private float buttonPositionX;
        private float buttonPositionY;
        private int buttonWidth;
        private int buttonHeight;
        private String buttonText;
        private int startButtonTextX;
        private int startButtonTextY;
        private Color buttonLightColor;
        private float buttonLightDistance;

        public Builder world(World world) {
            this.world = world;
            return this;
        }

        public Builder rayHandler(RayHandler rayHandler) {
            this.rayHandler = rayHandler;
            return this;
        }

        public Builder buttonFont(BitmapFont buttonFont) {
            this.buttonFont = buttonFont;
            return this;
        }

        public Builder buttonFontColor(Color buttonFontColor){
            this.buttonFontColor = buttonFontColor;
            return this;
        }

        public Builder buttonEdgeColor(Color buttonEdgeColor) {
            this.buttonEdgeColor = buttonEdgeColor;
            return this;
        }

        public Builder buttonBackground(Color buttonBackgroundColor) {
            this.buttonBackgroundColor = buttonBackgroundColor;
            return this;
        }

        public Builder buttonForeground(Color buttonForegroundColor){
            this.buttonForegroundColor = buttonForegroundColor;
            return this;
        }

        public Builder buttontTransparentColor(Color buttonTransparentColor) {
            this.buttonTransparentColor = buttonTransparentColor;
            return this;
        }

        public Builder buttonPositionX(float buttonPositionX) {
            this.buttonPositionX = buttonPositionX;
            return this;
        }

        public Builder buttonPositionY(float buttonPositionY) {
            this.buttonPositionY = buttonPositionY;
            return this;
        }

        public Builder buttonWidth(int buttonWidth) {
            this.buttonWidth = buttonWidth;
            return this;
        }

        public Builder buttonHeight(int buttonHeight) {
            this.buttonHeight = buttonHeight;
            return this;
        }

        public Builder buttonText(String buttonText) {
            this.buttonText = buttonText;
            return this;
        }

        public Builder startButtonTextX(int startButtonTextX) {
            this.startButtonTextX = startButtonTextX;
            return this;
        }

        public Builder startButtonTextY(int startButtonTextY) {
            this.startButtonTextY = startButtonTextY;
            return this;
        }

        public Builder buttonLightColor(Color buttonLightColor) {
            this.buttonLightColor = buttonLightColor;
            return this;
        }

        public Builder buttonLightDistance(float buttonLightDistance) {
            this.buttonLightDistance = buttonLightDistance;
            return this;
        }

        public LightButtonBuilder build() {
            if (this.world == null ||
                this.rayHandler == null) {
                throw new IllegalStateException("Not all required values given");
            }
            return new LightButtonBuilder(this);
        }
    }

    private LightButtonBuilder(Builder builder) {
        this.world = builder.world;
        this.rayHandler = builder.rayHandler;
        this.buttonFont = builder.buttonFont;
        this.buttonFontColor = builder.buttonFontColor;
        this.buttonEdgeColor = builder.buttonEdgeColor;
        this.buttonBackgroundColor = builder.buttonBackgroundColor;
        this.buttonForegroundColor = builder.buttonForegroundColor;
        this.buttonTransparentColor = builder.buttonTransparentColor;
        this.buttonPositionX = builder.buttonPositionX;
        this.buttonPositionY = builder.buttonPositionY;
        this.buttonWidth = builder.buttonWidth;
        this.buttonHeight = builder.buttonHeight;
        this.buttonText = builder.buttonText;
        this.startButtonTextX = builder.startButtonTextX;
        this.startButtonTextY = builder.startButtonTextY;
        this.buttonLightColor = builder.buttonLightColor;
        this.buttonLightDistance = builder.buttonLightDistance;
        this.initialiseLightButton();
    }

    private void initialiseLightButton() {
        this.light = new PointLight(rayHandler, 32);
        this.light.setActive(false);
        this.light.setColor(this.buttonLightColor);
        this.light.setDistance(buttonLightDistance);
        float lightButtonCentreX = this.buttonPositionX + (float)buttonWidth / 200.0f;
        float lightButtonCentreY = this.buttonPositionY + (float)buttonHeight / 200.0f;
        light.setPosition(lightButtonCentreX, lightButtonCentreY);
        lightButtonSprite = new Sprite(createButton());
        lightButtonSprite.setPosition(this.buttonPositionX, this.buttonPositionY);
        lightButtonSprite.setSize(buttonWidth, buttonHeight);
    }

    private Texture createButton() {
        Pixmap button = new Pixmap(buttonWidth, buttonHeight, Pixmap.Format.RGBA8888);
        button.setColor(this.buttonBackgroundColor);
        button.fillRectangle(0, 0, buttonWidth, buttonHeight);
        button.setColor(this.buttonForegroundColor);
        button.fillRectangle(2, 2, buttonWidth - 4, buttonHeight - 4);
        button.setColor(this.buttonEdgeColor);
        button.drawRectangle(0, 0, buttonWidth, buttonHeight);
        button = PixmapProcessors.createDynamicHorizontalFontTextViaFrameBuffer(this.buttonFont, this.buttonFontColor, this.buttonText, button, this.startButtonTextX, this.startButtonTextY);
        button.setColor(this.buttonTransparentColor);
        button.fillRectangle(0, 0, buttonWidth, buttonHeight);
        return new Texture(button);
    }

    public PointLight getLight() {
        return this.light;
    }

    public Sprite getSprite() {
        return lightButtonSprite;
    }
}
