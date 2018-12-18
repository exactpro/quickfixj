/******************************************************************************
 * Copyright 2009-2018 Exactpro (Exactpro Systems Limited)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package quickfix.test.util;

import junit.framework.Assert;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class ThreadsUtil {

    private static final long MAX_STEP_TIME = 250;
    private static final long DEFAULT_TIMEOUT = 10_000;

    public static void waitToStopThreads(String threadName, long timeout) {
        long startTime = System.currentTimeMillis();
        long stepTime = Math.min(timeout / 10, MAX_STEP_TIME);
        while (System.currentTimeMillis() <= startTime + timeout) {
            try {
                Thread.sleep(stepTime);
                if(isThreadTerminated(threadName)) {
                    return;
                }
            } catch (InterruptedException e) {
                Assert.fail(e.getMessage());
            }
        }
        Assert.fail("Thread with name [" + threadName + "] is still running");
    }

    public static void waitToStopThreads(String threadName) {
        waitToStopThreads(threadName, DEFAULT_TIMEOUT);
    }

    private static boolean isThreadTerminated(String threadName) {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] dumpAllThreads = bean.dumpAllThreads(false, false);
        for (ThreadInfo threadInfo : dumpAllThreads) {
            if (threadName.equals(threadInfo.getThreadName())) {
                return false;
            }
        }
        return true;
    }
}
