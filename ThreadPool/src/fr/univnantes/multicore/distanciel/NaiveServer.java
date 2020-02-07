package fr.univnantes.multicore.distanciel;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;


/**
 * Simple implementation of Server that does the job as soon as it is submitted
 */
public class NaiveServer implements Server {
	/**
	 * getBlock creates a future that encapsulates the block, and then compute it locally
	 * @param task All the data needed to build a new block
	 */
	public Future<Block> getBlock(Task task){
		// Create the future
		FutureTask<Block> future = new FutureTask<Block>(task);
		// Execute the future locally
		future.run();
		// Return the future
		return future;
	}
}
