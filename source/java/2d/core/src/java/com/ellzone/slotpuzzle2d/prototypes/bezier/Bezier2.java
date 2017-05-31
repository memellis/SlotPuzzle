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

package com.ellzone.slotpuzzle2d.prototypes.bezier;

import java.util.Random;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;

public class Bezier2 extends SPPrototypeTemplate {


	
    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private ReelTile reelTile;
    private Array<ReelTile> reelTiles;
    private Random random;
    private ShapeRenderer shapeRenderer;
    private Array<Vector2> points = new Array<Vector2>();
    private Array<Vector2> reelSpinBezier = new Array<Vector2>();
    private int graphStep;
    private int graphSize = 512;
    private Vector2[] reelSpinPath;

	@Override
	protected void initialiseOverride() {
		initialiseReelSlots();
        initialiseBezier();
        shapeRenderer = new ShapeRenderer();
	}

	@Override
	protected void loadAssetsOverride() {
	}

	@Override
	protected void disposeOverride() {
	}

	@Override
	protected void updateOverride(float dt) {
        for(ReelTile reelTile : reelTiles) {
            if (graphStep < reelSpinBezier.size - (graphSize/reelSpinPath.length)) {
                reelTile.setSy(reelSpinBezier.get(graphStep).y);
            }
            reelTile.update(dt);
        }
	}

	@Override
	protected void renderOverride(float dt) {
        batch.begin();
        for (ReelTile reelTile : reelTiles) {
            reelTile.draw(batch);
        }
        batch.end();
        if (graphStep < reelSpinBezier.size - (graphSize/reelSpinPath.length)) {
            drawGraphPoint(shapeRenderer, new Vector2(graphStep % Gdx.graphics.getWidth(), reelSpinBezier.get(graphStep).y + Gdx.graphics.getHeight() / 4 % Gdx.graphics.getHeight()));
            graphStep++;
        } else {
            graphStep = 0;
        }	
	}

	@Override
	protected void initialiseUniversalTweenEngineOverride() {
	}
	
    private void initialiseReelSlots() {
        random = new Random();
        reelTiles = new Array<ReelTile>();
        slotReelScrollPixmap = new Pixmap(spriteWidth, spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        reelTile = new ReelTile(slotReelScrollTexture, 0, 32, spriteWidth, spriteHeight, 0, null);
        reelTile.setX(0);
        reelTile.setY(0);
        reelTile.setSx(0);
        reelTile.setEndReel(random.nextInt(sprites.length - 1));
        reelTile.setSy(0);
        reelTiles.add(reelTile);
    }

    private void initialiseBezier() {
        reelSpinPath = new Vector2[23];
        int idx=0;
        reelSpinPath[idx++] = new Vector2(0, 0);
        for (int l=0; l<10; l++) {
            reelSpinPath[idx++] = new Vector2(0, 512*l);
        }
        reelSpinPath[idx++] = new Vector2(0, 370);
        reelSpinPath[idx++] = new Vector2(0, -256);
        reelSpinPath[idx++] = new Vector2(0, +128);
        reelSpinPath[idx++] = new Vector2(0, -64);
        reelSpinPath[idx++] = new Vector2(0, +48);
        reelSpinPath[idx++] = new Vector2(0, -32);
        reelSpinPath[idx++] = new Vector2(0, +40);
        reelSpinPath[idx++] = new Vector2(0, -16);
        reelSpinPath[idx++] = new Vector2(0, +32);
        reelSpinPath[idx++] = new Vector2(0, +32);
        reelSpinPath[idx++] = new Vector2(0, +32);
        reelSpinPath[idx  ] = new Vector2(0, +32);

        CatmullRomSpline<Vector2> myCatmull = new CatmullRomSpline<Vector2>(reelSpinPath, true);
        for(int i = 0; i < graphSize; i++) {
            reelSpinBezier.add(new Vector2());
            myCatmull.valueAt(reelSpinBezier.get(i), ((float)i)/((float)graphSize-1));
        }
    }

    private void drawGraphPoint(ShapeRenderer shapeRenderer, Vector2 newPoint) {
        if (points.size >= 2) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0, 255, 255, 255);
            for (int i = 0; i < points.size - 1; i++) {
                shapeRenderer.line(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            }
            shapeRenderer.end();
        }
        points.add(newPoint);
    }
}
