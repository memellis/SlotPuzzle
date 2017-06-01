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

package com.ellzone.slotpuzzle2d.physics;

import com.badlogic.gdx.utils.Array;

public class Bezier {
	
	public Point quadraticBezier(Point p0, Point p1, Point p2, float t) {
		Point pFinal = new Point();
		pFinal.x = (float) (Math.pow(1 - t, 2) * p0.x + 
			   (1 - t) * 2 * t * p1.x + 
			   t * t * p2.x);
		pFinal.y = (float) (Math.pow(1 - t, 2) * p0.y + 
			   (1 - t) * 2 * t * p1.y + 
			   t * t * p2.y);
		return pFinal;
	}

	public Point cubicBezier(Point p0, Point p1, Point p2, Point p3, float t) {
		Point pFinal= new Point();
		pFinal.x = (float) (Math.pow(1 - t, 3) * p0.x + 
			   Math.pow(1 - t, 2) * 3 * t * p1.x + 
			   (1 - t) * 3 * t * t * p2.x + 
			   t * t * t * p3.x);
		pFinal.y = (float) (Math.pow(1 - t, 3) * p0.y + 
			   Math.pow(1 - t, 2) * 3 * t * p1.y + 
			   (1 - t) * 3 * t * t * p2.y + 
			   t * t * t * p3.y);
		return pFinal;
	}
	
	public Array<Point> multicurve(Array<Point> points, float step) {
		Point p0 = new Point();
		Point p1 = new Point();
		Point p2 = new Point();
		Point mid = new Point();
		Array<Point> multicurvePoints = new Array<Point>();

		p0 = points.get(0);
		p1 = points.get(1);
		p2 = points.get(2);
		mid.x = (p1.x + p2.x) / 2;
		mid.y = (p1.y + p2.y) / 2;
		for (float t=0; t<1; t+=step) {
			multicurvePoints.add(quadraticBezier(p0, p1, mid, t));				
		}
		for(int i = 2; i < points.size - 2; i += 1) {
			p0.x = mid.x;
			p0.y = mid.y;
			p1 = points.get(i);
			p2 = points.get(i + 1);
			mid.x = (p1.x + p2.x) / 2;
			mid.y = (p1.y + p2.y) / 2;
			for (float t=0; t<1; t+=step) {
				multicurvePoints.add(quadraticBezier(p0, p1, mid, t));	
			}
		}
		p0 = points.get(points.size - 2);
		p1 = points.get(points.size - 1);
		for (float t=0; t<1; t+=step) {
			multicurvePoints.add(quadraticBezier(mid, p0, p1, t));
		}
		return multicurvePoints;
	}
}
