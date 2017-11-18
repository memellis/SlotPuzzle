package com.ellzone.slotpuzzle2d.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraHelper {

    public static OrthographicCamera GetCamera(float virtualWidth,
                                               float virtualHeight) {
        float viewportWidth = virtualWidth;
        float viewportHeight = virtualHeight;

        OrthographicCamera camera = new OrthographicCamera(viewportWidth,
                viewportHeight);
        camera.position.set(virtualWidth / 2, virtualHeight / 2, 0);
        camera.update();
        return camera;
    }
}
