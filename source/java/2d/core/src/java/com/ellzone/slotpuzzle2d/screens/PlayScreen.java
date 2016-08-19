package com.ellzone.slotpuzzle2d.screens;

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.physics.DampenedSine;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsEvent;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedFlashingEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.Score;
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
	private Sprite cheesecake, cherry, grapes, jelly, lemon, peach, pear, tomato;
	private float spriteWidth, spriteHeight;
 	private final TweenManager tweenManager = new TweenManager();
 	private Timeline introSequence;
 	private TextureAtlas reelAtlas;
	private boolean isLoaded = false;
	private Pixmap slotReelPixmap, slotReelScrollPixmap;
	private Texture slotReelTexture, slotReelScrollTexture;
	private Array<ReelTile> reels;
	private int reelsSpinning;
	private Array<DampenedSine> dampenedSines;
	private TiledMap level1;
	private Random random;
	private OrthogonalTiledMapRenderer renderer;
	private boolean gameOver = false;
	private boolean win = false;
	private int touchX, touchY;
	private boolean initialFlashingStopped;
	private boolean displaySpinHelp;
	private int displaySpinHelpSprite;
	private Sprite[] sprites;
    private ReelTile reelTile;
    private SlotPuzzleTween tween;
    private float returnValues[] = new float[2];
    private boolean tweenClicked = false;
    private Hud hud;
    private Array<Score> scores;

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
   	}
	
	private void initialiseScreen() {
		viewport = new FitViewport(SlotPuzzle.V_WIDTH, SlotPuzzle.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);		
	}
	
	private void initialiseTweenEngine() {
		SlotPuzzleTween.setWaypointsLimit(10);
		SlotPuzzleTween.setCombinedAttributesLimit(3);
		SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
		SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
		SlotPuzzleTween.registerAccessor(Score.class, new ScoreAccessor());
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

	private void initialisePlayScreen() {
		random = new Random();
		renderer = new OrthogonalTiledMapRenderer(level1);
		reels = new Array<ReelTile>();
		dampenedSines = new Array<DampenedSine>();
		initialFlashingStopped = false;
		displaySpinHelp = false;
		hud = new Hud(game.batch);
		scores = new Array<Score>();
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
				addReel(mapRectangle);
			} else {
				Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "I don't respond to grid r="+r+" c="+c+". There it won't be added to the level! Sort it out in a level editor.");				
			}
		}
		reelsSpinning = reels.size - 1;
		reels = checkLevel(reels);
		reels = adjustForAnyLonelyReels(reels);
		createDampenedSines(reels);
	}

	private void addReel(Rectangle mapRectangle) {
        int endReel = random.nextInt(sprites.length);
		ReelTile reel = new ReelTile(slotReelTexture, (int)spriteWidth, (int)spriteHeight, mapRectangle.getX(), mapRectangle.getY(), endReel);
		reel.addListener(new ReelTileListener() {
			@Override
			public void actionPerformed(ReelTileEvent event, ReelTile source) {
					if (event instanceof ReelStoppedSpinningEvent) {
						reelsSpinning--;
						if (hud.getWorldTime() < 293) {
							if (reelsSpinning <= -1) {
								if (testForHiddenPatternRevealed(reels)) {
									gameOver = true;
									win = true;
								}
							}
						}
					}
					if ((event instanceof ReelStoppedFlashingEvent) & (initialFlashingStopped)) {							
						if (testForAnyLonelyReels(reels)) {
							gameOver = true;
							System.out.println("I think its game over and you lose!");
							win = false;
						}
						if (testForHiddenPatternRevealed(reels)) {
							gameOver = true;
							win = true;
						}
						reelScoreAnimation(source);
						deleteReelAnimation(source);
					}
					if ((event instanceof ReelStoppedFlashingEvent) & (!initialFlashingStopped)) {
						initialFlashingStopped = true;				
					}	
				}
			}
		);
		reels.add(reel);		
	}
	
	private void createDampenedSines(Array<ReelTile> reelLevel) {
		DampenedSine dampenedSine; 
		for (ReelTile reel : reelLevel) {
			dampenedSine = new DampenedSine(0, reel.getSy(), 0, 0, 0, slotReelTexture.getHeight() * 80, slotReelTexture.getHeight(), reel.getEndReel());
			dampenedSine.setCallback(dsCallback);
			dampenedSine.setCallbackTriggers(SPPhysicsCallback.PARTICLE_UPDATE + SPPhysicsCallback.END);
			dampenedSine.setUserData(reel);
			dampenedSines.add(dampenedSine);
		}
	}
	
	private SPPhysicsCallback dsCallback = new SPPhysicsCallback() {
		@Override
		public void onEvent(int type, SPPhysicsEvent source) {
			delegateDSCallback(type, source); 
		}
	};
	
	private void delegateDSCallback(int type, SPPhysicsEvent source) {
		if (type == SPPhysicsCallback.END) {
			DampenedSine ds = (DampenedSine)source.getSource();
			ReelTile reel = (ReelTile)ds.getUserData();
			reel.setSpinning(false);
			reel.processEvent(new ReelStoppedSpinningEvent());
		}
	}
	
	private void createReelIntroSequence() {		
		introSequence = Timeline.createParallel();
		for(int i=0; i < reels.size; i++) {
			introSequence = introSequence
					      .push(buildSequence(reels.get(i), i, random.nextFloat() * 5.0f, random.nextFloat() * 5.0f));
		}
				
		introSequence = introSequence
				      .pushPause(0.3f)
				      .start(tweenManager);
		
        slotReelScrollPixmap = new Pixmap((int) spriteWidth, (int)spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        reelTile = new ReelTile(slotReelScrollTexture, (int) spriteWidth, (int) spriteHeight, 0, 32, 0);
        reelTile.setX(0);
        reelTile.setY(32);
        reelTile.setEndReel(random.nextInt(sprites.length));

        tween = SlotPuzzleTween.to(reelTile, ReelAccessor.SCROLL_XY, 20.0f) .target(0,  2560 + reelTile.getEndReel() * 32) .ease(Sine.OUT).setCallback(new TweenCallback() {
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
			.push(SlotPuzzleTween.to(target, SpriteAccessor.POS_XY, 1.0f).target(reels.get(id).getX(), reels.get(id).getY()).ease(Back.OUT))
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

    private Array<ReelTile> checkLevel(Array<ReelTile> reelLevel) {
        TupleValueIndex[][] grid = populateMatchGrid(reelLevel);
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
        return reelLevel;
    }
	
	boolean testForHiddenPatternRevealed(Array<ReelTile> levelReel) {
		PuzzleGridType puzzleGrid = new PuzzleGridType();
		TupleValueIndex[][] grid = populateMatchGrid(levelReel);
		Array<TupleValueIndex> matchedSlots;
		matchedSlots = puzzleGrid.matchGridSlots(grid);
		for (TupleValueIndex matchedSlot : matchedSlots) {
			levelReel.get(matchedSlot.index).setScore(matchedSlot.value);
		}
 		flashMatchedSlots(matchedSlots);
		return hiddenPatternRevealed(grid);	
	}

	boolean testForAnyLonelyReels(Array<ReelTile> levelReel) {		
		PuzzleGridType puzzleGrid = new PuzzleGridType();
		TupleValueIndex[][] grid = populateMatchGrid(levelReel);
		return puzzleGrid.anyLonelyTiles(grid);
	}
	
	Array<ReelTile> adjustForAnyLonelyReels(Array<ReelTile> levelReel) {
		PuzzleGridType puzzleGrid = new PuzzleGridType();
		TupleValueIndex[][] grid = populateMatchGrid(levelReel);
		Array<TupleValueIndex> lonelyTiles = puzzleGrid.getLonelyTiles(grid);
		for (TupleValueIndex lonelyTile : lonelyTiles) {
			if (lonelyTile.r == 0) {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r+1][lonelyTile.c].index).getEndReel());
			} else if (lonelyTile.c == 0) {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c+1].index).getEndReel());
			} else if (lonelyTile.r == 8) {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r-1][lonelyTile.c].index).getEndReel());
			} else if (lonelyTile.c == 8) {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c-1].index).getEndReel());
			} else {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r+1][lonelyTile.c].index).getEndReel());
			}
		}
		return levelReel;
	}
	
	private TupleValueIndex[][] populateMatchGrid(Array<ReelTile> reelLevel) {
		TupleValueIndex[][] matchGrid = new TupleValueIndex[9][9];
		int r, c;
		
		for (int i = 0; i < reelLevel.size; i++) {
			c = (int) (reelLevel.get(i).getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
			r = (int) (reelLevel.get(i).getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
			r = 8 - r;
			if ((r >= 0) & (r <= 8) & (c >= 0) & (c <= 8)) {
				if (reelLevel.get(i).isReelTileDeleted()) {
					matchGrid[r][c] = new TupleValueIndex(r, c, i, -1);
				} else {
					matchGrid[r][c] = new TupleValueIndex(r, c, i, reelLevel.get(i).getEndReel());
				}
			} else {
				Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "I don't respond to r="+r+" c="+c);
			}
		}
		return matchGrid;
	}
	
	private void deleteReelAnimation(ReelTile source) {
		Timeline.createSequence()
		    .beginParallel()
			    .push(SlotPuzzleTween.to(source, SpriteAccessor.SCALE_XY, 0.3f).target(6, 6).ease(Quad.IN))
			    .push(SlotPuzzleTween.to(source, SpriteAccessor.OPACITY, 0.3f).target(0).ease(Quad.IN))
		    .end()
		    .setUserData(source)
		    .setCallback(deleteReelCallback)
		    .setCallbackTriggers(TweenCallback.COMPLETE)
			.start(tweenManager);
	}

	private TweenCallback deleteReelCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			switch (type) {
				case COMPLETE: 
					ReelTile reel = (ReelTile) source.getUserData();
					Hud.addScore((reel.getEndReel() + 1) * reel.getScore());
					reel.deleteReelTile();
			}
		}
	};

	private void reelScoreAnimation(ReelTile source) {
		Score score = new Score(source.getX(), source.getY(), (source.getEndReel() + 1) * source.getScore());
		scores.add(score);
		Timeline.createSequence()
			.beginParallel()
				.push(SlotPuzzleTween.to(score, ScoreAccessor.POS_XY, 2.0f).targetRelative(random.nextInt(20), random.nextInt(160)).ease(Quad.IN))
				.push(SlotPuzzleTween.to(score, ScoreAccessor.SCALE_XY, 2.0f).target(2.0f, 2.0f).ease(Quad.IN))
			.end()
			.setUserData(score)
			.setCallback(deleteScoreCallback)
			.setCallbackTriggers(TweenCallback.COMPLETE)
			.start(tweenManager);	
	}
	
	private TweenCallback deleteScoreCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			switch (type) {
			case COMPLETE:
				Score score = (Score) source.getUserData();
				scores.removeValue(score, false);
			}
		}
	};
	

	public void handleInput(float dt) {
		if (Gdx.input.justTouched()) {
            touchX = Gdx.input.getX();
            touchY = Gdx.input.getY();
            Vector2 unProjTouch = new Vector2(touchX, touchY);
            unProjTouch = viewport.unproject(unProjTouch);
            if ((unProjTouch.x >= 0) & (unProjTouch.x <= spriteWidth) & (unProjTouch.y >= spriteHeight) & (unProjTouch.y <= 64)) {
                if (tween.getCurrentTime() == 0) {
                    tweenClicked = false;
                    reelTile.setEndReel(random.nextInt(sprites.length));
                    tween = SlotPuzzleTween.to(reelTile, ReelAccessor.SCROLL_XY, 20.0f) .target(0,  returnValues[1] + (2560 - returnValues[1] % 2560) + reelTile.getEndReel() * 32) .ease(Sine.OUT).setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            delegateTweenOnEvent(type, source);
                        }
                    }) .setCallbackTriggers(TweenCallback.END)
                            .start(tweenManager);
                } else {
                    if ((tween.getCurrentTime() < tween.getDuration() / 2.0f) & (!tweenClicked)) {
                        tweenClicked = true;
                        reelTile.setEndReel();
                        tween = tween.target(0, returnValues[1] + (1280 - (returnValues[1] % 1280)) + reelTile.getEndReel() * 32);
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
					TupleValueIndex[][] grid = populateMatchGrid(reels);
					ReelTile reel = reels.get(grid[r][c].index);
					DampenedSine ds = dampenedSines.get(grid[r][c].index);
					if (!reel.isReelTileDeleted()) {
						if (!reel.isSpinning()) {
								reel.setSpinning(true);	
								reelsSpinning++;
								reel.setEndReel(random.nextInt(sprites.length - 1));
								ds.setEndReel(reel.getEndReel());
								ds.initialiseDampenedSine();	
								ds.position.y = 0;
								ds.setDampPoint(slotReelTexture.getHeight() * 20);
						} else {
							if(ds.getDSState() == DampenedSine.DSState.UPDATING_DAMPENED_SINE) {
								displaySpinHelp = true;
								displaySpinHelpSprite = reel.getCurrentReel();
								reel.setEndReel(displaySpinHelpSprite);
								ds.setEndReel(reel.getEndReel());
							}
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
                ReelAccessor accessor = (ReelAccessor) tween.getAccessor();
                if (accessor != null) {
                    accessor.getValues(reelTile, ReelAccessor.SCROLL_XY, returnValues);
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
					if (!reels.get(grid[r][c].getIndex()).isReelTileDeleted()) {
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
                reels.get(index).setFlashMode(true);
            }
        }
    }

    private void update(float delta) {
		tweenManager.update(delta);
		int dsIndex = 0;
		for (ReelTile reel : reels) {
        	dampenedSines.get(dsIndex).update();
  		    reel.setSy(dampenedSines.get(dsIndex).position.y);
			reel.update(delta);
			dsIndex++;
		}
        reelTile.update(delta);
		renderer.setView(camera);
		hud.update(delta);
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
			for (ReelTile reel : reels) {
				if (!reel.isReelTileDeleted()) {
					reel.draw(game.batch);
				}
			}
			for (Score score : scores) {
				score.render(game.batch);
			}
            reelTile.draw(game.batch);
            if(displaySpinHelp) {
				sprites[displaySpinHelpSprite].draw(game.batch);
			}
			game.batch.end();
		    game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
		    hud.stage.draw();
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
		Gdx.app.log(SlotPuzzle.SLOT_PUZZLE + "PlayScreen", "show");
    }

    @Override
	public void resize(int width, int height) {
		viewport.update(width,  height);		
	}

	@Override
	public void pause() {
		Gdx.app.log(SlotPuzzle.SLOT_PUZZLE + "PlayScreen", "pause");
	}

	@Override
	public void resume() {
		Gdx.app.log(SlotPuzzle.SLOT_PUZZLE + "PlayScreen", "resume");
	}

	@Override
	public void hide() {
		Gdx.app.log(SlotPuzzle.SLOT_PUZZLE + "PlayScreen", "hide");
	}

	@Override
	public void dispose() {
		stage.dispose();
		for (ReelTile reel : reels) {
			reel.dispose();
		}
	}	
}