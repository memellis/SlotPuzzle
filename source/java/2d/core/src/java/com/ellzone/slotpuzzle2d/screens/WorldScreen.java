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
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.ScreenshotFactory;

public class WorldScreen implements Screen {
	
    public static final String LOG_TAG = "SlotPuzzle_WorldScreen";
    private static final String WORLD_MAP = "levels/WorldMap.tmx";
    private static final String TILE_PACK_ATLAS = "tiles/tiles.pack.atlas";
    private static final String WORLD_MAP_LEVEL_DOORS = "Level Doors";
	private SlotPuzzle game;
	private OrthographicCamera cam;
	private TiledMap worldMap;
	private GestureDetector gestureDetector;
	private MapGestureListener mapGestureListener;
	private Array<LevelDoor> levelDoors;
	private Array<MapTile> mapTiles;
	private OrthogonalTiledMapRenderer renderer;
	private BitmapFont font;
	private float w, h, cww, cwh, aspectRatio;
    private float screenOverCWWRatio, screenOverCWHRatio;
	private Pixmap levelDoorPixmap;
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
    	getAssets();
        loadWorld();
    	initialiseCamera();
    	initialiseUniversalTweenEngine();
    	initialiseLibGdx();
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
    
	private void createPopUps(Sprite mapTileSprite) {
		mapTiles = new Array<MapTile>();
		mapTiles.add(new MapTile(20, 20, 200, 200, new MapLevel1(), tilesAtlas, cam, font, tweenManager, mapTileSprite));
		mapTiles.add(new MapTile(20, 20, 200, 200, new MapLevel2(), tilesAtlas, cam, font, tweenManager, mapTileSprite));
		mapTiles.add(new MapTile(20, 20, 200, 200, new MapLevel3(), tilesAtlas, cam, font, tweenManager, mapTileSprite));
		mapTiles.add(new MapTile(20, 20, 200, 200, new MapLevel4(), tilesAtlas, cam, font, tweenManager, mapTileSprite));
		mapTiles.add(new MapTile(20, 20, 200, 200, new MapLevel5(), tilesAtlas, cam, font, tweenManager, mapTileSprite));		
	}
		
	private final TweenCallback maximizeCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			selectedTile = (MapTile) source.getUserData();
			selectedTile.getLevel().initialise();
			int levelNumber = selectedTile.getLevel().getLevelNumber();
			levelDoors.get(levelNumber).id = levelNumber;
			game.setScreen(new PlayScreen(game, levelDoors.get(levelNumber)));
			tweenManager.killAll();
		}
	};
    
	@Override
	public void show() {
		System.out.println("WorldScreen: show");
	}
	
	public void update(float delta) {
		tweenManager.update(delta);
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
		if(levelDoorSprite != null) {
			levelDoorSprite.draw(game.batch);
		}
		if (mapTiles != null) {
		    for (MapTile mapTile : mapTiles) {
		        if (mapTile != null) {
			        mapTile.draw(game.batch);
		        }
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

            levelDoorPixmap = ScreenshotFactory.getScreenshot(sx, sy, sw, sh, true);
            levelDoorTexture = new Texture(levelDoorPixmap);
            levelDoorSprite = new Sprite(levelDoorTexture);
            levelDoorSprite.setX(sx);
            levelDoorSprite.setY(sy);
            levelDoorSprite.setOrigin(0, 0);
            createPopUps(levelDoorSprite);
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
		levelDoorSprite = null;
		mapTiles = null;
	}
}
