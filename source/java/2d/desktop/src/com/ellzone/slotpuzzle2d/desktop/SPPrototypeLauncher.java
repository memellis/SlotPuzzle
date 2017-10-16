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

import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypes;

public class SPPrototypeLauncher extends JFrame implements SPPrototypeList.SPPrototypeLauncher {
	private static final long serialVersionUID = -3515677987227635114L;

	public SPPrototypeLauncher () throws HeadlessException {
        super("SlotPuzzle Prototypes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(new SPPrototypeList(this));
        pack();
        setSize(Math.max(getWidth(), 600), 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main (String[] argv) throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new SPPrototypeLauncher();
    }

    @Override
    public boolean launchSPPrototype(String testName) {
        SPPrototype spPrototype;

        try {
            spPrototype = SPPrototypes.newSPPrototype(testName);
        } catch (InstantiationException e) {
            return false;
        } catch (IllegalAccessException e) {
            return false;
        }

        if (spPrototype != null) {
            LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
            config.width = 1280;
            config.height = 720;
            config.title = testName;
            config.forceExit = false;

            new LwjglApplication(spPrototype, config);
            dispose();
            return true;
        }
        return false;
    }
}
