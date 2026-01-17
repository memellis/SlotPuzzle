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

package com.ellzone.slotpuzzle.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.ellzone.slotpuzzle.effects.CameraAccessor;
import com.ellzone.slotpuzzle.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle.tweenengine.Timeline;
import com.ellzone.slotpuzzle.tweenengine.TweenCallback;

import aurelienribon.tweenengine.equations.Quart;
import lombok.Getter;
import lombok.Setter;

public class CameraLERP {
    private final Camera camera;
    @Setter
    @Getter
    private boolean cameraLERPStarted = false;

    public CameraLERP(Camera camera) {
        this.camera = camera;
    }

    public Timeline setUpCameraLERP(Vector2 cameraTarget, TweenCallback callback) {
        cameraLERPStarted = true;
        Vector3 cameraStartPosition = camera.position;
        return Timeline.createSequence()
            .push(SlotPuzzleTween.set(
                    camera,
                    CameraAccessor.POS_XY).
                target(camera.position.x, camera.position.y))
            .push(SlotPuzzleTween.to(
                    camera,
                    CameraAccessor.POS_XY,
                    5.0f).
                target(cameraTarget.x, cameraTarget.y).
                ease(Quart.INOUT))
            .setCallback(callback);
    }

}
