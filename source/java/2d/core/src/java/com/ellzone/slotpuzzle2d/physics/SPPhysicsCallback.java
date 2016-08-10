package com.ellzone.slotpuzzle2d.physics;

public interface SPPhysicsCallback {
    public static final int BEGIN = 0x01;
    public static final int START = 0x02;
    public static final int END = 0x04;
    public static final int COMPLETE = 0x08;
    public static final int PARTICLE_UPDATE = 0x10;
    
    public void onEvent(int type, SPPhysicsEvent source);	
}
