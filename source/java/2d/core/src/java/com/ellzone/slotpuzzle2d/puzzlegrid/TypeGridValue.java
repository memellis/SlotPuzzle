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

package com.ellzone.slotpuzzle2d.puzzlegrid;

import java.lang.reflect.Array;

public class TypeGridValue<T> extends TupleValueIndex {
    public T t, n, e, s, w, ne, se, nw, sw;
    public TypeGridValue(int r, int c, int index, int value) {
        super(r, c, index, value);
    }

    public TypeGridValue(T t,int r, int c, int index, int value) {
        super(r, c, index, value);
        this.t = t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public void setN(T n) {
        this.n = n;
    }

    public void setE(T e) {
        this.e = e;
    }

    public void setS(T s) {
        this.s = s;
    }

    public void setW(T w) {
        this.w = w;
    }

    public void setNe(T ne) {
        this.ne = ne;
    }

    public void setSe(T se) {
        this.se = se;
    }

    public void setNw(T ne) {
        this.ne = ne;
    }

    public void setSw(T sw) {
        this.sw = sw;
    }

    public T getT() {
        return this.t;
    }

    public T getN() {
        return this.n;
    }

    public T getE() {
        return this.e;
    }

    public T getS() {
        return this.s;
    }

    public T getW() {
        return this.w;
    }

    public T getNe() {
        return this.ne;
    }

    public T getSe() {
        return this.se;
    }

    public T getNw() {
        return this.nw;
    }

    public T getSw() {
        return this.sw;
    }

    public static <T> T[] newArray(Class<T[]> type, int d1) {
        return type.cast(Array.newInstance(type.getComponentType(), d1));
    }

    public static <T> T[][] newArray(Class<T[][]> type, int d1, int d2) {
        return type.cast(Array.newInstance(type.getComponentType(), d1, d2));
    }
}
