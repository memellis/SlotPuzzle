package de.tomgrill.gdxtesting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import com.badlogic.gdx.backends.lwjgl.LwjglAWTCanvas;

public class GdxTestRunnerRunAllTestsLwgCanvas extends JFrame {
	private static final long serialVersionUID = -1693364752988953827L;
    private List<GdxTestRunnerGetAllTestClasses> allTestClasses;
    private Map<FrameworkMethod, RunNotifier> runChildren;
    private boolean testFinished;
 
    public void run() throws InitializationError {
        for (GdxTestRunnerGetAllTestClasses testClass : allTestClasses) {
            System.out.println("UnitTest class = " + testClass.getTestClass().getName());
            List<FrameworkMethod> testChildren = testClass.getSuperChildren();
            Map<FrameworkMethod, RunNotifier> childrenForTestClass = new HashMap<FrameworkMethod, RunNotifier>();
            for (FrameworkMethod testChild : testChildren) {
                System.out.println("Test method: " + testChild);
                if (this.runChildren.containsKey(testChild)) {
                    childrenForTestClass.put(testChild, this.runChildren.get(testChild));
                }
            }
            testFinished = false;
            try {
                runApp(testClass, childrenForTestClass);
            } catch (Exception e) {
            	System.out.println("Exception after runApp");
            	e.printStackTrace();
            }
            waitUntilTestFinished();
         }
    }

	LwjglAWTCanvas currentTest = null;
	GdxRunTest gdxRunTest;
	public void runApp(GdxTestRunnerGetAllTestClasses test,
            Map<FrameworkMethod, RunNotifier> runChildren) throws InitializationError {

		Container container = getContentPane();
		if (currentTest != null) {
			currentTest.stop();
			container.remove(currentTest.getCanvas());
		}
		
		gdxRunTest = new GdxRunTest(test, runChildren, this);			
		currentTest = new LwjglAWTCanvas(gdxRunTest);
		currentTest.getCanvas().setSize(800,600);
		
		container.add(currentTest.getCanvas(), BorderLayout.CENTER);
		pack();
	}

 
    private void waitUntilTestFinished() {
        try {
            while (!testFinished) {
                Thread.sleep(10);
                if (gdxRunTest != null) {
                	if (gdxRunTest.getNumberOfChildren() == 0) {
                		testFinished = true;
                	}
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public GdxTestRunnerRunAllTestsLwgCanvas(List<GdxTestRunnerGetAllTestClasses> allTestClasses,
            Map<FrameworkMethod, RunNotifier> runChildren) throws HeadlessException {
    	super("SlotPuzzle Tests");
    	this.allTestClasses = allTestClasses;
    	this.runChildren = runChildren;
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	Container container = getContentPane();
    	container.setBackground(Color.BLACK);
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
    
    public void testFinished() {
    	testFinished = true;
    }
    
    public void stopCurrentTest() {
    	currentTest.stop();
    }
}
