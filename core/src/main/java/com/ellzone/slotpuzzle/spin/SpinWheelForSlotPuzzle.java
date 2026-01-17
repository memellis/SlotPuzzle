/*
 *******************************************************************************
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

package com.ellzone.slotpuzzle.spin;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.IntArray;
import com.badlogic.gdx.utils.ObjectMap;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.scaleTo;
import static com.badlogic.gdx.scenes.scene2d.actions.Actions.sequence;

import lombok.Getter;

public class SpinWheelForSlotPuzzle implements SpinWheelSlotPuzzle {
    public static final float PPM = 100f;
    public static final float STANDARD_SIZE = 512F;
    private static final short BIT_PEG = 4;
    private static final short BIT_NEEDLE = 8;
    private static final short BIT_NEEDLE_BODY_LEFT_BASE_CONSTRAINT = 16;
    private static final short BIT_NEEDLE_BODY_RIGHT_BASE_CONSTRAINT = 32;
    public static final String RED = "f04950";
    public static final String ORANGE = "f9a54b";
    public static final String BRIGHT_YELLOW = "fff533";
    public static final String YELLOW = "fece3e";
    public static final String BRIGHT_GREEN = "a3fd39";
    public static final String CYAN = "33b8a5";
    public static final String LIGHT_BLUE = "33a7d8";
    public static final String BLUE = "3276b5";
    public static final String PURPLE = "8869ad";
    public static final String PINK = "b868ad";
    public static final String BRIGHT_PINK = "e966ac";

    private World world;
    private final BodyDef bodyDef = new BodyDef();
    private final FixtureDef fixtureDef = new FixtureDef();
    private final RevoluteJointDef revJointDef = new RevoluteJointDef();          // keep needle and wheel in the place.
    private final DistanceJointDef disJointDef = new DistanceJointDef();          // join needle with external bodies to constrain and keep it in place.

    private Body wheelCore;     // circle shape which join with all pegs.
    private Body wheelBase;     // any shape to join with core of wheel.
    private Body needle;        // polygon shape.
    private Body
        needleBodyCenterBaseConstraint,
        needleBodyLeftBaseConstraint,
        needleBodyRightBaseConstraint;

    private final float diameter;
    private final float x, y;
    private final int nPegs;
    private float distanceOfNeedleFromWheel;
    private Image needleImage;
    private Image wheelImage;
    @Getter
    private Image spinButton;
    private Circle spinButtonCircle;
    private float scaleFactor;
    private float scaledDiameter;

    public SpinWheelForSlotPuzzle(
        float diameter,
        float x,
        float y,
        int nPegs,
        World world) {
        this.diameter = (diameter / STANDARD_SIZE * diameter) / PPM;
        this.x = x / PPM;
        this.y = y / PPM;
        this.nPegs = nPegs;

        this.world = world;
        this.world.setContactListener(new SpinWheelWorldContact());

        createWheel();
        createNeedle();
    }

    @Override
    public void setUpSpinWheel() {
        final TextureAtlas spinWheelAtlas =
            new TextureAtlas("spin/spin_wheel_ui.atlas");
        setUpSpinWheelBody(spinWheelAtlas);
        spinButton = setUpSpinWheelSpinButton(spinWheelAtlas);
        setUpSpinWheelNeedleBody(spinWheelAtlas);
        setElementData();
    }

    @Override
    public void setUpSpinWheel(Stage stage) {
        final TextureAtlas spinWheelAtlas =
            new TextureAtlas("spin/spin_wheel_ui.atlas");

        setUpSpinWheelBody(spinWheelAtlas, stage);
        setUpSpinWheelSpinButton(spinWheelAtlas, stage);
        setUpSpinWheelNeedleBody(spinWheelAtlas, stage);
        setElementData();
    }

    @Override
    public Image getWheelImage() {
        return wheelImage;
    }

    @Override
    public Image getNeedleImage() {
        return needleImage;
    }

    @Override
    public void updateCoordinates(Body body, Image image, float incX, float incY) {
        image.setPosition(
            body.getPosition().x + (incX / SpinWheel.PPM),
            body.getPosition().y + (incY / SpinWheel.PPM),
            Align.center);
        image.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
    }

    /**
     *
     * @param omega spin impulse the angular in units of kg*m*m/s. The Maximum value is 30 to avoid needle to slip from joints.
     */
    @Override
    public void spin(float omega) {
        wheelCore.setAngularVelocity(MathUtils.clamp(omega, 0, 30));
    }

    @Override
    public boolean spinningStopped() {
        return !wheelCore.isAwake();
    }

    @Override
    public void setWorldContactListener(ContactListener listener) {
        world.setContactListener(listener);
    }

    @Override
    public void setWorld(World world) {
        this.world = world;
    }

    @Override
    public Body getWheelBody() {
        return wheelCore;
    }

    @Override
    public Body getNeedleBody() {
        return needle;
    }

    /**
     * @return center needle rotation value of X (NOT center X of the needle shape.) according given width.
     */
    @Override
    public float getNeedleCenterX(float needleWidth) {
        return needleWidth / 2;
    }

    @Override
    public float getNeedleCenterY(float needleHeight) {
        return 3 * needleHeight / 4;
    }

    /**
     * @param elements contains all data with known their objects which have two pegs numbers for each object.
     */
    @Override
    public void setElements(ObjectMap<IntArray, Object> elements) {
        this.elements = elements;
    }

    /**
     * @param object is between data (two numbers of pegs as known)
     * @param data   two numbers of pegs.
     */
    @Override
    public void addElementData(Object object, IntArray data) {
        elements.put(data, object);
    }

    /**
     * @return object between that two pegs.
     */
    @Override
    public Object getLuckyWinElement() {
        if (pegsSelectors.size > 0)
            for (IntArray array : elements.keys())
                if (array.contains(pegsSelectors.get(0)) && array.contains(pegsSelectors.get(1)))
                    return elements.get(array);
        return null;
    }

    public boolean isInsideSpinButton(Vector2 point) {
        return spinButtonCircle.contains(point);
    }

    private void setUpSpinWheelNeedleBody(TextureAtlas atlas) {
        getNeedleBody().setUserData(
            needleImage = new Image(new Sprite(atlas.findRegion("needle"))));
        needleImage.setWidth((needleImage.getWidth() * (diameter / 7.5f))  / SpinWheel.PPM);
        needleImage.setHeight((needleImage.getHeight() * (diameter / 7.5f)) / SpinWheel.PPM);
        updateCoordinates(getNeedleBody(), needleImage, 0, -25F);
        needleImage.setOrigin(
            getNeedleCenterX(needleImage.getWidth()), getNeedleCenterY(needleImage.getHeight()));
    }

    private void setUpSpinWheelNeedleBody(TextureAtlas atlas, Stage stage) {
        setUpSpinWheelNeedleBody(atlas);
        stage.addActor(needleImage);
    }

    private Image setUpSpinWheelSpinButton(TextureAtlas atlas) {
        final Image spinButton = new Image(atlas.findRegion("spin_button"));
        spinButton.setOrigin(Align.center);
        spinButton.setWidth((spinButton.getWidth() * (diameter / 7.5f))  / SpinWheel.PPM);
        spinButton.setHeight((spinButton.getHeight() * (diameter / 7.5f)) / SpinWheel.PPM);
        spinButton.setPosition(x, y, Align.center);
        spinButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                spinButton.addAction(
                    sequence(scaleTo(1.25F, 1.25F, 0.10F),
                        scaleTo(1F, 1F, 0.10F)));
                spin(MathUtils.random(5F, 30F));
            }
        });
        spinButtonCircle = new Circle(
            x, y, spinButton.getWidth() * diameter / 7.5f);
        return spinButton;
    }

    private void setUpSpinWheelSpinButton(TextureAtlas atlas, Stage stage) {
        stage.addActor(setUpSpinWheelSpinButton(atlas));
    }

    private void setUpSpinWheelBody(TextureAtlas atlas, Stage stage) {
        setUpSpinWheelBody(atlas);
        stage.addActor(wheelImage);
    }

    private void setUpSpinWheelBody(TextureAtlas atlas) {
        getWheelBody().setUserData(wheelImage = new Image(atlas.findRegion("spin_wheel")));
        wheelImage.setWidth(diameter);
        wheelImage.setHeight(diameter);
        updateCoordinates(getWheelBody(), wheelImage, 0, 0);
        wheelImage.setOrigin(Align.center);
    }

    private void setElementData() {
        addElementData(Color.valueOf(BRIGHT_PINK), getData(2, 1));
        addElementData(Color.valueOf(PINK), getData(3, 2));
        addElementData(Color.valueOf(PURPLE), getData(4, 3));
        addElementData(Color.valueOf(BLUE), getData(5, 4));
        addElementData(Color.valueOf(LIGHT_BLUE), getData(6, 5));
        addElementData(Color.valueOf(CYAN), getData(7, 6));
        addElementData(Color.valueOf(BRIGHT_GREEN), getData(8, 7));
        addElementData(Color.valueOf(BRIGHT_YELLOW), getData(9, 8));
        addElementData(Color.valueOf(YELLOW), getData(10, 9));
        addElementData(Color.valueOf(ORANGE), getData(11,10));
        addElementData(Color.valueOf(RED), getData(1, 12));
    }

    private IntArray getData(int peg_1, int peg_2) {
        IntArray array = new IntArray(2);
        array.addAll(peg_1, peg_2);
        return array;
    }

    private void createWheel() {
        baseOfWheel();
        coreOfWheel();
        pegsOfWheel();
        revolutionJointBaseAndCoreOfWheel();
    }

    private void createNeedle() {
        coreOfNeedle(
            30F * (diameter / STANDARD_SIZE),
            80F * (diameter / STANDARD_SIZE));

        centreBaseOfCnNeedle();
        jointBaseOfNeedleWithConstraintNeedle();
        leftBaseOfConstraintJointWithUpNeedle();
        rightBaseOfConstraintJointWithUpNeedle();
        jointLeftBaseConstraintRightBaseConstraintWithUpNeedle();
    }

    /**
     * The base of wheel is a static body with as a square shape.
     */
    private void baseOfWheel() {
        PolygonShape polygon = new PolygonShape();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x, y);
        wheelBase = world.createBody(bodyDef);
        setTheShapeOfBase(polygon);
        wheelBase.createFixture(fixtureDef);
        polygon.dispose();
    }

    private void setTheShapeOfBase(PolygonShape polygon) {
        polygon.setAsBox(32 / PPM, 32 / PPM);
        fixtureDef.shape = polygon;
    }

    /**
     * The core of wheel is a dynamic body as a circle shape
     */
    private void coreOfWheel() {
        CircleShape circle = new CircleShape();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        setToStopAfterSpinning();
        wheelCore = world.createBody(bodyDef);
        setTheShapeOfBase(circle);
        setShapePhysicProperties(0.25f, 0.25f);
        wheelCore.createFixture(fixtureDef);
        circle.dispose();
    }

    private void setShapePhysicProperties(float density, float friction) {
        fixtureDef.density = density;
        fixtureDef.friction = friction;
    }

    private void setTheShapeOfBase(CircleShape circle) {
        circle.setRadius(diameter / 2);
        fixtureDef.shape = circle;
    }

    private void setToStopAfterSpinning() {
        bodyDef.angularDamping = 0.25f;
        bodyDef.position.set(x, y);
    }

    /**
     * The pegs of wheel allowing the needle to collide only with the pegs and not the wheel.
     **/
    private void pegsOfWheel() {
        if (nPegs == 0)
            return;

        CircleShape circle = new CircleShape();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(x, y);
        setShapePhysicProperties(0.0f, 0.0f);
        setCategoryBitsToCollideWithNeedle();
        defineWheelPegs(circle);
        circle.dispose();
    }

    private void setCategoryBitsToCollideWithNeedle() {
        setCategoryBitsToCollideWithNeedle(BIT_PEG, BIT_NEEDLE);
    }

    private void defineWheelPegs(CircleShape circle) {
        for (int i = 0; i < nPegs; i++) {
            double theta = Math.toRadians((360.0f / nPegs) * i);
            float x = (float) Math.cos(theta);
            float y = (float) Math.sin(theta);

            setThePegPosition(circle, x, y);
            setTheShapeOfPegs(circle);
            Fixture fixture = createBaseFixture();
            setUserDataAsPegNumberAsIndicatorToLuckyElement(i, fixture);
        }
    }

    private Fixture createBaseFixture() {
        return wheelCore.createFixture(fixtureDef);
    }

    private void setUserDataAsPegNumberAsIndicatorToLuckyElement(int i, Fixture fixture) {
        fixture.setUserData((i + 1));
    }

    private void setThePegPosition(CircleShape circle, float x, float y) {
        circle.setPosition(
            circle.getPosition().set(x * diameter / 2, y * diameter / 2).scl(0.90f));
    }

    private void setTheShapeOfPegs(CircleShape circle) {
        circle.setRadius(12 * (diameter / STANDARD_SIZE) / 2);
        fixtureDef.shape = circle;
    }

    /**
     * Left static body to constrain and keep needle in the center.
     */
    private void leftBaseOfConstraintJointWithUpNeedle() {
        CircleShape circle = new CircleShape();

        circle.setRadius(4 * (diameter / STANDARD_SIZE));
        fixtureDef.shape = circle;

        bodyDef.type = BodyDef.BodyType.StaticBody;

        // needs to be true; the reason for this is that the pegs can move very quickly
        // when the wheel is spinning fast and sometimes the peg-needle collision will
        // be missed by box2d. If the isBullet flag is set, box2d resolves those cases
        // correctly.
        bodyDef.bullet = true;

        bodyDef.position.set(x - 40F * (diameter / STANDARD_SIZE), y + distanceOfNeedleFromWheel);

        needleBodyLeftBaseConstraint = world.createBody(bodyDef);

        setShapePhysicsProperties();
        setCategoryBitsToCollideWithNeedle(BIT_NEEDLE_BODY_LEFT_BASE_CONSTRAINT, BIT_NEEDLE);
        needleBodyLeftBaseConstraint.createFixture(fixtureDef);
        circle.dispose();
    }

    private void setCategoryBitsToCollideWithNeedle(short bits, short bitNeedle) {
        fixtureDef.filter.categoryBits = bits;
        fixtureDef.filter.maskBits = bitNeedle;
    }

    private void setShapePhysicsProperties() {
        fixtureDef.density = 1f;
        fixtureDef.restitution = 1f;
        fixtureDef.friction = 1f;
    }

    /**
     * Right static body to constrain and keep needle in the center.
     */
    private void rightBaseOfConstraintJointWithUpNeedle() {
        CircleShape circle = new CircleShape();

        circle.setRadius(4 * (diameter / STANDARD_SIZE));
        fixtureDef.shape = circle;

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.bullet = true;

        bodyDef.position.set(x + 40F * (diameter / STANDARD_SIZE), y + distanceOfNeedleFromWheel);

        needleBodyRightBaseConstraint = world.createBody(bodyDef);
        setShapePhysicsProperties();
        setCategoryBitsToCollideWithNeedle(BIT_NEEDLE_BODY_RIGHT_BASE_CONSTRAINT, BIT_NEEDLE);
        needleBodyRightBaseConstraint.createFixture(fixtureDef);
        circle.dispose();
    }

    /**
     * Center static body to join needle with it by joint.
     */
    private void centreBaseOfCnNeedle() {
        CircleShape circle = new CircleShape();

        circle.setRadius(4 * (diameter / STANDARD_SIZE));
        fixtureDef.shape = circle;

        bodyDef.type = BodyDef.BodyType.StaticBody;

        bodyDef.position.set(x, y + distanceOfNeedleFromWheel + 5F * (diameter / STANDARD_SIZE));

        needleBodyCenterBaseConstraint = world.createBody(bodyDef);
        fixtureDef.density = 0.0f;
        needleBodyCenterBaseConstraint.createFixture(fixtureDef);

        circle.dispose();
    }

    /**
     * The needle is a body with a single fixture in the shape of a polygon as a kite shape.
     */
    private void coreOfNeedle(float needleWidth, float needleHeight) {
        PolygonShape polygon = new PolygonShape();

        float[] vertices = {-needleWidth / 2, 0f, 0f, needleHeight / 4, needleWidth / 2, 0f, 0f, -3 * needleHeight / 4};
        polygon.set(vertices);
        fixtureDef.shape = polygon;

        bodyDef.type = BodyDef.BodyType.DynamicBody;

        distanceOfNeedleFromWheel = diameter / 1.95f;

        bodyDef.position.set(x, y + distanceOfNeedleFromWheel);

        bodyDef.bullet = true;
        bodyDef.angularDamping = 0.25f;
        setShapePhysicProperties(1f, 0.0f);

        needle = world.createBody(bodyDef);

        setCategoryBitsToCollideWithNeedle(
            BIT_NEEDLE,
            (short) (BIT_PEG |
                BIT_NEEDLE_BODY_LEFT_BASE_CONSTRAINT |
                BIT_NEEDLE_BODY_RIGHT_BASE_CONSTRAINT));

        needle.createFixture(fixtureDef);

        polygon.dispose();
    }

    /**
     * attach wheel to its base via a revolution joint. This joint allows the wheel to spin freely about the center.
     */
    private void revolutionJointBaseAndCoreOfWheel() {
        revJointDef.bodyA = wheelBase;
        revJointDef.bodyB = wheelCore;
        revJointDef.collideConnected = false;
        world.createJoint(revJointDef);
    }

    /**
     * keep needle in the center position with two distances joint connected by two bodies
     * left constraint body and right constraint body.
     */
    private void jointLeftBaseConstraintRightBaseConstraintWithUpNeedle() {
        disJointDef.bodyA = needleBodyLeftBaseConstraint;
        disJointDef.bodyB = needle;
        disJointDef.localAnchorB.set(0, 15 / PPM);
        disJointDef.length =
            (float) Math.sqrt(Math.pow(40F * (diameter / STANDARD_SIZE), 2) +
                Math.pow(15 / PPM + 5F * (diameter / STANDARD_SIZE), 2));
        disJointDef.collideConnected = true;
        world.createJoint(disJointDef);

        disJointDef.bodyA = needleBodyRightBaseConstraint;
        disJointDef.bodyB = needle;
        disJointDef.collideConnected = true;
        world.createJoint(disJointDef);
    }

    // The needle base rotate about the revolution joint.
    private void jointBaseOfNeedleWithConstraintNeedle() {
        revJointDef.bodyA = needleBodyCenterBaseConstraint;
        revJointDef.bodyB = needle;
        world.createJoint(revJointDef);
    }


    // contains two pegs (as a number which saved in UserData) with the object between them.
    private final IntArray pegsSelectors = new IntArray(2);
    // to connect between data (two pegs numbers) and object which this lucky element.
    private ObjectMap<IntArray, Object> elements = new ObjectMap<>();

    private final class SpinWheelWorldContact implements ContactListener {
        @Override
        public void beginContact(Contact contact) {
        }

        @Override
        public void endContact(Contact contact) {
            Object data = contact.getFixtureB().getUserData();
            if (data == null) return;

            int nPeg = (Integer) data, peg2;

            if (wheelCore.getAngularVelocity() <= 0)
                peg2 = addBeforePeg(nPeg);
            else
                peg2 = addAfterPeg(nPeg);

            pegsSelectors.clear();
            pegsSelectors.addAll(nPeg, peg2);
        }

        private int addAfterPeg(int nPeg) {
            int after = nPeg - 1;
            if (after == 0)
                after = nPegs;
            return after;
        }

        private int addBeforePeg(int nPeg) {
            int before = nPeg + 1;
            if (before == nPegs)
                before = 0;
            return before;
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {
        }
    }
}
