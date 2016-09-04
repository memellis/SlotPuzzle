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
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsEvent;
import com.ellzone.slotpuzzle2d.physics.Vector;
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
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;

public class PlayScreen implements Screen {
	private static final int TILE_WIDTH = 32;
	private static final int TILE_HEIGHT = 32;
	private static final int GAME_LEVEL_WIDTH = 8;
	private static final int GAME_LEVEL_HEIGHT = 8;
	private static final int SLOT_REEL_OBJECT_LAYER = 3;
	private static final int HIDDEN_PATTERN_LAYER = 0;  
	private static final float PUZZLE_GRID_START_X = 192.0f;
	private static final float PUZZLE_GRID_START_Y = 96.0f;
	private static final int TIME_INTRO_SEQUENCE_ENDS = 294;
	
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
	private Array<DampenedSineParticle> dampenedSines;
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
    private Hud hud;
    private Array<Score> scores;
	private Vector accelerator, velocityMin;
	private float acceleratorY, accelerateY, acceleratorFriction, velocityFriction, velocityY, velocityYMin;
	private Array<Timeline> endReelSeqs;
	private float slotReelScrollheight;
	float reelSlowingTargetTime;

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
		dampenedSines = new Array<DampenedSineParticle>();
		initialFlashingStopped = false;
		displaySpinHelp = false;
		hud = new Hud(game.batch);
		scores = new Array<Score>();
	}

	private void createSlotReelTexture() {
		slotReelPixmap = new Pixmap(PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT, Pixmap.Format.RGBA8888);		
		slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedPixmap(sprites, sprites.length);
		slotReelTexture = new Texture(slotReelPixmap);
        slotReelScrollPixmap = new Pixmap((int) spriteWidth, (int)spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        slotReelScrollheight = slotReelScrollTexture.getHeight();
	}
	
	private void createLevels() {
		for (MapObject mapObject : level1.getLayers().get(SLOT_REEL_OBJECT_LAYER).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
			int c = (int) (mapRectangle.getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
			int r = (int) (mapRectangle.getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
			r = GAME_LEVEL_HEIGHT - r;
			if ((r >= 0) & (r <= PlayScreen.GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= PlayScreen.GAME_LEVEL_WIDTH)) {
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
		ReelTile reel = new ReelTile(slotReelTexture, (int)spriteWidth, (int)spriteHeight, 0, 0, endReel);
		reel.setX(mapRectangle.getX());
		reel.setY(mapRectangle.getY());
		reel.setSx(0);
		int startReel = random.nextInt((int) slotReelScrollheight);
		startReel = (startReel / ((int) spriteHeight)) * (int)spriteHeight;
		reel.setSy(startReel);
		reel.addListener(new ReelTileListener() {
			@Override
			public void actionPerformed(ReelTileEvent event, ReelTile source) {
					if (event instanceof ReelStoppedSpinningEvent) {
						reelsSpinning--;
						if (hud.getWorldTime() < TIME_INTRO_SEQUENCE_ENDS) {
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
		endReelSeqs = new Array<Timeline>();
		velocityY = 4.0f;
		velocityYMin = 2.0f;
		velocityMin = new Vector(0, velocityYMin);
		acceleratorY = 3.0f;
		accelerator = new Vector(0, acceleratorY);
		accelerateY = 2.0f;
		acceleratorFriction = 0.97f;
		velocityFriction = 0.97f;
		for (ReelTile reel : reelLevel) {
			DampenedSineParticle dampenedSine = new DampenedSineParticle(0, reel.getSy(), 0, 0, 0, new Vector(0, velocityY), velocityMin, new Vector(0, acceleratorY), new Vector(0, accelerateY), velocityFriction, acceleratorFriction);
			dampenedSine.setCallback(dsCallback);
			dampenedSine.setCallbackTriggers(SPPhysicsCallback.PARTICLE_UPDATE);
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
		if (type == SPPhysicsCallback.PARTICLE_UPDATE) {
			DampenedSineParticle ds = (DampenedSineParticle)source.getSource();
			ReelTile reel = (ReelTile)ds.getUserData();
			Timeline endReelSeq = Timeline.createSequence();
			float endSy = (reel.getEndReel() * spriteHeight) % slotReelScrollheight;		
			reel.setSy(reel.getSy() % (slotReelScrollheight));
	        endReelSeq = endReelSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.SCROLL_XY, reelSlowingTargetTime)
	        		               .target(0f, endSy)
	        		               .ease(Elastic.OUT)
	        		               .setCallbackTriggers(TweenCallback.STEP + TweenCallback.END)
	        		               .setCallback(slowingSpinningCallback)
	        		               .setUserData(reel));	        					
	        endReelSeq = endReelSeq
	        				.start(tweenManager);
	        endReelSeqs.add(endReelSeq);
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
			reel.processEvent(new ReelStoppedSpinningEvent());
		}
	}
	
	private void createReelIntroSequence() {
		reelSlowingTargetTime = 4.0f;
		introSequence = Timeline.createParallel();
		for(int i=0; i < reels.size; i++) {
			introSequence = introSequence
					      .push(buildSequence(reels.get(i), i, random.nextFloat() * 3.0f, random.nextFloat() * 3.0f));
		}
				
		introSequence = introSequence
				      .pushPause(0.3f)
				      .start(tweenManager);
		
	}
	
	private Timeline buildSequence(Sprite target, int id, float delay1, float delay2) {
		return Timeline.createSequence()
			.push(SlotPuzzleTween.set(target, SpriteAccessor.POS_XY).target(-0.5f, -0.5f))
			.push(SlotPuzzleTween.set(target, SpriteAccessor.SCALE_XY).target(20, 20))
			.push(SlotPuzzleTween.set(target, SpriteAccessor.ROTATION).target(0))
			.push(SlotPuzzleTween.set(target, SpriteAccessor.OPACITY).target(0))
			.pushPause(delay1)
			.beginParallel()
				.push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
				.push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
			.end()
			.pushPause(-0.5f)
			.push(SlotPuzzleTween.to(target, SpriteAccessor.POS_XY, 0.8f).target(reels.get(id).getX(), reels.get(id).getY()).ease(Back.OUT))
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
			} else if (lonelyTile.r == GAME_LEVEL_HEIGHT) {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r-1][lonelyTile.c].index).getEndReel());
			} else if (lonelyTile.c == GAME_LEVEL_WIDTH) {
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
			r = GAME_LEVEL_HEIGHT - r;
			if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
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
			if (initialFlashingStopped){
				touchX = Gdx.input.getX();
				touchY = Gdx.input.getY();
  				Vector2 newPoints = new Vector2(touchX, touchY);
				newPoints = viewport.unproject(newPoints);
				int c = (int) (newPoints.x - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
				int r = (int) (newPoints.y - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
				r = GAME_LEVEL_HEIGHT - r;
				if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
					TupleValueIndex[][] grid = populateMatchGrid(reels);
					ReelTile reel = reels.get(grid[r][c].index);
					DampenedSineParticle ds = dampenedSines.get(grid[r][c].index);
					if (!reel.isReelTileDeleted()) {
						if (reel.isSpinning()) {
							if (ds.getDSState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
								reel.setEndReel(reel.getCurrentReel());
								displaySpinHelp = true;
								displaySpinHelpSprite = reel.getCurrentReel();
							}
						} else {
							reelSlowingTargetTime = 5.0f;
							reel.setEndReel(random.nextInt(sprites.length - 1));
					        reel.setSpinning(true);
					        reelsSpinning++;
					        reel.setSy(0);
							ds.initialiseDampenedSine();
							ds.position.y = 0;
							ds.velocity = new Vector(0, velocityY);
							accelerator = new Vector(0, acceleratorY);
							ds.accelerator = accelerator;
							ds.accelerate(new Vector(0, accelerateY));
							ds.velocityMin.y = velocityMin.y;
						}
					}
					Hud.addScore(-1);
				} else {
					Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
				}
			}
		}
	}
	
	private boolean hiddenPatternRevealed(TupleValueIndex[][] grid) {
		boolean hiddenPattern = true;
		for (MapObject mapObject : level1.getLayers().get(HIDDEN_PATTERN_LAYER).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
			int c = (int) (mapRectangle.getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
			int r = (int) (mapRectangle.getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
			r = GAME_LEVEL_HEIGHT - r;
			if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
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
        	if (dampenedSines.get(dsIndex).getDSState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
         		reel.setSy(dampenedSines.get(dsIndex).position.y);
  	       	}
			reel.update(delta);
			dsIndex++;
		}
		renderer.setView(camera);
		hud.update(delta);
		if (gameOver) {
			dispose();
			game.setScreen(new EndOfGameScreen(game));
		}
	}

	@Override
	public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(isLoaded) {
			update(delta);
			handleInput(delta);
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