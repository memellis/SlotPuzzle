package com.ellzone.slotpuzzle2d.physics;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.ellzone.slotpuzzle2d.prototypes.minislotmachine.MiniSlotMachineLevelPrototypeScenario1;
import com.ellzone.slotpuzzle2d.prototypes.minislotmachine.MiniSlotMachineLevelPrototypeWithLevelCreator;
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
            System.out.println("dealWithReelsHittingSinkBottom beginContact classA = " + classA + " classB=" + classB);
            dealWithReelsHittingSinkBottom((MiniSlotMachineLevelPrototypeScenario1) bodyA.getUserData(), (ReelTile) bodyB.getUserData());
        }
        if(classA.equalsIgnoreCase("com.ellzone.slotpuzzle2d.sprites.ReelTile") && classB.equalsIgnoreCase("com.ellzone.slotpuzzle2d.prototypes.minislotmachine.MiniSlotMachineLevelPrototypeScenario1")) {
            System.out.println("dealWithReelsHittingSinkBottom beginContact classA = " + classA + " classB=" + classB);
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
            System.out.println("Reels Tiles have fallen to the bottom of the sink!");
            prototype.dealWithHitSinkBottom(reelTile);
            this.prototype = prototype;
        }
    }

    private void dealWithReelTileHittingReelTile(ReelTile reelTileA, ReelTile reelTileB) {
        System.out.println("in dealWithReelTileHittingReelTile...");
        System.out.println("reelTileA="+reelTileA+" reelTileA.x="+reelTileA.getX()+" reelTileA.y="+reelTileA.getY());
        System.out.println("reelTileB="+reelTileB+" reelTileB.x="+reelTileB.getX()+" reelTileB.y="+reelTileB.getY());

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
