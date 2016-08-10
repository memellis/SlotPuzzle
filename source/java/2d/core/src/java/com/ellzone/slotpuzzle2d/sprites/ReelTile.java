package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

public class ReelTile extends ReelSprite {
    private Texture texture;
    private TextureRegion region;
    private int width;
    private int height;
    private float x;
    private float y;
    private float sx = 0;
    private float sy = 0;
    private float frameRate;
	private boolean deleteReelTile;

    public ReelTile(Texture texture, int width, int height, float x, float y, int endReel, float frameRate) {
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        super.setEndReel(endReel);
        this.frameRate = frameRate;
        defineReelSlotTileScroll();
    }

    private void defineReelSlotTileScroll() {
        setPosition(this.x, this.y);
        setOrigin(this.x, this.y);
        setBounds(this.x, this.y, texture.getWidth(), 32);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        region = new TextureRegion(texture);
        region.setRegion(0, 0, texture.getWidth(), 32);
        setRegion(region);
        deleteReelTile = false;
    }

	@Override
    public void update(float dt) {
        if(super.isSpinning()) {
            float syModulus = sy % texture.getHeight();
            region.setRegion((int) sx, (int) syModulus, texture.getWidth(), 32);
            setRegion(region);
        }
    }

    public float getSx() {
        return this.sx;
    }

    public float getSy() {
        return this.sy;
    }

    public void setSx(float sx) {
        this.sx = sx;
    }

    public void setSy(float sy) {
        this.sy = sy;
    }

    public void setEndReel() {
        float syModulus = sy % texture.getHeight();
        super.setEndReel((int) syModulus / 32);
    }

	@Override
	public void dispose() {
		
	}

    private void savePixmap(Pixmap pixmap, String pixmapFileName) {
        FileHandle pixmapFile = Gdx.files.local(pixmapFileName);
        if (pixmapFile.exists()) {
            pixmapFile.delete();
        }
        PixmapProcessors.savePixmap(pixmap, pixmapFile.file());
    }

	public boolean isReelTileDeleted() {
		return deleteReelTile;
	}
	
	public void deleteReelTile() {
		deleteReelTile = true;
	}

}
