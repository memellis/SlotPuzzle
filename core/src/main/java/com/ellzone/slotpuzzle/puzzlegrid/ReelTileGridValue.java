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

package com.ellzone.slotpuzzle.puzzlegrid;

import com.ellzone.slotpuzzle.sprites.reel.ReelTile;

import java.util.Arrays;
import java.util.Objects;

public class ReelTileGridValue extends TupleValueIndex {
    public ReelTile reelTile;
    private boolean discovered = false;

    public ReelTile[] reelTileNeighbours = new ReelTile[Compass.values().length];
    public ReelTileGridValue[] gridValueNeighbours = new ReelTileGridValue[Compass.values().length];

    public enum Compass {
        NORTH, NORTHEAST, EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST;
        private static Compass[] compasses = Compass.values();

        public static Compass getCompass(int index) {
            return index < compasses.length ? compasses[index] : null;
        }

        public static int getLastIndex() {
            return compasses.length - 1;
        }

        public static int getLength() {
            return compasses.length;
        }
    };

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
        reelTileNeighbours[Compass.NORTH.ordinal()] = n;
        reelTileNeighbours[Compass.EAST.ordinal()] = e;
        reelTileNeighbours[Compass.SOUTH.ordinal()] = s;
        reelTileNeighbours[Compass.WEST.ordinal()] = w;
        reelTileNeighbours[Compass.NORTHEAST.ordinal()] = ne;
        reelTileNeighbours[Compass.SOUTHEAST.ordinal()] = se;
        reelTileNeighbours[Compass.SOUTHWEST.ordinal()] = sw;
        reelTileNeighbours[Compass.NORTHWEST.ordinal()] = nw;
    }

    public ReelTileGridValue(
        ReelTile reelTile, int r, int c, int index, int value,
        ReelTile n, ReelTile e, ReelTile s, ReelTile w,
        ReelTile ne, ReelTile se, ReelTile sw, ReelTile nw,
        ReelTileGridValue nReelTileGridValue, ReelTileGridValue eReelTileGridValue,
        ReelTileGridValue sReelTileGridValue, ReelTileGridValue wReelTileGridValue,
        ReelTileGridValue neReelTileGridValue, ReelTileGridValue seReelTileGridValue,
        ReelTileGridValue swReelTileGridValue, ReelTileGridValue nwReelTileGridValue) {
        super(r, c, index, value);
        this.reelTile = reelTile;
        reelTileNeighbours[Compass.NORTH.ordinal()] = n;
        reelTileNeighbours[Compass.EAST.ordinal()] = e;
        reelTileNeighbours[Compass.SOUTH.ordinal()] = s;
        reelTileNeighbours[Compass.WEST.ordinal()] = w;
        reelTileNeighbours[Compass.NORTHEAST.ordinal()] = ne;
        reelTileNeighbours[Compass.SOUTHEAST.ordinal()] = se;
        reelTileNeighbours[Compass.SOUTHWEST.ordinal()] = sw;
        reelTileNeighbours[Compass.NORTHWEST.ordinal()] = nw;
        gridValueNeighbours[Compass.NORTH.ordinal()] = nReelTileGridValue;
        gridValueNeighbours[Compass.EAST.ordinal()] = eReelTileGridValue;
        gridValueNeighbours[Compass.SOUTH.ordinal()] = sReelTileGridValue;
        gridValueNeighbours[Compass.WEST.ordinal()] = wReelTileGridValue;
        gridValueNeighbours[Compass.NORTHEAST.ordinal()] = neReelTileGridValue;
        gridValueNeighbours[Compass.SOUTHEAST.ordinal()] = seReelTileGridValue;
        gridValueNeighbours[Compass.SOUTHWEST.ordinal()] = swReelTileGridValue;
        gridValueNeighbours[Compass.NORTHWEST.ordinal()] = nwReelTileGridValue;
    }

    public void setReelTile(ReelTile reelTile) {
        this.reelTile = reelTile;
    }

    public void setN(ReelTile n) {
        reelTileNeighbours[Compass.NORTH.ordinal()] = n;
    }

    public void setE(ReelTile e) {
        reelTileNeighbours[Compass.EAST.ordinal()] = e;
    }

    public void setS(ReelTile s) {
        reelTileNeighbours[Compass.SOUTH.ordinal()] = s;
    }

    public void setW(ReelTile w) {
        reelTileNeighbours[Compass.WEST.ordinal()] = w;
    }

    public void setNe(ReelTile ne) {
        reelTileNeighbours[Compass.NORTHEAST.ordinal()] = ne;
    }

    public void setSe(ReelTile se) {
        reelTileNeighbours[Compass.SOUTHEAST.ordinal()] = se;
    }

    public void setNw(ReelTile ne) {
        reelTileNeighbours[Compass.NORTHEAST.ordinal()] = ne;
    }

    public void setSw(ReelTile sw) {
        reelTileNeighbours[Compass.SOUTHWEST.ordinal()] = sw;
    }

    public void setDiscovered(boolean discovered) {
        this.discovered = discovered;
    }

    public ReelTile getReelTile() {
        return this.reelTile;
    }

    public ReelTile getN() {
        return reelTileNeighbours[Compass.NORTH.ordinal()];
    }

    public ReelTile getE() {
        return reelTileNeighbours[Compass.EAST.ordinal()];
    }

    public ReelTile getS() {
        return reelTileNeighbours[Compass.SOUTH.ordinal()];
    }

    public ReelTile getW() {
        return reelTileNeighbours[Compass.WEST.ordinal()];
    }

    public ReelTile getNe() {
        return reelTileNeighbours[Compass.NORTHEAST.ordinal()];
    }

    public ReelTile getSe() {
        return reelTileNeighbours[Compass.SOUTHEAST.ordinal()];
    }

    public ReelTile getNw() {
        return reelTileNeighbours[Compass.NORTHWEST.ordinal()];
    }

    public ReelTile getSw() {
        return reelTileNeighbours[Compass.SOUTHWEST.ordinal()];
    }

    public void setCompassPoint(Compass point, ReelTileGridValue reelTileGridValue) {
        gridValueNeighbours[point.ordinal()] = reelTileGridValue;
    }

    public ReelTileGridValue getCompassPoint(Compass point) {
        return gridValueNeighbours[point.ordinal()];
    }

    public void setNReelTileGridValue(ReelTileGridValue nReelTileGridValue) {
        gridValueNeighbours[Compass.NORTH.ordinal()] = nReelTileGridValue;
    }

    public void setEReelTileGridValue(ReelTileGridValue eReelTileGridValue) {
        gridValueNeighbours[Compass.EAST.ordinal()] = eReelTileGridValue;
    }

    public void setSReelTileGridValue(ReelTileGridValue sReelTileGridValue) {
        gridValueNeighbours[Compass.SOUTH.ordinal()] = sReelTileGridValue;
    }

    public void setWReelTileGridValue(ReelTileGridValue wReelTileGridValue) {
        gridValueNeighbours[Compass.WEST.ordinal()] = wReelTileGridValue;
    }

    public void setNeReelTileGridValue(ReelTileGridValue neReelTileGridValue) {
        gridValueNeighbours[Compass.NORTHEAST.ordinal()] = neReelTileGridValue;
    }

    public void setNwReelTileGridValue(ReelTileGridValue nwReelTileGridValue) {
        gridValueNeighbours[Compass.NORTHWEST.ordinal()] = nwReelTileGridValue;
    }

    public void setSeReelTileGridValue(ReelTileGridValue seReelTileGridValue) {
        gridValueNeighbours[Compass.SOUTHEAST.ordinal()] = seReelTileGridValue;
    }

    public void setSwReelTileGridValue(ReelTileGridValue swReelTileGridValue) {
        gridValueNeighbours[Compass.SOUTHWEST.ordinal()] = swReelTileGridValue;
    }

    public static ReelTileGridValue newInstance(ReelTileGridValue reelTileGridValue) {
        return new ReelTileGridValue(reelTileGridValue.getReelTile(), reelTileGridValue.getR(), reelTileGridValue.getC(), reelTileGridValue.getIndex(), reelTileGridValue.getValue(), reelTileGridValue.getN(), reelTileGridValue.getE(), reelTileGridValue.getS(), reelTileGridValue.getW(), reelTileGridValue.getNe(), reelTileGridValue.getSe(), reelTileGridValue.getSw(), reelTileGridValue.getNw());
    }

    public ReelTileGridValue getNReelTileGridValue() {
        return gridValueNeighbours[Compass.NORTH.ordinal()];
    }

    public ReelTileGridValue getEReelTileGridValue() {
        return gridValueNeighbours[Compass.EAST.ordinal()];
    }

    public ReelTileGridValue getSReelTileGridValue() {
        return gridValueNeighbours[Compass.SOUTH.ordinal()];
    }

    public ReelTileGridValue getWReelTileGridValue() {
        return gridValueNeighbours[Compass.WEST.ordinal()];
    }

    public ReelTileGridValue getNeReelTileGridValue() {
        return gridValueNeighbours[Compass.NORTHEAST.ordinal()];
    }

    public ReelTileGridValue getSeReelTileGridValue() {
        return gridValueNeighbours[Compass.SOUTHEAST.ordinal()];
    }

    public ReelTileGridValue getSwReelTileGridValue() {
        return gridValueNeighbours[Compass.SOUTHWEST.ordinal()];
    }

    public ReelTileGridValue getNwReelTileGridValue() {
        return gridValueNeighbours[Compass.NORTHWEST.ordinal()];
    }

    public boolean getDiscovered() {
        return discovered;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReelTileGridValue that = (ReelTileGridValue) o;
        return discovered == that.discovered &&
            Objects.equals(reelTile, that.reelTile);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(reelTile, discovered);
        result = 31 * result + Arrays.hashCode(reelTileNeighbours);
        result = 31 * result + Arrays.hashCode(gridValueNeighbours);
        return result;
    }
}
