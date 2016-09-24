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

package com.ellzone.slotpuzzle2d.effects;

import com.badlogic.gdx.graphics.Color;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import aurelienribon.tweenengine.TweenAccessor;

public class ReelAccessor implements TweenAccessor<ReelTile> {
    public static final int POS_XY = 1;
    public static final int CPOS_XY = 2;
    public static final int SCALE_XY = 3;
    public static final int ROTATION = 4;
    public static final int OPACITY = 5;
    public static final int TINT = 6;
    public static final int SCROLL_XY = 7;
    public static final int FLASH_TINT = 8;

    @Override
    public int getValues(ReelTile target, int tweenType, float[] returnValues) {
        switch (tweenType) {
            case POS_XY:
                returnValues[0] = target.getX();
                returnValues[1] = target.getY();
                return 2;

            case CPOS_XY:
                returnValues[0] = target.getX() + target.getWidth()/2;
                returnValues[1] = target.getY() + target.getHeight()/2;
                return 2;

            case SCALE_XY:
                returnValues[0] = target.getScaleX();
                returnValues[1] = target.getScaleY();
                return 2;

            case ROTATION: returnValues[0] = target.getRotation(); return 1;
            case OPACITY: returnValues[0] = target.getColor().a; return 1;

            case TINT:
                returnValues[0] = target.getColor().r;
                returnValues[1] = target.getColor().g;
                returnValues[2] = target.getColor().b;
                return 3;

            case SCROLL_XY:
                returnValues[0] = target.getSx();
                returnValues[1] = target.getSy();
                return 2;
                
            case FLASH_TINT:
            	returnValues[0] = target.getFlashColor().r;
                returnValues[1] = target.getFlashColor().g;
                returnValues[2] = target.getFlashColor().b;
                return 3;
            	
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(ReelTile target, int tweenType, float[] newValues) {
        switch (tweenType) {
            case POS_XY: target.setPosition(newValues[0], newValues[1]); break;
            case CPOS_XY: target.setPosition(newValues[0] - target.getWidth()/2, newValues[1] - target.getHeight()/2); break;
            case SCALE_XY: target.setScale(newValues[0], newValues[1]); break;
            case ROTATION: target.setRotation(newValues[0]); break;
            case OPACITY:
                Color oc = target.getColor();
                oc.set(oc.r, oc.g, oc.b, newValues[0]);
                target.setColor(oc);
                break;
                
            case TINT:
                Color tc = target.getColor();
                tc.set(newValues[0], newValues[1], newValues[2], tc.a);
                target.setColor(tc);
                break;
                
            case SCROLL_XY:
                target.setSx(newValues[0]);
                target.setSy(newValues[1]);
                break;

            case FLASH_TINT:
            	Color fc = target.getFlashColor();
                fc.set(newValues[0], newValues[1], newValues[2], fc.a);
            	target.setFlashColor(fc);
            	break;

            default: assert false;
        }
    }
}

