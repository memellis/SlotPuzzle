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

package com.ellzone.slotpuzzle.level.creator;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ellzone.slotpuzzle.sprites.reel.ReelSprites;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;
import com.ellzone.slotpuzzle.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle.utils.convert.TileMapToWorldConvert;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import box2dLight.RayHandler;

public class LevelObjectCreator {
    public static final String ADD_TO = "addTo";
    public static final String DELEGATE_TO_CALLBACK = "delegateToCallback";
    public static final String FIELD = "field";
    public static final String METHOD = "method";
    public static final String PROPERTY = "property";
    public static final String VALUE = "value";
    public static final String PARAMETER = "Parameter";
    public static final String PARAMETER_VALUE = "ParameterValue";
    public static final String COMPONENT = "Component";
    public static final String PROPERTY_NAME = "Property";
    public static final String DOT_REGULAR_EXPRESSION = "\\.";
    public static final String COLON = ":";
    public static final int LENGTH_TWO = 2;
    public static final int SECOND_PART = 1;

    public static final String INT = "int";
    public static final String LONG = "long";
    public static final String DOUBLE = "double";
    public static final String FLOAT = "float";
    public static final String BOOL = "bool";
    public static final String CHAR = "char";
    public static final String BYTE = "byte";
    public static final String VOID = "void";
    public static final String SHORT = "short";
    public static final String CLASS = "Class";
    public static final String ADD_COMPONENT_TO_ENTITY = "addComponentToEntity";
    public static final String COULD_NOT_EXTRACT_INTEGER_FROM_MESSAGE = "Could not extract integer from <{0}>";
    public static final String COULD_NOT_EXTRACT_FLOAT_FROM_MESSAGE = "Could not extract float from <{0}>";
    public static final String COULD_NOT_EXTRACT_BOOLEAN_FROM_MESSAGE = "Could not extract boolean from <{0}>";
    public static final String COULD_NOT_EXTRACT_OBJECT_FROM_MESSAGE = "Could not extract object from <{0}>";
    public static final String EXPECTED_NUMBER_OF_PROPERTY_PARTS_TO_BE_MESSAGE = "Expected number of property parts to be {0} actually found {1}";

    protected LevelCreatorInjectionInterface levelCreatorInjectionInterface;
    protected World world;
    protected RayHandler rayHandler;
    protected Color reelPointLightColor = new Color(0.2f, 0.2f, 0.2f, 0.2f);

    public LevelObjectCreator(
        LevelCreatorInjectionInterface injectionInterface,
        World box2dWorld,
        RayHandler rayHandler) {
        this.levelCreatorInjectionInterface = injectionInterface;
        this.world = box2dWorld;
        this.rayHandler = rayHandler;
    }

    public void createLevel(Array<MapObject> levelMapObjects) throws GdxRuntimeException {
        boolean huntingForComponents = true;
        int componentCount = 1;
        Array<String> components = new Array<>();
        if (levelMapObjects == null)
            throw new GdxRuntimeException(new IllegalArgumentException());

        for (MapObject mapObject :
            new Array.ArrayIterator<>(levelMapObjects)) {
            ProcessCustomMapProperties processCustomMapProperties =
                new ProcessCustomMapProperties(
                    huntingForComponents,
                    componentCount,
                    components,
                    mapObject).invoke();
            huntingForComponents = processCustomMapProperties.isHuntingForComponents();
            componentCount = processCustomMapProperties.getComponentCount();
        }
    }

    public AnnotationAssetManager getAnnotationAssetManager() {
        return levelCreatorInjectionInterface.getAnnotationAssetManager();
    }

    public ReelSprites getReelSprites() {
        return levelCreatorInjectionInterface.getReelSprites();
    }

    public Texture getSlotReelScrollTexture() {
        return levelCreatorInjectionInterface.getSlotReelScrollTexture();
    }

    public Sound getReelSpinningSound() {
        return levelCreatorInjectionInterface.getAnnotationAssetManager()
            .get(AssetsAnnotation.SOUND_REEL_SPINNING);
    }

    public Sound getReelStoppingSound() {
        return levelCreatorInjectionInterface.getAnnotationAssetManager()
            .get(AssetsAnnotation.SOUND_REEL_STOPPED);
    }

    public TweenManager getTweenManager() {
        return levelCreatorInjectionInterface.getTweenManager();
    }

    public TextureAtlas getSlotHandleAtlas() {
        return levelCreatorInjectionInterface.getSlotHandleAtlas();
    }

    public TileMapToWorldConvert getTileMapToWorldConvert() {
        return levelCreatorInjectionInterface.getTileMapToWorldConvert();
    }

    private Object[] parseConstructorParameterValues(Array<String> constructorParameterValues,
                                                     Class<?>[] classParameters,
                                                     MapProperties rectangleMapProperties)
        throws NoSuchFieldException,
        IllegalAccessException,
        NoSuchMethodException,
        InvocationTargetException {
        Object[] parameterValues = new Object[constructorParameterValues.size];
        int index = 0;
        for (String parameter : new Array.ArrayIterator<>(constructorParameterValues)) {
            parameterValues[index] =
                parseParameterValue(parameter, classParameters[index], rectangleMapProperties);
            index++;
        }
        return parameterValues;
    }

    private Object parseParameterValue(String parameter,
                                       Class<?> classParam,
                                       MapProperties rectangleMapProperties)
        throws NoSuchFieldException,
        IllegalAccessException,
        NoSuchMethodException,
        InvocationTargetException {
        if (parameter.toLowerCase().startsWith(FIELD))
            return parseField(parameter);
        if (parameter.toLowerCase().startsWith(METHOD))
            return parseMethod(parameter);
        if (parameter.toLowerCase().startsWith(PROPERTY))
            return parseProperty(parameter, classParam, rectangleMapProperties);
        if (parameter.toLowerCase().startsWith(VALUE))
            return parseValue(parameter, classParam);
        throw new GdxCouldNotParseParameterValueException(
            MessageFormat.format("Parameter={0}", parameter));
    }

    private Object parseValue(String parameter, Class<?> classParam) {
        String[] parts = parameter.split(COLON);
        String valuePart = parts.length == LENGTH_TWO ? parts[SECOND_PART] : null;
        if (valuePart == null)
            throw new GdxCouldNotParseParameterValueException(parameter);
        if (isInt(classParam))
            return Integer.valueOf(valuePart);
        if (isFloat(classParam))
            return Float.valueOf(valuePart);
        if (isBoolean(classParam))
            return Boolean.valueOf(valuePart);
        if (isString(classParam))
            return valuePart;
        throw new GdxCouldNotParseParameterValueException(parameter);
    }

    private Object parseField(String parameter)
        throws NoSuchFieldException, IllegalAccessException {
        String[] parts = parameter.split(DOT_REGULAR_EXPRESSION);
        String fieldPart = parts.length == LENGTH_TWO ? parts[SECOND_PART] : null;
        if (fieldPart == null)
            throw new GdxCouldNotParseParameterValueException(parameter);
        Field field = this.getClass().getDeclaredField(fieldPart);
        return field.get(this);
    }

    private Object parseMethod(String parameter)
        throws
        NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] parts = parameter.split(DOT_REGULAR_EXPRESSION);
        String methodPart = parts.length == LENGTH_TWO ? parts[SECOND_PART] : null;
        if (methodPart == null)
            throw new GdxCouldNotParseParameterValueException(parameter);
        Method method = this.getClass().getDeclaredMethod(methodPart);
        return method.invoke(this);
    }

    private Object parseProperty(String parameter, Class<?> classParam, MapProperties rectangleMapProperties) {
        String[] parts = parameter.split(DOT_REGULAR_EXPRESSION);
        if (isInt(classParam))
            return getInteger(classParam, rectangleMapProperties, parts);
        if (isFloat(classParam))
            return getFloat(classParam, rectangleMapProperties, parts);
        if (isBoolean(classParam))
            return getBoolean(classParam, rectangleMapProperties, parts);

        return getObject(classParam, rectangleMapProperties, parts);
    }

    private Integer getInteger(Class<?> classParam, MapProperties rectangleMapProperties, String[] parts) throws GdxRuntimeException {
        verifyThereAreTwoParts(parts);
        Integer parsedInteger = convertToIntegerFromFloat((Float) rectangleMapProperties.get(parts[1].toLowerCase(), classParam));
        if (parsedInteger != null)
            return parsedInteger;
        throw new GdxCouldNotParsePropertyException(
            MessageFormat.format(COULD_NOT_EXTRACT_INTEGER_FROM_MESSAGE,
                parts[1]));
    }

    private Float getFloat(Class<?> classParam, MapProperties rectangleMapProperties, String[] parts) {
        verifyThereAreTwoParts(parts);
        Float parsedFloat = (Float) rectangleMapProperties.get(parts[1].toLowerCase(), classParam);
        if (parsedFloat != null)
            return parsedFloat;
        throw new GdxCouldNotParsePropertyException(
            MessageFormat.format(COULD_NOT_EXTRACT_FLOAT_FROM_MESSAGE,
                parts[1]));
    }

    private Boolean getBoolean(Class<?> classParam, MapProperties rectangleMapProperties, String[] parts) {
        verifyThereAreTwoParts(parts);
        Boolean parsedBoolean = (Boolean) rectangleMapProperties.get(parts[1].toLowerCase(), classParam);
        if (parsedBoolean != null)
            return parsedBoolean;
        throw new GdxCouldNotParsePropertyException(
            MessageFormat.format(COULD_NOT_EXTRACT_BOOLEAN_FROM_MESSAGE,
                parts[1]));
    }

    private Object getObject(Class<?> classParam, MapProperties rectangleMapProperties, String[] parts) {
        verifyThereAreTwoParts(parts);
        Object parsedObject = rectangleMapProperties.get(parts[1].toLowerCase(), classParam);
        if (parsedObject != null)
            return parsedObject;
        else throw new GdxCouldNotParsePropertyException(
            MessageFormat.format(COULD_NOT_EXTRACT_OBJECT_FROM_MESSAGE,
                parts[1]));
    }

    private void verifyThereAreTwoParts(String[] parts) {
        if (parts.length != LENGTH_TWO)
            throw new GdxCouldNotParsePropertyException(
                MessageFormat.format(
                    EXPECTED_NUMBER_OF_PROPERTY_PARTS_TO_BE_MESSAGE,
                    LENGTH_TWO,
                    parts.length));
    }

    private boolean isInt(Class<?> clazz) {
        return clazz.equals(int.class);
    }

    private boolean isFloat(Class<?> clazz) {
        return clazz.equals(float.class);
    }

    private boolean isBoolean(Class<?> clazz) {
        return clazz.equals(boolean.class);
    }

    private boolean isString(Class<?> clazz) { return clazz.equals(String.class); }

    private Integer convertToIntegerFromFloat(Float floatParam){
        return floatParam == null ? null : floatParam.intValue();
    }

    Map<String, Class<?>> builtInMap = new HashMap<>(); {
        builtInMap.put(INT, Integer.TYPE );
        builtInMap.put(LONG, Long.TYPE );
        builtInMap.put(DOUBLE, Double.TYPE );
        builtInMap.put(FLOAT, Float.TYPE );
        builtInMap.put(BOOL, Boolean.TYPE );
        builtInMap.put(CHAR, Character.TYPE );
        builtInMap.put(BYTE, Byte.TYPE );
        builtInMap.put(VOID, Void.TYPE );
        builtInMap.put(SHORT, Short.TYPE );
    }

    public Class<?>[] getParamTypes(Array<String> parameters){
        Class<?>[] classParameters = new Class<?>[parameters.size];
        int index = 0;

        for( String type : new Array.ArrayIterator<>(parameters)) {
            try {
                if (builtInMap.containsKey(type))
                    classParameters[index++] = builtInMap.get(type);
                else
                    classParameters[index++] = Class.forName(type);
            } catch (ClassNotFoundException cnfe) {
                throw  new GdxRuntimeException(cnfe);
            }
        }
        return classParameters;
    }

    private void invokeObjectMethod(Object createdObject, String methodName) {
        try {
            Method method = this.getClass().getDeclaredMethod(methodName, createdObject.getClass());
            method.invoke(this, createdObject);
        } catch (NoSuchMethodException e) {
            throw new GdxRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new GdxRuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new GdxRuntimeException(e);
        }
    }

    private void invokeComponentObjectMethod(
        Object createdObject,
        Object createdComponent)
        throws
        NoSuchMethodException,
        IllegalAccessException,
        InvocationTargetException {
        Class<?>[] methodParameters = new Class[2];
        methodParameters[0] = createdObject.getClass();
        methodParameters[1] = Component.class;
        Method method = this.getClass().getDeclaredMethod(
            LevelObjectCreator.ADD_COMPONENT_TO_ENTITY,
            methodParameters);
        method.invoke(this, createdObject, createdComponent);
    }

    private class ProcessCustomMapProperties {
        public static final String NO_SUCH_CONSTRUCTOR_MESSAGE = "No such constructor: {0}";
        private boolean huntingForComponents;
        private int componentCount;
        private final Array<String> components;
        private final MapObject mapObject;

        public ProcessCustomMapProperties(
            boolean huntingForComponents,
            int componentCount,
            Array<String> components,
            MapObject mapObject) {
            this.huntingForComponents = huntingForComponents;
            this.componentCount = componentCount;
            this.components = components;
            this.mapObject = mapObject;
        }

        public boolean isHuntingForComponents() {
            return huntingForComponents;
        }

        public int getComponentCount() {
            return componentCount;
        }

        public ProcessCustomMapProperties invoke() {
            MapProperties mapObjectProperties = mapObject.getProperties();
            Class<?> clazz = getClassFromProperties(mapObjectProperties);
            if (clazz != null)
                processMapProperties(mapObjectProperties, clazz);

            return this;
        }

        private void processMapProperties(MapProperties mapObjectProperties, Class<?> clazz) {
            Object createdObject = createMapObjectFromProperties(mapObjectProperties, clazz);
            processComponents(mapObjectProperties, createdObject);
        }

        private Object createMapObjectFromProperties(
            MapProperties mapObjectProperties,
            Class<?> clazz) {
            Object createdObject;
            Array<String> constructorParameters =
                getParameters(mapObjectProperties, PARAMETER);
            Class<?>[] classParameters = getParamTypes(constructorParameters);
            Constructor<?> constructor = getConstructor(clazz, classParameters);
            Array<String> constructorParameterValues =
                getParameters(mapObjectProperties, PARAMETER_VALUE);
            createdObject = getCreatedObjectObject(
                mapObjectProperties,
                classParameters,
                constructor,
                constructorParameterValues);
            return createdObject;
        }

        private Object getCreatedObjectObject(
            MapProperties mapObjectProperties,
            Class<?>[] classParameters,
            Constructor<?> constructor,
            Array<String> constructorParameterValues) {
            Object createdObject;
            try {
                createdObject = getCreatedObject(
                    mapObjectProperties,
                    classParameters,
                    constructor,
                    constructorParameterValues);
                if (createdObject != null) {
                    invokeObjectMethod(createdObject, ADD_TO);
                    invokeObjectMethod(createdObject, DELEGATE_TO_CALLBACK);
                    componentCount = 1;
                    huntingForComponents = true;
                }
            } catch (Exception e) {
                throw new GdxRuntimeException(e);
            }
            return createdObject;
        }

        private void processComponents(
            MapProperties mapObjectProperties,
            Object createdObject) {
            while (huntingForComponents) {
                String component = parseComponent(
                    COMPONENT + componentCount,
                    mapObjectProperties);
                if (component != null) {
                    System.out.println(MessageFormat.format("{0}", component));
                    components.add(component);
                    Array<String> componentParameters =
                        getParameters(
                            mapObjectProperties,
                            COMPONENT + componentCount + PROPERTY_NAME);
                    if (componentParameters.size > 0) {
                        invokeCreatedComponentObjectMethod(
                            mapObjectProperties,
                            createdObject,
                            component,
                            componentParameters);
                    } else
                        throw new GdxComponentHasNoParametersException(component);
                } else
                    huntingForComponents = false;
            }
        }

        private void invokeCreatedComponentObjectMethod(
            MapProperties mapObjectProperties,
            Object createdObject,
            String component,
            Array<String> componentParameters) {
            Object createdComponent;
            Class<?>[] componentParameterTypes = getParamTypes(componentParameters);
            Array<String> componentParameterValues =
                getParameters(
                    mapObjectProperties,
                    COMPONENT + componentCount + "Value");
            Class<?> componentClass = getClass(component);
            Constructor<?> componentConstructor =
                getConstructor(componentClass, componentParameterTypes);
            try {
                createdComponent = getCreatedObject(
                    mapObjectProperties,
                    componentParameterTypes,
                    componentConstructor,
                    componentParameterValues);
                if (createdObject != null)
                    invokeComponentObjectMethod(createdObject, createdComponent);
            } catch (Exception e) {
                throw new GdxRuntimeException(e);
            }
            componentCount++;
        }

        private Class<?> getClassFromProperties(MapProperties mapObjectProperties) {
            String className = (String) mapObjectProperties.get(CLASS);
            return className == null ? null : getClass(className);
        }

        private Class<?> getClass(String className) {
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException cnfe) {
                throw new GdxRuntimeException(cnfe);
            }
            return clazz;
        }

        private Array<String> getParameters(
            MapProperties mapObjectProperties,
            String key) {
            int parameterCount = 1;
            Array<String> parameters = new Array<>();
            String parameterKey = key + parameterCount;
            String parameterValue =  (String) mapObjectProperties.get(parameterKey);
            while (parameterValue != null) {
                parameters.add(parameterValue);
                parameterKey = key + ++parameterCount;
                parameterValue =  (String) mapObjectProperties.get(parameterKey);
            }
            return parameters;
        }

        private Constructor<?> getConstructor(Class<?> clazz, Class<?>[] classParameters)
            throws GdxRuntimeException {
            Constructor<?> constructor;
            try {
                constructor = clazz.getConstructor(classParameters);
            } catch (NoSuchMethodException nsme) {
                throw new GdxRuntimeException(
                    MessageFormat.format(NO_SUCH_CONSTRUCTOR_MESSAGE, nsme.getMessage()));
            }
            return constructor;
        }

        private Object getCreatedObject(MapProperties mapObjectProperties,
                                        Class<?>[] classParameters,
                                        Constructor<?> constructor,
                                        Array<String> constructorParameterValues)
            throws
            NoSuchFieldException,
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException,
            NoSuchMethodException {
            Object[] constructorParametersValues = parseConstructorParameterValues(
                constructorParameterValues, classParameters, mapObjectProperties);
            return  constructor == null ? null : constructor.newInstance(constructorParametersValues);
        }

        private String parseComponent(String component, MapProperties mapObjectProperties) {
            return (String) mapObjectProperties.get(component);
        }
    }

    public static class GdxCouldNotParsePropertyException extends GdxRuntimeException {
        public GdxCouldNotParsePropertyException(String message) {
            super(message);
        }
    }

    public static class GdxCouldNotParseParameterValueException extends GdxRuntimeException {
        public GdxCouldNotParseParameterValueException(String message) {
            super(message);
        }
    }

    public static class GdxComponentHasNoParametersException extends GdxRuntimeException {
        public GdxComponentHasNoParametersException(String message) {super(message); }
    }
}
