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

package com.ellzone.slotpuzzle.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;

public class VersionInfoFile {
    private VersionInfo versionInfo;


    public VersionInfoFile() {
    }

    public VersionInfoFile(VersionInfo versionInfo) {
        this.versionInfo = versionInfo;
    }

    public void saveVersionInfo(String filename) {
        Json json = new Json();
        json.toJson(versionInfo, VersionInfo.class, Gdx.files.local(filename));
    }

    public void saveVersionInfoPretty(String versionInfoFilename) {
        Json json = new Json();
        Gdx.files.local(versionInfoFilename).writeString(
            json.prettyPrint(versionInfo),
            false
        );
    }

    public VersionInfo loadVersionInfo(String versionInfoFile) {
        Json json = new Json();
        return json.fromJson(VersionInfo.class,
            Gdx.files.local(versionInfoFile));
    }

    public VersionInfo loadVersionInfoInternal(String versionInfoFile) {
        Json json = new Json();
        return json.fromJson(VersionInfo.class,
            Gdx.files.internal(versionInfoFile));
    }
}
