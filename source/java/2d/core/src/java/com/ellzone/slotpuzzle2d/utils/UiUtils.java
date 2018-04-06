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

package com.ellzone.slotpuzzle2d.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;

public class UiUtils {
    public static Skin createBasicSkin(Skin skin) {
    //Create a font
	BitmapFont font = new BitmapFont();
	skin.add("default", font);

	//Create a texture
	Pixmap pixmap = new Pixmap((int)SlotPuzzleConstants.V_WIDTH/4,(int)SlotPuzzleConstants.V_HEIGHT/10, Pixmap.Format.RGB888);
	pixmap.setColor(Color.WHITE);
	pixmap.fill();
	skin.add("background",new Texture(pixmap));

	//Create a button style
	TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
	textButtonStyle.up = skin.newDrawable("background", Color.RED);
	textButtonStyle.down = skin.newDrawable("background", Color.RED);
	textButtonStyle.checked = skin.newDrawable("background", Color.RED);
	textButtonStyle.over = skin.newDrawable("background", Color.PINK);
	textButtonStyle.font = skin.getFont("default");
	skin.add("default", textButtonStyle);
	return skin;
    }
}
