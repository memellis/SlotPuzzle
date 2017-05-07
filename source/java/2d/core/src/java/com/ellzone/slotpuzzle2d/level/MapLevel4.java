package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.InputProcessor;

public class MapLevel4 extends Level {
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
		String title = "Level 4";
		return title;
	}
	
	public int getLevelNumber() {
		return 3;
	}

	@Override
	public void dispose() {			
	}
}
