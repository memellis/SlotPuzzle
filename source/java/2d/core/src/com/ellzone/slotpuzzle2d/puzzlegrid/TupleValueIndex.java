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
