package com.ellzone.slotpuzzle2d.prototypes.menu;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypesGame;
import com.ellzone.slotpuzzle2d.utils.UiUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MenuScreen implements Screen {
    SPPrototypesGame game;
    Skin skin;
    Stage stage;
    FitViewport viewport;
    BitmapFont font;
    String gdxVersion = "";
    Integer fps;
    boolean enteredSubScreen = false;

    public MenuScreen(SPPrototypesGame game) {
        this.game = game;
	defineMenuScreen();
    }

    private void defineMenuScreen() {
        initialiseScreen();
        font = new BitmapFont(); 
	    Gdx.input.setInputProcessor(stage);// Make the stage consume events

	    skin = new Skin();
        UiUtils.createBasicSkin(skin);
        createButtons();
    }

    private void initialiseScreen(){
	viewport = new FitViewport(SPPrototypesGame.V_WIDTH, SPPrototypesGame.V_HEIGHT, game.camera);
	stage = new Stage(viewport, game.batch);
    }

    private void createButtons() {
        TextButton button = new TextButton("Get libGDX version", skin); // Use the initialized skin
        button.setPosition(SPPrototypesGame.V_WIDTH/2 - SPPrototypesGame.V_WIDTH/8 , SPPrototypesGame.V_HEIGHT/2 - button.getHeight());
        stage.addActor(button);
	button.addListener(new ChangeListener() {
            @Override
	    public void changed(ChangeEvent event, Actor actor) {
		gdxVersion = SPPrototypesGame.gdxVersion.VERSION;
	    }
	});
    }

    @Override
    public void show() {
    }

    private void update(float delta) {
        if (enteredSubScreen) {
	    Gdx.input.setInputProcessor(stage);
	}
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
	    update(delta);
	    game.batch.begin();
	    fps = Gdx.graphics.getFramesPerSecond();
	    font.draw(game.batch,"fps:" + fps, 10, 15);
	    font.draw(game.batch, gdxVersion, SPPrototypesGame.V_WIDTH - 40, SPPrototypesGame.V_HEIGHT - 10);
	    game.batch.end();

        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
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
    }
}
