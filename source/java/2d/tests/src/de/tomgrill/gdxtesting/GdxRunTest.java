package de.tomgrill.gdxtesting;

import com.badlogic.gdx.ApplicationListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import java.util.HashMap;
import java.util.Map;

public class GdxRunTest implements ApplicationListener {
    private Map<FrameworkMethod, RunNotifier> runChildren = new HashMap<FrameworkMethod, RunNotifier>();
    private GdxTestRunnerGetAllTestClasses gdxTestRunnerGetAllTestClasses;
    private GdxTestRunnerRunAllTestsLwgCanvas lwjCanvas;
    
    public GdxRunTest(GdxTestRunnerGetAllTestClasses gdxTestRunnerGetAllTestClasses,
                      Map<FrameworkMethod, RunNotifier> runChildren,
                      GdxTestRunnerRunAllTestsLwgCanvas lwjCanvas) throws InitializationError {
        this.gdxTestRunnerGetAllTestClasses = gdxTestRunnerGetAllTestClasses;
        this.runChildren = runChildren;
        this.lwjCanvas = lwjCanvas;
    }

    @Override
    public void create() {
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void render() {
        for (Map.Entry<FrameworkMethod, RunNotifier> each : runChildren.entrySet()) {
            gdxTestRunnerGetAllTestClasses.runSuperChild(each.getKey(), each.getValue());
        }
        runChildren.clear();
        lwjCanvas.currentTest.stop();
    }

    @Override
    public void pause() {
        System.out.println("pause called");
    }

    @Override
    public void resume() {
        System.out.println("resume called");
    }

    @Override
    public void dispose() {
        System.out.println("dispose called");
    }
  
    public int getNumberOfChildren() {
    	return runChildren.size();
    }
}