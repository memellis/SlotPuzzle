package com.ellzone.slotpuzzle2d.sprites;

import com.ellzone.slotpuzzle2d.GameTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;
public class BuilderTest extends GameTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void lightButtonBuild() {
        thrown.expect(IllegalStateException.class);
        LightButtonBuilder lightButton = new LightButtonBuilder.Builder().build();
    }
}