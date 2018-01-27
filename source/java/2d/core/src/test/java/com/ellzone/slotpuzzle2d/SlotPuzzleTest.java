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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.screens.LoadingScreen;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class SlotPuzzleTest {
    String[] logLevelsAsStrings = {SlotPuzzleConstants.DEBUG, SlotPuzzleConstants.INFO, SlotPuzzleConstants.ERROR};
    int[] logLevels = {Application.LOG_DEBUG, Application.LOG_INFO, Application.LOG_ERROR};

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
        final BitmapFont bitmapFont = mock(BitmapFont.class);
        LoadingScreen loadingScreen = mock(LoadingScreen.class);

        Gdx.graphics = mock(Graphics.class);
        Gdx.app = mock(Application.class);

        when(mLibGdxFactory.newSpriteBatch()).thenReturn(mock(SpriteBatch.class));
        when(mLibGdxFactory.newLoadScreen(any(SlotPuzzle.class))).thenReturn(mock(LoadingScreen.class));
        when(mLibGdxFactory.newAnnotationAssetManager()).thenReturn(mock(AnnotationAssetManager.class));
    }

    @After
    public void tearDown() {
        slotPuzzle.dispose();
        slotPuzzle = null;
    }

    @Test
    public void testSlotPuzzleMocked() {
        slotPuzzle.create();
        assertThat(mLibGdxFactory.getClass().getName(), is(equalTo(LibGdxFactory.getInstance().getClass().getName())));
    }

    @Test
    public void testSlotPuzzleSetLogLevelViaSystemProperty() {
        for (int i=0; i<logLevels.length; i++) {
            System.setProperty(SlotPuzzleConstants.LIBGDX_LOGLEVEL_PROPERTY, logLevelsAsStrings[i]);
            when(Gdx.app.getLogLevel()).thenReturn(logLevels[i]);
            slotPuzzle.create();
            assertThat(Gdx.app.getLogLevel(), is(equalTo(logLevels[i])));
        }
    }

    @Rule
    public final EnvironmentVariables environmentVariables = new EnvironmentVariables();

    @Test
    public void testSlotPuzzleSetLogLevelViaEnviromentVariable() {
        for (int i=0; i<logLevels.length; i++) {
            environmentVariables.set(SlotPuzzleConstants.LIBGDX_LOGLEVEL, logLevelsAsStrings[i]);
            assertEquals(logLevelsAsStrings[i], System.getenv(SlotPuzzleConstants.LIBGDX_LOGLEVEL));
            slotPuzzle.create();
            when(Gdx.app.getLogLevel()).thenReturn(logLevels[i]);
            assertThat(Gdx.app.getLogLevel(), is(equalTo(logLevels[i])));
        }
    }
}