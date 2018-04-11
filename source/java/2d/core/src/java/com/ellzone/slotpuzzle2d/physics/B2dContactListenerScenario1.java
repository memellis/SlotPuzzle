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
import com.ellzone.slotpuzzle2d.prototypes.minislotmachine.MiniSlotMachineLevelPrototypeScenario1;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

public class B2dContactListenerScenario1 implements ContactListener {
    private MiniSlotMachineLevelPrototypeScenario1 prototype;

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
        if(classA.equalsIgnoreCase("com.ellzone.slotpuzzle2d.prototypes.minislotmachine.MiniSlotMachineLevelPrototypeScenario1") && classB.equalsIgnoreCase("com.ellzone.slotpuzzle2d.sprites.ReelTile")) {
            dealWithReelsHittingSinkBottom((MiniSlotMachineLevelPrototypeScenario1) bodyA.getUserData(), (ReelTile) bodyB.getUserData());
        }
        if(classA.equalsIgnoreCase("com.ellzone.slotpuzzle2d.sprites.ReelTile") && classB.equalsIgnoreCase("com.ellzone.slotpuzzle2d.prototypes.minislotmachine.MiniSlotMachineLevelPrototypeScenario1")) {
            dealWithReelsHittingSinkBottom((MiniSlotMachineLevelPrototypeScenario1) bodyB.getUserData(), (ReelTile) bodyA.getUserData());
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

    private void dealWithReelsHittingSinkBottom(MiniSlotMachineLevelPrototypeScenario1 prototype, ReelTile reelTile) {
        MiniSlotMachineLevelPrototypeScenario1.numberOfReelsToHitSinkBottom++;
        if (MiniSlotMachineLevelPrototypeScenario1.numberOfReelsToHitSinkBottom >= MiniSlotMachineLevelPrototypeScenario1.MAX_NUMBER_OF_REELS_HIT_SINK_BOTTOM) {
            prototype.dealWithHitSinkBottom(reelTile);
            this.prototype = prototype;
        }
    }

    private void dealWithReelTileHittingReelTile(ReelTile reelTileA, ReelTile reelTileB) {
        MiniSlotMachineLevelPrototypeScenario1.numberOfReelsToFall--;
        if (MiniSlotMachineLevelPrototypeScenario1.numberOfReelsToFall <= 0) {
            if (this.prototype != null) {
                this.prototype.dealWithReelTileHittingReelTile(reelTileA, reelTileB);
            } else {
                System.out.println("prototype is currently null");
            }
        }
    }
}
