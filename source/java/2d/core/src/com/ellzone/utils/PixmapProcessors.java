package com.ellzone.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.ellzone.slotpuzzle2d.SlotPuzzle;

public class PixmapProcessors {
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

}

