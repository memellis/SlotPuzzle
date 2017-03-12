package com.ellzone.slotpuzzle2d.prototypes;

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SPPrototypes {
    public static final List<Class<? extends SPPrototype>> tests = new ArrayList<Class<? extends SPPrototype>>((Collection<? extends Class<? extends SPPrototype>>) Arrays.asList(
       Bezier1.class,
       Bezier2.class
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

    public static SPPrototype newSample(String testName) {
        try {
            return ClassReflection.newInstance(forName(testName));
        } catch (ReflectionException e) {
            e.printStackTrace();
        }
        return null;
    }

}
