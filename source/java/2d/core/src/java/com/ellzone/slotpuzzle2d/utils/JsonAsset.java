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

package com.ellzone.slotpuzzle2d.utils;

import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.reflect.ClassReflection;

public class JsonAsset implements Json.Serializable  {
    public Class<?> type;
    public String path;
    public AssetLoaderParameters parameters;

    @Override
    public void write(Json json) {
        json.writeValue("assetType", type.getName());
        json.writeValue("path", path);
        json.writeValue("parameters", parameters);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        try {
            type = ClassReflection.forName(jsonData.get("type").asString());
        } catch (Exception e) {
            type = null;
        }

        path = jsonData.get("path").asString();

        JsonValue parametersValue = jsonData.get("parameters");
        parameters = parametersValue != null ? json.fromJson(AssetLoaderParameters.class, parametersValue.toString()) : null;
    }
}
