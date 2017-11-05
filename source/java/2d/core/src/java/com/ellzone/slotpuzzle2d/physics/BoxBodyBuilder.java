package com.ellzone.slotpuzzle2d.physics;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class BoxBodyBuilder {

    public static float WORLD_TO_BOX=0.01f;
    public static float BOX_TO_WORLD=100f;

    static float ConvertToBox(float x){
        return x*WORLD_TO_BOX;
    }

    static float ConvertToWorld(float x){
        return x*BOX_TO_WORLD;
    }

    public Body CreateCircleBody(World world, BodyDef.BodyType bodyType, float posx, float posy,
                                 float radius){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;
        bodyDef.position.set(ConvertToBox(posx),ConvertToBox(posy));
        bodyDef.angle=0;

        Body body = world.createBody(bodyDef);
        MakeCircleBody(body,radius,bodyType,1,0,0,1);
        return body;
    }

    void MakeCircleBody(Body body,float radius,BodyDef.BodyType bodyType,
                        float density,float restitution,float angle,float friction){

        FixtureDef fixtureDef=new FixtureDef();
        fixtureDef.density=density;
        fixtureDef.restitution=restitution;
        fixtureDef.friction=friction;
        fixtureDef.shape=new CircleShape();
        fixtureDef.shape.setRadius(ConvertToBox(radius));

        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();
    }

    public Body CreateEdgeBody(World world,BodyDef.BodyType bodyType,
                               float v1x, // X1 WORLD COORDINATE
                               float v1y, // Y1 WORLD COORDINATE
                               float v2x, // X2 WORLD COORDINATE
                               float v2y  // Y2 WORLD COORDINATE
    ){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = bodyType;

        //CALCULATE CENTER OF LINE SEGMENT
        float posx=(v1x+v2x)/2f;
        float posy=(v1y+v2y)/2f;

        //CALCULATE LENGTH OF LINE SEGMENT
        float len=(float) Math.sqrt((v1x-v2x)*(v1x-v2x)+(v1y-v2y)*(v1y-v2y));

        //CONVERT CENTER TO BOX COORDINATES
        float bx=ConvertToBox(posx);
        float by=ConvertToBox(posy);
        bodyDef.position.set(bx,by);
        bodyDef.angle=0;

        Body body = world.createBody(bodyDef);

        //ADD EDGE FIXTURE TO BODY
        MakeEdgeShape(body,len,bodyType,1,0,1);

        //CALCULATE ANGLE OF THE LINE SEGMENT
        body.setTransform(bx, by, MathUtils.atan2(v2y-v1y, v2x-v1x));

        return body;
    }

    void MakeEdgeShape(Body body,float len,BodyDef.BodyType bodyType,
                       float density,float restitution,float friction){
        FixtureDef fixtureDef=new FixtureDef();
        fixtureDef.density=density;
        fixtureDef.restitution=restitution;
        fixtureDef.friction=friction;

        EdgeShape es=new EdgeShape();
        //SET LENGTH IN BOX COORDINATES
        float boxLen=ConvertToBox(len);
        //SETTING THE POINTS AS OFFSET DISTANCE FROM CENTER
        es.set(-boxLen/2f,0,boxLen/2f,0);
        fixtureDef.shape=es;

        body.createFixture(fixtureDef);
        fixtureDef.shape.dispose();
    }
}
