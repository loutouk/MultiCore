package fr.univnantes.multicore.distanciel;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Server that uses a thread pool to automatically dispatch the tasks
 */
public class ThreadPoolServer implements Server {

    private ExecutorService threadPool;

    /**
     *
     * @param threadNumber The fixed number of thread the pool uses
     */
    public ThreadPoolServer(int threadNumber) {
        threadPool = Executors.newFixedThreadPool(threadNumber);
    }

    /**
     * Submits a task to the thread pool that will eventually compute it and return its result
     * @param task a Callable<Block> that contains enough information to compute the block
     * @return
     */
    @Override
    public Future<Block> getBlock(Task task) {
        return threadPool.submit(task);
    }
}
