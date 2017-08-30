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

package com.ellzone.slotpuzzle2d.testpuzzlegrid;


import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.ellzone.slotpuzzle2d.puzzlegrid.TypeGridValue;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestTypeGridValue {

    @Test
    public void testUsingTestTypeGridValueAsArray() {
        TypeGridValue<ReelTile>[] test = null;
        TypeGridValue<ReelTile>[] typeGridValues = TypeGridValue.newArray((Class<TypeGridValue<ReelTile>[]>) test.getClass(), 20);
        assertEquals(typeGridValues.length, 20);
    }


}
