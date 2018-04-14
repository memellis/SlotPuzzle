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

package com.ellzone.slotpuzzle2d.prototypes.assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.utils.Assets;

public class CreatePlayingCards extends SPPrototype {
	SpriteBatch spriteBatch;
	TextureAtlas atlas;
	Sprite front;
	Sprite back;

    @Override
    public void create() {
		spriteBatch = new SpriteBatch();
        loadAssets();
    }

    private void loadAssets() {
        Assets.inst().load("playingcards/carddeck.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();
        
        TextureAtlas cardAtlas = Assets.inst().get("playingcards/carddeck.atlas", TextureAtlas.class);

		front = cardAtlas.createSprite("clubs", 2);
		front.setPosition(100, 100);
		
		back = cardAtlas.createSprite("back", 3);
		back.setPosition(300, 100);    	
    }
    
	@Override
	public void dispose() {
		spriteBatch.dispose();
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		spriteBatch.begin();
		front.draw(spriteBatch);
		back.draw(spriteBatch);
		spriteBatch.end();
	}
}
