package com.ellzone.slotpuzzle2d.screens;

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.effects.ReelSpriteAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTile;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTileListener;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTileScroll;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedFlashingReelSlotTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningReelSlotTileEvent;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Sine;

public class PlayScreen implements Screen {
	private static final float SIXTY_FPS = 1/60f;
	private static final int TILE_WIDTH = 32;
	private static final int TILE_HEIGHT = 32;
	private static final int SLOT_REEL_OBJECT_LAYER = 3;
	private static final int HIDDEN_PATTERN_LAYER = 0;  
	private static final float PUZZLE_GRID_START_X = 192.0f;
	private static final float PUZZLE_GRID_START_Y = 96.0f; 
	
	private SlotPuzzle game;
	private final OrthographicCamera camera = new OrthographicCamera();
	private Viewport viewport;
	private Stage stage;
	private Sprite cheesecake;
	private Sprite cherry;
	private Sprite grapes;
	private Sprite jelly;
	private Sprite lemon;
	private Sprite peach;
	private Sprite pear;
	private Sprite tomato;
	private float spriteWidth, spriteHeight;
 	private final TweenManager tweenManager = new TweenManager();
 	private TextureAtlas reelAtlas;
	private boolean isLoaded = false;
	private Pixmap slotReelPixmap, slotReelScrollPixmap;
	private Texture slotReelTexture, slotReelScrollTexture;
	private Array<ReelSlotTile> levelReelSlotTiles;
	private TiledMap level1;
	private Random random;
	private OrthogonalTiledMapRenderer renderer;
	private boolean gameOver = false;
	private int touchX, touchY;
	private boolean initialFlashingStopped;
	private boolean displaySpinHelp;
	private int displaySpinHelpSprite;
	private Sprite[] sprites;
    private ReelSlotTileScroll reelSlot;
    private SlotPuzzleTween tween;
    private float returnValues[] = new float[2];
    private boolean tweenClicked = false;

    public PlayScreen(SlotPuzzle game) {
		this.game = game;
		createPlayScreen();		
	}
	
	private void createPlayScreen() {
		initialiseScreen();
		initialiseTweenEngine();
		loadAssets();
		getAssets();
		createSprites();
		initialisePlayScreen();
		createSlotReelTexture();
		createLevels();
		createReelIntroSequence();

        if (gameOver) {
        	Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        	Table table = new Table();
        	table.center();
        	table.setFillParent(true);

        	Label gameOverLabel = new Label("PLAY SCREEN", font);
        	Label playAgainLabel = new Label("Click to Play Again", font);

        	table.add(gameOverLabel).expandX();
        	table.row();
        	table.add(playAgainLabel).expandX().padTop(10f);
        
        	stage.addActor(table);
        }
   	}
	
	private void initialisePlayScreen() {
		random = new Random();
		renderer = new OrthogonalTiledMapRenderer(level1);
		levelReelSlotTiles = new Array<ReelSlotTile>();
		initialFlashingStopped = false;
		displaySpinHelp = false;		
	}
	
	private void initialiseTweenEngine() {
		SlotPuzzleTween.setWaypointsLimit(10);
		SlotPuzzleTween.setCombinedAttributesLimit(3);
		SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
		SlotPuzzleTween.registerAccessor(ReelSlotTileScroll.class, new ReelSpriteAccessor());		
	}
	
	private void initialiseScreen() {
		viewport = new FitViewport(800, 480, camera);
        stage = new Stage(viewport, game.batch);		
	}

	private void loadAssets() {
		game.assetManager.load("reel/reels.pack.atlas", TextureAtlas.class);
		game.assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		game.assetManager.load("levels/level 1.tmx", TiledMap.class);
 		game.assetManager.finishLoading();
	}

	private void getAssets() {
		level1 = game.assetManager.get("levels/level 1.tmx");
		reelAtlas = game.assetManager.get("reel/reels.pack.atlas", TextureAtlas.class);
	}
	
	private void createSprites() {
		cherry = reelAtlas.createSprite("cherry");
		cheesecake = reelAtlas.createSprite("cheesecake");
		grapes = reelAtlas.createSprite("grapes");
		jelly = reelAtlas.createSprite("jelly");
		lemon = reelAtlas.createSprite("lemon");
		peach = reelAtlas.createSprite("peach");
		pear = reelAtlas.createSprite("pear");
		tomato = reelAtlas.createSprite("tomato");
		
		sprites = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
		for (Sprite sprite : sprites) {
			sprite.setOrigin(0, 0);
		}
		spriteWidth = sprites[0].getWidth();
		spriteHeight = sprites[0].getHeight();

	}
	
	private void createSlotReelTexture() {
		slotReelPixmap = new Pixmap(PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT, Pixmap.Format.RGBA8888);		
		slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedPixmap(sprites, sprites.length);
		slotReelTexture = new Texture(slotReelPixmap);
	}
	
	private void createLevels() {
		for (MapObject mapObject : level1.getLayers().get(SLOT_REEL_OBJECT_LAYER).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
			int c = (int) (mapRectangle.getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
			int r = (int) (mapRectangle.getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
			r = 8 - r;
			if ((r >= 0) & (r <= 8) & (c >= 0) & (c <= 8)) {
                int endReel = random.nextInt(sprites.length);
				ReelSlotTile reelSlotTile = new ReelSlotTile(this, slotReelTexture, sprites.length, sprites.length * sprites.length, SIXTY_FPS, mapRectangle.getX(), mapRectangle.getY(), endReel);
				reelSlotTile.addListener(new ReelSlotTileListener() {
					@Override
					public void actionPerformed(ReelSlotTileEvent event) {
						if (event instanceof ReelStoppedSpinningReelSlotTileEvent) {
  							if (ReelSlotTile.reelsSpinning == 1) {
  								if (testForHiddenPatternRevealed(levelReelSlotTiles)) {
									gameOver = true;
								}
							}
						}
						if ((event instanceof ReelStoppedFlashingReelSlotTileEvent) & (initialFlashingStopped)) {
							if (testForHiddenPatternRevealed(levelReelSlotTiles)) {
								gameOver = true;
							}
						}
						if ((event instanceof ReelStoppedFlashingReelSlotTileEvent) & (!initialFlashingStopped)) {
							initialFlashingStopped = true;				
						}	
					}
				});
				levelReelSlotTiles.add(reelSlotTile);
			} else {
				Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "I don't respond to grid r="+r+"c="+c+". There it won't be added to the level! Sort it out in a level editor.");				
			}
		}
		levelReelSlotTiles = checkLevel(levelReelSlotTiles);	
	}

	private void createReelIntroSequence() {
		
		Timeline sequence = Timeline.createParallel();
		for(int i=0; i < levelReelSlotTiles.size; i++) {
			sequence = sequence
					      .push(buildSequence(levelReelSlotTiles.get(i), i, random.nextFloat() * 15.0f, random.nextFloat() * 15.0f));
		}
				
		sequence = sequence
				      .pushPause(0.3f)
				      .start(tweenManager);

        slotReelScrollPixmap = new Pixmap((int) spriteWidth, (int)spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        reelSlot = new ReelSlotTileScroll(slotReelScrollTexture, slotReelTexture.getWidth(), slotReelTexture.getHeight(), 0, 32, 0, PlayScreen.SIXTY_FPS);
        reelSlot.setX(0);
        reelSlot.setY(32);
        reelSlot.setEndReel(random.nextInt(sprites.length));

        tween = SlotPuzzleTween.to(reelSlot, ReelSpriteAccessor.SCROLL_XY, 20.0f) .target(0,  2560 + reelSlot.getEndReel() * 32) .ease(Sine.OUT).setCallback(new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
               delegateTweenOnEvent(type, source);
            }
        }) .setCallbackTriggers(TweenCallback.STEP + TweenCallback.END)
           .start(tweenManager);
	}
	
	private Timeline buildSequence(Sprite target, int id, float delay1, float delay2) {
		return Timeline.createSequence()
			.push(SlotPuzzleTween.set(target, SpriteAccessor.POS_XY).target(-0.5f, -0.5f))
			.push(SlotPuzzleTween.set(target, SpriteAccessor.SCALE_XY).target(10, 10))
			.push(SlotPuzzleTween.set(target, SpriteAccessor.ROTATION).target(0))
			.push(SlotPuzzleTween.set(target, SpriteAccessor.OPACITY).target(0))
			.pushPause(delay1)
			.beginParallel()
				.push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
				.push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
			.end()
			.pushPause(-0.5f)
			.push(SlotPuzzleTween.to(target, SpriteAccessor.POS_XY, 1.0f).target(levelReelSlotTiles.get(id).getX(), levelReelSlotTiles.get(id).getY()).ease(Back.OUT))
			.push(SlotPuzzleTween.to(target, SpriteAccessor.ROTATION, 0.8f).target(360).ease(Cubic.INOUT))
			.pushPause(delay2)
			.beginParallel()
				.push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 0.3f).target(3, 3).ease(Quad.IN))
				.push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 0.3f).target(0).ease(Quad.IN))
			.end()
			.pushPause(-0.5f)
			.beginParallel()
			    .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
			    .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
		    .end();
	}

    private Array<ReelSlotTile> checkLevel(Array<ReelSlotTile> slotReelTiles) {
        TupleValueIndex[][] grid = populateMatchGrid(slotReelTiles);
        int arraySizeR = grid.length;
        int arraySizeC = grid[0].length;

        for(int r = 0; r < arraySizeR; r++) {
            for(int c = 0; c < arraySizeC; c++) {
                if(grid[r][c] == null) {
                    Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "Found null grid tile. r=" + r + " c= " + c + ". I will therefore create a deleted entry for the tile.");
                    throw new GdxRuntimeException("Level incorrect. Found null grid tile. r=" + r + " c= " + c);
               }
            }
        }
        return slotReelTiles;
    }
	
	boolean testForHiddenPatternRevealed(Array<ReelSlotTile> levelReelSlotTiles) {
		PuzzleGridType puzzleGrid = new PuzzleGridType();
		TupleValueIndex[][] grid = populateMatchGrid(levelReelSlotTiles);
		Array<TupleValueIndex> matchedSlots;
		matchedSlots = puzzleGrid.matchGridSlots(grid);
 		flashMatchedSlots(matchedSlots);
		return hiddenPatternRevealed(grid);	
	}

	
	private TupleValueIndex[][] populateMatchGrid(Array<ReelSlotTile> slotReelTiles) {
		TupleValueIndex[][] matchGrid = new TupleValueIndex[9][9];
		int r, c;
		
		for (int i = 0; i < slotReelTiles.size; i++) {
			c = (int) (slotReelTiles.get(i).getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
			r = (int) (slotReelTiles.get(i).getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
			r = 8 - r;
			if ((r >= 0) & (r <= 8) & (c >= 0) & (c <= 8)) {
				if (slotReelTiles.get(i).isReelTileDeleted()) {
					matchGrid[r][c] = new TupleValueIndex(r, c, i, -1);
				} else {
					matchGrid[r][c] = new TupleValueIndex(r, c, i, slotReelTiles.get(i).getEndReel());
				}
			} else {
				Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
			}
		}
		return matchGrid;
	}

	public void handleInput(float dt) {
		if (Gdx.input.justTouched()) {
            touchX = Gdx.input.getX();
            touchY = Gdx.input.getY();
            Vector2 newPoints1 = new Vector2(touchX, touchY);
            newPoints1 = viewport.unproject(newPoints1);
            if ((newPoints1.x >= 0) & (newPoints1.x <= spriteWidth) & (newPoints1.y >= spriteHeight) & (newPoints1.y <= 64)) {
                if (tween.getCurrentTime() == 0) {
                    tweenClicked = false;
                    reelSlot.setEndReel(random.nextInt(sprites.length));
                    tween = SlotPuzzleTween.to(reelSlot, ReelSpriteAccessor.SCROLL_XY, 20.0f) .target(0,  returnValues[1] + (2560 - returnValues[1] % 2560) + reelSlot.getEndReel() * 32) .ease(Sine.OUT).setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            delegateTweenOnEvent(type, source);
                        }
                    }) .setCallbackTriggers(TweenCallback.END)
                            .start(tweenManager);
                } else {
                    if ((tween.getCurrentTime() < tween.getDuration() / 2.0f) & (!tweenClicked)) {
                        tweenClicked = true;
                        reelSlot.setEndReel();
                        tween = tween.target(0, returnValues[1] + (1280 - (returnValues[1] % 1280)) + reelSlot.getEndReel() * 32);
                        tween = tween.setDuration((tween.getDuration() - tween.getCurrentTime()));
                        tween.start();
                    }
                }
            }
			if (initialFlashingStopped){
				touchX = Gdx.input.getX();
				touchY = Gdx.input.getY();
  				Vector2 newPoints = new Vector2(touchX, touchY);
				newPoints = viewport.unproject(newPoints);
				int c = (int) (newPoints.x - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
				int r = (int) (newPoints.y - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
				r = 8 - r;
				if ((r >= 0) & (r <= 8) & (c >= 0) & (c <= 8)) {
					TupleValueIndex[][] grid = populateMatchGrid(levelReelSlotTiles);
					ReelSlotTile rst = levelReelSlotTiles.get(grid[r][c].index);
					if (!rst.isReelTileDeleted()) {
						if (!rst.isSpinning()) {
							rst.setSpinning(true);	
						} else {
							displaySpinHelp = true;
							displaySpinHelpSprite = rst.getCurrentReel();
							rst.setEndReel(displaySpinHelpSprite);
						}
					}
				} else {
					Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
				}
			}
		}
	}

    private void delegateTweenOnEvent(int type, BaseTween<?> source) {
        switch (type){
            case TweenCallback.END:
                ReelSpriteAccessor accessor = (ReelSpriteAccessor) tween.getAccessor();
                if (accessor != null) {
                    accessor.getValues(reelSlot, ReelSpriteAccessor.SCROLL_XY, returnValues);
                } else {
                    System.out.println("null!");
                }
                break;
        }
    }
	
	private boolean hiddenPatternRevealed(TupleValueIndex[][] grid) {
		boolean hiddenPattern = true;
		for (MapObject mapObject : level1.getLayers().get(HIDDEN_PATTERN_LAYER).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
			int c = (int) (mapRectangle.getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
			int r = (int) (mapRectangle.getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
			r = 8 - r;
			if ((r >= 0) & (r <= 8) & (c >= 0) & (c <= 8)) {
				if (grid[r][c] != null) {
					if (!levelReelSlotTiles.get(grid[r][c].getIndex()).isReelTileDeleted()) {
						hiddenPattern = false;
					}
				}
			} else {
				Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
			}
		}
		return hiddenPattern;
	}

    private void flashMatchedSlots(Array<TupleValueIndex> matchedSlots) {
        int index;
        for (int i = 0; i < matchedSlots.size; i++) {
            index = matchedSlots.get(i).getIndex();
            if (index  > 0) {
                levelReelSlotTiles.get(index).setFlashMode(true);
            }
        }
    }

    private void update(float delta) {
		tweenManager.update(delta);
		for (ReelSlotTile reel : levelReelSlotTiles) {
			reel.update(delta);
		}
        reelSlot.update(delta);
		renderer.setView(camera);
		if (gameOver) {
			dispose();
			game.setScreen(new EndOfGameScreen(game));
		}
	}

	@Override
	public void render(float delta) {
		update(delta);
		handleInput(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(isLoaded) {
			renderer.render();
			game.batch.begin();
			for (ReelSlotTile reel : levelReelSlotTiles) {
				if (!reel.isReelTileDeleted()) {
					reel.draw(game.batch);
				}
			}
            reelSlot.draw(game.batch);
            if(displaySpinHelp) {
				sprites[displaySpinHelpSprite].draw(game.batch);
			}
			game.batch.end();
		} else {
			if (game.assetManager.getProgress() < 1) {
				game.assetManager.update();
			} else {
				isLoaded = true;
			}
		}
        stage.draw();
	}

    @Override
    public void show() {
    }

    @Override
	public void resize(int width, int height) {
		viewport.update(width,  height);		
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
		stage.dispose();
		for (ReelSlotTile reelSlotTile : levelReelSlotTiles) {
			reelSlotTile.dispose();
		}
	}	
}
