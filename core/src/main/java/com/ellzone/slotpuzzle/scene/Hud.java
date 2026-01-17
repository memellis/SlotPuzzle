/*
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ellzone.slotpuzzle.scene;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle.SlotPuzzleConstants;

import lombok.Getter;

public class Hud implements Disposable {

    public Stage stage;

    private Integer worldTimer;
    @Getter
    private boolean timeUp, startWorldTimer;
    private float timeCount = 0;
    private Integer score;
    private Integer lives;
    private String levelName;
    private final Label scoreLabel;
    private final Label livesLeftLabel;
    private final Label countdownLabel;
    private final Label timeLabel;
    private final Label levelLabel;

    @SuppressWarnings("DefaultLocale")
    public Hud(SpriteBatch batch) {
        worldTimer = 300;
        score = 0;
        lives = 3;
        levelName = "1-1";
        timeUp = false;
        startWorldTimer = false;

        Viewport viewport = new FitViewport(SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, batch);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        livesLeftLabel = new Label(String.format("%03d", lives), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        levelLabel = new Label(levelName, new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Label worldLabel = new Label("LEVEL", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Label scoreLabelHeading = new Label("SCORE", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        Label livesLabel = new Label("LIVES", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(scoreLabelHeading).expandX().padTop(10);
        table.add(livesLabel).expandX().padTop(10);
        table.add(worldLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(livesLeftLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countdownLabel).expandX();

        stage.addActor(table);
    }

    @SuppressWarnings("DefaultLocale")
    public void update(float dt) {
        if (startWorldTimer) {
            timeCount += dt;
            if(timeCount >= 1){
                if (worldTimer > 0)
                    worldTimer--;
                else
                    timeUp = true;
                timeCount = 0;
            }
        }
        countdownLabel.setText(String.format("%03d", worldTimer));
    }

    public void addScore(int value) {
        score += value;
        scoreLabel.setText(String.format("%06d", score));
    }

    public void resetScore() {
        score = 0;
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void setLevelName(String name) {
        levelName = name;
        if (levelLabel != null)
            levelLabel.setText(levelName);
    }

    public Integer getWorldTime() {
        return worldTimer;
    }

    public void resetWorldTime(int time) {
        worldTimer = time;
    }

    public void setCountDownVisible(boolean visible) {
        countdownLabel.setVisible(visible);
        timeLabel.setVisible(visible);
    }

    public void startWorldTimer() {
        startWorldTimer = true;
    }

    public void stopWorldTimer() {
        startWorldTimer = false;
    }

    @SuppressWarnings("DefaultLocale")
    public void loseLife() {
        if (lives > 0)
            lives--;
        livesLeftLabel.setText(String.format("%03d", lives));
    }

    public int getLives() {
        return lives;
    }

    public int getScore() { return score; }
}
