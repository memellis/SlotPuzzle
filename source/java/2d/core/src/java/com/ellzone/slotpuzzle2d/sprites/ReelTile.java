package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

public class ReelTile extends ReelSprite {
    private Texture texture;
    private TextureRegion region;
    private int spriteWidth;
    private int spriteHeight;
    private float x;
    private float y;
    private float sx = 0;
    private float sy = 0;
	private boolean deleteReelTile;
	private boolean reelFlash;
	private boolean reelFlashTween;
	public enum FlashState {FLASH_OFF, FLASH_ON};
	private FlashState reelFlashState;
	private float reelFlashTimer;
	private int reelFlashCount;
	private Color flashColor;
	private int score;

    public ReelTile(Texture texture, int spriteWidth, int spriteHeight, float x, float y, int endReel) {
        this.texture = texture;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.x = x;
        this.y = y;
        super.setEndReel(endReel);
        defineReelSlotTileScroll();
    }

    private void defineReelSlotTileScroll() {
        setPosition(this.x, this.y);
        setOrigin(this.x, this.y);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        region = new TextureRegion(texture);
        region.setRegion(0, 0, spriteWidth, spriteHeight);
        setBounds(this.x, this.y, spriteWidth, spriteHeight);
        setRegion(region);
		super.setSpinning(true);
		reelFlashTimer = 0.3f;
		reelFlashCount = 10;
		reelFlash = false;
		reelFlashTween = false;
		reelFlashState = FlashState.FLASH_OFF;
		flashColor = Color.RED;
        deleteReelTile = false;
    }

	@Override
    public void update(float delta) {
        if (super.isSpinning()) {
        	processSpinningState();
        }
        if (reelFlash) {
        	processFlashState(delta);
        }
        if (reelFlashTween) {
        	processFlashTweenState(delta);
        }
    }
	
	private void processSpinningState() {
        float syModulus = sy % texture.getHeight();
        region.setRegion((int) sx, (int) syModulus, spriteWidth, spriteHeight);
        setRegion(region);
 	}
	
	private void processFlashState(float delta) {
		reelFlashTimer -= delta;
		if (reelFlashTimer < 0) {
			reelFlashTimer = 0.3f;
			reelFlashCount--;
			if (reelFlashCount <= 0) {
				reelFlash = false;
				reelFlashState = FlashState.FLASH_OFF;
				processEvent(new ReelStoppedFlashingEvent());
			} else {
				if (reelFlashState == FlashState.FLASH_OFF) {
					reelFlashState = FlashState.FLASH_ON;
					TextureRegion flashReel = drawFlashOn(region);
					setRegion(flashReel);
				} else {
					reelFlashState = FlashState.FLASH_OFF;
					processSpinningState();
				}
			}
		}
	}
	
	private void processFlashTweenState(float delta) {
		setFlashOn();
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
        super.setEndReel((int) syModulus / spriteHeight);
    }

	public int getCurrentReel() {
        float syModulus = sy % texture.getHeight();
 		return (int) ((syModulus + (spriteHeight / 2)) % texture.getHeight()) / spriteHeight;
	}

	@Override
	public void dispose() {
	}

	public boolean isReelTileDeleted() {
		return this.deleteReelTile;
	}
	
	public void deleteReelTile() {
		this.deleteReelTile = true;
	}
	
	public void setSpinning() {
		super.setSpinning(true);
	}	

	public FlashState getFlashState() {
		return this.reelFlashState;
	}
	
	public boolean isFlashing() {
		return this.reelFlash;
	}
	
	public void setFlashMode(boolean reelFlash) {
		this.reelFlash = reelFlash;
	}
	
	public Color getFlashColor() {
		return this.flashColor;
	}
	
	public void setFlashColor(Color flashColor) {
		this.flashColor = flashColor;
	}
	
	public void setFlashTween(boolean reelFlashTween) {
		this.reelFlashTween = reelFlashTween;
	}
	
	public boolean getFlashTween() {
		return this.reelFlashTween;
	}
	
	public void setFlashOn() {
		TextureRegion flashReel = drawFlashOn(region);
		setRegion(flashReel);
	}
	
	public void setFlashOff() {
		setRegion(region);
	}
	
	private TextureRegion drawFlashOn(TextureRegion reel) {
		Pixmap reelPixmap = PixmapProcessors.getPixmapFromtextureRegion(reel);
		reelPixmap.setColor(flashColor);
		reelPixmap.drawRectangle(0, 0, spriteWidth    , spriteHeight);
		reelPixmap.drawRectangle(1, 1, spriteWidth - 2, spriteHeight - 2);
		reelPixmap.drawRectangle(2, 2, spriteWidth - 4, spriteHeight - 4);
		TextureRegion textureRegion = new TextureRegion(new Texture(reelPixmap));
		return textureRegion;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public TextureRegion getRegion() {
		return this.region;
	}
}