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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglFiles;
import com.badlogic.gdx.backends.lwjgl.LwjglPreferences;
import com.badlogic.gdx.files.FileHandle;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypes;

class SPPrototypeList extends JPanel {
	private static final long serialVersionUID = 4871155599055981034L;

	public interface SPPrototypeLauncher {
        boolean launchSPPrototype(String sampleName);
    }

    private SPPrototypeLauncher testLauncher;

    public SPPrototypeList(SPPrototypeLauncher launcher) {
        testLauncher = launcher;

        setLayout(new BorderLayout());

        final JButton button = new JButton("Run SPPrototype");

        final JList<Object> list = new JList<Object>(SPPrototypes.getNames().toArray());
        JScrollPane pane = new JScrollPane(list);

        DefaultListSelectionModel m = new DefaultListSelectionModel();
        m.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m.setLeadAnchorNotificationEnabled(false);
        list.setSelectionModel(m);

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked (MouseEvent event) {
                if (event.getClickCount() == 2) button.doClick();
            }
        });

        list.addKeyListener(new KeyAdapter() {
            public void keyPressed (KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) button.doClick();
            }
        });

        final Preferences prefs = new LwjglPreferences(new FileHandle(new LwjglFiles().getExternalStoragePath()
                + ".prefs/libgdxCookbookSamples"));
        list.setSelectedValue(prefs.getString("last", null), true);

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed (ActionEvent e) {
                String testName = (String)list.getSelectedValue();
                prefs.putString("last", testName);
                prefs.flush();
                testLauncher.launchSPPrototype(testName);
            }
        });

        add(pane, BorderLayout.CENTER);
        add(button, BorderLayout.SOUTH);
    }
}