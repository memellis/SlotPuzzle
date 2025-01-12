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

package com.ellzone.slotpuzzle.utils;

import java.sql.Timestamp;

public class VersionInfo {
    private String version;
    private TimestampSerializer timestampSerializer;
    private String author;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Timestamp getTimetamp() {
        return timestampSerializer.getTimestamp();
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestampSerializer = new TimestampSerializer(timestamp);
    }

    public TimestampSerializer getTimestampSerializer() {
        return timestampSerializer;
    }

    public void setTimestampSerializer(TimestampSerializer timestampSerializer) {
        this.timestampSerializer = timestampSerializer;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
