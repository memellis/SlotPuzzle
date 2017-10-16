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
import com.badlogic.gdx.Screen;
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.MapLevel1;
import com.ellzone.slotpuzzle2d.level.MapLevel2;
import com.ellzone.slotpuzzle2d.level.MapLevel3;
import com.ellzone.slotpuzzle2d.level.MapLevel4;
import com.ellzone.slotpuzzle2d.level.MapLevel5;
import com.ellzone.slotpuzzle2d.level.MapLevel6;
import com.ellzone.slotpuzzle2d.pixmap.PixmapDrawAction;
import com.ellzone.slotpuzzle2d.prototypes.menu.SlotPuzzleGame;
import com.ellzone.slotpuzzle2d.prototypes.screens.MenuScreenPrototype;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.screens.WorldScreen;
import com.ellzone.slotpuzzle2d.sprites.LevelEntrance;
import com.ellzone.slotpuzzle2d.sprites.ScrollSign;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import org.jrenner.smartfont.SmartFontGenerator;
import java.io.IOException;

public class WorldScreenPrototype implements Screen {

    public static final String LOG_TAG = "SlotPuzzle_WorldScreenPrototype";
    public static final String LIBERATION_MONO_REGULAR_FONT_NAME = "LiberationMono-Regular.ttf";
    public static final String GENERATED_FONTS_DIR = "generated-fonts/";
    public static final String FONT_SMALL = "exo-small";
    public static final int FONT_SMALL_SIZE = 24;
    public static final int SIGN_WIDTH = 96;
    public static final int SIGN_HEIGHT = 32;
    public static final String LEVEL_TEXT = "Level";
    public static final String ENTRANCE_TEXT = "Entrance";
    public static final char SPACE = ' ';
    public static final int ORTHO_VIEWPORT_WIDTH = 10;
    public static final int ORTHO_VIEWPORT_HEIGHT = 10;
    private static final String WORLD_MAP_LEVEL_DOORS = "Level Doors";

    private OrthographicCamera camera;
    private Viewport viewport;
    private SpriteBatch batch;
    private TiledMap worldMap;
    private TiledMapTileLayer layer;
    private OrthogonalTiledMapRenderer renderer;
    private BitmapFont font;
    private GestureDetector gestureDetector;
    private MapGestureListener mapGestureListener;
    private Array<LevelDoor> levelDoors;
    private float w, h, resizeWidth, resizeHeight, cww, cwh, aspectRatio;
    private float screenOverCWWRatio, screenOverCWHRatio;
    private Texture levelDoorTexture;
    private Sprite levelDoorSprite;
    private TextureAtlas tilesAtlas;
    private MapTile mapTile, selectedTile;
    private TweenManager tweenManager;
    private int tilePixelWidth, tilePixelHeight;
    private BitmapFont fontSmall;
    private Array<ScrollSign> scrollSigns;
    private Array<LevelEntrance> levelEntrances;
    private Array<MapTile> mapTiles;
    private SlotPuzzle game;
    private InputMultiplexer inputMultiplexer;

    public WorldScreenPrototype(SlotPuzzle game) {
        this.game = game;
        game.setWorldScreen(this);
        createWorldScreen();
    }

    private void createWorldScreen() {
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
        createPopUps();
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
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT, this.camera);
        this.aspectRatio = SlotPuzzleConstants.V_WIDTH / SlotPuzzleConstants.V_HEIGHT;
        this.camera.setToOrtho(false, aspectRatio * ORTHO_VIEWPORT_WIDTH, ORTHO_VIEWPORT_HEIGHT);
        this.camera.zoom = 2;
        this.camera.update();
        this.cww = camera.viewportWidth * camera.zoom * tilePixelWidth;
        this.cwh = camera.viewportHeight * camera.zoom * tilePixelHeight;
        this.screenOverCWWRatio = SlotPuzzleConstants.V_WIDTH / cww;
        this.screenOverCWHRatio = SlotPuzzleConstants.V_HEIGHT / cwh;

    }

    private void initialiseLibGdx() {
        batch = new SpriteBatch();
        font = new BitmapFont();
        renderer = new OrthogonalTiledMapRenderer(worldMap, 1f / 40f);

        mapGestureListener = new MapGestureListener(camera);
        gestureDetector = new GestureDetector(2, 0.5f, 2, 0.15f, mapGestureListener);
        inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(gestureDetector);
        Gdx.input.setInputProcessor(inputMultiplexer);
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

    private Array<String> initialiseScrollSignMessages(String message) {
        Array<String> scrollSignMessages = new Array<String>();
        scrollSignMessages.add(message);
        scrollSignMessages.add(message + "level completed ");
        return scrollSignMessages;
    }

    private Array<Texture> initialiseFontTextures(Array<String> textureTexts) {
        Texture textTexture;
        Array <Texture> textTextures = new Array<Texture>();
        for (String textureText : textureTexts) {
            Pixmap textPixmap = new Pixmap(textureText.length() * SIGN_WIDTH / 6, SIGN_HEIGHT, Pixmap.Format.RGBA8888);
            textPixmap = PixmapProcessors.createDynamicHorizontalFontTextViaFrameBuffer(fontSmall, Color.BLUE, textureText, textPixmap, 3, 20);
            textTexture = new Texture(textPixmap);
            textTextures.add(textTexture);
        }
        return  textTextures;
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
        Array<String> textMessages = initialiseScrollSignMessages(LEVEL_TEXT + SPACE + (levelNumber + 1) + SPACE + ENTRANCE_TEXT + SPACE);
        Array<Texture> textTextures = initialiseFontTextures(textMessages);
        return new ScrollSign(textTextures, 0, 0, scrollSignWidth, SIGN_HEIGHT, ScrollSign.SignDirection.RIGHT);
    }

    private void drawOnCell(TiledMapTileLayer layer, int cellX, int cellY, PixmapDrawAction drawAction) {
        TiledMapTileLayer.Cell cell = layer.getCell(cellX, cellY);
        TiledMapTile tile = cell.getTile();
        Pixmap tilePixmap = PixmapProcessors.getPixmapFromTextureRegion(tile.getTextureRegion());
        drawAction.drawAction(tilePixmap);
        Texture tileTexture = new Texture(tilePixmap);
        TextureRegion tileTextureRegion = new TextureRegion(tileTexture);
        cell.setTile(new StaticTiledMapTile(tileTextureRegion));
        layer.setCell(cellX, cellY, cell);
    }

    private void drawLevelEntrance(int levelNumber, TiledMapTileLayer layer) {
        int levelDoorX = (int) levelDoors.get(levelNumber).doorPosition.getX() / 40;
        int levelDoorY = (int) levelDoors.get(levelNumber).doorPosition.getY() / 40;
        int levelDoorWidth = (int) levelDoors.get(levelNumber).doorPosition.getWidth() / 40;
        int levelDoorHeight = (int) levelDoors.get(levelNumber).doorPosition.getHeight() / 40;

        drawOnCell(layer, levelDoorX - 1, levelDoorY + levelDoorHeight, new PixmapDrawAction() {
            @Override
            public void drawAction(Pixmap pixmap) {
                pixmap.setColor(Color.RED);
                int tileWidth = pixmap.getWidth();
                int tileHeight = pixmap.getHeight();
                pixmap.fillRectangle(tileWidth - 4, 0, 4, tileHeight);
            }
        });

        for (int ceilingX = levelDoorX; ceilingX < levelDoorX + levelDoorWidth; ceilingX++) {
            drawOnCell(layer, ceilingX, levelDoorY + levelDoorHeight + 1, new PixmapDrawAction() {
                @Override
                public void drawAction(Pixmap pixmap) {
                    pixmap.setColor(Color.RED);
                    int tileWidth = pixmap.getWidth();
                    int tileHeight = pixmap.getHeight();
                    pixmap.fillRectangle(0, tileHeight - 4, tileWidth, tileHeight);
                }
            });
        }

        drawOnCell(layer, levelDoorX + levelDoorWidth, levelDoorY + levelDoorHeight, new PixmapDrawAction() {
            @Override
            public void drawAction(Pixmap pixmap) {
                pixmap.setColor(Color.RED);
                int tileHeight = pixmap.getHeight();
                pixmap.fillRectangle(0, 0, 4, tileHeight);
            }
        });
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

    private void createPopUps() {
        mapTiles = new Array<MapTile>();
        mapTiles.add(new MapTile(20, 20, 200, 200, SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT, new MapLevel1(), tilesAtlas, this.camera, font, tweenManager, new Sprite(levelEntrances.get(0).getLevelEntrance())));
        mapTiles.add(new MapTile(20, 20, 200, 200, SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT, new MapLevel2(), tilesAtlas, this.camera, font, tweenManager, new Sprite(levelEntrances.get(1).getLevelEntrance())));
        mapTiles.add(new MapTile(20, 20, 200, 200, SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT, new MapLevel3(), tilesAtlas, this.camera, font, tweenManager, new Sprite(levelEntrances.get(2).getLevelEntrance())));
        mapTiles.add(new MapTile(20, 20, 200, 200, SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT, new MapLevel4(), tilesAtlas, this.camera, font, tweenManager, new Sprite(levelEntrances.get(3).getLevelEntrance())));
        mapTiles.add(new MapTile(20, 20, 200, 200, SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT, new MapLevel5(), tilesAtlas, this.camera, font, tweenManager, new Sprite(levelEntrances.get(4).getLevelEntrance())));
        mapTiles.add(new MapTile(20, 20, 200, 200, SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT, new MapLevel6(), tilesAtlas, this.camera, font, tweenManager, new Sprite(levelEntrances.get(5).getLevelEntrance())));
    }

    private final TweenCallback maximizeCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
        selectedTile = (MapTile) source.getUserData();
        selectedTile.disableDraw();
        selectedTile.getLevel().initialise();
        int levelNumber = selectedTile.getLevel().getLevelNumber();
        levelDoors.get(levelNumber).id = levelNumber;
        game.setScreen(new MenuScreenPrototype(game, levelDoors.get(levelNumber), selectedTile));
        }
    };

    @Override
    public void show() {
        Gdx.app.log(LOG_TAG, "show() called.");
    }

    @Override
    public void resize(int width, int height) {
        this.resizeWidth = width;
        this.resizeHeight = height;
        Gdx.app.log(LOG_TAG, "resize(int width, int height) called: width=" + width + ", height="+height);
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

                if (mapTiles.get(levelNumber).getLevel().isLevelCompleted()) {
                    if (!mapTiles.get(levelNumber).getLevel().hasLevelScrollSignChanged()) {
                        updateScrollSignToLevelCompleted(mapTiles.get(levelNumber), scrollSigns.get(levelNumber));
                    }
                }
            }
            levelNumber++;
        }
        for (ScrollSign scrollSign : scrollSigns) {
            scrollSign.update(dt);
            scrollSign.setSx(scrollSign.getSx() + 1);
        }
    }

    private void updateScrollSignToLevelCompleted(MapTile maptile, ScrollSign scrollSign) {
        maptile.getLevel().setLevelScrollSignChanged(true);
        Array<Texture> signTextures = scrollSign.getSignTextures();
        String textureText = maptile.getLevel().getTitle() + " level completed with Score: " + maptile.getLevel().getScore();
        Pixmap textPixmap = new Pixmap(textureText.length() * SIGN_WIDTH / 6, SIGN_HEIGHT, Pixmap.Format.RGBA8888);
        textPixmap = PixmapProcessors.createDynamicHorizontalFontTextViaFrameBuffer(fontSmall, Color.CYAN, textureText, textPixmap, 0, 22);
        Texture textTexture = new Texture(textPixmap);
        signTextures.set(1, textTexture);
        scrollSign.switchSign(scrollSign.getCurrentSign() == 0 ? 1 : 0);
    }

    public void update(float delta) {
        tweenManager.update(delta);
        updateDynamicDoors(delta);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        update(delta);
        this.camera.update();
        mapGestureListener.update();
        renderer.setView(this.camera);
        renderer.render();
        game.batch.begin();
        for (MapTile mapTile : mapTiles) {
            mapTile.draw(game.batch);
        }
        font.draw(game.batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 10, 20);
        game.batch.end();
    }

    @Override
    public void pause() {
        Gdx.app.log(LOG_TAG, "pause() called.");
    }

    @Override
    public void resume() {
        Gdx.app.log(LOG_TAG, "resume() called.");
    }

    @Override
    public void hide() {
        Gdx.app.log(LOG_TAG, "hide() called.");
    }

    @Override
    public void dispose() {
        Gdx.app.log(LOG_TAG, "dispose() called.");
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
        public boolean zoom(float originalDistance, float currentDistance) {
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

        public void update() {
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
            float wy = screenYToWorldY(y) - tilePixelHeight;
            int levelDoorIndex = 0;
            for (LevelDoor levelDoor : levelDoors) {
                if (levelDoor.doorPosition.contains(wx, wy)) {
                    enterLevel(levelDoor, levelDoorIndex);
                }
                levelDoorIndex++;
            }
        }

        private void enterLevel(LevelDoor levelDoor, int levelDoorIndex) {
            int sx = (int) (worldXToScreenX(levelDoor.doorPosition.x) * SlotPuzzleConstants.V_WIDTH / w);
            int sy = (int) (worldYToScreenY(levelDoor.doorPosition.y + tilePixelHeight) * SlotPuzzleConstants.V_HEIGHT / h);
            int sw = (int) ((levelDoor.doorPosition.width * screenOverCWWRatio) * SlotPuzzleConstants.V_WIDTH / w);
            int sh = (int) ((levelDoor.doorPosition.height * screenOverCWHRatio) * SlotPuzzleConstants.V_HEIGHT / h);

            levelDoorTexture = levelEntrances.get(levelDoorIndex).getLevelEntrance();
            levelDoorSprite = new Sprite(levelDoorTexture);
            levelDoorSprite.setOrigin(0, 0);
            levelDoorSprite.setBounds(sx, sy, sw, sh);
            mapTiles.get(levelDoorIndex).setSprite(levelDoorSprite);
            mapTiles.get(levelDoorIndex).reinitialise();
            mapTiles.get(levelDoorIndex).maximize(maximizeCallback);
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
            return ((camera.position.x - aspectRatio * ORTHO_VIEWPORT_WIDTH) * tilePixelWidth) + ((x / screenOverCWWRatio) * (float)SlotPuzzleConstants.V_WIDTH / Gdx.graphics.getWidth());
        }

        private float screenYToWorldY(float y) {
            return ((camera.position.y - ORTHO_VIEWPORT_HEIGHT) * tilePixelHeight) + (( Gdx.graphics.getHeight() - y) / screenOverCWHRatio) * (float) SlotPuzzleConstants.V_HEIGHT / Gdx.graphics.getHeight();
        }

        private float worldXToScreenX(float wx) {
            return (wx - ((camera.position.x - aspectRatio * ORTHO_VIEWPORT_WIDTH) * tilePixelWidth)) * screenOverCWWRatio;
        }

        private float worldYToScreenY(float wy) {
            return (wy - ((camera.position.y - ORTHO_VIEWPORT_HEIGHT) * tilePixelHeight)) * screenOverCWHRatio;
        }
    }

    public void worldScreenCallBack() {
        tweenManager.killAll();
        Gdx.input.setInputProcessor(inputMultiplexer);
    }
}
