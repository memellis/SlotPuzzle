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
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.Level;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.sprites.LevelEntrance;
import com.ellzone.slotpuzzle2d.sprites.ScrollSign;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.ScreenshotFactory;
import org.jrenner.smartfont.SmartFontGenerator;
import java.io.IOException;
import java.nio.ByteBuffer;

import static com.badlogic.gdx.scenes.scene2d.ui.Table.Debug.cell;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.ellzone.slotpuzzle2d.level.LevelDoor;

public class WorldMapDynamicDoors extends SPPrototype {

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
    public static final String LIBERATION_MONO_REGULAR_FONT_NAME = "LiberationMono-Regular.ttf";
    public static final String GENERATED_FONTS_DIR = "generated-fonts/";
    public static final String FONT_SMALL = "exo-small";
    public static final int FONT_SMALL_SIZE = 24;
    public static final int SIGN_WIDTH = 96;
    public static final int SIGN_HEIGHT = 32;
    public static final String LEVEL_TEXT = "Level";
    public static final String ENTRANCE_TEXT = "Entrance";
    public static final char SPACE = ' ';
    private static final String WORLD_MAP_LEVEL_DOORS = "Level Doors";

    private OrthographicCamera cam;
    private SpriteBatch batch;
    private TiledMap worldMap;
    private TiledMapTileLayer layer;
    private OrthogonalTiledMapRenderer renderer;
    private BitmapFont font;
    private GestureDetector gestureDetector;
    private MapGestureListener mapGestureListener;
    private Array<LevelDoor> levelDoors;
    private float w, h, cww, cwh, aspectRatio;
    private float screenOverCWWRatio, screenOverCWHRatio;
    private Pixmap levelDoorPixmap;
    private Texture levelDoorTexture;
    private Sprite levelDoorSprite;
    private TextureAtlas tilesAtlas;
    private MapTile mapTile, selectedTile;
    private TweenManager tweenManager;
    private int tilePixelWidth, tilePixelHeight;
    private BitmapFont fontSmall;
    private Array<ScrollSign> scrollSigns;
    private Array<LevelEntrance> levelEntrances;

    @Override
    public void create() {
        scrollSigns = new Array<ScrollSign>();
        levelEntrances = new Array<LevelEntrance>();
        loadAssets();
        getAssets();
        loadWorld();
        initialiseCamera();
        initialiseLibGdx();
        initialiseUniversalTweenEngine();
        initialiseFonts();
        createLevelEntrances();
        initialiseMap();
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
    }

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

    private void initialiseFonts() {
        SmartFontGenerator fontGen = new SmartFontGenerator();
        FileHandle internalFontFile = Gdx.files.internal(LIBERATION_MONO_REGULAR_FONT_NAME);
        FileHandle generatedFontDir = Gdx.files.local(GENERATED_FONTS_DIR);
        generatedFontDir.mkdirs();

        FileHandle generatedFontFile = Gdx.files.local("generated-fonts/LiberationMono-Regular.ttf");
        try {
            FileUtils.copyFile(internalFontFile, generatedFontFile);
        } catch (IOException ex) {
            Gdx.app.error(SlotPuzzleConstants.SLOT_PUZZLE, "Could not copy " + internalFontFile.file().getPath() + " to file " + generatedFontFile.file().getAbsolutePath() + " " + ex.getMessage());
        }
        fontSmall = fontGen.createFont(generatedFontFile, FONT_SMALL, FONT_SMALL_SIZE);
    }

    private Texture initialiseFontTexture(String text) {
        Pixmap textPixmap = new Pixmap(text.length() * 16, SIGN_HEIGHT, Pixmap.Format.RGBA8888);
        textPixmap.setColor(Color.CLEAR);
        textPixmap.fillRectangle(0, 0, textPixmap.getWidth(), textPixmap.getHeight());
        textPixmap = PixmapProcessors.createDynamicHorizontalFontTextViaFrameBuffer(fontSmall, Color.BLUE, text, textPixmap, 0, 20);
        return new Texture(textPixmap);
    }

    private void createLevelEntrances() {
        for (int i = 0; i < levelDoors.size; i++) {
            levelEntrances.add(new LevelEntrance((int) levelDoors.get(i).doorPosition.getWidth(), (int) levelDoors.get(i).doorPosition.getHeight()));
        }
    }

    private void initialiseMap() {
        layer = (TiledMapTileLayer) worldMap.getLayers().get("Tile Layer 1");

        for (int levelNumber = 0; levelNumber < levelDoors.size; levelNumber++) {
            ScrollSign scrollSign = addScrollSign(levelNumber, levelEntrances.get(levelNumber).getLevelEntrance().getWidth());
            scrollSigns.add(scrollSign);

			drawLevelEntrance(levelNumber, layer);
            TextureRegion[][] splitTiles = TextureRegion.split(levelEntrances.get(levelNumber).getLevelEntrance(), 40, 40);
            int xx = (int) levelDoors.get(levelNumber).doorPosition.getX() / 40;
            int yy = (int) levelDoors.get(levelNumber).doorPosition.getY() / 40;
            for (int row = 0; row < splitTiles.length; row++) {
                for (int col = 0; col < splitTiles[row].length; col++) {
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(new StaticTiledMapTile(splitTiles[row][col]));
                    layer.setCell(xx + col, yy + (splitTiles.length - row), cell);
                }
            }
        }
    }

    private ScrollSign addScrollSign(int levelNumber, int scrollSignWidth) {
        Texture scrollSignTexture = initialiseFontTexture(LEVEL_TEXT + SPACE + (levelNumber + 1) + SPACE + ENTRANCE_TEXT + SPACE);
        return new ScrollSign(scrollSignTexture, 0, 0, scrollSignWidth, SIGN_HEIGHT, ScrollSign.SignDirection.RIGHT);
    }
	
	private void drawLevelEntrance(int levelNumber, TiledMapTileLayer layer) {
		int levelDoorX = (int) levelDoors.get(levelNumber).doorPosition.getX() / 40;
        int levelDoorY = (int) levelDoors.get(levelNumber).doorPosition.getY() / 40;
		int levelDoorWidth = (int) levelDoors.get(levelNumber).doorPosition.getWidth() / 40;
		int levelDoorHeight = (int) levelDoors.get(levelNumber).doorPosition.getHeight() / 40;
		TiledMapTileLayer.Cell cell = layer.getCell(levelDoorX -  1, levelDoorY + levelDoorHeight);
		TiledMapTile tile = cell.getTile();
		Pixmap tilePixmap = PixmapProcessors.getPixmapFromTextureRegion(tile.getTextureRegion());
		int tileWidth = tilePixmap.getWidth();
		int tileHeight = tilePixmap.getHeight();
		tilePixmap.setColor(Color.RED);
		tilePixmap.fillRectangle(tileWidth - 4, 0, 4, tileHeight);
		Texture tileTexture = new Texture(tilePixmap);
		TextureRegion tileTextureRegion = new TextureRegion(tileTexture);
		cell.setTile(new StaticTiledMapTile(tileTextureRegion));
		layer.setCell(levelDoorX -  1, levelDoorY + levelDoorHeight, cell);
		for (int ceilingX = levelDoorX; ceilingX < levelDoorX + levelDoorWidth; ceilingX++) {
		    cell = layer.getCell(ceilingX, levelDoorY + levelDoorHeight + 1);
			tile = cell.getTile();
			tilePixmap = PixmapProcessors.getPixmapFromTextureRegion(tile.getTextureRegion());
			tilePixmap.setColor(Color.RED);
			tilePixmap.fillRectangle(0, tileHeight - 4, tileWidth, tileHeight);
			tileTexture = new Texture(tilePixmap);
			tileTextureRegion = new TextureRegion(tileTexture);
			cell.setTile(new StaticTiledMapTile(tileTextureRegion));
			layer.setCell(ceilingX, levelDoorY + levelDoorHeight + 1, cell);
		}
		cell = layer.getCell(levelDoorX + levelDoorWidth, levelDoorY + levelDoorHeight);
		tile = cell.getTile();
		tilePixmap = PixmapProcessors.getPixmapFromTextureRegion(tile.getTextureRegion());
		tilePixmap.setColor(Color.RED);
		tilePixmap.fillRectangle(0, 0, 4, tileHeight);
		tileTexture = new Texture(tilePixmap);
		tileTextureRegion = new TextureRegion(tileTexture);
		cell.setTile(new StaticTiledMapTile(tileTextureRegion));
		layer.setCell(levelDoorX + levelDoorWidth, levelDoorY + levelDoorHeight, cell);
	}

    private void loadWorld() {
        getMapProperties();
        levelDoors = new Array<LevelDoor>();
        for (MapObject mapObject : worldMap.getLayers().get(WORLD_MAP_LEVEL_DOORS).getObjects().getByType(RectangleMapObject.class)) {
            LevelDoor levelDoor = new LevelDoor();
			levelDoor.levelName = ((RectangleMapObject) mapObject).getName();
			levelDoor.levelType = (String) ((RectangleMapObject) mapObject).getProperties().get("type");
			levelDoor.doorPosition = ((RectangleMapObject) mapObject).getRectangle();
			levelDoors.add(levelDoor);
		}
    }

    private void getMapProperties() {
        MapProperties worldProps = worldMap.getProperties();
        tilePixelWidth = worldProps.get("tilewidth", Integer.class);
        tilePixelHeight = worldProps.get("tileheight", Integer.class);
    }

    private void createPopUps(Sprite mapTileSprite) {
        Level level1 = new WorldMapDynamicDoors.MapLevel1();
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

    private void updateDynamicDoors(float dt) {
        int levelNumber = 0,
            levelDoorX,
            levelDoorY,
            levelDoorHeight;
        TiledMapTileLayer.Cell cell;

        for (LevelDoor levelDoor : levelDoors) {
            levelDoorX = (int) levelDoor.doorPosition.getX() / 40;
            levelDoorY = (int) levelDoor.doorPosition.getY() / 40;
            levelDoorHeight = (int) levelDoor.doorPosition.getHeight() / 40;
            for (int col = 0; col < (scrollSigns.get(levelNumber).getSignWidth()) / 40; col++) {
                cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(new TextureRegion(scrollSigns.get(levelNumber), col * 40, 0, 40, 40)));
                layer.setCell(levelDoorX + col, levelDoorY + levelDoorHeight, cell);
            }
            levelNumber++;
        }
        for (ScrollSign scrollSign : scrollSigns) {
            scrollSign.update(dt);
            scrollSign.setSx(scrollSign.getSx() + 1);
        }
    }

    public void update(float dt) {
        tweenManager.update(dt);
        updateDynamicDoors(dt);
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

    public class MapGestureListener implements GestureDetector.GestureListener {
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
			processTouch(x, y);
            return false;
        }

        @Override
        public boolean tap(float x, float y, int count, int button) {
            processTouch(x, y);
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
            velX = camera.zoom * velocityX * 0.01f;
            velY = camera.zoom * velocityY * 0.01f;
            return false;
        }

        @Override
        public boolean pan(float x, float y, float deltaX, float deltaY) {
            camera.position.add(-deltaX * camera.zoom * 0.01f, deltaY * camera.zoom * 0.1f, 0);
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

        @Override
        public void pinchStop() {
        }

        public void update () {
            if (flinging) {
                velX *= 0.9f;
                velY *= 0.9f;
                camera.position.add(-velX * Gdx.graphics.getDeltaTime(), velY * Gdx.graphics.getDeltaTime(), 0);
                clampCamera();
                if (Math.abs(velX) < 0.01f) velX = 0;
                if (Math.abs(velY) < 0.01f) velY = 0;
            }
        }
		
		private void processTouch(float x, float y) {
            float wx = screenXToWorldX(x);
            float wy = screenYToWorldY(y);
            int levelDoorIndex = 0;
            for (LevelDoor levelDoor : levelDoors) {
                if (levelDoor.doorPosition.contains(wx, wy)) {
                    enterLevel(levelDoor, levelDoorIndex);
                }
                levelDoorIndex++;
            }
        }
		
		private void enterLevel(LevelDoor levelDoor, int levelDoorIndex) {
        	int sx = (int)worldXToScreenX(levelDoor.doorPosition.x);
            int sy = (int)worldYToScreenY(levelDoor.doorPosition.y);
            int sw = (int)(levelDoor.doorPosition.width * screenOverCWWRatio);
            int sh = (int)(levelDoor.doorPosition.height * screenOverCWHRatio);

            levelDoorPixmap = ScreenshotFactory.getScreenshot(sx, sy, sw, sh, true);
            levelDoorTexture = new Texture(levelDoorPixmap);
            levelDoorSprite = new Sprite(levelDoorTexture);
            levelDoorSprite.setX(sx);
            levelDoorSprite.setY(sy);
            levelDoorSprite.setOrigin(0, 0);
            createPopUps(levelDoorSprite);
            mapTile.maximize(maximizeCallback);
        }
		
        private void clampCamera() {
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
