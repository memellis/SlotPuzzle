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

package com.ellzone.slotpuzzle2d.desktop.play;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeSelectionModel;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;


public class Main extends JFrame {
	private static final long serialVersionUID = -4296204662394260962L;

	public static String data;
	
	public static class AppDesc {
		public Class<? extends ApplicationListener> clazz;
		public String title;
		
		public int width;
		public int height;
		public String data;
		
		public AppDesc(String title, int width, int height, Class<? extends ApplicationListener> clazz, String data) {
			this.clazz = clazz;
			this.title = title;
			this.width = width;
			this.height = height;
			this.data = data;
		}
		
		public AppDesc(String title, int width, int height, Class<? extends ApplicationListener> clazz) {
			this(title, width, height, clazz, null);
		}
		
		@Override
		public String toString() {
			return title;
		}
	}

	public final static Object[] apps = {
		"SlotPuzzle Play Prototypes|Experiments",
		new Object[] {
			"Universal Tween Engine Experiments",
			new AppDesc("Experiment  1 - waypoints 1", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.tween.WayPoints1.class),
			new AppDesc("Experiment  2 - waypoints 2", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.tween.WayPoints2.class),
			new AppDesc("Experiment  3 - dynamic tween", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.tween.Dynamic.class),
			new AppDesc("Experiment  4 - flash", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.tween.Flash.class),
			new AppDesc("Experiment  5 - intro sequence", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.tween.IntroSequence.class),
			new AppDesc("Experiment  6 - veil", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.tween.Veil.class),
			new AppDesc("Experiment  7 - Game Over Pop-Up", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.tween.GameOverPopUp.class),
			new AppDesc("Experiment  8 - Tile Input Select", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.tween.TileInputSelect.class),
			new AppDesc("Experiment  9 - Level Pop-Up", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.tween.LevelPopUp.class),
			new AppDesc("Experiment 10 - Level Pop-Up using LevelPopUp", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.tween.LevelPopUpUsingLevelPopUp.class),						
			new AppDesc("Experiment 11 - Level Over Pop-Up using LevelPopUp", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.tween.LevelOverPopUpUsingLevelPopUp.class),						
			new AppDesc("Experiment 12 - ReelLetterTile", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.tween.ReelLetterTilePlay.class)
		},
		new Object[] {
			"Particle Experiments",
			new AppDesc("Experiment 1 - particles 1 - with Coding Math particle", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.particle.Particle1.class),
			new AppDesc("Experiment 2 - particles 2 - dampened sine", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.particle.Particle2.class),
			new AppDesc("Experiment 3 - particles 3 - dampened sine with predictable end", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.particle.Particle3.class),
			new AppDesc("Experiment 4 - particles 4 - dampened sine class with predictable end", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.particle.Particle4.class),
			new AppDesc("Experiment 5 - particles 5 - dampened sine class with predictable end via mouse click", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.particle.Particle5.class),
			new AppDesc("Experiment 6 - particles 6 - dampened sine but with tween predictable end via mouse click", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.particle.Particle6.class)
			
		},
		new Object[] {
			"Bezier Experiments",
			new AppDesc("Experiment 1 - bezier 1 - with Coding Math bezier", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.bezier.Bezier1.class),
			new AppDesc("Experiment 2 - bezier 2 - with libGDX spline", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.bezier.Bezier2.class),
			new AppDesc("Experiment 3 - bezier 3 - with libGDX spline - change points", 640, 480, com.ellzone.slotpuzzle2d.desktop.play.bezier.Bezier3.class)
		},
		new Object[] {
			"Basic2d Experiments",
			new AppDesc("Basic2d", 640,480, com.ellzone.slotpuzzle2d.desktop.play.basic2d.Basic2D.class)
		}
	};
	
	LwjglAWTCanvas currentTest = null;
	public boolean runApp(final AppDesc appDesc) {
		ApplicationListener listener;
		try {
			listener = appDesc.clazz.newInstance();
		} catch (InstantiationException e) {
			return false;
		} catch (IllegalAccessException e) {
			return false;
		}
		data = (appDesc.data == null || appDesc.data.isEmpty()) ? "data" : appDesc.data;
		
		Container container = getContentPane();
		if (currentTest != null) {
			currentTest.stop();
			container.remove(currentTest.getCanvas());
		}
			
		currentTest = new LwjglAWTCanvas(listener);
		currentTest.getCanvas().setSize(appDesc.width, appDesc.height);
		container.add(currentTest.getCanvas(), BorderLayout.CENTER);
		pack();
		return true;
	}
	
	public static void main(String[] args) throws Throwable {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		new Main();
	}
	
	public Main() throws HeadlessException {
		super("SlotPuzzle Play Prototypes|Experiments");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container container = getContentPane();
		JPanel appList = new AppList();
		appList.setSize(250, 600);
		container.add(appList, BorderLayout.LINE_START);
		pack();
		setSize(900, 600);
		setLocationRelativeTo(null);
		setVisible(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				if (currentTest != null) {
					currentTest.exit();
				}
			}
		});
	}
	
	class AppList extends JPanel {
		private static final long serialVersionUID = 1582559224991888475L;

		public AppList () {
			setLayout(new BorderLayout());

			final JButton button = new JButton("Run Test");

			DefaultMutableTreeNode root = processHierarchy(apps);
			final JTree tree = new JTree(root);
			JScrollPane pane = new JScrollPane(tree);

			DefaultTreeSelectionModel m = new DefaultTreeSelectionModel();
			m.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			tree.setSelectionModel(m);

			tree.addMouseListener(new MouseAdapter() {
				public void mouseClicked (MouseEvent event) {
					if (event.getClickCount() == 2) button.doClick();
				}
			});

			tree.addKeyListener(new KeyAdapter() {
				public void keyPressed (KeyEvent e) {
					if (e.getKeyCode() == KeyEvent.VK_ENTER) button.doClick();
				}
			});

			button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed (ActionEvent e) {
					Object obj = ((DefaultMutableTreeNode)tree.getLastSelectedPathComponent()).getUserObject();
					if (obj instanceof AppDesc) {
						AppDesc app = (AppDesc)obj;
						runApp(app);
					}
				}
			});

			add(pane, BorderLayout.CENTER);
			add(button, BorderLayout.SOUTH);
		}
		
		private DefaultMutableTreeNode processHierarchy(Object[] hierarchy) {
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(hierarchy[0]);
			DefaultMutableTreeNode child;
			for(int i=1; i<hierarchy.length; i++) {
				Object nodeSpecifier = hierarchy[i];
				if (nodeSpecifier instanceof Object[])
					child = processHierarchy((Object[])nodeSpecifier);
				else
					child = new DefaultMutableTreeNode(nodeSpecifier);
				node.add(child);
			}
			return node;
		}
	}
}
