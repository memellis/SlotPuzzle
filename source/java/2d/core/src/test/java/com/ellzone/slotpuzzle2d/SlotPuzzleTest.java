package com.ellzone.slotpuzzle2d;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ellzone.slotpuzzle2d.screens.WorldScreen;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SlotPuzzleTest {

    @Mock
    protected LibGdxFactory mLibGdxFactory;

    @InjectMocks
    protected SlotPuzzle slotPuzzle;

    @InjectMocks
    protected LibGdxFactory lowLevelSingleton;

    @Before
    public void setUp() {
        final BitmapFont bitmapFont = mock(BitmapFont.class);

        Gdx.graphics = mock(Graphics.class);
        Gdx.app = mock(Application.class);

        slotPuzzle.create();
    }

    @After
    public void tearDown() {
        slotPuzzle.dispose();
        slotPuzzle = null;
    }

    @Test
    public void testSlotPuzzle() {
        assertThat(mLibGdxFactory, is(LibGdxFactory.getInstance()));
        slotPuzzle.create();
    }
}