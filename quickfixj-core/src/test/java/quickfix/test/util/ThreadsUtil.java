package quickfix.test.util;

import junit.framework.Assert;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

public class ThreadsUtil {

    private static long maxStepTime = 250;

    public static void waitToStopThreads(String threadName, long timeout) {
        long startTime = System.currentTimeMillis();
        long stepTime = Math.min(timeout / 10, maxStepTime);
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
