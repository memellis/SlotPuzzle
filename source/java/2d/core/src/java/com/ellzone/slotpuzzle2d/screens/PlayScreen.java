package com.ellzone.slotpuzzle2d.screens;

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
import com.ellzone.slotpuzzle2d.sprites.ReelLetter;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTile;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTileListener;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTileScroll;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedFlashingReelSlotTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningReelSlotTileEvent;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Elastic;

@SuppressWarnings("unused")
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
 	private final TweenManager tweenManager = new TweenManager();
	private boolean isLoaded = false;
	private Pixmap slotReelPixmap, slotReelScrollPixmap;
	private Texture slotReelTexture, slotReelScrollTexture;
	private Array<ReelSlotTile> slotReels;
	private Array<ReelSlotTile> levelReelSlotTiles;
	private TmxMapLoader mapLoader;
	private TiledMap map;
	private Random random;
	private OrthogonalTiledMapRenderer renderer;
	private ShapeRenderer shapeRenderer; 
	private boolean gameOver = false;
	private int touchX, touchY;
	private boolean initialFlashingStopped;
	private boolean displaySpinHelp;
	private int displaySpinHelpSprite;
	private Sprite[] sprites;
    private ReelSlotTileScroll reelSlot;

    public PlayScreen(SlotPuzzle game) {
		this.game = game;
		createPlayScreen();		
	}
	
	private void createPlayScreen() {
		random = new Random();
		Tween.setWaypointsLimit(10);
		Tween.setCombinedAttributesLimit(3);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());
		Tween.registerAccessor(ReelSlotTileScroll.class, new ReelSpriteAccessor());

		viewport = new FitViewport(800, 480, camera);
        stage = new Stage(viewport, game.batch);
        
		Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
		Assets.inst().update();
		Assets.inst().finishLoading();
		isLoaded = true;

		mapLoader = new TmxMapLoader();
		map = mapLoader.load("levels/level 1.tmx");
		renderer = new OrthogonalTiledMapRenderer(map);
		shapeRenderer = new ShapeRenderer();
		
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

		slotReels = new Array<ReelSlotTile>();

		slotReelPixmap = new Pixmap(PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT, Pixmap.Format.RGBA8888);		
		slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedPixmap(sprites, sprites.length);
		slotReelTexture = new Texture(slotReelPixmap);

		levelReelSlotTiles = new Array<ReelSlotTile>();
		initialFlashingStopped = false;
		displaySpinHelp = false;
	
		int index = 0; 
		for (MapObject mapObject : map.getLayers().get(SLOT_REEL_OBJECT_LAYER).getObjects().getByType(RectangleMapObject.class)) {
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
			index++;
		}

		levelReelSlotTiles = checkLevel(levelReelSlotTiles);

        Timeline sequence = Timeline.createSequence();
		for(int i=0; i < levelReelSlotTiles.size; i++) {
			sequence = sequence.push(Tween.set(levelReelSlotTiles.get(i), SpriteAccessor.POS_XY).target(-60f, -20f + 32*i));
		}
		sequence = sequence.pushPause(0.5f);
		for(int i = 0; i < levelReelSlotTiles.size; i++) {
			sequence = sequence.push(Tween.to(levelReelSlotTiles.get(i), SpriteAccessor.POS_XY, 0.2f).target(levelReelSlotTiles.get(i).getX(), levelReelSlotTiles.get(i).getY()));
		}		
		sequence = sequence.pushPause(0.3f).start(tweenManager);

        slotReelScrollPixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        reelSlot = new ReelSlotTileScroll(slotReelScrollTexture, slotReelTexture.getWidth(), slotReelTexture.getHeight(), 32, 32, 0, PlayScreen.SIXTY_FPS);
		Timeline reelSeq = Timeline.createSequence();
        reelSeq = reelSeq.push(Tween.set(reelSlot, ReelSpriteAccessor.SCROLL_XY).target(0f, 0f).ease(Bounce.IN));
        reelSeq = reelSeq.push(Tween.to(reelSlot, ReelSpriteAccessor.SCROLL_XY, 5.0f).target(0f, 1000f).ease(Elastic.OUT));
        reelSeq = reelSeq.
                repeat(100, 0.0f).
                start(tweenManager);

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

    private Array<ReelSlotTile> checkLevel(Array<ReelSlotTile> slotReelTiles) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
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
	
	private boolean hiddenPatternRevealed(TupleValueIndex[][] grid) {
		boolean hiddenPattern = true;
		for (MapObject mapObject : map.getLayers().get(HIDDEN_PATTERN_LAYER).getObjects().getByType(RectangleMapObject.class)) {
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
        int r, c, index;
        for (int i = 0; i < matchedSlots.size; i++) {
            index = matchedSlots.get(i).getIndex();
            if (index  > 0) {
                levelReelSlotTiles.get(index).setFlashMode(true);
            }
        }
    }

    private void clearSlotReels(Array<ReelSlotTile> levelReelSlotTiles) {
        for (int i=0; i<levelReelSlotTiles.size; i++) {
            levelReelSlotTiles.get(i).deleteReelTile();
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
			if (Assets.inst().getProgress() < 1) {
				Assets.inst().update();
			} else {
				isLoaded = true;
			}
		}
        stage.draw();
	}

    @Override
    public void show() {
        // TODO Auto-generated method stub
    }

    @Override
	public void resize(int width, int height) {
		viewport.update(width,  height);		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		stage.dispose();
		for (ReelSlotTile reelSlotTile : levelReelSlotTiles) {
			reelSlotTile.dispose();
		}
	}
	
}
