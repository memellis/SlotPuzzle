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

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class CardDeck {
	private final Card[][] cards;
	
	public CardDeck(TextureAtlas atlas, int backIndex) {
		cards = new Card[Suit.values().length][];
		for (Suit suit : Suit.values()) {
			cards[suit.index] = new Card[Pip.values().length];
			for (Pip pip : Pip.values()) {
				Sprite front = atlas.createSprite(suit.name, pip.value);
				Sprite back = atlas.createSprite("back", backIndex);
				cards[suit.index][pip.index] = new Card(suit, pip, back, front);
			}
		}
	}
	
	public Card getCard(Suit suit, Pip pip) {
		return cards[suit.index][pip.index];
	}
}	
