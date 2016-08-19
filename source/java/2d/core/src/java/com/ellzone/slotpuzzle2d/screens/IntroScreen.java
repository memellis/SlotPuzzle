package com.ellzone.slotpuzzle2d.screens;

import java.io.IOException;
import java.util.Random;

import org.jrenner.smartfont.SmartFontGenerator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.sprites.ReelLetter;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Elastic;

public class IntroScreen implements Screen {
	private static final int VIEWPORT_WIDTH = 800;
	private static final int VIEWPORT_HEIGHT = 480;
    private static final int TEXT_SPACING_SIZE = 30;
    private static final float SIXTY_FPS = 1 / 60f;
    private static final int EXO_FONT_SMALL_SIZE = 24;
    private static final int REEL_SIZE_WIDTH = 40;
    private static final int REEL_SIZE_HEIGHT = 40;
    private static final int SCROLL_STEP = 4;
    private static final int SCROLL_HEIGHT = 20;
    private static final String COPYRIGHT = "\u00a9";
    private static final String SLOT_PUZZLE_REEL_TEXT = "Slot Puzzle";
    private static final String BY_TEXT = "by";
    private static final String AUTHOR_TEXT = "Mark Ellis";
    private static final String COPYRIGHT_YEAR_AUTHOR_TEXT = COPYRIGHT + "2016 Mark Ellis";
    private SlotPuzzle game;
    private Texture texture;
    private Pixmap slotReelPixmap;
    private Texture slotReelTexture;
    private final OrthographicCamera camera = new OrthographicCamera();
    private Viewport viewport;
    private Stage stage;
    private BitmapFont fontSmall;
    private BitmapFont fontMedium;
    private BitmapFont fontLarge;
    private Array<ReelLetter> introScreenLetters;
    private boolean endOfIntroScreen;
    private TextButton button;
    private TextButtonStyle textButtonStyle;
    private Skin skin;
    private TextureAtlas buttonAtlas;
    private TweenManager tweenManager = new TweenManager();
    private Sprite cheesecake;
    private Sprite cherry;
    private Sprite grapes;
    private Sprite jelly;
    private Sprite lemon;
    private Sprite peach;
    private Sprite pear;
    private Sprite tomato;
    private Sprite[] sprites;
    private ReelTile reelTile;
    private boolean isLoaded = false;
    private Random random;

    public IntroScreen(SlotPuzzle game) {
        this.game = game;
        defineIntroScreen();
    }

    void defineIntroScreen() {
    	initialiseIntroScreen();
        initialiseTweenEngine();
        initialiseFonts();
        initialiseIntroScreenText();
        initialiseUiStage();        
        initialiseIntroSequence();
    }
    
    private void initialiseIntroScreen() {
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        ReelLetter.instanceCount = 0;
        endOfIntroScreen = false;
        Gdx.input.setInputProcessor(stage);
        random = new Random();
    }

    private void initialiseTweenEngine() {
        Tween.setWaypointsLimit(10);
        Tween.setCombinedAttributesLimit(3);
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
        Tween.registerAccessor(ReelTile.class, new ReelAccessor());    	
    }
    
    private void initialiseFonts() {
        SmartFontGenerator fontGen = new SmartFontGenerator();
        FileHandle exoFileInternal = Gdx.files.internal("LiberationMono-Regular.ttf");
        FileHandle generatedFontDir = Gdx.files.local("generated-fonts/");
        generatedFontDir.mkdirs();

        FileHandle exoFile = Gdx.files.local("generated-fonts/LiberationMono-Regular.ttf");

        try {
            FileUtils.copyFile(exoFileInternal, exoFile);
        } catch (IOException ex) {
            Gdx.app.error(SlotPuzzle.SLOT_PUZZLE, "Could not copy " + exoFileInternal.file().getPath() + " to file " + exoFile.file().getAbsolutePath() + " " + ex.getMessage());
        }

        fontSmall = fontGen.createFont(exoFile, "exo-small", 24);
        fontMedium = fontGen.createFont(exoFile, "exo-medium", 48);
        fontLarge = fontGen.createFont(exoFile, "exo-large", 64);    	
    }
    
    private void initialiseIntroScreenText() {
        if (Gdx.files.local("SlotPuzzleTextFontTile.png").exists()) {
            texture = new Texture(Gdx.files.local("SlotPuzzleTextFontTile.png"));
            Gdx.app.log(SlotPuzzle.SLOT_PUZZLE, "Loaded cached SlotPuzzleTextFontTile.png file.");

        } else {
            introScreenLetters = new Array<ReelLetter>();
            createReelLetterString(IntroScreen.SLOT_PUZZLE_REEL_TEXT, introScreenLetters, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + IntroScreen.TEXT_SPACING_SIZE + 10);
            createReelLetterString(IntroScreen.BY_TEXT, introScreenLetters, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + IntroScreen.TEXT_SPACING_SIZE + 10);
            createReelLetterString(IntroScreen.AUTHOR_TEXT, introScreenLetters, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + IntroScreen.TEXT_SPACING_SIZE + 10);
            createReelLetterString(IntroScreen.COPYRIGHT_YEAR_AUTHOR_TEXT, introScreenLetters, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + IntroScreen.TEXT_SPACING_SIZE + 10);
        }    	
    }
    
    private void createReelLetterString(String reelLetterString, Array<ReelLetter> screenLetters, float posX, float posY) {
        Pixmap slotReelPixmap = new Pixmap(IntroScreen.REEL_SIZE_WIDTH, reelLetterString.length() * IntroScreen.REEL_SIZE_HEIGHT, Pixmap.Format.RGBA8888);
        slotReelPixmap = PixmapProcessors.createDynamicVerticalFontText(fontSmall, reelLetterString, slotReelPixmap);
        slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedVerticalText(slotReelPixmap, IntroScreen.SCROLL_HEIGHT, reelLetterString, IntroScreen.EXO_FONT_SMALL_SIZE, IntroScreen.SCROLL_STEP);
        Texture slotReelTexture = new Texture(slotReelPixmap);

        for (int i = 0; i < reelLetterString.length(); i++) {
            introScreenLetters.add(new ReelLetter(this, slotReelTexture, reelLetterString.length(), slotReelPixmap.getWidth() / IntroScreen.REEL_SIZE_WIDTH, SIXTY_FPS, (i * IntroScreen.TEXT_SPACING_SIZE) + posX, posY, i));
        }
    }
    
    private void initialiseUiStage() {
        skin = new Skin();
        buttonAtlas = new TextureAtlas(Gdx.files.internal("ui/ui-blue.atlas"));
        skin.addRegions(buttonAtlas);
        textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = fontSmall;
        textButtonStyle.up = skin.getDrawable("icon_arrow_up");
        textButtonStyle.down = skin.getDrawable("icon_arrow_down");
        textButtonStyle.checked = skin.getDrawable("checkbox_on");
        button = new TextButton("                  ", textButtonStyle);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                endOfIntroScreen = true;
            }
        });

        Label.LabelStyle font = new Label.LabelStyle(fontSmall, Color.WHITE);
        Label buttonPressLabel = new Label("LAUNCH", font);

        Table table = new Table();
        table.bottom();
        table.setFillParent(true);
        table.add(button).expandX().padTop(50f);
        table.row();
        table.add(buttonPressLabel).expandX();

        stage.addActor(table);
        viewport.update(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);    	
    }

    private void initialiseIntroSequence() {
        Timeline introSeq = Timeline.createSequence();
        for (int i = 0; i < introScreenLetters.size; i++) {
            introSeq = introSeq.push(Tween.set(introScreenLetters.get(i), SpriteAccessor.POS_XY).target(-40f, -20f + i * 20f));
        }

        introSeq = introSeq.pushPause(1.0f);
        for (int i = 0; i < SLOT_PUZZLE_REEL_TEXT.length(); i++) {
            introSeq = introSeq.push(Tween.to(introScreenLetters.get(i), SpriteAccessor.POS_XY, 0.4f).target(250f + i * 30f, 280f));
        }

        int startOfText = SLOT_PUZZLE_REEL_TEXT.length();
        int endOfText = SLOT_PUZZLE_REEL_TEXT.length() + BY_TEXT.length();
        for (int i = startOfText; i < endOfText; i++) {
            introSeq = introSeq.push(Tween.to(introScreenLetters.get(i), SpriteAccessor.POS_XY, 0.4f).target(60f + i * 30f, 240f));
        }

        startOfText = endOfText;
        endOfText = startOfText + AUTHOR_TEXT.length();
        for (int i = startOfText; i < endOfText; i++) {
            introSeq = introSeq.push(Tween.to(introScreenLetters.get(i), SpriteAccessor.POS_XY, 0.4f).target(-120f + i * 30f, 200f));
        }

        startOfText = endOfText;
        endOfText = startOfText + COPYRIGHT_YEAR_AUTHOR_TEXT.length();
        for (int i = startOfText; i < endOfText; i++) {
            introSeq = introSeq.push(Tween.to(introScreenLetters.get(i), SpriteAccessor.POS_XY, 0.4f).target(-520f + i * 30f, 90f));
        }

        initialiseAssets();
        
        slotReelPixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        slotReelPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelTexture = new Texture(slotReelPixmap);

        reelTile = new ReelTile(slotReelTexture, slotReelTexture.getWidth(), slotReelTexture.getHeight(), 32, 32, 0);

        Timeline reelSeq = Timeline.createSequence();
        reelSeq = reelSeq.push(Tween.set(reelTile, ReelAccessor.SCROLL_XY).target(0f, 0f).ease(Bounce.IN));
        reelSeq = reelSeq.push(Tween.to(reelTile, ReelAccessor.SCROLL_XY, 5.0f).target(0f, 32.0f * 8 * 3 + random.nextInt(slotReelTexture.getHeight() / 32) * 32).ease(Elastic.OUT));

        introSeq = introSeq
                .start(tweenManager);

        reelSeq = reelSeq.
                repeat(100, 0.0f).
                push(Tween.to(reelTile, ReelAccessor.SCROLL_XY, 5.0f).target(0f, 32.0f * 8 * 3 + random.nextInt(slotReelTexture.getHeight() / 32) * 32).ease(Elastic.OUT)).
                start(tweenManager);
    }
    
    private void initialiseAssets() {
        Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();
        isLoaded = true;

        TextureAtlas atlas = Assets.inst().get("reel/reels.pack.atlas", TextureAtlas.class);
        cherry = atlas.createSprite("cherry");
        cheesecake = atlas.createSprite("cheesecake");
        grapes = atlas.createSprite("grapes");
        jelly = atlas.createSprite("jelly");
        lemon = atlas.createSprite("lemon");
        peach = atlas.createSprite("peach");
        pear = atlas.createSprite("pear");
        tomato = atlas.createSprite("tomato");

        sprites = new Sprite[]{cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
        for (Sprite sprite : sprites) {
            sprite.setOrigin(0, 0);
        }
    }
    
    public void update(float dt) {
        tweenManager.update(dt);
        for (ReelLetter reel : introScreenLetters) {
            reel.update(dt);
        }
        reelTile.update(dt);
        if (ReelLetter.instanceCount == 0 | endOfIntroScreen) {
            game.setScreen(new PlayScreen(game));
            dispose();
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (isLoaded) {
            game.batch.begin();
            for (ReelLetter reel : introScreenLetters) {
                reel.draw(game.batch);
            }
            reelTile.draw(game.batch);
            game.batch.end();
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        fontSmall.newFontCache();
    }

    @Override
    public void show() {
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
        if (tweenManager != null) tweenManager.killAll();
        if (slotReelPixmap != null) slotReelPixmap.dispose();
        if (slotReelTexture != null) slotReelTexture.dispose();
        if (texture != null) texture.dispose();
        if (fontSmall != null) fontSmall.dispose();
        if (fontMedium != null) fontMedium.dispose();
        if (fontLarge != null) fontLarge.dispose();
    }

    public SlotPuzzle getGame() {
        return this.game;
    }
}