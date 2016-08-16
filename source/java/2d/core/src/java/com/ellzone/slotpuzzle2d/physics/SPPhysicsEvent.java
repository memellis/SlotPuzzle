package com.ellzone.slotpuzzle2d.physics;

public class SPPhysicsEvent {
	private Object source;
	
	public SPPhysicsEvent (Object source) {
		this.source = source;
	}
	
	public Object getSource() {
		return this.source;
	}
}
