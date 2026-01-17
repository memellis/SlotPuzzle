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

package com.ellzone.slotpuzzle.level.hidden;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.SlotPuzzleConstants;
import com.ellzone.slotpuzzle.level.card.Card;
import com.ellzone.slotpuzzle.level.creator.LevelCreator;
import com.ellzone.slotpuzzle.level.card.Suit;
import com.ellzone.slotpuzzle.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle.utils.Random;

public class HiddenPlayingCard extends HiddenPattern {
    public static final String CARD_BACK = "back";

    private TextureAtlas carddeckAtlas;
    private Array<Integer> hiddenPlayingCards;
    private Array<Card> cards;
    private int maxNumberOfPlayingCardsForLevel;
    private Suit randomSuit;
    private Pip randomPip;

    public HiddenPlayingCard(TiledMap level, TextureAtlas carddeckAtlas) {
        super(level);
        this.carddeckAtlas = carddeckAtlas;
        initialiseHiddenPlayingCards(level);
    }

    private void initialiseHiddenPlayingCards(TiledMap level) {
        setUpPlayingCards(level);
        MapProperties levelProperties = level.getProperties();
        int numberOfCardsToDisplayForLevel = Integer.parseInt(levelProperties.get("Number Of Cards", String.class));
        hiddenPlayingCards = new Array<>();

        for (int i = 0; i < numberOfCardsToDisplayForLevel; i++) {
            cards.add(addACard(i));
        }
    }

    private void setUpPlayingCards(TiledMap level) {
        randomSuit = null;
        randomPip = null;
        cards = new Array<>();
        maxNumberOfPlayingCardsForLevel = level.getLayers().get(LevelCreator.HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).size;
    }

    private Card addACard(int index) {
        int nextRandomHiddenPlayCard = getNextRandomHiddenPlayCard();
        if (isEven(index)) {
            randomSuit = Suit.values()[Random.getInstance().nextInt(Suit.getNumberOfSuits())];
            randomPip = Pip.values()[Random.getInstance().nextInt(Pip.getNumberOfCards())];
        }
        return getHiddenPlayingCardFromLevel(randomSuit, randomPip, nextRandomHiddenPlayCard);
    }

    private int getNextRandomHiddenPlayCard() {
        int nextRandomHiddenPlayCard = Random.getInstance().nextInt(maxNumberOfPlayingCardsForLevel);
        int theSamenextRandomHiddenPlayCardCount = 0;
        while ((hiddenPlayingCards.indexOf(nextRandomHiddenPlayCard, true) >= 0) &
            (theSamenextRandomHiddenPlayCardCount < 10)) {
            nextRandomHiddenPlayCard = Random.getInstance().nextInt(maxNumberOfPlayingCardsForLevel);
            theSamenextRandomHiddenPlayCardCount++;
        }
        if (theSamenextRandomHiddenPlayCardCount >= 10) {
            hiddenPlayingCards.sort();
            int lastHiddenPlayinGCard = hiddenPlayingCards.get(hiddenPlayingCards.size - 1);
            nextRandomHiddenPlayCard = Random.getInstance().nextInt(maxNumberOfPlayingCardsForLevel - lastHiddenPlayinGCard)
                + lastHiddenPlayinGCard;
        }
        hiddenPlayingCards.add(nextRandomHiddenPlayCard);
        return nextRandomHiddenPlayCard;
    }

    private Card getHiddenPlayingCardFromLevel(Suit randomSuit, Pip randomPip, int nextRandomHiddenPlayCard) {
        Card card = new Card(randomSuit,
            randomPip,
            carddeckAtlas.createSprite(CARD_BACK, 3),
            carddeckAtlas.createSprite(randomSuit.name, randomPip.value));

        RectangleMapObject hiddenLevelPlayingCard = getHiddenPlayingCard(level, nextRandomHiddenPlayCard);
        card.setPosition(hiddenLevelPlayingCard.getRectangle().x,
            hiddenLevelPlayingCard.getRectangle().y);
        card.setSize((int) hiddenLevelPlayingCard.getRectangle().width,
            (int) hiddenLevelPlayingCard.getRectangle().height);
        return card;
    }

    private boolean isEven(int index) {
        return (index & 1) == 0;
    }

    private RectangleMapObject getHiddenPlayingCard(TiledMap level, int cardIndex) {
        return level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).get(cardIndex);
    }

    public Array<Card> getCards() {
        return cards;
    }

    public Array<Integer> getHiddenPlayingCards() {
        return hiddenPlayingCards;
    }

    @Override
    public boolean isHiddenPatternRevealed(TupleValueIndex[][] grid, Array<ReelTile> reelTiles, int levelWidth, int levelHeight) {
        for (Integer hiddenPlayingCard : hiddenPlayingCards) {
            if (isHiddenPlayingCardRevealed(grid, reelTiles, levelWidth, levelHeight, hiddenPlayingCard))
                return false;
        }
        return true;
    }

    private boolean isHiddenPlayingCardRevealed(TupleValueIndex[][] grid, Array<ReelTile> reelTiles, int levelWidth, int levelHeight, Integer hiddenPlayingCard) {
        MapObject mapObject = level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).get(hiddenPlayingCard);
        Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
        for (int co = (int) (mapRectangle.getX()); co < (int) (mapRectangle.getX() + mapRectangle.getWidth()); co += SlotPuzzleConstants.TILE_WIDTH) {
            for (int ro = (int) (mapRectangle.getY()); ro < (int) (mapRectangle.getY() + mapRectangle.getHeight()); ro += SlotPuzzleConstants.TILE_HEIGHT) {
                int c = PuzzleGridTypeReelTile.getColumnFromLevel(co);
                int r = PuzzleGridTypeReelTile.getRowFromLevel(ro, levelHeight);
                if ((r >= 0) & (r <= levelHeight) & (c >= 0) & (c <= levelWidth)) {
                    if (grid[r][c] != null) {
                        if (!reelTiles.get(grid[r][c].getIndex()).isReelTileDeleted())
                            return true;
                    } else
                        throw new HiddenPattern.HiddenPatternPuzzleGridException(String.format("Grid cell r=%d, c=%d is null", r, c));
                } else
                    throw new HiddenPattern.HiddenPatternPuzzleGridException(String.format("Grid cell r=%d, c=%d has exceeded the grid limits: width=%d, height=%d", r, c, levelWidth, levelHeight));
            }
        }
        return false;
    }
}
