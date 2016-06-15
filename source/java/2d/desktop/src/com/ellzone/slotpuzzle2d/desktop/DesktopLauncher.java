package com.ellzone.slotpuzzle2d.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ellzone.slotpuzzle2d.SlotPuzzle;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.width = 800;
		config.height =480;
		new LwjglApplication(new SlotPuzzle(), config);
	}
}