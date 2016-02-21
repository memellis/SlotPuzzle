package com.ellzone.utils;

import com.badlogic.gdx.assets.AssetManager;

public class Assets extends AssetManager {
	private static Assets instance;
	public static Assets inst() {
		if (instance == null) instance = new Assets();
		return instance;
	}

	private Assets() {}

	@Override
	public synchronized void dispose() {
		super.dispose();
		instance = null;
	}
}
