package com.ellzone.SPPrototypes.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.async.AsyncExecutor;
import com.badlogic.gdx.utils.async.AsyncResult;
import com.badlogic.gdx.utils.async.AsyncTask;
import com.ellzone.SPPrototypes.SPPrototypes;
import com.ellzone.SPPrototypes.utils.UiUtils;
import com.ellzone.utils.FileUtils;
import com.ellzone.utils.JavaArchive;
import java.util.logging.*;
import java.io.*;
import com.ellzone.utils.*;
import java.util.*;


public class SPPrototypeCreateSPGdxJarScreen implements Screen {
	private SPPrototypes game;
	Skin skin;
	Stage stage;
	FitViewport viewport;
	BitmapFont font;
	JavaArchive javaArchive;
	String message = "";
	AsyncExecutor executor;
	AsyncResult<Void> task;
	final FileHandle jarArchive = Gdx.files.external("AppProjects/SPPrototypes/gdx-game/libs/gdx.jar");
	final FileHandle extractDir = Gdx.files.external("AppProjects/SPPrototypes/gdxjar");
	

	public SPPrototypeCreateSPGdxJarScreen(SPPrototypes game) {
		this.game = game;			
		defineSPGdxJarScreen();
	}

	private void defineSPGdxJarScreen() {
		initialiseScreen();
		font = new BitmapFont(); 
	    Gdx.input.setInputProcessor(stage);// Make the stage consume events
		skin = new Skin();
		UiUtils.createBasicSkin(skin);
		javaArchive = new JavaArchive();
		executor = new AsyncExecutor(1);
		createButtons();
	}

	private void initialiseScreen(){
		viewport = new FitViewport(SPPrototypes.V_WIDTH, SPPrototypes.V_HEIGHT, game.camera);
		stage = new Stage(viewport, game.batch);
    }

	private void createButtons() {
        TextButton button = new TextButton("Create SP GDX Jar screen", skin); // Use the initialized skin
        button.setPosition(SPPrototypes.V_WIDTH/2 - SPPrototypes.V_WIDTH/8 , SPPrototypes.V_HEIGHT/2 - button.getHeight());
        stage.addActor(button);
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				//createSPGdxJar();
				findJavaFiles();
			}
		});
		button = new TextButton("Exit Create SP GDX Jar screen", skin); // Use the initialized skin
        button.setPosition(SPPrototypes.V_WIDTH/2 - SPPrototypes.V_WIDTH/8 , SPPrototypes.V_HEIGHT/2 - 2 * button.getHeight());
        stage.addActor(button);
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(game.getPreviousScreen());
				Gdx.input.setInputProcessor(stage);				
			}
		});
	}	
	
	private void createSPGdxJar() {
		message = "Status: About to create SpGdx.jar";
		FileHandle cwd = Gdx.files.local(".");
		message = "Absolute path=" + cwd.file().getAbsolutePath();
		boolean isExternalAvailable = Gdx.files.isExternalStorageAvailable();
		if (isExternalAvailable) {
			message = "External Storage is available";
		}
				
		if (jarArchive.exists()) {
			message = "JAR archive file: " + jarArchive.file().getAbsolutePath() + " exists";
			task = executor.submit(new AsyncTask<Void>() {
					public Void call() throws IOException {
						javaArchive.extractJar(jarArchive.file().getAbsolutePath(), extractDir.file().getAbsolutePath());
						findJavaFiles();
						return null;
					} 
				});
		}
	}
	
	private void findJavaFiles() {
		HashSet<String> names = new HashSet<String>();
		HashMap<String, FileHandle> filehandles = new HashMap<String, FileHandle>();
		message = "Finding Java Files";
		FileUtils.gatherJavaFiles(extractDir, names, filehandles, true);
		String namesA[] = new String[names.size()];
		names.toArray(namesA);
		for (int i=0; i < names.size(); i++) {
			message = "Java file =" + namesA[i];
		}
	}

	@Override
	public void show() {
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (task != null && !task.isDone()) {
			message = "" + javaArchive.getNumJarElementsExtracted();
		}
		if (task != null && task.isDone()) {
			message = "JAR archive file: " + jarArchive.file().getAbsolutePath() + " extracted";
		}
		game.batch.begin();
		font.draw(game.batch, message, 0, SPPrototypes.V_HEIGHT - 10); 		   
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
