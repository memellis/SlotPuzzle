/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

public class ReelSlotTileScroll3D extends Sprite {

    public final static float TILE_WIDTH = 1f;
    public final static float TILE_HEIGHT = TILE_WIDTH * 32f / 32f;

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
    public final float[] vertices;
    public final short[] indices;

    public final Matrix4 transform = new Matrix4();
    public final Vector3 position = new Vector3();
    public float angle;


    public ReelSlotTileScroll3D(Texture texture, int width, int height, float x, float y, int endReel, float frameRate, Sprite front, Sprite back) {
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.endReel = endReel;
        this.frameRate = frameRate;
        front.setSize(TILE_WIDTH, TILE_HEIGHT);
        back.setSize(TILE_WIDTH, TILE_HEIGHT);
        front.setPosition(-front.getWidth() * 0.5f, -front.getHeight() * 0.5f);
        back.setPosition(-back.getWidth() * 0.5f, -back.getHeight() * 0.5f);
        System.out.println("front.getWidth="+front.getWidth());
        System.out.println("front.getHeight="+front.getHeight());

        vertices = convert(front.getVertices(), back.getVertices());
        indices = new short[] {0, 1, 2, 2, 3, 0, 4, 5, 6, 6, 7, 4 };

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

    private static float[] convert(float[] front, float[] back) {
        return new float[] {
                front[Batch.X2], front[Batch.Y2], 0, 0, 0, 1, front[Batch.U2], front[Batch.V2],
                front[Batch.X1], front[Batch.Y1], 0, 0, 0, 1, front[Batch.U1], front[Batch.V1],
                front[Batch.X4], front[Batch.Y4], 0, 0, 0, 1, front[Batch.U4], front[Batch.V4],
                front[Batch.X3], front[Batch.Y3], 0, 0, 0, 1, front[Batch.U3], front[Batch.V3],

                back[Batch.X1], back[Batch.Y1], 0, 0, 0, -1, back[Batch.U1], back[Batch.V1],
                back[Batch.X2], back[Batch.Y2], 0, 0, 0, -1, back[Batch.U2], back[Batch.V2],
                back[Batch.X3], back[Batch.Y3], 0, 0, 0, -1, back[Batch.U3], back[Batch.V3],
                back[Batch.X4], back[Batch.Y4], 0, 0, 0, -1, back[Batch.U4], back[Batch.V4]
        };
    }


    public void update(float dt) {
        if(spinning) {
            float syModulus = sy % texture.getHeight();
            region.setRegion((int) sx, (int) syModulus, texture.getWidth(), 32);
            setRegion(region);
        }
        float z = position.z + 0.5f * Math.abs(MathUtils.sinDeg(angle));
        transform.setToRotation(Vector3.Y, angle);
        transform.trn(position.x, position.y, z);
    }

    public void update() {
        float z = position.z + 0.5f * Math.abs(MathUtils.sinDeg(angle));
        transform.setToRotation(Vector3.Y, angle);
        transform.trn(position.x, position.y, z);
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
