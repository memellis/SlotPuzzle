package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.ellzone.slotpuzzle2d.utils.Assets;

public class Reels {
    public final static String REEL_PACK_ATLAS = "reel/reels.pack.atlas";
    public final static String CHERRY = "cherry 40x40";
    public final static String CHEESECAKE = "cheesecake 40x40";
    public final static String GRAPES = "grapes 40x40";
    public final static String JELLY = "jelly 40x40";
    public final static String LEMON = "lemon 40x40";
    public final static String PEACH = "peach 40x40";
    public final static String PEAR = "pear 40x40";
    public final static String TOMATO = "tomato 40x40";
    private Sprite cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato;
    private Sprite[] reels;
    private int spriteWidth;
    private int spriteHeight;

    public Reels() {
        initialiseReels();
    }

    private void initialiseReels() {
        Assets.inst().load(REEL_PACK_ATLAS, TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();

        TextureAtlas atlas = Assets.inst().get("reel/reels.pack.atlas", TextureAtlas.class);
        cherry = atlas.createSprite(CHERRY);
        cheesecake = atlas.createSprite(CHEESECAKE);
        grapes = atlas.createSprite(GRAPES);
        jelly = atlas.createSprite(JELLY);
        lemon = atlas.createSprite(LEMON);
        peach = atlas.createSprite(PEACH);
        pear = atlas.createSprite(PEAR);
        tomato = atlas.createSprite(TOMATO);

        reels = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
        for (Sprite sprite : reels) {
            sprite.setOrigin(0, 0);
        }
        spriteWidth = (int) reels[0].getWidth();
        spriteHeight = (int) reels[0].getHeight();
    }

    public int getReelWidth() {
        return spriteWidth;
    }

    public int getReelHeight() {
        return spriteHeight;
    }

    public Sprite[] getReels() {
        return reels;
    }

    public void dispose() {
        Assets.inst().dispose();
        for (Sprite reel : reels) {
            reel.getTexture().dispose();
        }
    }
}
