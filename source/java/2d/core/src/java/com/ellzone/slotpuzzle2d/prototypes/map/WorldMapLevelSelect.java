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

package com.ellzone.slotpuzzle2d.prototypes.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.Level;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.ScreenshotFactory;

public class WorldMapLevelSelect extends SPPrototype {

    public class MapLevel1 extends Level {
        @Override
        public void initialise() {
        }

        @Override
        public String getImageName() {
            return "MapTile";
        }

        @Override
        public InputProcessor getInput() {
            return null;
        }

        @Override
        public String getTitle() {
            String title = "World 1 Level 1";
            return title;
        }

        @Override
        public void dispose() {
        }

		@Override
		public int getLevelNumber() {
			return 1;
		}
    }

    public static final String LOG_TAG = "SlotPuzzle_WorldScreen";
    private OrthographicCamera cam;
    private SpriteBatch batch;
    private TiledMap worldMap;
    private OrthogonalTiledMapRenderer renderer;
    private BitmapFont font;
    private GestureDetector gestureDetector;
    private MapGestureListener mapGestureListener;
    private Array<Rectangle> levelDoors;
    private float w, h, cww, cwh, aspectRatio;
    private float screenOverCWWRatio, screenOverCWHRatio;
    private Pixmap levelDoorPixmap;
    private Texture levelDoorTexture;
    private Sprite levelDoorSprite;
    private TextureAtlas tilesAtlas;
    private MapTile mapTile, selectedTile;
    private TweenManager tweenManager;
    private int mapWidth, mapHeight, tilePixelWidth, tilePixelHeight;

    @Override
    public void create() {
        loadAssets();
        getAssets();
        loadWorld();
        initialiseCamera();
        initialiseLibGdx();
        initialiseUniversalTweenEngine();
    }

    private void loadAssets() {
        Assets.inst().setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        Assets.inst().load("levels/WorldMap.tmx", TiledMap.class);
        Assets.inst().load("tiles/tiles.pack.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();
    }

    private void getAssets() {
        worldMap = Assets.inst().get("levels/WorldMap.tmx");
        tilesAtlas = Assets.inst().get("tiles/tiles.pack.atlas", TextureAtlas.class);
    }

    private void initialiseCamera() {
        w = Gdx.graphics.getWidth();
        h = Gdx.graphics.getHeight();
        aspectRatio = w / h;
        cam = new OrthographicCamera();
        cam.setToOrtho(false, aspectRatio * 10, 10);
        cam.zoom = 2;
        cam.update();
        cww = cam.viewportWidth * cam.zoom * tilePixelWidth;
        cwh = cam.viewportHeight * cam.zoom * tilePixelHeight;
        screenOverCWWRatio = w / cww;
        screenOverCWHRatio = h / cwh;

    };

    private void initialiseLibGdx() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        renderer = new OrthogonalTiledMapRenderer(worldMap, 1f / 40f);

        mapGestureListener = new MapGestureListener(cam);
        gestureDetector = new GestureDetector(2, 0.5f, 2, 0.15f, mapGestureListener);
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(gestureDetector);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void initialiseUniversalTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        tweenManager = new TweenManager();
    }

    private void loadWorld() {
        getMapProperties();
        levelDoors = new Array<Rectangle>();
        for (MapObject mapObject : worldMap.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            levelDoors.add(mapRectangle);
        }
    }

    private void getMapProperties() {
        MapProperties worldProps = worldMap.getProperties();
        mapWidth = worldProps.get("width", Integer.class);
        mapHeight = worldProps.get("height", Integer.class);
        tilePixelWidth = worldProps.get("tilewidth", Integer.class);
        tilePixelHeight = worldProps.get("tileheight", Integer.class);
    }

    private void createPopUps(Sprite mapTileSprite) {
        Level level1 = new MapLevel1();
        mapTile = new MapTile(20, 20, 200, 200, level1, tilesAtlas, cam, font, tweenManager, mapTileSprite);
    }

    private final TweenCallback maximizeCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            selectedTile = (MapTile) source.getUserData();
            selectedTile.getLevel().initialise();
        }
    };

    @Override
    public void resize(int width, int height) {
    }

    public void update(float delta) {
        tweenManager.update(delta);
    }

    @Override
    public void render() {
        final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        update(delta);
        cam.update();
        mapGestureListener.update();
        renderer.setView(cam);
        renderer.render();
        batch.begin();
        if (mapTile != null) {
            mapTile.draw(batch);
        }
        font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
        batch.end();
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        font.dispose();
        batch.dispose();
        Assets.inst().dispose();
    }

    public class MapGestureListener implements GestureListener {

        private final OrthographicCamera camera;
        float velX, velY;
        boolean flinging = false;
        float initialScale = 1;

        public MapGestureListener(OrthographicCamera camera) {
            this.camera = camera;
        }

        @Override
        public boolean touchDown(float x, float y, int pointer, int button) {
            flinging = false;
            initialScale = camera.zoom;
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            float wx = screenXToWorldX(x);
            float wy = screenYToWorldY(y);
            for (Rectangle levelDoor : levelDoors) {
                if (levelDoor.contains(wx, wy)) {
                    int sx = (int)worldXToScreenX(levelDoor.x);
                    int sy = (int)worldYToScreenY(levelDoor.y);
                    int sw = (int)(levelDoor.width * screenOverCWWRatio);
                    int sh = (int)(levelDoor.height * screenOverCWHRatio);
                    levelDoorPixmap = ScreenshotFactory.getScreenshot(sx, sy, sw, sh, true);
                    levelDoorTexture = new Texture(levelDoorPixmap);
                    levelDoorSprite = new Sprite(levelDoorTexture);
                    levelDoorSprite.setPosition(sx, sy);
                    createPopUps(levelDoorSprite);
                    mapTile.maximize(maximizeCallback);
                    System.out.println("Found level door at ("+wx+","+wy+")");
                    Gdx.app.log(LOG_TAG, "ldx="+levelDoor.x);
                    Gdx.app.log(LOG_TAG, "ldy="+levelDoor.y);
                    Gdx.app.log(LOG_TAG, "x="+x);
                    Gdx.app.log(LOG_TAG, "y="+y);
                    Gdx.app.log(LOG_TAG, "w="+w);
                    Gdx.app.log(LOG_TAG, "h="+h);
                    Gdx.app.log(LOG_TAG, "sx="+sx);
                    Gdx.app.log(LOG_TAG, "sy="+sy);
                    Gdx.app.log(LOG_TAG, "wx="+wx);
                    Gdx.app.log(LOG_TAG, "wy="+wy);
                    Gdx.app.log(LOG_TAG, "cx="+camera.position.x);
                    Gdx.app.log(LOG_TAG, "cy="+camera.position.y);
                    Gdx.app.log(LOG_TAG, "cvpw="+camera.viewportWidth);
                    Gdx.app.log(LOG_TAG, "cvp="+camera.viewportHeight);
                    Gdx.app.log(LOG_TAG, "aspectRatio="+aspectRatio);
                    Gdx.app.log(LOG_TAG, "screenOverCWWRatio="+screenOverCWWRatio);
                    Gdx.app.log(LOG_TAG, "screenOverCWHRatio="+screenOverCWHRatio);

                }
            }
            return false;
        }

        @Override
        public boolean longPress(float x, float y) {
            System.out.println("longPress");
            return false;
        }

        @Override
        public boolean fling(float velocityX, float velocityY, int button) {
            flinging = true;
            velX = camera.zoom * velocityX * 0.1f;
            velY = camera.zoom * velocityY * 0.1f;
            return false;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            camera.position.add(-deltaX * camera.zoom * 0.1f, deltaY * camera.zoom * 0.1f, 0);
            clampCamera();
            return false;
        }

        @Override
        public boolean panStop(float x, float y, int pointer, int button) {
            return false;
        }

        @Override
        public boolean zoom (float originalDistance, float currentDistance){
            float ratio = originalDistance / currentDistance;
            camera.zoom = initialScale * ratio;
            return false;
        }

        @Override
        public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
            return false;
        }

        public void update () {
            if (flinging) {
                velX *= 0.98f;
                velY *= 0.98f;
                camera.position.add(-velX * Gdx.graphics.getDeltaTime(), velY * Gdx.graphics.getDeltaTime(), 0);
                clampCamera();
                if (Math.abs(velX) < 0.01f) velX = 0;
                if (Math.abs(velY) < 0.01f) velY = 0;
            }
        }

        public void clampCamera() {
            if (camera.position.x < 0) {
                camera.position.x = 0;
            }
            if (camera.position.x > 100) {
                camera.position.x = 100;
            }
            if (camera.position.y < 0) {
                camera.position.y = 0;
            }
            if (camera.position.y > 400) {
                camera.position.y = 400;
            }
        }

        private float screenXToWorldX(float x) {
            return ((camera.position.x - aspectRatio * 10) * tilePixelWidth) + (x / screenOverCWWRatio);
        }

        private float screenYToWorldY(float y) {
            return ((camera.position.y - 10) * tilePixelHeight) + ((h - y) / screenOverCWHRatio);
        }

        private float worldXToScreenX(float wx) {
            return (wx  - ((camera.position.x - aspectRatio * 10) * tilePixelWidth)) * screenOverCWWRatio;
        }

        private float worldYToScreenY(float wy) {
            return (wy - ((camera.position.y - 10) * tilePixelHeight)) * screenOverCWHRatio;
        }
    }
}