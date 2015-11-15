package com.ellzone.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ellzone.SlotPuzzle;
import com.ellzone.test.TestAnimation;
import com.ellzone.test.TestG3dModelExporter;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//new LwjglApplication(new SlotPuzzle(), config);
		new LwjglApplication(new TestAnimation(), config);
	}
}
