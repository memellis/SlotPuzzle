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

package com.ellzone.slotpuzzle2d.desktop;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypes;

public class SwingCanvasSPPrototype extends JFrame implements SPPrototypeList.SPPrototypeLauncher  {
  	private static final long serialVersionUID = -5512379626313409632L;
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

    LwjglCanvas canvas = null;

    @Override
    public boolean launchSPPrototype(String sampleName) {
        Container container = getContentPane();

        if (canvas != null) {
            canvas.stop();
            container.remove(canvas.getCanvas());
        }
        ApplicationListener sample;

        try {
            sample = SPPrototypes.newSPPrototype(sampleName);
        } catch (InstantiationException e) {
            return false;
        } catch (IllegalAccessException e) {
            return false;
        }

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