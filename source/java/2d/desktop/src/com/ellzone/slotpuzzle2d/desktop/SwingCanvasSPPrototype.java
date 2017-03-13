package com.ellzone.slotpuzzle2d.desktop;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypes;

public class SwingCanvasSPPrototype extends JFrame implements SPPrototypeList.SPPrototypeLauncher  {
    LwjglCanvas canvas;
    SPPrototypeList list;

    public SwingCanvasSPPrototype() {
        list = new SPPrototypeList(this);
        list.setSize(320, 540);

        Container container = getContentPane();
        container.add(list, BorderLayout.WEST);

        setSize(1280, 540);
        setVisible(true);
        setResizable(false);
        setTitle("SlotPuzzle Prototype");

        addWindowListener(new WindowAdapter() {
            public void windowClosed (WindowEvent event) {
                System.exit(0);
            }
        });
    }

    @Override
    public boolean launchSPPrototype(String sampleName) {
        Container container = getContentPane();

        if (canvas != null) {
            canvas.stop();
            container.remove(canvas.getCanvas());
        }

        ApplicationListener sample = SPPrototypes.newSPPrototype(sampleName);

        canvas = new LwjglCanvas(sample);
        canvas.getCanvas().setSize(960, 540);
        container.add(canvas.getCanvas(), BorderLayout.EAST);

        pack();

        return sample != null;
    }

    public static void main (String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run () {
                new SwingCanvasSPPrototype();
            }
        });
    }
}