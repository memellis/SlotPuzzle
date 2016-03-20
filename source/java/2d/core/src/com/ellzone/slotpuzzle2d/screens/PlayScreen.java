package com.ellzone.slotpuzzle2d.screens;

import static org.junit.Assert.assertTrue;

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
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGrid;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.Tuple;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTile;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTileListener;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningReelSlotTileEvent;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;

public class PlayScreen implements Screen {
	private static final float SIXTY_FPS = 1/60f;
	private static final int TILE_WIDTH = 32;
	private static final int TILE_HEIGHT = 32;
	private static final int SLOT_REEL_OBJECT_LAYER = 3;
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
	private Pixmap slotReelPixmap;
	private Texture slotReelTexture;
	private Array<ReelSlotTile> slotReels;
	private Array<ReelSlotTile> levelReelSlotTiles;
	private TmxMapLoader mapLoader;
	private TiledMap map;
	private Random random;
	private OrthogonalTiledMapRenderer renderer;
	private ShapeRenderer shapeRenderer; 
	private boolean gameOver = false;
	
	public PlayScreen(SlotPuzzle game) {
		this.game = game;
		createPlayScreen();		
	}
	
	private void createPlayScreen() {
		random = new Random();
		Tween.setWaypointsLimit(10);
		Tween.setCombinedAttributesLimit(3);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

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

		Sprite[] sprites = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
		
		// Fixme calculate scrollStep as a 1/4 of the sprite width
		slotReels = new Array<ReelSlotTile>();

		slotReelPixmap = new Pixmap(PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT, Pixmap.Format.RGBA8888);		
		slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedPixmap(sprites, sprites.length);
		slotReelTexture = new Texture(slotReelPixmap);

		levelReelSlotTiles = new Array<ReelSlotTile>();
	
		for (MapObject mapObject : map.getLayers().get(SLOT_REEL_OBJECT_LAYER).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
			ReelSlotTile reelSlotTile = new ReelSlotTile(this, slotReelTexture, sprites.length, sprites.length * sprites.length, SIXTY_FPS, mapRectangle.getX(), mapRectangle.getY(), random.nextInt(sprites.length)); 
			reelSlotTile.addListener(new ReelSlotTileListener() {

				@Override
				public void actionPerformed(ReelSlotTileEvent event) {
					if (event instanceof ReelStoppedSpinningReelSlotTileEvent) {
						if (ReelSlotTile.reelsSpinning == 1) {
							PuzzleGridType puzzleGrid = new PuzzleGridType();
							TupleValueIndex[][] grid = populateMatchGrid(levelReelSlotTiles);
							Array<TupleValueIndex> matchedSlots;
							matchedSlots = puzzleGrid.matchGridSlots(grid);
							flashMatchedSlots(matchedSlots);
						}
					}
				}
			});
			levelReelSlotTiles.add(reelSlotTile);
			
		}

		Timeline sequence = Timeline.createSequence();		
		for(int i=0; i < levelReelSlotTiles.size; i++) {
			sequence = sequence.push(Tween.set(levelReelSlotTiles.get(i), SpriteAccessor.POS_XY).target(-60f, -20f + 32*i));
		}
		sequence = sequence.pushPause(0.5f);
		for(int i = 0; i < levelReelSlotTiles.size; i++) {
			sequence = sequence.push(Tween.to(levelReelSlotTiles.get(i), SpriteAccessor.POS_XY, 0.2f).target(levelReelSlotTiles.get(i).getX(), levelReelSlotTiles.get(i).getY()));
		}		
		sequence = sequence.pushPause(0.3f).start(tweenManager);

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
	
	private TupleValueIndex[][] populateMatchGrid(Array<ReelSlotTile> slotReelTiles) {
		TupleValueIndex[][] matchGrid = new TupleValueIndex[9][9];
		int r, c;
		
		for (int i = 0; i < slotReelTiles.size; i++) {
			c = (int) (slotReelTiles.get(i).getX()  - 192.0) / 32;
			r = (int) (8 - (slotReelTiles.get(i).getY() - 96.0) / 32);
			matchGrid[r][c] = new TupleValueIndex(r, c, i, slotReelTiles.get(i).getEndReel());
		}
		return matchGrid;
	}

	@Override
	public void show() {
		// TODO Auto-generated method stub	
	}
	
	public void handleInput(float dt) {
		//if (Gdx.input.isKeyJustPressed(key))
	}
	
	private void update(float delta) {
		tweenManager.update(delta);
		for (ReelSlotTile reel : levelReelSlotTiles) {
			reel.update(delta);
		}
		renderer.setView(camera);
	}

	@Override
	public void render(float delta) {
		update(delta);
		if(Gdx.input.justTouched()) {
            game.setScreen(new EndOfGameScreen(game));
            dispose();
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(isLoaded) {
			renderer.render();
			game.batch.begin();
			for (ReelSlotTile reel : levelReelSlotTiles) {
				if (!reel.deleteReelTile()) {
					reel.draw(game.batch);
				}
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
		// TODO Auto-generated method stub
		stage.dispose();
		
	}
	
	private void flashMatchedSlots(Array<TupleValueIndex> matchedSlots) {
		int r, c, index;
		for (int i = 0; i < matchedSlots.size; i++) {
			index = matchedSlots.get(i).getIndex();
			levelReelSlotTiles.get(index).setFlashMode(true);
		}
	}

}
