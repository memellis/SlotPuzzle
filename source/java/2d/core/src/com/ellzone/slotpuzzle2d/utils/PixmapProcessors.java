package com.ellzone.slotpuzzle2d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import java.io.File;

public class PixmapProcessors {
	private static int counter = 0;
	
	public static Pixmap rotatePixmap(Pixmap src, float angle){
	    final int width = src.getWidth();
	    final int height = src.getHeight();
	    Pixmap rotated = new Pixmap(width, height, src.getFormat());

	    final double radians = Math.toRadians(angle), cos = Math.cos(radians), sin = Math.sin(radians);     

	    for (int x = 0; x < width; x++) {
	        for (int y = 0; y < height; y++) {
	            final int
	            centerx = width/2, centery = height / 2,
	            m = x - centerx,
	            n = y - centery,
	            j = ((int) (m * cos + n * sin)) + centerx,
	            k = ((int) (n * cos - m * sin)) + centery;
	            if (j >= 0 && j < width && k >= 0 && k < height){
	                rotated.drawPixel(x, y, src.getPixel(k, j));
	            }
	        }
	    }
	    return rotated;
	}
	
	public static Pixmap flipPixmap(Pixmap src) {
	    final int width = src.getWidth();
	    final int height = src.getHeight();
	    Pixmap flipped = new Pixmap(width, height, src.getFormat());

	    for (int x = 0; x < width; x++) {
	        for (int y = 0; y < height; y++) {
	            flipped.drawPixel(x, y, src.getPixel(width - x - 1, y));
	        }
	    }
	    return flipped;
	}
	
	public static Pixmap scrollPixmap(Pixmap src, int scrollSize, boolean scrollInXDirection) {
		final int width = src.getWidth();
		final int height = src.getHeight();
		Pixmap scrolledPixmap = new Pixmap(width, height, src.getFormat());
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				if(scrollInXDirection) {
					scrolledPixmap.drawPixel(x, y, src.getPixel(x, y + scrollSize));
				} else {
					scrolledPixmap.drawPixel(x, y, src.getPixel(x + scrollSize, y));
				}
			}
		}
		return scrolledPixmap;
	}

	public static Pixmap scrollPixmapWrap(Pixmap src, int scrollSize) {
		final int width = src.getWidth();
		final int height = src.getHeight();
		Pixmap scrolledPixmap = new Pixmap(width, height, src.getFormat());
		
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				scrolledPixmap.drawPixel(x, y, src.getPixel(x, y + scrollSize));
			}
		}
		for (int x = 0; x < width; x++) {
			for (int y = height - scrollSize - 1 ; y < height; y++) {
				scrolledPixmap.drawPixel(x, y, src.getPixel(x, y - height + scrollSize +1));
			}
		}
		
		return scrolledPixmap;
	}
	
	public static void copyPixmapHorizontally(Pixmap src, Pixmap dest, int offset) {		
		for (int x = 0; x < src.getWidth(); x++) {
			for (int y = 0; y < src.getHeight(); y++) {
				dest.drawPixel(x, y + offset, src.getPixel(x, y));
			}
		}
	}

	public static void copyPixmapVertically(Pixmap src, Pixmap dest, int offset) {
		for (int x = 0; x < src.getWidth(); x++) {
			for (int y = 0; y < src.getHeight(); y++) {
				dest.drawPixel(x + offset, y, src.getPixel(x, y));
			}
		}
	}
	
	public static Pixmap createDynamicVerticalFontText(BitmapFont font, String text, Pixmap src) {
		final int width = src.getWidth();
	    final int height = src.getHeight();
	    
	    Pixmap verticalFontText = new Pixmap(width, height, src.getFormat());
	    BitmapFont.BitmapFontData fontData = font.getData();
	    if (fontData == null) {
	    	Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "fontData is null :(");
	    }

		if (fontData.imagePaths.length == 0) {
			System.out.println("Doh! The length of the imagepaths is zero");
		} else {
			Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, fontData.getImagePath(0));
			Pixmap fontPixmap = new Pixmap(Gdx.files.local(fontData.getImagePath(0)));
			BitmapFont.Glyph glyph;
			verticalFontText.setColor(Color.BLACK);
			verticalFontText.fillRectangle(0, 0, width, height);
			verticalFontText.setColor(Color.WHITE);

			for (int i = 0; i < text.length(); i++) {
				glyph = fontData.getGlyph(text.charAt(i));
				verticalFontText.drawPixmap(fontPixmap,
						(verticalFontText.getWidth() - glyph.width) / 2,
						(i * (int) (font.getLineHeight() - 7)),
						glyph.srcX, glyph.srcY, glyph.width, glyph.height);
			}
		}
		return verticalFontText;
	}

	public static Pixmap createDynamicScrollAnimatedVerticalText(Pixmap textToAnimate, int textHeight, String text, int fontSize, int scrollStep) {
		Pixmap scrollAnimatedVerticalText = new Pixmap(fontSize * (text.length() * 5 - 1), text.length() * textHeight, textToAnimate.getFormat());
			
		PixmapProcessors.copyPixmapVertically(textToAnimate, scrollAnimatedVerticalText, 0);

		Pixmap scrolledText = new Pixmap(textToAnimate.getWidth(), textToAnimate.getHeight(), textToAnimate.getFormat());
		PixmapProcessors.copyPixmapVertically(textToAnimate, scrolledText, 0);
		
		for (int i = 0; i < text.length() * 5; i++) {
			scrolledText = PixmapProcessors.scrollPixmapWrap(scrolledText, scrollStep);
			PixmapProcessors.copyPixmapVertically(scrolledText, scrollAnimatedVerticalText, scrolledText.getWidth() * (i + 1));
		}
		
		return scrollAnimatedVerticalText;
	}
	
	public static Pixmap createDynamicScrollAnimatedPixmap(Sprite[] sprites, int scrollStep) {
		Pixmap pixmap = getPixmapFromSprite(sprites[0]);
		Pixmap pixmapToAnimate = new Pixmap(pixmap.getWidth(), pixmap.getHeight() * sprites.length, pixmap.getFormat());

		for (int i = 0; i < sprites.length; i++) {
			pixmap = getPixmapFromSprite(sprites[i]);
			PixmapProcessors.copyPixmapHorizontally(pixmap, pixmapToAnimate, (int) i * pixmap.getHeight());
		}
		
		Pixmap scrolledPixmap = new Pixmap(pixmapToAnimate.getWidth(), pixmapToAnimate.getHeight(), pixmapToAnimate.getFormat());
		PixmapProcessors.copyPixmapVertically(pixmapToAnimate, scrolledPixmap, 0);
		
		Pixmap scrollAnimatedVerticalPixmap = new Pixmap(pixmap.getWidth() * sprites.length * scrollStep, sprites.length * pixmap.getHeight(), pixmap.getFormat());
		PixmapProcessors.copyPixmapVertically(pixmapToAnimate, scrollAnimatedVerticalPixmap, 0);

		for (int i = 0; i < sprites.length * scrollStep ; i++) {
			scrolledPixmap = PixmapProcessors.scrollPixmapWrap(scrolledPixmap, scrollStep);
			PixmapProcessors.copyPixmapVertically(scrolledPixmap, scrollAnimatedVerticalPixmap, scrolledPixmap.getWidth() * (i + 1));
		}		
		
		savePixmap(scrollAnimatedVerticalPixmap);
		
		return scrollAnimatedVerticalPixmap;
	}

	public static void savePixmap(Pixmap pixmap) {
		try {
            FileHandle fh;
            do {
                fh = new FileHandle(Gdx.files.getLocalStoragePath() + "pixmap" + counter++ + ".png");
            } while (fh.exists());
            PixmapIO.writePNG(fh, pixmap);
        } catch (Exception e){
        	Gdx.app.error(SlotPuzzle.SLOT_PUZZLE, "Could not save pixmap to PNG file " + e.getMessage());
        }
	}
	
	public static void savePixmap(Pixmap pixmap, File file) {
		try {
			FileHandle fh;
            do {
                fh = new FileHandle(file);
            } while (fh.exists());
            PixmapIO.writePNG(fh, pixmap);
        } catch (Exception e){
        	Gdx.app.error(SlotPuzzle.SLOT_PUZZLE, "Could not save pixmap to PNG file " + e.getMessage());
        }
	}
	
	public static void saveTextureRegion(TextureRegion textureRegion) {
		savePixmap(getPixmapFromtextureRegion(textureRegion));		
	}
	
	public static void saveTextureRegion(TextureRegion textureRegion, File file) {
		savePixmap(getPixmapFromtextureRegion(textureRegion), file);		
	}
	
	public static Pixmap getPixmapFromtextureRegion(TextureRegion textureRegion) {
		Texture texture = textureRegion.getTexture();
		if (!texture.getTextureData().isPrepared()) {
		    texture.getTextureData().prepare();
		}
		Pixmap pixmap = texture.getTextureData().consumePixmap();
		Pixmap destinationPixmap = new Pixmap(textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), pixmap.getFormat());
		for (int x = 0; x < textureRegion.getRegionWidth(); x++) {
		    for (int y = 0; y < textureRegion.getRegionHeight(); y++) {
		        destinationPixmap.drawPixel(x, y, pixmap.getPixel(textureRegion.getRegionX() + x, textureRegion.getRegionY() + y));
		    }
		}
		return destinationPixmap;
	}
	
	public static Pixmap getPixmapFromSprite(Sprite sprite) {
		Texture texture = sprite.getTexture();
		if (!texture.getTextureData().isPrepared()) {
		    texture.getTextureData().prepare();
		}
		Pixmap pixmap = texture.getTextureData().consumePixmap();
		Pixmap destinationPixmap = new Pixmap(sprite.getRegionWidth(), sprite.getRegionHeight(), pixmap.getFormat());
		for (int x = 0; x < sprite.getRegionWidth(); x++) {
		    for (int y = 0; y < sprite.getRegionHeight(); y++) {
		        destinationPixmap.drawPixel(x, y, pixmap.getPixel(sprite.getRegionX() + x, sprite.getRegionY() + y));
		    }
		}
		return destinationPixmap;
	}
}

