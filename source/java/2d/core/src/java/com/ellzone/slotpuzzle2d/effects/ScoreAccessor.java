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

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.Color;
import com.ellzone.slotpuzzle2d.sprites.Score;

public class ScoreAccessor implements TweenAccessor<Score> {
	public static final int POS_XY = 1;
	public static final int SCALE_XY = 3;
	public static final int OPACITY = 5;
	public static final int TINT = 6;
	
	public int getValues(Score target, int tweenType, float[] returnValues) {
		switch (tweenType) {
			case POS_XY:
				returnValues[0] = target.getX();
				returnValues[1] = target.getY();
				return 2;
			case SCALE_XY:
				returnValues[0] = target.getScaleX();
				returnValues[1] = target.getScaleY();
				return 2;
			case OPACITY: returnValues[0] = target.getColor().a;
				return 1;
			case TINT:
				returnValues[0] = target.getColor().r;
				returnValues[1] = target.getColor().g;
				returnValues[2] = target.getColor().b;
				return 3;

			default: assert false; return -1;
		}
	}
	
	@Override
	public void setValues(Score target, int tweenType, float[] newValues) {
		switch (tweenType) {
			case POS_XY:
				target.setX(newValues[0]);
				target.setY(newValues[1]);
				break;
			case SCALE_XY:
				target.setScale(newValues[0], newValues[1]);
				break;
			case OPACITY: 
				Color c = target.getColor();
				c.set(c.r, c.g, c.b, newValues[0]);
				target.setColor(c);
				break;
			case TINT:
				c = target.getColor();
				c.set(newValues[0], newValues[1], newValues[2], c.a);
				target.setColor(c);
				break;

			default: assert false;
		}
	}		
}


