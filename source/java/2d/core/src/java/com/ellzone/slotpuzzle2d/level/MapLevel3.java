package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.InputProcessor;

public class MapLevel3 extends Level {
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
		String title = "1-3";
		return title;
	}
	
	public int getLevelNumber() {
		return 2;
	}

	@Override
	public void dispose() {			
	}
}
