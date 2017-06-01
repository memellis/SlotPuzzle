package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.InputProcessor;

public class MapLevel5 extends Level {
	@Override
	public void initialise() {
	}	

	@Override
	public String getImageName() {
		return "MapTile";
	}

	@Override
	public InputProcessor getInput() {
		return null;
	}

	@Override
	public String getTitle() {
		String title = "Level 5";
		return title;
	}
	
	public int getLevelNumber() {
		return 4;
	}

	@Override
	public void dispose() {			
	}
}
