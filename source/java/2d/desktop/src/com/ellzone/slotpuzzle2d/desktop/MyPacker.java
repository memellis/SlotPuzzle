package com.ellzone.slotpuzzle2d.desktop;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;
public class MyPacker {
    public static void main (String[] args) throws Exception {
        TexturePacker.process("./desktop/assets/tiles", "./desktop/assets/tiles1", "tiles.pack");
    }
}

