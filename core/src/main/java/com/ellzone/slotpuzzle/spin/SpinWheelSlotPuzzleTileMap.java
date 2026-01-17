/*
 *******************************************************************************
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

package com.ellzone.slotpuzzle.spin;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;
import com.ellzone.slotpuzzle.utils.convert.TileMapToWorldConvert;

public class SpinWheelSlotPuzzleTileMap implements SpinWheelSlotPuzzle {
    public static final int MAP_WIDTH = 4000;
    public static final int MAP_HEIGHT = 16000;
    public static final int WORLD_WIDTH = 10000;
    public static final int WORLD_HEIGHT = 40000;

    private SpinWheelForSlotPuzzle spinWheel;
    private TileMapToWorldConvert tileMapToWorldConvert;
    private float worldPositionX;
    private float worldPositionY;

    public SpinWheelSlotPuzzleTileMap(
        float diameter,
        float x,
        float y,
        int nPegs,
        World world) {
        initialise();
        spinWheel = new
            SpinWheelForSlotPuzzle(
            diameter,
            convertTileMapXToWorldPositionX(x),
            convertTileMapYToWorldPositionY(y),
            nPegs,
            world);
    }

    public SpinWheelSlotPuzzleTileMap(
        float diameter,
        float x,
        float y,
        int nPegs,
        World world,
        TileMapToWorldConvert tileMapToWorldConvert) {
        this.tileMapToWorldConvert = tileMapToWorldConvert;
        spinWheel = new
            SpinWheelForSlotPuzzle(
            diameter,
            convertTileMapXToWorldPositionX(x),
            convertTileMapYToWorldPositionY(y),
            nPegs,
            world);
    }

    private void initialise() {
        tileMapToWorldConvert = new TileMapToWorldConvert(
            MAP_WIDTH,
            MAP_HEIGHT,
            WORLD_WIDTH,
            WORLD_HEIGHT
        );
    }

    private float convertTileMapXToWorldPositionX(float x) {
        worldPositionX = tileMapToWorldConvert.convertToWorldX(x);
        return worldPositionX;
    }

    private float convertTileMapYToWorldPositionY(float y) {
        worldPositionY = tileMapToWorldConvert.convertToWorldY(y);
        return worldPositionY;
    }

    public float getWorldPositionX() {
        return worldPositionX;
    }

    public float getWorldPositionY() {
        return worldPositionY;
    }

    @Override
    public void setUpSpinWheel() {
        spinWheel.setUpSpinWheel();
    }

    @Override
    public void setUpSpinWheel(Stage stage) {
        spinWheel.setUpSpinWheel(stage);
    }

    @Override
    public Image getWheelImage() {
        return spinWheel.getWheelImage();
    }

    @Override
    public Image getNeedleImage() {
        return spinWheel.getNeedleImage();
    }

    public Image getSpinButton() { return spinWheel.getSpinButton(); }

    @Override
    public void updateCoordinates(Body body, Image image, float incX, float incY) {
        spinWheel.updateCoordinates(body, image, incX, incY);
    }

    @Override
    public void spin(float omega) {
        spinWheel.spin(omega);
    }

    @Override
    public boolean spinningStopped() {
        return spinWheel.spinningStopped();
    }

    @Override
    public void setWorldContactListener(ContactListener listener) {
        spinWheel.setWorldContactListener(listener);
    }

    @Override
    public void setWorld(World world) {
        spinWheel.setWorld(world);
    }

    @Override
    public Body getWheelBody() {
        return spinWheel.getWheelBody();
    }

    @Override
    public Body getNeedleBody() {
        return spinWheel.getNeedleBody();
    }

    @Override
    public float getNeedleCenterX(float needleWidth) {
        return spinWheel.getNeedleCenterX(needleWidth);
    }

    @Override
    public float getNeedleCenterY(float needleHeight) {
        return spinWheel.getNeedleCenterY(needleHeight);
    }

    @Override
    public void setElements(ObjectMap<IntArray, Object> elements) {
        spinWheel.setElements(elements);
    }

    @Override
    public void addElementData(Object object, IntArray data) {
        spinWheel.addElementData(object, data);
    }

    @Override
    public Object getLuckyWinElement() {
        return spinWheel.getLuckyWinElement();
    }

    public boolean isInsideSpinButton(Vector2 point) { return spinWheel.isInsideSpinButton(point); }
}
