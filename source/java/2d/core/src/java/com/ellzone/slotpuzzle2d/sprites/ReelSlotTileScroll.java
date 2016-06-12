package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

public class ReelSlotTileScroll extends Sprite {
    private Texture texture;
    private TextureRegion region;
    private int width;
    private int height;
    private float x;
    private float y;
    private float sx = 0;
    private float sy = 0;
    private int endReel;
    private float frameRate;
    private boolean spinning;
    private boolean flashing;

    public ReelSlotTileScroll(Texture texture, int width, int height, float x, float y, int endReel, float frameRate) {
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.endReel = endReel;
        this.frameRate = frameRate;
        defineReelSlotTileScroll();
    }

    private void defineReelSlotTileScroll() {
        this.spinning = true;
        this.flashing = false;
        setPosition(this.x, this.y);
        setOrigin(this.x, this.y);
        setBounds(this.x, this.y, texture.getWidth(), 32);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        region = new TextureRegion(texture);
        region.setRegion(0, 0, texture.getWidth(), 32);
        setRegion(region);
    }

    public void update(float dt) {
        if(spinning) {
            float syModulus = sy % texture.getHeight();
            region.setRegion((int) sx, (int) syModulus, texture.getWidth(), 32);
            setRegion(region);
        }
    }

    public int getEndReel() {
        return this.endReel;
    }

    public void setEndReel(int endReel) {
        this.endReel = endReel;
    }

    public boolean isSpinning() {
        return this.spinning;
    }

    public boolean isFlashing() {
        return this.flashing;
    }

    public void setFlashing(boolean flashing) {
        this.flashing = flashing;
    }

    public float getSX() {
        return this.sx;
    }

    public float getSY() {
        return this.sy;
    }

    public void setSX(float sx) {
        this.sx = sx;
    }

    public void setSY(float sy) {
        this.sy = sy;
    }

    public void setEndReel() {
        float syModulus = sy % texture.getHeight();
        this.endReel = (int) syModulus / 32;
    }

    private void savePixmap(Pixmap pixmap, String pixmapFileName) {
        FileHandle pixmapFile = Gdx.files.local(pixmapFileName);
        if (pixmapFile.exists()) {
            pixmapFile.delete();
        }
        PixmapProcessors.savePixmap(pixmap, pixmapFile.file());
    }
}
