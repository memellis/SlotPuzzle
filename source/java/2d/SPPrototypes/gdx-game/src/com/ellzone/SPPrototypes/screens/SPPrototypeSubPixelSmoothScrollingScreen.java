package com.ellzone.SPPrototypes.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ellzone.SPPrototypes.SPPrototypes;
import com.ellzone.SPPrototypes.utils.UiUtils;

public class SPPrototypeSubPixelSmoothScrollingScreen implements Screen {
	private SPPrototypes game;
	Skin skin;
	Stage stage;
	FitViewport viewport;
	BitmapFont font;
	String message = "";
		
	public SPPrototypeSubPixelSmoothScrollingScreen(SPPrototypes game) {
		this.game = game;			
		defineSubPixelSmoothScrollingScreen();
	}
	
	private void defineSubPixelSmoothScrollingScreen() {
		initialiseScreen();
		font = new BitmapFont(); 
	    Gdx.input.setInputProcessor(stage);// Make the stage consume events
		skin = new Skin();
		UiUtils.createBasicSkin(skin);
		createButtons();
	}
	
	private void initialiseScreen(){
		viewport = new FitViewport(SPPrototypes.V_WIDTH, SPPrototypes.V_HEIGHT, game.camera);
		stage = new Stage(viewport, game.batch);
    }

	private void createButtons() {
        TextButton button = new TextButton("Exit Smooth Scrolling screen", skin); // Use the initialized skin
        button.setPosition(SPPrototypes.V_WIDTH/2 - SPPrototypes.V_WIDTH/8 , SPPrototypes.V_HEIGHT/2 + button.getHeight());
        stage.addActor(button);
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(game.getPreviousScreen());
				Gdx.input.setInputProcessor(stage);				
			}
		});
	}	
	
	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		game.batch.begin();
		font.draw(game.batch, message, SPPrototypes.V_WIDTH - 40, SPPrototypes.V_HEIGHT - 10); 		   
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
