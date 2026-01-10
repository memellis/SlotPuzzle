package com.ellzone.slotpuzzle.level.sequence;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.SlotPuzzleConstants;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle.tweenengine.Timeline;
import com.ellzone.slotpuzzle.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;

import static com.badlogic.gdx.math.MathUtils.random;

public abstract class IntroSequence {
    protected Array<ReelTile> reelTiles;
    protected TweenManager tweenManager;

    public IntroSequence(Array<ReelTile> reelTiles, TweenManager tweenManager) {
        this.reelTiles = reelTiles;
        this.tweenManager = tweenManager;
    }

    public void createReelIntroSequence(TweenCallback introSequenceCallback) {
        Timeline introSequence = Timeline.createParallel();
        for(int i = 0; i < reelTiles.size; i++)
            introSequence = introSequence
                .push(buildSequence(reelTiles.get(i), i, random.nextFloat() * 3.0f, random.nextFloat() * 3.0f));
        introSequence.pushPause(0.3f)
            .setCallback(introSequenceCallback)
            .setCallbackTriggers(TweenCallback.END)
            .start(tweenManager);
    }

    protected Vector2 getRandomCorner() {
        int randomCorner = random.nextInt(4);
        switch (randomCorner) {
            case 0:
                return new Vector2(-1 * random.nextFloat(), -1 * random.nextFloat());
            case 1:
                return new Vector2(-1 * random.nextFloat(), SlotPuzzleConstants.VIRTUAL_WIDTH + random.nextFloat());
            case 2:
                return new Vector2(SlotPuzzleConstants.VIRTUAL_HEIGHT / 2 + random.nextFloat(), -1 * random.nextFloat());
            case 3:
                return new Vector2(SlotPuzzleConstants.VIRTUAL_HEIGHT + random.nextFloat(), SlotPuzzleConstants.VIRTUAL_WIDTH + random.nextFloat());
            default:
                return new Vector2(-0.5f, -0.5f);
        }
    }

    abstract protected Timeline buildSequence(Sprite target, int id, float delay1, float delay2);
}
