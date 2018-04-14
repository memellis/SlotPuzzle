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

package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.InputProcessor;

public abstract class Level {

	private boolean levelCompleted = false;
    private boolean levelScrollSignChanged = false;
	private int score = 0;

	public Level () {
	}

	public boolean isLevelCompleted() {
		return this.levelCompleted;
	}

	public void setLevelCompleted() {
		this.levelCompleted = true;
	}

	public boolean hasLevelScrollSignChanged() {
        return this.levelScrollSignChanged;
    }

    public void setLevelScrollSignChanged(boolean levelScrollSignChanged) {
        this.levelScrollSignChanged = levelScrollSignChanged;
    }

	public int getScore() {
		return this.score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public abstract void initialise();
	public abstract String getImageName();
	public abstract String getTitle();
	public abstract int getLevelNumber();
	public abstract void dispose();
	public abstract InputProcessor getInput();
}
