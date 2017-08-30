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

package com.ellzone.slotpuzzle2d.puzzlegrid;

import com.ellzone.slotpuzzle2d.sprites.ReelTile;

public class ReelTileGridValue extends TupleValueIndex {
    public ReelTile reelTile, n, e, s, w, ne, se, nw, sw;
    private boolean discovered = false;
    public ReelTileGridValue nReelTileGridValue,
                             eReelTileGridValue,
                             sReelTileGridValue,
                             wReelTileGridValue,
                             neReelTileGridValue,
                             seReelTileGridValue,
                             swReelTileGridValue,
                             nwReelTileGridValue;

    public enum Compass {NORTH, EAST, SOUTH, WEST, NORTHEAST, SOUTHEAST, SOUTHWEST, NORTHWEST};

    public ReelTileGridValue() {
        super();
    }

    public ReelTileGridValue(int r, int c, int index, int value) {
        super(r, c, index, value);
    }

    public ReelTileGridValue(ReelTile reelTile, int r, int c, int index, int value) {
        super(r, c, index, value);
        this.reelTile = reelTile;
    }

    public ReelTileGridValue(ReelTile reelTile, int r, int c, int index, int value,
                             ReelTile n, ReelTile e, ReelTile s, ReelTile w, ReelTile ne, ReelTile se, ReelTile sw, ReelTile nw) {
        super(r, c, index, value);
        this.reelTile = reelTile;
        this.n = n;
        this.e = e;
        this.s = s;
        this.w = w;
        this.ne = ne;
        this.se = se;
        this.sw = sw;
        this.nw = nw;
    }

    public ReelTileGridValue(ReelTile reelTile, int r, int c, int index, int value,
                             ReelTile n, ReelTile e, ReelTile s, ReelTile w, ReelTile ne, ReelTile se, ReelTile sw, ReelTile nw,
                             ReelTileGridValue nReelTileGridValue, ReelTileGridValue eReelTileGridValue, ReelTileGridValue sReelTileGridValue, ReelTileGridValue wReelTileGridValue,
                             ReelTileGridValue neReelTileGridValue, ReelTileGridValue seReelTileGridValue, ReelTileGridValue swReelTileGridValue, ReelTileGridValue nwReelTileGridValue) {
        super(r, c, index, value);
        this.reelTile = reelTile;
        this.n = n;
        this.e = e;
        this.s = s;
        this.w = w;
        this.ne = ne;
        this.se = se;
        this.sw = sw;
        this.nw = nw;
        this.nReelTileGridValue = nReelTileGridValue;
        this.eReelTileGridValue = eReelTileGridValue;
        this.sReelTileGridValue = sReelTileGridValue;
        this.wReelTileGridValue = wReelTileGridValue;
        this.neReelTileGridValue = neReelTileGridValue;
        this.seReelTileGridValue = seReelTileGridValue;
        this.swReelTileGridValue = swReelTileGridValue;
        this.nwReelTileGridValue = nwReelTileGridValue;
    }

    public void setReelTile(ReelTile reelTile) {
        this.reelTile = reelTile;
    }

    public void setN(ReelTile n) {
        this.n = n;
    }

    public void setE(ReelTile e) {
        this.e = e;
    }

    public void setS(ReelTile s) {
        this.s = s;
    }

    public void setW(ReelTile w) {
        this.w = w;
    }

    public void setNe(ReelTile ne) {
        this.ne = ne;
    }

    public void setSe(ReelTile se) {
        this.se = se;
    }

    public void setNw(ReelTile ne) {
        this.nw = nw;
    }

    public void setSw(ReelTile sw) {
        this.sw = sw;
    }

    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }

    public ReelTile getReelTile() {
        return this.reelTile;
    }

    public ReelTile getN() {
        return this.n;
    }

    public ReelTile getE() {
        return this.e;
    }

    public ReelTile getS() {
        return this.s;
    }

    public ReelTile getW() {
        return this.w;
    }

    public ReelTile getNe() {
        return this.ne;
    }

    public ReelTile getSe() {
        return this.se;
    }

    public ReelTile getNw() {
        return this.nw;
    }

    public ReelTile getSw() {
        return this.sw;
    }

    public void setNReelTileGridValue(ReelTileGridValue nReelTileGridValue) {
        this.nReelTileGridValue = nReelTileGridValue;
    }

    public void setEReelTileGridValue(ReelTileGridValue eReelTileGridValue) {
        this.eReelTileGridValue = eReelTileGridValue;
    }

    public void setSReelTileGridValue(ReelTileGridValue sReelTileGridValue) {
        this.sReelTileGridValue = sReelTileGridValue;
    }

    public void setWReelTileGridValue(ReelTileGridValue wReelTileGridValue) {
        this.wReelTileGridValue = wReelTileGridValue;
    }

    public static ReelTileGridValue newInstance(ReelTileGridValue reelTileGridValue) {
        return new ReelTileGridValue(reelTileGridValue.getReelTile(), reelTileGridValue.getR(), reelTileGridValue.getC(), reelTileGridValue.getIndex(), reelTileGridValue.getValue(), reelTileGridValue.getN(), reelTileGridValue.getE(), reelTileGridValue.getS(), reelTileGridValue.getW(), reelTileGridValue.getNe(), reelTileGridValue.getSe(), reelTileGridValue.getSw(), reelTileGridValue.getNw());
    }

    public ReelTileGridValue getNReelTileGridValue() {
        return  this.nReelTileGridValue;
    }

    public ReelTileGridValue getEReelTileGridValue() {
        return this.eReelTileGridValue;
    }

    public ReelTileGridValue getSReelTileGridValue() {
        return this.sReelTileGridValue;
    }

    public ReelTileGridValue getWReelTileGridValue() {
        return this.wReelTileGridValue;
    }

    public ReelTileGridValue getNeReelTileGridValue() {
        return this.neReelTileGridValue;
    }

    public ReelTileGridValue getSeReelTileGridValue() {
        return this.seReelTileGridValue;
    }

    public ReelTileGridValue getSwReelTileGridValue() {
        return this.swReelTileGridValue;
    }

    public ReelTileGridValue getNwReelTileGridValue() {
        return this.nwReelTileGridValue;
    }

    public boolean getDiscovered() {
        return discovered;
    }
}
