package com.ellzone.slotpuzzle2d.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
public class MyPacker {
    public static void main (String[] args) throws Exception {
        TexturePacker.process("../android/assets/tiles", "../android/assets/tiles", "tiles.pack");
    }
}

