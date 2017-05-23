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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.ellzone.slotpuzzle2d.prototypes.Reels;
import com.ellzone.slotpuzzle2d.prototypes.ReelTiles;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.prototypes.Particles;
import com.ellzone.slotpuzzle2d.physics.Particle;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.physics.DampenedSine;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsEvent;
import com.badlogic.gdx.graphics.Color;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import aurelienribon.tweenengine.equations.Elastic;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Particle6ExtendingParticleTemplate extends ParticleTemplate {
	private static final String PrototypeName = "Particle6";
    private ShapeRenderer shapeRenderer;
	private Random random;
	private Vector2 touch;
	private Reels reels;
	private Sprite[] reelSprites;
	private ReelTiles reelTiles;
	private Array<ReelTile> reelTilesArray;
	private int slotReelScrollheight;
	private Particles particles;
	private Array<Particle> reelParticles;
	private Vector accelerator;
	private int dampPoint;
	private Array<Vector2> points = new Array<Vector2>();
	private Array<DampenedSineParticle> dampenedSines;
	private DampenedSineParticle dampenedSine;
	private float graphStep;
	private TweenManager tweenManager;
	private Vector velocity;
	private float velocityY;
	private float velocityYMin;
	private Vector velocityMin;
	private float acceleratorY;
	private Vector accelerate;
	private float accelerateY;
	private float acceleratorFriction;
	private float velocityFriction;
	private Timeline endReelSeq;
	private Skin skin;
	private Label accelerateYLabel;
	private Label acceleratorYLabel;
	private Label velocityYMinLabel;
	private Label velocityYLabel;
	private Label acceleratorFrictionLabel;
	private Label velocityFrictionLabel;
	private Label fpsLabel;

	@Override
	protected void initialiseOverride() {
		initialiseReelTiles();
		initialiseParticles();
        initialiseDampenedSine();
		shapeRenderer = new ShapeRenderer();
		random = new Random();
		touch = new Vector2();
		initialiseUi();
	}

	@Override
	protected void loadAssetsOverride() {
		reels = new Reels();
        reelSprites = reels.getReels();
	}

	private void initialiseReelTiles() {
		reelTiles = new ReelTiles(reels);
		reelTilesArray = reelTiles.getReelTiles();
		slotReelScrollheight = reelTiles.getReelTileTextureHeight();
	}

	private void initialiseParticles() {
		particles = new Particles(reels, reelTiles);
		reelParticles = particles.getParticles();
		accelerator = particles.getAccelerator();
		dampPoint = particles.getDampoint();
	}

    private void initialiseDampenedSine() {
        velocityY = 4.0f;
        velocity = new Vector(0, velocityY);
        velocityYMin = 2.0f;
        velocityMin = new Vector(0, velocityYMin);
        acceleratorY = 3.0f;
        accelerator = new Vector(0, acceleratorY);
        accelerateY = 2.0f;
        accelerate = new Vector(0, accelerateY);
        acceleratorFriction = 0.97f;
        velocityFriction = 0.97f;
        dampenedSines = new Array<DampenedSineParticle>();
        for (ReelTile reel : reelTilesArray) {
            dampenedSine = new DampenedSineParticle(0, reel.getSy(), 0, 0, 0, velocity, velocityMin, accelerator, accelerate, velocityFriction, acceleratorFriction);
            dampenedSine.setCallback(dsCallback);
            dampenedSine.setCallbackTriggers(SPPhysicsCallback.PARTICLE_UPDATE);
            dampenedSine.setUserData(reel);
            dampenedSines.add(dampenedSine);
        }
        graphStep = 0f;
    }

    private SPPhysicsCallback dsCallback = new SPPhysicsCallback() {
        @Override
        public void onEvent(int type, SPPhysicsEvent source) {
            delegateDSCallback(type, source);
        }
    };

    private void delegateDSCallback(int type, SPPhysicsEvent source) {
        if (type == SPPhysicsCallback.PARTICLE_UPDATE) {
            DampenedSineParticle ds = (DampenedSineParticle)source.getSource();
            ReelTile reel = (ReelTile)ds.getUserData();
            endReelSeq = Timeline.createSequence();
            float endSy = (reel.getEndReel() * reels.getReelHeight()) % slotReelScrollheight;
            reel.setSy(reel.getSy() % (slotReelScrollheight));
            endReelSeq = endReelSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.SCROLL_XY, 5.0f)
										 .target(0f, endSy)
										 .ease(Elastic.OUT)
										 .setCallbackTriggers(TweenCallback.STEP + TweenCallback.END)
										 .setCallback(slowingSpinningCallback)
										 .setUserData(reel));
            endReelSeq = endReelSeq
				.start(tweenManager);
        }
    }

    private TweenCallback slowingSpinningCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            delegateSlowingSpinning(type, source);
        }
    };

    private void delegateSlowingSpinning(int type, BaseTween<?> source) {
        ReelTile reel = (ReelTile)source.getUserData();
        if (type == TweenCallback.END) {
            reel.setSpinning(false);
        } else if (type == TweenCallback.STEP) {
            addGraphPoint(new Vector2(graphStep++ % displayWindowWidth, (displayWindowHeight / 2 + (reel.getSy() % slotReelScrollheight))));
        }
    }

    private void addGraphPoint(Vector2 newPoint) {
        points.add(newPoint);
    }

	private void drawGraphPoint(ShapeRenderer shapeRenderer) {
        if (points.size >= 2) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.YELLOW);
            for (int i = 0; i < points.size - 1; i++) {
                shapeRenderer.line(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            }
            shapeRenderer.end();
        }
    }
	
	private void initialiseUi() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));

        final Slider acceleratorYSlider = new Slider(0, 100, 1, false, skin);
        acceleratorYSlider.setAnimateDuration(0.3f);
        acceleratorYSlider.setValue(acceleratorY);

        final Slider accelerateYSlider = new Slider(0, 100, 1, false, skin);
        accelerateYSlider.setAnimateDuration(0.3f);
        accelerateYSlider.setValue(accelerateY);

        final Slider velocityYMinSlider = new Slider(0, 100, 1, false, skin);
        velocityYMinSlider.setAnimateDuration(0.3f);
        velocityYMinSlider.setValue(velocityYMin);

        final Slider velocityYSlider = new Slider(0, 100, 1, false, skin);
        velocityYSlider.setAnimateDuration(0.3f);
        velocityYSlider.setValue(velocityY);

        final Slider velocityFrictionSlider = new Slider(0, 1, 0.01f, false, skin);
        velocityFrictionSlider.setAnimateDuration(0.3f);
        velocityFrictionSlider.setValue(velocityFriction);

        final Slider acceleratorFrictionSlider = new Slider(0, 1, 0.01f, false, skin);
        acceleratorFrictionSlider.setAnimateDuration(0.3f);
        acceleratorFrictionSlider.setValue(acceleratorFriction);

        accelerateYLabel = new Label("AccelrateY:", skin);
        acceleratorYLabel = new Label("AccelreatorY:", skin);
        velocityYMinLabel = new Label("Velocity Min:", skin);
        velocityYLabel = new Label("VelocityY:", skin);
        acceleratorFrictionLabel = new Label("Accelerator Friction:", skin);
        velocityFrictionLabel = new Label("Velocity Friction:", skin);
        fpsLabel = new Label("fps:", skin);

        Window window = new Window("Prototype Control", skin);
        window.setPosition(Gdx.graphics.getWidth() - 200, Gdx.graphics.getHeight() - 30);
        window.defaults().spaceBottom(10);
        window.add(accelerateYLabel);
        window.row();
        window.add(accelerateYSlider).minWidth(200).fillX().colspan(3);
        window.row();
        window.add(acceleratorYLabel);
        window.row();
        window.add(acceleratorYSlider).minWidth(200).fillX().colspan(3);
        window.row();
        window.add(velocityYMinLabel);
        window.row();
        window.add(velocityYMinSlider).minWidth(200).fillX().colspan(3);
        window.row();
        window.add(velocityYLabel);
        window.row();
        window.add(velocityYSlider).minWidth(200).fillX().colspan(3);
        window.row();
        window.add(acceleratorFrictionLabel);
        window.row();
        window.add(acceleratorFrictionSlider).minWidth(200).fillX().colspan(3);
        window.row();
        window.add(velocityFrictionLabel);
        window.row();
        window.add(velocityFrictionSlider).minWidth(200).fillX().colspan(3);
        window.row();
        window.row();
        window.add(fpsLabel);
        window.pack();
        stage.addActor(window);

        acceleratorYSlider.addListener(new ChangeListener() {
				public void changed (ChangeEvent event, Actor actor) {
					accelerator.setY(acceleratorYSlider.getValue());
					Gdx.app.log(PrototypeName, "acceleratorY: " + accelerator.getY());
				}
			});

        accelerateYSlider.addListener(new ChangeListener() {
				public void changed (ChangeEvent event, Actor actor) {
					accelerate.setY(accelerateYSlider.getValue());
					Gdx.app.log(PrototypeName, "accelerateY: " + accelerate.getY());
				}
			});

        velocityYMinSlider.addListener(new ChangeListener() {
				public void changed (ChangeEvent event, Actor actor) {
					velocityYMin = velocityYMinSlider.getValue();
					Gdx.app.log(PrototypeName, "velocityMin: " + velocityYMin);
				}
			});

        velocityYSlider.addListener(new ChangeListener() {
				public void changed (ChangeEvent event, Actor actor) {
					velocityY = velocityYSlider.getValue();
					Gdx.app.log(PrototypeName, "velocitySlider: " + velocityY);
				}
			});

        acceleratorFrictionSlider.addListener(new ChangeListener() {
				public void changed (ChangeEvent event, Actor actor) {
					acceleratorFriction = acceleratorFrictionSlider.getValue();
					Gdx.app.log(PrototypeName, "acceleratorFrictionSlider: " + acceleratorFriction);
				}
			});

        velocityFrictionSlider.addListener(new ChangeListener() {
				public void changed (ChangeEvent event, Actor actor) {
					velocityFriction = velocityFrictionSlider.getValue();
					Gdx.app.log(PrototypeName, "velocityFrictionSlider: " + velocityFriction);
				}
			});
    }
	
	@Override
	protected void disposeOverride() {
	}

	@Override
	protected void updateOverride(float delta) {
		tweenManager.update(delta);
        int dsIndex = 0;
        for (ReelTile reel : reelTilesArray) {
            dampenedSines.get(dsIndex).update();
            if (dampenedSines.get(dsIndex).getDSState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                reel.setSy(dampenedSines.get(dsIndex).position.y);
            }
            reel.update(delta);
            dsIndex++;
        }
		handleInput(delta);
	}

	public void handleInput(float delta) {
        int dsIndex = 0;
        for (ReelTile reelTile : reelTilesArray) {
            if (Gdx.input.justTouched()) {
                touch = touch.set(Gdx.input.getX(), cam.viewportHeight - Gdx.input.getY());
                if(reelTile.getBoundingRectangle().contains(touch)) {
                    if (reelTile.isSpinning()) {
                        if (dampenedSines.get(dsIndex).getDSState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                            reelTile.setEndReel(reelTile.getCurrentReel());
                        }
                    } else {
                        reelTile.setEndReel(random.nextInt(reelSprites.length - 1));
                        reelTile.setSpinning(true);
                        reelTile.setSy(0);
                        dampenedSines.get(dsIndex).initialiseDampenedSine();
                        dampenedSines.get(dsIndex).position.y = 0;
                        dampenedSines.get(dsIndex).velocity = new Vector(0, velocityY);
                        accelerator = new Vector(0, acceleratorY);
                        dampenedSines.get(dsIndex).accelerator = accelerator;
                        accelerate = new Vector(0, accelerateY);
                        dampenedSines.get(dsIndex).accelerate(accelerate);
                        dampenedSines.get(dsIndex).velocityMin.y = velocityMin.y;
                    }
                }
            }
            dsIndex++;
        }
    }
	
	@Override
	protected void renderOverride(float delta) {
		batch.begin();
        for (ReelTile reelTile : reelTilesArray) {
            reelTile.draw(batch);
			reelSprites[reelTile.getEndReel()].setX(32);
			reelSprites[reelTile.getEndReel()].draw(batch);
		}
        batch.end();
        drawGraphPoint(shapeRenderer);
        accelerateYLabel.setText("AccelerateYMin:" + accelerateY);
        acceleratorYLabel.setText("AcceleratorYMin:" + acceleratorY);
        velocityYMinLabel.setText("VelocityYMin:" + velocityYMin);
        velocityYLabel.setText("VeclocityY: " + velocityY);
        acceleratorFrictionLabel.setText("AcceleratorFriction: " + acceleratorFriction);
        velocityFrictionLabel.setText("VeclocityFriction: " + velocityFriction);

        fpsLabel.setText("fps: " + Gdx.graphics.getFramesPerSecond());
        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        stage.draw();
	}

	@Override
	protected void initialiseUniversalTweenEngineOverride() {
		SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        tweenManager = new TweenManager();
	}
}
