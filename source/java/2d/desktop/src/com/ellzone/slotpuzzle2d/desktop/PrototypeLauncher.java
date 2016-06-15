package com.ellzone.slotpuzzle2d.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ellzone.slotpuzzle2d.graphs.DrawTweenGraphs;

public class PrototypeLauncher {
    public static void main (String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.width = 800;
        config.height =480;
        new LwjglApplication(new DrawTweenGraphs(), config);
    }
}
