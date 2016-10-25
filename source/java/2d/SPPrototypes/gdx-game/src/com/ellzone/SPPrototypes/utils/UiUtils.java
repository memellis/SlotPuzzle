package com.ellzone.SPPrototypes.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.ellzone.SPPrototypes.SPPrototypes;
import com.ellzone.SPPrototypes.utils.UiUtils;

public class UiUtils {
	public static Skin createBasicSkin(Skin skin) {
		//Create a font
		BitmapFont font = new BitmapFont();
		skin.add("default", font);

		//Create a texture
		Pixmap pixmap = new Pixmap((int)SPPrototypes.V_WIDTH/4,(int)SPPrototypes.V_HEIGHT/10, Pixmap.Format.RGB888);
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
