package com.ellzone.slotpuzzle;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public interface SlotPuzzleGameInterface {
    Screen getWorldScreen();

    void setScreen(Screen screen);

    Screen getScreen();

    SpriteBatch getBatch();

    AnnotationAssetManager getAnnotationAssetManager();

    TweenManager getTweenManager();
}
