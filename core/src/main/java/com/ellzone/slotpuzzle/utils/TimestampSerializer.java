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

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampSerializer implements Json.Serializable{
    public static final String TIMESTAMP_FORMAT = "EEE, d MMM yyyy HH:mm:ss.SSS Z";

    private static final SimpleDateFormat dateFormat =
        new SimpleDateFormat(TIMESTAMP_FORMAT);
    private Timestamp timestamp;

    public TimestampSerializer() {}

    public TimestampSerializer(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getYear() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy");
        return dateFormat.format(timestamp);
    }

    public void write(Json json) {
        String formattedDate = dateFormat.format(timestamp);
        json.writeValue("timestamp", formattedDate);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        String timestampName = jsonData.child().name();
        String timestampValue = jsonData.child().asString();
        Date date = null;
        try {
            date = dateFormat.parse(timestampValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        timestamp = new Timestamp(date.getTime());
    }
}
