package com.ellzone.slotpuzzle2d;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.screens.SplashScreen;


public class SlotPuzzle extends Game
{
	public SpriteBatch batch;
	public final static String SLOT_PUZZLE = "Slot Puzzle";

	@Override
	public void create() {
		batch = new SpriteBatch();
		setScreen(new SplashScreen(this));
	}

	@Override
	public void render() {        
	    super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
		batch.dispose();
	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}
}
