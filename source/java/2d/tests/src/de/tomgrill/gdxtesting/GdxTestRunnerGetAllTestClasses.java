/*******************************************************************************
 * Copyright 2015 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package de.tomgrill.gdxtesting;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GdxTestRunnerGetAllTestClasses extends BlockJUnit4ClassRunner {
    public static List<GdxTestRunnerGetAllTestClasses> testClasses;
    public static Map<FrameworkMethod, RunNotifier> runChildren;
    public static int classCount;
    static {
        testClasses = new ArrayList<GdxTestRunnerGetAllTestClasses>();
        runChildren = new HashMap<FrameworkMethod, RunNotifier>();
        classCount = 0;
    }

    private int numberOfTestChildren;

    public GdxTestRunnerGetAllTestClasses(Class<?> klass) throws InitializationError {
        super(klass);
        GdxTestRunnerGetAllTestClasses.testClasses.add(this);
        classCount++;
        numberOfTestChildren = this.testCount();
    }

    List<FrameworkMethod> getSuperChildren() {
        return super.getChildren();
    }

    GdxTestRunnerRunAllTestsLwgCanvas gdxTestRunnerRunAlltests;
    
    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        GdxTestRunnerGetAllTestClasses.runChildren.put(method, notifier);
        numberOfTestChildren--;
        if (numberOfTestChildren == 0) {
            classCount--;
            if (classCount == 0) {
                System.out.println("This is the last testClass to run: " + this.getName());
                gdxTestRunnerRunAlltests =
                        new GdxTestRunnerRunAllTestsLwgCanvas(GdxTestRunnerGetAllTestClasses.testClasses,
                                                              GdxTestRunnerGetAllTestClasses.runChildren);
                try {
                    gdxTestRunnerRunAlltests.run();
                } catch (InitializationError ie) {
                    ie.printStackTrace();
                }
            }
        }
    }

    public void runSuperChild(FrameworkMethod method, RunNotifier notifier) {
    	super.runChild(method, notifier);    	
    }
}
