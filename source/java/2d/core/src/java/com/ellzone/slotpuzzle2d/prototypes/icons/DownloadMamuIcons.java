package com.ellzone.slotpuzzle2d.prototypes.icons;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.graphics.GL20;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;

public class DownloadMamuIcons extends SPPrototype {
	Stage stage;
	Skin skin;
	TextButton button;

	@Override
	public void create () {
		stage = new Stage(new FitViewport(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT));
		skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
		Gdx.input.setInputProcessor(stage);
		Table container = new Table();
		container.setFillParent(true);
		button = new TextButton("Click to download", skin);
		button.getStyle().disabled = skin.newDrawable("default-round", Color.DARK_GRAY);
		container.row();
		container.add(button).width(Value.percentWidth(0.50f));
		stage.addActor(container);
		button.addListener(new ClickListener() {
				//@Override
				public void clicked (InputEvent event, float x, float y) {
					if (!button.isDisabled()) {
						button.setDisabled(true);
						// Make a GET request
						HttpRequest request = new HttpRequest(HttpMethods.GET);
						request.setTimeOut(2500);
						request.setUrl("http://icons.mameworld.info/icons.zip/icons.zip");

						// Send the request, listen for the response						
						Gdx.net.sendHttpRequest(request, new HttpResponseListener() {

								@Override
								public void cancelled() {
									// TODO: Implement this method
								}

								@Override
								public void handleHttpResponse (HttpResponse httpResponse) {
									// Determine how much we have to download
									long length = Long.parseLong(httpResponse.getHeader("Content-Length"));
									// We're going to download the file to external storage, create the streams
									InputStream is = httpResponse.getResultAsStream();
									OutputStream os = Gdx.files.external("mamuicons.zip").write(false);
									byte[] bytes = new byte[1024];
									int count = -1;
									long read = 0;
									try {
										// Keep reading bytes and storing them until there are no more.
										while ((count = is.read(bytes, 0, bytes.length)) != -1) {
											os.write(bytes, 0, count);
											read += count;

											// Update the UI with the download progress
											final int progress = ((int) (((double) read / (double) length) * 100));
											final String progressString = progress == 100 ? "Click to download" : progress + "%";

											// Since we are downloading on a background thread, post a runnable to touch ui											
											Gdx.app.postRunnable(new Runnable() {
													@Override
													public void run () {
														if (progress == 100) {
															button.setDisabled(false);
														}													
														button.setText(progressString);
													}
												});
										}
									} catch (IOException e) {
									}
								}

								@Override
								public void failed (Throwable t) {
									Gdx.app.postRunnable(new Runnable() {
											@Override
											public void run () {
												button.setText("Too bad. Download failed.");
											}
										});
								}
							});
					}
				}
			});
	}


	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act();
		stage.draw();
	}
}
