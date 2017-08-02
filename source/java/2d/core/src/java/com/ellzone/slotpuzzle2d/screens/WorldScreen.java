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

package com.ellzone.slotpuzzle2d.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.MapLevel1;
import com.ellzone.slotpuzzle2d.level.MapLevel2;
import com.ellzone.slotpuzzle2d.level.MapLevel3;
import com.ellzone.slotpuzzle2d.level.MapLevel4;
import com.ellzone.slotpuzzle2d.level.MapLevel5;
import com.ellzone.slotpuzzle2d.level.MapLevel6;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.ScreenshotFactory;
import org.jrenner.smartfont.SmartFontGenerator;
import com.badlogic.gdx.files.FileHandle;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import java.io.IOException;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.badlogic.gdx.graphics.Color;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.sprites.LevelEntrance;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.ellzone.slotpuzzle2d.sprites.ScrollSign;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.maps.tiled.*;

public class WorldScreen implements Screen {
	
    public static final String LOG_TAG = "SlotPuzzle_WorldScreen";
    public static final String LIBERATION_MONO_REGULAR_FONT_NAME = "LiberationMono-Regular.ttf";
    public static final String GENERATED_FONTS_DIR = "generated-fonts/";
    public static final String FONT_SMALL = "exo-small";
    public static final int FONT_SMALL_SIZE = 24;
    public static final int SIGN_WIDTH = 96;
    public static final int SIGN_HEIGHT = 32;

    private static final String WORLD_MAP = "levels/WorldMap.tmx";
    private static final String TILE_PACK_ATLAS = "tiles/tiles.pack.atlas";
    private static final String WORLD_MAP_LEVEL_DOORS = "Level Doors";
	public static final String LEVEL_TEXT = "Level";
    public static final String ENTRANCE_TEXT = "Entrance";
    public static final char SPACE = ' ';
	
	private SlotPuzzle game;
	private OrthographicCamera cam;
	private TiledMap worldMap;
	private GestureDetector gestureDetector;
	private MapGestureListener mapGestureListener;
	private TiledMapTileLayer mapTextureLayer;
	private Array<LevelDoor> levelDoors;
	private Array<MapTile> mapTiles;
	private Array<LevelEntrance> levelEntrances;
	private Array<ScrollSign> scrollSigns;
	private OrthogonalTiledMapRenderer renderer;
	private BitmapFont font;
	private BitmapFont fontSmall;
	private float w, h, cww, cwh, aspectRatio;
	private float screenOverCWWRatio, screenOverCWHRatio;
	private Texture levelDoorTexture;
	private Sprite levelDoorSprite;
	private TextureAtlas tilesAtlas;
	private MapTile selectedTile;
	private TweenManager tweenManager;
	private int mapWidth, mapHeight, tilePixelWidth, tilePixelHeight;
	private String message = "";

	   
    public WorldScreen(SlotPuzzle game) {
		this.game = game;
		this.game.setWorldScreen(this);
		createWorldScreen();
	}

	private void createWorldScreen() {
		scrollSigns = new Array<ScrollSign>();
		levelEntrances = new Array<LevelEntrance>();
		getAssets();
		loadWorld();
		initialiseCamera();
		initialiseUniversalTweenEngine();
		initialiseLibGdx();
		initialiseFonts();
		createLevelEntrances();
		initialiseMap();
		createPopUps();
	}
    
	private void getAssets() {
		worldMap = game.assetManager.get(WORLD_MAP);
		tilesAtlas = game.assetManager.get(TILE_PACK_ATLAS, TextureAtlas.class);
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
		font = new BitmapFont();
		mapGestureListener = new MapGestureListener(cam);
		gestureDetector = new GestureDetector(2, 0.5f, 2, 0.15f, mapGestureListener);
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(gestureDetector);
		Gdx.input.setInputProcessor(multiplexer);
		renderer = new OrthogonalTiledMapRenderer(worldMap, 1f / 40f);
		Matrix4 gameProjectionMatrix = new Matrix4();
		gameProjectionMatrix.setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		game.batch.setProjectionMatrix(gameProjectionMatrix);
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
		mapTextureLayer = (TiledMapTileLayer) worldMap.getLayers().get("Tile Layer 1");

		for (int levelNumber = 0; levelNumber < levelDoors.size; levelNumber++) {
			ScrollSign scrollSign = addScrollSign(levelNumber, levelEntrances.get(levelNumber).getLevelEntrance().getWidth());
			scrollSigns.add(scrollSign);

			drawLevelEntrance(levelNumber, mapTextureLayer);
			TextureRegion[][] splitTiles = TextureRegion.split(levelEntrances.get(levelNumber).getLevelEntrance(), 40, 40);
			int xx = (int) levelDoors.get(levelNumber).doorPosition.getX() / 40;
			int yy = (int) levelDoors.get(levelNumber).doorPosition.getY() / 40;
			for (int row = 0; row < splitTiles.length; row++) {
				for (int col = 0; col < splitTiles[row].length; col++) {
					TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
					cell.setTile(new StaticTiledMapTile(splitTiles[row][col]));
					mapTextureLayer.setCell(xx + col, yy + (splitTiles.length - row), cell);
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
		TiledMapTileLayer.Cell cell = layer.getCell(levelDoorX - 1, levelDoorY + levelDoorHeight);
		TiledMapTile tile = cell.getTile();
		Pixmap tilePixmap = PixmapProcessors.getPixmapFromTextureRegion(tile.getTextureRegion());
		int tileWidth = tilePixmap.getWidth();
		int tileHeight = tilePixmap.getHeight();
		tilePixmap.setColor(Color.RED);
		tilePixmap.fillRectangle(tileWidth - 4, 0, 4, tileHeight);
		Texture tileTexture = new Texture(tilePixmap);
		TextureRegion tileTextureRegion = new TextureRegion(tileTexture);
		cell.setTile(new StaticTiledMapTile(tileTextureRegion));
		layer.setCell(levelDoorX - 1, levelDoorY + levelDoorHeight, cell);
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
		mapWidth = worldProps.get("width", Integer.class);
		mapHeight = worldProps.get("height", Integer.class);
		tilePixelWidth = worldProps.get("tilewidth", Integer.class);
		tilePixelHeight = worldProps.get("tileheight", Integer.class);
	}

	private void createPopUps() {
		mapTiles = new Array<MapTile>();
		mapTiles.add(new MapTile(20, 20, 200, 200, new MapLevel1(), tilesAtlas, cam, font, tweenManager, new Sprite(levelEntrances.get(0).getLevelEntrance())));
		mapTiles.add(new MapTile(20, 20, 200, 200, new MapLevel2(), tilesAtlas, cam, font, tweenManager, new Sprite(levelEntrances.get(1).getLevelEntrance())));
		mapTiles.add(new MapTile(20, 20, 200, 200, new MapLevel3(), tilesAtlas, cam, font, tweenManager, new Sprite(levelEntrances.get(2).getLevelEntrance())));
		mapTiles.add(new MapTile(20, 20, 200, 200, new MapLevel4(), tilesAtlas, cam, font, tweenManager, new Sprite(levelEntrances.get(3).getLevelEntrance())));
		mapTiles.add(new MapTile(20, 20, 200, 200, new MapLevel5(), tilesAtlas, cam, font, tweenManager, new Sprite(levelEntrances.get(4).getLevelEntrance())));
		mapTiles.add(new MapTile(20, 20, 200, 200, new MapLevel6(), tilesAtlas, cam, font, tweenManager, new Sprite(levelEntrances.get(5).getLevelEntrance())));
	}

	private final TweenCallback maximizeCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			selectedTile = (MapTile) source.getUserData();
			selectedTile.getLevel().initialise();
			int levelNumber = selectedTile.getLevel().getLevelNumber();
			levelDoors.get(levelNumber).id = levelNumber;
			game.setScreen(new PlayScreen(game, levelDoors.get(levelNumber), selectedTile));
			tweenManager.killAll();
		}
	};

	@Override
	public void show() {
		System.out.println("WorldScreen: show");
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
				mapTextureLayer.setCell(levelDoorX + col, levelDoorY + levelDoorHeight, cell);

				if (mapTiles.get(levelNumber).getLevel().isLevelCompleted()) {
					updateScrollSignToLevelCompleted(mapTiles.get(levelNumber));
				}
			}
			levelNumber++;
		}
		for (ScrollSign scrollSign : scrollSigns) {
			scrollSign.update(dt);
			scrollSign.setSx(scrollSign.getSx() + 1);
		}
	}

	private void updateScrollSignToLevelCompleted(MapTile maptile) {

	}

	
	public void update(float delta) {
		tweenManager.update(delta);
		updateDynamicDoors(delta);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		update(delta);
		mapGestureListener.update();
		renderer.render();
		renderer.setView(cam);
		cam.update();
		game.batch.begin();
		for (MapTile mapTile : mapTiles) {
			if (!mapTile.getLevel().isLevelCompleted()) {
				mapTile.draw(game.batch);
			}
		}
		font.draw(game.batch, message, 80, 100);
		game.batch.end();
	}

	@Override
	public void resize(int width, int height) {
		System.out.println("WorldScreen: resize");
	}

	@Override
	public void pause() {
		System.out.println("WorldScreen: pause");
	}

	@Override
	public void resume() {
		System.out.println("WorldScreen: resume");
	}

	@Override
	public void hide() {
		System.out.println("WorldScreen: hide");
	}

	@Override
	public void dispose() {
		System.out.println("WorldScreen: dispose");
	}

	public class MapGestureListener implements GestureListener {

		@Override
		public void pinchStop()
		{
			// TODO: Implement this method
		}

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
			camera.position.add(-deltaX * camera.zoom * 0.01f, deltaY * camera.zoom * 0.01f, 0);
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


			levelDoorTexture = levelEntrances.get(levelDoorIndex).getLevelEntrance();
			levelDoorSprite = new Sprite(levelDoorTexture);
			levelDoorSprite.setOrigin(0, 0);
			levelDoorSprite.setBounds(sx, sy, sw, sh);
			mapTiles.get(levelDoorIndex).setSprite(levelDoorSprite);
			mapTiles.get(levelDoorIndex).maximize(maximizeCallback);
		}

		private void clampCamera() {
			if (camera.position.x < 0) {
				camera.position.x = 0;
			}
			if (camera.position.x > mapWidth) {
				camera.position.x = mapWidth;
			}
			if (camera.position.y < 0) {
				camera.position.y = 0;
			}
			if (camera.position.y > mapHeight) {
				camera.position.y = mapHeight;
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

	public void worldScreenCallBack() {
		tweenManager.killAll();
		//levelDoorSprite = null;
		//mapTiles = null;
	}
}
