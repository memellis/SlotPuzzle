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

package com.ellzone.slotpuzzle2d.prototypes;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.ellzone.slotpuzzle2d.prototypes.minislotmachine.SpinningSlots;
import com.ellzone.slotpuzzle2d.prototypes.minislotmachine.SpinningSlotsRotateHandleSprite;
import com.ellzone.slotpuzzle2d.prototypes.minislotmachine.SpinningSlotsRotateHandleSpriteUsingTweenEngine;
import com.ellzone.slotpuzzle2d.prototypes.minislotmachine.SpinningSlotsUsingSlotHandleSprite;
import com.ellzone.slotpuzzle2d.prototypes.minislotmachine.SpinningSlotsWithThreeTilesDisplayed;
import com.ellzone.slotpuzzle2d.prototypes.menu.SlotPuzzleGame;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle1;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle2;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle3;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle4;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle5;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle6;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle1ExtendingParticleTemplate;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle2ExtendingParticleTemplate;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle3ExtendingParticleTemplate;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle4ExtendingParticleTemplate;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle5ExtendingParticleTemplate;
import com.ellzone.slotpuzzle2d.prototypes.particle.Particle6ExtendingParticleTemplate;
import com.ellzone.slotpuzzle2d.prototypes.scrollingsign.ScrollingSign;
import com.ellzone.slotpuzzle2d.prototypes.tween.Dynamic;
import com.ellzone.slotpuzzle2d.prototypes.tween.Flash;
import com.ellzone.slotpuzzle2d.prototypes.tween.GameOverPopUp;
import com.ellzone.slotpuzzle2d.prototypes.tween.IntroSequence;
import com.ellzone.slotpuzzle2d.prototypes.tween.LevelOverPopUpUsingLevelPopUp;
import com.ellzone.slotpuzzle2d.prototypes.tween.LevelPopUp;
import com.ellzone.slotpuzzle2d.prototypes.tween.LevelPopUpUsingLevelPopUp;
import com.ellzone.slotpuzzle2d.prototypes.tween.ReelLetterTilePlay;
import com.ellzone.slotpuzzle2d.prototypes.tween.TileInputSelect;
import com.ellzone.slotpuzzle2d.prototypes.tween.Veil;
import com.ellzone.slotpuzzle2d.prototypes.tween.WayPoints1;
import com.ellzone.slotpuzzle2d.prototypes.tween.WayPoints2;
import com.ellzone.slotpuzzle2d.prototypes.basic2d.Basic2D;
import com.ellzone.slotpuzzle2d.prototypes.bezier.Bezier1;
import com.ellzone.slotpuzzle2d.prototypes.bezier.Bezier2;
import com.ellzone.slotpuzzle2d.prototypes.bezier.Bezier3;
import com.ellzone.slotpuzzle2d.prototypes.map.SmoothScrollingWorldMap;
import com.ellzone.slotpuzzle2d.prototypes.map.SubPixelPerfectSmoothScrolling;
import com.ellzone.slotpuzzle2d.prototypes.map.WorldMapLevelSelect;
import com.ellzone.slotpuzzle2d.prototypes.map.WorldMapLevelSelectAndReturn;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class SPPrototypes {
    public static final List<Class<? extends SPPrototype>> tests = new ArrayList<Class<? extends SPPrototype>>((Collection<? extends Class<? extends SPPrototype>>) Arrays.asList(
        //CreateLevelReels.class,
        Basic2D.class,
        Bezier1.class,
        Bezier2.class,
        Bezier3.class,
        SmoothScrollingWorldMap.class,
        SubPixelPerfectSmoothScrolling.class,
        WorldMapLevelSelect.class,
        WorldMapLevelSelectAndReturn.class,
		Particle1.class,
        Particle2.class,
        Particle3.class,
        Particle4.class,
        Particle5.class,
        Particle6.class,
		Particle1ExtendingParticleTemplate.class,																										   
		Particle2ExtendingParticleTemplate.class,					
		Particle3ExtendingParticleTemplate.class,																																														   //Dynamic.class,																						   
	    Particle4ExtendingParticleTemplate.class,																										   
		Particle5ExtendingParticleTemplate.class,
		Particle6ExtendingParticleTemplate.class,
		ScrollingSign.class,
		Dynamic.class,																			   
		Flash.class,
        GameOverPopUp.class,
        IntroSequence.class,
        LevelOverPopUpUsingLevelPopUp.class,
        LevelPopUp.class,
        LevelPopUpUsingLevelPopUp.class,
		ReelLetterTilePlay.class,
        TileInputSelect.class,
        Veil.class,
        WayPoints1.class,
        WayPoints2.class,
	    SpinningSlots.class,
		SpinningSlotsRotateHandleSprite.class,
		SpinningSlotsRotateHandleSpriteUsingTweenEngine.class,
		SpinningSlotsUsingSlotHandleSprite.class,
		SpinningSlotsWithThreeTilesDisplayed.class,
		SPPrototypesGame.class,
		SlotPuzzleGame.class
    ));

    public static List<String> getNames () {
        List<String> names = new ArrayList<String>(tests.size());
        for (Class clazz : tests)
            names.add(clazz.getSimpleName());
        Collections.sort(names);
        return names;
    }

    private static Class<? extends SPPrototype> forName(String name) {
        for (Class clazz : tests)
            if (clazz.getSimpleName().equals(name)) return clazz;
        return null;
    }

    public static SPPrototype newSPPrototype(String testName) {
        try {
            return ClassReflection.newInstance(forName(testName));
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
        return null;
    }
}
