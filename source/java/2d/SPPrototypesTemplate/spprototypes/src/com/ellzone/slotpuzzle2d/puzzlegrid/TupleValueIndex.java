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

package com.ellzone.slotpuzzle2d.puzzlegrid;

public class TupleValueIndex {
	public int r;
	public int c;
	public int value;
	public int index;
	
	public TupleValueIndex(int r, int c, int index, int value) {
		this.r = r;
		this.c = c;
		this.index = index;
		this.value = value;
	}
	public int getR() {
		return this.r;
	}
	public int getC() {
		return this.c;
	}
	public int getIndex() {
		return this.index;
	}
	public int getValue() {
		return this.value;
	}
	public void setR(int r) {
		this.r = r;
	}
	public void setC(int c) {
		this.c = c;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public void setValue(int value) {
		this.value = value;
	}
}
