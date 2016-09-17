package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.InputProcessor;

public abstract class Level {

	public Level () {
	}
	
	public abstract void initialise();
	public abstract String getImageName();
	public abstract String getTitle();
	public abstract void dispose();
	public abstract InputProcessor getInput();
}
