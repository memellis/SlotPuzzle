/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ellzone.slotpuzzle.screens.LoadingScreen;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class LibGdxFactory {

    private static LibGdxFactory instance = new LibGdxFactory();

    private LibGdxFactory() {
    }

    private LibGdxFactory(final LibGdxFactory mock) {
        instance = mock;
    }

    public static LibGdxFactory getInstance() {
        return instance;
    }

    public SpriteBatch newSpriteBatch() {
        return new SpriteBatch();
    }

    public LoadingScreen newLoadScreen(SlotPuzzleGame game) {
        return new LoadingScreen(game);
    }

    public AnnotationAssetManager newAnnotationAssetManager() { return new AnnotationAssetManager(); }
}
