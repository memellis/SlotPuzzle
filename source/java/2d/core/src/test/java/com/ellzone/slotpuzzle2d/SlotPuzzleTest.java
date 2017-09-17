package com.ellzone.slotpuzzle2d;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class SlotPuzzleTest extends GameTest {
    @Test
    public void testSlotPuzzle() {
        SlotPuzzle slotPuzzle = new SlotPuzzle();
        Mockito.mock(SpriteBatch.class);
        slotPuzzle.create();
        assertNotNull(slotPuzzle);
    }
}