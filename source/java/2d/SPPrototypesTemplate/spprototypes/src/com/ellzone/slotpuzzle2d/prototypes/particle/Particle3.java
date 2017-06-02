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

package com.ellzone.slotpuzzle2d.prototypes.particle;

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.physics.Particle;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

public class Particle3 extends SPPrototype {

    private static final float MINIMUM_VIEWPORT_SIZE = 15.0f;
    private static final float VELOCITY_MIN = 2.0f;
    private PerspectiveCamera cam;
    private Sprite cheesecake;
    private Sprite cherry;
    private Sprite grapes;
    private Sprite jelly;
    private Sprite lemon;
    private Sprite peach;
    private Sprite pear;
    private Sprite tomato;
    private Sprite[] sprites;
    private int spriteWidth;
    private int spriteHeight;
    private boolean isLoaded;
    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private int slotReelScrollheight;
    private Random random;
    private ReelTile reelTile;
    private Array<ReelTile> reelTiles;
    private SpriteBatch batch;
    private Array<Particle> reelParticles;
    private Particle reelParticle;
    private Vector accelerator;
    private int dampPoint;
    private ShapeRenderer shapeRenderer;
    private Array<Vector2> points = new Array<Vector2>();
    private float graphStep;
    private float savedAmplitude;
    private float savedSy;
    private boolean saveAmplitude;
    private float plotTime;

    @Override
    public void create() {
        loadAssets();
        initialiseReelSlots();
        intialiseParticles();
        initialiseCamera();
        initialiseLibGdx();
        initialiseDampenedSine();
    }

    private void loadAssets() {
        Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();
        isLoaded = true;

        TextureAtlas atlas = Assets.inst().get("reel/reels.pack.atlas", TextureAtlas.class);
        cherry = atlas.createSprite("cherry");
        cheesecake = atlas.createSprite("cheesecake");
        grapes = atlas.createSprite("grapes");
        jelly = atlas.createSprite("jelly");
        lemon = atlas.createSprite("lemon");
        peach = atlas.createSprite("peach");
        pear = atlas.createSprite("pear");
        tomato = atlas.createSprite("tomato");

        sprites = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
        for (Sprite sprite : sprites) {
            sprite.setOrigin(0, 0);
        }
        spriteWidth = (int) sprites[0].getWidth();
        spriteHeight = (int) sprites[0].getHeight();
    }

    private void initialiseReelSlots() {
        random = new Random();
        reelTiles = new Array<ReelTile>();
        slotReelScrollPixmap = new Pixmap(spriteWidth, spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        slotReelScrollheight = slotReelScrollTexture.getHeight();
        reelTile = new ReelTile(slotReelScrollTexture, 0, 32, spriteWidth, spriteHeight, 0, null);
        reelTile.setX(0);
        reelTile.setY(0);
        reelTile.setSx(0);
        reelTile.setSy(0);
        reelTile.setEndReel(random.nextInt(sprites.length - 1));
        reelTiles.add(reelTile);
    }

    private void intialiseParticles() {
        accelerator = new Vector(0, 3f);
        reelParticles = new Array<Particle>();
        reelParticle = new Particle(0, reelTiles.get(0).getSy(), 0.0001f , 0, 0);
        reelParticle.velocity.setX(0);
        reelParticle.velocity.setY(4);
        reelParticle.accelerate(new Vector(0, 2f));
        reelParticles.add(reelParticle);
        dampPoint = slotReelScrollTexture.getHeight() * 20;
    }

    private void reinitialiseParticle(int index) {
        reelTiles.get(0).setSy(0);
        accelerator = new Vector(0, 3f);
        reelParticles.removeIndex(0);
        reelParticle = new Particle(0, reelTiles.get(0).getSy(), 0.0001f , 0, 0);
        reelParticle.velocity.setX(0);
        reelParticle.velocity.setY(4);
        reelParticle.accelerate(new Vector(0, 2f));
        reelParticles.add(reelParticle);
        dampPoint = slotReelScrollTexture.getHeight() * 20;
        saveAmplitude = true;
        savedAmplitude = 0;
        plotTime = 132;
    }

    private float dampenedSine(float initialAmplitude, float lambda, float angularFrequency, float time, float phaseAngle) {
        return (float) (initialAmplitude * Math.exp(-lambda * time) *  Math.cos(angularFrequency * time + phaseAngle));
    }

    private void initialiseCamera() {
        cam = new PerspectiveCamera();
        cam.position.set(0, 0, 10);
        cam.lookAt(0, 0, 0);
    }

    private void initialiseLibGdx() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
    }

    private void initialiseDampenedSine() {
        graphStep = 0;
        savedAmplitude = 0;
        saveAmplitude = true;
        plotTime = 132;
    }

    @Override
    public void resize(int width, int height) {
        float halfHeight = MINIMUM_VIEWPORT_SIZE * 0.5f;
        if (height > width)
            halfHeight *= (float)height / (float)width;
        float halfFovRadians = MathUtils.degreesToRadians * cam.fieldOfView * 0.5f;
        float distance = halfHeight / (float)Math.tan(halfFovRadians);
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.position.set(0, 0, distance);
        cam.lookAt(0, 0, 0);
        cam.update();
    }

    private void update(float delta) {
        for (Particle reelParticle : reelParticles) {
            reelParticle.update();
        }
        for(ReelTile reelSlot : reelTiles) {
            if (reelParticles.get(0).velocity.getY() > Particle3.VELOCITY_MIN) {
                // Particle3.VELOCITY.MIN has a major influence on the endReel
                // Current setting means the natural endReel is 4 (lemon)
                reelParticles.get(0).velocity.mulitplyBy(0.97f);
                reelParticles.get(0).accelerate(accelerator);
                accelerator.mulitplyBy(0.97f);
                reelSlot.setSy(reelParticles.get(0).position.getY());
            } else {
                if (reelSlot.getSy() < dampPoint) {
                    if (saveAmplitude) {
                        saveAmplitude = false;
                        savedSy = reelSlot.getSy() + slotReelScrollheight - (reelSlot.getSy() % slotReelScrollheight);
                        savedAmplitude = (dampPoint - savedSy);
                    }
                    float ds = dampenedSine(savedAmplitude, 1.0f, (float) (3 * Math.PI), plotTime++ / 32, 0);
                    float dsEndReel = ds + reelSlot.getEndReel() * 32;
                    addGraphPoint(new Vector2(graphStep++ % Gdx.graphics.getWidth(), (Gdx.graphics.getHeight() / 2 + dsEndReel)));
                    reelSlot.setSy(savedSy + dsEndReel);
                    if(Math.abs(ds)<0.0000001f) {
                        reinitialiseParticle(0);
                        reelSlot.setEndReel(random.nextInt(sprites.length - 1));
                    }
                }
            }
            reelSlot.update(delta);
        }
    }

    private void addGraphPoint(Vector2 newPoint) {
        points.add(newPoint);
    }

    private void drawGraphPoint(ShapeRenderer shapeRenderer) {
        if (points.size >= 2) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            int rr = random.nextInt(255);
            int rg = random.nextInt(255);
            int rb = random.nextInt(255);
            shapeRenderer.setColor(rr, rg, rb, 255);
            for (int i = 0; i < points.size - 1; i++) {
                shapeRenderer.line(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            }
            shapeRenderer.end();
        }
    }

    @Override
    public void render() {
        final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if(isLoaded) {
            batch.begin();
            for (ReelTile reelSlot : reelTiles) {
                reelSlot.draw(batch);
                sprites[reelSlot.getEndReel()].setX(32);
                sprites[reelSlot.getEndReel()].draw(batch);
            }
            batch.end();
            drawGraphPoint(shapeRenderer);
        }
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        Assets.inst().dispose();
    }
}