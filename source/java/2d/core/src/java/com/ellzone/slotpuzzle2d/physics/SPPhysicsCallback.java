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

package com.ellzone.slotpuzzle2d.physics;

public interface SPPhysicsCallback {
    public static final int BEGIN = 0x01;
    public static final int START = 0x02;
    public static final int END = 0x04;
    public static final int COMPLETE = 0x08;
    public static final int PARTICLE_UPDATE = 0x10;
    
    public void onEvent(int type, SPPhysicsEvent source);	
}
