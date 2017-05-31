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

import java.util.Arrays;
import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;

public class Bezier3 extends SPPrototypeTemplate {
	
    public class MyInputProcessor implements InputProcessor {
        @Override
        public boolean keyDown(int keycode){
            if (keycode == Input.Keys.S) {
                saveControlPoints();
            }
            if (keycode == Input.Keys.L) {
                loadControlPoints();
            }
            return false;
        }

        @Override
        public boolean keyUp(int keycode){
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if (button == 1) {
                addControlPoint(screenX, screenY);
            }
            return true;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            dragHit = false;
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            dragControlPoint(screenX, screenY);
            return true;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(int amount) {
            return false;
        }
    }

    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private Random random;
    private ReelTile reelTile;
    private Array<ReelTile> reelTiles;
    private ShapeRenderer shapeRenderer;
    private Array<Vector2> points = new Array<Vector2>();
    private Array<Vector2> reelSpinBezier = new Array<Vector2>();
    private int graphStep;
    private int graphSize = 384;
    private Vector2[] reelSpinPath;
    private MyInputProcessor inputProcessor;
    private boolean dragHit;
    private int dragControlPoint;
    CatmullRomSpline<Vector2> myCatmull;

	@Override
	protected void initialiseOverride() {
        initialiseReelSlots();
        initialiseBezier();
        shapeRenderer = new ShapeRenderer();
        inputProcessor = new MyInputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);
        dragHit = false;
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
        for (ReelTile reelSlot : reelTiles) {
            reelSlot.draw(batch);
        }
        batch.end();
        if (graphStep < reelSpinBezier.size) {
            drawGraphPoint(shapeRenderer, new Vector2(reelSpinBezier.get(graphStep).x, reelSpinBezier.get(graphStep).y + Gdx.graphics.getHeight() / 4 % Gdx.graphics.getHeight()));
            graphStep++;
        } else {
            graphStep = 0;
        }
        drawControlPoints(shapeRenderer, reelSpinPath);
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
        reelSpinPath = new Vector2[13];
        int idx=0;
        reelSpinPath[idx++] = new Vector2(  0, +0);
        reelSpinPath[idx++] = new Vector2( 37, +256);
        reelSpinPath[idx++] = new Vector2 (78, +332);
        reelSpinPath[idx++] = new Vector2(117, +128);
        reelSpinPath[idx++] = new Vector2(156, -96);
        reelSpinPath[idx++] = new Vector2(195, +96);
        reelSpinPath[idx++] = new Vector2(234, -64);
        reelSpinPath[idx++] = new Vector2(273, +48);
        reelSpinPath[idx++] = new Vector2(312, -32);
        reelSpinPath[idx++] = new Vector2(351, +40);
        reelSpinPath[idx++] = new Vector2(390, -16);
        reelSpinPath[idx++] = new Vector2(429, +32);
        reelSpinPath[idx  ] = new Vector2(468, +32);
        renewSpline();
    }

    private void drawGraphPoint(ShapeRenderer shapeRenderer, Vector2 newPoint) {
        if (points.size >= 2) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(random.nextInt(255), random.nextInt(255), random.nextInt(255), 255);
            for (int i = 0; i < points.size - 1; i++) {
                shapeRenderer.line(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            }
            shapeRenderer.end();
        }
        points.add(newPoint);
    }

    private void drawControlPoints(ShapeRenderer shapeRenderer, Vector2[] reelSpinPath) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(random.nextInt(255), random.nextInt(255), random.nextInt(255), 255);
        for (int i=0; i<reelSpinPath.length; i++) {
            shapeRenderer.circle(reelSpinPath[i].x, reelSpinPath[i].y + Gdx.graphics.getHeight() / 4, 4);
        }
        shapeRenderer.end();
    }

    private int instersectControlPoint(Vector2 touchPoint, Vector2[] controlPoints) {
        for (int i=0; i<controlPoints.length; i++){
            float xD = touchPoint.x - controlPoints[i].x;
            float yD = touchPoint.y - (controlPoints[i].y + Gdx.graphics.getHeight() / 4);
            float sqDist = xD * xD + yD * yD;
            boolean collesion = sqDist <= (0.5+4) * (0.5+4);
            if (collesion) {
                return i;
            }
        }
        return -1;
    }

    private void addControlPoint(int x, int y) {
        Vector2 touchPoint = new Vector2(x, y);
		touchPoint = viewport.unproject(touchPoint);		
        reelSpinPath = Arrays.copyOf(reelSpinPath, reelSpinPath.length+1);
        reelSpinPath[reelSpinPath.length - 1] = touchPoint;
        renewSpline();
    }

    private void dragControlPoint(int x, int y) {
        Vector2 touchPoint = new Vector2(x, y);
		touchPoint = viewport.unproject(touchPoint);
        if(dragHit) {
            reelSpinPath[dragControlPoint].x = touchPoint.x;
            reelSpinPath[dragControlPoint].y = touchPoint.y - Gdx.graphics.getHeight() / 4;
            renewSpline();
        } else {
            dragControlPoint = instersectControlPoint(touchPoint, reelSpinPath);
            if (dragControlPoint >= 0) {
                dragHit = true;
            }
        }
    }

    private void renewSpline() {
        myCatmull = new CatmullRomSpline<Vector2>(reelSpinPath, false);
        reelSpinBezier.clear();
        for(int i = 0; i < graphSize; i++) {
            reelSpinBezier.add(new Vector2());
            myCatmull.valueAt(reelSpinBezier.get(i), ((float)i)/((float)graphSize-1));
        }
    }

    private void saveControlPoints() {
        FileHandle controlPointsFile = Gdx.files.local("controlPointsFile.txt");
        for (int i=0; i<reelSpinPath.length; i++) {
            controlPointsFile.writeString(reelSpinPath[i].toString() + "\n", true);
        }
    }

    private void loadControlPoints() {
        FileHandle controlPointsFile = Gdx.files.local("controlPointsFile.txt");
        if (controlPointsFile.exists()) {
            String cpText = controlPointsFile.readString();
            String controlPoints[] = cpText.split("\\r?\\n");
            Vector2[] tempControlPoints = new Vector2[controlPoints.length];
            int i = 0;
            for (String controlPoint : controlPoints) {
                String cpFloats[] = controlPoint.split(",");
                cpFloats[0] = cpFloats[0].substring(1,cpFloats[0].length());
                cpFloats[1] = cpFloats[1].substring(0,cpFloats[1].length()-1);
                float x = Float.parseFloat(cpFloats[0]);
                float y = Float.parseFloat(cpFloats[1]);
                tempControlPoints[i++] = new Vector2(x, y);
            }
            reelSpinPath = tempControlPoints;
        }
        renewSpline();
    }
}
