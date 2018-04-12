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

package com.ellzone.slotpuzzle2d.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ellzone.slotpuzzle2d.prototypes.minislotmachine.MiniSlotMachineLevelPrototypeWithLevelCreator;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

public class B2dContactListener implements ContactListener {
    private MiniSlotMachineLevelPrototypeWithLevelCreator prototype;

    @Override
    public void beginContact(Contact contact) {
        Body bodyA = contact.getFixtureA().getBody();
        Body bodyB = contact.getFixtureB().getBody();
        String classA = "", classB = "";

        if (bodyA.getUserData() != null) {
            classA = bodyA.getUserData().getClass().getName();
        }

        if (bodyB.getUserData() != null) {
            classB = bodyB.getUserData().getClass().getName();
        }
        if(classA.equalsIgnoreCase("com.ellzone.slotpuzzle2d.prototypes.minislotmachine.MiniSlotMachineLevelPrototypeWithLevelCreator") && classB.equalsIgnoreCase("com.ellzone.slotpuzzle2d.sprites.ReelTile")) {
            System.out.println("dealWithReelsHittingSinkBottom beginContact classA = " + classA + " classB=" + classB);
            dealWithReelsHittingSinkBottom((MiniSlotMachineLevelPrototypeWithLevelCreator) bodyA.getUserData(), (ReelTile) bodyB.getUserData());
        }
        if(classA.equalsIgnoreCase("com.ellzone.slotpuzzle2d.sprites.ReelTile") && classB.equalsIgnoreCase("com.ellzone.slotpuzzle2d.prototypes.minislotmachine.MiniSlotMachineLevelPrototypeWithLevelCreator")) {
            System.out.println("dealWithReelsHittingSinkBottom beginContact classA = " + classA + " classB=" + classB);
            dealWithReelsHittingSinkBottom((MiniSlotMachineLevelPrototypeWithLevelCreator) bodyB.getUserData(), (ReelTile) bodyA.getUserData());
        }
        if((classA.equalsIgnoreCase("com.ellzone.slotpuzzle2d.sprites.ReelTile") && (classB.equalsIgnoreCase("com.ellzone.slotpuzzle2d.sprites.ReelTile")))) {
            dealWithReelTileHittingReelTile((ReelTile) bodyA.getUserData(), (ReelTile) bodyB.getUserData());
        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    private void dealWithReelsHittingSinkBottom(MiniSlotMachineLevelPrototypeWithLevelCreator prototype, ReelTile reelTile) {
        MiniSlotMachineLevelPrototypeWithLevelCreator.numberOfReelsToHitSinkBottom++;
        if (MiniSlotMachineLevelPrototypeWithLevelCreator.numberOfReelsToHitSinkBottom >= MiniSlotMachineLevelPrototypeWithLevelCreator.MAX_NUMBER_OF_REELS_HIT_SINK_BOTTOM) {
            System.out.println("Reels Tiles have fallen to the bottom of the sink!");
            prototype.dealWithHitSinkBottom(reelTile);
            this.prototype = prototype;
        }
   }

    private void dealWithReelTileHittingReelTile(ReelTile reelTileA, ReelTile reelTileB) {
        System.out.println("in dealWithReelTileHittingReelTile...");
        System.out.println("reelTileA="+reelTileA+" reelTileA.x="+reelTileA.getX()+" reelTileA.y="+reelTileA.getY());
        System.out.println("reelTileB="+reelTileB+" reelTileB.x="+reelTileB.getX()+" reelTileB.y="+reelTileB.getY());

        MiniSlotMachineLevelPrototypeWithLevelCreator.numberOfReelsToFall--;
        if (MiniSlotMachineLevelPrototypeWithLevelCreator.numberOfReelsToFall <= 0) {
            if (this.prototype != null) {
                this.prototype.dealWithReelTileHittingReelTile(reelTileA, reelTileB);
            } else {
                System.out.println("prototype is currently null");
            }
        }
    }
}
