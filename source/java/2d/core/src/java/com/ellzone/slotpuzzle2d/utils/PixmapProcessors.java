/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ellzone.slotpuzzle2d.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Gdx2DPixmap;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import java.io.File;

import static com.badlogic.gdx.Application.ApplicationType.Android;

public class PixmapProcessors {
	private static int counter = 0;

	public static boolean arePixmapsEqual(Pixmap pixmap1, Pixmap pixmap2) {
		final int width1 = pixmap1.getWidth();
		final int height1 = pixmap1.getHeight();
		final int width2 = pixmap2.getWidth();
		final int height2 = pixmap2.getHeight();

		if ((width1 == width2) & (height1 == height2)) {
			for (int x = 0; x < width1; x++) {
				for (int y = 0; y < height1; y++) {
					if (pixmap1.getPixel(x, y) != pixmap2.getPixel(x, y)){
						return false;
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	public static Pixmap rotatePixmap(Pixmap src, float angle) {
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

	public static Pixmap flipPixmapX(Pixmap src) {
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

    public static Pixmap flipPixmapY(Pixmap src) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        Pixmap flipped = new Pixmap(width, height, src.getFormat());

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                flipped.drawPixel(x, y, src.getPixel(x, height - y - 1));
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

	public static void changePixmapColour(Pixmap src, Pixmap dest, Color srcColor, Color destColor) {
		for (int x = 0; x < src.getWidth(); x++) {
			System.out.print("x="+x+" ");
			for (int y = 0; y < src.getHeight(); y++) {
                int srcPixel = src.getPixel(x, y);

				System.out.print(String.format(" 0x%08X", srcPixel));
                Color mySrcColor = new Color(srcPixel);
				if (!((mySrcColor.r == 0) & (mySrcColor.g == 0) & (mySrcColor.g == 0) & (mySrcColor.a == 0))) {
					dest.drawPixel(x, y, destColor.rgba8888(destColor));
				}
			}

			System.out.println();
		}
	}

	public static Pixmap createDynamicVerticalFontText(BitmapFont font, String text, Pixmap src) {
		final int width = src.getWidth();
	    final int height = src.getHeight();

	    Pixmap verticalFontText = new Pixmap(width, height, src.getFormat());
	    BitmapFont.BitmapFontData fontData = font.getData();

		if (fontData.imagePaths.length == 0) {
			System.out.println("Doh! The length of the imagepaths is zero");
		} else {
			Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, fontData.getImagePath(0));
			Pixmap fontPixmap = new Pixmap(Gdx.files.local(fontData.getImagePath(0)));
			BitmapFont.Glyph glyph;
			verticalFontText.setColor(Color.BLACK);
			verticalFontText.fillRectangle(0, 0, width, height);
			verticalFontText.setColor(Color.WHITE);

			for (int i = 0; i < text.length(); i++) {
				glyph = fontData.getGlyph(text.charAt(i));				
				int offSetX = (verticalFontText.getWidth() - glyph.width) / 2;
				int startY = i * verticalFontText.getHeight() / text.length() + 2;
				int offSetY = -glyph.yoffset;
				offSetY = startY + offSetY - glyph.height;

 				verticalFontText.drawPixmap(fontPixmap,
						offSetX,
						offSetY,
						glyph.srcX, glyph.srcY, glyph.width, glyph.height);
 			}
		}
		return verticalFontText;
	}

	public static Pixmap createDynamicScrollAnimatedVerticalText(Pixmap textToAnimate, int textHeight, String text, int fontSize, int scrollStep) {
		int xFactor = textToAnimate.getWidth() / scrollStep;
		Pixmap scrollAnimatedVerticalText = new Pixmap(textToAnimate.getWidth() * text.length() * xFactor, textToAnimate.getHeight(), textToAnimate.getFormat());
		PixmapProcessors.copyPixmapVertically(textToAnimate, scrollAnimatedVerticalText, 0);

		Pixmap scrolledText = new Pixmap(textToAnimate.getWidth(), textToAnimate.getHeight(), textToAnimate.getFormat());
		PixmapProcessors.copyPixmapVertically(textToAnimate, scrolledText, 0);

		for (int i = 0; i < text.length() * xFactor; i++) {
			scrolledText = PixmapProcessors.scrollPixmapWrap(scrolledText, scrollStep);
			PixmapProcessors.copyPixmapVertically(scrolledText, scrollAnimatedVerticalText, scrolledText.getWidth() * (i + 1));
		}
		return scrollAnimatedVerticalText;
	}

	public static Pixmap createDynamicHorizontalFontText(BitmapFont font, String text, Pixmap src) {
		final int width = src.getWidth();
	    final int height = src.getHeight();

	    Pixmap horizontalFontText = new Pixmap(width, height, src.getFormat());
	    BitmapFont.BitmapFontData fontData = font.getData();

		if (fontData.imagePaths.length == 0) {
			System.out.println("Doh! The length of the imagepaths is zero");
		} else {
			Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, fontData.getImagePath(0));
			Pixmap fontPixmap = new Pixmap(Gdx.files.local(fontData.getImagePath(0)));
			BitmapFont.Glyph glyph;
			horizontalFontText.setColor(Color.BLACK);
			horizontalFontText.fillRectangle(0, 0, width, height);
			horizontalFontText.setColor(Color.WHITE);

			for (int i = 0; i < text.length(); i++) {
				glyph = fontData.getGlyph(text.charAt(i));				
				int startY = 20;
				int startX = i * horizontalFontText.getWidth() / text.length() + 2;
				int offSetY = -glyph.xoffset;
				offSetY = startY + offSetY - glyph.height;
				int offSetX = startX;

 				horizontalFontText.drawPixmap(fontPixmap,
											  offSetX,
											  offSetY,
											  glyph.srcX, glyph.srcY, glyph.width, glyph.height);
 			}

		}
		return horizontalFontText;
	}

	public static Pixmap createDynamicHorizontalFontTextColor(BitmapFont font, Color fontColor, String text, Pixmap src, int startTextX, int startTextY) {
	    BitmapFont.BitmapFontData fontData = font.getData();

		if (fontData.imagePaths.length == 0) {
			System.out.println("Doh! The length of the imagepaths is zero");
		} else {
			Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, fontData.getImagePath(0));
			Pixmap fontPixmap = new Pixmap(Gdx.files.local(fontData.getImagePath(0)));
			BitmapFont.Glyph glyph;

			for (int i = 0; i < text.length(); i++) {
				glyph = fontData.getGlyph(text.charAt(i));
				int startX = startTextX + i * src.getWidth() / text.length() + 2;
				int offSetY = -glyph.xoffset;
				offSetY = startTextY + offSetY - glyph.height;
				int offSetX = startX;

                Pixmap fontGlyphPixmap = new Pixmap(glyph.width, glyph.height, fontPixmap.getFormat());
                Pixmap fontPixmapModifiedFontColor = new Pixmap(glyph.width, glyph.height, fontPixmap.getFormat());

                fontGlyphPixmap.drawPixmap(fontPixmap,
                        0,
                        0,
                        glyph.srcX, glyph.srcY, glyph.width, glyph.height);

                changePixmapColour(fontGlyphPixmap, fontPixmapModifiedFontColor, Color.WHITE, fontColor);

				src.drawPixmap(fontPixmapModifiedFontColor,
						offSetX,
						offSetY,
						0, 0, glyph.width, glyph.height);
			}
		}
		return src;
	}

	public static Pixmap createDynamicHorizontalFontTextViaFrameBuffer(BitmapFont font, Color fontColor, String text, Pixmap src, int startTextX, int startTextY) {
        SpriteBatch spriteBatch = new SpriteBatch();
        FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
        frameBuffer.begin();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        font.setColor(fontColor);
        spriteBatch.begin();
        spriteBatch.draw(new Texture(src), 0, 0);
        font.draw(spriteBatch, text, startTextX, startTextY);
        spriteBatch.end();
        Pixmap destPixmap= ScreenshotFactory.getScreenshot(0, 0, src.getWidth(), src.getHeight(), true);
		frameBuffer.end();
		
		return destPixmap;
	}

	public static Pixmap createPixmapToAnimate(Sprite[] sprites) {
        Pixmap pixmap = getPixmapFromSprite(sprites[0]);
        Pixmap pixmapToAnimate = new Pixmap(pixmap.getWidth(), pixmap.getHeight() * sprites.length, pixmap.getFormat());

        for (int i = 0; i < sprites.length; i++) {
            pixmap = getPixmapFromSprite(sprites[i]);
            PixmapProcessors.copyPixmapHorizontally(pixmap, pixmapToAnimate, (int) i * pixmap.getHeight());
        }

        Pixmap scrolledPixmap = new Pixmap(pixmapToAnimate.getWidth(), pixmapToAnimate.getHeight(), pixmapToAnimate.getFormat());
        PixmapProcessors.copyPixmapVertically(pixmapToAnimate, scrolledPixmap, 0);

        return pixmapToAnimate;
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

		FileHandle pixmapFile = new FileHandle(Gdx.files.getLocalStoragePath() + "pixmapToAnimate.png");
		if (pixmapFile.exists()) {
			pixmapFile.delete();
		}
		savePixmap(pixmapToAnimate, pixmapFile.file());

		Pixmap scrollAnimatedVerticalPixmap = new Pixmap(pixmap.getWidth() * sprites.length * scrollStep, sprites.length * pixmap.getHeight(), pixmap.getFormat());
		PixmapProcessors.copyPixmapVertically(pixmapToAnimate, scrollAnimatedVerticalPixmap, 0);

		for (int i = 0; i < sprites.length * scrollStep ; i++) {
			scrolledPixmap = PixmapProcessors.scrollPixmapWrap(scrolledPixmap, scrollStep);
			PixmapProcessors.copyPixmapVertically(scrolledPixmap, scrollAnimatedVerticalPixmap, scrolledPixmap.getWidth() * (i + 1));
		}
		return scrollAnimatedVerticalPixmap;
	}

	public static void savePixmap(Pixmap pixmap) {
	    try {
                FileHandle fh;
                do {
                    if (Gdx.app.getType() == Android) {
                        fh = new FileHandle("/sdcard/AppProjects/" + "pixmap" + counter++ + ".png");
                    } else {
                        fh = new FileHandle(Gdx.files.getLocalStoragePath() + "pixmap" + counter++ + ".png");
                    }
                } while (fh.exists());
                PixmapIO.writePNG(fh, pixmap);
            } catch (Exception e){
        	Gdx.app.error(SlotPuzzleConstants.SLOT_PUZZLE, "Could not save pixmap to PNG file " + e.getMessage());
            }
	}

	public static void savePixmap(Pixmap pixmap, File file) {
	    if (file.exists()) {
		    file.delete();
	    }
	    try {
		FileHandle fh;
                do {
                    fh = new FileHandle(file);
                } while (fh.exists());
                PixmapIO.writePNG(fh, pixmap);
            } catch (Exception e){
        	Gdx.app.error(SlotPuzzleConstants.SLOT_PUZZLE, "Could not save pixmap to PNG file " + e.getMessage());
            }
	}

    public static void savePixmap(Pixmap pixmap, String pixmapFileName) {
        FileHandle pixmapFile = Gdx.files.local(pixmapFileName);
        if (pixmapFile.exists()) {
            pixmapFile.delete();
        }
        PixmapProcessors.savePixmap(pixmap, pixmapFile.file());
    }

	public static void saveTextureRegion(TextureRegion textureRegion) {
		savePixmap(getPixmapFromTextureRegion(textureRegion));		
	}

	public static void saveTextureRegion(TextureRegion textureRegion, File file) {
		savePixmap(getPixmapFromTextureRegion(textureRegion), file);		
	}

	public static Pixmap getPixmapFromTextureRegion(TextureRegion textureRegion) {
		Texture texture = textureRegion.getTexture();
		if (!texture.getTextureData().isPrepared()) {
		    texture.getTextureData().prepare();
		}
		Pixmap pixmap = texture.getTextureData().consumePixmap();
		Pixmap destinationPixmap = new Pixmap(textureRegion.getRegionWidth(), textureRegion.getRegionHeight(), pixmap.getFormat());

        for (int x = 0; x < textureRegion.getRegionWidth(); x++) {
		    for (int y = 0; y < textureRegion.getRegionHeight(); y++) {
                int regionWidthTextureWidthDifference = textureRegion.getRegionX() + x - pixmap.getWidth();
                if (regionWidthTextureWidthDifference > 0) {
                    destinationPixmap.drawPixel(x, y, pixmap.getPixel(regionWidthTextureWidthDifference, textureRegion.getRegionY() + y));
                } else {
                    destinationPixmap.drawPixel(x, y, pixmap.getPixel(textureRegion.getRegionX() + x, textureRegion.getRegionY() + y));
                }
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

	public static void writeTextToPixmap(Pixmap destPixmap, Pixmap fontPixmap, BitmapFont.BitmapFontData fontData, int startX, int startY, String text) {
	    int cursor = startX;

		destPixmap.setColor(Color.BLACK);
		destPixmap.fillRectangle(0, 0, destPixmap.getWidth(), destPixmap.getHeight());
		destPixmap.setColor(Color.WHITE);

	    char[] chars = text.toCharArray();
	    for(int i = 0; i < chars.length; i++) {
	        BitmapFont.Glyph glyph = fontData.getGlyph(chars[i]);
	        destPixmap.drawPixmap(fontPixmap, glyph.srcX, glyph.srcY, glyph.width, glyph.height, cursor + glyph.xoffset, startY + glyph.yoffset, glyph.width, glyph.height);
	        cursor += glyph.xadvance;
	    }
	}
	
	public static Texture textureFromPixmap(Gdx2DPixmap pixmap) {
        Texture texture = new Texture(pixmap.getWidth(), pixmap.getHeight(), Format.RGB565);
        texture.bind();
        Gdx.gl.glTexImage2D(GL20.GL_TEXTURE_2D,
		                    0,
							pixmap.getGLInternalFormat(),
							pixmap.getWidth(),
							pixmap.getHeight(),
							0,
							pixmap.getGLFormat(),
							pixmap.getGLType(),
							pixmap.getPixels());
        return texture;
    }
}

