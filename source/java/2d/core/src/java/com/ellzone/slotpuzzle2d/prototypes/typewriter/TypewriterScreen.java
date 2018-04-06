/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 *   http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.prototypes.typewriter;

import com.badlogic.gdx.Screen;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import net.dermetfan.gdx.Typewriter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.math.Interpolation;

public class TypewriterScreen implements Screen {
	private SlotPuzzle game;
	private BitmapFont font;
	private String text = "This is a long multiline string. Some text that you can read through in a RPG, that scrolls like in the pokemon games or something"; 
	private Typewriter typewriter = new Typewriter(); 
	
	public TypewriterScreen(SlotPuzzle game) {
		this.game = game;
		defineTypeWriterScreen();
	}
	
	private void defineTypeWriterScreen() {
		font = new BitmapFont();
		typewriter.getInterpolator().setInterpolation(Interpolation.linear);

        // set some custom cursors
        typewriter.getAppender().set(new CharSequence[] {"", ".", "..", "..."}, 1.5f / 4f);
		typewriter.getAppender().set(new CharSequence[] {" ", "_"}, 1.5f / 4f);
	}
	
	@Override
	public void show() {
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT); 
		game.batch.begin();
		font.draw(game.batch,
		                 // update the time and get the interpolated CharSequence with cursor 
						 typewriter.updateAndType(text, delta),
						 80, 
						 120, 
						 400,
						 Align.right,
						 true);
		game.batch.end();
	}

	@Override
	public void resize(int p1, int p2) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		font.dispose();
	}	
}
