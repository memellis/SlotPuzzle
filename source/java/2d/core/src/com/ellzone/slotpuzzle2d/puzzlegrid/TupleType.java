package com.ellzone.slotpuzzle2d.puzzlegrid;

public class TupleType<T> {
	private int r, c;
	private T t;
	public TupleType(int r, int c) {
		this.r = r;
		this.c = c;
	}
	public TupleType(int r, int c, T t) {
		this.r = r;
		this.c = c;
		this.t = t;
	}
	public int getR() {
		return this.r;
	}
	public int getC() {
		return this.c;
	}
	public T getValue() {
		return this.t;
	}
	public void setR(int r) {
		this.r = r;
	}
	public void setC(int c) {
		this.c = c;
	}
	public void setValue(T t) {
		this.t = t;
	}
}
