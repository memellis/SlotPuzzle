/*******************************************************************************
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
 ******************************************************************************/

package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.utils.Random;

public class HiddenPlayingCard {
    public static final String CARD_FRONT = "front";
    public static final String CARD_BACK = "back";

    private TextureAtlas carddeckAtlas;
    private TiledMap level;
    private Array<Integer> hiddenPlayingCards;

    public HiddenPlayingCard(TiledMap level, TextureAtlas carddeckAtlas) {
        this.level = level;
        this.carddeckAtlas = carddeckAtlas;
    }

    private void initialiseHiddenPlayingCards(TiledMap level, TextureAtlas carddeckAtlas) {
        Suit randomSuit = null;
        Pip randomPip = null;
        Array<Card> cards = new Array<Card>();
        int maxNumberOfPlayingCardsForLevel = level.getLayers().get(LevelCreator.HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).size;
        MapProperties levelProperties = level.getProperties();
        int numberOfCardsToDisplayForLevel = Integer.parseInt(levelProperties.get("Number Of Cards", String.class));
        this.hiddenPlayingCards = new Array<Integer>();

        for (int i=0; i<numberOfCardsToDisplayForLevel; i++) {
            int nextRandomHiddenPlayCard = Random.getInstance().nextInt(maxNumberOfPlayingCardsForLevel);
            hiddenPlayingCards.add(nextRandomHiddenPlayCard);
            if ((i & 1) == 0) {
                randomSuit = Suit.values()[Random.getInstance().nextInt(Suit.getNumberOfSuits())];
                randomPip = Pip.values()[Random.getInstance().nextInt(Pip.getNumberOfCards())];
            }

            Card card = new Card(randomSuit,
                                 randomPip,
                                 carddeckAtlas.createSprite(CARD_BACK, 3),
                                 carddeckAtlas.createSprite(randomSuit.name, randomPip.value));

            RectangleMapObject hiddenLevelPlayingCard = getHiddenPlayingCard(level, nextRandomHiddenPlayCard);
            card.setPosition(hiddenLevelPlayingCard.getRectangle().x,
                    hiddenLevelPlayingCard.getRectangle().y);
            card.setSize((int)hiddenLevelPlayingCard.getRectangle().width,
                    (int)hiddenLevelPlayingCard.getRectangle().height);

            cards.add(card);
        }
    }

    private RectangleMapObject getHiddenPlayingCard(TiledMap level, int cardIndex) {
        return level.getLayers().get(LevelCreator.HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).get(cardIndex);
    }
}
