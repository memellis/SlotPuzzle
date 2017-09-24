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

package com.ellzone.slotpuzzle2d;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.screens.LoadingScreen;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SlotPuzzleTest {

    @Mock
    protected LibGdxFactory mLibGdxFactory;

    @InjectMocks
    protected SlotPuzzle slotPuzzle;

    @InjectMocks
    protected LibGdxFactory lowLevelSingleton;

    @InjectMocks
    private OrthographicCamera camera = new OrthographicCamera();

    private Viewport viewport;
    private Stage stage;

    @Before
    public void setUp() {
        //MockitoAnnotations.initMocks(this);
        final BitmapFont bitmapFont = mock(BitmapFont.class);
        LoadingScreen loadingScreen = mock(LoadingScreen.class);

        Gdx.graphics = mock(Graphics.class);
        Gdx.app = mock(Application.class);

        when(mLibGdxFactory.newSpriteBatch()).thenReturn(mock(SpriteBatch.class));
        when(mLibGdxFactory.newLoadScreen(any(SlotPuzzle.class))).thenReturn(mock(LoadingScreen.class));
        slotPuzzle.create();
    }

    @After
    public void tearDown() {
        slotPuzzle.dispose();
        slotPuzzle = null;
    }

    @Test
    public void testSlotPuzzle() {
        assertThat(mLibGdxFactory.getClass().getName(), is(equalTo(LibGdxFactory.getInstance().getClass().getName())));

    }
}