package com.ellzone.slotpuzzle2d.puzzlegrid;

public class Tuple {
	private int r, c, value;
	public Tuple(int r, int c) {
		this.r = r;
		this.c = c;
	}
	public Tuple(int r, int c, int value) {
		this.r = r;
		this.c = c;
		this.value = value;
	}
	public int getR() {
		return this.r;
	}
	public int getC() {
		return this.c;
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
	public void setValue(int value) {
		this.value = value;
	}
}
