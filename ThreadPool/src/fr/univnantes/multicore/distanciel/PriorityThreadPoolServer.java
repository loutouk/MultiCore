package fr.univnantes.multicore.distanciel;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * Author: Louis Boursier
 */

public class PriorityThreadPoolServer implements Server {

    private CustomThreadPool threadPool;

    public PriorityThreadPoolServer(int noOfThreads) {
        threadPool = new CustomThreadPool(noOfThreads);
    }

    /**
     *
     * @param task a Callable<Block> that contains enough information to compute the block
     * @return
     */
    @Override
    public Future<Block> getBlock(Task task) {
        // Create the future
        FutureTask<Block> future = new FutureTask<Block>(task);
        try {
            // Let the pool handle the execution of the task
            threadPool.execute(future, task.hasPriority());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Return the future
        return future;
    }

}
