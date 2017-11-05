package com.ellzone.slotpuzzle2d.physics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsManagerCustomBodies {
    BoxBodyBuilder bodyFactory;

    float accumulator;
    boolean isPaused;

    static final float BOX_STEP = 1 / 120f;
    static final float RENDER_STEP = 1 / 40f;
    static final int VELOCITY_ITER = 8;
    static final int POSITION_ITER = 3;

    World world;

    Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;

    public PhysicsManagerCustomBodies(OrthographicCamera cam) {
        this.isPaused = false;
        this.world = new World(new Vector2(0, -5), true);
        this.bodyFactory = new BoxBodyBuilder();

        this.debugMatrix=cam.combined.cpy();
        this.debugMatrix.scale(BoxBodyBuilder.BOX_TO_WORLD, BoxBodyBuilder.BOX_TO_WORLD, 1f);
        this.debugRenderer = new Box2DDebugRenderer();
    }

    public BoxBodyBuilder getBodyFactory() {
        return this.bodyFactory;
    }

    public void createCircleBody(BodyDef.BodyType bodyType, float posx, float posy, float radius) {
        this.bodyFactory.createCircleBody(this.world, bodyType, posx, posy, radius);
    }

    public void createEdgeBody(BodyDef.BodyType bodyType,
                               float v1x,
                               float v1y,
                               float v2x,
                               float v2y) {
        this.bodyFactory.createEdgeBody(this.world, bodyType, v1x, v1y, v2x, v2y);
    }

    public void update(float dt){
        if(!isPaused){
            accumulator += dt;
            while(accumulator > dt){
                world.step(BOX_STEP,VELOCITY_ITER, POSITION_ITER);
                accumulator -= BOX_STEP;
            }
        }
    }

    public void draw(SpriteBatch spriteBatch){
        spriteBatch.begin();
        debugRenderer.render(world, debugMatrix);
        spriteBatch.end();
    }

    public void dispose(){
        debugRenderer.dispose();
        disposeJoints();
        disposeBodies();
        world.dispose();
    }

    private void disposeBodies() {
        //while(world.getBodies().hasNext()){
        //    world.destroyBody(world.getBodies().next());
        //}
    }

    private void disposeJoints() {
        //while(world.getJoints().hasNext()){
        //    world.destroyJoint(world.getJoints().next());
        //}
    }
}
