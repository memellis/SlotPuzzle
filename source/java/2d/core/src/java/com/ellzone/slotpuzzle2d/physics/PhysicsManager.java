package com.ellzone.slotpuzzle2d.physics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

public class PhysicsManager {
    BoxBodyBuilder bodyFactory;


    Body edge;
    Body ball;

    float accumulator;
    boolean isPaused;

    static final float BOX_STEP = 1 / 120f;
    static final float RENDER_STEP = 1 / 40f;
    static final int VELOCITY_ITER = 8;
    static final int POSITION_ITER = 3;

    World world;

    Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;

    ShapeRenderer shapeRenderer;

    public PhysicsManager(OrthographicCamera cam){
        isPaused=false;
        world=new World(new Vector2(0,-5), true);

        debugMatrix=cam.combined.cpy();
        debugMatrix.scale(BoxBodyBuilder.BOX_TO_WORLD, BoxBodyBuilder.BOX_TO_WORLD, 1f);
        debugRenderer=new Box2DDebugRenderer();

        bodyFactory=new BoxBodyBuilder();
        ball=bodyFactory.CreateCircleBody(world, BodyDef.BodyType.DynamicBody, 120, 300,4);
        edge=bodyFactory.CreateEdgeBody(world, BodyDef.BodyType.StaticBody,100,290,260,280);
        edge=bodyFactory.CreateEdgeBody(world, BodyDef.BodyType.StaticBody,330,280,180,250);
        edge=bodyFactory.CreateEdgeBody(world, BodyDef.BodyType.StaticBody,65,255,260,215);
        edge=bodyFactory.CreateEdgeBody(world, BodyDef.BodyType.StaticBody,400,240,170,170);
        edge=bodyFactory.CreateEdgeBody(world, BodyDef.BodyType.StaticBody,5,190,290,95);
        edge=bodyFactory.CreateEdgeBody(world, BodyDef.BodyType.StaticBody,290,95,290,70);
        edge=bodyFactory.CreateEdgeBody(world, BodyDef.BodyType.StaticBody,315,95,315,70);
        edge=bodyFactory.CreateEdgeBody(world, BodyDef.BodyType.StaticBody,290,70,315,70);
        shapeRenderer=new ShapeRenderer();
    }




    public void Update(float dt){
        if(!isPaused){
            accumulator+=dt;
            while(accumulator>dt){
                world.step(BOX_STEP,VELOCITY_ITER, POSITION_ITER);
                accumulator-=BOX_STEP;
            }
        }

    }

    public void Draw(SpriteBatch sp){
        sp.begin();
        debugRenderer.render(world, debugMatrix);
        sp.end();
    }



    public void Dispose(){
        //DISPOSE JOINTS FIRST
        debugRenderer.dispose();
        DisposeJoints();
        DisposeBodies();
        world.dispose();
    }

    private void DisposeBodies() {
        // TODO Auto-generated method stub
        //while(world.getBodies().hasNext()){
        //    world.destroyBody(world.getBodies().next());
        //}
    }

    private void DisposeJoints() {
        // TODO Auto-generated method stub
        //while(world.getJoints().hasNext()){
        //    world.destroyJoint(world.getJoints().next());
        //}
    }
}
