/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle.level.popups;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.level.LevelDoor;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;
import com.ellzone.slotpuzzle.utils.assets.AssetsAnnotation;

public class PlayScreenPopUps {

    private static final String LEVEL_TIP_DESCRIPTION = "Reveal the hidden pattern to complete the level.";
    private static final String LEVEL_LOST_DESCRIPTION = "Sorry you lost that level. Touch/Press to restart the level.";
    private static final String LEVEL_WON_DESCRIPTION = "Well done you've won that level. Touch/Press to return to the World level.";
    private static final String LEVEL_BONUS_COMPLETED_DESCRIPTION = "Bonus level completed. Touch/Press to return to the World level";

    private final TextureAtlas tilesAtlas;
    private int sW, sH;
    private final SpriteBatch batch;
    private final TweenManager tweenManager;
    private final LevelDoor levelDoor;
    private Array<Sprite> popUpSprites, levelLostSprites, levelWonSprites, levelBonusSprites;
    private LevelPopUpTypeWriterText levelPopUp, levelLostPopUp, levelWonPopUp, levelBonusCompletedPopUp;

    public PlayScreenPopUps(TextureAtlas tilesAtlas, int screenWidth, int screenHeight, SpriteBatch batch, TweenManager tweenManager, LevelDoor levelDoor) {
        this.tilesAtlas = tilesAtlas;
        this.sW = screenWidth;
        this.sH = screenHeight;
        this.batch = batch;
        this.tweenManager = tweenManager;
        this.levelDoor = levelDoor;
    }

    public void initialise() {
        setUpSprites();
        createPopUps();
    }

    private void setUpSprites() {
        setUpPopUpSprites();
        setUpLevelLostSprites();
        setUpLevelWonSprites();
        setUpBonusLevelCompleteSprites();
    }

    public Array<Sprite> getLevelPopUpSprites() {
        return popUpSprites;
    }

    public Array<Sprite> getLevelLostSprites() {
        return levelLostSprites;
    }

    public Array<Sprite> getLevelWonSprites() {
        return levelWonSprites;
    }

    public Array<Sprite> getLevelBonusSprites() {
        return levelBonusSprites;
    }

    public LevelPopUpTypeWriterText getLevelPopUp() {
        return levelPopUp;
    }

    public LevelPopUpTypeWriterText getLevelLostPopUp() {
        return levelLostPopUp;
    }

    public LevelPopUpTypeWriterText getLevelWonPopUp() {
        return levelWonPopUp;
    }

    public LevelPopUpTypeWriterText getLevelBonusCompletedPopUp() { return levelBonusCompletedPopUp; }

    private void createPopUps() {
        BitmapFont currentLevelFont = new BitmapFont();
        currentLevelFont.getData().scale(1.5f);
        levelPopUp = new LevelPopUpTypeWriterText(batch, tweenManager, popUpSprites, currentLevelFont, levelDoor.getLevelName(), LEVEL_TIP_DESCRIPTION);
        levelLostPopUp = new LevelPopUpTypeWriterText(batch, tweenManager, levelLostSprites, currentLevelFont, levelDoor.getLevelName(), LEVEL_LOST_DESCRIPTION);
        levelWonPopUp = new LevelPopUpTypeWriterText(batch, tweenManager, levelWonSprites, currentLevelFont, levelDoor.getLevelName(), LEVEL_WON_DESCRIPTION);
        levelBonusCompletedPopUp = new LevelPopUpTypeWriterText(batch, tweenManager, levelBonusSprites, currentLevelFont, levelDoor.getLevelName(), LEVEL_BONUS_COMPLETED_DESCRIPTION);
    }

    private void setUpPopUpSprites() {
        popUpSprites = new Array<>();
        popUpSprites.add(tilesAtlas.createSprite(AssetsAnnotation.GAME_POPUP));
        popUpSprites.add(tilesAtlas.createSprite(AssetsAnnotation.LEVEL_SPRITE));
        setPopUpSpritePositions();
    }

    private void setUpLevelLostSprites() {
        levelLostSprites = new Array<>();
        levelLostSprites.add(tilesAtlas.createSprite(AssetsAnnotation.GAME_POPUP));
        levelLostSprites.add(tilesAtlas.createSprite(AssetsAnnotation.LEVEL_SPRITE));
        levelLostSprites.add(tilesAtlas.createSprite(AssetsAnnotation.LEVEL_SPRITE));
        levelLostSprites.add(tilesAtlas.createSprite(AssetsAnnotation.OVER));
        setLevelLostSpritePositions();
    }

    private void setUpLevelWonSprites() {
        levelWonSprites = new Array<>();
        levelWonSprites.add(tilesAtlas.createSprite(AssetsAnnotation.GAME_POPUP));
        levelWonSprites.add(tilesAtlas.createSprite(AssetsAnnotation.LEVEL_SPRITE));
        levelWonSprites.add(tilesAtlas.createSprite(AssetsAnnotation.LEVEL_SPRITE));
        levelWonSprites.add(tilesAtlas.createSprite(AssetsAnnotation.COMPLETE));
        setLevelWonSpritePositions();
    }

    private void setUpBonusLevelCompleteSprites() {
        levelBonusSprites = new Array<>();
        levelBonusSprites.add(tilesAtlas.createSprite(AssetsAnnotation.GAME_POPUP));
        levelBonusSprites.add(tilesAtlas.createSprite(AssetsAnnotation.LEVEL_SPRITE));
        levelBonusSprites.add(tilesAtlas.createSprite(AssetsAnnotation.LEVEL_SPRITE));
        levelBonusSprites.add(tilesAtlas.createSprite(AssetsAnnotation.COMPLETE));
        setLevelBonusSpritePositions();
    }

    public void setLevelLostSpritePositions() {
        levelLostSprites.get(0).setPosition(sW / 2 - levelLostSprites.get(0).getWidth() / 2, sH / 2 - levelLostSprites.get(0).getHeight() /2);
        levelLostSprites.get(1).setPosition(-200, sH / 2 - levelLostSprites.get(1).getHeight() / 2);
        levelLostSprites.get(2).setPosition(-200, sH / 2 - levelLostSprites.get(2).getHeight() / 2 + 40);
        levelLostSprites.get(3).setPosition(200 + sW, sH / 2 - levelLostSprites.get(3).getHeight() / 2 + 40);
    }

    public void setLevelWonSpritePositions() {
        levelWonSprites.get(0).setPosition(sW / 2 - levelWonSprites.get(0).getWidth() / 2, sH / 2 - levelWonSprites.get(0).getHeight() /2);
        levelWonSprites.get(1).setPosition(-200, sH / 2 - levelWonSprites.get(1).getHeight() / 2);
        levelWonSprites.get(2).setPosition(-200, sH / 2 - levelWonSprites.get(2).getHeight() / 2 + 40);
        levelWonSprites.get(3).setPosition(200 + sW, sH / 2 - levelWonSprites.get(3).getHeight() / 2 + 40);
    }

    public void setPopUpSpritePositions() {
        popUpSprites.get(0).setPosition(sW / 2 - popUpSprites.get(0).getWidth() / 2, sH / 2 - popUpSprites.get(0).getHeight() /2);
        popUpSprites.get(1).setPosition(-200, sH / 2 - popUpSprites.get(1).getHeight() / 2);
    }

    public void setLevelBonusSpritePositions() {
        levelBonusSprites.get(0).setPosition(sW / 2 - levelWonSprites.get(0).getWidth() / 2, sH / 2 - levelWonSprites.get(0).getHeight() /2);
        levelBonusSprites.get(1).setPosition(-200, sH / 2 - levelWonSprites.get(1).getHeight() / 2);
        levelBonusSprites.get(2).setPosition(-200, sH / 2 - levelWonSprites.get(2).getHeight() / 2 + 40);
        levelBonusSprites.get(3).setPosition(200 + sW, sH / 2 - levelWonSprites.get(3).getHeight() / 2 + 40);
    }
}
