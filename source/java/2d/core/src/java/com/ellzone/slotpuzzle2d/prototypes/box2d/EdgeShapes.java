package com.ellzone.slotpuzzle2d.prototypes.box2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ellzone.slotpuzzle2d.camera.CameraHelper;
import com.ellzone.slotpuzzle2d.camera.CameraSettings;
import com.ellzone.slotpuzzle2d.physics.PhysicsManager;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;

public class EdgeShapes extends SPPrototype {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    PhysicsManager physics;

    @Override
    public void create() {
        camera = CameraHelper.GetCamera(CameraSettings.VIRTUAL_WIDTH, CameraSettings.VIRTUAL_HEIGHT);
        batch = new SpriteBatch();

        physics=new PhysicsManager(camera);
    }

    @Override
    public void dispose() {

        physics.Dispose();
        batch.dispose();
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        Update(Gdx.graphics.getDeltaTime());


        physics.Draw(batch);
    }

    private void Update(float dt) {
        physics.Update(dt);
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }
}
