package fr.univnantes.multicore.distanciel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.FutureTask;

/**
 * Original code from Jakob Jenkov
 * Author: Louis Boursier
 */

public class CustomThreadPool {
    private BlockingQueue taskQueue = null;
    private List<PoolThread> threads = new ArrayList<PoolThread>();
    private boolean isStopped = false;

    public CustomThreadPool(int noOfThreads) {
        taskQueue = new BlockingQueue();

        for (int i = 0; i < noOfThreads; i++) {
            threads.add(new PoolThread(taskQueue));
        }
        for (PoolThread thread : threads) {
            thread.start();
        }
    }

    /**
     * The Runnable will be dequeued by an idle PoolThread and executed. See PoolThread.run()
     * @param task the Runnable we wish to execute eventually
     * @param hasPriority wether the task should be placed at the top or at the bottomp of the queue
     * @throws Exception
     */
    public synchronized void execute(FutureTask task, boolean hasPriority) throws Exception {
        if (this.isStopped) throw
                new IllegalStateException("ThreadPool is stopped");
        this.taskQueue.enqueue(task, hasPriority);
    }

    public synchronized void stop() {
        this.isStopped = true;
        for (PoolThread thread : threads) {
            thread.doStop();
        }
    }

    class PoolThread extends Thread {

        private BlockingQueue taskQueue = null;
        private boolean isStopped = false;

        public PoolThread(BlockingQueue queue) {
            taskQueue = queue;
        }

        // As soon as a thread is idle, it tries to dequeue and run the task that has been waiting the longest
        public void run() {
            while (!isStopped()) {
                try {
                    Runnable runnable = (Runnable) taskQueue.dequeue();
                    runnable.run();
                } catch (Exception e) {
                    //log or otherwise report exception,
                    //but keep pool thread alive.
                }
            }
        }

        public synchronized void doStop() {
            isStopped = true;
            // makes sure that a thread blocked in a wait() call inside the taskQueue.dequeue() call breaks out of the wait()
            // and leaves the dequeue() method call with an InterruptedException thrown, caught in the PoolThread.run() method
            this.interrupt();
        }

        public synchronized boolean isStopped() {
            return isStopped;
        }
    }
}
